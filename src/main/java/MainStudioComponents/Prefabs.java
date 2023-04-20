package MainStudioComponents;

import components.*;
import components.blockManagement.QuestionBlock;
import components.controling.PlayerController;
import components.spriteFunctionalities.AIComponents;
import components.spriteFunctionalities.Sprite;
import components.spriteFunctionalities.SpriteRenderer;
import components.spriteFunctionalities.Spritesheet;
import org.joml.Vector2f;
import PhysicsComponents.Box2DCollider;
import PhysicsComponents.CircleCollider;
import PhysicsComponents.PillboxCollider;
import PhysicsComponents.Rigidbody2D;
import PhysicsComponents.BodyType;
import UtilityComponents.Resources;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getScene().createGameObject("Sprite Object");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }

    public static GameObject generateCharacter() {
        Spritesheet playerSprites = Resources.getSpritesheet("assets/images/spritesheets/spritesheet.png");
        //Spritesheet poweredUpSprites = AssetPool.getSpritesheet("assets/images/spritesheets/powerUpSpritesheet.png");
        //Spritesheet enemySprite = AssetPool.getSpritesheet("assets/images/spritesheets/tomatohead1.png");
        GameObject character = generateSpriteObject(playerSprites.getSprite(4),
                0.25f, 0.25f);

        AnimationState animate = new AnimationState();
        animate.title = "Animate";
        float defaultFrameTime = 0.2f;
        animate.addFrame(playerSprites.getSprite(4), defaultFrameTime);
        animate.addFrame(playerSprites.getSprite(5), defaultFrameTime);
        animate.addFrame(playerSprites.getSprite(6), defaultFrameTime);
        animate.addFrame(playerSprites.getSprite(7), defaultFrameTime);
        animate.setDoesLoop(true);

        AnimationState switchDirection = new AnimationState();
        switchDirection.title = "Switch Direction";
        switchDirection.addFrame(playerSprites.getSprite(4), 0.1f);
        switchDirection.setDoesLoop(false);

        AnimationState idle = new AnimationState();
        idle.title = "Idle";
        idle.addFrame(playerSprites.getSprite(4), 0.1f);
        idle.setDoesLoop(false);

        AnimationState jump = new AnimationState();
        jump.title = "Jump";
        jump.addFrame(playerSprites.getSprite(22), 0.1f);
        jump.setDoesLoop(false);

        AnimationState die = new AnimationState();
        die.title = "Die";
        die.addFrame(playerSprites.getSprite(29), 0.1f);
        die.setDoesLoop(false);


        // powerup animations
        int powerUpoffset = 36;
        AnimationState powerupRun = new AnimationState();
        powerupRun.title = "PowerUpRun";
        powerupRun.addFrame(playerSprites.getSprite(powerUpoffset), defaultFrameTime);
        powerupRun.addFrame(playerSprites.getSprite(powerUpoffset + 1), defaultFrameTime);
        powerupRun.addFrame(playerSprites.getSprite(powerUpoffset + 2), defaultFrameTime);
        powerupRun.addFrame(playerSprites.getSprite(powerUpoffset + 3), defaultFrameTime);
        powerupRun.setDoesLoop(true);

        // powerup switch animations
        AnimationState powerupRunSwitch = new AnimationState();
        powerupRunSwitch.title = "PowerUpRunSwitch";
        powerupRunSwitch.addFrame(playerSprites.getSprite(powerUpoffset), 0.1f);
        powerupRunSwitch.setDoesLoop(false);

        // powerup idle
        AnimationState pwrIdle = new AnimationState();
        pwrIdle.title = "PwrIdle";
        pwrIdle.addFrame(playerSprites.getSprite(powerUpoffset), 0.1f);
        pwrIdle.setDoesLoop(false);

        // powerup jump
        AnimationState pwrJump = new AnimationState();
        pwrJump.title = "PwrJump";
        pwrJump.addFrame(playerSprites.getSprite(powerUpoffset + 18), 0.1f);
        pwrJump.setDoesLoop(false);



        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(animate);
        stateMachine.addState(switchDirection);
        stateMachine.addState(idle);
        stateMachine.addState(jump);
        stateMachine.addState(die);
        stateMachine.addState(powerupRun);
        stateMachine.addState(powerupRunSwitch);
        stateMachine.addState(pwrJump);

        stateMachine.setDefaultState(idle.title);
        // default character
        stateMachine.addTriggerState(animate.title, switchDirection.title, "switchDirection");
        stateMachine.addTriggerState(animate.title, idle.title, "stopRunning");
        stateMachine.addTriggerState(animate.title, jump.title, "jump");
        stateMachine.addTriggerState(switchDirection.title, idle.title, "stopRunning");
        stateMachine.addTriggerState(switchDirection.title, animate.title, "startRunning");
        stateMachine.addTriggerState(switchDirection.title, jump.title, "jump");
        stateMachine.addTriggerState(idle.title, animate.title, "startRunning");
        stateMachine.addTriggerState(idle.title, jump.title, "jump");
        stateMachine.addTriggerState(jump.title, idle.title, "stopJumping");

        // poweredup character
        stateMachine.addTriggerState(powerupRun.title, powerupRunSwitch.title, "switchDirection");
        stateMachine.addTriggerState(powerupRun.title, pwrIdle.title, "stopRunning");
        stateMachine.addTriggerState(powerupRun.title, pwrJump.title, "jump");
        stateMachine.addTriggerState(powerupRunSwitch.title, pwrIdle.title, "stopRunning");
        stateMachine.addTriggerState(powerupRunSwitch.title, powerupRun.title, "startRunning");
        stateMachine.addTriggerState(powerupRunSwitch.title, pwrJump.title, "jump");
        stateMachine.addTriggerState(pwrIdle.title, powerupRun.title, "startRunning");
        stateMachine.addTriggerState(pwrIdle.title, pwrJump.title, "jump");
        stateMachine.addTriggerState(pwrJump.title, pwrIdle.title, "stopJumping");

        stateMachine.addTriggerState(animate.title, powerupRun.title, "powerup");
        stateMachine.addTriggerState(idle.title, pwrIdle.title, "powerup");
        stateMachine.addTriggerState(switchDirection.title, powerupRunSwitch.title, "powerup");
        stateMachine.addTriggerState(jump.title, pwrJump.title, "powerup");

        stateMachine.addTriggerState(powerupRun.title, animate.title, "damage");
        stateMachine.addTriggerState(pwrIdle.title, idle.title, "damage");
        stateMachine.addTriggerState(powerupRunSwitch.title, switchDirection.title, "damage");
        stateMachine.addTriggerState(pwrJump.title, jump.title, "damage");




        // died characters
        stateMachine.addTriggerState(animate.title, die.title, "die");
        stateMachine.addTriggerState(switchDirection.title, die.title, "die");
        stateMachine.addTriggerState(idle.title, die.title, "die");
        stateMachine.addTriggerState(jump.title, die.title, "die");
        stateMachine.addTriggerState(powerupRunSwitch.title, switchDirection.title, "die");
        stateMachine.addTriggerState(powerupRun.title, animate.title, "die");
        stateMachine.addTriggerState(pwrIdle.title, idle.title, "die");
        stateMachine.addTriggerState(pwrJump.title, jump.title, "die");

        character.addComponent(stateMachine);

        PillboxCollider pb = new PillboxCollider();
        pb.width = 0.39f;
        pb.height = 0.31f;
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setContinuousCollision(false);
        rb.setFixedRotation(true);
        rb.setMass(25.0f);

        character.addComponent(pb);
        character.addComponent(rb);
        character.addComponent(new PlayerController());



        return character;
    }

    public static GameObject generateSpecialBlock() {
        Spritesheet specialSprites = Resources.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject specialBlock = generateSpriteObject(specialSprites.getSprite(0),
                0.25f, 0.25f);
        AnimationState animate = new AnimationState();
        animate.title = "SpecialBlock";
        float defFrameTime = 0.23f;
        animate.addFrame(specialSprites.getSprite(0), 0.57f);
        animate.addFrame(specialSprites.getSprite(1), defFrameTime);
        animate.addFrame(specialSprites.getSprite(2), defFrameTime);
        animate.setDoesLoop(true);

        AnimationState inactive = new AnimationState();
        inactive.title = "Inactive";
        inactive.addFrame(specialSprites.getSprite(3), 0.1f);
        inactive.setDoesLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(animate);
        stateMachine.addState(inactive);
        stateMachine.setDefaultState(animate.title);
        stateMachine.addTriggerState(animate.title, inactive.title, "setInactive");
        specialBlock.addComponent(stateMachine);

        specialBlock.addComponent(new QuestionBlock());

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        specialBlock.addComponent(rb);
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
        specialBlock.addComponent(b2d);
        specialBlock.addComponent(new Ground());

        return specialBlock;
    }

    public static GameObject generateBlockCoin() {
        Spritesheet items = Resources.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject coin = generateSpriteObject(items.getSprite(7), 0.25f, 0.25f);

        AnimationState coinFlip = new AnimationState();
        coinFlip.title = "CoinFlip";
        float defaultFrameTime = 0.23f;
        coinFlip.addFrame(items.getSprite(7), 0.57f);
        coinFlip.addFrame(items.getSprite(8), defaultFrameTime);
        coinFlip.addFrame(items.getSprite(9), defaultFrameTime);
        coinFlip.setDoesLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(coinFlip);
        stateMachine.setDefaultState(coinFlip.title);
        coin.addComponent(stateMachine);
        coin.addComponent(new QuestionBlock());

        coin.addComponent(new QuestionBlock.Coin());

        return coin;
    }

    public static GameObject generateMushroom() {
        Spritesheet items = Resources.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject mushroom = generateSpriteObject(items.getSprite(10), 0.25f, 0.25f);

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        mushroom.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.14f);
        mushroom.addComponent(circleCollider);
        mushroom.addComponent(new AIComponents.MushroomAI());

        return mushroom;
    }

    public static GameObject generateEnemy()
    {
        Spritesheet items = Resources.getSpritesheet("assets/images/spritesheets/tomatohead1.png");
        GameObject enemy = generateSpriteObject(items.getSprite(0),
                0.25f, 0.25f);

        //8,9,10,11
        AnimationState animate = new AnimationState();
        animate.title = "Animate";
        float defaultFrameTime = 0.23f;
        animate.addFrame(items.getSprite(0), defaultFrameTime);
        animate.addFrame(items.getSprite(1), defaultFrameTime);
        animate.addFrame(items.getSprite(3), defaultFrameTime);
        animate.setDoesLoop(true);

        AnimationState dead = new AnimationState();
        dead.title = "Dead";
        dead.addFrame(items.getSprite(2), 0.1f);
        dead.setDoesLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(animate);
        stateMachine.addState(dead);
        stateMachine.setDefaultState(animate.title);
        stateMachine.addTriggerState(animate.title, dead.title, "dead");
        enemy.addComponent(stateMachine);

        Rigidbody2D rBod = new Rigidbody2D();
        rBod.setBodyType(BodyType.Dynamic);
        rBod.setMass(0.1f);
        rBod.setFixedRotation(true);
        enemy.addComponent(rBod);
        CircleCollider circle = new CircleCollider();
        circle.setRadius(0.12f);
        enemy.addComponent(circle);

        enemy.addComponent(new AIComponents());

        return enemy;

    }
}