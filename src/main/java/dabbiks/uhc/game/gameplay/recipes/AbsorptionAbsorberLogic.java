package dabbiks.uhc.game.gameplay.recipes;

import dabbiks.uhc.Main;
import dabbiks.uhc.utils.ParticleUtils;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class AbsorptionAbsorberLogic {

    public static void handle(LivingEntity damager, Player victim) {
        ItemStack chestplate = victim.getInventory().getChestplate();
        if (chestplate == null || !chestplate.hasItemMeta()) return;

        Boolean hasPerk = NBT.get(chestplate, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.hasTag("ABSORPTION_ABSORBER"));

        if (hasPerk != null && hasPerk) {
            if (damager instanceof Player attacker) {
                double absorption = attacker.getAbsorptionAmount();
                if (absorption > 0) {
                    attacker.setAbsorptionAmount(0);
                    double healAmount = absorption / 2.0;
                    double maxHealth = victim.getAttribute(Attribute.MAX_HEALTH).getValue();
                    victim.setHealth(Math.min(maxHealth, victim.getHealth() + healAmount));

                    ParticleUtils.spawnParticleFollower(Main.plugin, attacker.getLocation().add(0, 1.0, 0), victim, Particle.HAPPY_VILLAGER, null);
                }
            }
        }
    }
}