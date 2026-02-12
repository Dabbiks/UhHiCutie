package dabbiks.uhc.game.gameplay.damage.handlers.enchants;

import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantManager;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import dabbiks.uhc.utils.ParticleUtils;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import static dabbiks.uhc.Main.playerU;
import static dabbiks.uhc.Main.plugin;

public class MeleeEnchantHandler {

    private final EnchantManager enchantManager;

    public MeleeEnchantHandler() {
        this.enchantManager = new EnchantManager();
    }

    public double handle(Player damager, LivingEntity victim, double damage,
                         EntityDamageByEntityEvent event, EnchantType type) {

        ItemStack item = damager.getInventory().getItemInMainHand();
        NBTItem nbtItem = new NBTItem(item);

        int level = enchantManager.getItemLevel(nbtItem, type);
        if (level == 0) return damage;

        switch (type) {
            case SHARPNESS -> damage += sharpness(level, event.isCritical());
            case SUNDER -> damage += sunder(level, event.isCritical());
            case HASTE -> haste(level, damager);
            case SLUDGE -> sludge(level, victim);
            case POISON -> poison(level, victim);
            case IGNITE -> ignite(level, victim);
            case SHATTER -> damage += level;

            case IRON_FEET -> damage -= iron_feet(level, damage);
            case LEAPING -> leaping(level, damager);
            case UNSTABLE_CORE ->unstable_core(damager, victim, damage);
        }

        return damage;
    }

    private double sharpness(int level, boolean isCritical) {
        return isCritical ? 0 : level * 0.75;
    }

    private double sunder(int level, boolean isCritical) {
        return isCritical ? level : 0;
    }

    private void haste(int level, Player damager) {
        damager.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 20 * level, 0, false, true));
    }

    private void sludge(int level, LivingEntity victim) {
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * level, 0, false, true));
        if (!(victim instanceof Player player)) return;
        SessionData sessionData = SessionDataManager.getData(victim.getUniqueId());
        sessionData.addTagFor(player, SessionTags.SLUDGE, (long) (level * 1.5));
    }

    private void poison(int level, LivingEntity victim) {
        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * level + 1, 0, false, true));
    }

    private void ignite(int level, LivingEntity victim) {
        victim.setFireTicks(40 * level);
    }

    private double iron_feet(int level, double damage) {
        return (damage / 4) * level + 1;
    }

    private void leaping(int level, Player damager) {
        double x = damager.getVelocity().getX() / 3;
        double z = damager.getVelocity().getZ() / 3;
        damager.setVelocity(new Vector(x, 0.75 * level, z));
    }

    private void unstable_core(Player damager, LivingEntity victim, double damage) {
        for (Entity entity : victim.getNearbyEntities(8, 5, 8)) {
            if (!(entity instanceof LivingEntity) || entity == victim) continue;
            if (entity instanceof Player player && TeamUtils.isTargetAlly(damager, player)) continue;

            ParticleUtils.spawnParticleFollower(
                    plugin,
                    victim.getEyeLocation(),
                    entity,
                    Particle.TRIAL_OMEN,
                    target -> {
                        ((LivingEntity) entity).damage(damage / 2, DamageSource.builder(DamageType.MAGIC).build());
                        if (!(entity instanceof Player)) return;
                        playerU.addHealth(damager, 3);
                    }
            );
        }
    }
}
