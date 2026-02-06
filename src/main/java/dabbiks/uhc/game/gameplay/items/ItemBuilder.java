package dabbiks.uhc.game.gameplay.items;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static dabbiks.uhc.Main.parryU;

public class ItemBuilder {

    private final ItemStack item;
    private ItemMeta meta;
    private final Map<String, Object> nbtData = new HashMap<>();

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material != null ? material : Material.STONE, amount);
        this.meta = item.getItemMeta();
    }

    public static ItemBuilder of(String materialName, int amount) {
        Material mat = Material.getMaterial(materialName != null ? materialName.toUpperCase() : "STONE");
        return new ItemBuilder(mat, amount);
    }

    public ItemBuilder setName(String name) {
        if (name != null) meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (lore != null) meta.setLore(new ArrayList<>(lore));
        return this;
    }

    public ItemBuilder setCustomModelData(Integer data) {
        if (data != null) meta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder setDamage(Integer damage) {
        if (damage != null && meta instanceof Damageable dMeta) dMeta.setDamage(damage);
        return this;
    }

    public ItemBuilder applyEnchantments(Map<String, Integer> enchants) {
        if (enchants != null) {
            enchants.forEach((key, level) -> {
                Enchantment e = Enchantment.getByName(key.toUpperCase());
                if (e != null) meta.addEnchant(e, level, true);
            });
        }
        return this;
    }

    public ItemBuilder applyPotionData(Map<String, Object> data) {
        if (data != null && meta instanceof PotionMeta pm) {
            try {
                String type = (String) data.get("type");
                int level = getInt(data, "level", 1);
                int duration = getInt(data, "duration", 200);
                PotionEffectType pet = PotionEffectType.getByName(type.toUpperCase());
                if (pet != null) pm.addCustomEffect(new PotionEffect(pet, duration, level - 1), true);
            } catch (Exception ignored) {}
        }
        return this;
    }

    public ItemBuilder applyFireworkData(Map<String, Object> data) {
        if (data != null && meta instanceof FireworkMeta fm) {
            try {
                fm.setPower(getInt(data, "power", 1));
                List<?> explosions = (List<?>) data.get("explosions");
                if (explosions != null) {
                    for (Object obj : explosions) {
                        if (obj instanceof Map<?, ?> ex) {
                            FireworkEffect.Builder b = FireworkEffect.builder();
                            try { b.with(FireworkEffect.Type.valueOf(((String) ex.get("type")).toUpperCase())); } catch (Exception e) { b.with(FireworkEffect.Type.BALL); }
                            b.flicker(Boolean.TRUE.equals(ex.get("flicker"))).trail(Boolean.TRUE.equals(ex.get("trail")));
                            List<Color> colors = new ArrayList<>();
                            if (ex.get("colors") instanceof List<?> cl) cl.forEach(c -> { try { colors.add(DyeColor.valueOf(((String)c).toUpperCase()).getColor()); } catch (Exception ignored){} });
                            b.withColor(colors);
                            fm.addEffect(b.build());
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        return this;
    }

    public ItemBuilder applyAttributesFromConfig(List<Map<String, Object>> attributes) {
        if (attributes == null) return this;
        for (Map<String, Object> attrMap : attributes) {
            try {
                String attrName = (String) attrMap.get("name");
                Number amount = (Number) attrMap.get("amount");
                Number op = (Number) attrMap.get("operation");
                if (attrName == null || amount == null || op == null) continue;

                Attribute attr = Attribute.valueOf(attrName.toUpperCase());
                AttributeModifier.Operation operation = AttributeModifier.Operation.values()[op.intValue()];
                UUID uuid = parseUUID((List<?>) attrMap.get("uuid"));
                EquipmentSlot slot = null;
                if (attrMap.get("slot") instanceof String s) try { slot = EquipmentSlot.valueOf(s.toUpperCase()); } catch (Exception ignored) {}

                meta.addAttributeModifier(attr, new AttributeModifier(uuid, attrName, amount.doubleValue(), operation, slot));
            } catch (Exception ignored) {}
        }
        return this;
    }

    public ItemBuilder applyCustomNbt(Map<String, Object> nbt) {
        if (nbt != null) this.nbtData.putAll(nbt);
        return this;
    }

    public ItemBuilder addSystemNbt(String key, Object value) {
        this.nbtData.put(key, value);
        return this;
    }

    public ItemBuilder applyGameMechanics(Map<String, Double> attrMap, Map<String, Integer> enchMap, List<String> perks) {
        if (attrMap != null) meta = ItemUtils.formatItemAttributesLore(item, attrMap, meta);

        List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();

        if (enchMap != null && !enchMap.isEmpty()) {
            if (!lore.isEmpty()) lore.add("");
            meta.setLore(lore);
            meta.setEnchantmentGlintOverride(true);
            meta = ItemUtils.formatItemEnchantmentsLore(item, enchMap, meta);
            lore = meta.getLore();
        }

        if (perks != null && !perks.isEmpty()) {
            if (!lore.isEmpty()) lore.add("");
            meta.setLore(lore);
            meta = ItemUtils.formatItemPerkLore(item, perks, meta);
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemStack build(boolean clearTags, String armorSlot, String armorTexture) {
        if (clearTags) return item;

        item.setItemMeta(meta);

        // NBTAPI Application
        if (!nbtData.isEmpty()) {
            NBTItem nbtItem = new NBTItem(item);
            nbtData.forEach(nbtItem::setObject); // Metoda helpera setObject (lub manualna obsługa typów)
            item.setItemMeta(nbtItem.getItem().getItemMeta());
        }

        // Final components
        if (isTool(item.getType())) parryU.addParryingComponent(item);
        if (armorSlot != null && armorTexture != null) {
            return ItemUtils.setEquippable(item, armorSlot, armorTexture);
        }

        return item;
    }

    private int getInt(Map<String, Object> map, String key, int def) {
        return ((Number) map.getOrDefault(key, def)).intValue();
    }

    private UUID parseUUID(List<?> raw) {
        if (raw != null && raw.size() == 4) {
            return new UUID(
                    ((Number) raw.get(0)).longValue() << 32 | (((Number) raw.get(1)).intValue() & 0xFFFFFFFFL),
                    ((Number) raw.get(2)).longValue() << 32 | (((Number) raw.get(3)).intValue() & 0xFFFFFFFFL)
            );
        }
        return UUID.randomUUID();
    }

    private boolean isTool(Material type) {
        String n = type.name();
        return n.endsWith("_SWORD") || n.endsWith("_AXE") || n.endsWith("_PICKAXE") || n.endsWith("_SHOVEL") || n.endsWith("_HOE");
    }
}
