package dabbiks.uhc.game.gameplay.champions.alchemist;

import dabbiks.uhc.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class BrewingManager {

    private final List<BrewingRecipe> recipes = new ArrayList<>();
    private final List<BrewingModifier> modifiers = new ArrayList<>();
    private final List<Predicate<ItemStack>> customIngredients = new ArrayList<>();
    private final Map<Location, BrewingTask> activeTasks = new HashMap<>();

    public void addRecipe(BrewingRecipe recipe) {
        recipes.add(recipe);
    }

    public void addModifier(BrewingModifier modifier) {
        modifiers.add(modifier);
    }

    public void addCustomIngredient(Predicate<ItemStack> condition) {
        customIngredients.add(condition);
    }

    public boolean isCustomIngredient(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        for (Predicate<ItemStack> condition : customIngredients) {
            if (condition.test(item)) return true;
        }
        return false;
    }

    public boolean canBrew(BrewerInventory inventory) {
        ItemStack ingredient = inventory.getIngredient();
        if (ingredient == null || ingredient.getType() == Material.AIR) return false;

        for (int i = 0; i < 3; i++) {
            ItemStack base = inventory.getItem(i);
            if (base != null && base.getType() != Material.AIR) {
                if (getRecipeResult(base, ingredient) != null || getModifierResult(base, ingredient) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public ItemStack getRecipeResult(ItemStack base, ItemStack ingredient) {
        for (BrewingRecipe recipe : recipes) {
            if (recipe.matches(base, ingredient)) return recipe.getResult();
        }
        return null;
    }

    public ItemStack getModifierResult(ItemStack base, ItemStack ingredient) {
        for (BrewingModifier modifier : modifiers) {
            if (modifier.matches(base, ingredient)) return modifier.modify(base, ingredient);
        }
        return null;
    }

    public void checkAndStartBrewing(BrewingStand stand) {
        Location loc = stand.getLocation();
        if (activeTasks.containsKey(loc)) {
            if (!canBrew(stand.getInventory())) {
                activeTasks.get(loc).cancel();
                activeTasks.remove(loc);
                stand.setBrewingTime(0);
                stand.update(true, false);
            }
            return;
        }

        if (canBrew(stand.getInventory())) {
            BrewingTask task = new BrewingTask(this, stand);
            task.runTaskTimer(Main.plugin, 0L, 1L);
            activeTasks.put(loc, task);
        }
    }

    public void completeBrewing(BrewingStand stand) {
        activeTasks.remove(stand.getLocation());
        BrewerInventory inventory = stand.getInventory();
        ItemStack ingredient = inventory.getIngredient();

        if (ingredient == null || ingredient.getType() == Material.AIR) return;

        boolean consumed = false;

        for (int i = 0; i < 3; i++) {
            ItemStack base = inventory.getItem(i);
            if (base == null || base.getType() == Material.AIR) continue;

            ItemStack result = getRecipeResult(base, ingredient);
            if (result != null) {
                inventory.setItem(i, result);
                consumed = true;
                continue;
            }

            result = getModifierResult(base, ingredient);
            if (result != null) {
                inventory.setItem(i, result);
                consumed = true;
            }
        }

        if (consumed) {
            ingredient.setAmount(ingredient.getAmount() - 1);
            if (ingredient.getAmount() <= 0) {
                inventory.setIngredient(null);
            }
        }
    }

    public void removeTask(Location location) {
        activeTasks.remove(location);
    }
}