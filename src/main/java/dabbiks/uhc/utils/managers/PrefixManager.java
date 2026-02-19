package dabbiks.uhc.utils.managers;

import dabbiks.uhc.game.teams.TeamData;
import dabbiks.uhc.game.teams.TeamLoader;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import static dabbiks.uhc.Main.symbolU;
import static dabbiks.uhc.Main.tabManager;

public class PrefixManager {

    public void update(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        StringBuilder prefix = new StringBuilder();

        String rankIcon = getRankIcon(persistentData);
        if (rankIcon != null && !rankIcon.isEmpty()) {
            prefix.append(rankIcon);
        }

        if (persistentData != null && persistentData.isManager()) {
            if (!prefix.isEmpty()) prefix.append(" ");
            prefix.append(symbolU.RANK_MANAGER);
        }

        String teamIcon = getTeamIcon(player);
        if (teamIcon != null && !teamIcon.isEmpty()) {
            if (!prefix.isEmpty()) prefix.append(" ");
            prefix.append(teamIcon);
        }

        if (!prefix.isEmpty()) prefix.append(" ");

        tabManager.setPlayerTabPrefix(player, prefix.toString());
    }

    private String getRankIcon(PersistentData persistentData) {
        String icon = "";

        if (persistentData == null) return icon;
        if (persistentData.getRank() == null) return icon;
        return persistentData.getRank().getIcon();
    }

    private String getTeamIcon(Player player) {
        Team team = TeamUtils.getPlayerTeam(player);
        if (team == null) return "";

        String teamName = team.getName();

        for (TeamData teamData : TeamLoader.getTeams()) {
            if (teamData.getName().equalsIgnoreCase(teamName)) {
                String icon = teamData.getSmallIcon();
                return (icon != null) ? icon : "";
            }
        }
        return "";
    }

}
