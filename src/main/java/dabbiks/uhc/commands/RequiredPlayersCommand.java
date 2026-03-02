package dabbiks.uhc.commands;

import dabbiks.uhc.game.configs.LobbyConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RequiredPlayersCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("uhc.admin")) {
            sender.sendMessage("§cBrak uprawnień.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§cUżycie: /setminplayers <int>");
            return true;
        }

        try {
            int minPlayers = Integer.parseInt(args[0]);
            if (minPlayers < 1) {
                sender.sendMessage("§cIlość graczy musi być większa od zera.");
                return true;
            }

            LobbyConfig.minPlayerCount = minPlayers;
            sender.sendMessage("§aPomyślnie zmieniono minimalną ilość graczy do startu na: " + minPlayers + ".");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cPodany argument nie jest liczbą całkowitą.");
        }

        return true;
    }
}