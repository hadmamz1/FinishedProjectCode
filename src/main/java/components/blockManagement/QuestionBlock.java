package components.blockManagement;

import MainStudioComponents.GameObject;
import MainStudioComponents.Prefabs;
import MainStudioComponents.Window;
import UtilityComponents.Resources;
import components.Component;
import components.StateMachine;
import components.controling.PlayerController;
import org.joml.Vector2f;

public class QuestionBlock extends Block {
    private enum BlockType {
        Coin,
        Powerup
    }

    public BlockType blockType = BlockType.Coin;

    @Override
    void hitPlayer(PlayerController player) {
        switch(blockType) {
            case Coin:
                doCoin(player);
                break;
            case Powerup:
                doPowerup(player);
                break;
        }

        StateMachine stateMachine = gameObj.getComponent(StateMachine.class);
        if (stateMachine != null) {
            stateMachine.trigger("setInactive");
            this.setInactive();
        }
    }


    private void doCoin(PlayerController playerController) {
        GameObject coin = Prefabs.generateBlockCoin();
        coin.transform.position.set(this.gameObj.transform.position);
        coin.transform.position.y += 0.25f;
        Window.getScene().addObjToScene(coin);
    }

    private void doPowerup(PlayerController playerController) {
        if (playerController.small()) {
            spawnMushroom();
        }
    }

    private void spawnMushroom() {
        GameObject mushroom = Prefabs.generateMushroom();
        mushroom.transform.position.set(gameObj.transform.position);
        mushroom.transform.position.y += 0.25f;
        Window.getScene().addObjToScene(mushroom);
    }

    public static class Coin extends Component {
        private Vector2f topY;
        private float coinSpeed = 1.4f;

        @Override
        public void startComponent() {
            topY = new Vector2f(this.gameObj.transform.position.y).add(0, 0.5f);
            Resources.getSound("assets/audio/coin.ogg").playAudio();
        }

        @Override
        public void updateComponent(float deltaTime) {
            if (this.gameObj.transform.position.y < topY.y) {
                this.gameObj.transform.position.y += deltaTime * coinSpeed;
                this.gameObj.transform.scale.x -= (0.5f * deltaTime) % -1.0f;
            } else {
                gameObj.destroy();
            }
        }
    }
}