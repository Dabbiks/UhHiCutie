package dabbiks.uhc.cosmetics.chest;

import dabbiks.uhc.menu.ChestMenu;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MysteryChestListener implements Listener {

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.CHEST) return;
        if (!block.getWorld().getName().equals("world")) return;

        MysteryChestSession session = MysteryChestSession.activeSession;
        if (session != null) {
            event.setCancelled(true);
            session.tryOpenChest(event.getPlayer().getUniqueId(), block);
        }
    }

    @EventHandler
    public void onMysteryChestClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.ENDER_CHEST) return;
        if (!block.getWorld().getName().equals("world")) return;

        event.setCancelled(true);
        new ChestMenu(event.getPlayer(), PersistentDataManager.getData(event.getPlayer().getUniqueId())).open(event.getPlayer());
    }
}