package dabbiks.uhc.utils;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.player.PlayerState;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class StateUtils {

    public final Map<Player, PlayerState> playerStates = new HashMap<>();
    private GameState gameState;

    // GAMESTATES

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    // PLAYERSTATES

    public PlayerState getPlayerState(Player player) {
        return playerStates.getOrDefault(player, PlayerState.SPECTATOR);
    }

    public void setPlayerState(Player player, PlayerState playerState) {
        playerStates.put(player, playerState);
    }

    public void removePlayerState(Player player) {
        playerStates.remove(player);
    }


}
