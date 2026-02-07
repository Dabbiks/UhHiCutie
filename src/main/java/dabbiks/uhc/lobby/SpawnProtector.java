package dabbiks.uhc.lobby;

import dabbiks.uhc.game.GameState;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
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
        event.setCancelled(true);
    }

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
        if (event instanceof EntityDamageByEntityEvent) return;
        if (event.getEntity().getWorld().equals(Bukkit.getWorld("world"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!event.getDamager().getWorld().equals(Bukkit.getWorld("world"))) { return; }

        if (event.getDamager() instanceof Player attacker) {
            if (!(event.getEntity() instanceof Player)) {
                if (!attacker.hasPermission("*")) {
                    event.setCancelled(true);
                }
                return;
            }

            Player victim = (Player) event.getEntity();

            if (attacker.getLocation().getBlockX() > 30.5 || attacker.getLocation().getBlockX() < 8.5 ||
                    attacker.getLocation().getBlockZ() > 17.5 || attacker.getLocation().getBlockZ() < -5.5) {
                event.setCancelled(true);
                return;
            }
            if (victim.getLocation().getBlockX() > 30.5 || victim.getLocation().getBlockX() < 8.5 ||
                    victim.getLocation().getBlockZ() > 17.5 || victim.getLocation().getBlockZ() < -5.5) {
                event.setCancelled(true);
                return;
            }
            if (attacker.getLocation().getBlockY() > 96 || attacker.getLocation().getBlockY() < 91 ||
                    victim.getLocation().getBlockY() > 96 || victim.getLocation().getBlockY() < 91) {
                event.setCancelled(true);
                return;
            }

            if (victim.isBlocking()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    ItemStack hand = victim.getInventory().getItemInMainHand();
                    hand.resetData(DataComponentTypes.BLOCKS_ATTACKS);
                    victim.setCooldown(hand, 30);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        itemU.addParryingComponent(hand);
                    }, 30L);
                });
                soundU.playSoundAtLocation(victim.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3f, (new Random().nextFloat(1.4f, 1.7f)));
                Vector direction = victim.getLocation().toVector().subtract(event.getDamager().getLocation().toVector());
                direction.setY(Math.max(direction.getY(), 0.1));
                direction.normalize().multiply(0.5);
                victim.setVelocity(direction);
            }

            indicatorManager.spawnDamageIndicator(victim, event.getFinalDamage(), event.isCritical());

            if (event.getFinalDamage() >= victim.getHealth()) {
                event.setCancelled(true);
                soundU.playSoundAtPlayer(victim, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1, 1);
                victim.teleport(new Location(Bukkit.getWorld("world"), 0.5, 100, 0.5));
//                playerU.addHealth(attacker, 100);
//                playerU.addHealth(victim, 100);
                if (stateU.getGameState() != GameState.IN_GAME) {
                    messageU.sendMessageToPlayers(playerListU.getWaitingPlayers(), "§e" + attacker.getName() + " §frozbroił §e" + victim.getName());
                }
            }
        } else {
            event.setCancelled(true);
        }
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