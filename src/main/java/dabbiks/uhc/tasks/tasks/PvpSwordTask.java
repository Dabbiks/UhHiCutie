package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.tasks.Task;
import dabbiks.uhc.utils.ParticleUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static dabbiks.uhc.Main.*;

public class PvpSwordTask extends Task {

    private static final Map<UUID, Integer> pvpPoints = new HashMap<>();

    protected long getPeriod() {
        return 5;
    }

    protected void tick() {
        for (Player player : playerListU.getAllPlayers()) {
            int lastPoints = pvpPoints.getOrDefault(player.getUniqueId(), 0);

            if (stateU.getPlayerState(player) == PlayerState.ALIVE) return;
            if (player.getInventory().getHeldItemSlot() == 4) {
                pvpPoints.put(player.getUniqueId(), pvpPoints.getOrDefault(player.getUniqueId(), 0) + 1);
            } else {
                pvpPoints.put(player.getUniqueId(), 0);
            }
            if (pvpPoints.getOrDefault(player.getUniqueId(), 0) < 0) pvpPoints.put(player.getUniqueId(), 0);
            if (pvpPoints.getOrDefault(player.getUniqueId(), 0) > 12) pvpPoints.put(player.getUniqueId(), 12);
            if (pvpPoints.getOrDefault(player.getUniqueId(), 0) == 12) {
                ParticleUtils.spawn(player.getLocation(), Particle.FLAME, 1, 0, 0.1);
            }
            if (lastPoints == 11) {
                soundU.playSoundAtPlayer(player, Sound.ENTITY_BLAZE_HURT, 0.5f, 0.5f);
            }
        }
    }

    public static boolean canFight(Player player) {
        return pvpPoints.getOrDefault(player.getUniqueId(), 0) == 12;
    }

}
