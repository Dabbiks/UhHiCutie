package dabbiks.uhc.game.gameplay.damage.listeners;

import dabbiks.uhc.game.configs.LobbyConfig;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.attribute.Attribute;

import java.util.concurrent.ThreadLocalRandom;

public class CustomMobListener implements Listener {

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.WARDEN) {
            Warden warden = (Warden) event.getEntity();
            double maxHealth = 125.0 * LobbyConfig.teamSize;
            warden.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
            warden.setHealth(maxHealth);
        }
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.WITHER_SKELETON) {
            event.getDrops().clear();

            int coalCount = ThreadLocalRandom.current().nextInt(1, 4);
            int goldCount = ThreadLocalRandom.current().nextInt(1, 4);

            event.getDrops().add(new ItemStack(Material.COAL, coalCount));
            event.getDrops().add(new ItemStack(Material.GOLD_INGOT, goldCount));

            if (ThreadLocalRandom.current().nextDouble() <= 0.50) {
                event.getDrops().add(new ItemStack(Material.NETHERITE_SCRAP, 1));
            }
            if (ThreadLocalRandom.current().nextDouble() <= 0.25) {
                event.getDrops().add(new ItemStack(Material.NETHERITE_SCRAP, 1));
            }
        } else if (event.getEntityType() == EntityType.WARDEN) {
            event.getDrops().clear();
            event.setDroppedExp(300);

            int obsidianCount = ThreadLocalRandom.current().nextInt(5, 11);

            event.getDrops().add(new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1));
            event.getDrops().add(new ItemStack(Material.OBSIDIAN, obsidianCount));
        }
    }
}