package dabbiks.uhc.cosmetics.particletrail;

import dabbiks.uhc.cosmetics.CosmeticTier;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import java.util.List;

public class TrailData {
    private final String id;
    private final String name;
    private final Material material;
    private final CosmeticTier tier;
    private final int coinsCost;
    private final int powderCost;
    private final List<Particle> particles;
    private final Color color;
    private final float size;

    public TrailData(String id, String name, Material material, CosmeticTier tier, int coinsCost, int powderCost, List<Particle> particles, Color color, float size) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.tier = tier;
        this.coinsCost = coinsCost;
        this.powderCost = powderCost;
        this.particles = particles;
        this.color = color;
        this.size = size;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Material getMaterial() { return material; }
    public CosmeticTier getTier() { return tier; }
    public int getCoinsCost() { return coinsCost; }
    public int getPowderCost() { return powderCost; }
    public List<Particle> getParticles() { return particles; }
    public Color getColor() { return color; }
    public float getSize() { return size; }
}