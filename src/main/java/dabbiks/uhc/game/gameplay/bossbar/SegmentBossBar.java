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
                mainBossBar.updateTitle("\uE02A §e5: §fEtap I");
                mainBossBar.updateProgress(0.8);
                mainBossBar.updateColor(BarColor.GREEN);
                mainBossBar.show();
            }
            case 2 -> mainBossBar.updateProgress(0.6);
            case 3 -> mainBossBar.updateProgress(0.4);
            case 4 -> mainBossBar.updateProgress(0.2);
            case 5 -> {
                mainBossBar.updateTitle("\uE02A §e10: §fEtap II");
                mainBossBar.updateProgress(1.0);
                mainBossBar.updateColor(BarColor.YELLOW);
            }
            case 6 -> mainBossBar.updateProgress(0.8);
            case 7 -> mainBossBar.updateProgress(0.6);
            case 8 -> mainBossBar.updateProgress(0.4);
            case 9 -> mainBossBar.updateProgress(0.2);
            case 10 -> {
                mainBossBar.updateTitle("\uE02A §e15: §fEtap III");
                mainBossBar.updateProgress(1.0);
                mainBossBar.updateColor(BarColor.YELLOW);
            }
            case 16 -> mainBossBar.updateProgress(0.2);
            case 17 -> mainBossBar.updateProgress(0.4);
            case 18 -> mainBossBar.updateProgress(0.6);
            case 19 -> mainBossBar.updateProgress(0.8);
            case 20 -> {
                mainBossBar.updateTitle("\uE02A §e20: §fEtap IV");
                mainBossBar.updateProgress(1.0);
                mainBossBar.updateColor(BarColor.RED);
            }
        }
    }
}
