package dabbiks.uhc.game.gameplay.elytra;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;

public class ElytraListener implements Listener {

    private final ChestplateManager manager;
    private final int COOLDOWN_TICKS = 200;
    private final double BOOST_MULTIPLIER = 1.5;

    public ElytraListener(ChestplateManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onRocketUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.FIREWORK_ROCKET) return;

        Player player = event.getPlayer();
        if (player.hasCooldown(Material.FIREWORK_ROCKET)) return;

        ItemStack elytra = manager.hasSavedElytra(player.getUniqueId())
                ? manager.getElytra(player.getUniqueId())
                : createCustomElytra();

        if (isElytraBroken(elytra)) {
            return;
        }

        event.setCancelled(true);
        player.setCooldown(Material.FIREWORK_ROCKET, COOLDOWN_TICKS);

        ItemStack currentChest = player.getInventory().getChestplate();
        if (currentChest != null && currentChest.getType() != Material.ELYTRA) {
            manager.saveChestplate(player.getUniqueId(), currentChest.clone());
        }

        player.getInventory().setChestplate(elytra);
        player.setGliding(true);
        player.setVelocity(player.getLocation().getDirection().multiply(BOOST_MULTIPLIER));
    }

    @EventHandler
    public void onLanding(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.isGliding()) return;

        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            manager.saveElytra(player.getUniqueId(), chestplate.clone());
            player.getInventory().setChestplate(null);
        }

        if (manager.hasSavedChestplate(player.getUniqueId())) {
            ItemStack saved = manager.getAndRemoveChestplate(player.getUniqueId());
            player.getInventory().setChestplate(saved);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArmorClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!manager.hasSavedChestplate(player.getUniqueId())) return;

        if (event.getSlotType() == InventoryType.SlotType.ARMOR && event.getSlot() == 38) {
            event.setCancelled(true);
        }

        if (event.getClick() == ClickType.NUMBER_KEY && event.getSlotType() == InventoryType.SlotType.ARMOR && event.getSlot() == 38) {
            event.setCancelled(true);
        }

        if (event.isShiftClick() && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ELYTRA) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (manager.hasSavedChestplate(player.getUniqueId())) {
            ItemStack dropped = event.getItemDrop().getItemStack();
            if (dropped.getType() == Material.ELYTRA) {
                event.setCancelled(true);
            }
        }
    }

    private ItemStack createCustomElytra() {
        ItemInstance instance = new ItemInstance();
        instance.setMaterial(Material.ELYTRA.name());
        instance.setAmount(1);
        instance.setEquipmentSlot(EquipmentSlot.CHEST);

        List<AttributeData> attributes = new ArrayList<>();
        attributes.add(new AttributeData(AttributeType.ARMOR, 3.0));
        attributes.add(new AttributeData(AttributeType.GRAVITY_PERCENT, -25.0));
        instance.setAttributes(attributes);

        return new ItemBuilder(instance).build();
    }

    private boolean isElytraBroken(ItemStack elytra) {
        if (elytra == null || elytra.getType() != Material.ELYTRA) return true;

        if (elytra.getItemMeta() instanceof Damageable damageable) {
            return damageable.getDamage() >= 431;
        }
        return false;
    }
}