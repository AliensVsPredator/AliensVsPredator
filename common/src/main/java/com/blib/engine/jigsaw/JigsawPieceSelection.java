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
 * later session. Rotation and mirror are user-driven via R/M hotkeys and scroll-while-placing; both reset to
 * {@code NONE} when the user picks a different piece, so per-piece muscle memory doesn't leak across selections.
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

    /**
     * Set the active piece. Resets rotation / mirror to {@code NONE} if the id is different from the previous selection
     * — matches the common authoring pattern where each piece starts at "default orientation" so the user doesn't carry
     * over leftover transforms from the last piece. Re-selecting the same id is a no-op for the transform state, so
     * accidentally clicking the same card twice doesn't reset their work.
     * <p>
     * Mutually exclusive with the entity-spawn selection and any active block-volume selection: arming a piece clears
     * both so the viewport's LMB-dispatch picks an unambiguous action and no leftover AABB wireframe coexists with the
     * piece's placement preview.
     */
    public static void select(ResourceLocation id) {
        if (!id.equals(selectedId)) {
            rotation = Rotation.NONE;
            mirror = Mirror.NONE;
        }
        selectedId = id;
        com.blib.engine.spawn.EntitySpawnSelection.clear();
        com.blib.engine.domain.selection.volume.BlockSelection.clear();
    }

    public static void clear() {
        selectedId = null;
        rotation = Rotation.NONE;
        mirror = Mirror.NONE;
    }

    public static Rotation rotation() {
        return rotation;
    }

    public static Mirror mirror() {
        return mirror;
    }

    /**
     * Cycle rotation by {@code direction} steps (typically +1 or -1) through the four {@link Rotation} values. Positive
     * direction maps to clockwise-when-viewed-from-above, matching the natural "scroll up = rotate right" convention
     * used by most authoring tools.
     */
    public static void cycleRotation(int direction) {
        var values = Rotation.values();
        var len = values.length;
        rotation = values[((rotation.ordinal() + direction) % len + len) % len];
    }

    /**
     * Cycle mirror by one step through the {@link Mirror} values ({@code NONE} → {@code LEFT_RIGHT} →
     * {@code FRONT_BACK}).
     */
    public static void cycleMirror() {
        var values = Mirror.values();
        mirror = values[(mirror.ordinal() + 1) % values.length];
    }
}
