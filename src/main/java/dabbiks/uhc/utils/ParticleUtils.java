package dabbiks.uhc.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class ParticleUtils {

    public static void spawn(Location loc, Particle particle, int count, double offset, double speed) {
        World world = loc.getWorld();
        if (world != null) {
            world.spawnParticle(particle, loc, count, offset, offset, offset, speed);
        }
    }

    public static void spawnColored(Location loc, Color color, float size) {
        World world = loc.getWorld();
        if (world != null) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(color, size);
            world.spawnParticle(Particle.TRAIL, loc, 1, 0, 0, 0, 0, dustOptions);
        }
    }

    public static void drawLine(Location start, Location end, Particle particle, double step, int count) {
        if (start.getWorld() != end.getWorld()) return;

        Vector vector = end.toVector().subtract(start.toVector());
        double length = vector.length();
        vector.normalize();

        for (double d = 0; d < length; d += step) {
            spawn(start.clone().add(vector.clone().multiply(d)), particle, count, 0, 0);
        }
    }

    public static void drawCircle(Location center, double radius, Particle particle, int points) {
        double increment = (2 * Math.PI) / points;
        for (int i = 0; i < points; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            spawn(new Location(center.getWorld(), x, center.getY(), z), particle, 1, 0, 0);
        }
    }

    public static void drawHelix(Location center, double radius, double height, Particle particle, double step) {
        double y = 0;
        for (double angle = 0; y < height; angle += step / 2) {
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            spawn(center.clone().add(x, y, z), particle, 1, 0, 0);
            y += step / 10;
        }
    }

    public static void drawSphere(Location center, double radius, Particle particle, int density) {
        for (int i = 0; i < density; i++) {
            double u = Math.random();
            double v = Math.random();
            double theta = 2 * Math.PI * u;
            double phi = Math.acos(2 * v - 1);

            double x = center.getX() + (radius * Math.sin(phi) * Math.cos(theta));
            double y = center.getY() + (radius * Math.sin(phi) * Math.sin(theta));
            double z = center.getZ() + (radius * Math.cos(phi));

            spawn(new Location(center.getWorld(), x, y, z), particle, 1, 0, 0);
        }
    }

    public static void drawCone(Location center, double height, double radius, Particle particle, int particlesPerCircle) {
        for (double y = 0; y < height; y += 0.5) {
            double currentRadius = radius * (1 - (y / height));
            drawCircle(center.clone().add(0, y, 0), currentRadius, particle, particlesPerCircle);
        }
    }

    public static void spawnParticleFollower(Plugin plugin, Location start, Entity target, Particle particle, Consumer<Entity> onHit) {
        new BukkitRunnable() {
            Location current = start.clone();
            double speed = 0.5;
            final double acceleration = 0.05;

            @Override
            public void run() {
                if (!target.isValid() || target.isDead() || target.getWorld() != current.getWorld()) {
                    this.cancel();
                    return;
                }

                Location targetLoc = target.getLocation().add(0, 1.0, 0);
                double distance = current.distance(targetLoc);

                if (distance < 0.5 || distance < speed) {
                    if (onHit != null) {
                        onHit.accept(target);
                    }
                    this.cancel();
                    return;
                }

                Vector direction = targetLoc.toVector().subtract(current.toVector()).normalize().multiply(speed);
                current.add(direction);

                spawn(current, particle, 1, 0, 0);

                speed += acceleration;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}