package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.configs.LobbyConfig;
import dabbiks.uhc.game.configs.WorldConfig;
import dabbiks.uhc.tasks.Task;
import dabbiks.uhc.utils.managers.StartManager;
import org.bukkit.Bukkit;

import static dabbiks.uhc.Main.*;

public class StartTask extends Task {

    public static int countdown = LobbyConfig.COUNTDOWN;

    @Override
    protected long getPeriod() {
        return 20;
    }

    @Override
    protected void tick() {
        int playerCount = playerListU.getAllPlayers().size();

        if (stateU.getGameState() == null) stateU.setGameState(GameState.WAITING);
        if (!(WorldConfig.isWorldGenerated)) return;

        if (stateU.getGameState() == GameState.STARTING && countdown > 0) {
            titleU.sendTitleToPlayers(playerListU.getAllPlayers(), "", String.valueOf(countdown), 30);
            countdown--;
        }

        if (stateU.getGameState() == GameState.WAITING && playerCount >= LobbyConfig.minPlayerCount) {
            stateU.setGameState(GameState.STARTING);
        }

        if (stateU.getGameState() == GameState.STARTING && playerCount < LobbyConfig.minPlayerCount) {
            titleU.sendTitleToPlayers(playerListU.getAllPlayers(), "", "Przerwane", 30);
            stateU.setGameState(GameState.WAITING);
            countdown = LobbyConfig.COUNTDOWN;
        }

        if (stateU.getGameState() == GameState.STARTING && countdown <= 0) {
            stateU.setGameState(GameState.IN_GAME);
            Bukkit.getWorld(WorldConfig.worldName).setTime(0);
            new StartManager().processStart();
        }


    }

}
