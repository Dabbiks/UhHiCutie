package dabbiks.uhc.game.gameplay.champions.alchemist;

import dabbiks.uhc.Main;
import org.bukkit.Bukkit;
import org.bukkit.block.BrewingStand;
import org.bukkit.scheduler.BukkitRunnable;

public class BrewingTask extends BukkitRunnable {

    private final BrewingManager manager;
    private final BrewingStand stand;
    private int time = 400;

    public BrewingTask(BrewingManager manager, BrewingStand stand) {
        this.manager = manager;
        this.stand = stand;
    }

    @Override
    public void run() {
        if (!stand.getChunk().isLoaded() || !manager.canBrew(stand.getInventory())) {
            stand.setBrewingTime(0);
            stand.update(true, false);
            manager.removeTask(stand.getLocation());
            this.cancel();
            return;
        }

        time--;
        stand.setBrewingTime(time);
        stand.update(true, false);

        if (time <= 0) {
            manager.completeBrewing(stand);
            stand.setBrewingTime(0);
            stand.update(true, false);
            this.cancel();

            Bukkit.getScheduler().runTaskLater(Main.plugin, () -> manager.checkAndStartBrewing(stand), 1L);
        }
    }
}