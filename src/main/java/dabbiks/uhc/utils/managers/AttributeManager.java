package dabbiks.uhc.utils.managers;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static dabbiks.uhc.Main.plugin;

public class AttributeManager {

    /**
     * Dodaje stały modyfikator do atrybutu.
     */
    public void addModifier(LivingEntity entity, Attribute attribute, String name, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;

        // UUID generowane deterministycznie z nazwy, aby unikać duplikatów
        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
        AttributeModifier modifier = new AttributeModifier(uuid, name, amount, operation);

        // Usuń stary modyfikator o tym samym UUID, jeśli istnieje, aby nadpisać
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
    public double getAttributeValue(LivingEntity entity, Attribute attribute) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return 0.0;
        }
        return instance.getValue();
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
