package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.tasks.Task;

import static dabbiks.uhc.Main.stateU;
import static dabbiks.uhc.Main.timeU;

public class TimeTask extends Task {

    @Override
    protected long getPeriod() {
        return 20;
    }

    @Override
    protected void tick() {
        if (stateU.getGameState() != GameState.IN_GAME) return;
        timeU.incrementTime();
    }

}
