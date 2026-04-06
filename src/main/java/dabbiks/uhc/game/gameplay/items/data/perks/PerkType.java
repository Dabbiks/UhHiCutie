package dabbiks.uhc.game.gameplay.items.data.perks;

import java.util.List;

import static dabbiks.uhc.Main.symbolU;

public enum PerkType {
    OXIDIZING("Utlenianie", List.of("§7Każde §czabójstwo §7utlenia przedmiot", "§7zwiększając jego statystyki.")),
    RABBIT_JUMP("Króliczy wyskok", List.of("§7Przytrzymywanie " + symbolU.SHIFT + " §7ładuje buty.", "§7Po puszczeniu przycisku §ewyrzucają do góry.")),
    COAL_FUELED("Fabrykator CO₂", List.of("§7Przedmiot §espala węgiel§7, żeby", "§7wzmocnić swoje statystyki.")),
    CONTACT_EXPLOSION("Eksplozja przy kontakcie", List.of("§7Przedmiot wywoła wybuch, gdy po", "§7raz pierwszy napotka blok.")),
    ABSORPTION_ABSORBER("Absorber Absorpcji", List.of("§7Przy otrzymywaniu obrażeń wysysa absorpcję przeciwnika", "§7i regeneruje posiadacza o połowę jej wartości")),
    BURNING_ATTACK("Płonący atak", List.of("§7Po spożyciu ataki podpalają graczy przez 30 sekund")),
    UPGRADE_CRYSTAL("Wzmocnienie zaklęć", List.of("§7Po użyciu inkrementuje poziom wszystkich enchantów", "§7na całej zbroi używającego")),
    PLAYER_RADAR("Wykrywanie graczy", List.of("§7Po odpaleniu podaje dystans do najbliższego", "§7gracza w promieniu 200 kratek"));

    private final String name;
    private final List<String> lore;

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
