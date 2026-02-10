package dabbiks.uhc.game.gameplay.items.data.perks;

import java.util.List;

public enum PerkType {
    GAS_LEAKER(List.of("nazwa: abc", ""));

    private List<String> lore;

    PerkType(List<String> lore) {
        this.lore = lore;
    }

    public List<String> getLore() {
        return lore;
    }
}
