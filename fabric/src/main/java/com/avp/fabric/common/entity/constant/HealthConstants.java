package com.avp.fabric.common.entity.constant;

public record HealthConstants() {

    // Basic measurements (in terms of half-hearts).
    public static final float PLAYER_HEALTH = 20F;

    // Alien measurements (in terms of half-hearts).
    public static final float CHESTBURSTER_HEALTH = PLAYER_HEALTH * 0.25F;

    public static final float DRONE_HEALTH = PLAYER_HEALTH * 2F;

    public static final float FACEHUGGER_HEALTH = PLAYER_HEALTH * 0.15F;

    public static final float OVAMORPH_HEALTH = PLAYER_HEALTH * 1.5F;

    public static final float PRAETORIAN_HEALTH = PLAYER_HEALTH * 5F;

    public static final float QUEEN_HEALTH = PLAYER_HEALTH * 10F;

    public static final float WARRIOR_HEALTH = PLAYER_HEALTH * 3F;

    public static final float YAUTJA_HEALTH = PLAYER_HEALTH * 10F;
}
