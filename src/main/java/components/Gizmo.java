package components;

import EditorScreenComponents.PropertiesWindow;
import MainStudioComponents.*;
import components.spriteFunctionalities.Sprite;
import components.spriteFunctionalities.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component {
    private Vector4f colourXAxis = new Vector4f(1, 0.3f, 0.3f, 1);
    private Vector4f colourXAxisHover = new Vector4f(1, 0, 0, 1);
    private Vector4f colourYAxis = new Vector4f(0.3f, 1, 0.3f, 1);
    private Vector4f colourYAxisHover = new Vector4f(0, 1, 0, 1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer spriteXAxis;
    private SpriteRenderer spriteYAxis;
    protected GameObject activeGameObj = null;

    private Vector2f offsetXAxis = new Vector2f(24f / 80f, -6f / 80f);
    private Vector2f offsetYAxis = new Vector2f(-7f / 80f, 21f / 80f);

    // width and height of gizmo in world units
    private float heightGizmo = 48f / 80f;
    private float widthGizmo = 16f / 80f;
    private boolean using = false;
    protected boolean activeXAxis = false;
    protected boolean activeYAxis = false;

    private PropertiesWindow propertiesWindow;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, widthGizmo, heightGizmo);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, widthGizmo, heightGizmo);
        this.spriteXAxis = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.spriteYAxis = this.yAxisObject.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;

        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

        Window.getScene().addObjToScene(this.xAxisObject);
        Window.getScene().addObjToScene(this.yAxisObject);
    }

    @Override
    public void startComponent() {
        this.xAxisObject.transform.rotation = 90;
        this.yAxisObject.transform.rotation = 180;
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;
        this.xAxisObject.dontSerialise();
        this.yAxisObject.dontSerialise();
    }

    @Override
    public void updateComponent(float deltaTime)
    {
        if (using)
        {
            this.setInactive();
        }
    }

    @Override
    public void updateEditor(float deltaTime) {
        if (!using) return;

        this.activeGameObj = this.propertiesWindow.getActiveGameObject();
        if (this.activeGameObj != null) {
            this.setActive();

        } else {
            this.setInactive();
            return;
        }

        boolean xAxisHot = checkHoverStateX();
        boolean yAxisHot = checkHoverStateY();

        if ((xAxisHot || activeXAxis) && Listener_Mouse.isDragging() && Listener_Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            activeXAxis = true;
            activeYAxis = false;
        } else if ((yAxisHot || activeYAxis) && Listener_Mouse.isDragging() && Listener_Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            activeYAxis = true;
            activeXAxis = false;
        } else {
            activeXAxis = false;
            activeYAxis = false;
        }

        if (this.activeGameObj != null) {
            this.xAxisObject.transform.position.set(this.activeGameObj.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObj.transform.position);
            this.xAxisObject.transform.position.add(this.offsetXAxis);
            this.yAxisObject.transform.position.add(this.offsetYAxis);
        }
    }

    private void setActive() {
        this.spriteXAxis.setColour(colourXAxis);
        this.spriteYAxis.setColour(colourYAxis);
    }

    private void setInactive() {
        this.activeGameObj = null;
        this.spriteXAxis.setColour(new Vector4f(0, 0, 0, 0));
        this.spriteYAxis.setColour(new Vector4f(0, 0, 0, 0));
    }

    private boolean checkHoverStateX() {
        Vector2f mousePos = Listener_Mouse.getWorld();
        if (mousePos.x <= xAxisObject.transform.position.x + (heightGizmo / 2.0f) &&
                mousePos.x >= xAxisObject.transform.position.x - (widthGizmo / 2.0f) &&
                mousePos.y >= xAxisObject.transform.position.y - (heightGizmo / 2.0f) &&
                mousePos.y <= xAxisObject.transform.position.y + (widthGizmo / 2.0f)) {
            spriteXAxis.setColour(colourXAxisHover);
            return true;
        }

        spriteXAxis.setColour(colourXAxis);
        return false;
    }

    private boolean checkHoverStateY() {
        Vector2f mousePos = Listener_Mouse.getWorld();
        if (mousePos.x <= yAxisObject.transform.position.x + (widthGizmo / 2.0f) &&
                mousePos.x >= yAxisObject.transform.position.x - (widthGizmo / 2.0f) &&
                mousePos.y <= yAxisObject.transform.position.y + (heightGizmo / 2.0f) &&
                mousePos.y >= yAxisObject.transform.position.y - (heightGizmo / 2.0f)) {
            spriteYAxis.setColour(colourYAxisHover);
            return true;
        }

        spriteYAxis.setColour(colourYAxis);
        return false;
    }

    public void setUsing() {
        this.using = true;
    }

    public void setNotUsing() {
        this.using = false;
        this.setInactive();
    }

    public static class ScaleGizmo extends Gizmo {
        public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
            super(scaleSprite, propertiesWindow);
        }

        @Override
        public void updateEditor(float deltaTime) {
            if (activeGameObj != null) {
                if (activeXAxis && !activeYAxis) {
                    activeGameObj.transform.scale.x -= Listener_Mouse.getWorldX();
                } else if (activeYAxis) {
                    activeGameObj.transform.scale.y -= Listener_Mouse.getWorldY();
                }
            }

            super.updateEditor(deltaTime);
        }
    }

    public static class TranslateGizmo extends Gizmo {

        public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
            super(arrowSprite, propertiesWindow);
        }

        @Override
        public void updateEditor(float deltaTime) {
            if (activeGameObj != null) {
                if (activeXAxis && !activeYAxis) {
                    activeGameObj.transform.position.x -= Listener_Mouse.getWorldX();
                } else if (activeYAxis) {
                    activeGameObj.transform.position.y -= Listener_Mouse.getWorldY();
                }
            }

            super.updateEditor(deltaTime);
        }
    }
}