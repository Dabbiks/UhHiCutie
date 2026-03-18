package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.Main;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TagHandler {

    private static final Map<UUID, BukkitTask> pikemanTasks = new HashMap<>();

    public double handle(Entity damager, Entity victim, double damage) {
        double modifier = 0;

        Player realDamager = (damager instanceof Player && victim != null) ? (Player) damager : null;
        Player realVictim = (victim instanceof Player) ? (Player) victim : null;

        if (victim == null && damager instanceof Player) {
            realVictim = (Player) damager;
        }

        if (realDamager != null) {
            modifier += handleDamagerTags(realDamager, damage);
        }

        if (realVictim != null) {
            modifier += handleVictimTags(realVictim, damage);
        }

        return modifier;
    }

    private double handleDamagerTags(Player damager, double damage) {
        double bonus = 0;
        SessionData sessionData = SessionDataManager.getData(damager.getUniqueId());

        if (sessionData != null) {
            if (sessionData.hasTag(SessionTags.DEFAULT)) {
                ItemStack handItem = damager.getInventory().getItemInMainHand();
                if (handItem != null && handItem.getType().name().endsWith("_SWORD")) {
                    PersistentData pData = PersistentDataManager.getData(damager.getUniqueId());
                    if (pData != null) {
                        int level = pData.getChampionLevel("default");
                        bonus += level * 0.1;
                    }
                }
            }

            if (sessionData.hasTag(SessionTags.PIKEMAN)) {
                ItemStack handItem = damager.getInventory().getItemInMainHand();
                if (handItem != null && handItem.getType().name().endsWith("_SPEAR")) {
                    PersistentData pData = PersistentDataManager.getData(damager.getUniqueId());
                    if (pData != null) {
                        int level = pData.getChampionLevel("pikeman");
                        double speedPercent = 0.10 + (level * 0.02);
                        float defaultSpeed = 0.2f;

                        damager.setWalkSpeed((float) (defaultSpeed * (1.0 + speedPercent)));

                        if (pikemanTasks.containsKey(damager.getUniqueId())) {
                            pikemanTasks.get(damager.getUniqueId()).cancel();
                        }

                        BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
                            if (damager.isOnline()) {
                                damager.setWalkSpeed(defaultSpeed);
                            }
                            pikemanTasks.remove(damager.getUniqueId());
                        }, 40L);

                        pikemanTasks.put(damager.getUniqueId(), task);
                    }
                }
            }
        }
        return bonus;
    }

    private double handleVictimTags(Player victim, double damage) {
        double reduction = 0;
        SessionData sessionData = SessionDataManager.getData(victim.getUniqueId());

        if (sessionData != null && victim.getAbsorptionAmount() > 0) {
            if (sessionData.hasTag(SessionTags.BIG_ABSORPTION_REDUCTION)) {
                reduction -= damage * 0.25;
            } else if (sessionData.hasTag(SessionTags.ABSORPTION_REDUCTION)) {
                reduction -= damage * 0.125;
            }
        }
        return reduction;
    }

    private double playerToPlayerTags(Player damager, Player victim, double damage) {
        return 0;
    }

    private double playerToMobTags(Player damager, Entity victim, double damage) {
        return 0;
    }

    private double mobToPlayerTags(Entity damager, Player victim, double damage) {
        return 0;
    }
}