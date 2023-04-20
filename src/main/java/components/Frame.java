package components;

import components.spriteFunctionalities.Sprite;

public class Frame {
    public Sprite sprite;
    public float frames;

    public Frame() {

    }

    public Frame(Sprite sprite, float time) {
        this.sprite = sprite;
        this.frames = time;
    }
}