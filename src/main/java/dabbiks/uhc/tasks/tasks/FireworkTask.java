package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.gameplay.bossbar.SegmentBossBar;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.tasks.Task;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static dabbiks.uhc.Main.playerListU;
import static dabbiks.uhc.Main.symbolU;

public class FireworkTask extends Task {

    @Override
    protected long getPeriod() {
        return 20;
    }

    @Override
    protected void tick() {
        for (Player player : playerListU.getPlayingPlayers()) {
            SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
            int charges = sessionData.getElytraCharges();
            int max = sessionData.getMaxElytraCharges();

            StringBuilder bar = new StringBuilder();
            bar.append(symbolU.FUEL.repeat(Math.max(0, charges)));
            bar.append(symbolU.NO_FUEL.repeat(Math.max(0, max - charges)));

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(bar.toString()));
        }
    }
}
