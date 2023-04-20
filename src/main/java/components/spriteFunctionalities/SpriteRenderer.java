package components.spriteFunctionalities;

import EditorScreenComponents.GameWindowImGui;
import MainStudioComponents.Transform;
import components.Component;
import org.joml.Vector2f;
import org.joml.Vector4f;
import RenderingComponents.Texture;

public class SpriteRenderer extends Component {

    private Vector4f colour = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;
    private transient boolean isDirty = true;

    @Override
    public void startComponent() {
        this.lastTransform = gameObj.transform.copy();
    }

    @Override
    public void updateComponent(float deltaTime) {
        if (!this.lastTransform.equals(this.gameObj.transform)) {
            this.gameObj.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void updateEditor(float deltaTime) {
        if (!this.lastTransform.equals(this.gameObj.transform)) {
            this.gameObj.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void imgui() {
        if (GameWindowImGui.colorPicker4("Colour Picker", this.colour)) {
            this.isDirty = true;
        }
    }

    public Vector4f getColour() {
        return this.colour;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTextureCoords() {
        return sprite.getTextureCoords();
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.isDirty = true;
    }

    public void setColour(Vector4f colour) {
        if (!this.colour.equals(colour)) {
            this.isDirty = true;
            this.colour.set(colour);
        }
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void setDirty()
    {
        this.isDirty = true;
    }

    public void setClean() {
        this.isDirty = false;
    }

    public void setTexture(Texture texture) {
        this.sprite.setTexture(texture);
    }
}