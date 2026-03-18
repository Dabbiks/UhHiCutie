package dabbiks.uhc.tasks.tasks;


import dabbiks.uhc.tasks.Task;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static dabbiks.uhc.Main.*;

public class HeartbeatTask extends Task {

    protected long getPeriod() {
        return 20;
    }

    protected void tick() {
        final double LOW_HEALTH_THRESHOLD = 0.10;
        final double MID_HEALTH_THRESHOLD = 0.30;

        for (Player player : playerListU.getPlayingPlayers()) {
            double maxHealth = player.getMaxHealth();
            double currentHealth = player.getHealth();
            double healthPercentage = currentHealth / maxHealth;

            if (healthPercentage > LOW_HEALTH_THRESHOLD && healthPercentage < MID_HEALTH_THRESHOLD) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.3F, 0.2F);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.3F, 0.2F);
                    }
                }.runTaskLater(plugin, 5);
            }

            if (healthPercentage <= LOW_HEALTH_THRESHOLD) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.4F, 0.4F);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.4F, 0.3F);
                    }
                }.runTaskLater(plugin, 4);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.4F, 0.4F);
                    }
                }.runTaskLater(plugin, 10);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.4F, 0.3F);
                    }
                }.runTaskLater(plugin, 14);
            }
        }
    }
}