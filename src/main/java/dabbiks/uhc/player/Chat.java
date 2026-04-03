package dabbiks.uhc.player;

import dabbiks.uhc.Main;
import dabbiks.uhc.game.gameplay.champions.ChampionManager;
import dabbiks.uhc.game.teams.TeamData;
import dabbiks.uhc.game.teams.TeamLoader;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.rank.RankType;
import dabbiks.uhc.player.punishments.PunishmentManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.scoreboard.Team;

import static dabbiks.uhc.Main.*;

public class Chat implements Listener {

    ChampionManager champions = new ChampionManager();
    ChatCensor chatCensor = new ChatCensor();

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();

        if (PunishmentManager.isMutedAll(player.getUniqueId())) {
            player.sendMessage("§cJesteś wyciszony na wszystkich czatach. Pozostały czas: §e" + PunishmentManager.getRemainingMuteAll(player.getUniqueId()));
            event.setCancelled(true);
            return;
        }

        String message = event.getMessage();
        boolean isTeamMessage = message.startsWith("!");

        if (!isTeamMessage && PunishmentManager.isMutedGlobal(player.getUniqueId())) {
            player.sendMessage("§cJesteś wyciszony na czacie globalnym. Pozostały czas: §e" + PunishmentManager.getRemainingMuteGlobal(player.getUniqueId()) + "\n§cMożesz pisać tylko do drużyny używając '!'.");
            event.setCancelled(true);
            return;
        }

        Team team = TeamUtils.getPlayerTeam(player);
        event.setCancelled(true);
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());

        if (isTeamMessage) {
            message = message.substring(1);
        }

        String censoredMessage = chatCensor.censor(message);

        if (isTeamMessage && team != null) {
            for (String member : team.getEntries()) {
                Player teamMember = Bukkit.getPlayer(member);
                if (teamMember != null) {
                    PersistentData targetData = PersistentDataManager.getData(teamMember.getUniqueId());
                    String finalMessage = (targetData != null && targetData.getCensor()) ? censoredMessage : message;

                    teamMember.sendMessage("§c[DRUŻYNA] §6"
                            + champions.getChampion(persistentData.getChampion()).getName() + " §e" + player.getName() + "§f " + finalMessage);
                }
            }
        } else {
            if (persistentData == null) return;
            if (persistentData.getRank() == null) {
                persistentData.setRank(RankType.UNRANKED);
            }
            StringBuilder prefix = new StringBuilder();

            String rankIcon = getRankIcon(persistentData);
            if (rankIcon != null && !rankIcon.isEmpty()) {
                prefix.append(rankIcon);
            }

            if (persistentData.isManager()) {
                if (!prefix.isEmpty()) prefix.append(" ");
                prefix.append(symbolU.RANK_MANAGER);
            }

            String teamIcon = getTeamIcon(player);
            if (!teamIcon.isEmpty()) {
                if (!prefix.isEmpty()) prefix.append(" ");
                prefix.append(teamIcon);
            }

            if (!prefix.isEmpty()) prefix.append(" ");

            String prefixStr = prefix.toString() + "§e" + player.getName() + "§f ";

            for (Player target : playerListU.getAllPlayers()) {
                PersistentData targetData = PersistentDataManager.getData(target.getUniqueId());
                String finalMessage = (targetData != null && targetData.getCensor()) ? censoredMessage : message;

                target.sendMessage(prefixStr + finalMessage);
            }
        }
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