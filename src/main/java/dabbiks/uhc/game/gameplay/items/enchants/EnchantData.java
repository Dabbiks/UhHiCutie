package dabbiks.uhc.game.gameplay.items.enchants;

public class EnchantData {

    private EnchantType type;
    private int level;

    public EnchantData(EnchantType type, int level) {
        this.type = type;
        this.level = level;
    }

    public EnchantType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
