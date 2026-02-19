package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.tasks.Task;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static dabbiks.uhc.Main.playerListU;

public class LobbyTeleportTask extends Task {

    Location dest = new Location(Bukkit.getWorld("world"), 0.5, 100.5, 0.5, 0f, -15f);

    @Override
    protected long getPeriod() {
        return 20;
    }

    @Override
    protected void tick() {
        for (Player player : playerListU.getAllPlayers()) {
            Location loc = player.getLocation();
            if (!loc.getWorld().getName().equals("world")) continue;
            if (loc.getY() > 50) continue;
            player.teleport(dest);
        }
    }
}
