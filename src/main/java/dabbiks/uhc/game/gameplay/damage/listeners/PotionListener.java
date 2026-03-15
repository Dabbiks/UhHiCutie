package dabbiks.uhc.game.gameplay.damage.listeners;

import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class PotionListener implements Listener {

    private final Set<PotionEffectType> negativeEffects = Set.of(
            PotionEffectType.POISON, PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS,
            PotionEffectType.INSTANT_DAMAGE, PotionEffectType.BLINDNESS, PotionEffectType.HUNGER,
            PotionEffectType.MINING_FATIGUE, PotionEffectType.WITHER, PotionEffectType.DARKNESS
    );

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        if (sessionData == null || !sessionData.hasTag(SessionTags.STATUS_EFFECT_PROOF)) return;

        if (event.getAction() == EntityPotionEffectEvent.Action.ADDED || event.getAction() == EntityPotionEffectEvent.Action.CHANGED) {
            event.getModifiedType();
            if (negativeEffects.contains(event.getModifiedType())) {
                event.setCancelled(true);
            }
        }
    }
}