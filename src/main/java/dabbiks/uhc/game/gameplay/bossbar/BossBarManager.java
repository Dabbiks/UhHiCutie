package dabbiks.uhc.game.gameplay.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager {

    private final BossBar bossBar;

    public BossBarManager(String title, BarColor color, BarStyle style) {
        this.bossBar = Bukkit.createBossBar(title, color, style);
        this.bossBar.setVisible(false);
    }

    public void show() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }
        bossBar.setVisible(true);
    }

    public void hide() {
        bossBar.removeAll();
        bossBar.setVisible(false);
    }

    public void updateTitle(String newTitle) {
        bossBar.setTitle(newTitle);
    }

    public void updateProgress(double progress) {
        bossBar.setProgress(Math.max(0, Math.min(1, progress)));
    }

    public void updateColor(BarColor color) {
        bossBar.setColor(color);
    }

    public void addPlayer(Player player) {
        if (!bossBar.getPlayers().contains(player)) {
            bossBar.addPlayer(player);
        }
    }

    public void removePlayer(Player player) {
        if (bossBar.getPlayers().contains(player)) {
            bossBar.removePlayer(player);
        }
    }
}