package dabbiks.uhc.lobby;

import dabbiks.uhc.menu.ChampionMenu;
import dabbiks.uhc.menu.RecipeMenu;
import dabbiks.uhc.menu.cosmetics.CosmeticsMainMenu;
import dabbiks.uhc.menu.wiki.WikiMainMenu;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;
import java.util.function.Function;

import static dabbiks.uhc.Main.*;

public class LobbyItems implements Listener {

    public static ItemStack champions;
    public static ItemStack recipes;
    public static ItemStack wiki;
    public static ItemStack cosmetics;
    public static ItemStack spectator;

    public LobbyItems() {
        init();
    }

    private void init() {
        champions = createLobbyItem(Material.IRON_PICKAXE, "§f" + symbolU.MOUSE_RIGHT + " Przeglądaj klasy", 10000, "CHAMPIONS");
        recipes = createLobbyItem(Material.BOOK, "§f" + symbolU.MOUSE_RIGHT + " Przeglądaj przepisy", 10000, "RECIPE_BOOK");
        wiki = createLobbyItem(Material.FLOW_BANNER_PATTERN, "§f" + symbolU.MOUSE_RIGHT + " Wikipedia", 10000, "WIKI");
        cosmetics = createLobbyItem(Material.EMERALD, "§f" + symbolU.MOUSE_RIGHT + " Dodatki", 10000, "COSMETICS");
        spectator = createLobbyItem(Material.ENDER_EYE, "§f" + symbolU.MOUSE_RIGHT + " Dołącz jako obserwujący", 10000, "SPECTATOR");
    }

    private ItemStack createLobbyItem(Material material, String name, int model, String id) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setCustomModelData(model);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setInteger(id, 1));
        return item;
    }

    @EventHandler
    public void onPlayerRightClickEvent(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() == Material.AIR) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        PersistentData data = PersistentDataManager.getData(player.getUniqueId());

        if (Boolean.TRUE.equals(NBT.get(item, (Function<ReadableItemNBT, Object>) nbt -> nbt.hasTag("RECIPE_BOOK")))) {
            event.setCancelled(true);
            new RecipeMenu(player, INSTANCE.getRecipeManager()).open(player);
        }

        if (!event.getPlayer().getLocation().getWorld().getName().equals("world")) return;

        if (Boolean.TRUE.equals(NBT.get(item, (Function<ReadableItemNBT, Object>) nbt -> nbt.hasTag("CHAMPIONS")))) {
            event.setCancelled(true);
            new ChampionMenu(player, data).open(player);
        }

        if (Boolean.TRUE.equals(NBT.get(item, (Function<ReadableItemNBT, Object>) nbt -> nbt.hasTag("WIKI")))) {
            event.setCancelled(true);
            new WikiMainMenu(player).open(player);
        }

        if (Boolean.TRUE.equals(NBT.get(item, (Function<ReadableItemNBT, Object>) nbt -> nbt.hasTag("COSMETICS")))) {
            event.setCancelled(true);
            new CosmeticsMainMenu(player, data).open(player);
        }

        if (Boolean.TRUE.equals(NBT.get(item, (Function<ReadableItemNBT, Object>) nbt -> nbt.hasTag("SPECTATOR")))) {
            event.setCancelled(true);
            player.teleport(playerListU.getPlayingPlayers().getFirst().getLocation());
            playerU.clearInventory(player);
        }
    }
}