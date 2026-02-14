package dabbiks.uhc.game.teams;

import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scoreboard.Team;

import static dabbiks.uhc.Main.INSTANCE;

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

        NBTEntity nbt = new NBTEntity(interaction);
        if (!nbt.hasTag("TEAM_INTERACTION")) {
            return;
        }

        String targetTeamName = nbt.getString("TEAM_INTERACTION");
        Player player = event.getPlayer();

        Team currentTeam = teamManager.getScoreboard().getEntryTeam(player.getName());

        if (currentTeam != null) {
            if (currentTeam.getName().equals(targetTeamName)) {
                player.sendMessage("§eJesteś już w tej drużynie.");
                return;
            }

            teamManager.removePlayer(player);
        }

        teamManager.addPlayer(player, targetTeamName);
    }
}