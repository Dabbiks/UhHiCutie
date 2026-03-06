package dabbiks.uhc.cosmetics.chest.rewards;

public enum RewardType {
    CURRENCY("WALUTA"),
    KILL_SOUND("DŹWIĘK"),
    MULTIPLIER("MNOŻNIK"),
    TRIAL("SMUGA"),
    PVP_SWORD("MIECZ PVP"),
    CAGE("KLATKA"),
    KEY("KLUCZ"),
    FRAGMENT("ODŁAMEK"),
    CHEST("SKRZYNIA"),
    CHAMPION("KLASA"),
    SPECIAL_ITEM("KOLEKCJA"),
    UPGRADE("ULEPSZENIE");

    private String name;

    RewardType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}