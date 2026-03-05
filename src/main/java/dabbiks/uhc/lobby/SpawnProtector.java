package dabbiks.uhc.lobby;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.gameplay.damage.handlers.CriticalHitHandler;
import dabbiks.uhc.game.gameplay.damage.handlers.ParryingHandler;
import dabbiks.uhc.tasks.tasks.PvpSwordTask;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;

import static dabbiks.uhc.Main.*;

public class SpawnProtector implements Listener {

    private final CriticalHitHandler criticalHitHandler = new CriticalHitHandler();
    private final ParryingHandler parryingHandler = new ParryingHandler();

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!event.getEntity().getWorld().getName().equalsIgnoreCase("world")) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        if (!(event instanceof EntityDamageByEntityEvent entityEvent) || !(entityEvent.getDamager() instanceof Player damager)) {
            event.setCancelled(true);
            return;
        }

        if (!PvpSwordTask.canFight(damager)) {
            event.setCancelled(true);
            return;
        }

        if (!PvpSwordTask.canFight(victim)) {
            event.setCancelled(true);
            return;
        }

        if (damager.getInventory().getHeldItemSlot() != 4) {
            event.setCancelled(true);
            return;
        }

        final double baseDamage = event.getDamage();
        double damage = baseDamage;

        if (parryingHandler.handle(victim, entityEvent)) {
            event.setCancelled(true);
            return;
        }

        damage += criticalHitHandler.handle(damager, baseDamage, entityEvent.isCritical());

        if (damage >= victim.getHealth()) {
            event.setCancelled(true);
            victim.teleport(new Location(victim.getWorld(), 0.5, 100, 0.5));
            playerU.addHealth(victim, 100);
            playerU.addHealth(damager, 100);
            return;
        }

        event.setDamage(damage);

        if (event.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        if (event.isApplicable(EntityDamageEvent.DamageModifier.ABSORPTION)) event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, 0);

        indicatorManager.spawnDamageIndicator(victim, damage, entityEvent.isCritical());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("*")) return;
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasPermission("*")) return;
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("*")) return;
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer().hasPermission("*")) return;
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (event.getPlayer().hasPermission("*")) return;
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player player)) return;
        if (player.hasPermission("*")) return;
        if (!player.getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onOtherEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (player.hasPermission("*")) return;
        if (!player.getWorld().getName().equalsIgnoreCase("world")) return;
        if (event.getEntity() instanceof Player) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().hasPermission("*")) return;
        if (!event.getWhoClicked().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked().hasPermission("*")) return;
        if (!event.getWhoClicked().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().hasPermission("*")) return;
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().hasPermission("*")) return;
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.getPlayer().hasPermission("*")) return;
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (event.getPlayer().hasPermission("*")) return;
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFireworkDamage(EntityDamageByEntityEvent event) {
        if (stateU.getGameState() == GameState.IN_GAME) return;
        if (!(event.getDamager() instanceof Firework)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onSaturationLose(FoodLevelChangeEvent event) {
        if (!event.getEntity().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) return;
        event.setCancelled(true);
        event.getItem().remove();
    }
}