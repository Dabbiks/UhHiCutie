package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class BoomerangHandler {

    private final ArmorHandler armorHandler = new ArmorHandler();

    public void handleHit(Player thrower, LivingEntity victim, ItemStack boomerangItem) {
        Double baseDamage = NBT.get(boomerangItem, (Function<ReadableItemNBT, Double>) nbt -> nbt.getDouble(AttributeType.RANGED_DAMAGE.name()));
        if (baseDamage == null) baseDamage = 1.0;

        double finalDamage = armorHandler.handle(thrower, victim, baseDamage);

        victim.damage(finalDamage, thrower);
        victim.setNoDamageTicks(0);
    }
}