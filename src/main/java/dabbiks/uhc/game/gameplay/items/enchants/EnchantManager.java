package dabbiks.uhc.game.gameplay.items.enchants;

import static dabbiks.uhc.Main.symbolU;

public class EnchantManager {

    public void combineLevel(EnchantData data1, EnchantData data2) {
        if (data1.getType() != data2.getType()) return;
        if (data1.getLevel() > data2.getLevel()) return;
        if (data2.getLevel() > data1.getLevel()) data1.setLevel(data2.getLevel());
        if (data1.getLevel() == data2.getLevel() && data1.getLevel() < data1.getType().getMaxLevel()) data1.setLevel(data1.getLevel()+1);
        data1.setLevel(data1.getLevel());
    }

    private static String numberToRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> "?";
        };
    }

    private static String tierToIcon(EnchantTier tier) {
        return switch (tier) {
            case COMMON -> symbolU.enchant_common;
            case RARE -> symbolU.enchant_rare;
            case EPIC -> symbolU.enchant_epic;
            case MYTHIC -> symbolU.enchant_mythic;
            case LEGENDARY -> symbolU.enchant_legendary;
        };
    }

    public static String formatLoreLine(EnchantData data) {
        return tierToIcon(data.getType().getTier()) + " " + data.getType().getName() + " " + numberToRoman(data.getLevel());
    }

}
