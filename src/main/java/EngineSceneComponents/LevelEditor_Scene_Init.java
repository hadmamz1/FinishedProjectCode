package EngineSceneComponents;

import components.*;
import components.blockManagement.BreakableBrick;
import components.controling.KeyControls;
import components.controling.MouseControls;
import components.spriteFunctionalities.Sprite;
import components.spriteFunctionalities.SpriteRenderer;
import components.spriteFunctionalities.Spritesheet;
import imgui.ImGui;
import imgui.ImVec2;
import MainStudioComponents.*;
import org.joml.Vector2f;
import PhysicsComponents.Box2DCollider;
import PhysicsComponents.Rigidbody2D;
import PhysicsComponents.BodyType;
//import sun.security.ssl.Debug;
import UtilityComponents.Resources;

public class LevelEditor_Scene_Init extends Scene_Init {

    private Spritesheet sprites;

    private GameObject levelEditorComponents;

    public LevelEditor_Scene_Init() {

    }

    @Override
    public void init(Scene scene) {
        sprites = Resources.getSpritesheet("assets/images/spritesheets/tileTestSS.png");
        Spritesheet gizmos = Resources.getSpritesheet("assets/images/gizmos.png");

        levelEditorComponents = scene.createGameObject("LevelEditor");
        levelEditorComponents.dontSerialise();
        levelEditorComponents.addComponent(new EditorCamera(scene.camera()));
        levelEditorComponents.addComponent(new MouseControls());
        levelEditorComponents.addComponent(new KeyControls());
        levelEditorComponents.addComponent(new GridLines());
        levelEditorComponents.addComponent(new GizmoSystem(gizmos));
        scene.addObjToScene(levelEditorComponents);
    }

    @Override
    public void loadResources(Scene scene) {
        Resources.getShader("assets/shaders/default.glsl");

        Resources.addSpritesheet("assets/images/spritesheets/tileTestSS.png",
                new Spritesheet(Resources.getTexture("assets/images/spritesheets/tileTestSS.png"),
                        16, 16, 40, 0));
        Resources.addSpritesheet("assets/images/gizmos.png",
                new Spritesheet(Resources.getTexture("assets/images/gizmos.png"),
                        24, 48, 3, 0));
        Resources.getTexture("assets/images/blendImage2.png");
        // for animation and items
        Resources.addSpritesheet("assets/images/spritesheets/spritesheet.png",
                new Spritesheet(Resources.getTexture("assets/images/spritesheets/spritesheet.png"),
                        16, 16, 64, 0));
        Resources.addSpritesheet("assets/images/spritesheets/items.png",
                new Spritesheet(Resources.getTexture("assets/images/spritesheets/items.png"),
                        16, 16, 34, 0));
        // powerup
        Resources.addSpritesheet("assets/images/spritesheets/powerUpSpritesheet.png",
                new Spritesheet(Resources.getTexture("assets/images/spritesheets/powerUpSpritesheet.png"),
                        16, 16, 32, 0));
        // enemy AI
        Resources.addSpritesheet("assets/images/spritesheets/tomatohead1.png",
                new Spritesheet(Resources.getTexture("assets/images/spritesheets/tomatohead1.png"),
                        12, 12, 4, 0));


        // sounds
        Resources.addAudio("assets/audio/fireball.ogg", false);
        Resources.addAudio("assets/audio/jump-super.ogg", false);
        Resources.addAudio("assets/audio/jump-small.ogg", false);
        Resources.addAudio("assets/audio/invincible.ogg", false);
        Resources.addAudio("assets/audio/break_block.ogg", false);
        Resources.addAudio("assets/audio/stomp.ogg", false);
        Resources.addAudio("assets/audio/die.ogg", false);
        Resources.addAudio("assets/audio/powerup.ogg", false);
        Resources.addAudio("assets/audio/powerup_appears.ogg", false);
        Resources.addAudio("assets/audio/gameover.ogg", false);
        Resources.addAudio("assets/audio/coin.ogg", false);
        Resources.addAudio("assets/audio/1-up.ogg", false);
        Resources.addAudio("assets/audio/bump.ogg", false);
        Resources.addAudio("assets/audio/powerDown.ogg", false);


        for (GameObject gameObj : scene.getGameObjs()) {
            if (gameObj.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = gameObj.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(Resources.getTexture(spr.getTexture().getFilepath()));
                }
            }

            if (gameObj.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = gameObj.getComponent(StateMachine.class);
                stateMachine.textureRefresh();
            }

        }
    }


    @Override
    public void imgui() {
        ImGui.begin("Level Editor Components");
        levelEditorComponents.imgui();
        ImGui.end();

        ImGui.begin("BLOCK DROP");

        if (ImGui.beginTabBar("TabBar")) {
            if (ImGui.beginTabItem("Solid Blocks")) {
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < sprites.size(); i++) {
                    if ((i == 6)) continue; if ((i == 7)) continue;
                    if ((i == 14)) continue; if ((i == 15)) continue;
                    if ((i == 22)) continue; if ((i == 23)) continue;
                    if ((i == 30)) continue; if ((i == 31)) continue;
                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 2.5f;
                    float spriteHeight = sprite.getHeight() * 2.5f;
                    int spriteID = sprite.getTextureID();
                    Vector2f[] texCoords = sprite.getTextureCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(spriteID, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject obj = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                        Rigidbody2D rb = new Rigidbody2D();
                        rb.setBodyType(BodyType.Static);
                        obj.addComponent(rb);
                        Box2DCollider b2d = new Box2DCollider();
                        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
                        obj.addComponent(b2d);
                        obj.addComponent(new Ground());
                        /*
                        if (i == 6 || i == 7 || i == 14 || i == 15
                                || i == 22 || i == 23 || i == 30 || i == 31) {
                            obj.addComponent(new BreakableBrick());
                        }
                         */
                        levelEditorComponents.getComponent(MouseControls.class).objPickingUp(obj);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Breakable Blocks")) {
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < sprites.size(); i++) {
                    if (i != 6 && i != 7 && i != 14 && i != 15
                            && i != 22 && i != 23 && i != 30 && i != 31)
                        continue;


                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 2.5f;
                    float spriteHeight = sprite.getHeight() * 2.5f;
                    int spriteID = sprite.getTextureID();
                    Vector2f[] texCoords = sprite.getTextureCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(spriteID, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject obj = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                        Rigidbody2D rb = new Rigidbody2D();
                        rb.setBodyType(BodyType.Static);
                        obj.addComponent(rb);
                        Box2DCollider b2d = new Box2DCollider();
                        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
                        obj.addComponent(b2d);
                        obj.addComponent(new Ground());

                        if (i == 6 || i == 7 || i == 14 || i == 15
                                || i == 22 || i == 23 || i == 30 || i == 31) {
                            obj.addComponent(new BreakableBrick());
                        }

                        levelEditorComponents.getComponent(MouseControls.class).objPickingUp(obj);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Special Sprites"))
            {
                int uid = 0;
                Spritesheet specialSprites = Resources.getSpritesheet("assets/images/spritesheets/spritesheet.png");
                Sprite sprite = specialSprites.getSprite(4);
                float spriteWidth = sprite.getWidth() * 2.5f;
                float spriteHeight = sprite.getHeight() * 2.5f;
                int spriteID = sprite.getTextureID();
                Vector2f[] texCoords = sprite.getTextureCoords();

                ImGui.pushID(uid++);
                if (ImGui.imageButton(spriteID, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateCharacter();
                    levelEditorComponents.getComponent(MouseControls.class).objPickingUp(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                Spritesheet specialBlocks = Resources.getSpritesheet("assets/images/spritesheets/items.png");
                sprite = specialBlocks.getSprite(0);
                spriteID = sprite.getTextureID();
                texCoords = sprite.getTextureCoords();

                ImGui.pushID(uid++);
                if (ImGui.imageButton(spriteID, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateSpecialBlock();
                    levelEditorComponents.getComponent(MouseControls.class).objPickingUp(object);
                }
                ImGui.popID();
                ImGui.sameLine();

                Spritesheet itemSprites = Resources.getSpritesheet("assets/images/spritesheets/tomatohead1.png");
                sprite = itemSprites.getSprite(0);
                spriteID = sprite.getTextureID();
                texCoords = sprite.getTextureCoords();
                ImGui.pushID(uid++);
                if (ImGui.imageButton(spriteID, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generateEnemy();
                    levelEditorComponents.getComponent(MouseControls.class).objPickingUp(object);
                }
                ImGui.popID();

                ImGui.endTabItem();
            }

            ImGui.endTabBar();
        }


        ImGui.end();
    }
}