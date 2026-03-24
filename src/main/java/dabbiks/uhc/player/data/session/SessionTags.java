package dabbiks.uhc.player.data.session;

public enum SessionTags {

    MINER("Górnik", "I"),
    SMALL_ANVIL_DISCOUNT("Górnik", "I"),
    BIG_ANVIL_DISCOUNT("Górnik", "I"),
    IMMORTAL_EXPERIENCE("Górnik", "I"),
    RECIPE_BLAST_FURNACE("Górnik", "III"),
    RECIPE_CHEAP_NETHERITE("Górnik", "III"),

    DEFAULT("Cywil", "I"),
    ABSORPTION_REDUCTION("Cywil", "I"),
    BIG_ABSORPTION_REDUCTION("Cywil", "I"),
    ADDITIONAL_REGENERATION("Cywil", "I"),
    RECIPE_TOTEM_OF_CONTROL("Cywil", "III"),

    ARCHER("Łucznik", "I"),
    PROJECTILE_HIT_REGENERATION("Łucznik", "I"),
    BIG_PROJECTILE_HIT_REGENERATION("Łucznik", "I"),
    PROJECTILE_HIT_ARMOR_CORROSION("Łucznik", "I"),
    RECIPE_CHAINMAIL_SET("Łucznik", "III"),

    PIKEMAN("Pikinier", "I"),
    ON_HIT_PROOF("Pikinier", "I"),
    STATUS_EFFECT_PROOF("Pikinier", "I"),
    RECIPE_KAENIC_ROOKERN("Pikinier", "III"),

    FISHERMAN("Wędkarz", "I"),
    SMALL_FISHING_ROD_KNOCKBACK("Wędkarz", "I"),
    BIG_FISHING_ROD_KNOCKBACK("Wędkarz", "I"),
    MORE_FISHING_DROPS("Wędkarz", "I"),
    RECIPE_FISH_OIL("Wędkarz", "III"),

    HUNTER("Łowca", "I"),
    BOOMERANG_LOOTING("Łowca", "I"),
    BOOMERANG_EXECUTE("Łowca", "I"),
    RECIPE_EXPLOSIVE_BOOMERANG("Łowca", "III"),

    SLUDGE("Szlam", "I");

    private final String name;
    private final String level;

    SessionTags(String name, String level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }
}