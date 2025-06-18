package com.avp.common.constant;

public record AttackDamageConstants() {

    // Basic measurements (in terms of half-hearts).
    public static final float PLAYER_HEALTH = 20;

    // Alien measurements (in terms of half-hearts).
    public static final float MARINE_ATTACK_DAMAGE = PLAYER_HEALTH * 0.1F;

    public static final float ADOLESCENT_ATTACK_DAMAGE = PLAYER_HEALTH * 0.1F;

    public static final float BOILER_ATTACK_DAMAGE = PLAYER_HEALTH * 0.25F;

    public static final float CHESTBURSTER_ATTACK_DAMAGE = PLAYER_HEALTH * 0.1F;

    public static final float CRUSHER_ATTACK_DAMAGE = PLAYER_HEALTH * 0.75F;

    public static final float DRONE_ATTACK_DAMAGE = PLAYER_HEALTH * 0.25F;

    public static final float PRAETORIAN_ATTACK_DAMAGE = PLAYER_HEALTH * 0.75F;

    public static final float PROWLER_ATTACK_DAMAGE = PLAYER_HEALTH * 0.5F;

    public static final float QUEEN_ATTACK_DAMAGE = PLAYER_HEALTH * 2.5F;

    public static final float RUNNER_ATTACK_DAMAGE = PLAYER_HEALTH * 0.25F;

    public static final float WARRIOR_ATTACK_DAMAGE = PLAYER_HEALTH * 0.5F;

    public static final float YAUTJA_ATTACK_DAMAGE = PLAYER_HEALTH * 0.75F;
}
