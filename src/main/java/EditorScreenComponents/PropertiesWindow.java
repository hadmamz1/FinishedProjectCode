package EditorScreenComponents;

import imgui.ImGui;
import MainStudioComponents.GameObject;
import components.spriteFunctionalities.SpriteRenderer;
import PhysicsComponents.Box2DCollider;
import PhysicsComponents.CircleCollider;
import PhysicsComponents.Rigidbody2D;
import RenderingComponents.InvisiblePickingTexture;
import org.joml.Vector4f;
import java.util.ArrayList;
import java.util.List;

public class PropertiesWindow {
    private GameObject activeGameObject = null;
    private List<Vector4f> activeGameObjsOriginalCol;
    private List<GameObject> activeGameObjects;

    private InvisiblePickingTexture invisiblePickingTexture;

    public PropertiesWindow(InvisiblePickingTexture invisiblePickingTexture)
    {
        this.invisiblePickingTexture = invisiblePickingTexture;
        this.activeGameObjects = new ArrayList<>();
        this.activeGameObjsOriginalCol = new ArrayList<>();

    }


    public void imgui() {
        if (activeGameObjects.size() == 1 &&
                activeGameObjects.get(0) != null) {
            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties");
            if (ImGui.beginPopupContextWindow("ComponentAdder")) {

                if (ImGui.menuItem("Add Box Collider")) {
                    if (activeGameObject.getComponent(Box2DCollider.class) == null && activeGameObject.getComponent(CircleCollider.class) == null) {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if (ImGui.menuItem("Add Circle Collider")) {
                    if (activeGameObject.getComponent(Box2DCollider.class) == null && activeGameObject.getComponent(CircleCollider.class) == null) {
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                if (ImGui.menuItem("Add Rigidbody")) {
                    if (activeGameObject.getComponent(Rigidbody2D.class) == null) {
                        activeGameObject.addComponent(new Rigidbody2D());
                    }
                }

                ImGui.endPopup();
            }
            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public void setActiveGameObject(GameObject activeGameObject) {

        if (activeGameObject != null) {
            clearSelected();
            this.activeGameObjects.add(activeGameObject);
        }
    }
    public GameObject getActiveGameObject() {

        return activeGameObjects.size() == 1 ? this.activeGameObjects.get(0) :
                null;
    }

    public List<GameObject> getActiveGameObjs() {
        return this.activeGameObjects;
    }

    public void clearSelected() {
        if (activeGameObjsOriginalCol.size() > 0) {
            int i = 0;
            for (GameObject go : activeGameObjects) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr != null) {
                    spr.setColour(activeGameObjsOriginalCol.get(i));
                }
                i++;
            }
        }
        this.activeGameObjects.clear();
        this.activeGameObjsOriginalCol.clear();
    }


    public void addActiveGameObject(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null ) {
            this.activeGameObjsOriginalCol.add(new Vector4f(spr.getColour()));
            spr.setColour(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
        } else {
            this.activeGameObjsOriginalCol.add(new Vector4f());
        }
        this.activeGameObjects.add(go);
    }

    public InvisiblePickingTexture getPickingTexture() {
        return this.invisiblePickingTexture;
    }
}