package PhysicsComponents;

import components.Component;
import org.joml.Vector2f;
import RenderingComponents.Drawing;

public class CircleCollider extends Component {
    protected Vector2f offset = new Vector2f();
    private float radius = 1f;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
    public Vector2f getOffset() {
        return this.offset;
    }

    public void setOffset(Vector2f newOffset) { this.offset.set(newOffset); }

    @Override
    public void updateEditor(float deltaTime) {
        Vector2f center = new Vector2f(this.gameObj.transform.position).add(this.offset);
        Drawing.addCircle(center, this.radius);
    }
}