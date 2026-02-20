package dabbiks.uhc.menu.cosmetics;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.*;

public class PurchaseMessage {

    public static void send(Player player, String item, int price, boolean coins) {
        List<String> message = new ArrayList<>();
        message.add(" ");
        message.add("§5» §7Gracz §d" + player.getName() + "§7 zdobywa");
        message.add(" §f" + item);
        if (coins) message.add(" §7o wartości §f" + price + symbolU.SCOREBOARD_COIN);
        if (!coins) message.add(" §7o wartości §f" + price + symbolU.SCOREBOARD_POWDER);
        message.add(" ");
        for (String string : message) {
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), string);
        }
    }

}
