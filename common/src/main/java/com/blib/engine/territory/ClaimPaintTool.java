package com.blib.engine.territory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.runtime.EventBus;
import com.blib.engine.runtime.tool.ActiveTool;
import com.blib.engine.runtime.tool.ToolChangedEvent;
import com.blib.engine.runtime.tool.ToolStateMachine;

/**
 * Mutable singleton holding viewport claim-paint mode state. When active, LMB-drag in the viewport claims chunks for
 * the inspected faction, RMB-drag unclaims. The {@link #hoveredChunk} is updated each frame by the viewport renderer so
 * the world overlay can highlight the chunk under the cursor.
 * <p>
 * The {@link #paintTarget} is the currently-inspected {@code FactionSelectable}'s id, resolved from
 * {@link com.blib.engine.domain.selection.picking.SelectionManager} each frame — switching the selection mid-paint
 * redirects the paint target on the next click/drag tick without an explicit handoff.
 * <p>
 * Mutual exclusion with other armed tools (block volume, jigsaw piece, entity spawn) is handled through
 * {@link ToolStateMachine}: {@link #activate} switches to {@link ActiveTool#CLAIM_PAINT}, and a tool-changed subscriber
 * installed in {@link #installToolListener} deactivates this singleton when any other tool takes over.
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
        active = true;
        // Activates CLAIM_PAINT through the tool state machine; subscribed tool listeners (block volume, jigsaw piece,
        // entity spawn) self-disarm so a contradictory engine state — like a leftover AABB wireframe while painting
        // chunks — can't occur.
        ToolStateMachine.get().activate(ActiveTool.CLAIM_PAINT);
    }

    public static void deactivate() {
        active = false;
        paintTarget = null;
        hoveredChunk = null;
    }

    /**
     * Install the subscriber that deactivates this tool when any other tool becomes active. Called once per session
     * from {@link com.blib.engine.session.EngineMode#enter}.
     */
    public static void installToolListener() {
        EventBus.get().subscribe(ToolChangedEvent.class, e -> {
            if (e.current() != ActiveTool.CLAIM_PAINT && active) {
                deactivate();
            }
        });
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
