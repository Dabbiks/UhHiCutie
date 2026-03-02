package dabbiks.uhc.game.gameplay;

import dabbiks.uhc.game.GameData;
import dabbiks.uhc.game.Shutdown;
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
        if (GameData.isEnding) return;

        Team team = TeamUtils.getLastAliveTeam();
        if (team == null) return;

        GameData.isEnding = true;

        soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 0.9f);

        List<Player> winners = new ArrayList<>();
        StringBuilder bottomLine = new StringBuilder(" ");

        for (String member : team.getEntries()) {
            Player player = Bukkit.getPlayer(member);
            if (player == null) continue;

            winners.add(player);

            if (stateU.getPlayerState(player) != PlayerState.ALIVE) {
                bottomLine.append("§7").append(player.getName()).append(" ");
            } else {
                bottomLine.append("§f").append(player.getName()).append(" ");
            }
        }

        titleU.sendTitleToPlayers(playerListU.getAllPlayers(), "§e§l#1 Victory royale", bottomLine.toString(), 500);

        for (Player winner : winners) {
            rewardU.win(winner);
            rewardU.summary(winner);
        }

        Shutdown.shutdownServer();
    }

}