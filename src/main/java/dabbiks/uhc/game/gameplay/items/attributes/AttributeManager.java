package dabbiks.uhc.game.gameplay.items.attributes;

public class AttributeManager {

    public void combineValue(AttributeData data1, AttributeData data2) {
        if (data1.getAttributeType() != data2.getAttributeType()) return;
        double value1 = data1.getAttributeValue();
        double value2 = data2.getAttributeValue();
        switch (data1.getAttributeType().getOperation()) {
            case ROUND -> data1.setAttributeValue((value1 + value2) / 2);
            case COMPARE -> data1.setAttributeValue(Math.max(value1, value2));
            case DECREASE, INCREASE -> data1.setAttributeValue((value1 + value2) / 2 * data1.getAttributeType().getMultiplier());
        }
    }

    public static String formatLoreLine(AttributeData attributeData) {
        String percent = attributeData.isPercent() ? "%" : "";
        return attributeData.getAttributeType().getSymbol() + " " + attributeData.getAttributeValue() + percent + " " + attributeData.getAttributeType().getName();
    }

}
