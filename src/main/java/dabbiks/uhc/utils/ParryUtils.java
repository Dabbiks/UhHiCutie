package dabbiks.uhc.utils;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.datacomponent.item.Weapon;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;

import static io.papermc.paper.registry.RegistryKey.DAMAGE_TYPE;

public class ParryUtils {

    public ItemStack addParryingComponent(ItemStack item) {
        RegistryKey registryKey = DAMAGE_TYPE.typedKey("player_attack").registryKey();
        RegistryKeySet<DamageType> keySet = RegistrySet.keySet(registryKey, DAMAGE_TYPE.typedKey("player_attack"));
        DamageReduction damageReduction = DamageReduction.damageReduction().type(keySet).horizontalBlockingAngle(30f).factor(1f).build();
        BlocksAttacks blocksAttacks = BlocksAttacks.blocksAttacks().itemDamage(ItemDamageFunction.itemDamageFunction().base(-4f).build()).addDamageReduction(damageReduction).build();
        item.setData(DataComponentTypes.BLOCKS_ATTACKS, blocksAttacks);
        Weapon baseWeapon = Weapon.weapon().build();
        item.setData(DataComponentTypes.WEAPON, baseWeapon);
        return item;
    }

}
