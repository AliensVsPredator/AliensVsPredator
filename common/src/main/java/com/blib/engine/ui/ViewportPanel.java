package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import com.blib.engine.blockselection.BlockSelection;
import com.blib.engine.blockselection.BlockSelectionScaleGizmo;
import com.blib.engine.blockselection.BlockSelectionTranslateGizmo;
import com.blib.engine.blockselection.MoveBlocksGizmo;
import com.blib.engine.input.ActiveKeybindings;
import com.blib.engine.input.Keybindings;
import com.blib.engine.jigsaw.JigsawPieceLibrary;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.jigsaw.JigsawPlacementCursor;
import com.blib.engine.jigsaw.placement.CollisionScanner;
import com.blib.engine.jigsaw.placement.JigsawPlacementOptions;
import com.blib.engine.jigsaw.placement.JigsawTool;
import com.blib.engine.jigsaw.placement.JigsawWorldRaycast;
import com.blib.engine.jigsaw.placement.PlacementContext;
import com.blib.engine.jigsaw.placement.PlacementMode;
import com.blib.engine.selection.BlockSelectable;
import com.blib.engine.selection.BlockVolumeSelectable;
import com.blib.engine.selection.EntitySelectable;
import com.blib.engine.selection.FactionSelectable;
import com.blib.engine.selection.Selectable;
import com.blib.engine.selection.SelectionManager;
import com.blib.engine.session.EngineMode;
import com.blib.engine.session.EngineNavigation;
import com.blib.engine.spawn.EntitySpawnSelection;
import com.blib.engine.territory.ClaimPaintTool;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SAddChunkClaimPayload;
import com.blib.mod.common.network.packet.C2SMoveSelectionPayload;
import com.blib.mod.common.network.packet.C2SPlaceJigsawPiecePayload;
import com.blib.mod.common.network.packet.C2SRemoveChunkClaimPayload;
import com.blib.mod.common.network.packet.C2SSpawnEntityPayload;

/**
 * The viewport panel — the rect where the downsampled world+HUD blit lands. This panel doesn't draw anything itself; it
 * just records its inner rect during {@link #render} and routes mouse events to the engine session. Tool actions live
 * on LMB / RMB; camera control lives on MMB so users can frame the shot regardless of what tool they're holding:
 * <ul>
 * <li>LMB click → place piece (if a jigsaw piece is held) else select entity via ray-cast.</li>
 * <li>RMB click → undo last placement (if a piece is held) else fire {@link RightClickHandler} (entity context menu).
 * </li>
 * <li>MMB drag → orbit around the pivot computed at press time.</li>
 * <li>Shift+MMB drag → screen-plane pan.</li>
 * <li>Ctrl+MMB drag → dolly zoom toward / away from the pivot.</li>
 * <li>Scroll → rotate piece (if held) else multiplicative zoom toward / away from the pivot.</li>
 * </ul>
 * Modifier state for the MMB gesture is latched at press time, not live-sampled, so a Shift release mid-drag won't flip
 * orbit→pan unexpectedly. Mouse coords arrive in workspace logical pixels; deltas are converted to raw-pixel equivalent
 * via the GUI scale so orbit / pan / dolly sensitivities feel the same regardless of MC's GUI scale.
 */
@ApiStatus.Internal
public final class ViewportPanel implements Panel {

    /** Notified when the user right-clicks (without a held piece) inside the viewport. */
    public interface RightClickHandler {

        void onRightClick(@Nullable LivingEntity entity, double cursorX, double cursorY);

        /**
         * RMB hit on the block-volume selection's AABB. Distinct from {@link #onRightClick} so the host can open a
         * volume-specific context menu rather than the entity menu. Default is no-op so existing handlers don't have to
         * change.
         */
        default void onRightClickVolume(double cursorX, double cursorY) {}

        /**
         * RMB hover-hit on a placed jigsaw piece. Receives the piece UUID so the menu can wire actions (open inspector,
         * delete, etc.). Default no-op so existing handlers don't break.
         */
        default void onRightClickPiece(java.util.UUID pieceId, double cursorX, double cursorY) {}
    }

    private final String title;

    private final @Nullable RightClickHandler rightClickHandler;

    /**
     * Adapter for spawning modal confirms — used by the WARN-policy placement path to ask the user before stamping a
     * piece onto colliding blocks. Reuses {@link ProjectContentActionHandler} (workspace screen forwards to its shared
     * {@code ConfirmDialog}) rather than introducing a viewport-specific handler.
     */
    private final @Nullable ProjectContentActionHandler confirmHandler;

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    private @Nullable MmbDrag mmbDrag;

    /**
     * Captured at the end of {@link #render} when the cursor hovers a transport-toolbar button — the workspace reads
     * this back via {@link #tooltipText()} and renders the tooltip near the cursor. Null when nothing tooltip-worthy is
     * hovered this frame.
     */
    private @Nullable Component hoveredTooltip;

    /**
     * Active drag-to-pick anchor — the block clicked on LMB-press, used as cornerA throughout the drag while every
     * mouseDragged updates cornerB. {@code null} when no drag-pick is in flight. Cleared on mouseReleased.
     */
    private @Nullable BlockPos dragPickAnchor;

    /**
     * Squared click-vs-drag threshold in workspace-logical pixels. INSPECT mode treats LMB-press → release as a click
     * (inspect a single object); only motion past this threshold during the drag promotes it to a block-volume marquee.
     * 4 px matches Minecraft's own click-vs-drag feel.
     */
    private static final double DRAG_PROMOTE_THRESHOLD_PX = 4.0;

    private static final double DRAG_PROMOTE_THRESHOLD_SQ = DRAG_PROMOTE_THRESHOLD_PX * DRAG_PROMOTE_THRESHOLD_PX;

    /**
     * Block under cursor at the most recent INSPECT-mode LMB-press, or {@code null} if the cursor was over the sky.
     * Used as the marquee anchor when {@link #pendingClickActive} promotes to a volume drag.
     */
    private @Nullable BlockPos pendingClickBlock;

    /**
     * Cursor position at LMB-press in workspace-logical pixels. The {@link #DRAG_PROMOTE_THRESHOLD_PX} test compares
     * against this snapshot.
     */
    private double pressMouseX;

    private double pressMouseY;

    /**
     * Whether Shift was held at LMB-press. Latched (not live-sampled) so a Shift release mid-gesture can't flip the
     * promotion decision, mirroring the {@link MmbDrag} latch pattern.
     */
    private boolean pressShiftDown;

    /**
     * True between an INSPECT-mode LMB-press and either (a) promotion to a volume marquee, (b) the user's release
     * without promotion, or (c) a determination that the current candidate isn't promotable. While true, mouseDragged
     * re-checks the threshold on every frame.
     */
    private boolean pendingClickActive;

    /**
     * Per-drag dedup set for claim-paint mode. While LMB/RMB is held the cursor sweeps multiple chunks; this set
     * remembers which ones we've already fired packets for so dragging back over them doesn't spam the network. Keyed
     * by {@code ChunkPos.toLong}. Cleared on mouseReleased.
     */
    private final java.util.Set<Long> claimPaintedThisDrag = new java.util.HashSet<>();

    /**
     * Mouse button (0 = claim, 1 = unclaim) of the active claim-paint drag, or {@code null} when no drag is in flight.
     */
    private @Nullable Integer claimPaintButton;

    public ViewportPanel(String title) {
        this(title, null, null);
    }

    public ViewportPanel(String title, @Nullable RightClickHandler rightClickHandler) {
        this(title, rightClickHandler, null);
    }

    public ViewportPanel(
        String title,
        @Nullable RightClickHandler rightClickHandler,
        @Nullable ProjectContentActionHandler confirmHandler
    ) {
        this.title = title;
        this.rightClickHandler = rightClickHandler;
        this.confirmHandler = confirmHandler;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public @Nullable Component tooltipText() {
        return hoveredTooltip;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;
        // No game-view drawing: the compositor has already painted the downsampled world+HUD into this exact rect on
        // the main render target before the workspace's panels render. Anything drawn here would obscure the live game
        // view — except the transport-control overlay, which is *meant* to sit on top of the corner.
        ViewportTransportToolbar.render(graphics, x, y);

        // Refresh the hover tooltip for the transport buttons each frame so the cursor reads the current world state
        // (paused vs running) without having to look at the icon.
        hoveredTooltip = null;
        var transportHover = ViewportTransportToolbar.hitTest(mouseX, mouseY, x, y);
        if (transportHover == ViewportTransportToolbar.Hit.PLAY) {
            hoveredTooltip = Component.literal(EngineTickControl.isPaused() ? "Game paused" : "Game running");
        }

        // Publish the rect in raw window-pixel space so the world-render hook (running in a different render pass)
        // can map cursor coords back into [0,1] viewport-relative coords for the placement preview's ray cast. The
        // workspace pose stack scales by SCALE = 0.375 around the panel rect; we transform through it to get screen-
        // logical, then multiply by guiScale to reach raw window pixels.
        var matrix = graphics.pose().last().pose();
        var topLeft = matrix.transformPosition((float) x, (float) y, 0f, new Vector3f());
        var bottomRight = matrix.transformPosition((float) (x + width), (float) (y + height), 0f, new Vector3f());
        var guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        var rawX = (int) Math.round(topLeft.x * guiScale);
        var rawY = (int) Math.round(topLeft.y * guiScale);
        var rawW = (int) Math.round((bottomRight.x - topLeft.x) * guiScale);
        var rawH = (int) Math.round((bottomRight.y - topLeft.y) * guiScale);
        JigsawPlacementCursor.updateViewportRect(rawX, rawY, rawW, rawH);

        // Paint-mode hover + paint target tracking. The renderer reads these to highlight the chunk under the cursor.
        // The paint target is resolved from the current selection each frame so switching factions in the inspector
        // mid-paint takes effect immediately.
        if (ClaimPaintTool.isActive()) {
            var session = EngineMode.get().session();
            ClaimPaintTool.setHoveredChunk(session != null ? floorPlaneChunkUnderCursor(session) : null);
            var single = SelectionManager.current().single();
            ClaimPaintTool.setPaintTarget(single instanceof FactionSelectable fs ? fs.factionId() : null);
        } else if (ClaimPaintTool.hoveredChunk() != null) {
            ClaimPaintTool.setHoveredChunk(null);
        }

        // Hover probe — runs every frame so the user sees a "what would I select" outline on the block/entity under
        // the cursor. Suppressed when the cursor is outside the viewport rect (cursor over a side panel shouldn't
        // light up a phantom block) or when the session isn't ready.
        var session = EngineMode.get().session();
        if (session != null && inRect(mouseX, mouseY)) {
            var relX = (mouseX - rectX) / (double) rectWidth;
            var relY = (mouseY - rectY) / (double) rectHeight;
            com.blib.engine.selection.EngineHoverProbe.update(session, relX, relY);
        } else {
            com.blib.engine.selection.EngineHoverProbe.clear();
        }
    }

    /**
     * Resolve the chunk under the cursor by intersecting the camera-cursor ray with the same horizontal floor plane
     * that {@link com.blib.engine.territory.ChunkClaimOverlayRenderer} draws (world min build height + 1). Picking via
     * the first block hit instead — what the previous version did — felt off in paint mode because the hovered chunk
     * would follow whatever block the user happened to be looking at (e.g. a tree branch or a mountainside), not the
     * chunk whose floor plane sat visually under the cursor. Returns {@code null} when the ray doesn't reach the floor
     * (looking up at the sky, or grazingly horizontal).
     */
    private static @Nullable net.minecraft.world.level.ChunkPos floorPlaneChunkUnderCursor(com.blib.engine.session.EngineSession session) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }
        var dir = JigsawPlacementCursor.cursorRayDirection(session);
        if (dir == null) {
            return null;
        }
        var origin = JigsawPlacementCursor.cursorRayOrigin(session);
        // Floor plane Y matches ChunkClaimOverlayRenderer.FLOOR_Y_OFFSET so hover lands on the plane the user sees.
        var floorY = mc.level.getMinBuildHeight() + 1.0;
        // Need a strictly-downward component for the ray to reach the floor in front of the camera.
        if (dir.y >= -1.0E-6) {
            return null;
        }
        var t = (floorY - origin.y) / dir.y;
        if (t <= 0) {
            return null;
        }
        var hitX = origin.x + dir.x * t;
        var hitZ = origin.z + dir.z * t;
        return new net.minecraft.world.level.ChunkPos(
            net.minecraft.core.SectionPos.blockToSectionCoord((int) Math.floor(hitX)),
            net.minecraft.core.SectionPos.blockToSectionCoord((int) Math.floor(hitZ))
        );
    }

    /**
     * Paint-mode click/drag dispatch. Returns {@code true} when the event was consumed by paint mode so callers stop
     * routing to the default tool paths. Resolves the chunk via the world raycast, dedups across the drag stroke so
     * sweeping back over a chunk doesn't fire duplicate packets, and routes LMB→add / RMB→remove.
     */
    private boolean dispatchClaimPaint(int button) {
        if (!ClaimPaintTool.isActive() || (button != 0 && button != 1)) {
            return false;
        }
        var session = EngineMode.get().session();
        if (session == null) {
            return false;
        }
        var target = ClaimPaintTool.paintTarget();
        if (target == null) {
            // Tool active but no faction selected to paint for — swallow the click so it doesn't fall through into
            // default selection / placement behavior (which would be surprising while a paint tool is "armed").
            return true;
        }
        var chunk = floorPlaneChunkUnderCursor(session);
        if (chunk == null) {
            return true;
        }
        var key = chunk.toLong();
        if (!claimPaintedThisDrag.add(key)) {
            return true;
        }
        if (button == 0) {
            BLib.MOD.networking().sendToServer(new C2SAddChunkClaimPayload(target, chunk.x, chunk.z));
        } else {
            BLib.MOD.networking().sendToServer(new C2SRemoveChunkClaimPayload(target, chunk.x, chunk.z));
        }
        claimPaintButton = button;
        return true;
    }

    /**
     * Capture MMB presses ahead of normal click dispatch so the workspace routes the entire MMB drag — including frames
     * where the cursor leaves the viewport rect — back to this panel. Without capture, fast pans that move the cursor
     * off the rect would have their drags routed to whatever panel the cursor happens to land on, which either dies on
     * the floor or yanks UI scrollbars unexpectedly. LMB / RMB stay on the standard click path.
     */
    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        if (!inRect(mouseX, mouseY)) {
            return false;
        }

        // Transport toolbar (play/pause + step) lives in the top-left corner of the viewport — hit-test it first so a
        // click on the button doesn't fall through to a gizmo pick or selection action behind it. Only LMB triggers.
        if (button == 0) {
            var transportHit = ViewportTransportToolbar.hitTest(mouseX, mouseY, rectX, rectY);
            if (transportHit == ViewportTransportToolbar.Hit.PLAY) {
                EngineTickControl.toggle();
                return true;
            }
            if (transportHit == ViewportTransportToolbar.Hit.STEP) {
                EngineTickControl.step(ViewportTransportToolbar.STEP_TICKS);
                return true;
            }
        }

        // The viewport has no edge UI (no scrollbars, no inline buttons in the outer band), so LMB clicks within
        // {@link EngineWorkspaceScreen#DIVIDER_HIT_PX} of any edge are far more likely to be a divider-drag attempt
        // than a gizmo pick — and a selected entity / block volume can project gizmo handles right at the edge,
        // making divider clicks impossible without this yield. MMB stays captured so camera orbit / pan still latches
        // when the cursor starts near the edge.
        if (button == 0 && nearViewportEdge(mouseX, mouseY)) {
            return false;
        }

        var session = EngineMode.get().session();
        if (session == null) {
            return false;
        }

        // Claim paint mode takes over LMB/RMB before any other tool. Capturing keeps the drag routed here even if the
        // cursor leaves the viewport rect mid-stroke (matches gizmo / mmb behavior).
        if (ClaimPaintTool.isActive() && (button == 0 || button == 1)) {
            return dispatchClaimPaint(button);
        }

        // LMB on a selection gizmo: only the active-mode gizmo is pickable since the others aren't rendered. Re-picks
        // at click time rather than reading any cached hover state so the grab matches the cursor's *current*
        // position, not the most recent render frame.
        if (button == 0 && BlockSelection.picking() == BlockSelection.PickingState.NONE) {
            // Entity gizmo arms first (and only) when an entity is the active selection — block gizmos are hidden
            // by their renderers in this case so a click against an entity wouldn't reach a block-handle anyway, but
            // routing entity-first here keeps the dispatch deterministic.
            var entitySel = com.blib.engine.selection.SelectionManager.current().single();
            if (entitySel instanceof com.blib.engine.selection.EntitySelectable es && es.entity() != null) {
                var entity = es.entity();
                if (com.blib.engine.entityselection.EntityGizmoMode.get() == com.blib.engine.entityselection.EntityGizmoMode.TRANSLATE) {
                    var hit = com.blib.engine.entityselection.EntityTranslateGizmo.pickUnderCursorWithDistance(session, entity);
                    if (hit != null) {
                        com.blib.engine.entityselection.EntityTranslateGizmo.beginDrag(entity, hit.axis(), session);
                        return true;
                    }
                } else {
                    var hit = com.blib.engine.entityselection.EntityScaleGizmo.pickUnderCursorWithDistance(session, entity);
                    if (hit != null) {
                        com.blib.engine.entityselection.EntityScaleGizmo.beginDrag(entity, session);
                        return true;
                    }
                }
            } else {
                var gizmoMode = BlockSelection.gizmoMode();
                if (gizmoMode == BlockSelection.GizmoMode.SCALE_VOLUME) {
                    var scaleHit = BlockSelectionScaleGizmo.pickUnderCursorWithDistance(session);
                    if (scaleHit != null) {
                        BlockSelectionScaleGizmo.beginDrag(scaleHit.face(), session);
                        return true;
                    }
                } else if (gizmoMode == BlockSelection.GizmoMode.TRANSLATE_VOLUME) {
                    var translateHit = BlockSelectionTranslateGizmo.pickUnderCursorWithDistance(session);
                    if (translateHit != null) {
                        BlockSelectionTranslateGizmo.beginDrag(translateHit.axis(), session);
                        return true;
                    }
                } else if (gizmoMode == BlockSelection.GizmoMode.MOVE_BLOCKS) {
                    var moveHit = MoveBlocksGizmo.pickUnderCursorWithDistance(session);
                    if (moveHit != null) {
                        // Copy-vs-cut used to latch off Alt, but Alt was retired (Linux window-manager conflict).
                        // Always cuts for now; restoring copy-mode means picking a non-conflicting modifier and
                        // re-introducing the keybinding.
                        MoveBlocksGizmo.beginDrag(moveHit.axis(), session, false);
                        return true;
                    }
                }
            }
        }

        if (button != 2) {
            return false;
        }

        var relX = (mouseX - rectX) / (double) rectWidth;
        var relY = (mouseY - rectY) / (double) rectHeight;

        // Latch the press-time modifier state. Live-sampling during drag would let a stray Shift-release flip an
        // active orbit into a pan mid-stroke, which is jarring; latching keeps the gesture mode stable end-to-end.
        var latched = new MmbDrag(Screen.hasShiftDown(), Screen.hasControlDown());
        this.mmbDrag = latched;
        if (!latched.shift() && !latched.ctrl()) {
            EngineNavigation.beginOrbitDrag(session, relX, relY);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!inRect(mouseX, mouseY)) {
            return false;
        }

        var session = EngineMode.get().session();
        if (session == null) {
            return false;
        }

        var relX = (mouseX - rectX) / (double) rectWidth;
        var relY = (mouseY - rectY) / (double) rectHeight;

        if (button == 0) {
            // Capture-corner pick has top priority: when the Capture Panel is in "picking A" or "picking B" state,
            // the next LMB in the viewport sets that corner instead of placing or selecting. Default behavior is to
            // include the *clicked* block in the volume (what users intuitively expect — "click the block I want
            // to be a corner"). Holding Shift falls back to the surface-adjacent cell, mirroring vanilla item
            // placement and the jigsaw tool — useful when the user wants the volume to start in the air gap above
            // a surface (e.g. capturing only what they're about to build, not the floor under it).
            if (BlockSelection.picking() != BlockSelection.PickingState.NONE) {
                var hit = JigsawPlacementCursor.clipFromCursor(session);
                if (hit != null) {
                    var pos = net.minecraft.client.gui.screens.Screen.hasShiftDown()
                        ? hit.getBlockPos().relative(hit.getDirection())
                        : hit.getBlockPos();
                    BlockSelection.onBlockClicked(pos);
                }
                return true;
            }

            // (Gizmo handle clicks are handled in mouseClickedCapture so the workspace captures the panel for the
            // duration of the drag — we never reach this point when a gizmo handle was hit.)

            // When a jigsaw piece is selected, LMB in the viewport means "place". The placement (anchor + rotation +
            // mirror) comes from the active resolver, not from raw cursor + selection state — for FREE mode the two
            // are identical, but later modes (jigsaw-snap) override rotation to align with a target jigsaw, and we
            // want the click to send what the user is actually seeing in the world preview.
            var selectedPieceId = JigsawPieceSelection.selectedId();
            if (selectedPieceId != null) {
                var template = JigsawPieceLibrary.get(selectedPieceId);
                if (template != null) {
                    var ctx = new PlacementContext(
                        session,
                        template,
                        JigsawPieceSelection.rotation(),
                        JigsawPieceSelection.mirror()
                    );
                    var placement = JigsawTool.activeResolver().resolve(ctx);
                    if (placement != null) {
                        // Re-run the collision scan at click time rather than reading the renderer's frame state.
                        // The two should agree (both run against the same level state, same resolver result), but
                        // re-scanning gives us "the count as of this exact click" which is the right thing to gate
                        // policy decisions on. Frame state is a render-side cache; trusting it across the click event
                        // boundary would be one more invalidation rule to maintain.
                        var mc = net.minecraft.client.Minecraft.getInstance();
                        var policy = JigsawPlacementOptions.collisionPolicy();
                        var collisionCount = 0;
                        if (
                            mc.level != null
                                && (policy == JigsawPlacementOptions.CollisionPolicy.BLOCK
                                    || policy == JigsawPlacementOptions.CollisionPolicy.WARN)
                        ) {
                            var snapAnchor = JigsawTool.activeMode() == PlacementMode.JIGSAW_SNAP
                                ? JigsawWorldRaycast.raycastJigsaw(session)
                                : null;
                            collisionCount = CollisionScanner.scan(mc.level, placement, template, snapAnchor);
                        }
                        if (policy == JigsawPlacementOptions.CollisionPolicy.BLOCK && collisionCount > 0) {
                            return true;
                        }

                        // Snapshot the placement so the confirm-dialog runnable doesn't read stale fields. The dialog
                        // is modal — by the time the user confirms, the cursor may have moved and `placement` would
                        // resolve to a different anchor; we want the click-time decision to stick.
                        var pieceId = selectedPieceId;
                        var anchor = placement.anchor();
                        var rotation = placement.rotation();
                        var mirror = placement.mirror();
                        Runnable sendPlace = () -> BLib.MOD.networking()
                            .sendToServer(new C2SPlaceJigsawPiecePayload(pieceId, anchor, rotation.ordinal(), mirror.ordinal()));

                        if (
                            policy == JigsawPlacementOptions.CollisionPolicy.WARN
                                && collisionCount > 0
                                && confirmHandler != null
                        ) {
                            var msg = "Placing this piece will overwrite "
                                + collisionCount
                                + " existing block"
                                + (collisionCount == 1 ? "" : "s")
                                + ". Continue?";
                            confirmHandler.confirm("Overwrite Blocks", msg, "Place", true, sendPlace);
                            return true;
                        }

                        sendPlace.run();
                    }
                }
                return true;
            }

            // Entity-spawn dispatch — the UI replacement for /summon. Mutually exclusive with the jigsaw piece
            // path above (selecting one clears the other) so we only reach here when no piece is held. Spawn
            // position is the block adjacent to the clicked surface, matching item-placement intuition (the entity
            // stands on the face the user clicked).
            var spawnTypeId = EntitySpawnSelection.selectedTypeId();
            if (spawnTypeId != null) {
                var hit = JigsawPlacementCursor.clipFromCursor(session);
                if (hit != null) {
                    var anchor = hit.getBlockPos().relative(hit.getDirection());
                    var mc = net.minecraft.client.Minecraft.getInstance();
                    if (mc.player != null) {
                        var dim = mc.player.level().dimension().location();
                        BLib.MOD.networking().sendToServer(new C2SSpawnEntityPayload(spawnTypeId, anchor, dim));
                    }
                }
                return true;
            }

            // Capture pre-handler state for Shift+LMB extension. performSelectionAt and BlockSelection.clearVolume
            // (called inside it on hit) mutate both the current selection and the staged corners, so snapshotting
            // here is required to remember the prior anchor for "select between two clicks".
            var shiftHeld = Screen.hasShiftDown();
            var previousSelection = SelectionManager.current().single();
            var previousCornerA = BlockSelection.cornerA();

            // LMB picks the closest selectable along the cursor ray (entity / jigsaw / generic block);
            // performSelectionAt clears the selection on a sky miss. Then stage the deferred-decision state so
            // mouseDragged can promote to a volume marquee on enough motion (or immediately if Shift was held).
            EngineNavigation.performSelectionAt(session, relX, relY);

            var blockHit = JigsawPlacementCursor.clipFromCursor(session);
            this.pendingClickBlock = blockHit != null ? blockHit.getBlockPos() : null;
            this.pressMouseX = mouseX;
            this.pressMouseY = mouseY;
            this.pressShiftDown = shiftHeld;

            // Pending-click promotion is only valid when the candidate is a block — entities aren't promotable on a
            // drag-nudge. Preserves the feel of "I clicked an entity, then nudged the mouse a few pixels before
            // releasing". Shift extend-from-previous below bypasses this filter and promotes anyway.
            var sel = SelectionManager.current().single();
            this.pendingClickActive = !(sel instanceof EntitySelectable);

            // Shift+LMB extends the marquee: cornerA = the previous single-block / volume anchor, cornerB = the
            // just-clicked block. Lets the user "select between two clicks" without dragging. When there's no
            // meaningful prior anchor (entity, faction, tag, nothing), it falls back to a fresh 1-block volume at
            // the click point so the gesture still feels deterministic.
            if (pressShiftDown && pendingClickBlock != null) {
                var anchor = anchorForShiftExtend(previousSelection, previousCornerA, pendingClickBlock);
                BlockSelection.setCornersDirect(anchor, pendingClickBlock);
                SelectionManager.selectSingle(new BlockVolumeSelectable());
                dragPickAnchor = anchor;
                this.pendingClickActive = false;
            }

            return true;
        }

        if (button == 1) {
            // Place mode is a non-selection workflow — the user is actively placing pieces, so RMB must not open a
            // context menu (which would select a block / piece / entity, defeating the placement focus). Consume the
            // click so it doesn't fall through to anything else.
            if (JigsawPieceSelection.selectedId() != null) {
                return true;
            }
            // RMB opens a context menu — never mutates the selection. The hover probe (refreshed every render) tells
            // us what's under the cursor, so we can dispatch to the right menu without a fresh raycast or a
            // selectSingle
            // call. The previous design's volume-re-select / performSelectionAt-on-RMB pattern was confusing — RMB on
            // a generic block silently swapped the inspector to that block, even though no context menu opened.
            if (rightClickHandler != null) {
                // AABB volume takes priority — RMB anywhere inside the current selection's AABB opens its menu,
                // independent of the hover probe (the volume might extend behind another hit).
                var aabbOpt = BlockSelection.aabb();
                if (aabbOpt.isPresent() && rmbHitsAabb(session, aabbOpt.get())) {
                    rightClickHandler.onRightClickVolume(mouseX, mouseY);
                    return true;
                }
                var hover = com.blib.engine.selection.EngineHoverProbe.current();
                if (hover instanceof com.blib.engine.selection.EngineHoverProbe.Target.Piece pt) {
                    rightClickHandler.onRightClickPiece(pt.id(), mouseX, mouseY);
                    return true;
                }
                if (hover instanceof com.blib.engine.selection.EngineHoverProbe.Target.Entity et) {
                    rightClickHandler.onRightClick(et.entity(), mouseX, mouseY);
                    return true;
                }
                // Block or no hit — no block context menu today, so this dismisses any open menu (the handler treats
                // a null entity as "close").
                rightClickHandler.onRightClick(null, mouseX, mouseY);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        var session = EngineMode.get().session();
        if (session == null) {
            return false;
        }

        // Continue an in-flight claim-paint stroke. The button captured at mouseClickedCapture wins for the whole
        // drag — switching buttons mid-stroke (impossible in practice but defensive here) doesn't change direction.
        if (claimPaintButton != null && button == claimPaintButton) {
            return dispatchClaimPaint(button);
        }

        // Promote a pending INSPECT-mode click to a volume marquee once cursor motion crosses the threshold. The
        // candidate eligibility (entity / jigsaw filtered out; only generic blocks or empty miss promotable) was
        // already checked at press time — pendingClickActive is only true when promotion is possible. Sky presses
        // (pendingClickBlock == null) can't anchor a marquee — clear the pending state so we stop re-checking.
        if (button == 0 && pendingClickActive) {
            if (pendingClickBlock != null) {
                var dx = mouseX - pressMouseX;
                var dy = mouseY - pressMouseY;
                if (dx * dx + dy * dy >= DRAG_PROMOTE_THRESHOLD_SQ) {
                    BlockSelection.setCornersDirect(pendingClickBlock, pendingClickBlock);
                    SelectionManager.selectSingle(new BlockVolumeSelectable());
                    dragPickAnchor = pendingClickBlock;
                    pendingClickActive = false;
                }
            } else {
                pendingClickActive = false;
            }
        }

        // Drag-to-pick claims LMB drags between mouseClicked (anchor set) and mouseReleased (anchor cleared). Live-
        // updates cornerB to whatever block the cursor is over, so the wireframe grows as the user drags. Skipped
        // when the cursor leaves the world (clipFromCursor returns null) — last valid cornerB stays put.
        if (button == 0 && dragPickAnchor != null) {
            var hit = JigsawPlacementCursor.clipFromCursor(session);
            if (hit != null) {
                BlockSelection.setCornersDirect(dragPickAnchor, hit.getBlockPos());
            }
            return true;
        }

        // Gizmo drag claims LMB drags while any selection gizmo is grabbed. Other LMB drags are no-ops at this layer
        // (mmbDrag handles MMB orbit/pan/dolly below). Block and entity gizmos are mutually exclusive at click time
        // but the update path is unified so a single ray-fetch services whichever gizmo is live.
        if (
            button == 0
                && (BlockSelectionScaleGizmo.isDragging() || BlockSelectionTranslateGizmo.isDragging() || MoveBlocksGizmo.isDragging()
                    || com.blib.engine.entityselection.EntityTranslateGizmo.isDragging()
                    || com.blib.engine.entityselection.EntityScaleGizmo.isDragging())
        ) {
            var rayDir = JigsawPlacementCursor.cursorRayDirection(session);
            if (rayDir != null) {
                if (BlockSelectionScaleGizmo.isDragging()) {
                    BlockSelectionScaleGizmo.updateDrag(session, rayDir);
                }
                if (BlockSelectionTranslateGizmo.isDragging()) {
                    BlockSelectionTranslateGizmo.updateDrag(session, rayDir);
                }
                if (MoveBlocksGizmo.isDragging()) {
                    MoveBlocksGizmo.updateDrag(session, rayDir);
                }
                if (com.blib.engine.entityselection.EntityTranslateGizmo.isDragging()) {
                    com.blib.engine.entityselection.EntityTranslateGizmo.updateDrag(session, rayDir);
                }
                if (com.blib.engine.entityselection.EntityScaleGizmo.isDragging()) {
                    com.blib.engine.entityselection.EntityScaleGizmo.updateDrag(session, rayDir);
                }
            }
            return true;
        }

        if (button != 2 || mmbDrag == null) {
            return false;
        }

        // Convert workspace-logical deltas to raw-pixel deltas so orbit / pan / dolly sensitivity is independent of
        // GUI scale.
        var guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        var rawDx = deltaX * guiScale;
        var rawDy = deltaY * guiScale;

        if (mmbDrag.ctrl()) {
            EngineNavigation.applyDollyDelta(session, rawDy);
        } else if (mmbDrag.shift()) {
            EngineNavigation.applyPanDelta(session, rawDx, rawDy);
        } else {
            EngineNavigation.applyOrbitDelta(session, rawDx, rawDy);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var session = EngineMode.get().session();
        if (session == null) {
            return false;
        }

        // End an in-flight claim-paint stroke. Clearing the dedup set + button lets a fresh drag fire packets again.
        if (claimPaintButton != null && button == claimPaintButton) {
            claimPaintButton = null;
            claimPaintedThisDrag.clear();
            return true;
        }

        if (button == 0) {
            // Clear deferred-decision state whether or not the press promoted to a marquee — a release without
            // promotion is just a single-block inspect, and the pending fields aren't needed once the gesture ends.
            pendingClickActive = false;
            pendingClickBlock = null;
            if (dragPickAnchor != null) {
                dragPickAnchor = null;
                return true;
            }
        }

        if (
            button == 0
                && (BlockSelectionScaleGizmo.isDragging() || BlockSelectionTranslateGizmo.isDragging() || MoveBlocksGizmo.isDragging()
                    || com.blib.engine.entityselection.EntityTranslateGizmo.isDragging()
                    || com.blib.engine.entityselection.EntityScaleGizmo.isDragging())
        ) {
            BlockSelectionScaleGizmo.endDrag();
            BlockSelectionTranslateGizmo.endDrag();
            // Entity translate / scale commits via single packet on release. Both could theoretically be active
            // simultaneously (they aren't, since LMB capture is exclusive), but defensively endDrag both so we don't
            // leak ghost state if some future change introduces concurrent gestures.
            var entityTranslateResult = com.blib.engine.entityselection.EntityTranslateGizmo.endDrag();
            var entityScaleResult = com.blib.engine.entityselection.EntityScaleGizmo.endDrag();
            var mcInst = net.minecraft.client.Minecraft.getInstance();
            if (mcInst.player != null) {
                var dim = mcInst.player.level().dimension().location();
                if (entityTranslateResult != null) {
                    BLib.MOD.networking()
                        .sendToServer(
                            new com.blib.mod.common.network.packet.C2STranslateEntityPayload(
                                entityTranslateResult.entity().getId(),
                                entityTranslateResult.finalX(),
                                entityTranslateResult.finalY(),
                                entityTranslateResult.finalZ(),
                                dim
                            )
                        );
                }
                if (entityScaleResult != null) {
                    BLib.MOD.networking()
                        .sendToServer(
                            new com.blib.mod.common.network.packet.C2SSetEntityScalePayload(
                                entityScaleResult.entity().getId(),
                                entityScaleResult.newScale(),
                                dim
                            )
                        );
                }
            }
            // Move drag commits via packet on release if the user dragged a non-zero distance. The ghost stays
            // visible (moveOffset is preserved) until the server's reply lands, so the user has continuous feedback.
            var moveResult = MoveBlocksGizmo.endDrag();
            if (moveResult != null) {
                var a = BlockSelection.cornerA();
                var b = BlockSelection.cornerB();
                var mc = Minecraft.getInstance();
                if (a != null && b != null && mc.player != null) {
                    var dim = mc.player.level().dimension().location();
                    BLib.MOD.networking()
                        .sendToServer(
                            new C2SMoveSelectionPayload(
                                a,
                                b,
                                moveResult.offset().getX(),
                                moveResult.offset().getY(),
                                moveResult.offset().getZ(),
                                moveResult.copy(),
                                dim
                            )
                        );
                }
            }
            return true;
        }

        if (button == 2) {
            EngineNavigation.endOrbitDrag(session);
            this.mmbDrag = null;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!inRect(mouseX, mouseY)) {
            return false;
        }

        var session = EngineMode.get().session();
        if (session == null) {
            return false;
        }

        // Scroll always zooms — the previous scroll-cycles-rotation behavior conflicted with the dominant user need
        // (zooming) and trapped the user out of camera adjustment while a piece was held. Rotation is on the R key
        // (Keybindings.JIGSAW_ROTATE) for users who need it.
        if (!ActiveKeybindings.matchesScroll(Keybindings.VIEWPORT_ZOOM)) {
            return false;
        }
        EngineNavigation.applyZoomScroll(session, scrollY);
        return true;
    }

    private boolean inRect(double x, double y) {
        return x >= rectX && x < rectX + rectWidth && y >= rectY && y < rectY + rectHeight;
    }

    /**
     * Compute the marquee anchor for a Shift+LMB click. Extends from the previous single block or the prior volume's
     * cornerA so the user can "select between two clicks": click block A → A inspected, Shift+click block B → volume
     * from A to B. Subsequent Shift+clicks keep the anchor and replace the second corner. Returns {@code fallback}
     * (typically the just-clicked block) when there's no meaningful previous anchor — gives a fresh 1-block marquee
     * from which the user can drag.
     */
    private static BlockPos anchorForShiftExtend(@Nullable Selectable previous, @Nullable BlockPos previousCornerA, BlockPos fallback) {
        if (previous instanceof BlockSelectable bs) {
            return bs.pos();
        }
        if (previous instanceof BlockVolumeSelectable && previousCornerA != null) {
            return previousCornerA;
        }
        return fallback;
    }

    /** True when the cursor sits within {@link EngineWorkspaceScreen#DIVIDER_HIT_PX} of any viewport edge. */
    private boolean nearViewportEdge(double x, double y) {
        var hit = EngineWorkspaceScreen.DIVIDER_HIT_PX;
        return x < rectX + hit || x >= rectX + rectWidth - hit || y < rectY + hit || y >= rectY + rectHeight - hit;
    }

    /**
     * Ray-vs-AABB hit test using the cursor ray and the captured camera origin. Returns true if the ray intersects the
     * box at any positive parametric distance — including the case where the camera is inside the AABB, since the user
     * might want to right-click "into" their selection from inside it.
     */
    private static boolean rmbHitsAabb(com.blib.engine.session.EngineSession session, net.minecraft.world.phys.AABB aabb) {
        var rayDir = JigsawPlacementCursor.cursorRayDirection(session);
        if (rayDir == null) {
            return false;
        }
        var capturedCam = com.blib.engine.session.EngineCameraFrame.cameraPosition();
        var origin = capturedCam != null ? capturedCam : session.cameraPosition();
        return rayIntersectsAabb(origin.x, origin.y, origin.z, rayDir.x, rayDir.y, rayDir.z, aabb);
    }

    private static boolean rayIntersectsAabb(
        double ox,
        double oy,
        double oz,
        double dx,
        double dy,
        double dz,
        net.minecraft.world.phys.AABB aabb
    ) {
        var tMin = Double.NEGATIVE_INFINITY;
        var tMax = Double.POSITIVE_INFINITY;
        for (var i = 0; i < 3; i++) {
            var o = i == 0 ? ox : (i == 1 ? oy : oz);
            var d = i == 0 ? dx : (i == 1 ? dy : dz);
            var mn = i == 0 ? aabb.minX : (i == 1 ? aabb.minY : aabb.minZ);
            var mx = i == 0 ? aabb.maxX : (i == 1 ? aabb.maxY : aabb.maxZ);
            if (Math.abs(d) < 1.0e-9) {
                if (o < mn || o > mx) {
                    return false;
                }
                continue;
            }
            var t1 = (mn - o) / d;
            var t2 = (mx - o) / d;
            if (t1 > t2) {
                var s = t1;
                t1 = t2;
                t2 = s;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
                return false;
            }
        }
        return tMax >= 0;
    }

    /**
     * Latched modifier state for an in-flight MMB camera gesture. Captured once at press time by
     * {@link #mouseClickedCapture} and consulted on every {@link #mouseDragged} call until {@link #mouseReleased}
     * clears it. Latching (rather than live-sampling) means a stray Shift release mid-drag can't flip the gesture's
     * mode unexpectedly.
     */
    private record MmbDrag(
        boolean shift,
        boolean ctrl
    ) {}
}
