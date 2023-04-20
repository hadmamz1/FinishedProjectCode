package PhysicsComponents;

import components.Component;
import org.joml.Vector2f;
import RenderingComponents.Drawing;

public class Box2DCollider extends Component {
    private Vector2f halfSize = new Vector2f(1);
    private Vector2f origin = new Vector2f();

    private Vector2f offset = new Vector2f();

    public Vector2f getOffset() {
        return this.offset;
    }

    public void setOffset(Vector2f newOffset) { this.offset.set(newOffset); }

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    public Vector2f getOrigin()
    {
        return this.origin;
    }

    @Override
    public void updateEditor(float deltaTime)
    {
        Vector2f centre = new Vector2f(this.gameObj.transform.position).add(this.offset);
        Drawing.addBox2D(centre, this.halfSize, this.gameObj.transform.rotation);


    }
}