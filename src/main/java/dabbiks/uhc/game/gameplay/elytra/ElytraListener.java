package dabbiks.uhc.game.gameplay.elytra;

import dabbiks.uhc.Main;
import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static dabbiks.uhc.Main.symbolU;

public class ElytraListener implements Listener {

    private final ChestplateManager manager;
    private final int COOLDOWN_TICKS = 10;
    private final double BOOST_MULTIPLIER = 1.5;

    public ElytraListener(ChestplateManager manager) {
        this.manager = manager;
    }

    private boolean isPersonalFirework(ItemStack item) {
        if (item == null || item.getType() != Material.FIREWORK_ROCKET) return false;
        return Boolean.TRUE.equals(NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.hasTag(ItemTags.PERSONAL.name())));
    }

    @EventHandler
    public void onRocketUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (!isPersonalFirework(item)) return;

        Player player = event.getPlayer();
        if (player.hasCooldown(Material.FIREWORK_ROCKET)) return;

        ItemStack elytra = manager.hasSavedElytra(player.getUniqueId())
                ? manager.getElytra(player.getUniqueId())
                : createCustomElytra();

        if (isElytraBroken(elytra)) {
            return;
        }

        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        if (!sessionData.consumeElytraCharge()) {
            event.setCancelled(true);
            displayCharges(player, sessionData);
            return;
        }

        event.setCancelled(true);
        player.setCooldown(Material.FIREWORK_ROCKET, COOLDOWN_TICKS);

        displayCharges(player, sessionData);

        Vector direction = player.getLocation().getDirection();
        direction.multiply(1.8);
        player.setVelocity(direction);

        Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
            ItemStack currentChest = player.getInventory().getChestplate();
            if (currentChest != null && currentChest.getType() != Material.ELYTRA) {
                manager.saveChestplate(player.getUniqueId(), currentChest.clone());
            }

            player.getInventory().setChestplate(elytra);
            player.setGliding(true);
            player.setVelocity(player.getLocation().getDirection().multiply(BOOST_MULTIPLIER));
        }, 2L);
    }

    private void displayCharges(Player player, SessionData sessionData) {
        int charges = sessionData.getElytraCharges();
        int max = sessionData.getMaxElytraCharges();

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < charges; i++) {
            bar.append(symbolU.FUEL);
        }
        for (int i = charges; i < max; i++) {
            bar.append(symbolU.NO_FUEL);
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(bar.toString()));
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

        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            ItemStack current = event.getCurrentItem();
            if (current != null && current.getType() == Material.ELYTRA) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArmorSwapInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (!manager.hasSavedChestplate(player.getUniqueId())) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        String name = item.getType().name();
        if (name.endsWith("_CHESTPLATE") || name.equals("ELYTRA")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFireworkInventoryClick(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        boolean isCurrentFirework = isPersonalFirework(current);
        boolean isCursorFirework = isPersonalFirework(cursor);

        if (event.getClick() == ClickType.NUMBER_KEY) {
            ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            if (isPersonalFirework(hotbarItem)) {
                if (event.getClickedInventory() != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
                    event.setCancelled(true);
                }
            }
        }

        if (!isCurrentFirework && !isCursorFirework) return;

        if (event.isShiftClick() && isCurrentFirework) {
            if (event.getView().getTopInventory().getType() != InventoryType.CRAFTING) {
                event.setCancelled(true);
                return;
            }
        }

        if (isCursorFirework && event.getClickedInventory() != null) {
            if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFireworkDrag(InventoryDragEvent event) {
        if (!isPersonalFirework(event.getOldCursor())) return;

        for (int slot : event.getRawSlots()) {
            if (slot < event.getView().getTopInventory().getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack dropped = event.getItemDrop().getItemStack();

        if (isPersonalFirework(dropped)) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        if (manager.hasSavedChestplate(player.getUniqueId())) {
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
        attributes.add(new AttributeData(AttributeType.GRAVITY_PERCENT, -10.0));
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