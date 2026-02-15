package dabbiks.uhc.game.teams;

import dabbiks.uhc.game.configs.LobbyConfig;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dabbiks.uhc.Main.soundU;

public class TeamManager {

    private final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private final Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
    private final Map<String, TextDisplay> teamDisplays = new HashMap<>();
    private final List<Interaction> interactions = new ArrayList<>();

    public void addPlayer(Player player, String name) {
        Team team = scoreboard.getTeam(name);
        if (team == null) return;

        if (team.getEntries().size() >= LobbyConfig.teamSize) {
            player.sendMessage("§cTa drużyna jest już pełna!");
            return;
        }

        if (team.hasEntry(player.getName())) {
            player.sendMessage("§eJesteś już w tej drużynie.");
            return;
        }

        team.addEntry(player.getName());
        updateTeamVisuals(name);

        for (String entry : team.getEntries()) {
            Player p = Bukkit.getPlayer(entry);
            if (p == null) continue;
            p.sendMessage("§a+ §7Gracz §f" + player.getName() + " §7dołączył do drużyny.");
            soundU.playSoundAtLocation(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }
    }

    public void removePlayer(Player player) {
        Team team = scoreboard.getEntryTeam(player.getName());
        if (team == null) return;

        String teamName = team.getName();
        String playerName = player.getName();

        team.removeEntry(playerName);
        updateTeamVisuals(teamName);

        if (player.isOnline()) {
            player.sendMessage("§eOpuściłeś drużynę §6" + teamName);
        }

        for (String entry : team.getEntries()) {
            Player p = Bukkit.getPlayer(entry);
            if (p == null || p.getName().equals(playerName)) continue;

            p.sendMessage("§c- §7Gracz §f" + playerName + " §7opuścił drużynę.");
            soundU.playSoundAtLocation(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
        }
    }

    private void updateTeamVisuals(String teamName) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) return;

        List<String> entries = new ArrayList<>(team.getEntries());

        for (int i = 1; i <= LobbyConfig.teamSize; i++) {
            String key = teamName.toUpperCase() + i;
            TextDisplay display = teamDisplays.get(key);

            if (display == null) continue;

            if (!display.isValid()) {
                Entity freshEntity = Bukkit.getEntity(display.getUniqueId());
                if (freshEntity instanceof TextDisplay textDisplay) {
                    display = textDisplay;
                    teamDisplays.put(key, display);
                } else {
                    continue;
                }
            }

            if (i <= entries.size()) {
                display.setText("§7" + entries.get(i - 1));
            } else {
                display.setText("---");
            }
        }
    }

    public void deleteTeams() {
        for (Team team : scoreboard.getTeams()) team.unregister();
        for (Interaction interaction : interactions) interaction.remove();
        for (TextDisplay textDisplay : teamDisplays.values()) textDisplay.remove();

        for (Entity entity : Bukkit.getWorld("world").getEntities()) {
            boolean isTeamEntity = NBT.getPersistentData(entity, nbt -> nbt.hasTag("team_entity"));
            if (isTeamEntity) entity.remove();
        }

        interactions.clear();
        teamDisplays.clear();
    }

    public Map<String, TextDisplay> getTeamDisplays() { return teamDisplays; }
    public Scoreboard getScoreboard() { return scoreboard; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public List<Interaction> getInteractions() { return interactions; }
}