package dabbiks.uhc.game.teams;

import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.rank.RankType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.scoreboard.Team;

public class TeamChat implements Listener {

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        Team team = TeamUtils.getPlayerTeam(player);
        String message = event.getMessage();

        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());

        boolean isTeamMessage = message.startsWith("!");
        if (isTeamMessage) {
            message = message.substring(1);
        }

        event.setCancelled(true);

//        if (isTeamMessage && team != null) {
//            // Wiadomość drużynowa
//            for (String member : team.getEntries()) {
//                Player teamMember = Bukkit.getPlayer(member);
//                if (teamMember != null) {
//                    teamMember.sendMessage("§c[DRUŻYNA] §6" + persistentData.get() + " §e" + player.getName() + "§f " + message);
//                }
//            }
//        } else {
//            SessionData memberSessionData = SessionDataManager.getData(player.getUniqueId());
//            if (persistentData == null) return;
//            if (persistentData.getPlayerRank() == null) {
//                persistentData.setPlayerRank(RankType.UNRANKED);
//            }
//            memberSessionData.rankIcon = persistentData.getPlayerRank().getIcon();
//            String prefix = "";
//            if (!memberSessionData.rankIcon.isEmpty()) prefix = prefix + memberSessionData.rankIcon + " ";
//            if (!memberSessionData.teamIcon.isEmpty()) prefix = prefix + memberSessionData.teamIcon + " ";
//            if (persistentData.getIsManager()) prefix = prefix + "\uE088" + " ";
//            Bukkit.broadcastMessage(prefix + "§e" + player.getName() + "§f " + message);
//        }
    }
}