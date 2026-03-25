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
        DONATIONS("WPŁATY");

        private final String title;

        TopCategory(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    private static final Map<TopCategory, Map<String, Double>> tops = new EnumMap<>(TopCategory.class);

    public static void loadTops() {
        File folder = new File(plugin.getDataFolder() + "/player-data");
        if (!folder.exists() || !folder.isDirectory()) return;

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        Map<String, Double> coins = new HashMap<>();
        Map<String, Double> powder = new HashMap<>();
        Map<String, Double> ranking = new HashMap<>();
        Map<String, Double> wins = new HashMap<>();
        Map<String, Double> played = new HashMap<>();
        Map<String, Double> kills = new HashMap<>();
        Map<String, Double> glory = new HashMap<>();
        Map<String, Double> donations = new HashMap<>();

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                String name = root.has("name") && !root.get("name").isJsonNull() ? root.get("name").getAsString() : "Unknown";
                JsonObject stats = root.has("stats") ? root.getAsJsonObject("stats") : new JsonObject();

                coins.put(name, (double) getStat(stats, "COINS"));
                powder.put(name, (double) getStat(stats, "POWDER"));
                ranking.put(name, (double) getStat(stats, "RANK_PR"));
                wins.put(name, (double) getStat(stats, "WINS"));
                played.put(name, (double) getStat(stats, "PLAYED"));
                kills.put(name, (double) getStat(stats, "KILLS"));

                double donationAmount = root.has("donations") && !root.get("donations").isJsonNull() ? root.get("donations").getAsDouble() : 0.0;
                if (donationAmount > 0) {
                    donations.put(name, donationAmount);
                }

            } catch (Exception ignored) {
            }
        }

        tops.put(TopCategory.COINS, sortByValueDescending(coins));
        tops.put(TopCategory.POWDER, sortByValueDescending(powder));
        tops.put(TopCategory.RANKING, sortByValueDescending(ranking));
        tops.put(TopCategory.WINS, sortByValueDescending(wins));
        tops.put(TopCategory.PLAYED, sortByValueDescending(played));
        tops.put(TopCategory.KILLS, sortByValueDescending(kills));
        tops.put(TopCategory.DONATIONS, sortByValueDescending(donations));
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
        Map<String, Double> map = tops.get(category);

        if (map == null || map.isEmpty()) return;

        newLines.add("§l" + category.getTitle());
        int index = 1;

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            if (index > 10) break;

            String nickColor = switch (index) {
                case 1 -> "§6";
                case 2 -> "§f";
                case 3 -> "§c";
                default -> "§7";
            };

            String valueDisplay;
            if (category == TopCategory.DONATIONS) {
                double val = entry.getValue();
                valueDisplay = "§a" + (val == Math.floor(val) ? String.format(Locale.US, "%.0f", val) : String.format(Locale.US, "%.2f", val)) + " zł";
            } else if (category == TopCategory.RANKING) {
                RankType rank = RankType.getByPoints(entry.getValue().intValue());
                valueDisplay = "§f" + rank.getDisplayName();
            } else {
                valueDisplay = "§e" + formatNumber(entry.getValue().intValue());
            }

            newLines.add("§8" + index + ". " + nickColor + entry.getKey() + " §7- " + valueDisplay);
            index++;
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

            int incomingTick = 0;
            int totalIncomingTicks = 45;

            final Set<OldAnim> oldAnims = new HashSet<>();
            final Set<NewAnim> newAnims = new HashSet<>();

            @Override
            public void run() {
                if (oldIndex < oldDisplays.size()) {
                    oldAnims.add(new OldAnim(oldDisplays.get(oldIndex)));
                    oldIndex++;
                } else if (delayTicks > 0) {
                    delayTicks--;
                } else {
                    if (newIndex < newLines.size()) {
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
                        newAnims.add(new NewAnim(td, start.getY(), targetY, incomingTick, totalIncomingTicks));
                        newIndex++;
                    }
                    incomingTick++;
                }

                Iterator<OldAnim> oldIt = oldAnims.iterator();
                while (oldIt.hasNext()) {
                    OldAnim anim = oldIt.next();
                    Location loc = anim.display.getLocation();
                    anim.velocity += 0.015;
                    loc.add(0, anim.velocity, 0);

                    anim.display.setTeleportDuration(2);
                    anim.display.teleport(loc);

                    if (loc.getY() >= thresholdY) {
                        anim.display.remove();
                        oldIt.remove();
                    }
                }

                Iterator<NewAnim> newIt = newAnims.iterator();
                while (newIt.hasNext()) {
                    NewAnim anim = newIt.next();
                    int ticksElapsed = incomingTick - anim.startTick;
                    int ticksTotal = anim.endTick - anim.startTick;

                    if (ticksElapsed >= ticksTotal) {
                        Location loc = anim.display.getLocation();
                        loc.setY(anim.targetY);
                        anim.display.setTeleportDuration(1);
                        anim.display.teleport(loc);
                        newIt.remove();
                    } else {
                        double p = (double) ticksElapsed / ticksTotal;
                        double pEased = 1.0 - Math.pow(1.0 - p, 3.0);
                        double currentY = anim.spawnY + (anim.targetY - anim.spawnY) * pEased;

                        Location loc = anim.display.getLocation();
                        loc.setY(currentY);
                        anim.display.setTeleportDuration(2);
                        anim.display.teleport(loc);
                    }
                }

                if (oldIndex >= oldDisplays.size() && delayTicks <= 0 && newIndex >= newLines.size() && oldAnims.isEmpty() && newAnims.isEmpty()) {
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

    private static class OldAnim {
        TextDisplay display;
        double velocity = 0;

        OldAnim(TextDisplay display) {
            this.display = display;
        }
    }

    private static class NewAnim {
        TextDisplay display;
        double spawnY;
        double targetY;
        int startTick;
        int endTick;

        NewAnim(TextDisplay display, double spawnY, double targetY, int startTick, int endTick) {
            this.display = display;
            this.spawnY = spawnY;
            this.targetY = targetY;
            this.startTick = startTick;
            this.endTick = endTick;
        }
    }
}