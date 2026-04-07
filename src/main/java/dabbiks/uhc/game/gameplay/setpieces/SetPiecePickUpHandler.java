package dabbiks.uhc.game.gameplay.setpieces;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static dabbiks.uhc.Main.soundU;

public class SetPiecePickUpHandler implements Listener {
    private final Map<UUID, UUID> interactionFromItemDisplay = new HashMap<>();
    public void addItemDisplay(ItemDisplay itemDisplay) {
        Location location = itemDisplay.getLocation();
        Interaction interaction = location.getWorld().spawn(location, Interaction.class);
        interaction.setInteractionHeight(0.7F);
        interaction.setInteractionWidth(0.7F);
        interactionFromItemDisplay.put(interaction.getUniqueId(),itemDisplay.getUniqueId());
    }
    @EventHandler
    public void clickOnInteraction(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Interaction interaction)) return;
        UUID uuid = interaction.getUniqueId();
        if (!interactionFromItemDisplay.containsKey(uuid)) return;
        ItemDisplay itemDisplay = (ItemDisplay) Bukkit.getEntity(interactionFromItemDisplay.get(uuid));
        if (itemDisplay == null) return;
        Location location = itemDisplay.getLocation();
        ItemStack item = itemDisplay.getItemStack();
        location.getWorld().dropItem(location,item);
        Block block = location.clone().add(0,-0.5,0).getBlock();
        Sound blockSound =  block.getSoundGroup().getBreakSound();
        soundU.playSoundAtLocation(location,Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM,1,1);
        soundU.playSoundAtLocation(location,blockSound,1,1);

        interactionFromItemDisplay.remove(uuid);
        itemDisplay.remove();
        interaction.remove();
    }
}