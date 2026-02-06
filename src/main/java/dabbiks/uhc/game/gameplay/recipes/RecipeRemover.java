package dabbiks.uhc.game.gameplay.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public class RecipeRemover {

    private static final Set<Material> VANILLA_ITEMS_TO_REMOVE = EnumSet.of(
            // Miecze
            Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
            Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            // Kilofy
            Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
            // Siekiery
            Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
            Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
            // Łopaty
            Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
            Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,
            // Motyki
            Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
            Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE,
            // Zbroje – hełmy
            Material.LEATHER_HELMET, Material.IRON_HELMET, Material.CHAINMAIL_HELMET,
            Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET,
            Material.TURTLE_HELMET,
            // Zbroje – napierśniki
            Material.LEATHER_CHESTPLATE, Material.IRON_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE,
            Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE,
            // Zbroje – getry
            Material.LEATHER_LEGGINGS, Material.IRON_LEGGINGS, Material.CHAINMAIL_LEGGINGS,
            Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS,
            // Zbroje – buty
            Material.LEATHER_BOOTS, Material.IRON_BOOTS, Material.CHAINMAIL_BOOTS,
            Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS,
            // Buzdygany
            Material.MACE
    );

    public static void removeVanillaRecipes() {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (VANILLA_ITEMS_TO_REMOVE.contains(recipe.getResult().getType())) {
                iterator.remove();
            }
        }
        Bukkit.getLogger().info("Usunięto vanilla przepisy dla broni, narzędzi i zbroi.");
    }

}
