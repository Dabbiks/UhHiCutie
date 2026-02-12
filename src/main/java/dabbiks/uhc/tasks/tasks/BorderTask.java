package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.tasks.Task;

import static dabbiks.uhc.Main.INSTANCE;
import static dabbiks.uhc.Main.stateU;

public class BorderTask extends Task {

    protected long getPeriod() {
        return 20;
    }

    protected void tick() {
        if (stateU.getGameState() != GameState.IN_GAME) return;
        INSTANCE.getWorldBorder().manageBorder();
    }

}
