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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

import static dabbiks.uhc.Main.plugin;

public class LobbyTopManager {

    private static final Location TOP_LOC = new Location(Bukkit.getWorld("world"), -8.99, 103.0, 5.0);
    private static final float TOP_YAW = -90f;
    private static BukkitTask currentAnimationTask;

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
        List<String> newLines = new ArrayList<>();

        if (category == TopCategory.DONATIONS) {
            if (donationsTop == null || donationsTop.isEmpty()) return;
            newLines.add("§l" + TopCategory.DONATIONS.getTitle());
            int index = 1;
            for (Map.Entry<String, Double> entry : donationsTop.entrySet()) {
                if (index > 10) break;
                double val = entry.getValue();
                String valueDisplay = "§a" + (val == Math.floor(val) ? String.format(Locale.US, "%.0f", val) : String.format(Locale.US, "%.2f", val)) + " zł";
                newLines.add("§8" + index + ". §7" + entry.getKey() + " §7- " + valueDisplay);
                index++;
            }
        } else {
            Map<String, Integer> map = tops.get(category);
            if (map == null || map.isEmpty()) return;
            newLines.add("§l" + category.getTitle());
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
                newLines.add("§8" + index + ". " + nickColor + entry.getKey() + " §7- " + valueDisplay);
                index++;
            }
        }

        animateTransition(newLines);
    }

    private static void animateTransition(List<String> newLines) {
        World world = TOP_LOC.getWorld();
        if (world == null) return;

        if (currentAnimationTask != null && !currentAnimationTask.isCancelled()) {
            currentAnimationTask.cancel();
            deleteAll();
        }

        List<TextDisplay> oldDisplays = new ArrayList<>();
        for (Entity entity : world.getEntitiesByClass(TextDisplay.class)) {
            if (entity.hasMetadata("TOP_HOLO")) {
                oldDisplays.add((TextDisplay) entity);
            }
        }

        oldDisplays.sort((a, b) -> Double.compare(b.getLocation().getY(), a.getLocation().getY()));

        double titleY = TOP_LOC.getY();
        double thresholdY = titleY + 1.5;
        double spawnY = titleY - 5.0;

        currentAnimationTask = new BukkitRunnable() {
            int oldIndex = 0;
            int newIndex = 0;
            int delayTicks = 15;
            final Set<ActiveAnim> animations = new HashSet<>();

            @Override
            public void run() {
                if (oldIndex < oldDisplays.size()) {
                    animations.add(new ActiveAnim(oldDisplays.get(oldIndex), true, null));
                    oldIndex++;
                } else if (delayTicks > 0) {
                    delayTicks--;
                } else if (newIndex < newLines.size()) {
                    Location target = TOP_LOC.clone();
                    target.setYaw(TOP_YAW);

                    double targetY = titleY;
                    if (newIndex > 0) {
                        targetY -= 0.5 + (newIndex - 1) * 0.25;
                    }
                    target.setY(targetY);

                    Location start = target.clone();
                    start.setY(spawnY);

                    TextDisplay td = createLineEntity(world, start, newLines.get(newIndex), TOP_YAW);
                    animations.add(new ActiveAnim(td, false, target));
                    newIndex++;
                }

                Iterator<ActiveAnim> it = animations.iterator();
                while (it.hasNext()) {
                    ActiveAnim anim = it.next();
                    Location loc = anim.display.getLocation();

                    if (anim.isOld) {
                        anim.velocity += 0.015;
                        loc.add(0, anim.velocity, 0);

                        anim.display.setTeleportDuration(2);
                        anim.display.teleport(loc);

                        if (loc.getY() >= thresholdY) {
                            anim.display.remove();
                            it.remove();
                        }
                    } else {
                        double dist = anim.target.getY() - loc.getY();
                        if (dist <= 0.02) {
                            anim.display.setTeleportDuration(1);
                            anim.display.teleport(anim.target);
                            it.remove();
                        } else {
                            double step = dist * 0.15;
                            if (step < 0.02) step = 0.02;
                            loc.add(0, step, 0);

                            anim.display.setTeleportDuration(2);
                            anim.display.teleport(loc);
                        }
                    }
                }

                if (oldIndex >= oldDisplays.size() && delayTicks <= 0 && newIndex >= newLines.size() && animations.isEmpty()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static TextDisplay createLineEntity(World world, Location loc, String text, float yaw) {
        return world.spawn(loc, TextDisplay.class, td -> {
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

    private static class ActiveAnim {
        TextDisplay display;
        boolean isOld;
        Location target;
        double velocity = 0;

        ActiveAnim(TextDisplay display, boolean isOld, Location target) {
            this.display = display;
            this.isOld = isOld;
            this.target = target;
        }
    }
}