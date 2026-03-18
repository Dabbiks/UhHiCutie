package dabbiks.uhc.game.gameplay.damage.listeners;

import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantSlot;
import dabbiks.uhc.tasks.TaskManager;
import dabbiks.uhc.tasks.tasks.BoomerangFlightTask;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Function;

public class BoomerangLaunch implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() == Material.AIR || !event.getAction().name().contains("RIGHT")) return;

        String enchantSlotString = NBT.get(item, (Function<ReadableItemNBT, String>) nbt -> nbt.getString("ENCHANT_SLOT"));
        if (enchantSlotString == null || !enchantSlotString.equals(EnchantSlot.BOOMERANG.name())) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null && clickedBlock.getType().isInteractable() && !player.isSneaking()) {
            return;
        }

        event.setCancelled(true);
        if (player.hasCooldown(item.getType())) return;
        player.setCooldown(item.getType(), 10);

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