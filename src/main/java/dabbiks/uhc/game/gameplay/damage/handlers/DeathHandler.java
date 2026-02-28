package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.cosmetics.KillSound;
import dabbiks.uhc.game.gameplay.Victory;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import org.bukkit.*;
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
            Player killer = sessionData.getDamager();

            rewardU.kill(killer);
            rewardU.death(player);
            rewardU.summary(player);
            for (Player assistPlayer : sessionData.getAssists()) rewardU.assist(assistPlayer);

            messageU.sendMessageToPlayers(playerListU.getAllPlayers(),
                    "§c§lELIMINACJA! §7" + killer.getName() + " zabija " + player.getName() + "!");

            PersistentData killerData = PersistentDataManager.getData(killer.getUniqueId());
            if (killerData != null && killerData.getKillSound() != null) {
                String soundName = killerData.getKillSound().getSound();
                soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.valueOf(soundName), 0.6f, 1);
            } else {
                soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.valueOf(KillSound.BLASTX.getSound()), 0.6f, 1);
            }

        } else {
            rewardU.death(player);
            rewardU.summary(player);
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(),
                    "§c§lELIMINACJA! §7" + player.getName() + " umiera!");
        }

        Victory.processWin();
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