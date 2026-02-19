package dabbiks.uhc.cosmetics;

public enum CosmeticTier {
    COMMON("§7§lP "),
    RARE("§b§lR "),
    EPIC("§a§lE "),
    MYTHIC("§d§lM "),
    LEGENDARY("§5§lL ");

    private final String icon;

    CosmeticTier(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
