package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ProjectileHandler {

    public void handle(Projectile projectile, ItemStack weapon) {
        if (projectile instanceof Arrow arrow) handleArrow(arrow, weapon);
        else if (projectile instanceof Trident trident) handleTrident(trident, weapon);
    }

    public void handleArrow(Arrow arrow, ItemStack weapon) {
        applyTag(weapon, arrow, AttributeType.RANGED_DAMAGE.name(), "double");
        applyTag(weapon, arrow, EnchantType.POWER.name(), "int");
        applyTag(weapon, arrow, EnchantType.GLOWING.name(), "int");
        applyTag(weapon, arrow, EnchantType.PYROTECHNICS.name(), "int");
    }

    public void handleTrident(Trident trident, ItemStack weapon) {
        applyTag(weapon, trident, AttributeType.RANGED_DAMAGE.name(), "double");
        applyTag(weapon, trident, EnchantType.GROUNDING.name(), "int");
        applyTag(weapon, trident, EnchantType.CHANNELING.name(), "int");
    }

    private void applyTag(ItemStack weapon, Projectile projectile, String key, String type) {
        NBT.get(weapon, nbtItem -> {
            if (!nbtItem.hasTag(key)) return;

            if (type.equals("double")) {
                double value = nbtItem.getDouble(key);
                NBT.modifyPersistentData(projectile, (Consumer<ReadWriteNBT>) nbt -> nbt.setDouble(key, value));
            } else {
                int value = nbtItem.getInteger(key);
                NBT.modifyPersistentData(projectile, (Consumer<ReadWriteNBT>) nbt -> nbt.setInteger(key, value));
            }
        });
    }
}