package MainStudioComponents;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class Listener_Key {
    private static Listener_Key instance;
    private boolean keyPressed[] = new boolean[350];
    private boolean keyBeginPress[] = new boolean[350];


    private Listener_Key() {

    }

    public static Listener_Key get() {
        if (Listener_Key.instance == null) {
            Listener_Key.instance = new Listener_Key();
        }

        return Listener_Key.instance;
    }
    public static void frameEnd()
    {
        Arrays.fill(get().keyBeginPress, false);
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
            get().keyBeginPress[key] = true;
        } else if (action == GLFW_RELEASE) {
            get().keyPressed[key] = false;
            get().keyBeginPress[key] = false;
        }
    }

    public static boolean keyIsPressed(int keyCode) {
        return get().keyPressed[keyCode];
    }

    public static boolean keyBeginPress(int keyCode) {
        return get().keyBeginPress[keyCode];
    }


}