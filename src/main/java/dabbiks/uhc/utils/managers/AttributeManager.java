package dabbiks.uhc.utils.managers;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static dabbiks.uhc.Main.plugin;
import static org.bukkit.inventory.EquipmentSlot.HAND;

public class AttributeManager {

    /**
     * Dodaje stały modyfikator do atrybutu.
     */
    public void addModifier(LivingEntity entity, Attribute attribute, String name, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;

        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
        AttributeModifier modifier = new AttributeModifier(uuid, name, amount, operation);

        if (instance.getModifier(uuid) != null) {
            instance.removeModifier(uuid);
        }
        instance.addModifier(modifier);
    }

    /**
     * Dodaje czasowy modyfikator do atrybutu (usuwa się po określonym czasie).
     * @param durationTicks Czas trwania w tickach (20 ticków = 1 sekunda).
     */
    public void addTemporaryModifier(LivingEntity entity, Attribute attribute, String name, double amount, AttributeModifier.Operation operation, long durationTicks) {
        addModifier(entity, attribute, name, amount, operation);

        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.isValid()) {
                    removeModifier(entity, attribute, uuid);
                }
            }
        }.runTaskLater(plugin, durationTicks);
    }

    /**
     * Usuwa konkretny modyfikator na podstawie UUID.
     */
    public void removeModifier(LivingEntity entity, Attribute attribute, UUID uuid) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null && instance.getModifier(uuid) != null) {
            instance.removeModifier(uuid);
        }
    }

    /**
     * Pobiera aktualną całkowitą wartość atrybutu (wartość bazowa + wszystkie modyfikatory).
     * @return Wartość atrybutu lub 0.0, jeśli atrybut nie występuje u tego bytu.
     */
    public double getAttributeValue(LivingEntity entity, AttributeType type, double baseValue) {
        if (type.getAttribute() != null) {
            AttributeInstance instance = entity.getAttribute(type.getAttribute());
            return instance != null ? instance.getValue() : 0.0;
        }

        double flatValue = 0.0;
        double percentValue = 0.0;
        EntityEquipment equipment = entity.getEquipment();

        if (equipment == null) {
            return 0.0;
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item;
            switch (slot) {
                case HAND: item = equipment.getItemInMainHand(); break;
                case OFF_HAND: item = equipment.getItemInOffHand(); break;
                case HEAD: item = equipment.getHelmet(); break;
                case CHEST: item = equipment.getChestplate(); break;
                case LEGS: item = equipment.getLeggings(); break;
                case FEET: item = equipment.getBoots(); break;
                default: continue;
            }

            if (item == null || item.getType().isAir()) {
                continue;
            }

            NBTItem nbt = new NBTItem(item);
            String key = type.name();

            if (!nbt.hasKey(key)) {
                continue;
            }

            if (nbt.hasKey("SLOT")) {
                String slotName = nbt.getString("SLOT");
                if (!slotName.equals(slot.name())) {
                    continue;
                }
            }

            double value = nbt.getDouble(key);
            boolean isPercent = nbt.hasKey(key + "_PERCENT") && nbt.getBoolean(key + "_PERCENT");

            if (isPercent) {
                percentValue += value;
            } else {
                flatValue += value;
            }
        }

        if (flatValue == 0.0 && percentValue > 0.0) {
            return baseValue * percentValue;
        }

        return flatValue * (1.0 + percentValue);
    }



    /**
     * Czyści WSZYSTKIE modyfikatory ze WSZYSTKICH atrybutów danego entity.
     * Przywraca entity do stanu bazowego (resetuje statystyki).
     */
    public void clearAllAttributes(LivingEntity entity) {
        for (Attribute attribute : Attribute.values()) {
            AttributeInstance instance = entity.getAttribute(attribute);
            if (instance != null) {
                for (AttributeModifier modifier : instance.getModifiers()) {
                    instance.removeModifier(modifier);
                }
            }
        }
    }

    /**
     * Czyści wszystkie modyfikatory tylko dla jednego, konkretnego atrybutu.
     */
    public void clearSpecificAttribute(LivingEntity entity, Attribute attribute) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            for (AttributeModifier modifier : instance.getModifiers()) {
                instance.removeModifier(modifier);
            }
        }
    }
}
