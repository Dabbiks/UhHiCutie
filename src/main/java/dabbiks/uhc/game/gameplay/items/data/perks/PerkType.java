package dabbiks.uhc.game.gameplay.items.data.perks;

import java.util.List;

import static dabbiks.uhc.Main.symbolU;

public enum PerkType {
    OXIDIZING("Utlenianie", List.of("§7Każde §czabójstwo §7utlenia przedmiot", "§7zwiększając jego statystyki.")),
    RABBIT_JUMP("Króliczy wyskok", List.of("§7Przytrzymywanie " + symbolU.SHIFT + " §7ładuje buty.", "§7Po puszczeniu przycisku §ewyrzucają do góry.")),
    COAL_FUELED("Fabrykator CO₂", List.of("§7Przedmiot §espala węgiel§7, żeby", "§7wzmocnić swoje statystyki."));

    private String name;
    private List<String> lore;

    PerkType(String name, List<String> lore) {
        this.name = name;
        this.lore = lore;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getName() {
        return name;
    }
}
