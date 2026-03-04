package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.GameData;
import dabbiks.uhc.game.gameplay.Victory;
import dabbiks.uhc.game.gameplay.elytra.ChestplateManager;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.function.Function;

import static dabbiks.uhc.Main.*;

public class DeathHandler {

    public void handle(Player player) {
        if (GameData.isEnding) return;

        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        dropFullInventory(player);

        player.setGameMode(GameMode.SPECTATOR);
        stateU.setPlayerState(player, PlayerState.SPECTATOR);

        if (sessionData.getDamager() != null && timeU.getTime() - sessionData.getDamagerTime() < 60) {
            Player killer = sessionData.getDamager();

            rewardU.death(player);
            rewardU.summary(player);
            for (Player assistPlayer : new ArrayList<>(sessionData.getAssists())) {
                rewardU.assist(assistPlayer);
            }
            messageU.sendMessageToPlayers(
                    playerListU.getAllPlayers(), "§c§lELIMINACJA! §7" + killer.getName() + " zabija " + player.getName() + "!");

            PersistentData killerData = PersistentDataManager.getData(killer.getUniqueId());

            if (killer.isOnline()) {
                rewardU.kill(killer);
                String sound = killerData.getKillSound().getSound();
                for (Player player1 : playerListU.getAllPlayers()) player1.playSound(player1, "sounds:" + sound.toLowerCase(), 0.8f, 1f);
            } else {
                for (Player player1 : playerListU.getAllPlayers()) player1.playSound(player1, "sounds:blastx", 0.8f, 1f);
            }
        } else {
            rewardU.death(player);
            rewardU.summary(player);
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(),
                    "§c§lELIMINACJA! §7" + player.getName() + " umiera!");
        }

        Victory.processWin();
    }

    private boolean isPersonalItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        return Boolean.TRUE.equals(NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.hasTag(ItemTags.PERSONAL.name())));
    }

    private void dropFullInventory(Player player) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        if (world == null) return;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR || isPersonalItem(item)) continue;
            world.dropItemNaturally(loc, item);
        }
        player.getInventory().clear();

        ItemStack cursorItem = player.getItemOnCursor();
        if (cursorItem != null && cursorItem.getType() != Material.AIR) {
            if (!isPersonalItem(cursorItem)) world.dropItemNaturally(loc, cursorItem);
            player.setItemOnCursor(null);
        }

        ChestplateManager manager = INSTANCE.getChestplateManager();
        if (manager == null || !manager.hasSavedChestplate(player.getUniqueId())) return;

        ItemStack savedChestplate = manager.getAndRemoveChestplate(player.getUniqueId());
        if (savedChestplate == null || savedChestplate.getType() == Material.AIR || isPersonalItem(savedChestplate)) return;

        world.dropItemNaturally(loc, savedChestplate);
    }
}