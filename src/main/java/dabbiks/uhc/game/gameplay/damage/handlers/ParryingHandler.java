package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.gameplay.items.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

import static dabbiks.uhc.Main.plugin;
import static dabbiks.uhc.Main.soundU;

public class ParryingHandler {

    public boolean handle(Player player, EntityDamageByEntityEvent event) {
        if (!player.isBlocking()) return false;

        Location playerLoc = player.getLocation();
        Vector playerDirection = playerLoc.getDirection().setY(0).normalize();

        Location damagerLoc = event.getDamager().getLocation();
        Vector damageDirection = damagerLoc.toVector()
                .subtract(playerLoc.toVector())
                .setY(0)
                .normalize();

        double dotProduct = playerDirection.dot(damageDirection);
        double angle = Math.toDegrees(Math.acos(dotProduct));

        if (angle > 30) return false;

        ItemUtils itemUtils = new ItemUtils();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand.isEmpty() || hand.getType().isAir()) return false;

        Bukkit.getScheduler().runTask(plugin, () -> {
            hand.resetData(DataComponentTypes.BLOCKS_ATTACKS);
            player.setCooldown(hand, 30);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (hand.hasItemMeta() && (hand.getType().name().endsWith("_SWORD") || hand.getType().name().endsWith("_AXE") || hand.getType().name().endsWith("_SPEAR"))) {
                    itemUtils.addParryingComponent(hand);
                }
            }, 30L);
        });

        soundU.playSoundAtLocation(event.getDamager().getLocation(),
                Sound.BLOCK_ANVIL_LAND, 0.5f,
                new Random().nextFloat(1.4f, 1.7f));

        Vector direction = player.getLocation().toVector()
                .subtract(event.getDamager().getLocation().toVector());
        direction.setY(Math.max(direction.getY(), 0.1));
        direction.normalize().multiply(0.5);
        player.setVelocity(direction);
        return true;
    }
}