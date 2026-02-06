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
                mainBossBar.updateTitle("\uE02A §e10: §fZmniejszanie granicy");
                mainBossBar.updateProgress(0.1);
                mainBossBar.updateColor(BarColor.GREEN);
                mainBossBar.show();
            }
            case 2 -> mainBossBar.updateProgress(0.2);
            case 3 -> mainBossBar.updateProgress(0.3);
            case 4 -> mainBossBar.updateProgress(0.4);
            case 5 -> mainBossBar.updateProgress(0.5);
            case 6 -> mainBossBar.updateProgress(0.6);
            case 7 -> mainBossBar.updateProgress(0.7);
            case 8 -> mainBossBar.updateProgress(0.8);
            case 9 -> mainBossBar.updateProgress(0.9);
            case 10 -> {
                mainBossBar.updateTitle("\uE02A §e15: §fKoniec ochrony");
                mainBossBar.updateProgress(0.0);
                mainBossBar.updateColor(BarColor.YELLOW);
            }
            case 11 -> mainBossBar.updateProgress(0.2);
            case 12 -> mainBossBar.updateProgress(0.4);
            case 13 -> mainBossBar.updateProgress(0.6);
            case 14 -> mainBossBar.updateProgress(0.8);
            case 15 -> {
                mainBossBar.updateTitle("\uE02A §e20: §fZatrzymanie granicy");
                mainBossBar.updateProgress(0.0);
                mainBossBar.updateColor(BarColor.YELLOW);
            }
            case 16 -> mainBossBar.updateProgress(0.2);
            case 17 -> mainBossBar.updateProgress(0.4);
            case 18 -> mainBossBar.updateProgress(0.6);
            case 19 -> mainBossBar.updateProgress(0.8);
            case 20 -> {
                mainBossBar.updateTitle("\uE02A §e25: §fRadar graczy");
                mainBossBar.updateProgress(0.0);
                mainBossBar.updateColor(BarColor.YELLOW);
            }
            case 21 -> mainBossBar.updateProgress(0.2);
            case 22 -> mainBossBar.updateProgress(0.4);
            case 23 -> mainBossBar.updateProgress(0.6);
            case 24 -> mainBossBar.updateProgress(0.8);
            case 25 -> {
                mainBossBar.updateTitle("\uE02A §e30: §fWyłączenie dropu i zmniejszanie granicy");
                mainBossBar.updateProgress(0.0);
                mainBossBar.updateColor(BarColor.RED);
            }
            case 26 -> mainBossBar.updateProgress(0.2);
            case 27 -> mainBossBar.updateProgress(0.4);
            case 28 -> mainBossBar.updateProgress(0.6);
            case 29 -> mainBossBar.updateProgress(0.8);
            case 30 -> {
                mainBossBar.updateTitle("\uE02A §e32: §fNadejście nadzorcy (Wylaczone)");
                mainBossBar.updateProgress(0.0);
                mainBossBar.updateColor(BarColor.RED);
            }
            case 31 -> mainBossBar.updateProgress(0.5);
            case 32 -> {
                mainBossBar.updateTitle("\uE02A §e45: §fZamknięcie granicy");
                mainBossBar.updateProgress(0.0);
                mainBossBar.updateColor(BarColor.RED);
            }
            case 33 -> mainBossBar.updateProgress(0.2);
            case 34 -> mainBossBar.updateProgress(0.4);
            case 35 -> mainBossBar.updateProgress(0.6);
            case 36 -> mainBossBar.updateProgress(0.8);
            case 37 -> mainBossBar.updateProgress(0.8);
            case 38 -> mainBossBar.updateProgress(0.8);
            case 39 -> mainBossBar.updateProgress(0.8);
            case 40 -> {
                mainBossBar.updateTitle("§fShowdown");
                mainBossBar.updateProgress(1);
                mainBossBar.updateColor(BarColor.RED);
            }
        }
    }
}
