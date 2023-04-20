package components;

import MainStudioComponents.Listener_Key;
import MainStudioComponents.Window;
import components.spriteFunctionalities.Spritesheet;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;

public class GizmoSystem extends Component {
    private Spritesheet gizmos;
    private int gizmoUsed = 0;

    public GizmoSystem(Spritesheet gizmoSprites) {
        gizmos = gizmoSprites;
    }

    @Override
    public void startComponent() {
        gameObj.addComponent(new Gizmo.TranslateGizmo(gizmos.getSprite(1),
                Window.getImguiLayer().getPropertiesWindow()));
        gameObj.addComponent(new Gizmo.ScaleGizmo(gizmos.getSprite(2),
                Window.getImguiLayer().getPropertiesWindow()));
    }

    @Override
    public void updateEditor(float deltaTime) {
        if (gizmoUsed == 0) {
            gameObj.getComponent(Gizmo.TranslateGizmo.class).setUsing();
            gameObj.getComponent(Gizmo.ScaleGizmo.class).setNotUsing();
        } else if (gizmoUsed == 1) {
            gameObj.getComponent(Gizmo.TranslateGizmo.class).setNotUsing();
            gameObj.getComponent(Gizmo.ScaleGizmo.class).setUsing();
        }

        if (Listener_Key.keyIsPressed(GLFW_KEY_T)) {
            gizmoUsed = 0;
        } else if (Listener_Key.keyIsPressed(GLFW_KEY_S)) {
            gizmoUsed = 1;
        }
    }
}