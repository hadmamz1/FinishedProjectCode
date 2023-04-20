package components;

import MainStudioComponents.Camera;
import MainStudioComponents.Listener_Key;
import MainStudioComponents.Listener_Mouse;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component {

    private Camera levelEditorCam;

    // assume user is dragging if mouse is held down for
    // 2 frames
    private float debounceDrag = 0.032f;
    private Vector2f originOfClick;
    private float scrollSensitivity = 0.1f;
    private float dragSensitivity = 30.0f;
    private float lerpTime = 0.0f;
    private boolean resetCamera = false;

    public EditorCamera(Camera levelEditorCam) {
        this.levelEditorCam = levelEditorCam;
        this.originOfClick = new Vector2f();
    }

    @Override
    public void updateEditor(float deltaTime) {
        if (Listener_Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)
                && debounceDrag > 0) {
            this.originOfClick = Listener_Mouse.getWorld();
            debounceDrag -= deltaTime;
            return;
        } else if (Listener_Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            Vector2f mousePos = Listener_Mouse.getWorld();
            Vector2f mouseDistance = new Vector2f(mousePos).sub(this.originOfClick);
            levelEditorCam.camPos.sub(mouseDistance.mul(deltaTime).mul(dragSensitivity));
            this.originOfClick.lerp(mousePos, deltaTime);
        }

        if (debounceDrag <= 0.0f && !Listener_Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            debounceDrag = 0.1f;
        }

        if (Listener_Mouse.getScrollY() != 0.0f) {
            float valueAdd = (float)Math.pow(Math.abs(Listener_Mouse.getScrollY() * scrollSensitivity),
                    1 / levelEditorCam.getZoom());
            valueAdd *= -Math.signum(Listener_Mouse.getScrollY());
            levelEditorCam.addZoom(valueAdd);
        }

        if (Listener_Key.keyIsPressed(GLFW_KEY_SPACE)) {
            resetCamera = true;
        }

        if (resetCamera) {
            levelEditorCam.camPos.lerp(new Vector2f(), lerpTime);
            levelEditorCam.setZoom(this.levelEditorCam.getZoom() +
                    ((1.0f - levelEditorCam.getZoom()) * lerpTime));
            this.lerpTime += 0.1f * deltaTime;
            if (Math.abs(levelEditorCam.camPos.x) <= 5.0f &&
                    Math.abs(levelEditorCam.camPos.y) <= 5.0f) {
                this.lerpTime = 0.0f;
                levelEditorCam.camPos.set(0f, 0f);
                this.levelEditorCam.setZoom(1.0f);
                resetCamera = false;
            }
        }
    }
}