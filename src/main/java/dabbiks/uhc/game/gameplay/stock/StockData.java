package dabbiks.uhc.game.gameplay.stock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.plugin;
import static dabbiks.uhc.Main.symbolU;

public class StockData {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Double.class, new TypeAdapter<Double>() {
                @Override
                public void write(JsonWriter out, Double value) throws IOException {
                    if (value == null) out.nullValue();
                    else out.value(Math.round(value * 10.0) / 10.0);
                }
                @Override
                public Double read(JsonReader in) throws IOException {
                    return in.nextDouble();
                }
            })
            .create();

    private static final String FILE_NAME = "stock_data.json";
    private static final int MAX_HISTORY_SIZE = 20;
    private static final double INITIAL_PRICE = 1000.0;

    private final File file;
    private double currentPrice = INITIAL_PRICE;
    private List<Integer> gamesHistory = new ArrayList<>();
    private List<Double> priceHistory = new ArrayList<>(List.of(INITIAL_PRICE));

    private final List<Location> chartLocations = new ArrayList<>();
    private final List<TextDisplay> activeDisplays = new ArrayList<>();
    private final List<Boolean> priceDirections = new ArrayList<>();

    private final Transformation transformation = new Transformation(
            new Vector3f(), new Quaternionf(), new Vector3f(0.6f, 0.6f, 0.6f), new Quaternionf()
    );

    private static class StockDTO {
        double currentPrice;
        List<Integer> gamesHistory;
        List<Double> priceHistory;
    }

    public StockData(File dataFolder) {
        this.file = new File(dataFolder, FILE_NAME);
        load();
        Bukkit.getScheduler().runTaskLater(plugin, this::buildChart, 100L);
    }

    private void load() {
        if (!file.exists()) {
            save();
            return;
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            StockDTO dto = GSON.fromJson(reader, StockDTO.class);
            if (dto != null) {
                this.currentPrice = dto.currentPrice;
                if (dto.gamesHistory != null) this.gamesHistory = dto.gamesHistory;
                if (dto.priceHistory != null) this.priceHistory = dto.priceHistory;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        StockDTO dto = new StockDTO();
        dto.currentPrice = this.currentPrice;
        dto.gamesHistory = this.gamesHistory;
        dto.priceHistory = this.priceHistory;

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(dto, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateStockPrice() {
        int currentPlayers = Bukkit.getOnlinePlayers().size();

        if (gamesHistory.isEmpty()) {
            gamesHistory.add(currentPlayers);
            save();
            return;
        }

        double averagePlayers = gamesHistory.stream().mapToInt(Integer::intValue).average().orElse(0);
        double difference = currentPlayers - averagePlayers;

        currentPrice = Math.max(0, currentPrice + (currentPrice * (difference / 100.0)));

        gamesHistory.add(currentPlayers);
        priceHistory.add(currentPrice);

        if (gamesHistory.size() > MAX_HISTORY_SIZE) gamesHistory.remove(0);
        if (priceHistory.size() > MAX_HISTORY_SIZE) priceHistory.remove(0);

        save();
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double price) {
        this.currentPrice = Math.max(0, price);
        priceHistory.add(this.currentPrice);
        if (priceHistory.size() > MAX_HISTORY_SIZE) priceHistory.remove(0);
        save();
    }

    public void buildChart() {
        clearDisplays();
        chartLocations.clear();
        priceDirections.clear();

        Location baseLocation = new Location(Bukkit.getWorld("world"), 15, 105, -1.98);
        double offset = 0;

        chartLocations.add(baseLocation);

        for (int i = priceHistory.size() - 1; i >= 0; i--) {
            double val = priceHistory.get(i);
            double diff = (val - currentPrice) / 200.0;

            offset -= 0.115;

            if (Math.abs(diff) > 2) continue;

            chartLocations.add(baseLocation.clone().add(offset, diff, 0));

            if (i > 0) {
                priceDirections.add(priceHistory.get(i) > priceHistory.get(i - 1));
            }
        }

        spawnChartGrid();
        spawnChartLines();
    }

    private void spawnChartGrid() {
        Location startLoc = new Location(Bukkit.getWorld("world"), 15.5, 105, -1.99);
        double[] priceOffsets = {0, 100, 200, 300, -100, -200, -300};
        double[] heightOffsets = {0, 0.5, 1.0, 1.5, -0.5, -1.0, -1.5};

        for (int i = 0; i < priceOffsets.length; i++) {
            Location rowLoc = startLoc.clone().add(0, heightOffsets[i], 0);
            spawnTextDisplay(rowLoc, String.format("§0%.1f§f " + symbolU.SCOREBOARD_COIN, currentPrice + priceOffsets[i]));

            Location line1 = rowLoc.clone().add(-3.95, 0, -0.001);
            spawnTextDisplay(line1, "§7--------------------§0");

            Location line2 = line1.clone().add(1.4, 0, 0);
            spawnTextDisplay(line2, "§7---------------------------------§0");

            Location line3 = line2.clone().add(2.0, 0, 0);
            spawnTextDisplay(line3, "§7---------------------------------§0");
        }

        Location titleLoc = new Location(Bukkit.getWorld("world"), 14.5, 104, 7.99);
        TextDisplay title = titleLoc.getWorld().spawn(titleLoc, TextDisplay.class);
        title.setText("§eInwestomat " + symbolU.SCOREBOARD_COIN);
        title.setRotation(180, 0);
        title.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        title.setGlowing(false);
        title.setBillboard(Display.Billboard.FIXED);
        title.setShadowed(true);
        activeDisplays.add(title);
    }

    private void spawnChartLines() {
        if (chartLocations.size() < 2) return;

        Location current = chartLocations.get(0);
        for (int i = 1; i < chartLocations.size() && (i - 1) < priceDirections.size(); i++) {
            Location next = chartLocations.get(i);
            boolean isUp = priceDirections.get(i - 1);

            Location spawnLoc = isUp ? next.clone().add(0.005, 0, 0) : next;
            spawnTextDisplay(spawnLoc, isUp ? "\uE0A2" : "\uE0A3");

            current = next;
        }
    }

    private void spawnTextDisplay(Location loc, String text) {
        TextDisplay display = loc.getWorld().spawn(loc, TextDisplay.class);
        display.setText(text);
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        display.setGlowing(true);
        display.setBillboard(Display.Billboard.FIXED);
        display.setRotation(0, 0);
        display.setTransformation(transformation);
        activeDisplays.add(display);
    }

    public void clearDisplays() {
        activeDisplays.forEach(Display::remove);
        activeDisplays.clear();
    }
}