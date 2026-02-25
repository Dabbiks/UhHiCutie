package dabbiks.uhc.player.events;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemDeconstructor;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

import static dabbiks.uhc.Main.soundU;

public class Grinding implements Listener {

    @EventHandler
    public void onGrind(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.GRINDSTONE) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;

        Boolean isEnchanted = NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.hasTag(ItemTags.IS_ENCHANTED.name()));

        if (Boolean.TRUE.equals(isEnchanted)) {
            event.setCancelled(true);

            ItemInstance instance = new ItemDeconstructor(item).deconstruct();
            instance.setEnchants(null);

            event.getPlayer().getInventory().setItemInMainHand(new ItemBuilder(instance).build());

            soundU.playSoundAtLocation(block.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1, 1);
        }
    }

}