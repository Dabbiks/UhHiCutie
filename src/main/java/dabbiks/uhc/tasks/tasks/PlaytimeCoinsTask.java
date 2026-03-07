package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.tasks.Task;
import org.bukkit.entity.Player;

import static dabbiks.uhc.Main.*;
import static dabbiks.uhc.Main.playerU;

public class PlaytimeCoinsTask extends Task {

    protected long getPeriod() {
        return 400;
    }

    protected void tick() {
        for (Player player : playerListU.getPlayingPlayers()) {
            rewardU.playing(player);
        }
    }
}
