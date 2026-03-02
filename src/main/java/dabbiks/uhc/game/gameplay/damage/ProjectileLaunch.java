package dabbiks.uhc.game.gameplay.damage;

import dabbiks.uhc.game.gameplay.damage.handlers.MagicArrowHandler;
import dabbiks.uhc.game.gameplay.damage.handlers.ProjectileHandler;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class ProjectileLaunch implements Listener {

    private final ProjectileHandler projectileHandler = new ProjectileHandler();

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player)) return;

        if (projectile instanceof Trident trident) {
            ItemStack weapon = trident.getItem();
            if (weapon == null || weapon.getType() == Material.AIR) return;

            projectileHandler.handle(projectile, weapon);
        }
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack weapon = event.getBow();
        if (weapon == null) return;

        if (!(event.getProjectile() instanceof Projectile projectile)) return;

        boolean hasMagicArrow = Boolean.TRUE.equals(NBT.get(weapon, (Function<ReadableItemNBT, Object>) nbt -> nbt.hasTag(EnchantType.MAGIC_ARROW.name())));

        if (projectile instanceof Arrow arrow && hasMagicArrow) {
            event.setCancelled(true);
            MagicArrowHandler.launch(player, arrow, weapon);
            return;
        }

        projectileHandler.handle(projectile, weapon);
    }
}