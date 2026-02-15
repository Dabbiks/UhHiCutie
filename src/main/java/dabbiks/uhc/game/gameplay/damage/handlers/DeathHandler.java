package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static dabbiks.uhc.Main.*;

public class DeathHandler {

    public void handle(Player player) {
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        dropFullInventory(player);

        player.setGameMode(GameMode.SPECTATOR);
        stateU.setPlayerState(player, PlayerState.SPECTATOR);

        if (sessionData.getDamager() != null && timeU.getTime() - sessionData.getDamagerTime() < 60) {
            rewardU.kill(sessionData.getDamager());
            rewardU.death(player);
            for (Player assistPlayer : sessionData.getAssists()) rewardU.assist(assistPlayer);

            messageU.sendMessageToPlayers(playerListU.getAllPlayers(),
                    "§c§lELIMINACJA! §7" + sessionData.getDamager().getName() + " zabija " + player.getName() + "!");
            return;
        }

        rewardU.death(player);
        messageU.sendMessageToPlayers(playerListU.getAllPlayers(),
                "§c§lELIMINACJA! §7" + player.getName() + " umiera!");
    }

    private void dropFullInventory(Player player) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        if (world == null) return;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                world.dropItemNaturally(loc, item);
            }
        }
        player.getInventory().clear();

        ItemStack cursorItem = player.getItemOnCursor();
        if (cursorItem.getType() != Material.AIR) {
            world.dropItemNaturally(loc, cursorItem);
            player.setItemOnCursor(null);
        }
    }

}
