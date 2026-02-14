package dabbiks.uhc.lobby;

import dabbiks.uhc.game.configs.WorldConfig;
import dabbiks.uhc.menu.ChampionMenu;
import dabbiks.uhc.menu.RecipeMenu;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import de.tr7zw.nbtapi.NBTItem;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static dabbiks.uhc.Main.*;

public class LobbyItems implements Listener {

    public static ItemStack championBook;
    public static ItemStack recipeBook;
    public static ItemStack spectate;

    public LobbyItems() {
        init();
    }

    private void init() {
        championBook = createLobbyItem(Material.IRON_PICKAXE, "§f" + symbolU.MOUSE_LEFT + " Przeglądaj klasy", 10000, "CHAMPIONS");
        recipeBook = createLobbyItem(Material.BOOK, "§f" + symbolU.MOUSE_LEFT + " Przeglądaj przepisy", 10000, "RECIPE_BOOK");
        spectate = createLobbyItem(Material.ENDER_EYE, "§f" + symbolU.MOUSE_LEFT + " Dołącz jako obserwujący", 10000, "SPECTATOR");
    }

    private ItemStack createLobbyItem(Material material, String name, int model, String id) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setCustomModelData(model);
        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(id, 1);
        return nbtItem.getItem();
    }

    @EventHandler
    public void onPlayerRightClickEvent(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!event.getPlayer().getLocation().getWorld().getName().equals("world")) return;

        event.setCancelled(true);

        ItemStack item = event.getItem();
        NBTItem nbtItem = new NBTItem(item);
        Player player = event.getPlayer();
        PersistentData data = PersistentDataManager.getData(player.getUniqueId());

        if (nbtItem.hasTag("CHAMPIONS")) {
            new ChampionMenu(player, data).open(player);
        }

        if (nbtItem.hasTag("RECIPE_BOOK")) {
            new RecipeMenu(player, INSTANCE.getRecipeManager()).open(player);
        }

        if (nbtItem.hasTag("SPECTATOR")) {
            player.teleport(playerListU.getPlayingPlayers().getFirst().getLocation());
            playerU.clearInventory(player);
        }
    }
}
