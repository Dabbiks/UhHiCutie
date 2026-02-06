package dabbiks.uhc.game.gameplay.recipes;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Map;

public class RecipeResult {

    private String material;
    private int amount;
    private String name;
    private List<String> lore;
    private Integer customModelData;
    private Integer damage;
    private Map<String, Object> nbt;
    private boolean clearTags;

    private Map<String, Integer> enchantments;
    private List<Map<String, Object>> attributes;
    private Map<String, Object> potionData;
    private Map<String, Object> firework;

    private boolean canBeForged;
    private Map<String, Double> attributeMap;
    private Map<String, Integer> enchantMap;
    private List<String> perkList;
    private String armorSlot;
    private String armorTexture;

    /**
     * Główna metoda tworząca przedmiot do gry.
     */
    public ItemStack buildItem(String id) {
        return createBuilder(id).build(clearTags, armorSlot, armorTexture);
    }

    /**
     * Metoda tworząca przedmiot do podglądu (np. w GUI).
     */
    public ItemStack buildShowcaseItem(String id) {
        return createBuilder(id).build(clearTags, armorSlot, armorTexture);
    }

    /**
     * Helper mapujący pola klasy na Buildera.
     */
    private ItemBuilder createBuilder(String id) {
        return ItemBuilder.of(material, amount)
                .setName(name)
                .setLore(lore)
                .setCustomModelData(customModelData)
                .setDamage(damage)
                .applyPotionData(potionData)
                .applyFireworkData(firework)
                .applyEnchantments(enchantments)
                .applyAttributesFromConfig(attributes)
                .applyCustomNbt(nbt)
                .addSystemNbt("GET_RECIPE_ID", id)
                .addSystemNbt("CANBEFORGED", canBeForged ? 1 : 0)
                .addSystemNbt("ALREADYUPDATED", 1)
                .applyGameMechanics(attributeMap, enchantMap, perkList);
    }

    public boolean shouldClearTags() {
        return clearTags;
    }

    public void setEnchantments(Map<String, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public void setAttributes(List<Map<String, Object>> attributes) {
        this.attributes = attributes;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public void setPotionData(Map<String, Object> potionData) {
        this.potionData = potionData;
    }

    public void setFirework(Map<String, Object> firework) {
        this.firework = firework;
    }
}