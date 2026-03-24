package dabbiks.uhc.game.gameplay.items.stations.grindstone;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemDeconstructor;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GrindstoneManager implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void prepareGrindstone(PrepareGrindstoneEvent event) {
        GrindstoneInventory inventory = event.getInventory();
        ItemStack top = inventory.getUpperItem();
        ItemStack bottom = inventory.getLowerItem();

        boolean hasTop = top != null && top.getType() != Material.AIR;
        boolean hasBottom = bottom != null && bottom.getType() != Material.AIR;

        if (hasTop && hasBottom) {
            Material refinedMaterial = getRefinedMaterial(top);
            if (refinedMaterial != null && isPickaxe(bottom)) {
                ItemStack result = new ItemStack(refinedMaterial, top.getAmount());
                event.setResult(result);
                return;
            }
        }

        if (hasBottom) {
            event.setResult(null);
            return;
        }

        if (hasTop) {
            ItemInstance instance = new ItemDeconstructor(top).deconstruct();
            if (instance != null && instance.getEnchants() != null && !instance.getEnchants().isEmpty()) {
                ItemInstance resultInstance = instance.clone();
                resultInstance.setEnchants(null);
                resultInstance.setIsEnchanted(false);
                ItemStack result = new ItemBuilder(resultInstance).build();
                event.setResult(result);
            } else {
                event.setResult(null);
            }
        }
    }

    @EventHandler
    public void onGrindstoneClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof GrindstoneInventory inventory)) return;
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        if (slot == 0 || slot == 1) {
            ItemStack cursor = event.getCursor();
            ItemStack current = event.getCurrentItem();

            boolean isCursorCustom = cursor.getType() == Material.PAPER && getRefinedMaterial(cursor) != null;
            boolean isCurrentCustom = current != null && current.getType() == Material.PAPER && getRefinedMaterial(current) != null;

            if (isCursorCustom || isCurrentCustom) {
                if (slot == 1 && isCursorCustom) {
                    event.setCancelled(true);
                    return;
                }

                event.setCancelled(true);
                ItemStack tempCursor = cursor.clone();
                ItemStack tempCurrent = current != null ? current.clone() : new ItemStack(Material.AIR);

                event.setCurrentItem(tempCursor);
                player.setItemOnCursor(tempCurrent);
                player.updateInventory();
                return;
            }
        }

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && event.getClickedInventory() == player.getInventory()) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && clicked.getType() == Material.PAPER && getRefinedMaterial(clicked) != null) {
                event.setCancelled(true);
                if (inventory.getUpperItem() == null) {
                    inventory.setUpperItem(clicked.clone());
                    event.setCurrentItem(new ItemStack(Material.AIR));
                    player.updateInventory();
                }
                return;
            }
        }

        if (slot == 2) {
            ItemStack result = inventory.getItem(2);
            if (result == null || result.getType() == Material.AIR) return;

            ItemStack top = inventory.getUpperItem();
            ItemStack bottom = inventory.getLowerItem();

            boolean isRefining = top != null && isPickaxe(bottom) && getRefinedMaterial(top) != null;
            boolean isDisenchanting = false;

            if (!isRefining && top != null && (bottom == null || bottom.getType() == Material.AIR)) {
                ItemInstance instance = new ItemDeconstructor(top).deconstruct();
                if (instance != null && instance.getEnchants() != null && !instance.getEnchants().isEmpty()) {
                    isDisenchanting = true;
                }
            }

            event.setCancelled(true);

            if (!isRefining && !isDisenchanting) return;

            if (event.isShiftClick()) {
                ItemStack resultClone = result.clone();
                HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(resultClone);

                if (!leftovers.isEmpty()) {
                    int addedAmount = resultClone.getAmount() - leftovers.get(0).getAmount();
                    if (addedAmount > 0) {
                        ItemStack toRemove = resultClone.clone();
                        toRemove.setAmount(addedAmount);
                        player.getInventory().removeItem(toRemove);
                    }
                    player.sendMessage("§cTwój ekwipunek jest pełny!");
                    return;
                }
            } else {
                event.getCursor();
                if (event.getCursor().getType() != Material.AIR) {
                    return;
                }
                player.setItemOnCursor(result.clone());
            }

            inventory.setUpperItem(null);

            player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1.0f, 1.0f);
        }
    }

    private boolean isPickaxe(ItemStack item) {
        return item != null && item.getType().name().endsWith("_PICKAXE");
    }

    private Material getRefinedMaterial(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) return null;
        if (!item.hasItemMeta() || !item.getItemMeta().hasCustomModelData()) return null;

        int cmd = item.getItemMeta().getCustomModelData();
        return switch (cmd) {
            case 20 -> Material.DIAMOND;
            case 21 -> Material.EMERALD;
            case 22 -> Material.LAPIS_LAZULI;
            case 23 -> Material.REDSTONE;
            default -> null;
        };
    }
}