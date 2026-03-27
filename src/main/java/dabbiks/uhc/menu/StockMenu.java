package dabbiks.uhc.menu;

import dabbiks.uhc.lobby.stock.StockData;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static dabbiks.uhc.Main.soundU;
import static dabbiks.uhc.Main.symbolU;

public class StockMenu extends FastInv {

    private final Player player;
    private final PersistentData data;
    private final StockData stockData;

    public StockMenu(Player player, PersistentData data, StockData stockData) {
        super(9, "Inwestycje");
        this.player = player;
        this.data = data;
        this.stockData = stockData;

        soundU.playSoundToPlayer(player, Sound.ITEM_BOOK_PAGE_TURN, 0.5F, 1);
        render();
    }

    private void render() {
        double price = stockData.getCurrentPrice();
        int ownedStock = data.getStats().getOrDefault(PersistentStats.STOCK, 0);

        setItem(0, createBuyItem(Material.GOLD_NUGGET, symbolU.MOUSE_LEFT + " §cKup 1 akcję", price), e -> handleBuy(1, price));
        setItem(1, createBuyItem(Material.GOLD_INGOT, symbolU.MOUSE_LEFT + " §cKup 5 akcji", price * 5), e -> handleBuy(5, price * 5));
        setItem(2, createBuyItem(Material.RAW_GOLD, symbolU.MOUSE_LEFT + " §cKup 20 akcji", price * 20), e -> handleBuy(20, price * 20));

        ItemStack infoItem = new ItemStack(Material.SUNFLOWER);
        ItemMeta meta = infoItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§cTwoje inwestycje");
            meta.setLore(List.of(
                    "§fTwoje akcje: §e" + ownedStock,
                    "§fWartość bez prowizji: §e" + String.format("%.1f", ownedStock * price) + symbolU.SCOREBOARD_COIN,
                    "§fWartość z prowizją: §a" + String.format("%.1f", ownedStock * price * 0.95) + symbolU.SCOREBOARD_COIN
            ));
            infoItem.setItemMeta(meta);
        }
        setItem(4, infoItem);

        setItem(6, createSellItem(Material.WHITE_CANDLE, symbolU.MOUSE_LEFT + " §cSprzedaj 1 akcję", 1, price), e -> handleSell(1, price));
        setItem(7, createSellItem(Material.LIME_CANDLE, symbolU.MOUSE_LEFT + " §cSprzedaj 5 akcji", 5, price), e -> handleSell(5, price));
        setItem(8, createSellItem(Material.GREEN_CANDLE, symbolU.MOUSE_LEFT + " §cSprzedaj wszystkie akcje", ownedStock, price), e -> handleSell(ownedStock, price));
    }

    private ItemStack createBuyItem(Material material, String name, double cost) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of("§fCena§7: §e" + String.format("%.1f", cost) + symbolU.SCOREBOARD_COIN));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createSellItem(Material material, String name, int amount, double price) {
        double revenue = price * amount;
        double commission = revenue * 0.05;
        double finalRevenue = revenue - commission;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of(
                    "§fWartość bazowa: §e" + String.format("%.1f", revenue) + symbolU.SCOREBOARD_COIN,
                    "§cProwizja (5%): §c-" + String.format("%.1f", commission) + symbolU.SCOREBOARD_COIN,
                    "§aOtrzymasz: §a" + String.format("%.1f", finalRevenue) + symbolU.SCOREBOARD_COIN
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private void handleBuy(int amount, double cost) {
        int coins = data.getStats().getOrDefault(PersistentStats.COINS, 0);

        if (coins >= cost) {
            data.removeStats(PersistentStats.COINS, (int) cost);
            data.addStats(PersistentStats.STOCK, amount);
            soundU.playSoundToPlayer(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            render();
        } else {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
        }
    }

    private void handleSell(int amount, double price) {
        int ownedStock = data.getStats().getOrDefault(PersistentStats.STOCK, 0);

        if (ownedStock >= amount && amount > 0) {
            double revenue = price * amount;
            double finalRevenue = revenue * 0.95;

            data.removeStats(PersistentStats.STOCK, amount);
            data.addStats(PersistentStats.COINS, (int) finalRevenue);
            soundU.playSoundToPlayer(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            render();
        } else {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
        }
    }
}