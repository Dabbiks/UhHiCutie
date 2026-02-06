package dabbiks.uhc.utils;

import dabbiks.uhc.player.PlayerState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.stateU;

public class PlayerListUtils {

    public List<Player> getWaitingPlayers() {
        List<Player> waitingPlayers = new ArrayList<>();
        for (Player player : stateU.playerStates.keySet()) {

            if (stateU.getPlayerState(player) == PlayerState.LOBBY) {
                waitingPlayers.add(player);
            }
        }
        return waitingPlayers;
    }

    public List<Player> getPlayingPlayers() {
        List<Player> playingPlayers = new ArrayList<>();
        for (Player player : stateU.playerStates.keySet()) {
            if (!player.isOnline()) continue;
            if (stateU.getPlayerState(player) == PlayerState.ALIVE) {
                playingPlayers.add(player);
            }
        }
        return playingPlayers;
    }

    public List<Player> getSpectatingPlayers() {
        List<Player> spectatingPlayers = new ArrayList<>();
        for (Player player : stateU.playerStates.keySet()) {

            if (stateU.getPlayerState(player) == PlayerState.SPECTATOR) {
                spectatingPlayers.add(player);
            }
        }
        return spectatingPlayers;
    }

    public List<Player> getAllPlayers() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(stateU.playerStates.keySet());
        return allPlayers;
    }

}
