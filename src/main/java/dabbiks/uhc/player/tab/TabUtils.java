package dabbiks.uhc.player.tab;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import org.bukkit.entity.Player;

import static dabbiks.uhc.Main.playerListU;

public class TabUtils {
    public TabAPI instance = TabAPI.getInstance();

    public void setPlayerTabPrefix(Player player, String prefix) {
        if (!player.isOnline()) return;
        TabPlayer tabPlayer = instance.getPlayer(player.getUniqueId());
        instance.getTabListFormatManager().setPrefix(tabPlayer, prefix);
        instance.getNameTagManager().setPrefix(tabPlayer, prefix);
    }

    public void setPlayerTabSuffix(Player player, String suffix) {
        if (!player.isOnline()) return;
        TabPlayer tabPlayer = instance.getPlayer(player.getUniqueId());
        instance.getTabListFormatManager().setSuffix(tabPlayer, suffix);
        instance.getNameTagManager().setSuffix(tabPlayer, suffix);
    }

    public void setTabHeader(Player player, String header) {
        if (!player.isOnline()) return;
        TabPlayer tabPlayer = instance.getPlayer(player.getUniqueId());
        instance.getHeaderFooterManager().setHeader(tabPlayer, header);
    }

    public void setTabFooter(Player player, String footer) {
        if (!player.isOnline()) return;
        TabPlayer tabPlayer = instance.getPlayer(player.getUniqueId());
        instance.getHeaderFooterManager().setFooter(tabPlayer, footer);
    }

    public void setGlobalTabHeader(String header) {
        for (Player player : playerListU.getAllPlayers()) {
            if (!player.isOnline()) continue;
            TabPlayer tabPlayer = instance.getPlayer(player.getUniqueId());
            instance.getHeaderFooterManager().setHeader(tabPlayer, header);
        }
    }

    public void setGlobalTabFooter(String footer) {
        for (Player player : playerListU.getAllPlayers()) {
            if (!player.isOnline()) continue;
            TabPlayer tabPlayer = instance.getPlayer(player.getUniqueId());
            instance.getHeaderFooterManager().setFooter(tabPlayer, footer);
        }
    }
}
