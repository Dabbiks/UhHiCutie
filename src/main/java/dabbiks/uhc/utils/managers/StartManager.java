package dabbiks.uhc.utils.managers;

import dabbiks.uhc.Main;
import dabbiks.uhc.game.gameplay.champions.Champion;
import dabbiks.uhc.game.gameplay.champions.ChampionManager;
import dabbiks.uhc.game.gameplay.elytra.ChestplateManager;
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
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.*;

public class StartManager implements Listener {

    ItemStack firework = new ItemStack(Material.BARRIER);
    ChestplateManager chestplateManager = INSTANCE.getChestplateManager();

    public void processStart() {
        setFireworkItem();
        prepareWorldBorder();
        prepareTeams();
        prepareTab();
        preparePlayers();

        Bukkit.getPluginManager().registerEvents(this, Main.plugin);
        TeamUtils.createCagesAndTeleport(200);

        new BukkitRunnable() {
            int time = 20;

            @Override
            public void run() {
                if (time > 0) {
                    titleU.sendTitleToPlayers(playerListU.getAllPlayers(), "§cStart za", "§e" + time, 20);
                    time--;
                } else {
                    titleU.sendTitleToPlayers(playerListU.getAllPlayers(), "§aSTART!", "", 20);
                    TeamUtils.removeCages();
                    HandlerList.unregisterAll(StartManager.this);
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

    private void setFireworkItem() {
        ItemInstance itemInstance = new ItemInstance();
        itemInstance.setMaterial(Material.FIREWORK_ROCKET.name());
        itemInstance.setName(symbolU.MOUSE_RIGHT + " §fAktywuj elytrę");
        itemInstance.setAmount(1);
        firework = new ItemBuilder(itemInstance).build();

        NBT.modify(firework, nbt -> {
            nbt.setInteger(ItemTags.PERSONAL.name(), 1);
        });
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
        for (Player player : playerListU.getAllPlayers()) {
            stateU.setPlayerState(player, PlayerState.ALIVE);
            RankManager.calculatePlayerModifier(player);

            playerU.cleanseState(player);

            attributeManager.addModifier(player, Attribute.WAYPOINT_RECEIVE_RANGE, "waypoint_receive", 1000, AttributeModifier.Operation.ADD_NUMBER);
            attributeManager.addModifier(player, Attribute.WAYPOINT_TRANSMIT_RANGE, "waypoint_transmit", 1000, AttributeModifier.Operation.ADD_NUMBER);

            player.getInventory().addItem(firework);
            player.getInventory().setItem(8, LobbyItems.recipes);

            PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
            if (persistentData.getChampion() == null) persistentData.addUnlockedChampion("default");
            if (persistentData.getChampion() == null) persistentData.setChampion("default");
            Champion champion = championManager.getChampion(persistentData.getChampion());
            champion.onStart(player, persistentData.getChampionLevel(persistentData.getChampion()));
        }
    }
}