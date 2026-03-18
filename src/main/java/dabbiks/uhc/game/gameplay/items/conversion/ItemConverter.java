package dabbiks.uhc.game.gameplay.items.conversion;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantCalculator;
import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeInstance;
import dabbiks.uhc.game.gameplay.items.recipes.loader.RecipeManager;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;
import java.util.function.Function;

public class ItemConverter {

    private final EnchantCalculator enchantCalculator = new EnchantCalculator();

    public ItemStack convert(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return item;

        Boolean isUhcItem = NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.hasTag(ItemTags.UHC_ITEM.name()));
        if (Boolean.TRUE.equals(isUhcItem)) return item;

        String recipeId = item.getType().name().toLowerCase();
        Optional<RecipeInstance> optional = RecipeManager.getRecipeById(recipeId);

        if (optional.isEmpty()) return item;

        RecipeInstance recipe = optional.get();
        ItemInstance itemInstance = recipe.getResult().clone();

        ItemMeta meta = item.getItemMeta();
        int power = enchantCalculator.convertVanillaToPower(meta);

        if (power > 0) {
            itemInstance.setEnchants(enchantCalculator.calculateEnchants(power, itemInstance.getEnchantSlot()));
        }

        return new ItemBuilder(itemInstance).build();
    }
}