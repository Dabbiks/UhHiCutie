package dabbiks.uhc.game.teams;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scoreboard.Team;

import static dabbiks.uhc.Main.*;

public class TeamClick implements Listener {

    private final TeamManager teamManager;

    public TeamClick() {
        this.teamManager = INSTANCE.getTeamManager();
    }

    @EventHandler
    public void onInteractionClick(PlayerInteractEntityEvent event) {
        messageU.sendMessageToPlayer(event.getPlayer(), "TEST 1");
        if (!(event.getRightClicked() instanceof Interaction interaction)) {
            return;
        }
        messageU.sendMessageToPlayer(event.getPlayer(), "TEST 2");

        String teamName = NBT.get(interaction, nbt -> (String) nbt.getString("TEAM_INTERACTION"));
        if (teamName == null) return;

        messageU.sendMessageToPlayer(event.getPlayer(), "TEST 3");

        Player player = event.getPlayer();

        Team currentTeam = teamManager.getScoreboard().getEntryTeam(player.getName());

        if (currentTeam != null) {
            if (currentTeam.getName().equals(teamName)) {
                player.sendMessage("§eJesteś już w tej drużynie.");
                return;
            }
            messageU.sendMessageToPlayer(event.getPlayer(), "TEST 4");

            teamManager.removePlayer(player);
        }
        messageU.sendMessageToPlayer(event.getPlayer(), "TEST 5");

        teamManager.addPlayer(player, teamName);
    }
}