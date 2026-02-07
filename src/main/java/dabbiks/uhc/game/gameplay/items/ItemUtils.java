package dabbiks.uhc.game.gameplay.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.Weapon;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static io.papermc.paper.registry.RegistryKey.DAMAGE_TYPE;

public class ItemUtils {

    public void addParryingComponent(ItemStack item) {
        RegistryKey registryKey = DAMAGE_TYPE.typedKey("player_attack").registryKey();
        RegistryKeySet<DamageType> keySet = RegistrySet.keySet(registryKey, DAMAGE_TYPE.typedKey("player_attack"));
        DamageReduction damageReduction = DamageReduction.damageReduction().type(keySet).horizontalBlockingAngle(30f).factor(1f).build();
        BlocksAttacks blocksAttacks = BlocksAttacks.blocksAttacks().itemDamage(ItemDamageFunction.itemDamageFunction().base(-4f).build()).addDamageReduction(damageReduction).build();
        item.setData(DataComponentTypes.BLOCKS_ATTACKS, blocksAttacks);
        Weapon baseWeapon = Weapon.weapon().build();
        item.setData(DataComponentTypes.WEAPON, baseWeapon);
    }

    public void setEquippableTexture(ItemStack item, String slot, String assetId) {
        if (item == null || item.getType().isAir()) return;
        EquipmentSlot eqSlot = EquipmentSlot.HEAD;
        if (slot.equals("head")) eqSlot = EquipmentSlot.HEAD;
        if (slot.equals("chest")) eqSlot = EquipmentSlot.CHEST;
        if (slot.equals("legs")) eqSlot = EquipmentSlot.LEGS;
        if (slot.equals("feet")) eqSlot = EquipmentSlot.FEET;
        Equippable equippable = Equippable.equippable(eqSlot).assetId(Key.key(assetId)).build();
        item.setData(DataComponentTypes.EQUIPPABLE, equippable);
    }

}
