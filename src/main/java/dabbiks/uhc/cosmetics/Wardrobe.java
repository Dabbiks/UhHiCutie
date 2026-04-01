package dabbiks.uhc.cosmetics;

import org.bukkit.Color;

public enum Wardrobe {
    DEFAULT("Strój Cywila", "Czapka Cywila", "Napierśnik Cywila", "Spodnie Cywila", "Buty Cywila",
            CosmeticTier.COMMON, null,
            "#604020", "#604020", "#604020", "#604020"),

    MINER("Strój Górnika", "Kask Górnika", "Napierśnik Górnika", "Spodnie Górnika", "Buty Górnika",
            CosmeticTier.RARE, null,
            "#FFFF00", "#FFFF00", "#404040", "#000000"),

    BANKER("Strój Bankiera", "Czapka Bankiera", "Napierśnik Bankiera", "Spodnie Bankiera", "Buty Bankiera",
            CosmeticTier.EPIC, null,
            "#FFFFFF", "#FFD700", "#FFD700", "#FFD700"),

    GEKKO("Strój Kameleona", "Czapka Kameleona", "Napierśnik Kameleona", "Spodnie Kameleona", "Buty Kameleona",
            CosmeticTier.MYTHIC, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTliMTE0NGQwNGRjMTQ0MWMwNDY2ZWQ1MjUwMGJiYTJhZjEyYmFkYzU4YTRiNWZiMGFjNTE1N2Y5YjM2OTI5ZCJ9fX0=",
            "#00FF00", "#4DFF4D", "#99FF99", "#CCFFCC"),

    SPIDER("Strój Pajączka", "Czapka Pajączka", "Napierśnik Pajączka", "Spodnie Pajączka", "Buty Pajączka",
            CosmeticTier.LEGENDARY, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNlMjk2M2M2OWVmZjcwODg3NDdlNGJjMThkZmZiZGRmZTJjY2UxZmNhNjgwMDE1NDMxMmI2NWI2MWExNmIzZCJ9fX0=",
            "#000000", "#000000", "#000000", "#000000"),
    EVIL_EGG_STEALER("Strój Bezczelnego Złodzieja Jaj", "Czapka Bezczelnego Złodzieja Jaj", "Bluzka Bezczelnego Złodzieja Jaj", "Spodnie Bezczelnego Złodzieja Jaj", "Buty Bezczelnego Złodzieja Jaj",
            CosmeticTier.EASTER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODgxN2RkZmM1M2Y5MDY1ZWNlNTBmMmUzYTQ5NDlmZjEzYWM0ZGM1MWFkNjA2ZDU2NjEyNGIwYjY1MDZhYzNlYSJ9fX0=",
            "#FFB6C1", "#FFB6C1", "#FFFACD", "#ADD8E6"),
    EASTER_BUNNY("Strój Wielkanocnego Króliczka", "Maska Wielkanocnego Króliczka", "Napierśnik Wielkanocnego Króliczka", "Spodnie Wielkanocnego Króliczka", "Kapcie Wielkanocnego Króliczka",
            CosmeticTier.EASTER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2VjMjQyZTY2N2FlZTQ0NDkyNDEzZWY0NjFiODEwY2FjMzU2Yjc0ZDg3MThlNWNlYzFmODkyYTZiNDNlNWUxIn19fQ==",
            "#FFCCFF", "#FFCCFF","#FFCCFF", "#FFFF80");
    private final String setName;
    private final String helmetName;
    private final String chestplateName;
    private final String leggingsName;
    private final String bootsName;
    private final CosmeticTier tier;
    private final String headTexture;
    private final Color helmetColor;
    private final Color chestplateColor;
    private final Color leggingsColor;
    private final Color bootsColor;

    Wardrobe(String setName, String helmetName, String chestplateName, String leggingsName, String bootsName, CosmeticTier tier, String headTexture, String helmetHex, String chestplateHex, String leggingsHex, String bootsHex) {
        this.setName = setName;
        this.helmetName = helmetName;
        this.chestplateName = chestplateName;
        this.leggingsName = leggingsName;
        this.bootsName = bootsName;
        this.tier = tier;
        this.headTexture = headTexture;
        this.helmetColor = parseColor(helmetHex);
        this.chestplateColor = parseColor(chestplateHex);
        this.leggingsColor = parseColor(leggingsHex);
        this.bootsColor = parseColor(bootsHex);
    }

    private Color parseColor(String hex) {
        if (hex == null || hex.isEmpty()) return null;
        return Color.fromRGB(Integer.parseInt(hex.replace("#", ""), 16));
    }

    public String getSetName() { return setName; }
    public String getHelmetName() { return helmetName; }
    public String getChestplateName() { return chestplateName; }
    public String getLeggingsName() { return leggingsName; }
    public String getBootsName() { return bootsName; }
    public CosmeticTier getTier() { return tier; }
    public String getHeadTexture() { return headTexture; }
    public Color getHelmetColor() { return helmetColor; }
    public Color getChestplateColor() { return chestplateColor; }
    public Color getLeggingsColor() { return leggingsColor; }
    public Color getBootsColor() { return bootsColor; }
}