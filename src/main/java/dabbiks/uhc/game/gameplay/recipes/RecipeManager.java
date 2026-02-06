package dabbiks.uhc.game.gameplay.recipes;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

import static dabbiks.uhc.Main.plugin;

public class RecipeManager implements Listener {

    public final Map<String, UHCRecipe> loadedRecipes = new HashMap<>();
    private final Map<String, Map<Integer, UHCRecipe>> categorizedRecipes = new HashMap<>();

    public void loadRecipes() {
        File folder = new File(plugin.getDataFolder(), "recipes");
        if (!folder.exists()) folder.mkdirs();

        Gson gson = new Gson();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try (Reader reader = new FileReader(file)) {
                UHCRecipe recipe = gson.fromJson(reader, UHCRecipe.class);
                if (recipe == null || recipe.getId() == null || recipe.getResult() == null) continue;

                loadedRecipes.put(recipe.getId(), recipe);
                registerRecipe(recipe);
                categorizeRecipe(recipe);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Błąd podczas ładowania przepisu z pliku: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    private void categorizeRecipe(UHCRecipe recipe) {
        if (recipe.getCategories() == null) return;
        if (!recipe.isShowInRecipeBook()) return;

        for (String category : recipe.getCategories()) {
            categorizedRecipes.putIfAbsent(category, new HashMap<>());
            Map<Integer, UHCRecipe> categoryMap = categorizedRecipes.get(category);
            categoryMap.put(categoryMap.size(), recipe);
        }
    }

    public void registerRecipe(UHCRecipe recipe) {
        NamespacedKey key = new NamespacedKey(plugin, recipe.getId());

        if (recipe.isShaped()) {
            // normalny wariant
            ShapedRecipe shaped = new ShapedRecipe(key, recipe.buildResult());
            shaped.shape(recipe.getShape().toArray(new String[0]));

            for (Map.Entry<Character, RecipeIngredient> entry : recipe.getIngredients().entrySet()) {
                Material mat = entry.getValue().getMaterial();

                if (mat == Material.OAK_PLANKS) {
                    shaped.setIngredient(entry.getKey(), new RecipeChoice.MaterialChoice(
                            Material.OAK_PLANKS,
                            Material.SPRUCE_PLANKS,
                            Material.BIRCH_PLANKS,
                            Material.JUNGLE_PLANKS,
                            Material.ACACIA_PLANKS,
                            Material.DARK_OAK_PLANKS,
                            Material.MANGROVE_PLANKS,
                            Material.CHERRY_PLANKS,
                            Material.BAMBOO_PLANKS
                    ));
                } else {
                    shaped.setIngredient(entry.getKey(), mat);
                }
            }

            Bukkit.addRecipe(shaped);

            // odwrócony wariant
            List<String> reversedShape = new ArrayList<>();
            for (String row : recipe.getShape()) {
                reversedShape.add(new StringBuilder(row).reverse().toString());
            }

            NamespacedKey reversedKey = new NamespacedKey(plugin, recipe.getId() + "_reversed");
            ShapedRecipe reversed = new ShapedRecipe(reversedKey, recipe.buildResult());
            reversed.shape(reversedShape.toArray(new String[0]));

            for (Map.Entry<Character, RecipeIngredient> entry : recipe.getIngredients().entrySet()) {
                Material mat = entry.getValue().getMaterial();

                if (mat == Material.OAK_PLANKS) {
                    reversed.setIngredient(entry.getKey(), new RecipeChoice.MaterialChoice(
                            Material.OAK_PLANKS,
                            Material.SPRUCE_PLANKS,
                            Material.BIRCH_PLANKS,
                            Material.JUNGLE_PLANKS,
                            Material.ACACIA_PLANKS,
                            Material.DARK_OAK_PLANKS,
                            Material.MANGROVE_PLANKS,
                            Material.CHERRY_PLANKS,
                            Material.BAMBOO_PLANKS
                    ));
                } else {
                    reversed.setIngredient(entry.getKey(), mat);
                }
            }

            Bukkit.addRecipe(reversed);

        } else {
            ShapelessRecipe shapeless = new ShapelessRecipe(key, recipe.buildResult());

            for (RecipeIngredient ingredient : recipe.getIngredients().values()) {
                Material mat = ingredient.getMaterial();

                if (mat == Material.OAK_PLANKS) {
                    shapeless.addIngredient(new RecipeChoice.MaterialChoice(
                            Material.OAK_PLANKS,
                            Material.SPRUCE_PLANKS,
                            Material.BIRCH_PLANKS,
                            Material.JUNGLE_PLANKS,
                            Material.ACACIA_PLANKS,
                            Material.DARK_OAK_PLANKS,
                            Material.MANGROVE_PLANKS,
                            Material.CHERRY_PLANKS,
                            Material.BAMBOO_PLANKS
                    ));
                } else {
                    shapeless.addIngredient(mat);
                }
            }

            Bukkit.addRecipe(shapeless);
        }
    }



    public Optional<UHCRecipe> getRecipeById(String id) {
        return Optional.ofNullable(loadedRecipes.get(id));
    }

    public Collection<UHCRecipe> getAllRecipes() {
        return loadedRecipes.values();
    }

    public Map<Integer, UHCRecipe> getRecipesByCategory(String category) {
        return categorizedRecipes.getOrDefault(category, Collections.emptyMap());
    }

    public Set<String> getAvailableCategories() {
        return categorizedRecipes.keySet();
    }
}