package dabbiks.uhc.game.gameplay.champions.listeners;

import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PyromaniacListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null || event.getItem().getType() != Material.FIRE_CHARGE) return;

        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        if (sessionData.hasTag(SessionTags.FIRE_CHARGE_DISPENSER)) {
            event.setCancelled(true);
            event.getItem().setAmount(event.getItem().getAmount() - 1);
            Fireball fireball = player.launchProjectile(Fireball.class);
            fireball.setYield(2.0F);
            fireball.setIsIncendiary(true);
        }
    }
}