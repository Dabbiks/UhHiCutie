package dabbiks.uhc.game.gameplay.items.stations.anvil;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemDeconstructor;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.ItemMerger;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class AnvilManager implements Listener {

    @EventHandler
    public void onAnvilTakeItem(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory inventory)) return;
        if (event.getRawSlot() != 2) return;

        ItemStack result = inventory.getResult();
        if (result == null || result.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        int repairCost = inventory.getRepairCost();

        if (player.getLevel() < repairCost && player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
        }

        if (event.isShiftClick()) {
            event.setCancelled(true);
            return;
        }

        if (event.getCursor().getType() != Material.AIR) {
            event.setCancelled(true);
            return;
        }

        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            player.setLevel(player.getLevel() - repairCost);
        }

        event.setCancelled(true);
        player.setItemOnCursor(result);
        inventory.setFirstItem(null);
        inventory.setSecondItem(null);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void prepareAnvil(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getFirstItem();
        ItemStack secondItem = event.getInventory().getSecondItem();

        if (firstItem == null || secondItem == null) return;

        boolean isBook = secondItem.getType() == Material.ENCHANTED_BOOK || secondItem.getType() == Material.BOOK;
        if (firstItem.getType() != secondItem.getType() && !isBook) return;

        ItemInstance firstInstance = new ItemDeconstructor(firstItem).deconstruct();
        ItemInstance secondInstance = new ItemDeconstructor(secondItem).deconstruct();
        if (!firstInstance.canBeForged() || !secondInstance.canBeForged()) return;
        ItemInstance resultInstance = new ItemMerger(firstInstance, secondInstance).merge();

        if (resultInstance == null) return;

        ItemStack result = new ItemBuilder(resultInstance).build();
        event.setResult(result);
        event.getInventory().setRepairCost(10);
        event.getView().setRepairCost(10);
        event.getView().bypassEnchantmentLevelRestriction(true);
    }
}