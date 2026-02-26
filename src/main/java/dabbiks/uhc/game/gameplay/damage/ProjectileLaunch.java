package dabbiks.uhc.game.gameplay.damage;

import dabbiks.uhc.game.gameplay.damage.handlers.MagicArrowHandler;
import dabbiks.uhc.game.gameplay.damage.handlers.ProjectileHandler;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class ProjectileLaunch implements Listener {

    private final ProjectileHandler projectileHandler = new ProjectileHandler();

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) return;

        ItemStack weapon = player.getInventory().getItemInMainHand();

        if (!isValidWeapon(weapon, projectile)) {
            weapon = player.getInventory().getItemInOffHand();
            if (!isValidWeapon(weapon, projectile)) return;
        }

        NBTItem nbt = new NBTItem(weapon);

        if (projectile instanceof Arrow arrow && nbt.hasTag(EnchantType.MAGIC_ARROW.name())) {
            event.setCancelled(true);
            MagicArrowHandler.launch(player, arrow, nbt);
            return;
        }

        projectileHandler.handle(projectile, nbt);
    }

    private boolean isValidWeapon(ItemStack item, Projectile projectile) {
        if (item == null || item.getType() == Material.AIR) return false;
        Material type = item.getType();

        if (projectile instanceof Arrow) {
            return type == Material.BOW || type == Material.CROSSBOW;
        } else if (projectile instanceof Trident) {
            return type == Material.TRIDENT;
        }
        return false;
    }
}