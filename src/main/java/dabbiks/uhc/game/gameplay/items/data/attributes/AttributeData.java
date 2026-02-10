package dabbiks.uhc.game.gameplay.items.data.attributes;

import org.bukkit.inventory.EquipmentSlot;

public class AttributeData {

    private EquipmentSlot equipmentSlot;
    private AttributeType attributeType;
    private double attributeValue;
    private boolean percent;

    public AttributeData(EquipmentSlot equipmentSlot, AttributeType attributeType, double attributeValue, boolean percent) {
        this.equipmentSlot = equipmentSlot;
        this.attributeType = attributeType;
        this.attributeValue = attributeValue;
        this.percent = percent;
    }

    public EquipmentSlot getEquipmentSlot() {
        return equipmentSlot;
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
