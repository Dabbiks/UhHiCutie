package dabbiks.uhc.game.gameplay.champions.alchemist;

import org.bukkit.inventory.ItemStack;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class BrewingModifier {

    private final Predicate<ItemStack> potionCondition;
    private final Predicate<ItemStack> ingredientCondition;
    private final BiFunction<ItemStack, ItemStack, ItemStack> modifierFunction;

    public BrewingModifier(Predicate<ItemStack> potionCondition, Predicate<ItemStack> ingredientCondition, BiFunction<ItemStack, ItemStack, ItemStack> modifierFunction) {
        this.potionCondition = potionCondition;
        this.ingredientCondition = ingredientCondition;
        this.modifierFunction = modifierFunction;
    }

    public boolean matches(ItemStack potion, ItemStack ingredient) {
        if (potion == null || ingredient == null) return false;
        return potionCondition.test(potion) && ingredientCondition.test(ingredient);
    }

    public ItemStack modify(ItemStack potion, ItemStack ingredient) {
        return modifierFunction.apply(potion, ingredient);
    }
}