package dabbiks.uhc.game.gameplay.items.conversion;

import dabbiks.uhc.game.gameplay.items.recipes.loader.RecipeManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ConversionManager implements Listener {

    private final ItemConverter itemConverter = new ItemConverter();

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        ItemStack item = event.getItem().getItemStack();

        ItemStack convertedItem = itemConverter.convert(item);
        if (item.equals(convertedItem)) return;
        event.getItem().setItemStack(convertedItem);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            ItemStack converted = itemConverter.convert(item);

            if (item != null && !item.equals(converted)) {
                inventory.setItem(i, converted);
            }
        }
    }


}
