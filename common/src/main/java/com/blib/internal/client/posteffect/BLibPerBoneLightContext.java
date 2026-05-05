package com.blib.internal.client.posteffect;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Thread-local context for per-bone lighting. Pushed at the top of {@code LivingEntityRenderer.render} and popped on
 * return so any {@code ModelPart.compile} call inside that scope can see the current entity's level reference,
 * camera position, and "naturally hot" flag. Without this, the per-bone mixin has no way to resolve a bone's
 * pose-stack-derived position back into a world {@code BlockPos} for {@code level.getBrightness()} sampling.
 *
 * <p>{@code blockLightFloor} is the minimum block-light coord the bone is guaranteed to read in thermal mode —
 * for entities in the {@code THERMAL_HOT} tag this is 15 (max lit), so they appear as bright orange/red in IR
 * regardless of ambient lighting. For normal entities it's 0 (no floor; world block-light dominates).
 *
 * <p>Render thread only — no synchronization needed. Vanilla MC is single-threaded for entity rendering.
 */
@ApiStatus.Internal
public final class BLibPerBoneLightContext {

    public record Frame(Level level, Vec3 cameraPos, int blockLightFloor) {}

    private static @Nullable Frame current;

    private BLibPerBoneLightContext() {
        throw new UnsupportedOperationException();
    }

    public static void push(Level level, Vec3 cameraPos, int blockLightFloor) {
        current = new Frame(level, cameraPos, blockLightFloor);
    }

    public static void pop() {
        current = null;
    }

    public static @Nullable Frame current() {
        return current;
    }
}
