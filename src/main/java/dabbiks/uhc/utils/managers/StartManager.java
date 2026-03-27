package dabbiks.uhc.utils.managers;

import dabbiks.uhc.Main;
import dabbiks.uhc.game.gameplay.champions.Champion;
import dabbiks.uhc.game.gameplay.champions.ChampionManager;
import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.world.events.WeatherCycle;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.lobby.LobbyItems;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.rank.RankManager;
import dabbiks.uhc.player.tab.TabUtils;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.*;

public class StartManager implements Listener {

    public void processStart() {
        prepareWorldBorder();
        prepareTeams();
        prepareTab();
        preparePlayers();
        stockData.updateStockPrice();

        Bukkit.getPluginManager().registerEvents(this, Main.plugin);
        TeamUtils.createCagesAndTeleport(200);

        new BukkitRunnable() {
            int time = 20;

            @Override
            public void run() {
                List<Player> players = playerListU.getAllPlayers();
                if (time > 0) {
                    titleU.sendTitleToPlayers(players, "§cStart za", "§e" + time, 60);
                    soundU.playSoundToPlayers(players, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.8f, 1);
                    time--;
                } else {
                    titleU.sendTitleToPlayers(players, "§aSTART!", "", 60);
                    soundU.playSoundToPlayers(players, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.8f, 1);
                    TeamUtils.removeCages();
                    HandlerList.unregisterAll(StartManager.this);

                    String[] messages;
                    messages = new String[]{
                            "§c§lETAP I",
                            "§fZdobywaj surowce i twórz przedmioty,",
                            "§fPvP zostaje włączone w Etapie II.",
                            "§fOstatnia drużyna wygrywa! Powodzenia"
                    };
                    messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "");
                    for (String msg : messages) {
                        messageU.sendMessageToPlayers(playerListU.getAllPlayers(), msg);
                    }
                    messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "");

                    this.cancel();
                }
            }
        }.runTaskTimer(Main.plugin, 0L, 20L);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (TeamUtils.hasCages() && event.getBlock().getY() >= 250 && event.getBlock().getY() <= 256) {
            event.setCancelled(true);
        }
    }

    private void prepareWorldBorder() {
        INSTANCE.getWorldBorder().prepareWorldBorder();
    }

    private void prepareTeams() {
        TeamUtils.balanceTeams();
    }

    private void prepareTab() {
        new TabUtils().setGlobalTabFooter("\n" + WeatherCycle.getWeatherIcon() + "\n");
    }

    private void preparePlayers() {
        ChampionManager championManager = new ChampionManager();
        List<String> championMessage = new ArrayList<>();
        championMessage.add("");
        for (Player player : playerListU.getAllPlayers()) {
            stateU.setPlayerState(player, PlayerState.ALIVE);
            RankManager.calculatePlayerModifier(player);

            playerU.cleanseState(player);

            attributeManager.addModifier(player, Attribute.WAYPOINT_RECEIVE_RANGE, "waypoint_receive", 1000, AttributeModifier.Operation.ADD_NUMBER);
            attributeManager.addModifier(player, Attribute.WAYPOINT_TRANSMIT_RANGE, "waypoint_transmit", 1000, AttributeModifier.Operation.ADD_NUMBER);

            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 10, false, false));

            player.getInventory().setItem(8, LobbyItems.recipes);

            PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
            if (persistentData.getChampion() == null) persistentData.addUnlockedChampion("default");
            if (persistentData.getChampion() == null) persistentData.setChampion("default");
            Champion champion = championManager.getChampion(persistentData.getChampion());
            champion.onStart(player, persistentData.getChampionLevel(persistentData.getChampion()));
            championMessage.add("§7» §f" + player.getName() + " §e" + champion.getName() + " §8(Poz. " + persistentData.getChampionLevel(champion.getId()) + ")");
        }
        championMessage.add("");
        List<Player> players = playerListU.getAllPlayers();
        for (String string : championMessage) messageU.sendMessageToPlayers(players, string);
    }
}