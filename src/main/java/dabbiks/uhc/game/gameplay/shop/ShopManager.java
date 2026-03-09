package dabbiks.uhc.game.gameplay.shop;

import dabbiks.uhc.game.world.pathfinder.PathSmoother;
import dabbiks.uhc.game.world.pathfinder.Pathfinder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;

import static dabbiks.uhc.Main.plugin;

public class ShopManager {

    private HashMap<Integer, Location> path = new HashMap<>();

    public void spawnShops() {
        findPathForShops();

        new ItemShopEntity(path, 0);
    }

    private void findPathForShops() {
        Pathfinder.generatePathAsync(175, 325, 180, 160).thenAccept(rawPathMap -> {

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (rawPathMap != null && !rawPathMap.isEmpty()) {

                    path = PathSmoother.smoothPath(rawPathMap, 20);
                }
            });


        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}
