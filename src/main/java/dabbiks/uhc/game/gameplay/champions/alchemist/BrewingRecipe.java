package dabbiks.uhc.game.gameplay.champions.alchemist;

import org.bukkit.inventory.ItemStack;
import java.util.function.Predicate;

public class BrewingRecipe {

    private final Predicate<ItemStack> baseCondition;
    private final Predicate<ItemStack> ingredientCondition;
    private final ItemStack result;

    public BrewingRecipe(Predicate<ItemStack> baseCondition, Predicate<ItemStack> ingredientCondition, ItemStack result) {
        this.baseCondition = baseCondition;
        this.ingredientCondition = ingredientCondition;
        this.result = result;
    }

    public boolean matches(ItemStack base, ItemStack ingredient) {
        if (base == null || ingredient == null) return false;
        return baseCondition.test(base) && ingredientCondition.test(ingredient);
    }

    public ItemStack getResult() {
        return result.clone();
    }
}