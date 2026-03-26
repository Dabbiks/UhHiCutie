package dabbiks.uhc.game.gameplay.stock;

import dabbiks.uhc.menu.StockMenu;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static dabbiks.uhc.Main.stockData;

public class StockInteract implements Listener {

    @EventHandler
    public void onLecternClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block != null && block.getType() == Material.LECTERN) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            PersistentData data = PersistentDataManager.getData(player.getUniqueId());

            new StockMenu(player, data, stockData).open(player);
        }
    }
}