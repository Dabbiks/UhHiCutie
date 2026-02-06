package dabbiks.uhc.player.rank;

public enum RankTypes {
    UNRANKED("Brak", "\uE031", 0, 0),
    BRONZE_I("Brąz I", "\uE032", 1, 99),
    BRONZE_II("Brąz II", "\uE032", 100, 199),
    BRONZE_III("Brąz III", "\uE032", 200, 299),
    COPPER_I("Miedź I", "\uE0A1", 300, 399),
    COPPER_II("Miedź II", "\uE0A1", 400, 499),
    COPPER_III("Miedź III", "\uE0A1", 500, 599),
    IRON_I("Żelazo I", "\uE042", 600, 699),
    IRON_II("Żelazo II", "\uE042", 700, 799),
    IRON_III("Żelazo III", "\uE042", 800, 899),
    GOLD_I("Złoto I", "\uE033", 900, 999),
    GOLD_II("Złoto II", "\uE033", 1000, 1099),
    GOLD_III("Złoto III", "\uE033", 1100, 1199),
    EMERALD_I("Szmaragd I", "\uE0A2", 1200, 1299),
    EMERALD_II("Szmaragd II", "\uE0A2", 1300, 1399),
    EMERALD_III("Szmaragd III", "\uE0A2", 1400, 1499),
    DIAMOND_I("Diament I", "\uE034", 1500, 1599),
    DIAMOND_II("Diament II", "\uE034",  1600, 1699),
    DIAMOND_III("Diament III", "\uE034", 1700, 1799),
    AMETHYST("Ametyst", "\uE035", 1800, 2199),
    NETHERITE("Netheryt", "\uE036", 2200, 10000);


    private final String name;
    private final String icon;
    private final int minThreshold;
    private final int maxThreshold;

    RankTypes(String name, String icon, int minThreshold, int maxThreshold) {
        this.name = name;
        this.icon = icon;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public int getMinThreshold() {
        return minThreshold;
    }

    public int getMaxThreshold() { return maxThreshold; }
}
