package com.blib.api.client.posteffect.v1;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Thread-local context for per-bone lighting. A consumer mod (e.g. a vision post-effect) pushes a frame around its
 * entity-renderer hook so the framework's {@code ModelPart.compile} mixin can resolve each bone's pose-stack-derived
 * position back into a world {@code BlockPos} for {@code level.getBrightness()} sampling.
 * <p>
 * {@code blockLightFloor} is the minimum block-light coord any bone is guaranteed to read while the frame is pushed —
 * for "naturally hot" entities this is typically near maximum, so the entity reads as fully-lit regardless of ambient
 * lighting; for normal entities it's 0 (no floor; world block-light dominates).
 * <p>
 * Render thread only — no synchronization needed. Vanilla MC is single-threaded for entity rendering. The pushed frame
 * is overwritten by the next push and cleared by {@link #pop()}; do not rely on it being preserved across renderer
 * calls.
 */
public final class BLibPerBoneLightContext {

    public record Frame(
        Level level,
        Vec3 cameraPos,
        int blockLightFloor
    ) {}

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
