package dabbiks.uhc.game.gameplay.champions.alchemist;

import dabbiks.uhc.Main;
import dabbiks.uhc.game.gameplay.champions.alchemist.BrewingManager;
import dabbiks.uhc.game.gameplay.champions.alchemist.BrewingModifier;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Predicate;

public class BrewingRecipeLoader {

    private static final NamespacedKey POTION_ID_KEY = new NamespacedKey(Main.plugin, "custom_potion_id");

    public static void load(BrewingManager manager) {
        Material[] ingredients = {
                Material.NETHER_WART, Material.REDSTONE, Material.GLOWSTONE_DUST,
                Material.GUNPOWDER, Material.DRAGON_BREATH, Material.FERMENTED_SPIDER_EYE,
                Material.SUGAR, Material.RABBIT_FOOT, Material.BLAZE_POWDER,
                Material.GLISTERING_MELON_SLICE, Material.SPIDER_EYE, Material.GHAST_TEAR,
                Material.MAGMA_CREAM, Material.PUFFERFISH, Material.GOLDEN_CARROT,
                Material.PHANTOM_MEMBRANE, Material.TURTLE_HELMET
        };

        for (Material mat : ingredients) {
            manager.addCustomIngredient(isMat(mat));
        }

        manager.addModifier(new BrewingModifier(
                isAnyPotion(),
                isMat(Material.GUNPOWDER),
                (potion, ingredient) -> changeMaterial(potion, Material.SPLASH_POTION)
        ));

        manager.addModifier(new BrewingModifier(
                item -> item != null && item.getType() == Material.SPLASH_POTION,
                isMat(Material.DRAGON_BREATH),
                (potion, ingredient) -> changeMaterial(potion, Material.LINGERING_POTION)
        ));

        manager.addModifier(new BrewingModifier(
                isWaterBottle(),
                isMat(Material.NETHER_WART),
                (potion, ing) -> createPotion(potion.getType(), "awkward", null, 0, 0, Color.BLUE, "§fKlarowna Mikstura")
        ));

        addRecipe(manager, "awkward", Material.SUGAR, "speed", PotionEffectType.SPEED, Color.AQUA, "§bMikstura Szybkości", true);
        addRecipe(manager, "awkward", Material.RABBIT_FOOT, "jump", PotionEffectType.JUMP_BOOST, Color.LIME, "§aMikstura Skoku", true);
        addRecipe(manager, "awkward", Material.BLAZE_POWDER, "strength", PotionEffectType.STRENGTH, Color.MAROON, "§cMikstura Siły", true);
        addRecipe(manager, "awkward", Material.GLISTERING_MELON_SLICE, "healing", PotionEffectType.INSTANT_HEALTH, Color.RED, "§cMikstura Leczenia", true);
        addRecipe(manager, "awkward", Material.SPIDER_EYE, "poison", PotionEffectType.POISON, Color.GREEN, "§2Mikstura Trucizny", true);
        addRecipe(manager, "awkward", Material.GHAST_TEAR, "regen", PotionEffectType.REGENERATION, Color.FUCHSIA, "§dMikstura Regeneracji", true);
        addRecipe(manager, "awkward", Material.MAGMA_CREAM, "fireres", PotionEffectType.FIRE_RESISTANCE, Color.ORANGE, "§6Mikstura Odporności na Ogień", false);
        addRecipe(manager, "awkward", Material.PUFFERFISH, "waterbreath", PotionEffectType.WATER_BREATHING, Color.NAVY, "§1Mikstura Oddychania pod Wodą", false);
        addRecipe(manager, "awkward", Material.GOLDEN_CARROT, "nightvision", PotionEffectType.NIGHT_VISION, Color.PURPLE, "§5Mikstura Widzenia w Ciemności", false);
        addRecipe(manager, "awkward", Material.PHANTOM_MEMBRANE, "slowfall", PotionEffectType.SLOW_FALLING, Color.WHITE, "§fMikstura Powolnego Opadania", false);

        addRecipe(manager, "water_bottle", Material.FERMENTED_SPIDER_EYE, "weakness", PotionEffectType.WEAKNESS, Color.GRAY, "§8Mikstura Osłabienia", false);

        addCorruption(manager, "speed", "slowness", PotionEffectType.SLOWNESS, Color.GRAY, "§8Mikstura Spowolnienia", true);
        addCorruption(manager, "jump", "slowness", PotionEffectType.SLOWNESS, Color.GRAY, "§8Mikstura Spowolnienia", true);
        addCorruption(manager, "healing", "harming", PotionEffectType.INSTANT_DAMAGE, Color.BLACK, "§4Mikstura Obrażeń", true);
        addCorruption(manager, "poison", "harming", PotionEffectType.INSTANT_DAMAGE, Color.BLACK, "§4Mikstura Obrażeń", true);
        addCorruption(manager, "nightvision", "invisibility", PotionEffectType.INVISIBILITY, Color.GRAY, "§7Mikstura Niewidzialności", false);
    }

    private static void addRecipe(BrewingManager manager, String baseId, Material ingredient, String resultId, PotionEffectType effect, Color color, String name, boolean hasGlowstone) {
        manager.addModifier(new BrewingModifier(
                baseId.equals("water_bottle") ? isWaterBottle() : isCustomPotion(baseId),
                isMat(ingredient),
                (potion, ing) -> createPotion(potion.getType(), resultId, effect, effect.isInstant() ? 0 : 100, 1, color, name)
        ));

        manager.addModifier(new BrewingModifier(
                isCustomPotion(resultId),
                isMat(Material.REDSTONE),
                (potion, ing) -> createPotion(potion.getType(), resultId + "_long", effect, effect.isInstant() ? 0 : 300, 1, color, name)
        ));

        if (hasGlowstone) {
            manager.addModifier(new BrewingModifier(
                    isCustomPotion(resultId),
                    isMat(Material.GLOWSTONE_DUST),
                    (potion, ing) -> createPotion(potion.getType(), resultId + "_strong", effect, effect.isInstant() ? 0 : 100, 2, color, name)
            ));
        }
    }

    private static void addCorruption(BrewingManager manager, String baseId, String resultId, PotionEffectType effect, Color color, String name, boolean hasGlowstone) {
        manager.addModifier(new BrewingModifier(
                isCustomPotion(baseId),
                isMat(Material.FERMENTED_SPIDER_EYE),
                (potion, ing) -> createPotion(potion.getType(), resultId, effect, effect.isInstant() ? 0 : 100, 1, color, name)
        ));

        manager.addModifier(new BrewingModifier(
                isCustomPotion(baseId + "_long"),
                isMat(Material.FERMENTED_SPIDER_EYE),
                (potion, ing) -> createPotion(potion.getType(), resultId + "_long", effect, effect.isInstant() ? 0 : 300, 1, color, name)
        ));

        if (hasGlowstone) {
            manager.addModifier(new BrewingModifier(
                    isCustomPotion(baseId + "_strong"),
                    isMat(Material.FERMENTED_SPIDER_EYE),
                    (potion, ing) -> createPotion(potion.getType(), resultId + "_strong", effect, effect.isInstant() ? 0 : 100, 2, color, name)
            ));
        }
    }

    private static Predicate<ItemStack> isMat(Material material) {
        return item -> item != null && item.getType() == material;
    }

    private static Predicate<ItemStack> isAnyPotion() {
        return item -> item != null && (item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION);
    }

    private static Predicate<ItemStack> isWaterBottle() {
        return item -> {
            if (item == null || item.getType() != Material.POTION) return false;
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            return meta != null && meta.getBasePotionType() == org.bukkit.potion.PotionType.WATER;
        };
    }

    private static Predicate<ItemStack> isCustomPotion(String id) {
        return item -> {
            if (!isAnyPotion().test(item)) return false;
            if (!item.hasItemMeta()) return false;
            String itemId = item.getItemMeta().getPersistentDataContainer().get(POTION_ID_KEY, PersistentDataType.STRING);
            return id != null && id.equals(itemId);
        };
    }

    private static ItemStack changeMaterial(ItemStack potion, Material newMaterial) {
        ItemStack modified = potion.clone();
        modified.setType(newMaterial);
        return modified;
    }

    private static ItemStack createPotion(Material material, String id, PotionEffectType effectType, int durationTicks, int amplifier, Color color, String name) {
        ItemStack item = new ItemStack(material);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta != null) {
            meta.clearCustomEffects();
            if (effectType != null) {
                meta.addCustomEffect(new PotionEffect(effectType, durationTicks, amplifier), true);
            }
            meta.setColor(color);
            meta.setDisplayName(name);
            meta.getPersistentDataContainer().set(POTION_ID_KEY, PersistentDataType.STRING, id);
            item.setItemMeta(meta);
        }
        return item;
    }
}