package dabbiks.uhc.game.gameplay.items.data.potions;

import org.bukkit.potion.PotionEffectType;

public enum PotionType {
    SPEED("Szybkość", PotionEffectType.SPEED),
    HASTE("Pośpiech", PotionEffectType.HASTE),
    STRENGTH("Siła", PotionEffectType.STRENGTH),
    INSTANT_HEALTH("Natychmiastowe zdrowie", PotionEffectType.INSTANT_HEALTH),
    JUMP_BOOST("Skoczność", PotionEffectType.JUMP_BOOST),
    REGENERATION("Regeneracja", PotionEffectType.REGENERATION),
    RESISTANCE("Odporność", PotionEffectType.RESISTANCE),
    FIRE_RESISTANCE("Odporność na ogień", PotionEffectType.FIRE_RESISTANCE),
    WATER_BREATHING("Oddychanie pod wodą", PotionEffectType.WATER_BREATHING),
    INVISIBILITY("Niewidzialność", PotionEffectType.INVISIBILITY),
    NIGHT_VISION("Widzenie w ciemności", PotionEffectType.NIGHT_VISION),
    HEALTH_BOOST("Dodatkowe zdrowie", PotionEffectType.HEALTH_BOOST),
    ABSORPTION("Absorpcja", PotionEffectType.ABSORPTION),
    SATURATION("Nasycenie", PotionEffectType.SATURATION),
    LUCK("Szczęście", PotionEffectType.LUCK),
    SLOW_FALLING("Powolne opadanie", PotionEffectType.SLOW_FALLING),
    CONDUIT_POWER("Moc przewodnika", PotionEffectType.CONDUIT_POWER),
    DOLPHINS_GRACE("Gracja delfina", PotionEffectType.DOLPHINS_GRACE),
    HERO_OF_THE_VILLAGE("Bohater wioski", PotionEffectType.HERO_OF_THE_VILLAGE),

    SLOWNESS("Spowolnienie", PotionEffectType.SLOWNESS),
    MINING_FATIGUE("Zmęczenie przy kopaniu", PotionEffectType.MINING_FATIGUE),
    INSTANT_DAMAGE("Natychmiastowe obrażenia", PotionEffectType.INSTANT_DAMAGE),
    NAUSEA("Mdłości", PotionEffectType.NAUSEA),
    BLINDNESS("Ślepota", PotionEffectType.BLINDNESS),
    HUNGER("Głód", PotionEffectType.HUNGER),
    WEAKNESS("Osłabienie", PotionEffectType.WEAKNESS),
    POISON("Trucizna", PotionEffectType.POISON),
    WITHER("Wither", PotionEffectType.WITHER),
    UNLUCK("Pech", PotionEffectType.UNLUCK),
    BAD_OMEN("Zły omen", PotionEffectType.BAD_OMEN),
    DARKNESS("Ciemność", PotionEffectType.DARKNESS),

    TRIAL_OMEN("Omen wyzwania", PotionEffectType.TRIAL_OMEN),
    RAID_OMEN("Omen najazdu", PotionEffectType.RAID_OMEN),
    WIND_CHARGED("Naładowanie wiatrem", PotionEffectType.WIND_CHARGED),
    WEAVING("Oplątanie", PotionEffectType.WEAVING),
    OOZING("Wyciekanie", PotionEffectType.OOZING),
    INFESTED("Zainfekowanie", PotionEffectType.INFESTED);

    private final String friendlyName;
    private final PotionEffectType bukkitType;

    PotionType(String friendlyName, PotionEffectType bukkitType) {
        this.friendlyName = friendlyName;
        this.bukkitType = bukkitType;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public PotionEffectType getBukkitType() {
        return bukkitType;
    }

    public static PotionType fromString(String name) {
        try {
            return PotionType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}