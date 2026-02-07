package dabbiks.uhc.game.gameplay.items.potions;

import org.bukkit.Color;
import org.bukkit.potion.PotionType;

public class PotionData {

    private PotionType type;
    private int duration;
    private int amplifier;
    private String colorHex;

    public PotionData(PotionType type, int duration, int amplifier, String colorHex) {
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
        this.colorHex = colorHex;
    }

    public PotionType getType() { return type; }
    public int getDuration() { return duration; }
    public int getAmplifier() { return amplifier; }
    public String getColorHex() { return colorHex; }

    public Color getBukkitColor() {
        if (colorHex == null || !colorHex.startsWith("#")) return null;
        try {
            java.awt.Color jColor = java.awt.Color.decode(colorHex);
            return Color.fromRGB(jColor.getRed(), jColor.getGreen(), jColor.getBlue());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}