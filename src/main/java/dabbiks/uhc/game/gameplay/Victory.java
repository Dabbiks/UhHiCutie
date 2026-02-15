package dabbiks.uhc.game.gameplay;

import dabbiks.uhc.game.Shutdown;
import dabbiks.uhc.game.teams.TeamData;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.*;

public class Victory {

    public static void processWin() {
        Team team = TeamUtils.getLastAliveTeam();
        if (team == null) return;

        soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 0.9f);

        List<String> players = new ArrayList<>();
        players.add(" ");

        for (String member : team.getEntries()) {
            Player player = Bukkit.getPlayer(member);
            if (player == null) continue;

            if (stateU.getPlayerState(player) != PlayerState.ALIVE) {
                players.add("§7" + player.getName() + " ");
                continue;
            }

            players.add("§f" + player.getName() + " ");
        }

        StringBuilder bottomLine = new StringBuilder();
        for (String s : players) {
            bottomLine.append(s);
        }

        titleU.sendTitleToPlayers(playerListU.getAllPlayers(), "§e§l#1 Victory royale", bottomLine.toString(), 500);

        for (String playerName : players) {
            Player player = Bukkit.getPlayer(playerName);
            assert player != null;
            rewardU.win(player);
            rewardU.summary(player);
        }

        Shutdown.shutdownServer();
    }

}
