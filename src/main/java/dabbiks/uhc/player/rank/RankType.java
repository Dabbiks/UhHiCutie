package dabbiks.uhc.player.rank;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum RankType {
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
    DIAMOND_II("Diament II", "\uE034", 1600, 1699),
    DIAMOND_III("Diament III", "\uE034", 1700, 1799),
    AMETHYST("Ametyst", "\uE035", 1800, 2199),
    NETHERITE("Netheryt", "\uE036", 2200, Integer.MAX_VALUE);

    private final String name;
    private final String icon;
    private final int minThreshold;
    private final int maxThreshold;

    private static final List<RankType> ORDERED_VALUES = Arrays.stream(values())
            .sorted(Comparator.comparingInt(RankType::getMinThreshold))
            .toList();

    RankType(String name, String icon, int minThreshold, int maxThreshold) {
        this.name = name;
        this.icon = icon;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return icon + " " + name;
    }

    public String getIcon() {
        return icon;
    }

    public int getMinThreshold() {
        return minThreshold;
    }

    public int getMaxThreshold() {
        return maxThreshold;
    }

    public static RankType getByPoints(int points) {
        if (points >= NETHERITE.minThreshold) return NETHERITE;
        if (points <= 0) return UNRANKED;

        for (RankType rank : ORDERED_VALUES) {
            if (points >= rank.minThreshold && points <= rank.maxThreshold) {
                return rank;
            }
        }
        return UNRANKED;
    }

    public RankType next() {
        int index = ORDERED_VALUES.indexOf(this);
        if (index == -1 || index == ORDERED_VALUES.size() - 1) {
            return this;
        }
        return ORDERED_VALUES.get(index + 1);
    }

    public RankType previous() {
        int index = ORDERED_VALUES.indexOf(this);
        if (index <= 0) {
            return this;
        }
        return ORDERED_VALUES.get(index - 1);
    }

    public double getProgressPercentage(int currentPoints) {
        if (this == UNRANKED || this == NETHERITE) return 1.0;

        double range = maxThreshold - minThreshold;
        double earned = currentPoints - minThreshold;

        return Math.max(0.0, Math.min(1.0, earned / range));
    }

    public int getPointsToNextRank(int currentPoints) {
        if (this == NETHERITE) return 0;
        return (maxThreshold + 1) - currentPoints;
    }
}