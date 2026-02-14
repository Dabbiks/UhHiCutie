package dabbiks.uhc.game.teams;

import dabbiks.uhc.game.configs.LobbyConfig;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.TextDisplay;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static dabbiks.uhc.Main.plugin;
import static dabbiks.uhc.Main.soundU;
import static dabbiks.uhc.game.teams.TeamUtils.scoreboard;

public class TeamDisplay {

    // Klucze: teamName+identifier, wartości: entity (TextDisplay)
    private static final Map<String, TextDisplay> teamTextDisplays = new HashMap<>();
    // Klucze: UUID entity Interaction, wartości: Interaction
    private static final Map<UUID, Interaction> teamInteractionDisplays = new HashMap<>();

    public static void createTeamDisplay(TeamData team) {
        Location location = team.getBukkitLocation();

        TextDisplay title = (TextDisplay) Bukkit.getWorld(location.getWorld().getName()).spawnEntity(location.clone(), org.bukkit.entity.EntityType.TEXT_DISPLAY);
        Interaction interactionDisplay = (Interaction) Bukkit.getWorld(location.getWorld().getName()).spawnEntity(location.clone().subtract(0, 0.4, 0), org.bukkit.entity.EntityType.INTERACTION);

        setTeamDisplaySettings(title, "TITLE", team.name, team.rotation);
        setTeamInteractionDisplaySettings(interactionDisplay, "DISPLAY", team.name);

        title.setText("§a§l" + team.name);
        teamTextDisplays.put(team.name + "TITLE", title);
        teamInteractionDisplays.put(interactionDisplay.getUniqueId(), interactionDisplay);

        float subtract = 0.25f;
        for (int i = 1; i <= LobbyConfig.teamSize; i++) {
            TextDisplay memberDisplay = (TextDisplay) Bukkit.getWorld(location.getWorld().getName()).spawnEntity(location.clone().subtract(0, subtract, 0), org.bukkit.entity.EntityType.TEXT_DISPLAY);
            setTeamDisplaySettings(memberDisplay, "MEMBER" + i, team.name, team.rotation);
            memberDisplay.setText("§f---");
            teamTextDisplays.put(team.name + "MEMBER" + i, memberDisplay);
            subtract += 0.25f;
        }

        TextDisplay logo = (TextDisplay) Bukkit.getWorld(location.getWorld().getName()).spawnEntity(location.clone().add(0, 0.45f, 0), org.bukkit.entity.EntityType.TEXT_DISPLAY);
        setTeamDisplaySettings(logo, "LOGO", team.name, team.rotation);
        logo.setText(team.icon);
        teamTextDisplays.put(team.name + "LOGO", logo);
    }

    private static void setTeamDisplaySettings(TextDisplay display, String identifier, String teamName, int rotation) {
        display.setMetadata("TEAM_DISPLAY_ID_IDENTIFIER", new FixedMetadataValue(plugin, teamName + identifier));
        display.setBillboard(org.bukkit.entity.Display.Billboard.FIXED);
        display.setRotation(rotation, 0);
        display.setDefaultBackground(false);
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        display.setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),       // przesunięcie (translation)
                new Quaternionf(),             // lewa rotacja
                new Vector3f(0.85f, 0.85f, 0.85f),// skala
                new Quaternionf()              // prawa rotacja
        ));
    }

    private static void setTeamInteractionDisplaySettings(Interaction interaction, String identifier, String teamName) {
        interaction.setMetadata("TEAM_DISPLAY_ID_IDENTIFIER", new FixedMetadataValue(plugin, teamName + identifier));
        interaction.setInteractionHeight(1.2f);
        interaction.setInteractionWidth(1.2f);
    }

    public static void modifyTeamDisplay(String teamName, String identifier, String newLine) {
        String key = teamName + identifier;
        TextDisplay textDisplay = teamTextDisplays.get(key);
        if (textDisplay != null) {
            textDisplay.setText(newLine);
        }
    }

    public static void reloadTeamDisplay(org.bukkit.scoreboard.Team team) {
        var members = team.getEntries().stream().toList();
        for (int i = 1; i <= LobbyConfig.teamSize; i++) {
            if (i <= members.size()) {
                modifyTeamDisplay(team.getName(), "MEMBER" + i, members.get(i - 1));
            } else {
                modifyTeamDisplay(team.getName(), "MEMBER" + i, "§f---");
            }
        }
    }

    public static void deleteTeamDisplays() {
        for (TextDisplay td : new ArrayList<>(teamTextDisplays.values())) {
            if (td != null && !td.isDead()) {
                td.remove();
            }
        }
        teamTextDisplays.clear();

        for (Interaction interaction : new ArrayList<>(teamInteractionDisplays.values())) {
            if (interaction != null && !interaction.isDead()) {
                interaction.remove();
            }
        }
        teamInteractionDisplays.clear();
    }


    public static void processTeamJoin(org.bukkit.entity.Player player, String teamName) {
        int count = TeamUtils.getPlayerCountInTeam(teamName);
        String identifier = "MEMBER" + count;
        modifyTeamDisplay(teamName, identifier, player.getName());

        TeamData teamData = TeamLoader.getTeamByName(teamName);

        soundU.playSoundToPlayer(player, Sound.BLOCK_LEVER_CLICK, 0.3f, 1);

        if (teamData != null) {
            SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
            sessionData.setTeamIcon(teamData.smallIcon);
//            sessionData.updatePlayerPrefix(player);
        }

        identifier = "TITLE";
        if (count > 0 && count < LobbyConfig.teamSize) {
            modifyTeamDisplay(teamName, identifier, "§e§l" + teamName);
            return;
        }

        if (count == LobbyConfig.teamSize) {
            modifyTeamDisplay(teamName, identifier, "§c§l" + teamName);
        }
    }

    public static void processTeamQuit(org.bukkit.entity.Player player, String teamName) {
        org.bukkit.scoreboard.Team team = scoreboard.getTeam(teamName);
        if (team == null) return;

        reloadTeamDisplay(team);

        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
//        sessionData.updatePlayerPrefix(player);

        int count = TeamUtils.getPlayerCountInTeam(teamName);
        String identifier = "TITLE";

        if (count > 0 && count < LobbyConfig.teamSize) {
            modifyTeamDisplay(teamName, identifier, "§e§l" + teamName);
            return;
        }

        if (count == 0) {
            modifyTeamDisplay(teamName, identifier, "§a§l" + teamName);
        }
    }

    public static void processServerQuitTeamQuit(org.bukkit.entity.Player player, String teamName) {
        org.bukkit.scoreboard.Team team = scoreboard.getTeam(teamName);
        if (team == null) return;

        reloadTeamDisplay(team);

        int count = TeamUtils.getPlayerCountInTeam(teamName);
        String identifier = "TITLE";

        if (count > 0 && count < LobbyConfig.teamSize) {
            modifyTeamDisplay(teamName, identifier, "§e§l" + teamName);
            return;
        }

        if (count == 0) {
            modifyTeamDisplay(teamName, identifier, "§a§l" + teamName);
        }
    }

    public static boolean containsInteraction(Interaction interaction) {
        return teamInteractionDisplays.containsKey(interaction.getUniqueId());
    }

}
