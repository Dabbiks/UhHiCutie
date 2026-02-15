package dabbiks.uhc.game.gameplay.damage;

import dabbiks.uhc.game.gameplay.damage.handlers.ProjectileHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileLaunch implements Listener {

    private final ProjectileHandler projectileHandler = new ProjectileHandler();

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        projectileHandler.handle(player, event.getEntity());
    }
}