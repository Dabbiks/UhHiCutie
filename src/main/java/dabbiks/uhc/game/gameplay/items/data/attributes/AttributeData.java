package dabbiks.uhc.game.gameplay.items.data.attributes;

import org.bukkit.inventory.EquipmentSlot;

public class AttributeData {

    private AttributeType attributeType;
    private double attributeValue;
    private boolean percent;

    public AttributeData(AttributeType attributeType, double attributeValue, boolean percent) {
        this.attributeType = attributeType;
        this.attributeValue = attributeValue;
        this.percent = percent;
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public double getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(double attributeValue) {
        this.attributeValue = attributeValue;
    }

    public boolean isPercent() {
        return percent;
    }
}
