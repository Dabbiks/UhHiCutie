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
        if (player == null) return;
        if (rewards == null) {
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "");
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "§e" + player.getName() + " §7otwiera");
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), getChestString(chestType, false));
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "");
        } else {
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "");
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "§e" + player.getName() + " §7znajduje");
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "§7w skrzyni " + getChestString(chestType, true));
            for (String string : rewards) {
                messageU.sendMessageToPlayers(playerListU.getAllPlayers(), string);
            }
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "");
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
        }
        if (!rewards) return string;
        switch (chestType) {
            case COMMON -> string = "§7§lPOSPOLITEJ";
            case RARE -> string = "§b§lRZADKIEJ";
            case EPIC -> string = "§a§lEPICKIEJ";
            case MYTHIC -> string = "§d§lMITYCZNEJ";
            case LEGENDARY -> string = "§5§lLEGENDARNEJ";
        }
        return string;
    }

}
