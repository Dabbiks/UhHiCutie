package dabbiks.uhc.game.gameplay.damage.listeners;

import dabbiks.uhc.tasks.TaskManager;
import dabbiks.uhc.tasks.tasks.BoomerangFlightTask;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BoomerangLaunch implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.WOODEN_SWORD || !event.getAction().name().contains("RIGHT")) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData() || meta.getCustomModelData() <= 20000) return;

        ItemStack thrownItem = item.clone();
        thrownItem.setAmount(1);
        item.setAmount(item.getAmount() - 1);

        ItemDisplay display = player.getWorld().spawn(player.getEyeLocation(), ItemDisplay.class, entity -> {
            entity.setItemStack(thrownItem);
            entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
            entity.setInterpolationDuration(1);
            entity.setTeleportDuration(1);
        });

        TaskManager.addTask(new BoomerangFlightTask(player, display, thrownItem));
    }
}