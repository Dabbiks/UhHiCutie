package dabbiks.uhc.player.data.persistent;

import dabbiks.uhc.Main;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PersistentDataManager {

    private static Map<UUID, PersistentData> dataMap = new HashMap<>();

    @Nullable
    public static PersistentData getData(UUID uuid) {
        return dataMap.get(uuid);
    }

    public static void loadData(UUID uuid) {
        PersistentData persistentData = Main.persistentDataJson.loadPlayerData(uuid);

        // Jeśli dane nie istnieją (nowy gracz), tworzymy je i USTAWIAMY NICK
        if (persistentData == null) {
            persistentData = new PersistentData();
            persistentData.setPlayerId(uuid);

            String name = Bukkit.getOfflinePlayer(uuid).getName();
            persistentData.setPlayerName(name != null ? name : "Unknown");
        }

        dataMap.put(uuid, persistentData);
    }

    public static void delData(UUID uuid) {
        dataMap.remove(uuid);
    }

    public static void saveData(UUID uuid) {
        PersistentData persistentData = getData(uuid);
        if (persistentData != null) {
            Main.persistentDataJson.savePlayerData(persistentData);
        }
    }
}