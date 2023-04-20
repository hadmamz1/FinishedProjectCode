package components.spriteFunctionalities;

import MainStudioComponents.Camera;
import MainStudioComponents.GameObject;
import MainStudioComponents.Window;
import components.Component;
import components.StateMachine;
import components.controling.PlayerController;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import PhysicsComponents.Physics;
import PhysicsComponents.Rigidbody2D;
import UtilityComponents.Resources;

public class AIComponents extends Component {

    private transient boolean moveRight = false;
    private transient Rigidbody2D rBod;
    private transient float walkSpeed = 0.6f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f termVel = new Vector2f();
    private transient boolean onGround = false;
    private transient boolean dead = false;
    private transient float killTime = 0.5f;
    private transient StateMachine stateMachine;

    @Override
    public void startComponent() {
        this.stateMachine = gameObj.getComponent(StateMachine.class);
        this.rBod = gameObj.getComponent(Rigidbody2D.class);
        this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
    }

    @Override
    public void updateComponent(float deltaTime) {
        Camera camera = Window.getScene().camera();
        if (this.gameObj.transform.position.x >
                camera.camPos.x + camera.getProjectionSize().x * camera.getZoom()) {
            return;
        }

        if (dead) {
            killTime -= deltaTime;
            if (killTime <= 0) {
                this.gameObj.destroy();
            }
            this.rBod.setVelocity(new Vector2f());
            return;
        }

        if (moveRight) {
            velocity.x = walkSpeed;
        } else {
            velocity.x = -walkSpeed;
        }

        onGroundCheck();
        if (onGround) {
            this.acceleration.y = 0;
            this.velocity.y = 0;
        } else {
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        }

        this.velocity.y += this.acceleration.y * deltaTime;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.termVel.y), -termVel.y);
        this.rBod.setVelocity(velocity);
    }

    public void onGroundCheck() {
        float innerPlayerWidth = 0.25f * 0.7f;
        float yVal = -0.14f;
        onGround = Physics.checkOnGround(this.gameObj, innerPlayerWidth, yVal);
    }

    @Override
    public void beginObjectCollision(GameObject gObj, Contact contact, Vector2f contactNorm) {
        if (dead) {
            return;
        }

        PlayerController playerController = gObj.getComponent(PlayerController.class);
        if (playerController != null) {
            if (!playerController.died() && !playerController.invincibleHurt() &&
                    contactNorm.y > 0.58f) {
                playerController.enemyBounce();
                stomp();
            } else if (!playerController.died() && !playerController.invincible()) {
                playerController.die();
            }
        } else if (Math.abs(contactNorm.y) < 0.1f) {
            moveRight = contactNorm.x < 0;
        }
    }

    public void stomp() {
        stomp(true);
    }

    public void stomp(boolean playSound) {
        this.dead = true;
        this.velocity.zero();
        this.rBod.setVelocity(new Vector2f());
        this.rBod.setAngularVelocity(0.0f);
        this.rBod.setGravityScale(0.0f);
        this.stateMachine.trigger("dead");
        this.rBod.setIsSensor();
        if (playSound) {
            Resources.getSound("assets/audio/stomp.ogg").playAudio();
        }
    }

    // child class to control mushroom AI when block is hit
    public static class MushroomAI extends Component {
        private transient boolean moveRight = true;
        private transient Rigidbody2D rBod;
        private transient Vector2f speed = new Vector2f(1.0f, 0.0f);
        private transient float maxSpeed = 0.85f;
        private transient boolean hitPlayer = false;

        @Override
        public void startComponent() {
            this.rBod = gameObj.getComponent(Rigidbody2D.class);
            Resources.getSound("assets/audio/powerup_appears.ogg").playAudio();
        }

        @Override
        public void updateComponent(float deltaTime) {
            if (moveRight && Math.abs(rBod.getVelocity().x) < maxSpeed) {
                rBod.addVelocity(speed);
            } else if (!moveRight && Math.abs(rBod.getVelocity().x) < maxSpeed) {
                rBod.addVelocity(new Vector2f(-speed.x, speed.y));
            }
        }

        @Override
        public void preSolve(GameObject gObj, Contact contact, Vector2f contactNorm) {
            PlayerController playerController = gObj.getComponent(PlayerController.class);
            if (playerController != null) {
                contact.setEnabled(false);
                if (!hitPlayer) {
                    playerController.powerUp();
                    this.gameObj.destroy();
                    hitPlayer = true;
                }
            }

            if (Math.abs(contactNorm.y) < 0.1f) {
                moveRight = contactNorm.x < 0;
            }
        }
    }
}