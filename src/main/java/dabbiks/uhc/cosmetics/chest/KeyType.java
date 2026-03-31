package dabbiks.uhc.cosmetics.chest;

import dabbiks.uhc.cosmetics.CosmeticTier;

public enum KeyType {
    COMMON(CosmeticTier.COMMON.getIcon() + "Klucz", 1, 250, 0),
    RARE(CosmeticTier.RARE.getIcon() + "Klucz", 1, 400, 1),
    EPIC(CosmeticTier.EPIC.getIcon() + "Klucz", 1, 675, 2),
    MYTHIC(CosmeticTier.MYTHIC.getIcon() + "Klucz", 1, 975, 3),
    LEGENDARY(CosmeticTier.LEGENDARY.getIcon() + "Klucz", 1, 1225, 4),
    EASTER("§e§lE §r§fKlucz", 1, 450, 5);

    private final String name;
    private final int model;
    private final int price;
    private final int index;

    KeyType(String name, int model, int price, int index) {
        this.name = name;
        this.model = model;
        this.price = price;
        this.index = index;
    }

    public String getName() {
        return name;
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
