package dabbiks.uhc.game.gameplay.damage.listeners;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ParryingBlocker implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.OFF_HAND) {
            return;
        }

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getPlayer().getInventory().getItemInOffHand();

            if (item.hasData(DataComponentTypes.BLOCKS_ATTACKS)) {
                event.setCancelled(true);
            }
        }
    }

}
