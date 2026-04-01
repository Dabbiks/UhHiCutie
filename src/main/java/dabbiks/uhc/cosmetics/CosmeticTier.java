package dabbiks.uhc.cosmetics;

public enum CosmeticTier {
    COMMON("§7§lP §f"),
    RARE("§b§lR §f"),
    EPIC("§a§lE §f"),
    MYTHIC("§d§lM §f"),
    LEGENDARY("§5§lL §f"),
    PRESTIGE("§6§l$ §f"),
    EASTER("§e§lW §f");

    private final String icon;

    CosmeticTier(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}