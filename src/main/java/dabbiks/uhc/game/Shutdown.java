package dabbiks.uhc.game;

import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static dabbiks.uhc.Main.*;
import static dabbiks.uhc.game.gameplay.bossbar.SegmentBossBar.mainBossBar;

public class Shutdown {

    public static void shutdownServer() {
        new BukkitRunnable() {
            int counter = 20;
            @Override
            public void run() {
                counter--;
                if (counter == 5) {
                    for (Player player : playerListU.getAllPlayers()) {
                        PersistentDataManager.saveData(player.getUniqueId());
                        mainBossBar.hide();
                    }
                }
                if (counter == 0) {
                    Bukkit.restart();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

}
