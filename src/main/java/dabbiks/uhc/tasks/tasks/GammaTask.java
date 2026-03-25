package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.tasks.Task;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static dabbiks.uhc.Main.*;

public class GammaTask extends Task {
    @Override
    protected long getPeriod() {
        return 20;
    }

    @Override
    protected void tick() {
        if (stateU.getGameState() != GameState.IN_GAME) return;

        for (Player player : playerListU.getPlayingPlayers()) {
            PersistentData pData = PersistentDataManager.getData(player.getUniqueId());
            if (pData != null && pData.getGamma()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 240, 0, false, false, false));
            }
        }
    }
}
