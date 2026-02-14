package dabbiks.uhc.player.tab;

import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.event.EventHandler;
import me.neznamy.tab.api.event.player.PlayerLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static dabbiks.uhc.Main.plugin;

public class TabEvent implements EventHandler<PlayerLoadEvent> {

    TabUtils tabU = new TabUtils();

    @Override
    public void handle(PlayerLoadEvent event) {
        TabPlayer tabPlayer = event.getPlayer();
        Player player = Bukkit.getPlayer(tabPlayer.getName());
        tabU.setTabHeader(player, "");
        tabU.setTabFooter(player, "");
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
//        sessionData.updatePlayerPrefix(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
//            SessionDataManager.getData(player.getUniqueId()).updatePlayerPrefix(player);
        }, 20L);
    }

}