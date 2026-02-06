package dabbiks.uhc.lobby.teams;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.configs.LobbyConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scoreboard.Team;

import java.util.List;

import static dabbiks.uhc.Main.messageU;
import static dabbiks.uhc.Main.stateU;
import static dabbiks.uhc.lobby.teams.TeamUtils.scoreboard;

public class TeamManager implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Interaction interaction)) {
            return;
        }

        if (stateU.getGameState() == GameState.IN_GAME) {
            return;
        }

        if (!TeamDisplay.containsInteraction(interaction)) {
            return;
        }

        if (!interaction.hasMetadata("TEAM_DISPLAY_ID_IDENTIFIER")) return;
        List<MetadataValue> metadataValues = interaction.getMetadata("TEAM_DISPLAY_ID_IDENTIFIER");

        Player player = event.getPlayer();

        for (TeamData team : TeamLoader.getTeams()) {
            for (MetadataValue value : metadataValues) {
                if (value.asString().equals(team.name + "DISPLAY")) {
                    Team sbTeam = scoreboard.getTeam(team.name);
                    if (sbTeam != null) {
                        processClick(sbTeam, player);
                    }
                    break;
                }
            }
        }
    }

    public void processClick(Team team, Player player) {

        if (TeamUtils.getPlayerTeam(player) != null && TeamUtils.getPlayerTeam(player).getName().equals(team.getName())) {
            messageU.sendMessageToPlayer(player, messageU.gamePrefix + "§cJesteś już w tej drużynie!");
            return;
        }

        if (TeamUtils.getPlayerCountInTeam(team.getName()) >= LobbyConfig.teamSize) {
            messageU.sendMessageToPlayer(player, messageU.gamePrefix + "§cTa drużyna jest pełna!");
            return;
        }

        if (TeamUtils.getPlayerTeam(player) != null) {
            Team previousTeam = TeamUtils.getPlayerTeam(player);
            TeamUtils.removePlayerFromTeam(player, previousTeam.getName());
            TeamDisplay.processTeamQuit(player, previousTeam.getName());
        }

        TeamUtils.addPlayerToTeam(player, team.getName());
        TeamDisplay.processTeamJoin(player, TeamUtils.getPlayerTeam(player).getName());
        messageU.sendMessageToPlayer(player, messageU.gamePrefix + "§fDołączyłeś do drużyny!");
        for (String member : TeamUtils.getPlayerTeam(player).getEntries()) {
            Player playerMember = Bukkit.getPlayer(member);
            if (playerMember == null || playerMember == player) {
                continue;
            }
            messageU.sendMessageToPlayer(playerMember, messageU.gamePrefix + "§f" + player.getName() + " §fdołączył do drużyny!");
        }
    }
}
