package com.blib.engine.territory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Mutable singleton holding viewport claim-paint mode state. When active, LMB-drag in the viewport claims chunks for
 * the inspected faction, RMB-drag unclaims. The {@link #hoveredChunk} is updated each frame by the viewport renderer so
 * the world overlay can highlight the chunk under the cursor.
 * <p>
 * The {@link #paintTarget} is the currently-inspected {@code FactionSelectable}'s id, resolved from
 * {@link com.blib.engine.selection.SelectionManager} each frame — switching the selection mid-paint redirects the paint
 * target on the next click/drag tick without an explicit handoff.
 */
@ApiStatus.Internal
public final class ClaimPaintTool {

    private static boolean active;

    private static @Nullable ResourceLocation paintTarget;

    private static @Nullable ChunkPos hoveredChunk;

    /**
     * Master visibility flag for the world-overlay (the floor planes drawn over each claimed chunk). Toggled from the
     * View menu; defaults to on so first-time users see claims without needing to discover the menu item.
     */
    private static boolean overlayVisible = true;

    private ClaimPaintTool() {}

    public static boolean isActive() {
        return active;
    }

    public static void activate() {
        // Paint mode is mutually exclusive with editing a block volume — leaving an AABB wireframe up while the user
        // starts painting chunks would be a contradictory engine state.
        com.blib.engine.blockselection.BlockSelection.clear();
        active = true;
    }

    public static void deactivate() {
        active = false;
        paintTarget = null;
        hoveredChunk = null;
    }

    public static @Nullable ResourceLocation paintTarget() {
        return paintTarget;
    }

    public static void setPaintTarget(@Nullable ResourceLocation factionId) {
        paintTarget = factionId;
    }

    public static @Nullable ChunkPos hoveredChunk() {
        return hoveredChunk;
    }

    public static void setHoveredChunk(@Nullable ChunkPos pos) {
        hoveredChunk = pos;
    }

    public static boolean isOverlayVisible() {
        return overlayVisible;
    }

    public static void setOverlayVisible(boolean visible) {
        overlayVisible = visible;
    }

    public static void toggleOverlayVisible() {
        overlayVisible = !overlayVisible;
    }
}
