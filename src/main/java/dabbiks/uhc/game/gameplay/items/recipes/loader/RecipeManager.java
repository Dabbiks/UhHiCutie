package dabbiks.uhc.game.gameplay.items.recipes.loader;

import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeIngredient;
import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeInstance;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

import static dabbiks.uhc.Main.plugin;

public class RecipeManager {

    private static final Map<String, RecipeInstance> recipes = new HashMap<>();
    private final Map<String, List<RecipeType>> recipeCategories = new HashMap<>();

    private void categorizeRecipe(RecipeInstance recipe) {
        if (recipe.getCategories() == null) return;
        List<RecipeType> categories = new ArrayList<>();
        for (String category : recipe.getCategories()) {
            categories.add(RecipeType.valueOf(category));
        }
        recipeCategories.putIfAbsent(recipe.getId(), categories);
    }

    public void registerRecipe(RecipeInstance recipe) {
        ItemStack result = recipe.buildResult();
        recipes.putIfAbsent(recipe.getId(), recipe);

        registerBase(recipe, result, recipe.getId(), recipe.getShape());
        categorizeRecipe(recipe);

        if (recipe.isShaped()) {
            List<String> reversedShape = recipe.getShape().stream()
                    .map(row -> new StringBuilder(row).reverse().toString())
                    .toList();

            if (!recipe.getShape().equals(reversedShape)) {
                RecipeInstance reversedRecipe = new RecipeInstance();
                reversedRecipe.setId(recipe.getId() + "_reversed");
                reversedRecipe.setType(recipe.getType());
                reversedRecipe.setShape(reversedShape);
                reversedRecipe.setIngredients(recipe.getIngredients());
                reversedRecipe.setResult(recipe.getResult());
                reversedRecipe.setMaxCraftsPerPlayer(recipe.getMaxCraftsPerPlayer());
                reversedRecipe.setCategories(recipe.getCategories());
                reversedRecipe.setShowInRecipeBook(recipe.showInRecipeBook());

                recipes.putIfAbsent(reversedRecipe.getId(), reversedRecipe);
                registerBase(reversedRecipe, result, reversedRecipe.getId(), reversedShape);
                categorizeRecipe(reversedRecipe);
            }
        }
    }

    private void registerBase(RecipeInstance recipe, ItemStack result, String id, List<String> shape) {
        NamespacedKey key = new NamespacedKey(plugin, id);

        if (recipe.isShaped()) {
            ShapedRecipe shaped = new ShapedRecipe(key, result);
            shaped.shape(shape.toArray(new String[0]));

            for (Map.Entry<Character, RecipeIngredient> entry : recipe.getIngredients().entrySet()) {
                shaped.setIngredient(entry.getKey(), getRecipeChoice(entry.getValue().getMaterial()));
            }
            Bukkit.addRecipe(shaped);
        } else {
            ShapelessRecipe shapeless = new ShapelessRecipe(key, result);
            for (RecipeIngredient ingredient : recipe.getIngredients().values()) {
                shapeless.addIngredient(getRecipeChoice(ingredient.getMaterial()));
            }
            Bukkit.addRecipe(shapeless);
        }
    }

    private RecipeChoice getRecipeChoice(Material material) {
        if (Tag.PLANKS.isTagged(material)) {
            return new RecipeChoice.MaterialChoice(new ArrayList<>(Tag.PLANKS.getValues()));
        }
        if (Tag.WOOL.isTagged(material)) {
            return new RecipeChoice.MaterialChoice(new ArrayList<>(Tag.WOOL.getValues()));
        }
        if (Tag.LOGS.isTagged(material)) {
            return new RecipeChoice.MaterialChoice(new ArrayList<>(Tag.LOGS.getValues()));
        }
        if (material == Material.COBBLESTONE || material == Material.COBBLED_DEEPSLATE) {
            return new RecipeChoice.MaterialChoice(Material.COBBLESTONE, Material.COBBLED_DEEPSLATE);
        }
        if (material == Material.COAL || material == Material.CHARCOAL) {
            return new RecipeChoice.MaterialChoice(Material.COAL, Material.CHARCOAL);
        }

        return new RecipeChoice.MaterialChoice(material);
    }

    public static Optional<RecipeInstance> getRecipeById(String id) {
        return Optional.ofNullable(recipes.get(id));
    }

    public Collection<RecipeInstance> getRecipes() {
        return recipes.values();
    }

    public List<RecipeInstance> getRecipesFromCategory(RecipeType type) {
        List<RecipeInstance> foundRecipes = new ArrayList<>();
        for (Map.Entry<String, List<RecipeType>> entry : recipeCategories.entrySet()) {
            if (!entry.getValue().contains(type)) continue;
            getRecipeById(entry.getKey()).ifPresent(foundRecipes::add);
        }
        return foundRecipes;
    }
}