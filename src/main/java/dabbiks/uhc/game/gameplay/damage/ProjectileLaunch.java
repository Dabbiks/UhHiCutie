package dabbiks.uhc.game.gameplay.damage;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class ProjectileLaunch {

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        if (event.getEntity() instanceof Arrow arrow) addArrowNBT(player, event, arrow);
    }

    private void addArrowNBT(Player player, ProjectileLaunchEvent event, Arrow arrow) {

    }

    private void addTridentNBT() {

    }

}
