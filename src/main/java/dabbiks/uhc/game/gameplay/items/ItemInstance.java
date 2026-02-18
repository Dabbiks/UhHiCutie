package dabbiks.uhc.game.gameplay.items;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantData;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantSlot;
import dabbiks.uhc.game.gameplay.items.data.fireworks.FireworkData;
import dabbiks.uhc.game.gameplay.items.data.perks.PerkType;
import dabbiks.uhc.game.gameplay.items.data.potions.PotionData;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;

public class ItemInstance implements Cloneable {

    private String material;
    private int amount;
    private String name;
    private List<String> lore;
    private Integer customModelData;
    private boolean tags;
    private boolean canBeForged;
    private boolean canBeEnchanted;
    private boolean canParry;
    private String armorSlot;
    private String armorTexture;

    private List<AttributeData> attributes;
    private EquipmentSlot equipmentSlot;
    private List<EnchantData> enchants;
    private EnchantSlot enchantSlot;
    private List<PerkType> perks;
    private List<PotionData> potion;
    private FireworkData firework;

    public Material getMaterial() { return Material.valueOf(material.toUpperCase()); }
    public void setMaterial(String material) { this.material = material; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getLore() { return lore; }
    public void setLore(List<String> lore) { this.lore = lore; }

    public Integer getCustomModelData() { return customModelData; }
    public void setCustomModelData(Integer customModelData) { this.customModelData = customModelData; }

    public boolean isTags() { return tags; }
    public void setTags(boolean tags) { this.tags = tags; }

    public List<PotionData> getPotion() { return potion; }
    public void setPotion(List<PotionData> potion) { this.potion = potion; }

    public FireworkData getFirework() { return firework; }
    public void setFirework(FireworkData firework) { this.firework = firework; }

    public boolean canBeForged() { return canBeForged; }
    public void setCanBeForged(boolean canBeForged) { this.canBeForged = canBeForged; }

    public boolean canBeEnchanted() { return canBeEnchanted; }
    public void setCanBeEnchanted(boolean canBeEnchanted) { this.canBeEnchanted = canBeEnchanted; }

    public boolean canParry() { return canParry; }
    public void setCanParry(boolean canParry) { this.canParry = canParry; }

    public List<AttributeData> getAttributes() { return attributes; }
    public void setAttributes(List<AttributeData> data) { this.attributes = data; }

    public EquipmentSlot getEquipmentSlot() { return equipmentSlot; }
    public void setEquipmentSlot(EquipmentSlot equipmentSlot) { this.equipmentSlot = equipmentSlot; }

    public List<EnchantData> getEnchants() { return enchants; }
    public void setEnchants(List<EnchantData> data) { this.enchants = data; }

    public EnchantSlot getEnchantSlot() { return enchantSlot; }
    public void setEnchantSlot(EnchantSlot enchantSlot) { this.enchantSlot = enchantSlot; }

    public List<PerkType> getPerks() { return perks; }
    public void setPerks(List<PerkType> perks) { this.perks = perks; }

    public String getArmorSlot() { return armorSlot; }
    public void setArmorSlot(String armorSlot) { this.armorSlot = armorSlot; }

    public String getArmorTexture() { return armorTexture; }
    public void setArmorTexture(String armorTexture) { this.armorTexture = armorTexture; }

    @Override
    public ItemInstance clone() {
        try {
            ItemInstance cloned = (ItemInstance) super.clone();

            if (this.lore != null) cloned.lore = new ArrayList<>(this.lore);
            if (this.attributes != null) cloned.attributes = new ArrayList<>(this.attributes);
            if (this.enchants != null) cloned.enchants = new ArrayList<>(this.enchants);
            if (this.perks != null) cloned.perks = new ArrayList<>(this.perks);
            if (this.potion != null) cloned.potion = new ArrayList<>(this.potion);

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Error cloning ItemInstance", e);
        }
    }
}