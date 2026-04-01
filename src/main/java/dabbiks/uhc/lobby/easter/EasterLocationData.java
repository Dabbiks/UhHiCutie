package dabbiks.uhc.lobby.easter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dabbiks.uhc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EasterLocationData {
    private static final File file = new File(Main.plugin.getDataFolder(), "easter_locations.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final List<Location> locations = new ArrayList<>();

    public static void load() {
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> list = gson.fromJson(reader, type);
            if (list != null) {
                for (Map<String, Object> map : list) {
                    World world = Bukkit.getWorld((String) map.get("world"));
                    if (world != null) {
                        double x = ((Number) map.get("x")).doubleValue();
                        double y = ((Number) map.get("y")).doubleValue();
                        double z = ((Number) map.get("z")).doubleValue();
                        locations.add(new Location(world, x, y, z));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addLocation(Location loc) {
        locations.add(loc);
        save();
    }

    public static void save() {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            List<Map<String, Object>> list = new ArrayList<>();
            for (Location loc : locations) {
                Map<String, Object> map = new HashMap<>();
                map.put("world", loc.getWorld().getName());
                map.put("x", loc.getX());
                map.put("y", loc.getY());
                map.put("z", loc.getZ());
                list.add(map);
            }
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(list, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Location> getLocations() {
        return locations;
    }
}