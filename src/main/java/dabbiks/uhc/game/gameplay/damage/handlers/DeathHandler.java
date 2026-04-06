package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.GameData;
import dabbiks.uhc.game.gameplay.Victory;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.function.Function;

import static dabbiks.uhc.Main.*;

public class DeathHandler {

    public void handle(Player player) {
        if (GameData.isEnding) return;

        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        if (sessionData.hasTag(SessionTags.IMMORTAL_EXPERIENCE)) {
            sessionData.removeTag(SessionTags.IMMORTAL_EXPERIENCE);

            int xpLevel = Math.min(player.getLevel(), 30);
            double scale = xpLevel / 30.0;

            player.setHealth(1.0);
            player.setFireTicks(0);
            player.playEffect(EntityEffect.TOTEM_RESURRECT);
            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);

            if (xpLevel > 0) {
                int regenTicks = 60 + (int) (140 * scale);
                int absTicks = 20 + (int) (60 * scale);
                int absAmp = 4;

                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, regenTicks, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, absTicks, absAmp));
            }

            player.setLevel(0);
            player.setExp(0);
            return;
        }

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
                playerU.addHealth(killer, 6);
                killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 1));

                SessionData killerSessionData = SessionDataManager.getData(killer.getUniqueId());

                if (killerSessionData.hasTag(SessionTags.ENCHANTED_DUELIST)) {
                    int levels = Math.min(player.getLevel(), 30);
                    int bonusHealthChunks = levels / 5;
                    if (bonusHealthChunks > 0) {
                        double currentMaxHealth = killer.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
                        double healthToAdd = bonusHealthChunks * 1.0;
                        killer.getAttribute(Attribute.MAX_HEALTH).setBaseValue(currentMaxHealth + healthToAdd);
                        playerU.addHealth(killer, healthToAdd);
                    }
                }

                long currentTime = timeU.getTime();
                if (currentTime - killerSessionData.getLastKillTime() < 60) {
                    killerSessionData.setKillStreak(killerSessionData.getKillStreak() + 1);
                } else {
                    killerSessionData.setKillStreak(1);
                }
                killerSessionData.setLastKillTime(currentTime);

                int streak = killerSessionData.getKillStreak();
                if (streak > 1) {
                    String streakName = "§0ʟᴇɢᴇɴᴅᴀ";
                    switch (streak) {
                        case 2: streakName = "§aᴅᴏᴜʙʟᴇ ᴋɪʟʟ"; break;
                        case 3: streakName = "§bᴛʀɪᴘʟᴇ ᴋɪʟʟ"; break;
                        case 4: streakName = "§9ǫᴜᴀᴅʀᴀ ᴋɪʟʟ"; break;
                        case 5: streakName = "§eᴘᴇɴᴛᴀ ᴋɪʟʟ"; break;
                        case 6: streakName = "§6ʜᴇxᴀ ᴋɪʟʟ"; break;
                        case 7: streakName = "§cʜᴇᴘᴛᴀ ᴋɪʟʟ"; break;
                        case 8: streakName = "§dᴏᴄᴛᴀ ᴋɪʟʟ"; break;
                        case 9: streakName = "§5ɴᴏɴᴀ ᴋɪʟʟ"; break;
                        case 10: streakName = "§4ᴅᴇᴄᴀ ᴋɪʟʟ"; break;
                    }
                    titleU.sendTitleToPlayers(playerListU.getAllPlayers(), streakName, "§7" + killer.getName(), 30);
                }

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

    private void dropItemEntity(World world, Location loc, ItemStack itemStack) {
        world.dropItemNaturally(loc, itemStack);
    }

    private void dropFullInventory(Player player) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        if (world == null) return;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR || isPersonalItem(item)) continue;
            dropItemEntity(world, loc, item);
        }
        player.getInventory().clear();

        ItemStack cursorItem = player.getItemOnCursor();
        if (cursorItem.getType() != Material.AIR) {
            if (!isPersonalItem(cursorItem)) dropItemEntity(world, loc, cursorItem);
            player.setItemOnCursor(null);
        }
    }
}