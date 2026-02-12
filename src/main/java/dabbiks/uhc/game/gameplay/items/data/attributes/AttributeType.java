package dabbiks.uhc.game.gameplay.items.data.attributes;

import org.bukkit.attribute.Attribute;

import static dabbiks.uhc.Main.symbolU;

public enum AttributeType {
    SIZE(                symbolU.SIZE,                 "Rozmiar",               AttributeOperation.INCREASE, 0.3,  Attribute.SCALE),
    ARMOR(               symbolU.ARMOR,                "Pancerz",               AttributeOperation.INCREASE, 0.2,  Attribute.ARMOR),
    LIFE_STEAL(          symbolU.LIFE_STEAL,           "Wampiryzm",             AttributeOperation.INCREASE, 0.05, null),
    REGENERATION(        symbolU.REGENERATION,         "Regeneracja",           AttributeOperation.INCREASE, 0.5,  null),
    ARROW_DAMAGE(        symbolU.ARROW_DAMAGE,         "Obrażenia",             AttributeOperation.INCREASE, 0.2,  null),
    GRAVITY(             symbolU.GRAVITY,              "Grawitacja",            AttributeOperation.DECREASE, 0.5,  Attribute.GRAVITY),
    SNEAK_SPEED(         symbolU.SNEAK_SPEED,          "Skradanie",             AttributeOperation.INCREASE, 0.1,  Attribute.SNEAKING_SPEED),
    HEALTH_BOOST(        symbolU.HEALTH_BOOST,         "Zdrowie",               AttributeOperation.INCREASE, 2.0,  Attribute.MAX_HEALTH),
    ATTACK_DAMAGE(       symbolU.ATTACK_DAMAGE,        "Obrażenia",             AttributeOperation.INCREASE, 0.15, Attribute.ATTACK_DAMAGE),
    ARMOR_PENETRATION(   symbolU.ARMOR_PENETRATION,    "Przebicie pancerza",    AttributeOperation.INCREASE, 0.1,  null),
    CRIT_DAMAGE(         symbolU.CRITICAL_DAMAGE,      "Obrażenia krytyczne",   AttributeOperation.INCREASE, 0.25, null),
    BURNING_TIME(        symbolU.BURNING_TIME,         "Czas płonięcia",        AttributeOperation.COMPARE,  1.0,  Attribute.BURNING_TIME),
    ATTACK_SPEED(        symbolU.ATTACK_SPEED,         "Prędkość ataku",        AttributeOperation.COMPARE,  1.0,  Attribute.ATTACK_SPEED),
    JUMP_STRENGTH(       symbolU.JUMP_STRENGTH,        "Siła skoku",            AttributeOperation.COMPARE,  1.0,  Attribute.JUMP_STRENGTH),
    ELECTRIC_DAMAGE(     symbolU.ELECTRIC_DAMAGE,      "Obrażenia elektryczne", AttributeOperation.INCREASE, 0.3,  null),
    MOVEMENT_SPEED(      symbolU.MOVEMENT_SPEED,       "Prędkość ruchu",        AttributeOperation.INCREASE, 0.05, Attribute.MOVEMENT_SPEED),
    KNOCKBACK_POWER(     symbolU.KNOCKBACK_POWER,      "Siła odrzutu",          AttributeOperation.INCREASE, 0.2,  Attribute.ATTACK_KNOCKBACK),
    FALL_DAMAGE(         symbolU.FALL_DAMAGE,          "Obrażenia upadku",      AttributeOperation.INCREASE, -0.2, Attribute.FALL_DAMAGE_MULTIPLIER),
    ATTACK_RANGE(        symbolU.ATTACK_RANGE,         "Zasięg ataku",          AttributeOperation.COMPARE,  1.0,  Attribute.ENTITY_INTERACTION_RANGE),
    KNOCKBACK_RESISTANCE(symbolU.KNOCKBACK_RESISTANCE,  "Odporność na odrzut",   AttributeOperation.INCREASE, 0.1,  Attribute.KNOCKBACK_RESISTANCE);

    private final String symbol;
    private final String name;
    private final AttributeOperation operation;
    private final double multiplier;
    private final Attribute attribute;

    AttributeType(String symbol, String name, AttributeOperation operation, double multiplier, Attribute attribute) {
        this.symbol = symbol;
        this.name = name;
        this.operation = operation;
        this.multiplier = multiplier;
        this.attribute = attribute;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public AttributeOperation getOperation() {
        return operation;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public Attribute getAttribute() {
        return attribute;
    }

}