package dabbiks.uhc.game.gameplay.damage.listeners;

import dabbiks.uhc.tasks.TaskManager;
import dabbiks.uhc.tasks.tasks.BoomerangFlightTask;
import org.bukkit.Material;
import org.bukkit.Sound;
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
        if (meta == null || !meta.hasCustomModelData() || meta.getCustomModelData() != 11) return;

        event.setCancelled(true);
        if (player.hasCooldown(Material.WOODEN_SWORD)) return;
        player.setCooldown(Material.WOODEN_SWORD, 10);

        ItemStack thrownItem = item.clone();
        thrownItem.setAmount(1);
        item.setAmount(item.getAmount() - 1);

        player.playSound(player.getLocation(), Sound.ENTITY_WIND_CHARGE_THROW, 0.6f, 1f);

        ItemDisplay display = player.getWorld().spawn(player.getEyeLocation(), ItemDisplay.class, entity -> {
            entity.setItemStack(thrownItem);
            entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
        });

        TaskManager.addTask(new BoomerangFlightTask(player, display, thrownItem));
    }
}