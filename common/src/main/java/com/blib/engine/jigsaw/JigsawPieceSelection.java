package com.blib.engine.jigsaw;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Mutable singleton holding the user's currently-selected jigsaw piece in engine mode. When non-null, the world preview
 * renderer draws the piece at the targeted block position and a viewport LMB triggers placement instead of selection.
 * <p>
 * Cleared automatically on {@link com.blib.engine.session.EngineMode#exit()} so a stale selection doesn't bleed into a
 * later session. Rotation/mirror are placeholder fields for the next iteration; for the MVP they stay at {@code NONE}.
 */
@ApiStatus.Internal
public final class JigsawPieceSelection {

    private static @Nullable ResourceLocation selectedId;

    private static Rotation rotation = Rotation.NONE;

    private static Mirror mirror = Mirror.NONE;

    private JigsawPieceSelection() {}

    public static @Nullable ResourceLocation selectedId() {
        return selectedId;
    }

    public static boolean hasSelection() {
        return selectedId != null;
    }

    public static void select(ResourceLocation id) {
        selectedId = id;
    }

    public static void clear() {
        selectedId = null;
    }

    public static Rotation rotation() {
        return rotation;
    }

    public static Mirror mirror() {
        return mirror;
    }
}
