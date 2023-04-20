package components.blockManagement;

import UtilityComponents.Resources;
import components.controling.PlayerController;

public class BreakableBrick extends Block {

    @Override
    void hitPlayer(PlayerController player) {
        if (!player.small()) {
            Resources.getSound("assets/audio/break_block.ogg").playAudio();
            gameObj.destroy();
        }
    }
}