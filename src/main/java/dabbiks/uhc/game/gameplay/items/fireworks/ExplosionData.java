package dabbiks.uhc.game.gameplay.items.fireworks;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import java.util.ArrayList;
import java.util.List;

public class ExplosionData {

    private ExplosionType type;
    private List<String> colorsHex;
    private List<String> fadeColorsHex;
    private boolean trail;
    private boolean flicker;

    public ExplosionData(ExplosionType type, List<String> colorsHex, List<String> fadeColorsHex, boolean trail, boolean flicker) {
        this.type = type;
        this.colorsHex = colorsHex;
        this.fadeColorsHex = fadeColorsHex;
        this.trail = trail;
        this.flicker = flicker;
    }

    public List<Color> getBukkitColors(List<String> hexList) {
        List<Color> colors = new ArrayList<>();
        if (hexList == null) return colors;
        for (String hex : hexList) {
            try {
                java.awt.Color jColor = java.awt.Color.decode(hex);
                colors.add(Color.fromRGB(jColor.getRed(), jColor.getGreen(), jColor.getBlue()));
            } catch (Exception ignored) {}
        }
        return colors;
    }

    public ExplosionType getType() { return type; }
    public List<String> getColorsHex() { return colorsHex; }
    public List<String> getFadeColorsHex() { return fadeColorsHex; }
    public boolean isTrail() { return trail; }
    public boolean isFlicker() { return flicker; }
}