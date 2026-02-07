package dabbiks.uhc.game.gameplay.items;

import dabbiks.uhc.game.gameplay.items.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.enchants.EnchantData;
import dabbiks.uhc.game.gameplay.items.fireworks.FireworkData;
import dabbiks.uhc.game.gameplay.items.perks.PerkType;
import dabbiks.uhc.game.gameplay.items.potions.PotionData;
import org.bukkit.Material;
import org.bukkit.block.data.type.Fire;

import java.util.List;
import java.util.Map;

public class ItemInstance {

    private String material;
    private int amount;
    private String name;
    private List<String> lore;
    private Integer customModelData;
    private boolean tags;
    private boolean canBeForged;
    private boolean canParry;
    private String armorSlot;
    private String armorTexture;

    private List<AttributeData> attributes;
    private List<EnchantData> enchants;
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

    public boolean canParry() { return canParry; }
    public void setCanParry(boolean canParry) { this.canParry = canParry; }

    public List<AttributeData> getAttributes() { return attributes; }
    public void setAttributes(List<AttributeData> data) { this.attributes = data; }

    public List<EnchantData> getEnchants() { return enchants; }
    public void setEnchants(List<EnchantData> data) { this.enchants = data; }

    public List<PerkType> getPerks() { return perks; }
    public void setPerks(List<PerkType> perks) { this.perks = perks; }

    public String getArmorSlot() { return armorSlot; }
    public void setArmorSlot(String armorSlot) { this.armorSlot = armorSlot; }

    public String getArmorTexture() { return armorTexture; }
    public void setArmorTexture(String armorTexture) { this.armorTexture = armorTexture; }
}