package dabbiks.uhc.menu;

public enum DiscountType {
    CAGE("Klatki"),
    KILL_SOUND("Dźwięki zabójstw"),
    PVP_SWORD("Miecze PvP"),
    CHAMPION("Klasy"),
    CHAMPION_UPGRADE("Ulepszenia klas"),
    MOUNT("Mounty"),
    CHEST("Skrzynie"),
    KEY("Klucze");

    private final String name;

    DiscountType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
