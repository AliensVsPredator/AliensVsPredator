package com.blib.common.constant;

public class PlayerStatConstants {

    // Basic measurements (in terms of half-hearts).
    public static final float BASE_HEALTH = 20F;

    // Basic measurements.
    private static final float SPEED_MULTIPLIER = 10 * 0.315000001192092896F;

    public static final float BASE_WALK_SPEED = 0.1F * SPEED_MULTIPLIER;

    public static final float BASE_SPRINT_SPEED = BASE_WALK_SPEED * 1.15F;

    public static final float BASE_SPRINT_JUMP_SPEED = BASE_SPRINT_SPEED * 1.15F;
}
