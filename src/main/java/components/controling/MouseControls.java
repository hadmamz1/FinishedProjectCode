package components.controling;

import EditorScreenComponents.PropertiesWindow;
import MainStudioComponents.GameObject;
import MainStudioComponents.Listener_Key;
import MainStudioComponents.Listener_Mouse;
import MainStudioComponents.Window;
import components.Component;
import components.NonPickable;
import components.spriteFunctionalities.SpriteRenderer;
import components.StateMachine;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import RenderingComponents.Drawing;
import RenderingComponents.InvisiblePickingTexture;
import EngineSceneComponents.Scene;
import UtilityComponents.Settings;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
public class MouseControls extends Component {
    GameObject held = null;

    private float debounceLength = 0.2f;
    private float debounce = debounceLength;

    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();


    public void objPickingUp(GameObject gObj) {
        if (this.held != null)
        {
            this.held.destroy();
        }
        this.held = gObj;
        this.held.getComponent(SpriteRenderer.class)
                .setColour(new Vector4f(0.8f,0.8f,0.8f,0.6f));
        this.held.addComponent(new NonPickable());
        Window.getScene().addObjToScene(gObj);
    }

    public void objPlacing() {
        GameObject obj = this.held.copy();
        if (obj.getComponent(StateMachine.class) != null)
        {
            obj.getComponent(StateMachine.class).textureRefresh();
        }
        obj.getComponent(SpriteRenderer.class)
                .setColour(new Vector4f(1,1,1,1));
        obj.remComponent(NonPickable.class);
        Window.getScene().addObjToScene(obj);
    }

    @Override
    public void updateEditor(float deltaTime) {
        debounce -= deltaTime;
        InvisiblePickingTexture invisiblePickingTexture = Window.getImguiLayer().getPropertiesWindow().getPickingTexture();
        Scene thisScene = Window.getScene();
        if (held != null) {
            held.transform.position.x = Listener_Mouse.getWorldX();
            held.transform.position.y = Listener_Mouse.getWorldY();
            held.transform.position.x = ((int)Math.floor(held.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH)
                    + Settings.GRID_WIDTH / 2.0f;
            held.transform.position.y = ((int)Math.floor(held.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT)
                    + Settings.GRID_WIDTH / 2.0f;

            if (Listener_Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                float halfWidth = Settings.GRID_WIDTH / 2.0f;
                float halfHeight = Settings.GRID_HEIGHT / 2.0f;
                if (Listener_Mouse.isDragging() &&
                        !objInSquare(held.transform.position.x - halfWidth,
                                held.transform.position.y - halfHeight)) {
                    objPlacing();
                } else if (!Listener_Mouse.isDragging() && debounce < 0) {
                    objPlacing();
                    debounce = debounceLength;
                }
            }
            // stop holding when escape key is pressed
            if (Listener_Key.keyIsPressed(GLFW_KEY_ESCAPE))
            {
                held.destroy();
                held = null;
            }

        } else if (!Listener_Mouse.isDragging() && Listener_Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int) Listener_Mouse.getScreenX();
            int y = (int) Listener_Mouse.getScreenY();
            int gameObjID = invisiblePickingTexture.readPixel(x, y);
            GameObject pickedObj = thisScene.getGameObject(gameObjID);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                Window.getImguiLayer().getPropertiesWindow().setActiveGameObject(pickedObj);
            } else if (pickedObj == null && !Listener_Mouse.isDragging()) {
                Window.getImguiLayer().getPropertiesWindow().clearSelected();
            }
            this.debounce = 0.2f;
        } else if (Listener_Mouse.isDragging() && Listener_Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            if (!boxSelectSet) {
                Window.getImguiLayer().getPropertiesWindow().clearSelected();
                boxSelectStart = Listener_Mouse.getScreen();
                boxSelectSet = true;
            }
            boxSelectEnd = Listener_Mouse.getScreen();
            Vector2f boxSelectStartWorld = Listener_Mouse.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = Listener_Mouse.screenToWorld(boxSelectEnd);
            Vector2f halfSize =
                    (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            Drawing.addBox2D(
                    (new Vector2f(boxSelectStartWorld)).add(halfSize),
                    new Vector2f(halfSize).mul(2.0f),
                    0.0f);
        } else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int) boxSelectStart.x;
            int screenStartY = (int) boxSelectStart.y;
            int screenEndX = (int) boxSelectEnd.x;
            int screenEndY = (int) boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int x = screenStartX;
                screenStartX = screenEndX;
                screenEndX = x;
            }
            if (screenEndY < screenStartY) {
                int x = screenStartY;
                screenStartY = screenEndY;
                screenEndY = x;
            }

            float[] gameObjIDs = invisiblePickingTexture.readPixels(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY)
            );
            Set<Integer> distinctiveGameObjIDs = new HashSet<>();
            for (float objID : gameObjIDs) {
                distinctiveGameObjIDs.add((int) objID);
            }

            for (Integer gameObjID : distinctiveGameObjIDs) {
                GameObject pickedObj = Window.getScene().getGameObject(gameObjID);
                if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                    Window.getImguiLayer().getPropertiesWindow().addActiveGameObject(pickedObj);
                }
            }
        }
    }
    private boolean objInSquare(float x, float y) {
        PropertiesWindow propertiesWindow = Window.getImguiLayer().getPropertiesWindow();
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
        Vector2f fStart = Listener_Mouse.worldToScreen(start);
        Vector2f fEnd = Listener_Mouse.worldToScreen(end);
        Vector2i startScreen = new Vector2i((int)fStart.x + 2, (int)fStart.y + 2);
        Vector2i endScreen = new Vector2i((int)fEnd.x - 2, (int)fEnd.y - 2);
        float[] gameObjIDs = propertiesWindow.getPickingTexture().readPixels(startScreen, endScreen);

        for (int i = 0; i < gameObjIDs.length; i++) {
            if (gameObjIDs[i] >= 0) {
                GameObject pickedObj = Window.getScene().getGameObject((int)gameObjIDs[i]);
                if (pickedObj.getComponent(NonPickable.class) == null) {
                    return true;
                }
            }
        }

        return false;
    }
}