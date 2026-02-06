package dabbiks.uhc.lobby.teams;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dabbiks.uhc.Main.plugin;

public class TeamLoader {

    private static List<TeamData> teams = new ArrayList<>();

    public static void loadTeams() {
        File folder = new File(plugin.getDataFolder(), "teams");
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, "teams.json");
        if (!file.exists()) {
            plugin.getLogger().warning("teams.json not found in " + folder.getAbsolutePath());
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            TeamData[] loaded = gson.fromJson(reader, TeamData[].class);
            teams = Arrays.asList(loaded);
            plugin.getLogger().info("Loaded " + teams.size() + " teams from JSON.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<TeamData> getTeams() {
        return teams;
    }

    public static TeamData getTeamByName(String name) {
        for (TeamData team : teams) {
            if (team.name.equalsIgnoreCase(name)) return team;
        }
        return null;
    }
}
