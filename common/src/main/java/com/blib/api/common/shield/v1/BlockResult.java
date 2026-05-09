package com.blib.api.common.shield.v1;

/**
 * Per-hit decision returned from {@link BLibShieldItem#onBlocked}. Encodes how much of the incoming damage to absorb
 * and whether the hit consumed the shield's ability to block (triggering an item cooldown).
 *
 * @param damageReduction      Fraction of the incoming damage to absorb, in {@code [0, 1]}. {@code 1} blocks the hit
 *                             entirely; {@code 0} lets the full damage through (useful when an {@code onBlocked}
 *                             implementation wants to ignore certain damage sources).
 * @param disable              Whether the shield should be disabled after this hit. When {@code true}, BLib puts the
 *                             item on cooldown via {@code Player.getCooldowns().addCooldown} for
 *                             {@link #disableDurationTicks} ticks, mirroring vanilla axe-disables-shield behavior. Has
 *                             no effect on non-player users.
 * @param disableDurationTicks Cooldown duration in ticks when {@link #disable} is {@code true}. Ignored when
 *                             {@link #disable} is {@code false}.
 */
public record BlockResult(
    float damageReduction,
    boolean disable,
    int disableDurationTicks
) {

    public BlockResult {
        if (damageReduction < 0f || damageReduction > 1f) {
            throw new IllegalArgumentException("damageReduction must be in [0, 1], got " + damageReduction);
        }
        if (disable && disableDurationTicks <= 0) {
            throw new IllegalArgumentException("disableDurationTicks must be positive when disable=true, got " + disableDurationTicks);
        }
    }

    /** Block the entire hit, do not disable. */
    public static BlockResult fullBlock() {
        return new BlockResult(1f, false, 0);
    }

    /** Block a fraction of the hit, do not disable. */
    public static BlockResult partial(float reduction) {
        return new BlockResult(reduction, false, 0);
    }

    /** Block the entire hit and disable the shield for the given tick count. */
    public static BlockResult blockedAndDisabled(int disableDurationTicks) {
        return new BlockResult(1f, true, disableDurationTicks);
    }

    /** Let the full hit through (no reduction, no disable). Useful for ignoring specific damage sources. */
    public static BlockResult passThrough() {
        return new BlockResult(0f, false, 0);
    }
}
