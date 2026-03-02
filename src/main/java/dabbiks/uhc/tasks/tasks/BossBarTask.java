package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.gameplay.bossbar.SegmentBossBar;
import dabbiks.uhc.tasks.Task;

import static dabbiks.uhc.Main.stateU;
import static dabbiks.uhc.Main.timeU;

public class BossBarTask extends Task {

    @Override
    protected long getPeriod() {
        return 20;
    }

    @Override
    protected void tick() {
        SegmentBossBar.processBossBar();
    }
}
