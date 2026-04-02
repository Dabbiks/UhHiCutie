package dabbiks.uhc.player.punishments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunishmentManager {

    private static final Map<UUID, Long> mutesAll = new HashMap<>();
    private static final Map<UUID, Long> mutesGlobal = new HashMap<>();

    public enum Type {
        BAN("Ban"), TEMPBAN("TempBan"), MUTE_ALL("Mute All"), MUTE_GLOBAL("Mute Global"), KICK("Kick");
        private final String name;
        Type(String name) { this.name = name; }
        public String getName() { return name; }
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
                break;
            case MUTE_GLOBAL:
                mutesGlobal.put(target.getUniqueId(), expire);
                break;
        }

        String durationStr = durationMillis == -1 ? "" : " §8(Czasowo)";
        if (type == Type.KICK) durationStr = "";

        String msg = "§8[§cUHC§8] §c" + admin.getName() + " §7ukarał gracza §c" + target.getName() + "\n" +
                "§8» §7Typ: §e" + type.getName() + durationStr + "\n" +
                "§8» §7Powód: §e" + reason;

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(msg);
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1f, 1f);
        }
    }

    public static boolean isMutedAll(UUID uuid) {
        if (!mutesAll.containsKey(uuid)) return false;
        long exp = mutesAll.get(uuid);
        if (exp == -1) return true;
        if (System.currentTimeMillis() > exp) {
            mutesAll.remove(uuid);
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
            return false;
        }
        return true;
    }
}