package components;

import components.spriteFunctionalities.SpriteRenderer;
import imgui.ImGui;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StateMachine extends Component {
    private class TriggerState {
        public String state;
        public String trigger;

        public TriggerState() {}

        public TriggerState(String state, String trigger) {
            this.state = state;
            this.trigger = trigger;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() != TriggerState.class) return false;
            TriggerState t2 = (TriggerState)obj;
            return t2.trigger.equals(this.trigger) && t2.state.equals(this.state);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, trigger);
        }
    }

    public HashMap<TriggerState, String> transferState = new HashMap<>();
    private transient AnimationState currentState = null;
    private List<AnimationState> states = new ArrayList<>();
    private String defaultStateTitle = "";

    public void addTriggerState(String start, String end, String onTrigger) {
        this.transferState.put(new TriggerState(start, onTrigger), end);
    }
    public void textureRefresh() {
        for (AnimationState state : states) {
            state.refreshTextures();
        }
    }

    public void addState(AnimationState state) {
        this.states.add(state);
    }

    public void setDefaultState(String animationTitle) {
        for (AnimationState state : states) {
            if (state.title.equals(animationTitle)) {
                defaultStateTitle = animationTitle;
                if (currentState == null) {
                    currentState = state;
                }
                return;
            }
        }

        System.out.println("Unable to find default state '" + animationTitle + "'");
    }

    public void trigger(String trigger) {
        for (TriggerState state : transferState.keySet()) {
            if (state.state.equals(currentState.title) && state.trigger.equals(trigger)) {
                if (transferState.get(state) != null) {
                    int newStateIndex = stateIndexOf(transferState.get(state));
                    if (newStateIndex > -1) {
                        currentState = states.get(newStateIndex);
                    }
                }
                return;
            }
        }

        System.out.println("Unable to find trigger '" + trigger + "'");
    }

    private int stateIndexOf(String stateTitle) {
        int index = 0;
        for (AnimationState state : states) {
            if (state.title.equals(stateTitle)) {
                return index;
            }
            index++;
        }

        return -1;
    }


    @Override
    public void startComponent() {
        for (AnimationState state : states) {
            if (state.title.equals(defaultStateTitle)) {
                currentState = state;
                break;
            }
        }
    }

    @Override
    public void updateComponent(float deltaTime) {
        if (currentState != null) {
            currentState.updateAnimation(deltaTime);
            SpriteRenderer sprite = gameObj.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentState.getCurrentSprite());
                sprite.setTexture(currentState.getCurrentSprite().getTexture());
            }
        }
    }

    @Override
    public void updateEditor(float deltaTime) {
        if (currentState != null) {
            currentState.updateAnimation(deltaTime);
            SpriteRenderer sprite = gameObj.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentState.getCurrentSprite());
                sprite.setTexture(currentState.getCurrentSprite().getTexture());
            }
        }
    }

    @Override
    public void imgui() {
        for (AnimationState state : states) {
            ImString title = new ImString(state.title);
            ImGui.inputText("State: ", title);
            state.title = title.get();

            int index = 0;
            for (Frame frame : state.animationFrames) {
                float[] tmp = new float[1];
                tmp[0] = frame.frames;
                ImGui.dragFloat("Frame(" + index + ") Time: ", tmp, 0.01f);
                frame.frames = tmp[0];
                index++;
            }
        }
    }
}