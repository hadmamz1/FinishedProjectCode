package components.controling;

import MainStudioComponents.GameObject;
import MainStudioComponents.Listener_Key;
import MainStudioComponents.Window;
import components.Component;
import components.Ground;
import components.spriteFunctionalities.SpriteRenderer;
import components.StateMachine;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;
import PhysicsComponents.Physics;
import PhysicsComponents.PillboxCollider;
import PhysicsComponents.Rigidbody2D;
import PhysicsComponents.BodyType;
import EngineSceneComponents.LevelEditor_Scene_Init;
import UtilityComponents.Resources;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
    private enum PlayerState {
        Small,
        PoweredUp,
        Invincible
    }

    private transient float hurtInvincibilityTimeLeft = 0;
    private transient float hurtInvincibilityTime = 1.3f;
    private transient float deadMaxHeight = 0;
    private transient float deadMinHeight = 0;
    private transient boolean deadGoingUp = true;
    private transient float blinkTime = 0.0f;
    private transient SpriteRenderer spr;
    public float walkSpeed = 1.9f;
    public float jumpBoost = 1.0f;
    public float jumpImpulse = 3.0f;
    public float slowDownForce = 0.05f;
    public Vector2f termVel = new Vector2f(2.1f, 3.1f);

    public transient boolean onGround = false;
    private transient float groundDebounce = 0.0f;
    private transient float groundDebounceLength = 0.1f;
    private transient Rigidbody2D rBod;
    private transient StateMachine stateMachine;
    private transient float bigJumpBoostAmount = 1.05f;
    private transient float playerWidth = 0.25f;
    private PlayerState playerState = PlayerState.Small;
    private transient int jumpTime = 0;
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f velocity = new Vector2f();
    private transient boolean dead = false;
    private transient int enemyBounce = 0;

    @Override
    public void startComponent() {
        this.spr = gameObj.getComponent(SpriteRenderer.class);
        this.rBod = gameObj.getComponent(Rigidbody2D.class);
        this.stateMachine = gameObj.getComponent(StateMachine.class);
        this.rBod.setGravityScale(0.0f);
    }

    @Override
    public void updateComponent(float deltaTime) {
        if (dead) {
            if (this.gameObj.transform.position.y < deadMaxHeight && deadGoingUp) {
                this.gameObj.transform.position.y += deltaTime * walkSpeed / 2.0f;
            } else if (this.gameObj.transform.position.y >= deadMaxHeight && deadGoingUp) {
                deadGoingUp = false;
            } else if (!deadGoingUp && gameObj.transform.position.y > deadMinHeight) {
                this.rBod.setBodyType(BodyType.Kinematic);
                this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
                this.velocity.y += this.acceleration.y * deltaTime;
                this.velocity.y = Math.max(Math.min(this.velocity.y, this.termVel.y), -this.termVel.y);
                this.rBod.setVelocity(this.velocity);
                this.rBod.setAngularVelocity(0);
            } else if (!deadGoingUp && gameObj.transform.position.y <= deadMinHeight) {
                Window.changeScene(new LevelEditor_Scene_Init());
            }
            return;
        }

        if (hurtInvincibilityTimeLeft > 0) {
            hurtInvincibilityTimeLeft -= deltaTime;
            blinkTime -= deltaTime;

            if (blinkTime <= 0) {
                blinkTime = 0.2f;
                if (spr.getColour().w == 1) {
                    spr.setColour(new Vector4f(1, 1, 1, 0));
                } else {
                    spr.setColour(new Vector4f(1, 1, 1, 1));
                }
            } else {
                if (spr.getColour().w == 0) {
                    spr.setColour(new Vector4f(1, 1, 1, 1));
                }
            }
        }
        if (Listener_Key.keyIsPressed(GLFW_KEY_RIGHT) || Listener_Key.keyIsPressed(GLFW_KEY_D)) {
            this.gameObj.transform.scale.x = playerWidth;
            this.acceleration.x = walkSpeed;

            if (this.velocity.x < 0) {
                this.stateMachine.trigger("switchDirection");
                this.velocity.x += slowDownForce;
            } else {
                this.stateMachine.trigger("startRunning");
            }
        } else if (Listener_Key.keyIsPressed(GLFW_KEY_LEFT) || Listener_Key.keyIsPressed(GLFW_KEY_A)) {
            this.gameObj.transform.scale.x = -playerWidth;
            this.acceleration.x = -walkSpeed;

            if (this.velocity.x > 0) {
                this.stateMachine.trigger("switchDirection");
                this.velocity.x -= slowDownForce;
            } else {
                this.stateMachine.trigger("startRunning");
            }
        } else {
            this.acceleration.x = 0;
            if (this.velocity.x > 0) {
                this.velocity.x = Math.max(0, this.velocity.x - slowDownForce);
            } else if (this.velocity.x < 0) {
                this.velocity.x = Math.min(0, this.velocity.x + slowDownForce);
            }

            if (this.velocity.x == 0) {
                this.stateMachine.trigger("stopRunning");
            }
        }

        onGrounCheck();
        if ((Listener_Key.keyIsPressed(GLFW_KEY_SPACE) || Listener_Key.keyIsPressed(GLFW_KEY_UP))
                && (jumpTime > 0 || onGround || groundDebounce > 0)) {
            if ((onGround || groundDebounce > 0) && jumpTime == 0) {
                Resources.getSound("assets/audio/jump-small.ogg").playAudio();
                jumpTime = 28;
                this.velocity.y = jumpImpulse;
            } else if (jumpTime > 0) {
                jumpTime--;
                this.velocity.y = ((jumpTime / 2.2f) * jumpBoost);
            } else {
                this.velocity.y = 0;
            }
            groundDebounce = 0;
        } else if (enemyBounce > 0) {
            enemyBounce--;
            this.velocity.y = ((enemyBounce / 2.2f) * jumpBoost);
        } else if (!onGround) {
            if (this.jumpTime > 0) {
                this.velocity.y *= 0.35f;
                this.jumpTime = 0;
            }
            groundDebounce -= deltaTime;
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        } else {
            this.velocity.y = 0;
            this.acceleration.y = 0;
            groundDebounce = groundDebounceLength;
        }

        this.velocity.x += this.acceleration.x * deltaTime;
        this.velocity.y += this.acceleration.y * deltaTime;
        this.velocity.x = Math.max(Math.min(this.velocity.x, this.termVel.x), -this.termVel.x);
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.termVel.y), -this.termVel.y);
        this.rBod.setVelocity(this.velocity);
        this.rBod.setAngularVelocity(0);

        if (!onGround) {
            stateMachine.trigger("jump");
        } else {
            stateMachine.trigger("stopJumping");
        }
    }

    public void onGrounCheck() {
        float innerPlayerWidth = this.playerWidth * 0.6f;
        float yVal = playerState == PlayerState.Small ? -0.14f : -0.24f;
        onGround = Physics.checkOnGround(this.gameObj, innerPlayerWidth, yVal);
    }

    @Override
    public void beginObjectCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
        if (dead) return;

        if (collidingObject.getComponent(Ground.class) != null) {
            if (Math.abs(contactNormal.x) > 0.8f) {
                this.velocity.x = 0;
            } else if (contactNormal.y > 0.8f) {
                this.velocity.y = 0;
                this.acceleration.y = 0;
                this.jumpTime = 0;
            }
        }
    }

    public boolean small() {
        return this.playerState == PlayerState.Small;
    }

    public void powerUp()
    {
        if(playerState == PlayerState.Small)
        {
            playerState = PlayerState.PoweredUp;
            Resources.getSound("assets/audio/powerup.ogg").playAudio();
            PillboxCollider pb = gameObj.getComponent(PillboxCollider.class);
            if (pb != null)
            {
                jumpBoost += bigJumpBoostAmount;
                walkSpeed += bigJumpBoostAmount;
            }
        }

        stateMachine.trigger("powerup");
    }
    public void enemyBounce() {
        this.enemyBounce = 8;
    }

    public boolean invincible() {
        return this.playerState == PlayerState.Invincible || this.hurtInvincibilityTimeLeft > 0;
    }
    public boolean invincibleHurt() {
        return this.hurtInvincibilityTimeLeft > 0;
    }

    public void die() {
        this.stateMachine.trigger("die");
        if (this.playerState == PlayerState.Small) {
            this.velocity.set(0, 0);
            this.acceleration.set(0, 0);
            this.rBod.setVelocity(new Vector2f());
            this.dead = true;
            this.rBod.setIsSensor();
            Resources.getSound("assets/audio/die.ogg").playAudio();
            deadMaxHeight = this.gameObj.transform.position.y + 0.3f;
            this.rBod.setBodyType(BodyType.Static);
            if (gameObj.transform.position.y > 0) {
                deadMinHeight = -0.25f;
            }
        } else if (this.playerState == PlayerState.PoweredUp) {
            this.playerState = PlayerState.Small;
            gameObj.transform.scale.y = 0.25f;
            PillboxCollider pb = gameObj.getComponent(PillboxCollider.class);
            if (pb != null) {
                jumpBoost /= bigJumpBoostAmount;
                walkSpeed /= bigJumpBoostAmount;
                pb.setHeight(0.31f);
            }
            hurtInvincibilityTimeLeft = hurtInvincibilityTime;
            Resources.getSound("assets/audio/powerDown.ogg").playAudio();
        }
    }

    public boolean died() {
        return this.dead;
    }

}