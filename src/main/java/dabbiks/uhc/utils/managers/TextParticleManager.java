package dabbiks.uhc.utils.managers;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import static dabbiks.uhc.Main.plugin;

public class TextParticleManager {

    public void spawnParticle(Location loc, String text, Vector velocityPerTick, int lifespanTicks) {
        int growDuration = lifespanTicks / 4;
        int shrinkStartTick = (lifespanTicks * 4) / 5;
        int shrinkDuration = lifespanTicks - shrinkStartTick;

        TextDisplay display = loc.getWorld().spawn(loc, TextDisplay.class, entity -> {
            entity.setText(text);
            entity.setBillboard(TextDisplay.Billboard.CENTER);
            entity.setDefaultBackground(false);
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            entity.setShadowed(true);
            entity.setGravity(false);
            entity.setInvulnerable(true);
            entity.setPersistent(false);
            entity.setTeleportDuration(lifespanTicks);

            Transformation t = entity.getTransformation();
            t.getScale().set(0.001f, 0.001f, 0.001f);
            entity.setTransformation(t);
        });

        Location targetLoc = loc.clone().add(
                velocityPerTick.getX() * lifespanTicks,
                velocityPerTick.getY() * lifespanTicks,
                velocityPerTick.getZ() * lifespanTicks
        );

        new BukkitRunnable() {
            @Override
            public void run() {
                if (display.isValid()) {
                    display.teleport(targetLoc);

                    Transformation t = display.getTransformation();
                    t.getScale().set(1.0f, 1.0f, 1.0f);
                    display.setInterpolationDuration(growDuration);
                    display.setInterpolationDelay(0);
                    display.setTransformation(t);
                }
            }
        }.runTaskLater(plugin, 1L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (display.isValid()) {
                    Transformation t = display.getTransformation();
                    t.getScale().set(0.001f, 0.001f, 0.001f);
                    display.setInterpolationDuration(shrinkDuration);
                    display.setInterpolationDelay(0);
                    display.setTransformation(t);
                }
            }
        }.runTaskLater(plugin, shrinkStartTick);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (display.isValid()) {
                    display.remove();
                }
            }
        }.runTaskLater(plugin, lifespanTicks + 1L);
    }
}