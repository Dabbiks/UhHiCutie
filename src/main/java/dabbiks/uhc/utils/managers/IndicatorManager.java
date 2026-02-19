package dabbiks.uhc.utils.managers;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.Random;

import static dabbiks.uhc.Main.plugin;
import static dabbiks.uhc.Main.symbolU;

public class IndicatorManager {

    private final DecimalFormat format = new DecimalFormat("0.0");
    private final Random random = new Random();

    private final int TELEPORT_PERIOD = 5;
    private final int DAMAGE_LIFETIME = 30;
    private final int HEAL_LIFETIME = 20;

    public void spawnDamageIndicator(Entity entity, double damage, boolean isCritical) {
        if (!entity.isValid()) return;

        Location base = entity.getLocation().clone().add(randomOffsetXZ(), 1, randomOffsetXZ());

        TextDisplay display = entity.getWorld().spawn(base, TextDisplay.class);
        display.setBillboard(Display.Billboard.CENTER);
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        display.setSeeThrough(false);
        display.setShadowed(true);
        display.setGlowing(true);

        String text = isCritical
                ? symbolU.CRITICAL_DAMAGE + " " + format.format(damage)
                : "§f" + format.format(damage);

        display.setText(text);

        if (damage == 0) display.setText("§9Blok");

        display.setInterpolationDelay(0);
        display.setInterpolationDuration(TELEPORT_PERIOD);
        display.setTeleportDuration(TELEPORT_PERIOD);

        Vector velocity = randomXZVector().multiply(0.05).add(new Vector(0, 0.05, 0));
        Vector gravity = new Vector(0, -0.015, 0); // mocniejszy spadek
        Vector currentVelocity = velocity.clone();

        new BukkitRunnable() {
            int ticks = 0;
            Location currentLoc = base.clone();

            @Override
            public void run() {
                if (!display.isValid()) {
                    cancel();
                    return;
                }

                currentVelocity.add(gravity);
                currentLoc.add(currentVelocity);

                if (ticks % TELEPORT_PERIOD == 0) {
                    display.teleport(currentLoc);
                }

                if (++ticks >= DAMAGE_LIFETIME) {
                    display.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void spawnHealIndicator(Player player, double heal) {
        if (!player.isOnline() || !player.isValid()) return;

        Location base = player.getLocation().clone().add(0, 2.2, 0);

        TextDisplay display = player.getWorld().spawn(base, TextDisplay.class);
        display.setBillboard(Display.Billboard.CENTER);
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        display.setSeeThrough(false);
        display.setShadowed(true);
        display.setGlowing(true);

        display.setText(symbolU.REGENERATION + " " + format.format(Math.abs(heal)));

        display.setInterpolationDelay(0);
        display.setInterpolationDuration(TELEPORT_PERIOD);
        display.setTeleportDuration(TELEPORT_PERIOD);

        Vector velocity = new Vector(0, 0.03, 0);

        new BukkitRunnable() {
            int ticks = 0;
            Location currentLoc = base.clone();

            @Override
            public void run() {
                if (!display.isValid()) {
                    cancel();
                    return;
                }

                currentLoc.add(velocity);

                if (ticks % TELEPORT_PERIOD == 0) {
                    display.teleport(currentLoc);
                }

                if (++ticks >= HEAL_LIFETIME) {
                    display.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private Vector randomXZVector() {
        double angle = random.nextDouble() * 2 * Math.PI;
        return new Vector(Math.cos(angle), 0, Math.sin(angle));
    }

    private double randomOffsetXZ() {
        return (random.nextDouble() - 0.5) * 0.6; // ±0.3
    }
}
