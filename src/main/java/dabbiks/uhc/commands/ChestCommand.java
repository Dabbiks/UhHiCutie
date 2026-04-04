package dabbiks.uhc.commands;

import dabbiks.uhc.cosmetics.chest.ChestType;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("uhc.admin")) return true;

        if (args.length != 2) {
            sender.sendMessage("§cUzycie: /skrzynka <nick> <typ_skrzynki>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        ChestType type;

        try {
            type = ChestType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cNieznany typ skrzyni. Dostepne: COMMON, RARE, EPIC, MYTHIC, LEGENDARY, EASTER");
            return true;
        }

        PersistentData data = PersistentDataManager.getData(target.getUniqueId());
        boolean wasUnloaded = false;

        if (data == null) {
            PersistentDataManager.loadData(target.getUniqueId());
            data = PersistentDataManager.getData(target.getUniqueId());
            wasUnloaded = true;
        }

        if (data != null) {
            data.addChests(type.getIndex(), 1);
            data.addKeys(type.getIndex(), 1);
            PersistentDataManager.saveData(target.getUniqueId());

            if (wasUnloaded && !target.isOnline()) {
                PersistentDataManager.delData(target.getUniqueId());
            }

            sender.sendMessage("§aPomyślnie dodano skrzynkę i klucz " + type.name() + " dla gracza " + target.getName() + ".");
        } else {
            sender.sendMessage("§cBlad wczytywania danych gracza.");
        }

        return true;
    }
}