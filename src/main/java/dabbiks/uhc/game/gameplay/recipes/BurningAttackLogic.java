package dabbiks.uhc.game.gameplay.recipes;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BurningAttackLogic implements Listener {

    private static final Map<UUID, Long> activePlayers = new HashMap<>();

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        Boolean hasPerk = NBT.get(item, nbt -> {
            if (nbt.hasTag("perks")) {
                return nbt.getStringList("perks").contains("BURNING_ATTACK");
            }
            return false;
        });

        if (hasPerk != null && hasPerk) {
            activePlayers.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 30000L);
        }
    }

    public static void handle(Player damager, LivingEntity victim) {
        Long expireTime = activePlayers.get(damager.getUniqueId());
        if (expireTime != null) {
            if (System.currentTimeMillis() <= expireTime) {
                victim.setFireTicks(Math.max(victim.getFireTicks(), 100));
            } else {
                activePlayers.remove(damager.getUniqueId());
            }
        }
    }
}