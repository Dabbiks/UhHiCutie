package dabbiks.uhc.cosmetics.chest;

import dabbiks.uhc.cosmetics.CosmeticTier;

public enum ChestType {
    COMMON("§fSkrzynia §7§lPOSPOLITA", 2, 1, 1, 0),
    RARE("§fSkrzynia §7§lRZADKA", 2, 1, 1, 1),
    EPIC("§fSkrzynia §a§lEPICKA", 3, 1, 1, 2),
    MYTHIC("§fSkrzynia §d§lMITYCZNA", 4, 1, 1, 3),
    LEGENDARY("§fSkrzynia §5§lLEGENDARNA", 5, 1, 1, 4);

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
