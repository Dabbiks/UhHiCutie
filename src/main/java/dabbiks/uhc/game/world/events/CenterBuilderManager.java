package dabbiks.uhc.game.world.events;

import dabbiks.uhc.game.configs.WorldConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import static dabbiks.uhc.Main.titleU;

public class CenterBuilderManager implements Listener {

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
        return loc.getX() >= -20 && loc.getX() <= 20 && loc.getZ() >= -20 && loc.getZ() <= 20;
    }
}