package dabbiks.uhc.game.teams;

import dabbiks.uhc.game.configs.LobbyConfig;
import de.tr7zw.nbtapi.NBT;
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
        if (!(event.getRightClicked() instanceof Interaction interaction)) {
            return;
        }

        String targetTeamName = NBT.getPersistentData(interaction, nbt -> nbt.getString("team_interaction"));
        if (targetTeamName == null) return;

        Player player = event.getPlayer();
        Team targetTeam = teamManager.getScoreboard().getTeam(targetTeamName);

        if (targetTeam == null) return;

        if (targetTeam.hasEntry(player.getName())) {
            player.sendMessage("§cJesteś już w tej drużynie.");
            return;
        }

        if (targetTeam.getEntries().size() >= LobbyConfig.teamSize) {
            player.sendMessage("§cTa drużyna jest już pełna!");
            return;
        }

        Team currentTeam = teamManager.getScoreboard().getEntryTeam(player.getName());
        if (currentTeam != null) {
            teamManager.removePlayer(player);
        }

        teamManager.addPlayer(player, targetTeamName);
    }
}