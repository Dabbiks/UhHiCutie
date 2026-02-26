package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.configs.SegmentConfig;
import dabbiks.uhc.game.world.events.WeatherCycle;
import dabbiks.uhc.player.tab.TabUtils;
import dabbiks.uhc.tasks.Task;
import org.bukkit.Sound;

import java.util.concurrent.TimeUnit;

import static dabbiks.uhc.Main.*;

public class SegmentTask extends Task {

    protected long getPeriod() {
        return 20;
    }

    protected void tick() {
        if (stateU.getGameState() != GameState.IN_GAME) return;
        if ((int) timeU.getTime() == (SegmentConfig.eachSegmentTime * SegmentConfig.actualSegment) - 1) {
            soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.BLOCK_LEVER_CLICK, 0.1f, 1.3f);
        }
        if ((int) timeU.getTime() == (SegmentConfig.eachSegmentTime * SegmentConfig.actualSegment)) {
            soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.BLOCK_LEVER_CLICK, 0.2f, 1.1f);
        }
        if ((int) timeU.getTime() >= (SegmentConfig.eachSegmentTime * SegmentConfig.actualSegment) + 1) {
            soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.BLOCK_LEVER_CLICK, 0.4f, 0.9f);

            SegmentConfig.actualSegment++;
            WeatherCycle.rollWeather();
            new TabUtils().setGlobalTabFooter("\n" + WeatherCycle.getWeatherIcon() + "\n");
        }
    }
}
