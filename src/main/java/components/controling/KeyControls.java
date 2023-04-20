package components.controling;

import EditorScreenComponents.PropertiesWindow;
import MainStudioComponents.GameObject;
import MainStudioComponents.Listener_Key;
import MainStudioComponents.Window;
import UtilityComponents.Settings;
import components.Component;
import components.StateMachine;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component
{

    private float debounce = 0.0f;
    private float debounceLength = 0.1f;

    @Override
    public void updateEditor(float deltaTime) {
        debounce -= deltaTime;
        PropertiesWindow propertiesWindow = Window.getImguiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjs();
        float shift = Listener_Key.keyIsPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1.0f;

        if (Listener_Key.keyIsPressed(GLFW_KEY_LEFT_CONTROL) &&
                Listener_Key.keyBeginPress(GLFW_KEY_V) && activeGameObject != null) {
            GameObject newObj = activeGameObject.copy();
            Window.getScene().addObjToScene(newObj);
            newObj.transform.position.add(Settings.GRID_WIDTH,
                    0.0f);
            propertiesWindow.setActiveGameObject(newObj);
            if (newObj.getComponent(StateMachine.class) != null) {
                newObj.getComponent(StateMachine.class).textureRefresh();
            }
            // delete an object
        } else if (Listener_Key.keyBeginPress(GLFW_KEY_DELETE)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
            // copy paste objects
        } else if (Listener_Key.keyIsPressed(GLFW_KEY_LEFT_CONTROL) &&
                Listener_Key.keyBeginPress(GLFW_KEY_V) && activeGameObjects.size() > 1) {
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.getScene().addObjToScene(copy);
                propertiesWindow.addActiveGameObject(copy);
                if (copy.getComponent(StateMachine.class) != null) {
                    copy.getComponent(StateMachine.class).textureRefresh();
                }
            }
            // Hot keys for moving blocks and objects using keys:
        } else if (Listener_Key.keyIsPressed(GLFW_KEY_UP) && debounce < 0) {
            debounce = debounceLength;
            for (GameObject gObj : activeGameObjects)
            {
                gObj.transform.position.y += Settings.GRID_HEIGHT * shift;
            }
        } else if (Listener_Key.keyIsPressed(GLFW_KEY_DOWN) && debounce < 0) {
            debounce = debounceLength;
            for (GameObject gObj : activeGameObjects)
            {
                gObj.transform.position.y -= Settings.GRID_HEIGHT * shift;
            }
        } else if (Listener_Key.keyIsPressed(GLFW_KEY_LEFT) && debounce < 0) {
            debounce = debounceLength;
            for (GameObject gObj : activeGameObjects)
            {
                gObj.transform.position.x -= Settings.GRID_HEIGHT * shift;
            }
        } else if (Listener_Key.keyIsPressed(GLFW_KEY_RIGHT) && debounce < 0) {
            debounce = debounceLength;
            for (GameObject gObj : activeGameObjects)
            {
                gObj.transform.position.x += Settings.GRID_HEIGHT * shift;
            }
        }
    }
}
