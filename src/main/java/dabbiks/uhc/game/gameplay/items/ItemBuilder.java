package dabbiks.uhc.game.gameplay.items;

import dabbiks.uhc.game.gameplay.items.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.attributes.AttributeManager;
import dabbiks.uhc.game.gameplay.items.enchants.EnchantData;
import dabbiks.uhc.game.gameplay.items.enchants.EnchantManager;
import dabbiks.uhc.game.gameplay.items.fireworks.ExplosionData;
import dabbiks.uhc.game.gameplay.items.fireworks.FireworkData;
import dabbiks.uhc.game.gameplay.items.perks.PerkType;
import dabbiks.uhc.game.gameplay.items.potions.PotionData;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static dabbiks.uhc.Main.itemU;

public class ItemBuilder {

    private final ItemInstance instance;

    public ItemBuilder(ItemInstance instance) {
        this.instance = instance;
    }

    public ItemStack build() {
        Material material = instance.getMaterial();
        if (material == null) material = Material.BARRIER;

        ItemStack item = new ItemStack(material, instance.getAmount() > 0 ? instance.getAmount() : 1);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        // * META

        if (meta == null) return item;

        if (instance.getName() != null) {
            meta.setDisplayName(instance.getName());
        }

        if (instance.getLore() != null) {
            lore = new ArrayList<>(instance.getLore());
        }

        if (instance.getCustomModelData() != null) {
            meta.setCustomModelData(instance.getCustomModelData());
        }

        if (instance.getEnchants() != null) {
            if (!lore.isEmpty()) { lore.add(""); }
            for (EnchantData enchantData : instance.getEnchants()) {
                lore.add(EnchantManager.formatLoreLine(enchantData));
                if (enchantData.getType().getEnchantment() == null) continue;
                meta.addEnchant(enchantData.getType().getEnchantment(), enchantData.getLevel(), true);
            }
        }

        if (instance.getAttributes() != null) {
            if (!lore.isEmpty()) { lore.add(""); }
            for (AttributeData attributeData : instance.getAttributes()) {
                lore.add(AttributeManager.formatLoreLine(attributeData));
                if (attributeData.getAttributeType().getAttribute() == null) continue;
                AttributeModifier.Operation operation = attributeData.isPercent() ? AttributeModifier.Operation.ADD_SCALAR : AttributeModifier.Operation.ADD_NUMBER;
                AttributeModifier modifier = new AttributeModifier(
                        UUID.randomUUID(),
                        attributeData.getAttributeType().getName(),
                        attributeData.getAttributeValue(),
                        operation,
                        attributeData.getEquipmentSlot()
                );
                meta.addAttributeModifier(attributeData.getAttributeType().getAttribute(), modifier);
            }

        }

        if (instance.getPotion() != null && meta instanceof PotionMeta potionMeta) {
            if (!lore.isEmpty()) { lore.add(""); }
            for (PotionData potionData : instance.getPotion()) {
                // ! lore
                PotionEffectType type = potionData.getType().getBukkitType();
                int level = potionData.getAmplifier();
                int duration = potionData.getDuration();
                potionMeta.addCustomEffect(new PotionEffect(type, duration * 20, level-1), true);
            }
        }

        if (instance.getFirework() != null && meta instanceof FireworkMeta fireworkMeta) {
            FireworkData data = instance.getFirework();

            fireworkMeta.setPower(data.getFlightDuration());

            for (ExplosionData exp : data.getExplosions()) {
                FireworkEffect.Builder builder = FireworkEffect.builder()
                        .with(exp.getType().getBukkitType())
                        .flicker(exp.isFlicker())
                        .trail(exp.isTrail());

                List<Color> colors = exp.getBukkitColors(exp.getColorsHex());
                if (!colors.isEmpty()) builder.withColor(colors);

                List<Color> fades = exp.getBukkitColors(exp.getFadeColorsHex());
                if (!fades.isEmpty()) builder.withFade(fades);

                fireworkMeta.addEffect(builder.build());
            }
        }

        if (instance.getPerks() != null && !instance.getPerks().isEmpty()) {
            if (!lore.isEmpty()) { lore.add(""); }
            for (PerkType perkType : instance.getPerks()) {
                lore.addAll(perkType.getLore());
            }
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.setLore(lore);
        item.setItemMeta(meta);

        // * NBT

        NBTItem nbtItem = new NBTItem(item);

        if (instance.getEnchants() != null) {
            for (EnchantData enchantData : instance.getEnchants()) {
                nbtItem.setInteger(enchantData.getType().getName(), enchantData.getLevel());
            }
        }

        if (instance.getAttributes() != null) {
            for (AttributeData attributeData : instance.getAttributes()) {
                String key = attributeData.getAttributeType().getName();
                nbtItem.setDouble(key, attributeData.getAttributeValue());

                if (attributeData.getEquipmentSlot() != null) {
                    nbtItem.setString(key + "_SLOT", attributeData.getEquipmentSlot().name());
                }

                nbtItem.setBoolean(key + "_PERCENT", attributeData.isPercent());
            }
        }

        if (instance.getPerks() != null && !instance.getPerks().isEmpty()) {
            for (PerkType perkType : instance.getPerks()) {
                nbtItem.setInteger(perkType.name(), 1);
            }
        }

        if (instance.canBeForged()) {
            nbtItem.setInteger("CAN_BE_FORGED", 1);
        }

        item = nbtItem.getItem();

        // * ADDITIONAL MODIFIERS

        if (instance.canParry()) {
            itemU.addParryingComponent(item);
        }

        if (instance.getArmorSlot() != null && instance.getArmorTexture() != null) {
            itemU.setEquippableTexture(item, instance.getArmorSlot(), instance.getArmorTexture());
        }

        return item;
    }
}
