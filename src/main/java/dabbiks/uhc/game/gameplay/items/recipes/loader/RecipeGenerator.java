package dabbiks.uhc.game.gameplay.items.recipes.loader;

import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantSlot;
import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeIngredient;
import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeInstance;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

import java.lang.reflect.Field;
import java.util.*;

public class RecipeGenerator {

    private final RecipeManager recipeManager;

    public RecipeGenerator(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public void registerAll() {
        registerTier("WOODEN", Material.OAK_PLANKS, 4.0, 0.0, 0);
        registerTier("STONE", Material.COBBLESTONE, 5.0, 0.0, 0);
        registerTier("COPPER", Material.COPPER_INGOT, 5.0, 1.5, 0);
        registerTier("GOLDEN", Material.GOLD_INGOT, 4.0, 1.0, 0);
        registerTier("IRON", Material.IRON_INGOT, 6.0, 2.0, 0);
        registerTier("DIAMOND", Material.DIAMOND, 7.0, 3.0, 0);
        registerTier("NETHERITE", Material.NETHERITE_INGOT, 8.0, 3.0, 0);

        registerSpears();
        registerMace();
        registerRanged();
    }

    private void registerTier(String prefix, Material ingredient, double baseDmg, double baseArmor, int modelData) {
        registerSword(prefix + "_SWORD", ingredient, baseDmg, modelData);
        registerTool(prefix + "_PICKAXE", ingredient, "PICKAXE", EnchantSlot.PICKAXE, modelData, "III", " S ", " S ");
        registerTool(prefix + "_AXE", ingredient, "AXE", EnchantSlot.AXE, modelData, "II", "IS", " S");
        registerTool(prefix + "_SHOVEL", ingredient, "SHOVEL", EnchantSlot.TOOL, modelData, "I", "S", "S");
        registerTool(prefix + "_HOE", ingredient, "HOE", EnchantSlot.TOOL, modelData, "II", " S", " S");

        if (baseArmor > 0) {
            registerArmorSet(prefix, ingredient, baseArmor, modelData);
        }
    }

    private void registerSword(String matName, Material ingredient, double damage, int modelData) {
        AttributeData dmgAttr = new AttributeData(AttributeType.ATTACK_DAMAGE, damage);
        registerRecipe(matName.toLowerCase(), Material.valueOf(matName), ingredient, modelData,
                EnchantSlot.SWORD, EquipmentSlot.HAND, RecipeType.WEAPON, List.of(dmgAttr), "I", "I", "S");
    }

    private void registerTool(String matName, Material ingredient, String type, EnchantSlot slot, int modelData, String... shape) {
        double damage = switch (type) {
            case "AXE" -> 9.0;
            case "PICKAXE" -> 5.0;
            case "SHOVEL" -> 5.5;
            default -> 1.0;
        };
        AttributeData dmgAttr = new AttributeData(AttributeType.ATTACK_DAMAGE, damage);
        registerRecipe(matName.toLowerCase(), Material.valueOf(matName), ingredient, modelData,
                slot, EquipmentSlot.HAND, RecipeType.TOOL, List.of(dmgAttr), shape);
    }

    private void registerArmorSet(String prefix, Material ingredient, double baseArmor, int modelData) {
        registerRecipe(prefix.toLowerCase() + "_helmet", Material.valueOf(prefix + "_HELMET"), ingredient, modelData,
                EnchantSlot.HELMET, EquipmentSlot.HEAD, RecipeType.ARMOR,
                List.of(new AttributeData(AttributeType.ARMOR, baseArmor)), "III", "I I");

        registerRecipe(prefix.toLowerCase() + "_chestplate", Material.valueOf(prefix + "_CHESTPLATE"), ingredient, modelData,
                EnchantSlot.CHESTPLATE, EquipmentSlot.CHEST, RecipeType.ARMOR,
                List.of(new AttributeData(AttributeType.ARMOR, baseArmor + 5.0)), "I I", "III", "III");

        registerRecipe(prefix.toLowerCase() + "_leggings", Material.valueOf(prefix + "_LEGGINGS"), ingredient, modelData,
                EnchantSlot.LEGGINGS, EquipmentSlot.LEGS, RecipeType.ARMOR,
                List.of(new AttributeData(AttributeType.ARMOR, baseArmor + 3.0)), "III", "I I", "I I");

        registerRecipe(prefix.toLowerCase() + "_boots", Material.valueOf(prefix + "_BOOTS"), ingredient, modelData,
                EnchantSlot.BOOTS, EquipmentSlot.FEET, RecipeType.ARMOR,
                List.of(new AttributeData(AttributeType.ARMOR, baseArmor)), "I I", "I I");
    }

    private void registerSpears() {
        registerSpear("WOODEN", Material.OAK_PLANKS, 4.0);
        registerSpear("STONE", Material.COBBLESTONE, 5.0);
        registerSpear("COPPER", Material.COPPER_INGOT, 5.0);
        registerSpear("IRON", Material.IRON_INGOT, 6.0);
        registerSpear("GOLDEN", Material.GOLD_INGOT, 4.0);
        registerSpear("DIAMOND", Material.DIAMOND, 7.0);
        registerSpear("NETHERITE", Material.NETHERITE_INGOT, 8.0);
    }

    private void registerSpear(String prefix, Material ingredient, double baseDmg) {
        String id = prefix.toLowerCase() + "_spear";
        Material resultMat = Material.valueOf(prefix + "_SPEAR");

        List<AttributeData> attrs = new ArrayList<>();
        attrs.add(new AttributeData(AttributeType.ATTACK_DAMAGE, baseDmg));

        EnchantSlot slot = EnchantSlot.SPEAR;

        registerRecipe(id, resultMat, ingredient, 0, slot, EquipmentSlot.HAND, RecipeType.WEAPON, attrs, "  I", " S ", "S  ");
    }

    private void registerMace() {
        Map<Character, RecipeIngredient> ingredients = new HashMap<>();
        ingredients.put('H', createIngredient("HEAVY_CORE", 0));
        ingredients.put('B', createIngredient("BREEZE_ROD", 0));

        List<AttributeData> attrs = List.of(new AttributeData(AttributeType.ATTACK_DAMAGE, 7.0));
        ItemInstance result = createResult(Material.MACE, "Mace", 0, EnchantSlot.MACE, EquipmentSlot.HAND, attrs);

        createAndRegister("mace", "shaped", List.of("H", "B"), ingredients, result, RecipeType.WEAPON);
    }

    private void registerRanged() {
        Map<Character, RecipeIngredient> bowIng = new HashMap<>();
        bowIng.put('S', createIngredient("STICK", 0));
        bowIng.put('T', createIngredient("STRING", 0));
        createAndRegister("bow", "shaped", List.of(" ST", "S T", " ST"), bowIng,
                createResult(Material.BOW, "Łuk", 0, EnchantSlot.BOW, EquipmentSlot.HAND, Collections.emptyList()), RecipeType.WEAPON);

        Map<Character, RecipeIngredient> crossIng = new HashMap<>();
        crossIng.put('S', createIngredient("STICK", 0));
        crossIng.put('T', createIngredient("STRING", 0));
        crossIng.put('I', createIngredient("IRON_INGOT", 0));
        crossIng.put('H', createIngredient("TRIPWIRE_HOOK", 0));
        createAndRegister("crossbow", "shaped", List.of("SIS", "THT", " S "), crossIng,
                createResult(Material.CROSSBOW, "Kusza", 0, EnchantSlot.CROSSBOW, EquipmentSlot.HAND, Collections.emptyList()), RecipeType.WEAPON);

        Map<Character, RecipeIngredient> fishIng = new HashMap<>();
        fishIng.put('S', createIngredient("STICK", 0));
        fishIng.put('T', createIngredient("STRING", 0));
        createAndRegister("fishing_rod", "shaped", List.of("  S", " ST", "S T"), fishIng,
                createResult(Material.FISHING_ROD, "Wędka", 0, EnchantSlot.FISHING_ROD, EquipmentSlot.HAND, Collections.emptyList()), RecipeType.TOOL);
    }

    private void registerRecipe(String id, Material resultMat, Material ingredient, int modelData,
                                EnchantSlot enchantSlot, EquipmentSlot eqSlot, RecipeType category,
                                List<AttributeData> attributes, String... shape) {

        Map<Character, RecipeIngredient> ingredients = new HashMap<>();
        ingredients.put('I', createIngredient(ingredient.name(), 0));
        if (Arrays.toString(shape).contains("S")) {
            ingredients.put('S', createIngredient("STICK", 0));
        }

        ItemInstance result = createResult(resultMat, null, modelData, enchantSlot, eqSlot, attributes);
        createAndRegister(id, "shaped", Arrays.asList(shape), ingredients, result, category);
    }

    private void createAndRegister(String id, String type, List<String> shape, Map<Character, RecipeIngredient> ingredients,
                                   ItemInstance result, RecipeType category) {
        RecipeInstance recipe = new RecipeInstance();
        recipe.setId(id);
        recipe.setType(type);
        recipe.setShape(shape);
        recipe.setIngredients(ingredients);
        recipe.setResult(result);
        recipe.setMaxCraftsPerPlayer(500);
        recipe.setCategories(List.of(category.name()));
        recipe.setShowInRecipeBook(false);

        recipeManager.registerRecipe(recipe);
    }

    private ItemInstance createResult(Material material, String name, int modelData, EnchantSlot enchantSlot,
                                      EquipmentSlot eqSlot, List<AttributeData> attributes) {
        ItemInstance item = new ItemInstance();
        item.setMaterial(material.name());
        item.setAmount(1);
        if (name != null) item.setName(name);
        item.setCustomModelData(modelData);
        item.setTags(true);
        item.setCanBeForged(true);
        item.setCanBeEnchanted(true);
        item.setCanParry(material.name().contains("SWORD"));
        item.setEnchantSlot(enchantSlot);
        item.setEquipmentSlot(eqSlot);
        item.setAttributes(new ArrayList<>(attributes));

        return item;
    }

    private RecipeIngredient createIngredient(String material, int modelData) {
        try {
            RecipeIngredient ingredient = new RecipeIngredient();
            Field matField = RecipeIngredient.class.getDeclaredField("material");
            matField.setAccessible(true);
            matField.set(ingredient, material);

            Field modelField = RecipeIngredient.class.getDeclaredField("customModelData");
            modelField.setAccessible(true);
            modelField.set(ingredient, modelData);

            return ingredient;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create ingredient via reflection", e);
        }
    }
}