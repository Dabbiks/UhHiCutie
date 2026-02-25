package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;

public class ProjectileHandler {

    public void handle(Player player, Projectile projectile) {
        if (projectile instanceof Arrow arrow) handleArrow(player, arrow);
        else if (projectile instanceof Trident trident) handleTrident(player, trident);
    }

    public void handleArrow(Player player, Arrow arrow) {
        NBTItem nbt = new NBTItem(player.getInventory().getItemInMainHand());

        applyTag(nbt, arrow, AttributeType.RANGED_DAMAGE.name(), "double");
        applyTag(nbt, arrow, EnchantType.POWER.name(), "int");
        applyTag(nbt, arrow, EnchantType.GLOWING.name(), "int");
        applyTag(nbt, arrow, EnchantType.PYROTECHNICS.name(), "int");
    }

    public void handleTrident(Player player, Trident trident) {
        NBTItem nbt = new NBTItem(player.getInventory().getItemInMainHand());

        applyTag(nbt, trident, AttributeType.RANGED_DAMAGE.name(), "double");
        applyTag(nbt, trident, EnchantType.GROUNDING.name(), "int");
        applyTag(nbt, trident, EnchantType.CHANNELING.name(), "int");
    }

    private void applyTag(NBTItem nbtItem, Projectile projectile, String key, String type) {
        if (!nbtItem.hasTag(key)) return;

        if (type.equals("double")) {
            NBT.modifyPersistentData(projectile, nbt -> {
                nbt.setDouble(key, nbtItem.getDouble(key));
            });
        } else {
            NBT.modifyPersistentData(projectile, nbt -> {
                nbt.setInteger(key, nbtItem.getInteger(key));
            });
        }
    }
}