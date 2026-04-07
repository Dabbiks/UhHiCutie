package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.gameplay.damage.handlers.BoomerangHandler;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantManager;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantSlot;
import dabbiks.uhc.game.gameplay.items.data.perks.PerkType;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.tasks.Task;
import dabbiks.uhc.tasks.TaskManager;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
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

import java.util.HashMap;
import java.util.function.Function;

public class BoomerangFlightTask extends Task {

    private final Player thrower;
    private final ItemDisplay display;
    private final ItemStack boomerangItem;
    private final BoomerangHandler handler;
    private final EnchantManager enchantManager;

    private Location currentLocation;
    private final Vector direction;
    private Vector velocity;
    private double speed = 1.6;

    private int ticks = 0;
    private final int maxDistanceTicks;
    private boolean returning = false;
    private float rotationAngle = 0f;

    private boolean hasHit = false;
    private int returnTimer = -1;
    private boolean restoredDurability = false;

    private final boolean hasContactExplosion;
    private boolean hasExploded = false;

    public BoomerangFlightTask(Player thrower, ItemDisplay display, ItemStack boomerangItem) {
        this.thrower = thrower;
        this.display = display;
        this.boomerangItem = boomerangItem;
        this.handler = new BoomerangHandler();
        this.enchantManager = new EnchantManager();

        this.currentLocation = thrower.getEyeLocation().add(thrower.getLocation().getDirection().multiply(0.5));
        this.direction = thrower.getLocation().getDirection().normalize();
        this.velocity = direction.clone().multiply(speed);

        display.teleport(currentLocation);
        display.setTeleportDuration(3);
        display.setInterpolationDuration(3);
        display.setInterpolationDelay(0);

        Integer isBoomerangValue = NBT.get(boomerangItem, (Function<ReadableItemNBT, Integer>) nbt -> {
            return nbt.hasTag(EnchantSlot.BOOMERANG.name()) ? nbt.getInteger(EnchantSlot.BOOMERANG.name()) : 0;
        });
        boolean isBoomerang = isBoomerangValue != null && isBoomerangValue == 1;

        Integer ceValue = NBT.get(boomerangItem, (Function<ReadableItemNBT, Integer>) nbt -> {
            return nbt.hasTag(PerkType.CONTACT_EXPLOSION.name()) ? nbt.getInteger(PerkType.CONTACT_EXPLOSION.name()) : 0;
        });
        this.hasContactExplosion = ceValue != null && ceValue == 1;

        this.maxDistanceTicks = isBoomerang ? 30 : 15;
    }

    @Override
    protected void tick() {
        if (display.isDead() || !thrower.isOnline()) {
            cleanup();
            return;
        }

        if (returnTimer > 0) {
            returnTimer--;
            if (returnTimer == 0) {
                finish(10, true);
                return;
            }
        }

        if (!returning) {
            speed -= (1.3 / maxDistanceTicks);
            if (speed <= 0.1) speed = 0.1;
            velocity = direction.clone().multiply(speed);

            if (ticks >= maxDistanceTicks) returning = true;
        } else {
            Vector toPlayer = thrower.getEyeLocation().toVector().subtract(currentLocation.toVector());
            double dist = toPlayer.length();

            if (dist < 2.0) {
                finish(10, true);
                return;
            }

            speed += 0.12;
            if (speed > 1.8) speed = 1.8;

            double pull = Math.max(0.4, 3.0 / Math.max(1.0, dist));
            velocity = velocity.add(toPlayer.normalize().multiply(pull)).normalize().multiply(speed);
        }

        currentLocation.add(velocity);

        if (currentLocation.getBlock().getType().isSolid()) {
            if (hasContactExplosion && !hasExploded && !returning) {
                currentLocation.getWorld().createExplosion(currentLocation, 5.0f, false, true, thrower);
                hasExploded = true;
            }

            if (!returning) {
                returning = true;
            } else {
                currentLocation.subtract(velocity);
                finish(20, false);
                return;
            }
        }

        rotationAngle += 0.8f;

        display.setInterpolationDelay(0);
        display.setInterpolationDuration(3);

        Transformation transform = display.getTransformation();
        transform.getLeftRotation().rotationX((float) Math.PI / 2f).rotateZ(rotationAngle);
        display.setTransformation(transform);

        display.setTeleportDuration(3);
        display.teleport(currentLocation);

        for (Entity entity : currentLocation.getWorld().getNearbyEntities(currentLocation, 1.2, 1.2, 1.2)) {
            if (!(entity instanceof LivingEntity target) || entity.equals(thrower)) continue;
            if (target instanceof Player targetPlayer && TeamUtils.isPlayerAlly(thrower, targetPlayer)) continue;

            boolean killed = handler.handleHit(thrower, target, boomerangItem, returning);
            thrower.playSound(thrower.getLocation(), Sound.ITEM_TRIDENT_HIT, 0.3f, 1.6f);

            if (killed && enchantManager.getItemLevel(boomerangItem, EnchantType.EXECUTIONER) > 0) {
                restoredDurability = true;
            }

            if (hasHit) continue;
            hasHit = true;

            if (enchantManager.getItemLevel(boomerangItem, EnchantType.RETURN) > 0 && returnTimer == -1) {
                returnTimer = 10;
            }
        }

        ticks++;
    }

    private void finish(int damageCost, boolean giveToPlayer) {
        ItemMeta meta = boomerangItem.getItemMeta();

        if (restoredDurability) {
            damageCost = 0;
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(0);
                boomerangItem.setItemMeta(meta);
            }
        } else if (giveToPlayer) {
            int durabilityLevel = enchantManager.getItemLevel(boomerangItem, EnchantType.BOOMERANG_DURABILITY);
            if (durabilityLevel > 0 && Math.random() < (durabilityLevel * 0.15)) {
                damageCost = 0;
            }
        }

        if (damageCost > 0 && meta instanceof Damageable damageable) {
            damageable.setDamage(damageable.getDamage() + damageCost);
            boomerangItem.setItemMeta(meta);

            if (damageable.getDamage() >= boomerangItem.getType().getMaxDurability()) {
                thrower.playSound(thrower.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                cleanup();
                return;
            }
        }

        if (giveToPlayer) {
            thrower.playSound(thrower.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.6f, 1f);
            HashMap<Integer, ItemStack> leftover = thrower.getInventory().addItem(boomerangItem);
            if (!leftover.isEmpty()) {
                thrower.getWorld().dropItemNaturally(thrower.getLocation(), boomerangItem);
            }
        } else {
            thrower.getWorld().dropItemNaturally(currentLocation, boomerangItem);
        }

        cleanup();
    }

    private void cleanup() {
        display.remove();
        TaskManager.removeTask(this);
    }
}