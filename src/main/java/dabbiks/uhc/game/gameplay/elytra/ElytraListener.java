package dabbiks.uhc.game.gameplay.elytra;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ElytraListener implements Listener {

    private final ChestplateManager manager;
    private final int COOLDOWN_TICKS = 200;
    private final double BOOST_MULTIPLIER = 1.5;

    public ElytraListener(ChestplateManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onRocketUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.FIREWORK_ROCKET) return;

        Player player = event.getPlayer();
        if (player.hasCooldown(Material.FIREWORK_ROCKET)) return;

        event.setCancelled(true);
        player.setCooldown(Material.FIREWORK_ROCKET, COOLDOWN_TICKS);

        ItemStack currentChest = player.getInventory().getChestplate();
        if (currentChest != null && currentChest.getType() != Material.ELYTRA) {
            manager.saveChestplate(player.getUniqueId(), currentChest.clone());
        }

        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
        player.setGliding(true);
        player.setVelocity(player.getLocation().getDirection().multiply(BOOST_MULTIPLIER));
    }

    @EventHandler
    public void onLanding(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.isGliding()) return;

        if (manager.hasSavedChestplate(player.getUniqueId())) {
            ItemStack saved = manager.getAndRemoveChestplate(player.getUniqueId());
            player.getInventory().setChestplate(saved);
        } else if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.ELYTRA) {
            player.getInventory().setChestplate(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArmorClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!manager.hasSavedChestplate(player.getUniqueId())) return;

        if (event.getSlotType() == InventoryType.SlotType.ARMOR && event.getSlot() == 38) {
            event.setCancelled(true);
        }

        if (event.isShiftClick() && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ELYTRA) {
            event.setCancelled(true);
        }
    }
}