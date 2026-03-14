package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.gameplay.damage.handlers.BoomerangHandler;
import dabbiks.uhc.tasks.Task;
import dabbiks.uhc.tasks.TaskManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Collection;

public class BoomerangFlightTask extends Task {

    private final Player thrower;
    private final ItemDisplay display;
    private final ItemStack boomerangItem;
    private final BoomerangHandler handler;

    private Vector velocity;
    private int ticks;
    private final int maxDistanceTicks;
    private boolean returning;
    private float rotationAngle;

    public BoomerangFlightTask(Player thrower, ItemDisplay display, ItemStack boomerangItem) {
        this.thrower = thrower;
        this.display = display;
        this.boomerangItem = boomerangItem;
        this.handler = new BoomerangHandler();
        this.velocity = thrower.getLocation().getDirection().normalize().multiply(1.3);
        this.ticks = 0;
        this.returning = false;
        this.rotationAngle = 0f;

        display.setTeleportDuration(1);
        display.setInterpolationDuration(1);
        display.setInterpolationDelay(0);

        ItemMeta meta = boomerangItem.getItemMeta();
        int cmd = (meta != null && meta.hasCustomModelData()) ? meta.getCustomModelData() : 20001;

        this.maxDistanceTicks = switch (cmd) {
            case 20001 -> 10;
            case 20002 -> 15;
            case 20003 -> 20;
            case 20004 -> 25;
            case 20005 -> 30;
            case 20006 -> 38;
            default -> 15;
        };
    }

    @Override
    protected void tick() {
        if (display.isDead() || !thrower.isOnline()) {
            cleanup();
            return;
        }

        Location loc = display.getLocation();

        if (!returning) {
            velocity.setY(velocity.getY() - 0.005);
            if (ticks >= maxDistanceTicks) {
                returning = true;
            }
        } else {
            Vector toPlayer = thrower.getEyeLocation().toVector().subtract(loc.toVector());
            if (toPlayer.length() < 1.6) {
                finish(10, true);
                return;
            }
            velocity = velocity.add(toPlayer.normalize().multiply(0.25)).normalize().multiply(1.3);
        }

        loc.add(velocity);
        loc.setDirection(velocity);
        display.teleport(loc);

        rotationAngle += 0.9f;

        Quaternionf flatRotation = new Quaternionf(new AxisAngle4f((float) Math.toRadians(90), 1, 0, 0));
        Quaternionf spinRotation = new Quaternionf(new AxisAngle4f(rotationAngle, 0, 1, 0));

        Transformation transformation = display.getTransformation();
        transformation.getLeftRotation().set(flatRotation.mul(spinRotation));
        display.setTransformation(transformation);

        if (!loc.getBlock().isPassable()) {
            finish(20, false);
            return;
        }

        Collection<Entity> nearby = loc.getWorld().getNearbyEntities(loc, 0.7, 0.7, 0.7);
        for (Entity entity : nearby) {
            if (entity instanceof LivingEntity target && !entity.equals(thrower)) {
                handler.handleHit(thrower, target, boomerangItem);
            }
        }

        ticks++;
    }

    private void finish(int damageCost, boolean giveToPlayer) {
        ItemMeta meta = boomerangItem.getItemMeta();
        if (meta instanceof Damageable damageable) {
            damageable.setDamage(damageable.getDamage() + damageCost);
            boomerangItem.setItemMeta(meta);

            if (damageable.getDamage() >= boomerangItem.getType().getMaxDurability()) {
                thrower.playSound(thrower.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                cleanup();
                return;
            }
        }

        if (giveToPlayer) {
            if (thrower.getInventory().firstEmpty() != -1) {
                thrower.getInventory().addItem(boomerangItem);
            } else {
                thrower.getWorld().dropItemNaturally(thrower.getLocation(), boomerangItem);
            }
        } else {
            thrower.getWorld().dropItemNaturally(display.getLocation(), boomerangItem);
        }

        cleanup();
    }

    private void cleanup() {
        display.remove();
        TaskManager.removeTask(this);
    }
}