package dabbiks.uhc.game.gameplay.items.data.attributes;

import java.text.DecimalFormat;

public class AttributeManager {

    public static void combineValue(AttributeData data1, AttributeData data2) {
        if (data1.getAttributeType() != data2.getAttributeType()) return;

        double value1 = data1.getAttributeValue();
        double value2 = data2.getAttributeValue();

        switch (data1.getAttributeType().getOperation()) {
            case ROUND -> data1.setAttributeValue((value1 + value2) / 2);
            case COMPARE -> data1.setAttributeValue(Math.max(value1, value2));
            case DECREASE, INCREASE -> {
                double newValue = ((value1 + value2) / 2) + data1.getAttributeType().getMultiplier();
                data1.setAttributeValue(newValue);
            }
        }
    }

    private static final DecimalFormat FORMATTER = new DecimalFormat("###.##");
    private static final DecimalFormat ATTACK_SPEED_FORMATTER = new DecimalFormat("###.#");

    public static String formatLoreLine(AttributeData attributeData) {
        double val = attributeData.getAttributeValue();
        AttributeType type = attributeData.getAttributeType();
        String color = "§f";

        if (type == AttributeType.ATTACK_SPEED) {
            val = 4.0 * Math.pow(0.632, -val / 2.0);
        }

        String typeName = type.toString().toUpperCase();
        boolean isNegativeBenefit = typeName.startsWith("SIZE") ||
                typeName.startsWith("BURNING_TIME") ||
                typeName.startsWith("FALL_DAMAGE");

        if (isNegativeBenefit) {
            if (val > 0) {
                color = "§c";
            }
        } else {
            if (val < 0) {
                color = "§c";
            }
        }

        String percent = type.isPercentage() ? "%" : "";
        String value = (type == AttributeType.ATTACK_SPEED) ? ATTACK_SPEED_FORMATTER.format(val) : FORMATTER.format(val);

        return "§r§f" + type.getSymbol() + " " + color + value + percent + " §7" + type.getName();
    }
}
