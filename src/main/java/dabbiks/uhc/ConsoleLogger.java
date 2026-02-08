package dabbiks.uhc;

import org.bukkit.Bukkit;

public class ConsoleLogger {

    public static void sendInfo(LogType type, String info) {
        Bukkit.getLogger().info("[" + type.name() + "] " + info);
    }

    public static void sendWarning(LogType type, String warning) {
        Bukkit.getLogger().warning("[" + type.name() + "] " + warning);
    }

    public enum LogType {
        PLAYER_DATA,
        TASK_MANAGER,
        TEAMS,
        RECIPES,
        ENCHANTS
    }

}
