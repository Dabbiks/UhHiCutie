package dabbiks.uhc.commands;

import dabbiks.uhc.Main;
import dabbiks.uhc.game.configs.LobbyConfig;
import dabbiks.uhc.game.teams.TeamInitializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TeamSizeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("uhc.admin")) {
            sender.sendMessage("§cBrak uprawnień.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§cUżycie: /setteamsize <int>");
            return true;
        }

        try {
            int newSize = Integer.parseInt(args[0]);
            if (newSize < 1) {
                sender.sendMessage("§cRozmiar drużyny musi być większy od zera.");
                return true;
            }

            LobbyConfig.teamSize = newSize;

            Main.INSTANCE.getTeamManager().deleteTeams();
            new TeamInitializer();

            sender.sendMessage("§aPomyślnie zmieniono rozmiar drużyny na: " + newSize + " i zaktualizowano hologramy.");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cPodany argument nie jest liczbą całkowitą.");
        }

        return true;
    }
}