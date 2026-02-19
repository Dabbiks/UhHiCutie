package dabbiks.uhc.player.traffic;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.configs.LobbyConfig;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dabbiks.uhc.Main.*;
import static dabbiks.uhc.game.gameplay.bossbar.SegmentBossBar.mainBossBar;

public class JoinEvent implements Listener {

    public static Map<Player, FastBoard> boards = new HashMap<>();
    List<NamespacedKey> toRemove = new RecipeRemover().getRemovedRecipeKeys();
    private final PrefixManager prefixManager = INSTANCE.getPrefixManager();

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
        setupPlayerState(player);

        player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 100, 0.5));
        player.setRotation(0, -5);

        handleJoinMessages(player);
    }

    private void handleInitialData(Player player, PersistentData data) {
        if (!player.hasPlayedBefore()) {
            data.addStats(PersistentStats.FREECLASSTOKENS, 1);
        }

        if (data.getUnlockedChampions() == null || data.getUnlockedChampions().isEmpty()) {
            data.addUnlockedChampion("default");
            data.setChampion("default");
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

    private void setupPlayerState(Player player) {
        stateU.setPlayerState(player, PlayerState.LOBBY);
        player.setGameMode(GameMode.ADVENTURE);
        playerU.cleanseState(player);
        attributeManager.clearAllAttributes(player);

        player.getInventory().setItem(0, LobbyItems.champions);
        player.getInventory().setItem(1, LobbyItems.recipes);
        player.getInventory().setItem(7, LobbyItems.wiki);
        player.getInventory().setItem(8, LobbyItems.cosmetics);
        if (stateU.getGameState() == GameState.IN_GAME) player.getInventory().setItem(4, LobbyItems.spectator);
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