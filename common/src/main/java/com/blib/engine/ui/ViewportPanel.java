package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import com.blib.engine.blockselection.BlockSelection;
import com.blib.engine.blockselection.BlockSelectionScaleGizmo;
import com.blib.engine.blockselection.BlockSelectionTranslateGizmo;
import com.blib.engine.blockselection.MoveBlocksGizmo;
import com.blib.engine.jigsaw.JigsawPieceLibrary;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.jigsaw.JigsawPlacementCursor;
import com.blib.engine.jigsaw.placement.CollisionScanner;
import com.blib.engine.jigsaw.placement.JigsawPlacementOptions;
import com.blib.engine.jigsaw.placement.JigsawTool;
import com.blib.engine.jigsaw.placement.JigsawWorldRaycast;
import com.blib.engine.jigsaw.placement.PlacementContext;
import com.blib.engine.jigsaw.placement.PlacementMode;
import com.blib.engine.selection.BlockVolumeSelectable;
import com.blib.engine.selection.EntitySelectable;
import com.blib.engine.selection.JigsawBlockSelectable;
import com.blib.engine.selection.SelectionManager;
import com.blib.engine.session.EngineMode;
import com.blib.engine.session.EngineNavigation;
import com.blib.engine.spawn.EntitySpawnSelection;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SMoveSelectionPayload;
import com.blib.mod.common.network.packet.C2SPlaceJigsawPiecePayload;
import com.blib.mod.common.network.packet.C2SSpawnEntityPayload;
import com.blib.mod.common.network.packet.C2SUndoPlacementPayload;

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
    }

    private final String title;

    private final @Nullable RightClickHandler rightClickHandler;

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    private @Nullable MmbDrag mmbDrag;

    /**
     * Active drag-to-pick anchor — the block clicked on LMB-press, used as cornerA throughout the drag while every
     * mouseDragged updates cornerB. {@code null} when no drag-pick is in flight. Cleared on mouseReleased.
     */
    private @Nullable BlockPos dragPickAnchor;

    public ViewportPanel(String title) {
        this(title, null);
    }

    public ViewportPanel(String title, @Nullable RightClickHandler rightClickHandler) {
        this.title = title;
        this.rightClickHandler = rightClickHandler;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;
        // No drawing: the compositor has already painted the downsampled world+HUD into this exact rect on the main
        // render target before the workspace's panels render. Anything drawn here would obscure the live game view.

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

        var session = EngineMode.get().session();
        if (session == null) {
            return false;
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
                        // Latch Alt at click time for copy-vs-cut. Live-sampling during drag would let a stray Alt
                        // release flip the operation mid-drag, which is jarring; latching keeps it stable.
                        MoveBlocksGizmo.beginDrag(moveHit.axis(), session, Screen.hasAltDown());
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
                        // BLOCK policy on. Frame state is a render-side cache; trusting it across the click event
                        // boundary would be one more invalidation rule to maintain.
                        var mc = net.minecraft.client.Minecraft.getInstance();
                        if (mc.level != null && JigsawPlacementOptions.collisionPolicy() == JigsawPlacementOptions.CollisionPolicy.BLOCK) {
                            var snapAnchor = JigsawTool.activeMode() == PlacementMode.JIGSAW_SNAP
                                ? JigsawWorldRaycast.raycastJigsaw(session)
                                : null;
                            var count = CollisionScanner.scan(mc.level, placement, template, snapAnchor);
                            if (count > 0) {
                                return true;
                            }
                        }

                        BLib.MOD.networking()
                            .sendToServer(
                                new C2SPlaceJigsawPiecePayload(
                                    selectedPieceId,
                                    placement.anchor(),
                                    placement.rotation().ordinal(),
                                    placement.mirror().ordinal()
                                )
                            );
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

            // Try entity / jigsaw-block selection first (existing path). If neither hit, fall back to drag-to-pick:
            // start a fresh block-volume selection at the clicked block. Subsequent mouseDragged events extend
            // cornerB; mouseReleased finalizes the drag.
            EngineNavigation.performSelectionAt(session, relX, relY);
            var selected = SelectionManager.current().single();
            if (selected instanceof EntitySelectable || selected instanceof JigsawBlockSelectable) {
                return true;
            }
            var blockHit = JigsawPlacementCursor.clipFromCursor(session);
            if (blockHit != null) {
                var clicked = blockHit.getBlockPos();
                BlockSelection.setCornersDirect(clicked, clicked);
                SelectionManager.selectSingle(new BlockVolumeSelectable());
                dragPickAnchor = clicked;
            }
            return true;
        }

        if (button == 1) {
            // RMB while a piece is selected = "undo last placement". Faster than clearing the piece, finding the
            // undo button, etc. — the user's right-hand stays on the mouse mid-iteration. Without a piece held, RMB
            // opens the entity context menu (route the ray-pick first so the menu sees the entity under the cursor).
            if (JigsawPieceSelection.hasSelection()) {
                BLib.MOD.networking().sendToServer(C2SUndoPlacementPayload.INSTANCE);
                return true;
            }

            if (rightClickHandler != null) {
                // AABB right-click takes priority over entity / jigsaw selection. Re-select the volume so the
                // inspector mirrors what the user is operating on, then open the volume context menu.
                var aabbOpt = BlockSelection.aabb();
                if (aabbOpt.isPresent() && rmbHitsAabb(session, aabbOpt.get())) {
                    SelectionManager.selectSingle(new BlockVolumeSelectable());
                    rightClickHandler.onRightClickVolume(mouseX, mouseY);
                    return true;
                }
                EngineNavigation.performSelectionAt(session, relX, relY);
                var selection = SelectionManager.current().single();
                LivingEntity selectedEntity = null;
                if (selection instanceof EntitySelectable es) {
                    selectedEntity = es.entity();
                }
                rightClickHandler.onRightClick(selectedEntity, mouseX, mouseY);
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

        if (button == 0 && dragPickAnchor != null) {
            dragPickAnchor = null;
            return true;
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

        // While a placement piece is selected, scroll cycles rotation instead of zooming. This is the dominant
        // pattern in authoring tools (Blender, Unreal placement mode, etc.) and keeps the user's hand on the mouse
        // for the full place-rotate-place loop. Camera zoom is still available via Ctrl+MMB drag, so scroll-rotate
        // doesn't trap the user out of zooming.
        if (JigsawPieceSelection.hasSelection()) {
            // Positive scrollY = scroll up = rotate clockwise (matches the convention from the world preview's
            // initial card rendering, where scroll-up is "rotate right").
            JigsawPieceSelection.cycleRotation(scrollY > 0 ? 1 : -1);
            return true;
        }

        EngineNavigation.applyZoomScroll(session, scrollY);
        return true;
    }

    private boolean inRect(double x, double y) {
        return x >= rectX && x < rectX + rectWidth && y >= rectY && y < rectY + rectHeight;
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
