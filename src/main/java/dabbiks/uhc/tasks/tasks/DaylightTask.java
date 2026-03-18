package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.configs.WorldConfig;
import dabbiks.uhc.tasks.Task;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class DaylightTask extends Task {

    protected long getPeriod() {
        return 2;
    }

    protected void tick() {
        World world = Bukkit.getWorld(WorldConfig.worldName);
        if (world == null) {
            return;
        }

        if (world.getTime() < 23996) {
            world.setTime(world.getTime() + 4);
            return;
        }

        world.setTime(0);
    }

}