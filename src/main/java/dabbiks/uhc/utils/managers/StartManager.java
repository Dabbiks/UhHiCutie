package dabbiks.uhc.utils.managers;

import dabbiks.uhc.game.gameplay.champions.Champion;
import dabbiks.uhc.game.gameplay.champions.ChampionManager;
import dabbiks.uhc.game.world.events.WeatherCycle;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.rank.RankManager;
import dabbiks.uhc.player.tab.TabUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import static dabbiks.uhc.Main.*;

public class StartManager {

    public void processStart() {
        prepareWorldBorder();
        prepareTeams();
        prepareTab();
        preparePlayers();
    }

    private void prepareWorldBorder() {
        INSTANCE.getWorldBorder().prepareWorldBorder();
    }

    private void prepareTeams() {
        TeamUtils.balanceTeams();
    }

    private void prepareTab() {
        new TabUtils().setGlobalTabFooter("\n" + WeatherCycle.getWeatherIcon()
        + "\n\n" + symbolU.SEGMENT_1 + "\n");
    }

    private void preparePlayers() {
        ChampionManager championManager = new ChampionManager();
        for (Player player : playerListU.getAllPlayers()) {
            stateU.setPlayerState(player, PlayerState.ALIVE);
            RankManager.calculatePlayerModifier(player);

            playerU.cleanseState(player);

            attributeManager.addModifier(player, Attribute.WAYPOINT_RECEIVE_RANGE, "waypoint_receive", 1000, AttributeModifier.Operation.ADD_NUMBER);
            attributeManager.addModifier(player, Attribute.WAYPOINT_TRANSMIT_RANGE, "waypoint_transmit", 1000, AttributeModifier.Operation.ADD_NUMBER);

            PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
            if (persistentData.getChampion() == null) persistentData.setChampion("default");
            Champion champion = championManager.getChampion(persistentData.getChampion());
            champion.onStart(player, persistentData.getChampionLevel(persistentData.getChampion()));
        }
        TeamUtils.teleportTeamsRandomly(300, 300);
    }

}
