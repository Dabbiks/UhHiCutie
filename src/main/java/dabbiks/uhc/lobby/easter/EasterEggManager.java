package dabbiks.uhc.lobby.easter;

import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Random;

import static dabbiks.uhc.Main.symbolU;

public class EasterEggManager implements Listener {

    private static final Random random = new Random();
    private static Location currentEggLocation = null;

    public static void spawnRandomEgg() {
        List<Location> locations = EasterLocationData.getLocations();
        if (locations.isEmpty()) return;

        if (currentEggLocation != null && currentEggLocation.getBlock().getType() == Material.SNIFFER_EGG) {
            currentEggLocation.getBlock().setType(Material.AIR);
        }

        Location newLoc = locations.get(random.nextInt(locations.size()));
        while (locations.size() > 1 && newLoc.equals(currentEggLocation)) {
            newLoc = locations.get(random.nextInt(locations.size()));
        }

        currentEggLocation = newLoc;

        if (currentEggLocation.getWorld() != null) {
            currentEggLocation.getBlock().setType(Material.SNIFFER_EGG);
        }
    }

    public static void clearAllEggs() {
        for (Location loc : EasterLocationData.getLocations()) {
            if (loc.getWorld() != null && loc.getBlock().getType() == Material.SNIFFER_EGG) {
                loc.getBlock().setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && block.getType() == Material.SNIFFER_EGG) {
            if (event.getAction() == Action.PHYSICAL) {
                event.setCancelled(true);
                return;
            }

            if (block.getWorld().getName().equals("world")) {
                event.setCancelled(true);
                Player player = event.getPlayer();

                block.setType(Material.AIR);
                block.getWorld().spawnParticle(Particle.CLOUD, block.getLocation().add(0.5, 0.5, 0.5), 20, 0.3, 0.3, 0.3, 0.05);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);

                PersistentData data = PersistentDataManager.getData(player.getUniqueId());
                data.addStats(PersistentStats.POWDER, 100);
                player.sendMessage("§f100 " + symbolU.SCOREBOARD_POWDER);

                spawnRandomEgg();
            }
        }
    }

    @EventHandler
    public void onGrow(BlockGrowEvent event) {
        if (event.getBlock().getType() == Material.SNIFFER_EGG || event.getNewState().getType() == Material.SNIFFER_EGG) {
            event.setCancelled(true);
        }
    }
}