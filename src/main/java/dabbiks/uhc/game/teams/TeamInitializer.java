package dabbiks.uhc.game.teams;

import dabbiks.uhc.game.configs.LobbyConfig;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.TextDisplay;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

import static dabbiks.uhc.Main.INSTANCE;
import static dabbiks.uhc.Main.plugin;

public class TeamInitializer {

    private final TeamLoader teamLoader = new TeamLoader();
    private final TeamManager teamManager = INSTANCE.getTeamManager();

    public TeamInitializer() {
        teamLoader.loadTeams();
        init();
    }

    private void init() {
        for (TeamData data : teamLoader.getTeams()) {
            Location location = data.getBukkitLocation();
            String name = data.getName();
            String icon = data.getIcon();
            int rotation = data.getRotation();

            createTeam(data);
            createTeamDisplay(name, icon, location, rotation);
        }
    }

    private void createTeam(TeamData data) {
        String name = data.getName();

        teamManager.getScoreboard().registerNewTeam(name);

    }

    private void createTeamDisplay(String name, String icon, Location location, int rotation) {
        for (int i = -1; i <= LobbyConfig.teamSize; i++) {
            double yOffset = switch (i) {
                case -1 -> 0.45;
                case 0 -> 0.0;
                default -> i * -0.25;
            };

            String text = switch (i) {
                case -1 -> icon;
                case 0 -> name;
                default -> "§8---";
            };

            TextDisplay display = (TextDisplay) location.getWorld().spawnEntity(location.clone().add(0, yOffset, 0), EntityType.TEXT_DISPLAY);
            display.setText(text);

            applyDisplaySettings(display, rotation);

            int finalI = i;
            NBT.modifyPersistentData(display, nbt -> {
                nbt.setInteger(name.toUpperCase(), finalI);
                nbt.setInteger("team_entity", 1);
            });

            teamManager.getTeamDisplays().put(name.toUpperCase() + i, display);
        }

        Interaction interaction = (Interaction) location.getWorld().spawnEntity(location.clone().subtract(0, 0.4, 0), EntityType.INTERACTION);

        NBT.modifyPersistentData(interaction, nbt -> {
            nbt.setString("team_interaction", name);
            nbt.setInteger("team_entity", 1);
        });

        interaction.setInteractionHeight(1.2f);
        interaction.setInteractionWidth(1.2f);
        teamManager.getInteractions().add(interaction);
    }

    private void applyDisplaySettings(TextDisplay textDisplay, int rotation) {
        textDisplay.setBillboard(org.bukkit.entity.Display.Billboard.FIXED);
        textDisplay.setRotation(rotation, 0);
        textDisplay.setDefaultBackground(false);
        textDisplay.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        textDisplay.setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),
                new Quaternionf(),
                new Vector3f(0.85f, 0.85f, 0.85f),
                new Quaternionf()
        ));
    }
}
