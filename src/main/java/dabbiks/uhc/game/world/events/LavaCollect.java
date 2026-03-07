package dabbiks.uhc.game.world.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

public class LavaCollect implements Listener {

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (event.getBlockClicked().getType() == Material.LAVA) {
            event.setCancelled(true);
            event.getBlockClicked().setType(Material.AIR);

            Player player = event.getPlayer();
            ItemStack powder = new ItemStack(Material.BLAZE_POWDER, 2);

            player.getInventory().addItem(powder).values().forEach(item ->
                    player.getWorld().dropItemNaturally(player.getLocation(), item)
            );
        }
    }
}
