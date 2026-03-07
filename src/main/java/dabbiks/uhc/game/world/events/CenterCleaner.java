package dabbiks.uhc.game.world.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import static dabbiks.uhc.Main.plugin;

public class CenterCleaner {

    private final World world;
    private final int maxY = 319;
    private int currentY = -64;

    public CenterCleaner(World world) {
        this.world = world;
        startCleaningTask();
        startDamageAndParticleTask();
    }

    private void startCleaningTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentY > maxY) {
                    this.cancel();
                    return;
                }

                int targetY = Math.min(currentY + 4, maxY);

                for (int y = currentY; y <= targetY; y++) {
                    for (int x = -45; x <= 45; x++) {
                        for (int z = -45; z <= 45; z++) {
                            world.getBlockAt(x, y, z).setType(Material.AIR, false);
                        }
                    }
                }

                Location center = new Location(world, 0, targetY, 0);
                world.playSound(center, Sound.BLOCK_STONE_BREAK, 0.15f, 0.5f);

                currentY = targetY + 1;
            }
        }.runTaskTimer(plugin, 0L, 100L);
    }

    private void startDamageAndParticleTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentY > maxY) {
                    this.cancel();
                    return;
                }

                for (Player player : world.getPlayers()) {
                    Location loc = player.getLocation();
                    if (loc.getX() >= -45 && loc.getX() <= 45 && loc.getZ() >= -45 && loc.getZ() <= 45) {
                        if (loc.getY() < currentY) {
                            player.damage(6.0);
                        }
                    }
                }

                double particleY = currentY + 0.1;
                for (int x = -45; x <= 45; x += 4) {
                    for (int z = -45; z <= 45; z += 4) {
                        world.spawnParticle(Particle.ASH, x + 0.5, particleY, z + 0.5, 1, 0, 0, 0, 0);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}