package dabbiks.uhc.game.gameplay.items.data.attributes;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static dabbiks.uhc.Main.plugin;

public class EquipmentChangeListener implements Listener {

    private void updateAttribute(Player player, Attribute bukkitAttribute, String nbtKey) {
        EntityEquipment equipment = player.getEquipment();

        double flatValue = 0.0;
        double percentValue = 0.0;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item;
            switch (slot) {
                case HAND: item = equipment.getItemInMainHand(); break;
                case OFF_HAND: item = equipment.getItemInOffHand(); break;
                case HEAD: item = equipment.getHelmet(); break;
                case CHEST: item = equipment.getChestplate(); break;
                case LEGS: item = equipment.getLeggings(); break;
                case FEET: item = equipment.getBoots(); break;
                default: continue;
            }

            if (item == null || item.getType().isAir()) continue;

            NBTItem nbt = new NBTItem(item);
            if (!nbt.hasKey(nbtKey)) continue;
            if (nbt.hasKey("SLOT") && !nbt.getString("SLOT").equals(slot.name())) continue;

            double value = nbt.getDouble(nbtKey);
            boolean isPercent = nbt.hasKey(nbtKey + "_PERCENT") && nbt.getBoolean(nbtKey + "_PERCENT");

            if (isPercent) {
                percentValue += value;
            } else {
                flatValue += value;
            }
        }

        AttributeInstance instance = player.getAttribute(bukkitAttribute);
        if (instance == null) return;

        UUID flatUuid = UUID.nameUUIDFromBytes((nbtKey + "_FLAT").getBytes());
        UUID percentUuid = UUID.nameUUIDFromBytes((nbtKey + "_PERCENT").getBytes());

        if (instance.getModifier(flatUuid) != null) instance.removeModifier(flatUuid);
        if (instance.getModifier(percentUuid) != null) instance.removeModifier(percentUuid);

        if (flatValue != 0.0) {
            instance.addModifier(new AttributeModifier(flatUuid, nbtKey + "_FLAT", flatValue, AttributeModifier.Operation.ADD_NUMBER));
        }
        if (percentValue != 0.0) {
            instance.addModifier(new AttributeModifier(percentUuid, nbtKey + "_PERCENT", percentValue, AttributeModifier.Operation.ADD_SCALAR));
        }
    }

    private void scheduleUpdate(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                updateAttribute(player, Attribute.MAX_HEALTH, "Zdrowie");
            }
        }, 1L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            scheduleUpdate(player);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            scheduleUpdate(player);
        }
    }

    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event) {
        scheduleUpdate(event.getPlayer());
    }

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        scheduleUpdate(event.getPlayer());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        scheduleUpdate(event.getPlayer());
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        scheduleUpdate(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        scheduleUpdate(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        scheduleUpdate(event.getPlayer());
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            scheduleUpdate(player);
        }
    }
}