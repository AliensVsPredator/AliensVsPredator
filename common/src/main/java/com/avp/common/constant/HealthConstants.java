package com.avp.common.constant;

public record HealthConstants() {

    // Basic measurements (in terms of half-hearts).
    public static final float PLAYER_HEALTH = 20F;

    // Alien measurements (in terms of half-hearts).
    public static final float ADOLESCENT_HEALTH = PLAYER_HEALTH * 0.5F;

    public static final float BOILER_HEALTH = PLAYER_HEALTH * 2F;

    public static final float CHESTBURSTER_HEALTH = PLAYER_HEALTH * 0.25F;

    public static final float CRUSHER_HEALTH = PLAYER_HEALTH * 5F;

    public static final float DRONE_HEALTH = PLAYER_HEALTH * 2F;

    public static final float FACEHUGGER_HEALTH = PLAYER_HEALTH * 0.15F;

    public static final float OVOMORPH_HEALTH = PLAYER_HEALTH * 1.5F;

    public static final float PREDALIEN_ADOLESCENT_HEALTH = PLAYER_HEALTH * 0.5F;

    public static final float PREDALIEN_CHESTBURSTER_HEALTH = PLAYER_HEALTH * 0.25F;

    public static final float PREDALIEN_HEALTH = PLAYER_HEALTH * 10F;

    public static final float PRAETORIAN_HEALTH = PLAYER_HEALTH * 5F;

    public static final float PROWLER_HEALTH = PLAYER_HEALTH * 3F;

    public static final float QUEEN_HEALTH = PLAYER_HEALTH * 10F;

    public static final float RUNNER_HEALTH = PLAYER_HEALTH * 2F;

    public static final float SPITTER_HEALTH = PLAYER_HEALTH * 3F;

    public static final float WARRIOR_HEALTH = PLAYER_HEALTH * 3F;

    public static final float YAUTJA_HEALTH = PLAYER_HEALTH * 10F;
}
