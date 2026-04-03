package dabbiks.uhc.player.punishments;

import dabbiks.uhc.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunishmentManager {

    private static final Map<UUID, Long> mutesAll = new HashMap<>();
    private static final Map<UUID, Long> mutesGlobal = new HashMap<>();
    private static File file;
    private static FileConfiguration config;

    public enum Type {
        BAN("Ban"), TEMPBAN("Ban czasowy"), MUTE_ALL("Wyciszenie (Wszystko)"), MUTE_GLOBAL("Wyciszenie (Czat publiczny)"), KICK("Wyrzucenie");
        private final String name;
        Type(String name) { this.name = name; }
        public String getName() { return name; }
    }

    public static void init() {
        file = new File(Main.plugin.getDataFolder(), "punishments.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private static void load() {
        mutesAll.clear();
        mutesGlobal.clear();
        if (config.contains("mutesAll")) {
            for (String key : config.getConfigurationSection("mutesAll").getKeys(false)) {
                mutesAll.put(UUID.fromString(key), config.getLong("mutesAll." + key));
            }
        }
        if (config.contains("mutesGlobal")) {
            for (String key : config.getConfigurationSection("mutesGlobal").getKeys(false)) {
                mutesGlobal.put(UUID.fromString(key), config.getLong("mutesGlobal." + key));
            }
        }
    }

    public static void save() {
        if (config == null) return;

        config.set("mutesAll", null);
        config.set("mutesGlobal", null);
        for (Map.Entry<UUID, Long> entry : mutesAll.entrySet()) {
            config.set("mutesAll." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Long> entry : mutesGlobal.entrySet()) {
            config.set("mutesGlobal." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void execute(Player admin, OfflinePlayer target, Type type, String reason, long durationMillis) {
        long expire = durationMillis == -1 ? -1 : System.currentTimeMillis() + durationMillis;
        Date expireDate = durationMillis == -1 ? null : new Date(expire);

        switch (type) {
            case BAN:
            case TEMPBAN:
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(target.getName(), reason, expireDate, admin.getName());
                if (target.isOnline()) ((Player) target).kickPlayer("§cZostałeś zbanowany!\n§7Powód: §e" + reason);
                break;
            case KICK:
                if (target.isOnline()) ((Player) target).kickPlayer("§cZostałeś wyrzucony!\n§7Powód: §e" + reason);
                break;
            case MUTE_ALL:
                mutesAll.put(target.getUniqueId(), expire);
                save();
                break;
            case MUTE_GLOBAL:
                mutesGlobal.put(target.getUniqueId(), expire);
                save();
                break;
        }

        String durationStr = formatDuration(durationMillis);
        if (type == Type.KICK) durationStr = "Brak";

        String msg = "\n" + "§8[§cUHC§8] §c" + admin.getName() + " §7ukarał gracza §c" + target.getName() + "\n" +
                "§8» §7Typ: §e" + type.getName() + "\n" +
                "§8» §7Czas: §e" + durationStr + "\n" +
                "§8» §7Powód: §e" + reason + "\n§c";

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(msg);
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1f, 1f);
        }
    }

    public static void clearAll(Player admin, OfflinePlayer target) {
        UUID uuid = target.getUniqueId();
        mutesAll.remove(uuid);
        mutesGlobal.remove(uuid);
        save();

        if (Bukkit.getBanList(org.bukkit.BanList.Type.NAME).isBanned(target.getName())) {
            Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(target.getName());
        }

        String msg = "\n" + "§8[§cUHC§8] §a" + admin.getName() + " §7wyczyścił wszystkie kary gracza §a" + target.getName() + "\n§c";
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(msg);
        }
    }

    public static boolean isMutedAll(UUID uuid) {
        if (!mutesAll.containsKey(uuid)) return false;
        long exp = mutesAll.get(uuid);
        if (exp == -1) return true;
        if (System.currentTimeMillis() > exp) {
            mutesAll.remove(uuid);
            save();
            return false;
        }
        return true;
    }

    public static boolean isMutedGlobal(UUID uuid) {
        if (!mutesGlobal.containsKey(uuid)) return false;
        long exp = mutesGlobal.get(uuid);
        if (exp == -1) return true;
        if (System.currentTimeMillis() > exp) {
            mutesGlobal.remove(uuid);
            save();
            return false;
        }
        return true;
    }

    private static String formatDuration(long millis) {
        if (millis == -1) return "Permanentne";
        long minutes = millis / 60000;
        if (minutes < 60) return minutes + " minut";
        long hours = minutes / 60;
        if (hours < 24) return hours + " godzin(y)";
        return (hours / 24) + " dni";
    }

    public static String getRemainingMuteAll(UUID uuid) {
        long exp = mutesAll.getOrDefault(uuid, 0L);
        if (exp == -1) return "Permanentne";
        long mins = (exp - System.currentTimeMillis()) / 60000;
        return mins <= 0 ? "< 1 minuta" : mins + " minut";
    }

    public static String getRemainingMuteGlobal(UUID uuid) {
        long exp = mutesGlobal.getOrDefault(uuid, 0L);
        if (exp == -1) return "Permanentne";
        long mins = (exp - System.currentTimeMillis()) / 60000;
        return mins <= 0 ? "< 1 minuta" : mins + " minut";
    }
}