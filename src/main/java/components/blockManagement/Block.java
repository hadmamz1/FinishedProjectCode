package components.blockManagement;

import MainStudioComponents.GameObject;
import components.Component;
import components.controling.PlayerController;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import UtilityComponents.Resources;

public abstract class Block extends Component {
    private transient boolean bopUp = true;
    private transient boolean animateBop = false;
    private transient Vector2f start;
    private transient Vector2f topLocation;
    private transient boolean active = true;

    public float speed = 0.4f;

    @Override
    public void startComponent() {
        this.start = new Vector2f(this.gameObj.transform.position);
        this.topLocation = new Vector2f(start).add(0.0f, 0.02f);
    }

    @Override
    public void updateComponent(float deltaTime) {
        if (animateBop) {
            if (bopUp) {
                if (this.gameObj.transform.position.y < topLocation.y) {
                    this.gameObj.transform.position.y += speed * deltaTime;
                } else {
                    bopUp = false;
                }
            } else {
                if (this.gameObj.transform.position.y > start.y) {
                    this.gameObj.transform.position.y -= speed * deltaTime;
                } else {
                    this.gameObj.transform.position.y = this.start.y;
                    bopUp = true;
                    animateBop = false;
                }
            }
        }
    }

    @Override
    public void beginObjectCollision(GameObject obj, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = obj.getComponent(PlayerController.class);
        if (active && playerController != null && contactNormal.y < -0.8f) {
            animateBop = true;
            Resources.getSound("assets/audio/bump.ogg").playAudio();
            hitPlayer(playerController);
        }
    }

    public void setInactive() {
        this.active = false;
    }

    abstract void hitPlayer(PlayerController player);
}