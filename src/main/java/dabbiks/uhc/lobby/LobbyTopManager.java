package dabbiks.uhc.lobby;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dabbiks.uhc.player.rank.RankType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

import static dabbiks.uhc.Main.plugin;

public class LobbyTopManager {

    private static final Location TOP_LOC = new Location(Bukkit.getWorld("world"), -8.99, 103.0, 5.0);
    private static final float TOP_YAW = -90f;

    public enum TopCategory {
        COINS("MONETY"),
        POWDER("PROSZEK"),
        RANKING("RANKING"),
        WINS("WYGRANE"),
        PLAYED("ROZEGRANE"),
        KILLS("ZABÓJSTWA"),
        GLORY("CHWAŁA"),
        DONATIONS("WPŁATY");

        private final String title;

        TopCategory(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    private static final Map<TopCategory, Map<String, Integer>> tops = new EnumMap<>(TopCategory.class);
    private static Map<String, Double> donationsTop = new LinkedHashMap<>();

    public static void loadDonations() {
        File file = new File(plugin.getDataFolder(), "donations.txt");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception ignored) {}
            return;
        }

        Map<String, Double> donations = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] split = line.split(";");
                if (split.length == 2) {
                    try {
                        donations.put(split[0].trim(), Double.parseDouble(split[1].trim()));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception ignored) {}

        donationsTop = sortByValueDescending(donations);
    }

    public static void loadTops() {
        File folder = new File(plugin.getDataFolder() + "/player-data");
        if (!folder.exists() || !folder.isDirectory()) return;

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        Map<String, Integer> coins = new HashMap<>();
        Map<String, Integer> powder = new HashMap<>();
        Map<String, Integer> ranking = new HashMap<>();
        Map<String, Integer> wins = new HashMap<>();
        Map<String, Integer> played = new HashMap<>();
        Map<String, Integer> kills = new HashMap<>();
        Map<String, Integer> glory = new HashMap<>();

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                String name = root.has("name") && !root.get("name").isJsonNull() ? root.get("name").getAsString() : "Unknown";
                JsonObject stats = root.has("stats") ? root.getAsJsonObject("stats") : new JsonObject();

                coins.put(name, getStat(stats, "COINS"));
                powder.put(name, getStat(stats, "POWDER"));
                ranking.put(name, getStat(stats, "RANK_PR"));
                wins.put(name, getStat(stats, "WINS"));
                played.put(name, getStat(stats, "PLAYED"));
                kills.put(name, getStat(stats, "KILLS"));
                glory.put(name, getStat(stats, "GLORY"));
            } catch (Exception ignored) {
            }
        }

        tops.put(TopCategory.COINS, sortByValueDescending(coins));
        tops.put(TopCategory.POWDER, sortByValueDescending(powder));
        tops.put(TopCategory.RANKING, sortByValueDescending(ranking));
        tops.put(TopCategory.WINS, sortByValueDescending(wins));
        tops.put(TopCategory.PLAYED, sortByValueDescending(played));
        tops.put(TopCategory.KILLS, sortByValueDescending(kills));
        tops.put(TopCategory.GLORY, sortByValueDescending(glory));
    }

    private static int getStat(JsonObject stats, String key) {
        return stats.has(key) ? stats.get(key).getAsInt() : 0;
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public static String formatNumber(int number) {
        if (number >= 1000000) {
            double mln = number / 1000000.0;
            return mln == Math.floor(mln) ? String.format(Locale.US, "%.0fmln", mln) : String.format(Locale.US, "%.1fmln", mln);
        } else if (number >= 1000) {
            double k = number / 1000.0;
            return k == Math.floor(k) ? String.format(Locale.US, "%.0fk", k) : String.format(Locale.US, "%.1fk", k);
        }
        return String.valueOf(number);
    }

    public static void displayTop(TopCategory category) {
        deleteAll();

        if (category == TopCategory.DONATIONS) {
            displayDonations();
            return;
        }

        Map<String, Integer> map = tops.get(category);
        if (map == null || map.isEmpty()) return;

        World world = TOP_LOC.getWorld();
        if (world == null) return;

        Location current = TOP_LOC.clone();

        createLine(world, current, "§l" + category.getTitle(), TOP_YAW);
        current.subtract(0, 0.5, 0);

        int index = 1;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (index > 10) break;

            String nickColor = switch (index) {
                case 1 -> "§6";
                case 2 -> "§f";
                case 3 -> "§c";
                default -> "§7";
            };

            String valueDisplay;
            if (category == TopCategory.RANKING) {
                RankType rank = RankType.getByPoints(entry.getValue());
                valueDisplay = "§f" + rank.getDisplayName();
            } else {
                valueDisplay = "§e" + formatNumber(entry.getValue());
            }

            String line = "§8" + index + ". " + nickColor + entry.getKey() + " §7- " + valueDisplay;
            createLine(world, current, line, TOP_YAW);
            current.subtract(0, 0.25, 0);

            index++;
        }
    }

    private static void displayDonations() {
        if (donationsTop == null || donationsTop.isEmpty()) return;

        World world = TOP_LOC.getWorld();
        if (world == null) return;

        Location current = TOP_LOC.clone();

        createLine(world, current, "§l" + TopCategory.DONATIONS.getTitle(), TOP_YAW);
        current.subtract(0, 0.5, 0);

        int index = 1;
        for (Map.Entry<String, Double> entry : donationsTop.entrySet()) {
            if (index > 10) break;

            String nickColor = "§7";
            double val = entry.getValue();
            String valueDisplay = "§a" + (val == Math.floor(val) ? String.format(Locale.US, "%.0f", val) : String.format(Locale.US, "%.2f", val)) + " zł";

            String line = "§8" + index + ". " + nickColor + entry.getKey() + " §7- " + valueDisplay;
            createLine(world, current, line, TOP_YAW);
            current.subtract(0, 0.25, 0);

            index++;
        }
    }

    private static void createLine(World world, Location loc, String text, float yaw) {
        world.spawn(loc, TextDisplay.class, td -> {
            td.setText("§r" + text);
            td.setBillboard(Display.Billboard.FIXED);
            td.setShadowed(true);
            td.setRotation(yaw, 0);
            td.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            td.setMetadata("TOP_HOLO", new FixedMetadataValue(plugin, true));
        });
    }

    public static void deleteAll() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(TextDisplay.class)) {
                if (entity.hasMetadata("TOP_HOLO")) {
                    entity.remove();
                }
            }
        }
    }
}