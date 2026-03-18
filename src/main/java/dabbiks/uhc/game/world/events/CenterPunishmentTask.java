package dabbiks.uhc.game.world.events;

import dabbiks.uhc.game.configs.WorldConfig;
import dabbiks.uhc.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static dabbiks.uhc.Main.plugin;
import static dabbiks.uhc.Main.stateU;
import static dabbiks.uhc.Main.titleU;

public class CenterPunishmentTask extends BukkitRunnable {

    public CenterPunishmentTask() {
        this.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void run() {
        World world = Bukkit.getWorld(WorldConfig.worldName);
        if (world == null) return;

        double currentBorderSize = world.getWorldBorder().getSize();
        if (currentBorderSize >= 50) return;

        for (Player player : world.getPlayers()) {
            if (stateU.getPlayerState(player) != PlayerState.ALIVE) continue;

            Location loc = player.getLocation();
            if (loc.getX() >= -20 && loc.getX() <= 20 && loc.getZ() >= -20 && loc.getZ() <= 20) {
                int highestBlockY = world.getHighestBlockYAt(loc);

                if (loc.getY() < highestBlockY) {
                    player.damage(2.0);
                    titleU.sendTitleToPlayer(player, "§c§lZAKAZANA STREFA", "§7Nie możesz przebywać pod centrum mapy!", 10);
                }
            }
        }
    }
}