package dabbiks.uhc.game.gameplay.items.recipes.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RecipeIngredient {

    private String material;
    private Integer customModelData;

    public Material getMaterial() {
        return Material.valueOf(material.toUpperCase());
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    public ItemStack build() {
        if (getMaterial() == null) return new ItemStack(Material.BARRIER);
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta meta = item.getItemMeta();

        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
        return item;
    }

}
