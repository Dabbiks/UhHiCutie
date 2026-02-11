package dabbiks.uhc.player.data.persistent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import static dabbiks.uhc.Main.plugin;

public class PersistentDataJson {

    private Gson gson;
    private File dataFolder;

    public PersistentDataJson() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        dataFolder = new File(plugin.getDataFolder() + "/player-data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public PersistentData loadPlayerData(UUID playerId) {

        File file = new File(dataFolder, playerId.toString() + ".json");
        if (!file.exists()) return null;

        try (FileReader reader = new FileReader(file)) {
            PersistentData persistentData = gson.fromJson(reader, PersistentData.class);
            persistentData.setUUID(playerId);

            String name = Bukkit.getOfflinePlayer(playerId).getName();
            if (name == null) name = "Unknown";
            persistentData.setName(name);

            return persistentData;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void savePlayerData(PersistentData persistentData) {
        File file = new File(dataFolder, persistentData.getUUID().toString() + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(persistentData, writer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
