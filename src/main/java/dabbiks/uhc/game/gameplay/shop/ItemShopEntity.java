package dabbiks.uhc.game.gameplay.shop;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;

import static dabbiks.uhc.Main.symbolU;

public class ItemShopEntity {

    private final ShopEntity itemShopEntity;

    public ItemShopEntity(HashMap<Integer, Location> track, int trackIndex) {
        itemShopEntity = new ShopEntity(track, trackIndex);

        itemShopEntity.spawn();
        itemShopEntity.applySkinToMannequin(symbolU.ITEM_SHOP_SKIN);
        itemShopEntity.setHarness(Material.YELLOW_HARNESS);
        itemShopEntity.startFlightTask();
    }
}
