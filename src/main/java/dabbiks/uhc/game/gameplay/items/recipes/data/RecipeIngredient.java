package dabbiks.uhc.game.gameplay.items.recipes.data;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RecipeIngredient {

    private String material;
    private Integer customModelData;
    private String name;

    public Material getMaterial() {
        try {
            return Material.valueOf(material.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Material.BARRIER;
        }
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    public String getName() {
        return name;
    }

    public List<Material> getValidMaterials() {
        Material base = getMaterial();
        List<Material> list = new ArrayList<>();

        if (Tag.PLANKS.isTagged(base)) {
            list.addAll(Tag.PLANKS.getValues());
        } else if (Tag.WOOL.isTagged(base)) {
            list.addAll(Tag.WOOL.getValues());
        } else if (Tag.LOGS.isTagged(base)) {
            list.addAll(Tag.LOGS.getValues());
        } else if (base == Material.COBBLESTONE || base == Material.COBBLED_DEEPSLATE) {
            list.add(Material.COBBLESTONE);
            list.add(Material.COBBLED_DEEPSLATE);
        } else if (base == Material.COAL || base == Material.CHARCOAL) {
            list.add(Material.COAL);
            list.add(Material.CHARCOAL);
        } else {
            list.add(base);
        }

        return list;
    }

    public ItemStack build(Material mat) {
        if (mat == null) mat = Material.BARRIER;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (customModelData != null) {
                meta.setCustomModelData(customModelData);
            }
            if (name != null && !name.isEmpty()) {
                meta.setDisplayName("§f" + name);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack build() {
        return build(getValidMaterials().get(0));
    }

}