package dabbiks.uhc.utils;

import org.bukkit.entity.Player;

import java.util.List;

public class MessageUtils {

    public void sendMessageToPlayer(Player player, String message) {
        player.sendMessage(message);
    }

    public void sendMessageToPlayers(List<Player> players, String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public final String gamePrefix = "DB >";
    public final String tipPrefix = "TIP >";

}
