package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.configs.LobbyConfig;
import dabbiks.uhc.game.configs.SegmentConfig;
import dabbiks.uhc.game.configs.WorldConfig;
import dabbiks.uhc.game.world.events.WeatherCycle;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.tab.TabUtils;
import dabbiks.uhc.tasks.Task;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

import static dabbiks.uhc.Main.*;
import static org.bukkit.Bukkit.getLogger;

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

            if (SegmentConfig.actualSegment == SegmentConfig.pvpStageSegment + 1) {
                Bukkit.getWorld(WorldConfig.worldName).setPVP(true);
            }

            if (SegmentConfig.actualSegment == SegmentConfig.compassStageSegment + 1) {
                World world = Bukkit.getWorld(WorldConfig.worldName);
                setGameRule(world, "locator_bar", true);
            }

            WeatherCycle.rollWeather();

            new TabUtils().setGlobalTabFooter("\n" + WeatherCycle.getWeatherIcon() + "\n");

            String[] messages = null;
            switch (SegmentConfig.actualSegment) {
                case 10:
                    messages = new String[]{
                            "§c§lETAP I",
                            "§fGranica ruszyła",
                            "§fPvP zostało włączone"
                    };
                    break;
                case 15:
                    messages = new String[]{
                            "§c§lETAP II",
                            "§fGranica zatrzymała się",
                            "§fRadar na pasku namierza graczy"
                    };
                    break;
                case 20:
                    messages = new String[]{
                            "§c§lETAP III",
                            "§fGranica ruszyła"
                    };
                    break;
            }

            if (messages != null) {
                messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "");
                for (String msg : messages) {
                    messageU.sendMessageToPlayers(playerListU.getAllPlayers(), msg);
                }
                messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "");
            }
        }
    }

    private static <T> void setGameRule(World world, String ruleName, T value) {
        GameRule<T> rule = GameRule.getByName(ruleName);
        if (rule != null) {
            world.setGameRule(rule, value);
        } else {
            getLogger().warning("GameRule " + ruleName + " not found.");
        }
    }
}