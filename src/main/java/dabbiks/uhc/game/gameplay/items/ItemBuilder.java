package dabbiks.uhc.game.gameplay.items;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeManager;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantData;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantManager;
import dabbiks.uhc.game.gameplay.items.data.fireworks.ExplosionData;
import dabbiks.uhc.game.gameplay.items.data.fireworks.FireworkData;
import dabbiks.uhc.game.gameplay.items.data.perks.PerkType;
import dabbiks.uhc.game.gameplay.items.data.potions.PotionData;
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

        if (meta == null) return item;

        if (instance.getName() != null) {
            meta.setDisplayName("§f" + instance.getName());
        }

        if (instance.getLore() != null) {
            lore = new ArrayList<>(instance.getLore());
        }

        if (instance.getCustomModelData() != null) {
            meta.setCustomModelData(instance.getCustomModelData());
        }

        if (instance.getAttributes() != null) {
            if (!lore.isEmpty()) { lore.add(""); }

            List<AttributeData> attributes = new ArrayList<>(instance.getAttributes());

            attributes.sort((a, b) -> {
                double valA = a.getAttributeValue();
                double valB = b.getAttributeValue();

                if (a.getAttributeType() == AttributeType.ATTACK_SPEED) {
                    valA = 4.0 * Math.pow(0.632, -valA / 2.0);
                }
                if (b.getAttributeType() == AttributeType.ATTACK_SPEED) {
                    valB = 4.0 * Math.pow(0.632, -valB / 2.0);
                }

                String typeNameA = a.getAttributeType().toString().toUpperCase();
                String typeNameB = b.getAttributeType().toString().toUpperCase();

                boolean isNegTypeA = typeNameA.startsWith("SIZE") ||
                        typeNameA.startsWith("BURNING_TIME") ||
                        typeNameA.startsWith("FALL_DAMAGE");
                boolean isNegTypeB = typeNameB.startsWith("SIZE") ||
                        typeNameB.startsWith("BURNING_TIME") ||
                        typeNameB.startsWith("FALL_DAMAGE");

                boolean isPositiveA = isNegTypeA ? valA <= 0 : valA >= 0;
                boolean isPositiveB = isNegTypeB ? valB <= 0 : valB >= 0;

                int groupA = isPositiveA ? (a.getAttributeType().isPercentage() ? 1 : 0)
                        : (a.getAttributeType().isPercentage() ? 3 : 2);
                int groupB = isPositiveB ? (b.getAttributeType().isPercentage() ? 1 : 0)
                        : (b.getAttributeType().isPercentage() ? 3 : 2);

                if (groupA != groupB) {
                    return Integer.compare(groupA, groupB);
                }

                return a.getAttributeType().getName().compareToIgnoreCase(b.getAttributeType().getName());
            });

            for (AttributeData attributeData : attributes) {
                lore.add(AttributeManager.formatLoreLine(attributeData));
                if (attributeData.getAttributeType().getAttribute() == null) continue;

                double value = attributeData.getAttributeValue();
                AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_NUMBER;

                if (attributeData.getAttributeType().isPercentage()) {
                    operation = AttributeModifier.Operation.ADD_SCALAR;
                    value = value / 100.0;
                }

                AttributeModifier modifier = new AttributeModifier(
                        UUID.randomUUID(),
                        attributeData.getAttributeType().getName(),
                        value,
                        operation,
                        instance.getEquipmentSlot()
                );
                meta.addAttributeModifier(attributeData.getAttributeType().getAttribute(), modifier);
            }
        }

        if (instance.getEnchants() != null) {
            if (!lore.isEmpty()) { lore.add(""); }
            meta.setEnchantmentGlintOverride(true);
            for (EnchantData enchantData : instance.getEnchants()) {
                lore.add(EnchantManager.formatLoreLine(enchantData));
                if (enchantData.getType().getEnchantment() == null) continue;
                meta.addEnchant(enchantData.getType().getEnchantment(), enchantData.getLevel(), true);
            }
        }

        if (instance.getPotion() != null && meta instanceof PotionMeta potionMeta) {
            if (!lore.isEmpty()) { lore.add(""); }
            for (PotionData potionData : instance.getPotion()) {
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
                lore.add("§b⭐ §f" + perkType.getName());
                lore.addAll(perkType.getLore());
            }
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.setLore(lore);
        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);

        if (instance.getEnchants() != null) {
            for (EnchantData enchantData : instance.getEnchants()) {
                nbtItem.setInteger(enchantData.getType().name(), enchantData.getLevel());
            }
        }

        if (instance.getAttributes() != null) {
            for (AttributeData attributeData : instance.getAttributes()) {
                nbtItem.setDouble(attributeData.getAttributeType().name(), attributeData.getAttributeValue());
            }
        }

        if (instance.getEquipmentSlot() != null) {
            nbtItem.setString("SLOT", instance.getEquipmentSlot().name());
        }

        if (instance.getPerks() != null && !instance.getPerks().isEmpty()) {
            for (PerkType perkType : instance.getPerks()) {
                nbtItem.setInteger(perkType.name(), 1);
            }
        }

        if (instance.canBeForged()) {
            nbtItem.setInteger(ItemTags.CAN_BE_FORGED.name(), 1);
        }

        if (instance.canBeEnchanted()) {
            nbtItem.setInteger(ItemTags.CAN_BE_ENCHANTED.name(), 1);
        }

        if (instance.isEnchanted()) {
            nbtItem.setBoolean(ItemTags.IS_ENCHANTED.name(), true);
        }

        if (instance.getEnchantSlot() != null) {
            nbtItem.setInteger(instance.getEnchantSlot().name(), 1);
        }

        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);

        item = nbtItem.getItem();

        if (instance.canParry()) {
            itemU.addParryingComponent(item);
        }

        if (instance.getArmorSlot() != null && instance.getArmorTexture() != null) {
            itemU.setEquippableTexture(item, instance.getArmorSlot(), instance.getArmorTexture());
        }

        return item;
    }
}
