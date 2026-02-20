package dabbiks.uhc.cosmetics.particletrail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dabbiks.uhc.cosmetics.CosmeticTier;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.plugin;

public class TrailDataLoader {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File trailsFolder;

    public TrailDataLoader() {
        this.trailsFolder = new File(plugin.getDataFolder(), "trails");
        if (!trailsFolder.exists()) {
            trailsFolder.mkdirs();
        }
    }

    public List<TrailData> loadAllTrails() {
        List<TrailData> trails = new ArrayList<>();
        File[] files = trailsFolder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null) return trails;

        for (File file : files) {
            try {
                trails.add(loadFromFile(file));
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load trail file: " + file.getName() + " - " + e.getMessage());
            }
        }

        trails.add(new TrailData("default", "Podstawowa smuga", Material.SNOWBALL, CosmeticTier.COMMON,
                0, 0, null, null, 0));

        return trails;
    }

    public TrailData loadFromFile(File file) throws IOException {
        return loadFromJson(Files.readString(file.toPath()));
    }

    public TrailData loadFromJson(String json) {
        JsonObject root = gson.fromJson(json, JsonObject.class);

        String id = root.get("id").getAsString();
        String name = root.get("name").getAsString();
        Material material = Material.valueOf(root.get("material").getAsString().toUpperCase());
        CosmeticTier tier = CosmeticTier.valueOf(root.get("tier").getAsString().toUpperCase());
        int coinsCost = root.get("coinsCost").getAsInt();
        int powderCost = root.get("powderCost").getAsInt();

        List<Particle> particles = new ArrayList<>();
        JsonArray particlesArray = root.getAsJsonArray("particles");

        for (int i = 0; i < particlesArray.size(); i++) {
            String particleName = particlesArray.get(i).getAsString().toUpperCase();
            try {
                Particle particle = Particle.valueOf(particleName);
                particles.add(particle);

                if (particle.getDataType() != Particle.DustOptions.class) {
                    plugin.getLogger().warning("Particle " + particleName + " in trail '" + id + "' does not support custom color/size.");
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().severe("Invalid particle type '" + particleName + "' in trail '" + id + "'.");
            }
        }

        JsonObject colorObj = root.getAsJsonObject("color");
        Color color = Color.fromRGB(
                colorObj.get("r").getAsInt(),
                colorObj.get("g").getAsInt(),
                colorObj.get("b").getAsInt()
        );

        float size = root.get("size").getAsFloat();

        return new TrailData(id, name, material, tier, coinsCost, powderCost, particles, color, size);
    }
}