package dabbiks.uhc.game.gameplay.mobs;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static dabbiks.uhc.Main.symbolU;

public class NautilusHoverManager extends PacketListenerAbstract implements Listener {

    private final Map<UUID, Long> lastClicks = new ConcurrentHashMap<>();
    private final Map<UUID, Long> dashCooldown = new ConcurrentHashMap<>();
    private final String itemName = symbolU.MOUSE_RIGHT + " Jedź do przodu";

    public NautilusHoverManager(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        PacketEvents.getAPI().getEventManager().registerListener(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isInsideVehicle()) {
                        Entity vehicle = player.getVehicle();
                        if (vehicle != null && vehicle.getType().name().equals("NAUTILUS")) {

                            if (vehicle instanceof Mob) {
                                Mob mob = (Mob) vehicle;
                                mob.setAI(true);
                                mob.setAware(false);
                            }
                            vehicle.setGravity(false);

                            Location loc = vehicle.getLocation();
                            double groundY = loc.getWorld().getMinHeight();
                            for (int y = loc.getBlockY(); y > loc.getWorld().getMinHeight(); y--) {
                                if (loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ()).getType().isSolid()) {
                                    groundY = y + 1.0;
                                    break;
                                }
                            }

                            Vector velocity = new Vector(0, 0, 0);
                            Long lastClick = lastClicks.get(player.getUniqueId());
                            Vector direction = player.getLocation().getDirection().setY(0);

                            if (direction.lengthSquared() > 0.0001) {
                                direction.normalize();
                                if (lastClick != null && now - lastClick < 350) {
                                    double speed = 0.54;

                                    Long dashTime = dashCooldown.get(player.getUniqueId());
                                    if (dashTime != null && now - dashTime < 500) {
                                        speed *= 2.5;
                                        loc.getWorld().spawnParticle(Particle.BUBBLE_POP, loc, 5, 0.2, 0.2, 0.2, 0.1);
                                    }

                                    velocity.add(direction.clone().multiply(speed));

                                    for (double d = 0.5; d <= 2.0; d += 0.5) {
                                        Location pLoc = loc.clone().subtract(direction.clone().multiply(d)).add(0, 0.5, 0);
                                        loc.getWorld().spawnParticle(Particle.BUBBLE, pLoc, 2, 0.1, 0.1, 0.1, 0);
                                    }
                                }
                            }

                            double targetY = groundY + 1.25;
                            double diffY = targetY - loc.getY();
                            velocity.setY(Math.max(-0.5, Math.min(0.5, diffY * 0.25)));

                            vehicle.setRotation(player.getLocation().getYaw(), 0);
                            vehicle.setVelocity(velocity);
                            vehicle.setFallDistance(0);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    @EventHandler
    public void onMount(EntityMountEvent event) {
        if (event.getEntity() instanceof Player && event.getMount().getType().name().equals("NAUTILUS")) {
            Player p = (Player) event.getEntity();
            ItemStack shell = new ItemStack(Material.NAUTILUS_SHELL);
            ItemMeta meta = shell.getItemMeta();
            meta.setDisplayName(itemName);
            shell.setItemMeta(meta);
            p.getInventory().setItem(5, shell);
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            ItemStack item = p.getInventory().getItem(5);
            if (item != null && item.getType() == Material.NAUTILUS_SHELL && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(itemName)) {
                p.getInventory().setItem(5, null);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = event.getPlayer();
            ItemStack item = p.getInventory().getItemInMainHand();
            if (item.getType() == Material.NAUTILUS_SHELL && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(itemName)) {
                lastClicks.put(p.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            WrapperPlayClientSteerVehicle wrapper = new WrapperPlayClientSteerVehicle(event);
            if (wrapper.isJump() && event.getPlayer() instanceof Player) {
                Player p = (Player) event.getPlayer();
                if (p.isInsideVehicle() && p.getVehicle().getType().name().equals("NAUTILUS")) {
                    long now = System.currentTimeMillis();
                    Long lastDash = dashCooldown.get(p.getUniqueId());
                    if (lastDash == null || now - lastDash > 2000) {
                        dashCooldown.put(p.getUniqueId(), now);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().getType().name().equals("NAUTILUS")) {
            EntityDamageEvent.DamageCause c = event.getCause();
            if (c == EntityDamageEvent.DamageCause.DRYOUT || c == EntityDamageEvent.DamageCause.SUFFOCATION ||
                    c == EntityDamageEvent.DamageCause.DROWNING || c == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }
}