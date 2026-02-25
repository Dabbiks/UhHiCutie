package dabbiks.uhc.lobby;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.gameplay.damage.handlers.CriticalHitHandler;
import dabbiks.uhc.game.gameplay.damage.handlers.ParryingHandler;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.tasks.tasks.PvpSwordTask;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

import static dabbiks.uhc.Main.*;

public class SpawnProtector implements Listener {

    CriticalHitHandler criticalHitHandler = new CriticalHitHandler();
    ParryingHandler parryingHandler = new ParryingHandler();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("*")) {return;}
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasPermission("*")) {return;}
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer().hasPermission("*")) {return;}
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().hasPermission("*")) { return; }
        if (event.getWhoClicked().getWorld().getName().equalsIgnoreCase("world")) {
            event.setCancelled(true);
        }    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked().hasPermission("*")) {return;}
        if (event.getWhoClicked().getWorld().getName().equalsIgnoreCase("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().hasPermission("*")) {return;}
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().hasPermission("*")) {return;}
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!event.getEntity().getWorld().equals(Bukkit.getWorld("world"))) return;
        if (!(event instanceof EntityDamageByEntityEvent entityEvent)) return;
        if (!(entityEvent.getDamager() instanceof Player damager)) return;
        if (!(entityEvent.getEntity() instanceof Player victim)) return;
        if (!PvpSwordTask.canFight(damager)) return;
        if (!PvpSwordTask.canFight(victim)) return;
        if (damager.getInventory().getHeldItemSlot() != 4) return;

        final double baseDamage = event.getDamage();
        double damage = baseDamage;

        if (parryingHandler.handle(victim, entityEvent)) return;
        damage += criticalHitHandler.handle(damager, baseDamage, entityEvent.isCritical());

        double totalDamage = damage;

        if (totalDamage >= victim.getHealth()) {
            event.setCancelled(true);
            victim.setGameMode(GameMode.SPECTATOR);
            victim.teleport(new Location(victim.getWorld(),0.5f, 100, 0.5f));
            playerU.addHealth(victim, 100);
            playerU.addHealth(damager, 100);
            return;
        }

        entityEvent.setDamage(damage);
        if (entityEvent.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) entityEvent.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        if (entityEvent.isApplicable(EntityDamageEvent.DamageModifier.ABSORPTION)) entityEvent.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, 0);

        indicatorManager.spawnDamageIndicator(victim, totalDamage, entityEvent.isCritical());
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!event.getDamager().getWorld().equals(Bukkit.getWorld("world"))) { return; }
        event.setCancelled(true);
    }

    @EventHandler
    public void onFireworkDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework && stateU.getGameState() != GameState.IN_GAME) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSaturationLose(FoodLevelChangeEvent event) {
        if (!event.getEntity().getWorld().equals(Bukkit.getWorld("world"))) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        if (!event.getPlayer().getWorld().getName().equals("world")) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        if (player.getWorld() != Bukkit.getWorld("world")) return;
        event.setCancelled(true);
        event.getItem().remove();
    }


}