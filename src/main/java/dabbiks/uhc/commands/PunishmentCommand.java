package dabbiks.uhc.commands;

import dabbiks.uhc.player.punishments.PunishmentMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishmentCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.hasPermission("uhc.admin")) {
            player.sendMessage("§cBrak uprawnień.");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage("§cUżycie: /kara <nick>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        new PunishmentMenu.MainMenu(player, target).open(player);
        return true;
    }
}