package dabbiks.uhc.game.gameplay.bossbar;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.configs.SegmentConfig;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import static dabbiks.uhc.Main.stateU;

public class SegmentBossBar {

    public static BossBarManager mainBossBar = new BossBarManager("", BarColor.RED, BarStyle.SOLID);

    public static void processBossBar() {
        if (stateU.getGameState() != GameState.IN_GAME) return;
        switch (SegmentConfig.actualSegment) {
            case 1 -> {
                mainBossBar.updateTitle("§fEtap I");
                mainBossBar.updateProgress(1);
                mainBossBar.updateColor(BarColor.GREEN);
                mainBossBar.show();
            }
            case 10 -> {
                mainBossBar.updateTitle("§fEtap II");
                mainBossBar.updateColor(BarColor.YELLOW);
            }
            case 15 -> {
                mainBossBar.updateTitle("§fEtap III");
                mainBossBar.updateColor(BarColor.RED);
            }
            case 20 -> {
                mainBossBar.updateTitle("§fEtap IV");
                mainBossBar.updateProgress(1.0);
                mainBossBar.updateColor(BarColor.RED);
            }
        }
    }
}
