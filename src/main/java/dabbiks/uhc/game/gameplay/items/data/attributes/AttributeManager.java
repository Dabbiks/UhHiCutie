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

    public static String formatLoreLine(AttributeData attributeData) {
        String percent = attributeData.getAttributeType().isPercentage() ? "%" : "";
        String value = FORMATTER.format(attributeData.getAttributeValue());

        return "§r§f" + attributeData.getAttributeType().getSymbol() + "§f " + value + percent + " §7" + attributeData.getAttributeType().getName();
    }

}
