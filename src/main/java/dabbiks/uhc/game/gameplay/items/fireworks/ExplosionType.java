package dabbiks.uhc.game.gameplay.items.fireworks;

import org.bukkit.FireworkEffect.Type;

public enum ExplosionType {
    BALL(Type.BALL),
    LARGE_BALL(Type.BALL_LARGE),
    STAR(Type.STAR),
    BURST(Type.BURST),
    CREEPER(Type.CREEPER);

    private final Type bukkitType;

    ExplosionType(Type bukkitType) {
        this.bukkitType = bukkitType;
    }

    public Type getBukkitType() { return bukkitType; }
}