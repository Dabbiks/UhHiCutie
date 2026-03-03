package dabbiks.uhc.game.world.events;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.util.Vector;

public class ItemDrop implements Listener {

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();

        item.setGravity(false);
        item.setUnlimitedLifetime(true);

        Vector velocity = item.getVelocity();
        item.setVelocity(velocity.multiply(0.25));
    }

}
