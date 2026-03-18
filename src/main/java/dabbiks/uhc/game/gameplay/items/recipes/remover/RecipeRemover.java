package dabbiks.uhc.game.gameplay.items.recipes.remover;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RecipeRemover {

    private final Set<Material> VANILLA_ITEMS_TO_REMOVE = EnumSet.of(
            Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
            Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            Material.COPPER_SWORD,

            Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
            Material.COPPER_PICKAXE,

            Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
            Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
            Material.COPPER_AXE,

            Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
            Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,
            Material.COPPER_SHOVEL,

            Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
            Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE,
            Material.COPPER_HOE,

            Material.LEATHER_HELMET, Material.IRON_HELMET, Material.CHAINMAIL_HELMET,
            Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET,
            Material.TURTLE_HELMET, Material.COPPER_HELMET,

            Material.LEATHER_CHESTPLATE, Material.IRON_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE,
            Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE,
            Material.COPPER_CHESTPLATE,

            Material.LEATHER_LEGGINGS, Material.IRON_LEGGINGS, Material.CHAINMAIL_LEGGINGS,
            Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS,
            Material.COPPER_LEGGINGS,

            Material.LEATHER_BOOTS, Material.IRON_BOOTS, Material.CHAINMAIL_BOOTS,
            Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS,
            Material.COPPER_BOOTS,

            Material.WOODEN_SPEAR, Material.STONE_SPEAR, Material.IRON_SPEAR,
            Material.GOLDEN_SPEAR, Material.DIAMOND_SPEAR, Material.NETHERITE_SPEAR,
            Material.COPPER_SPEAR,

            Material.FISHING_ROD, Material.BOW, Material.CROSSBOW, Material.MACE, Material.SHIELD
    );

    public void removeVanillaRecipes() {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();

            if (!VANILLA_ITEMS_TO_REMOVE.contains(recipe.getResult().getType())) {
                continue;
            }

            if (recipe instanceof Keyed keyed) {
                String namespace = keyed.getKey().getNamespace();

                if (!namespace.equals("minecraft")) {
                    continue;
                }
            }

            iterator.remove();
        }
        Bukkit.getLogger().info("Vanilla recipes removed");
    }

    public List<NamespacedKey> getRemovedRecipeKeys() {
        List<NamespacedKey> keys = new ArrayList<>();
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (VANILLA_ITEMS_TO_REMOVE.contains(recipe.getResult().getType())) {
                if (recipe instanceof Keyed keyed) {
                    if (keyed.getKey().getNamespace().equals("minecraft")) {
                        keys.add(keyed.getKey());
                    }
                }
            }
        }
        return keys;
    }
}