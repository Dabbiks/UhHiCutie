package dabbiks.uhc.game.gameplay.recipes;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class RecipeIngredient {

    private String material;
    private String name;
    private List<String> lore;
    private Integer customModelData;
    private Map<String, Object> nbt;

    public Material getMaterial() {
        return Material.valueOf(material.toUpperCase());
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    public String getName() {
        return name;
    }

    public ItemStack buildItem() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (name != null) {
                meta.displayName(Component.text(name));
            }

            if (lore != null && !lore.isEmpty()) {
                meta.lore(lore.stream()
                        .map(Component::text)
                        .toList());
            }

            if (customModelData != null) {
                meta.setCustomModelData(customModelData);
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}
