package EngineSceneComponents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import MainStudioComponents.Camera;
import MainStudioComponents.GameObject;
import MainStudioComponents.Transform;
import org.joml.Vector2f;
import PhysicsComponents.Physics;
import RenderingComponents.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene {

    private Renderer renderer;
    private Camera cam;
    private Physics physics;
    private boolean isRunning;
    private List<GameObject> pendingObjs;
    private List<GameObject> gameObjs;
    private Scene_Init sceneInit;

    public Scene(Scene_Init sceneInit) {
        this.sceneInit = sceneInit;
        this.physics = new Physics();
        this.gameObjs = new ArrayList<>();
        this.pendingObjs = new ArrayList<>();
        this.renderer = new Renderer();
        this.isRunning = false;
    }

    public void init() {
        this.cam = new Camera(new Vector2f(-250, 0));
        this.sceneInit.loadResources(this);
        this.sceneInit.init(this);

    }

    // called by Window class after scene has initialised
    public void start() {
        for (int i = 0; i < gameObjs.size(); i++) {
            GameObject gObj = gameObjs.get(i);
            gObj.startGameObj();
            this.renderer.add(gObj);
            this.physics.add(gObj);
        }
        isRunning = true;
    }

    public void addObjToScene(GameObject gObj) {
        if (!isRunning) {
            gameObjs.add(gObj);
        } else {
            pendingObjs.add(gObj);
        }
    }

    public List<GameObject> getGameObjs(){
        return this.gameObjs;
    }

    public GameObject getGameObject(int gameObjectId) {
        Optional<GameObject> result = this.gameObjs.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst();
        return result.orElse(null);
    }

    public void updateEditor(float deltaTime) {
        this.cam.modifyProjec();

        for (int i = 0; i < gameObjs.size(); i++) {
            GameObject go = gameObjs.get(i);
            go.updateEditorObjs(deltaTime);

            if (go.dead()) {
                gameObjs.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics.destroyGameObject(go);
                i--;
            }
        }

        for (GameObject go : pendingObjs) {
            gameObjs.add(go);
            go.startGameObj();
            this.renderer.add(go);
            this.physics.add(go);
        }
        pendingObjs.clear();
    }

    public void updateScene(float deltaTime) {
        this.cam.modifyProjec();
        this.physics.update(deltaTime);

        for (int i = 0; i < gameObjs.size(); i++) {
            GameObject gObj = gameObjs.get(i);
            gObj.updateGameObj(deltaTime);

            if (gObj.dead()) {
                gameObjs.remove(i);
                this.renderer.destroyGameObject(gObj);
                this.physics.destroyGameObject(gObj);
                i--;
            }
        }

        for (GameObject gObj : pendingObjs) {
            gameObjs.add(gObj);
            gObj.startGameObj();
            this.renderer.add(gObj);
            this.physics.add(gObj);
        }
        pendingObjs.clear();
    }

    public void render()
    {
        this.renderer.render();
    }


    public Camera camera() {
        return this.cam;
    }


    public void imgui() {
        this.sceneInit.imgui();
    }

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void destroy() {
        for (GameObject go : gameObjs) {
            go.destroy();
        }
    }

    public void save() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new Component.DeserialiseComponent())
                .registerTypeAdapter(GameObject.class, new GameObject.GameObjectDeserialiser())
                .enableComplexMapKeySerialization()
                .create();

        try {
            FileWriter writer = new FileWriter("level.txt");
            List<GameObject> objsToSerialize = new ArrayList<>();
            for (GameObject obj : this.gameObjs) {
                if (obj.doSerialise()) {
                    objsToSerialize.add(obj);
                }
            }
            writer.write(gson.toJson(objsToSerialize));
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new Component.DeserialiseComponent())
                .registerTypeAdapter(GameObject.class, new GameObject.GameObjectDeserialiser())
                .enableComplexMapKeySerialization()
                .create();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
            int maxGameObjID = -1;
            int maxCompID = -1;
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i=0; i < objs.length; i++) {
                addObjToScene(objs[i]);

                for (Component c : objs[i].getAllComponents()) {
                    if (c.getUid() > maxCompID) {
                        maxCompID = c.getUid();
                    }
                }
                if (objs[i].getUid() > maxGameObjID) {
                    maxGameObjID = objs[i].getUid();
                }
            }

            maxCompID++;
            maxGameObjID++;
            Component.init(maxCompID);
            GameObject.init(maxGameObjID);
        }
    }

    public Physics getPhysics() {
        return this.physics;
    }

}