package dabbiks.uhc.game.gameplay.items.data.attributes;

import org.bukkit.inventory.EquipmentSlot;

public class AttributeData {

    private AttributeType attributeType;
    private double attributeValue;

    public AttributeData(AttributeType attributeType, double attributeValue) {
        this.attributeType = attributeType;
        this.attributeValue = attributeValue;
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
}
