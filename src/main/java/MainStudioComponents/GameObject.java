package MainStudioComponents;

import com.google.gson.*;
import components.Component;
import components.spriteFunctionalities.SpriteRenderer;
import imgui.ImGui;
import UtilityComponents.Resources;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private static int ID_COUNTER = 0;
    private int uid = -1;
    private boolean doSerialise = true;
    private boolean dead = false;

    private List<Component> components;
    public String name;
    public transient Transform transform;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = transform;

        this.uid = ID_COUNTER++;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false: "ERROR!: Casting Component";
                }
            }
        }

        return null;
    }

    public <T extends Component> void remComponent(Class<T> componentClass) {
        for (int i=0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        c.generateID();
        this.components.add(c);
        c.gameObj = this;
    }

    public void updateGameObj(float deltaTime) {
        for (int i=0; i < components.size(); i++) {
            components.get(i).updateComponent(deltaTime);
        }
    }

    public void updateEditorObjs(float deltaTime) {
        for (int i=0; i < components.size(); i++) {
            components.get(i).updateEditor(deltaTime);
        }
    }


    public void startGameObj() {
        for (int i=0; i < components.size(); i++) {
            components.get(i).startComponent();
        }
    }

    public void imgui() {
        for (Component c : components) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
                c.imgui();
        }
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }
    public boolean dead() {
        return this.dead;
    }

    public GameObject copy() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Component.class, new Component.DeserialiseComponent())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserialiser())
                .enableComplexMapKeySerialization()
                .create();
        String objAsJSON = gson.toJson(this);
        GameObject obj = gson.fromJson(objAsJSON, GameObject.class);

        obj.generateUID();
        for (Component c : obj.getAllComponents()) {
            c.generateID();
        }

        SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
        if (sprite != null && sprite.getTexture() != null) {
            sprite.setTexture(Resources.getTexture(sprite.getTexture().getFilepath()));
        }

        return obj;
    }

    public void destroy() {
        this.dead = true;
        for (int i=0; i < components.size(); i++) {
            components.get(i).destroyComponents();
        }
    }

    public int getUid() {
        return this.uid;
    }

    public List<Component> getAllComponents() {
        return this.components;
    }

    public void dontSerialise() {
        this.doSerialise = false;
    }

    public boolean doSerialise() {
        return this.doSerialise;
    }


    public void generateUID() {
        this.uid = ID_COUNTER++;
    }


    public static class GameObjectDeserialiser implements JsonDeserializer<GameObject> {
        @Override
        public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            JsonArray components = jsonObject.getAsJsonArray("components");

            GameObject gameObj = new GameObject(name);
            for (JsonElement e : components) {
                Component c = context.deserialize(e, Component.class);
                gameObj.addComponent(c);
            }
            gameObj.transform = gameObj.getComponent(Transform.class);
            return gameObj;
        }
    }
}