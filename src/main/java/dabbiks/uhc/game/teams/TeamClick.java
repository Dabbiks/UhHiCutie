package dabbiks.uhc.game.teams;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.Bukkit;
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

        String teamName = NBT.getPersistentData(interaction, nbt -> nbt.getString("team_interaction"));
        if (teamName == null) return;


        Player player = event.getPlayer();

        Team currentTeam = teamManager.getScoreboard().getEntryTeam(player.getName());

        if (currentTeam != null) {
            if (currentTeam.getName().equals(teamName)) {
                player.sendMessage("§eJesteś już w tej drużynie.");
                return;
            }

            teamManager.removePlayer(player);
        }

        teamManager.addPlayer(player, teamName);
    }
}