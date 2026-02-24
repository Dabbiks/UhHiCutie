package dabbiks.uhc.player.events;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemDeconstructor;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Grinding implements Listener {

    @EventHandler
    public void onGrind(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) return;
        if (block.getType() != Material.GRINDSTONE) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        NBTItem nbtItem = new NBTItem(item);

        if (nbtItem.hasTag(ItemTags.IS_ENCHANTED.name())) {
            ItemInstance instance = new ItemDeconstructor(item).deconstruct();
            instance.setEnchants(null);
            player.getInventory().setItemInMainHand(new ItemBuilder(instance).build());
        }
    }

}
