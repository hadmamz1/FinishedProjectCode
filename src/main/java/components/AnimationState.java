package components;

import UtilityComponents.Resources;
import components.spriteFunctionalities.Sprite;

import java.util.ArrayList;
import java.util.List;

public class AnimationState {

    public String title;
    public List<Frame> animationFrames = new ArrayList<>();

    private static Sprite defaultSprite = new Sprite();
    private transient float time = 0.0f;
    private transient int currentSprite = 0;
    public boolean doesLoop = false;

    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new Frame(sprite, frameTime));
    }
    public void refreshTextures() {
        for (Frame frame : animationFrames) {
            frame.sprite.setTexture(Resources.getTexture
                    (frame.sprite.getTexture().getFilepath()));
        }
    }

    public void setDoesLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }

    public void updateAnimation(float dt) {
        if (currentSprite < animationFrames.size()) {
            time -= dt;
            if (time <= 0) {
                if (currentSprite != animationFrames.size() -1 || doesLoop) {
                    currentSprite = (currentSprite + 1) % animationFrames.size();
                }
                time = animationFrames.get(currentSprite).frames;
            }
        }
    }

    public Sprite getCurrentSprite() {
        if (currentSprite < animationFrames.size()) {
            return animationFrames.get(currentSprite).sprite;
        }

        return defaultSprite;
    }
}