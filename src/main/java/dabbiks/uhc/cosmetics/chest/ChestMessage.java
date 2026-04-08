package dabbiks.uhc.cosmetics.chest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static dabbiks.uhc.Main.messageU;
import static dabbiks.uhc.Main.playerListU;

public class ChestMessage {

    public void send(ChestType chestType, UUID uuid, List<String> rewards) {
        Player player = Bukkit.getPlayer(uuid);
        List<Player> players = playerListU.getAllPlayers();
        players.removeIf(player1 -> !player1.getWorld().getName().equals("world"));
        if (player == null) return;
        if (rewards == null) {
            messageU.sendMessageToPlayers(players, "");
            messageU.sendMessageToPlayers(players, "§e" + player.getName() + " §7otwiera");
            messageU.sendMessageToPlayers(players, getChestString(chestType, false));
            messageU.sendMessageToPlayers(players, "");
        } else {
            messageU.sendMessageToPlayers(players, "");
            messageU.sendMessageToPlayers(players, "§e" + player.getName() + " §7znajduje");
            messageU.sendMessageToPlayers(players, "§7w skrzyni " + getChestString(chestType, true));
            for (String string : rewards) {
                messageU.sendMessageToPlayers(players, string);
            }
            messageU.sendMessageToPlayers(players, "");
        }
    }

    private String getChestString(ChestType chestType, boolean rewards) {
        String string = "";
        switch (chestType) {
            case COMMON -> string = "§7skrzynię §7§lPOSPOLITĄ";
            case RARE -> string = "§7skrzynię §b§lRZADKĄ";
            case EPIC -> string = "§7skrzynię §a§lEPICKĄ";
            case MYTHIC -> string = "§7skrzynię §d§lMITYCZNĄ";
            case LEGENDARY -> string = "§7skrzynię §5§lLEGENDARNĄ";
            case EASTER -> string = "§7skrzynię §e§lWIELKANOCNĄ";
        }
        if (!rewards) return string;
        switch (chestType) {
            case COMMON -> string = "§7§lPOSPOLITEJ";
            case RARE -> string = "§b§lRZADKIEJ";
            case EPIC -> string = "§a§lEPICKIEJ";
            case MYTHIC -> string = "§d§lMITYCZNEJ";
            case LEGENDARY -> string = "§5§lLEGENDARNEJ";
            case EASTER -> string = "§e§lWIELKANOCNEJ";
        }
        return string;
    }

}
