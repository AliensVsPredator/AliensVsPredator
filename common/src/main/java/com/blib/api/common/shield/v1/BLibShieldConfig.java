package com.blib.api.common.shield.v1;

import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Static configuration for a {@link BLibShieldItem}. Carries the values BLib needs to wire vanilla-style use
 * semantics (use-duration, animation) and to gate the blocking pipeline (cone check, block sound). Everything
 * dynamic — how much damage to absorb, whether to disable on a given hit — is decided per-hit by
 * {@link BLibShieldItem#onBlocked}.
 *
 * @param useDuration         Tick count returned from {@code Item.getUseDuration}. Vanilla shields use 72000
 *                            ("effectively forever"); custom shields can shorten it for items that fatigue.
 * @param blockAngleDegrees   Total angular width of the front block cone, in degrees. {@code 360} means
 *                            omnidirectional (any incoming attack can be blocked); vanilla shields use
 *                            roughly {@code 100}. Used by the blocking pipeline to reject hits coming from
 *                            outside the cone before {@code onBlocked} runs.
 * @param blockSound          Optional sound played when the shield successfully enters the {@code onBlocked}
 *                            callback (i.e. the cone check passed). {@code null} suppresses the sound entirely.
 */
public record BLibShieldConfig(
    int useDuration,
    float blockAngleDegrees,
    @Nullable SoundEvent blockSound
) {

    public BLibShieldConfig {
        if (useDuration <= 0) {
            throw new IllegalArgumentException("useDuration must be positive, got " + useDuration);
        }
        if (blockAngleDegrees <= 0 || blockAngleDegrees > 360) {
            throw new IllegalArgumentException("blockAngleDegrees must be in (0, 360], got " + blockAngleDegrees);
        }
    }
}
