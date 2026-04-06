package dabbiks.uhc.game.gameplay.recipes;

import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.player.PlayerState;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static dabbiks.uhc.Main.*;

public class FlareListener implements Listener {

    @EventHandler
    public void onFlareUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getClickedBlock() != null && event.getClickedBlock().getType().isInteractable() && !player.isSneaking()) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.FIREWORK_ROCKET) return;

        if (!hasPerk(item)) return;

        event.setCancelled(true);

        item.setAmount(item.getAmount() - 1);

        fireworkU.instantExplode(player.getLocation(), Color.RED);
        fireworkU.spawnQuick(player.getLocation(), Color.WHITE);

        double minDistance = 200.0;
        Player nearest = null;

        for (Player target : playerListU.getPlayingPlayers()) {
            if (target.equals(player)) continue;
            if (!target.getWorld().equals(player.getWorld())) continue;

            if (stateU.getPlayerState(target) != PlayerState.ALIVE) continue;

            if (TeamUtils.isPlayerAlly(player, target)) continue;

            double distance = player.getLocation().distance(target.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = target;
            }
        }

        if (nearest != null) {
            String distanceStr = String.format("%.1f", minDistance).replace(",", ".");
            titleU.sendTitleToPlayer(player, "§c" + distanceStr, "§fNajbliższy gracz", 60);
        } else {
            titleU.sendTitleToPlayer(player, "§cBRAK", "§fNie znaleziono nikogo", 60);
        }
    }

    private boolean hasPerk(ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
        return true;
    }
}
