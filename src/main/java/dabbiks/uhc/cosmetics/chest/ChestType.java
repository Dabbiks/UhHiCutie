package dabbiks.uhc.cosmetics.chest;

import dabbiks.uhc.cosmetics.CosmeticTier;

public enum ChestType {
    COMMON("§fSkrzynia §7§lPOSPOLITA", 2, 12, 450, 0),
    RARE("§fSkrzynia §b§lRZADKA", 2, 14, 675, 1),
    EPIC("§fSkrzynia §a§lEPICKA", 3, 16, 950, 2),
    MYTHIC("§fSkrzynia §d§lMITYCZNA", 4, 18, 1200, 3),
    LEGENDARY("§fSkrzynia §5§lLEGENDARNA", 5, 20, 1825, 4);

    private final String name;
    private final int chests;
    private final int model;
    private final int price;
    private final int index;

    ChestType(String name, int chests, int model, int price, int index) {
        this.name = name;
        this.chests = chests;
        this.model = model;
        this.price = price;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getChests() {
        return chests;
    }

    public int getModel() {
        return model;
    }

    public int getPrice() {
        return price;
    }

    public int getIndex() {
        return index;
    }
}
