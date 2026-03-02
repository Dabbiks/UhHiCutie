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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.*;

public class StartManager {

    ItemStack firework = new ItemStack(Material.BARRIER);
    ChestplateManager chestplateManager = INSTANCE.getChestplateManager();

    public void processStart() {
        setFireworkItem();
        prepareWorldBorder();
        prepareTeams();
        prepareTab();
        preparePlayers();
        prepareFlight();
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
        TeamUtils.teleportTeamsRandomly(300, 300);
    }

    private void prepareFlight() {
        final int COOLDOWN_TICKS = 100;
        final double BOOST_MULTIPLIER = 1.5;

        for (Player player : playerListU.getPlayingPlayers()) {
            if (player.hasCooldown(Material.FIREWORK_ROCKET)) return;
            player.setCooldown(Material.FIREWORK_ROCKET, COOLDOWN_TICKS);

            Vector velocity = player.getVelocity();
            velocity.add(new Vector(0, 0.5, 0));
            velocity.multiply(1.5);
            player.setVelocity(velocity);

            Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
                ItemStack currentChest = player.getInventory().getChestplate();
                if (currentChest != null && currentChest.getType() != Material.ELYTRA) {
                    chestplateManager.saveChestplate(player.getUniqueId(), currentChest.clone());
                }

                player.getInventory().setChestplate(createCustomElytra());
                player.setGliding(true);
                player.setVelocity(player.getLocation().getDirection().multiply(BOOST_MULTIPLIER));
            }, 5L);
        }
    }

    private ItemStack createCustomElytra() {
        ItemInstance instance = new ItemInstance();
        instance.setMaterial(Material.ELYTRA.name());
        instance.setAmount(1);
        instance.setEquipmentSlot(EquipmentSlot.CHEST);

        List<AttributeData> attributes = new ArrayList<>();
        attributes.add(new AttributeData(AttributeType.ARMOR, 3.0));
        attributes.add(new AttributeData(AttributeType.GRAVITY_PERCENT, -10.0));
        instance.setAttributes(attributes);

        return new ItemBuilder(instance).build();
    }

}