package dabbiks.uhc.utils;

import org.bukkit.entity.Player;

import java.util.List;

public class TitleUtils {

    public void sendTitleToPlayer(Player player, String top, String bottom, int lasts) {
        player.sendTitle(top, bottom, 0, lasts, 3);
    }

    public void sendTitleToPlayers(List<Player> players, String top, String bottom, int lasts) {
        for (Player player : players) {
            player.sendTitle(top, bottom, 0, lasts, 3);
        }
    }

}
