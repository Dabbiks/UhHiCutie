package dabbiks.uhc.game.gameplay.items.stations.anvil;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemDeconstructor;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.ItemMerger;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
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

import java.util.HashMap;

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

        event.setCancelled(true);

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
            if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
                return;
            }
            player.setItemOnCursor(result);
        }

        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            player.setLevel(player.getLevel() - repairCost);
        }

        inventory.setFirstItem(null);
        inventory.setSecondItem(null);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void prepareAnvil(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getFirstItem();
        ItemStack secondItem = event.getInventory().getSecondItem();

        if (firstItem == null || secondItem == null) return;

        boolean isBook = secondItem.getType() == Material.ENCHANTED_BOOK;
        if (firstItem.getType() != secondItem.getType() && !isBook) return;

        ItemInstance firstInstance = new ItemDeconstructor(firstItem).deconstruct();
        ItemInstance secondInstance = new ItemDeconstructor(secondItem).deconstruct();

        if (!firstInstance.canBeForged() || !secondInstance.canBeForged()) return;

        ItemInstance resultInstance = new ItemMerger(firstInstance, secondInstance).merge();

        if (resultInstance == null) return;
        if (resultInstance.getEnchants() != null && !resultInstance.getEnchants().isEmpty()) resultInstance.setIsEnchanted(true);

        ItemStack result = new ItemBuilder(resultInstance).build();
        event.setResult(result);

        Player player = (Player) event.getView().getPlayer();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        int price = 10;
        if (sessionData.hasTag(SessionTags.SMALL_ANVIL_DISCOUNT)) {
            price = 7;
        } else if (sessionData.hasTag(SessionTags.BIG_ANVIL_DISCOUNT)) {
            price = 5;
        }

        event.getInventory().setRepairCost(price);
        event.getView().setRepairCost(price);
        event.getView().bypassEnchantmentLevelRestriction(true);
    }
}