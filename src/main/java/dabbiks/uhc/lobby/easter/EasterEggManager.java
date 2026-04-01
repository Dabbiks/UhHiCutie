package dabbiks.uhc.lobby.easter;

import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.persistent.PersistentStats;
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

import static dabbiks.uhc.Main.symbolU;

public class EasterEggManager implements Listener {

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
                data.addStats(PersistentStats.POWDER, 25);
                player.sendMessage("§a+ 25 " + symbolU.SCOREBOARD_POWDER);
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