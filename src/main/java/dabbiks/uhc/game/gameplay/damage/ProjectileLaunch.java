package dabbiks.uhc.game.gameplay.damage;

import dabbiks.uhc.game.gameplay.damage.handlers.MagicArrowHandler;
import dabbiks.uhc.game.gameplay.damage.handlers.ProjectileHandler;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class ProjectileLaunch implements Listener {

    private final ProjectileHandler projectileHandler = new ProjectileHandler();

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;

        ItemStack bow = player.getInventory().getItemInMainHand();
        if (bow.getType() == Material.AIR) return;
        NBTItem nbt = new NBTItem(bow);

        if (nbt.hasTag(EnchantType.MAGIC_ARROW.name())) {
            event.setCancelled(true);
            MagicArrowHandler.launch(player, arrow, nbt);
            return;
        }

        projectileHandler.handle(player, arrow);
    }
}