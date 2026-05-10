package com.blib.engine.spawn;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.jigsaw.JigsawPieceSelection;

/**
 * Mutable singleton holding the user's currently-armed entity type for the engine's spawn-on-click flow. Mirrors
 * {@link JigsawPieceSelection}: when non-null, a viewport LMB triggers a spawn instead of a selection / placement, and
 * the cursor swaps to a crosshair while the cursor is over the viewport.
 * <p>
 * Mutually exclusive with {@link JigsawPieceSelection}: arming an entity clears the held jigsaw piece (and vice versa,
 * via the corresponding hook in this class). Two simultaneously-held click actions on the same LMB would be ambiguous,
 * and forcing the user to manually clear one before holding the other adds friction without any benefit.
 * <p>
 * Cleared automatically on {@link com.blib.engine.session.EngineMode#exit()} and on Esc cascade in
 * {@link com.blib.engine.ui.EngineWorkspaceScreen}, so a stale selection doesn't bleed into the next session.
 */
@ApiStatus.Internal
public final class EntitySpawnSelection {

    private static @Nullable ResourceLocation selectedTypeId;

    private EntitySpawnSelection() {}

    public static @Nullable ResourceLocation selectedTypeId() {
        return selectedTypeId;
    }

    public static boolean hasSelection() {
        return selectedTypeId != null;
    }

    /**
     * Arm the given entity type for spawning. Clears any held jigsaw piece so the LMB-place dispatch is unambiguous —
     * the viewport's mouseClicked checks one or the other, never both.
     */
    public static void select(ResourceLocation typeId) {
        selectedTypeId = typeId;
        JigsawPieceSelection.clear();
    }

    public static void clear() {
        selectedTypeId = null;
    }
}
