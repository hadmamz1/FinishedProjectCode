package UtilityComponents;

import components.spriteFunctionalities.Spritesheet;
import MainStudioComponents.Audio;
import RenderingComponents.Shader;
import RenderingComponents.Texture;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Resources {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();
    private static Map<String, Audio> sounds = new HashMap<>();


    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        if (Resources.shaders.containsKey(file.getAbsolutePath())) {
            return Resources.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(resourceName);
            shader.compile();
            Resources.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (Resources.textures.containsKey(file.getAbsolutePath())) {
            return Resources.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(resourceName);
            Resources.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpritesheet(String resourceName, Spritesheet spritesheet) {
        File file = new File(resourceName);
        if (!Resources.spritesheets.containsKey(file.getAbsolutePath())) {
            Resources.spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static Spritesheet getSpritesheet(String resourceName) {
        File file = new File(resourceName);
        if (!Resources.spritesheets.containsKey(file.getAbsolutePath())) {
            assert false : "Error: Tried to access spritesheet '" + resourceName + "' and it has not been added to asset pool.";
        }
        return Resources.spritesheets.getOrDefault(file.getAbsolutePath(), null);
    }

    public static Collection<Audio> getAllSounds() {
        return sounds.values();
    }

    public static Audio getSound(String soundFile) {
        File file = new File(soundFile);
        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            assert false : "Sound file not added '" + soundFile + "'";
        }

        return null;
    }

    public static Audio addAudio(String soundFile, boolean loops) {
        File file = new File(soundFile);
        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            Audio sound = new Audio(file.getAbsolutePath(), loops);
            Resources.sounds.put(file.getAbsolutePath(), sound);
            return sound;
        }
    }
}