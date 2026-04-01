package dabbiks.uhc.player.traffic;

import dabbiks.uhc.Main;
import dabbiks.uhc.cosmetics.Cage;
import dabbiks.uhc.cosmetics.KillSound;
import dabbiks.uhc.cosmetics.PvpSword;
import dabbiks.uhc.cosmetics.Wardrobe;
import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.configs.LobbyConfig;
import dabbiks.uhc.game.gameplay.champions.Champion;
import dabbiks.uhc.game.gameplay.champions.ChampionManager;
import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.recipes.remover.RecipeRemover;
import dabbiks.uhc.lobby.LobbyItems;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.rank.RankManager;
import dabbiks.uhc.player.rank.RankType;
import dabbiks.uhc.utils.managers.PrefixManager;
import fr.mrmicky.fastboard.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static dabbiks.uhc.Main.*;
import static dabbiks.uhc.game.gameplay.bossbar.SegmentBossBar.mainBossBar;

public class JoinEvent implements Listener {

    public static Map<Player, FastBoard> boards = new HashMap<>();
    List<NamespacedKey> toRemove = new RecipeRemover().getRemovedRecipeKeys();
    private final PrefixManager prefixManager = INSTANCE.getPrefixManager();
    private final ChampionManager championManager = new ChampionManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("");

        player.undiscoverRecipes(toRemove);

        PersistentDataManager.loadData(player.getUniqueId());
        PersistentData data = PersistentDataManager.getData(player.getUniqueId());
        SessionDataManager.getData(player.getUniqueId());

        handleInitialData(player, data);
        setupPlayerUI(player);
        setupPlayerState(player, data);
        setupPvpSword(player, data);

        player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 100, 0.5));
        player.setRotation(0, -5);

        handleJoinMessages(player);
    }

    private void handleInitialData(Player player, PersistentData data) {
        if (data.getUnlockedChampions() == null || data.getUnlockedChampions().isEmpty()) {
            data.addUnlockedChampion("default");
            data.setChampion("default");
        }

        for (Champion champion : championManager.getChampions()) {
            if (data.getUnlockedChampions().contains(champion.getId())) continue;
            data.addUnlockedChampion(champion.getId());
        }

        if (data.getKillSound() == null) {
            data.unlockKillSound(KillSound.BLASTX);
            data.setKillSound(KillSound.BLASTX);
        }

        if (data.getPvpSword() == null) {
            data.unlockPvpSword(PvpSword.WOODEN_SWORD);
            data.setPvpSword(PvpSword.WOODEN_SWORD);
        }

        if (data.getCage() == null) {
            data.unlockCage(Cage.DEFAULT);
            data.setCage(Cage.DEFAULT);
        }

        RankManager.processPlacements(player);
        if (data.getRank() == null) {
            data.setRank(RankType.UNRANKED);
        }

        if (data.getStats().getOrDefault(PersistentStats.PLAYED, 0) < 10) {
            sendDiscordInvite(player);
        }
    }

    private void setupPlayerUI(Player player) {
        FastBoard board = new FastBoard(player);
        board.updateTitle("§8  s ᴜ ᴘ ᴇ ʀ ɢ ʟ ɪ  .  ᴅ ᴇ");

        boards.put(player, board);
        mainBossBar.addPlayer(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            tabManager.setTabFooter(player, "");
            tabManager.setTabHeader(player, "");
            prefixManager.update(player);
        }, 3L);
    }

    private void setupPlayerState(Player player, PersistentData data) {
        stateU.setPlayerState(player, PlayerState.LOBBY);
        player.setGameMode(GameMode.ADVENTURE);
        playerU.cleanseState(player);
        attributeManager.clearAllAttributes(player);

        player.getInventory().setItem(0, LobbyItems.champions);
        player.getInventory().setItem(1, LobbyItems.recipes);
        player.getInventory().setItem(7, LobbyItems.settings);
        player.getInventory().setItem(8, LobbyItems.cosmetics);

        if (stateU.getGameState() == GameState.IN_GAME) player.getInventory().setItem(2, LobbyItems.spectator);

        Wardrobe h = data.getWardrobeHelmet();
        if (h != null) {
            if (h.getHeadTexture() != null && !h.getHeadTexture().isEmpty()) {
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                PlayerTextures textures = profile.getTextures();

                try {
                    String decoded = new String(Base64.getDecoder().decode(h.getHeadTexture()));
                    String url = decoded.split("\"url\":\"")[1].split("\"")[0];
                    textures.setSkin(new URL(url));
                    profile.setTextures(textures);
                    meta.setOwnerProfile(profile);
                } catch (Exception ignored) {}

                item.setItemMeta(meta);
                player.getInventory().setHelmet(item);
            } else if (h.getHelmetColor() != null) {
                ItemStack item = new ItemStack(Material.LEATHER_HELMET);
                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                meta.setColor(h.getHelmetColor());
                item.setItemMeta(meta);
                player.getInventory().setHelmet(item);
            }
        }

        Wardrobe c = data.getWardrobeChestplate();
        if (c != null && c.getChestplateColor() != null) {
            ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(c.getChestplateColor());
            item.setItemMeta(meta);
            player.getInventory().setChestplate(item);
        }

        Wardrobe l = data.getWardrobeLeggings();
        if (l != null && l.getLeggingsColor() != null) {
            ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(l.getLeggingsColor());
            item.setItemMeta(meta);
            player.getInventory().setLeggings(item);
        }

        Wardrobe b = data.getWardrobeBoots();
        if (b != null && b.getBootsColor() != null) {
            ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(b.getBootsColor());
            item.setItemMeta(meta);
            player.getInventory().setBoots(item);
        }
    }

    private void setupPvpSword(Player player, PersistentData persistentData) {
        List<AttributeData> attrs = new ArrayList<>();
        attrs.add(new AttributeData(AttributeType.ATTACK_DAMAGE, 3));
        attrs.add(new AttributeData(AttributeType.ATTACK_SPEED, -2));
        attrs.add(new AttributeData(AttributeType.CRIT_DAMAGE_PERCENT, 25));

        ItemInstance itemInstance = new ItemInstance();
        itemInstance.setName(persistentData.getPvpSword().getName());
        itemInstance.setMaterial(persistentData.getPvpSword().getMaterial().name());
        itemInstance.setAttributes(attrs);
        if (persistentData.getPvpSword().getCustomModelData() != 0)
            itemInstance.setCustomModelData(persistentData.getPvpSword().getCustomModelData());

        player.getInventory().setItem(4, new ItemBuilder(itemInstance).build());
    }

    private void sendDiscordInvite(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage(Component.empty());
            Component message = Component.text("§4§l! §r§fNajedź na mnie")
                    .clickEvent(ClickEvent.openUrl("https://discord.gg/ueAFMTYRQA"))
                    .hoverEvent(HoverEvent.showText(Component.text("§f" + symbolU.MOUSE_LEFT + " Kliknij i dołącz do discorda :)", NamedTextColor.GRAY)));
            player.sendMessage(message);
            player.sendMessage(Component.empty());
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
        }, 100L);
    }

    private void handleJoinMessages(Player player) {
        GameState state = stateU.getGameState();
        String msg = (state == GameState.WAITING || state == GameState.STARTING)
                ? "§a" + player.getName() + " §7(§f" + Bukkit.getOnlinePlayers().size() + "/" + LobbyConfig.maxPlayerCount + "§7)"
                : "§a+ §7" + player.getName();

        messageU.sendMessageToPlayers(playerListU.getAllPlayers(), msg);
    }
}