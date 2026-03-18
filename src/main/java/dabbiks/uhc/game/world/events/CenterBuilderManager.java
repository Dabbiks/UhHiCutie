package dabbiks.uhc.game.world.events;

import dabbiks.uhc.game.configs.WorldConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class CenterBuilderManager implements Listener {

    private Integer restrictedY = null;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getBlock().getWorld().getName().equals(WorldConfig.worldName)) return;

        Location loc = event.getBlock().getLocation();
        if (isInsideRestrictedZone(loc)) {
            if (event.getBlockPlaced().getType() != Material.WATER) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getWorld().getName().equals(WorldConfig.worldName)) return;

        Location loc = event.getBlock().getLocation();
        if (isInsideRestrictedZone(loc)) {
            event.setCancelled(true);
        }
    }

    private boolean isInsideRestrictedZone(Location loc) {
        if (loc.getX() < -20 || loc.getX() > 20 || loc.getZ() < -20 || loc.getZ() > 20) {
            return false;
        }

        if (restrictedY == null) {
            restrictedY = calculateRestrictedY(loc.getWorld());
        }

        return loc.getY() >= restrictedY;
    }

    private int calculateRestrictedY(World world) {
        int y = world.getHighestBlockYAt(0, 0);

        while (y > world.getMinHeight()) {
            Block block = world.getBlockAt(0, y, 0);
            Material type = block.getType();
            String typeName = type.name();

            if (type.isAir() || typeName.contains("LEAVES") || typeName.contains("LOG") || typeName.contains("WOOD")) {
                y--;
            } else {
                break;
            }
        }

        return y - 10;
    }
}