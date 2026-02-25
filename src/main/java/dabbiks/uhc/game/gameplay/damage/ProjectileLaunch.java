package dabbiks.uhc.game.gameplay.damage;

import dabbiks.uhc.game.gameplay.damage.handlers.ProjectileHandler;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ProjectileLaunch implements Listener {

    private final ProjectileHandler projectileHandler = new ProjectileHandler();

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;

        NBTItem nbt = new NBTItem(player.getInventory().getItemInMainHand());

        if (nbt.hasTag(EnchantType.MAGIC_ARROW.name())) {
            event.setCancelled(true);
            event.getEntity().remove();

            int level = nbt.getInteger(EnchantType.MAGIC_ARROW.name());
            Location startLoc = player.getEyeLocation();
            Vector direction = startLoc.getDirection().normalize().multiply(2.0);
            JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());

            new BukkitRunnable() {
                int distance = 0;
                final Location currentLoc = startLoc.clone();

                @Override
                public void run() {
                    if (distance >= 50 || !currentLoc.getBlock().isPassable()) {
                        this.cancel();
                        return;
                    }

                    currentLoc.add(direction);
                    currentLoc.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, currentLoc, 3, 0.1, 0.1, 0.1, 0);

                    for (Entity entity : currentLoc.getWorld().getNearbyEntities(currentLoc, 0.5, 0.5, 0.5)) {
                        if (entity instanceof LivingEntity victim && !entity.equals(player)) {
                            victim.damage(level * 1.5, player);

                            new BukkitRunnable() {
                                int freezeDuration = 0;
                                @Override
                                public void run() {
                                    if (freezeDuration >= 80 || victim.isDead()) {
                                        this.cancel();
                                        return;
                                    }
                                    victim.setFreezeTicks(100);
                                    freezeDuration++;
                                }
                            }.runTaskTimer(plugin, 0L, 1L);

                            this.cancel();
                            return;
                        }
                    }
                    distance++;
                }
            }.runTaskTimer(plugin, 0L, 1L);

            return;
        }

        projectileHandler.handle(player, event.getEntity());
    }
}