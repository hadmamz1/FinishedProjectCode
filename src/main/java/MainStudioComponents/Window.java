package MainStudioComponents;

import EventObserver.EventSystem;
import EventObserver.Observer;
import EventObserver.events.Event;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import PhysicsComponents.Physics;
import RenderingComponents.*;
import EngineSceneComponents.LevelEditor_Scene_Init;
import EngineSceneComponents.Scene;
import EngineSceneComponents.Scene_Init;
import UtilityComponents.Resources;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {
    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imguiLayer;
    private Framebuffer framebuffer;
    private InvisiblePickingTexture invisiblePickingTexture;
    private long audioContext;
    private long audioDevice;
    private boolean runtimePlay = false;


    private static Window window = null;

    private static Scene presentScene;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Studio 2D";
        EventSystem.addObserver(this);
    }

    public static void changeScene(Scene_Init sceneInit) {
        if (presentScene != null)
        {
            presentScene.destroy();
        }
        getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        presentScene = new Scene(sceneInit);
        presentScene.load();
        presentScene.init();
        presentScene.start();
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public static Physics getPhysics() { return presentScene.getPhysics(); }

    public static Scene getScene() {
        return presentScene;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Destroy the audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        glfwSetCursorPosCallback(glfwWindow, Listener_Mouse::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, Listener_Mouse::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, Listener_Mouse::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, Listener_Key::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // Initialize the OpenAL
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (!alCapabilities.OpenAL10) {
            assert false : "Audio library not supported.";
        }

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new Framebuffer(1920, 1080);
        this.invisiblePickingTexture = new InvisiblePickingTexture(1920, 1080);
        glViewport(0, 0, 1920, 1080);

        this.imguiLayer = new ImGuiLayer(glfwWindow, invisiblePickingTexture);
        this.imguiLayer.initImGui();

        Window.changeScene(new LevelEditor_Scene_Init());
    }

    public void loop() {
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = Resources.getShader("assets/shaders/default.glsl");
        Shader pickingShader = Resources.getShader("assets/shaders/pickingShader.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            // Render pass 1. Render to picking texture
            glDisable(GL_BLEND);
            invisiblePickingTexture.enableWriting();

            glViewport(0, 0, 1920, 1080);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            presentScene.render();

            invisiblePickingTexture.disableWriting();
            glEnable(GL_BLEND);

            // Render pass 2. Render actual game
            Drawing.beginFrame();

            this.framebuffer.bind();
            glClearColor(1,1,1,1);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {

                Renderer.bindShader(defaultShader);
                if (runtimePlay) {
                    presentScene.updateScene(dt);
                } else {
                    presentScene.updateEditor(dt);
                }
                presentScene.render();
                Drawing.draw();
            }
            this.framebuffer.unbind();

            this.imguiLayer.update(dt, presentScene);

            Listener_Key.frameEnd();
            Listener_Mouse.endFrame();
            glfwSwapBuffers(glfwWindow);


            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

    }

    public static int getWidth() {
        return 1920;//get().width;
    }

    public static int getHeight() {
        return 1080;//get().height;
    }

    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    public static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    public static Framebuffer getFramebuffer() {
        return get().framebuffer;
    }

    public static float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }

    public static ImGuiLayer getImguiLayer() {
        return get().imguiLayer;
    }

    @Override
    public void onNotify(GameObject obj, Event event) {
        switch (event.type)
        {
            case GameEngineStartPlay:
                System.out.println("PLAY STARTING!");
                this.runtimePlay = true;
                presentScene.save();
                Window.changeScene(new LevelEditor_Scene_Init());
                break;
            case GameEngineStopPlay:
                System.out.println("PLAY STOPPING");
                this.runtimePlay = false;
                Window.changeScene(new LevelEditor_Scene_Init());
                break;
            case SaveLevel:
                presentScene.save();
            case LoadLevel:
                Window.changeScene(new LevelEditor_Scene_Init());break;

        }
    }
}