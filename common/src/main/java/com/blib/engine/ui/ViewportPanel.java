package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import com.blib.engine.jigsaw.JigsawPieceLibrary;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.jigsaw.JigsawPlacementCursor;
import com.blib.engine.jigsaw.placement.CollisionScanner;
import com.blib.engine.jigsaw.placement.JigsawPlacementOptions;
import com.blib.engine.jigsaw.placement.JigsawTool;
import com.blib.engine.jigsaw.placement.JigsawWorldRaycast;
import com.blib.engine.jigsaw.placement.PlacementContext;
import com.blib.engine.jigsaw.placement.PlacementMode;
import com.blib.engine.session.EngineMode;
import com.blib.engine.session.EngineNavigation;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SPlaceJigsawPiecePayload;
import com.blib.mod.common.network.packet.C2SUndoPlacementPayload;

/**
 * The viewport panel — the rect where the downsampled world+HUD blit lands. This panel doesn't draw anything itself; it
 * just records its inner rect during {@link #render} and routes mouse events to engine-mode camera control:
 * <ul>
 * <li>LMB click → entity selection (ray-cast through the cursor's relative position into the world).</li>
 * <li>LMB drag → orbit camera around the pivot computed at press time.</li>
 * <li>RMB click (no drag) → fire {@link RightClickHandler} with the ray-cast entity (if any) at cursor position.</li>
 * <li>RMB drag → screen-plane pan.</li>
 * <li>Scroll → multiplicative zoom toward / away from the pivot.</li>
 * </ul>
 * Mouse coords arrive in workspace logical pixels; deltas are converted to raw-pixel equivalent via the GUI scale so
 * the orbit / pan / zoom sensitivities feel the same regardless of MC's GUI scale setting.
 */
@ApiStatus.Internal
public final class ViewportPanel implements Panel {

    /**
     * Squared cursor-motion threshold in workspace logical pixels. RMB drags shorter than this — measured at release —
     * are treated as right-clicks (context menu); longer ones are pans.
     */
    private static final double RMB_CLICK_VS_DRAG_THRESHOLD_SQ = 16.0;

    /** Notified when the user right-clicks (without dragging) inside the viewport. */
    public interface RightClickHandler {

        void onRightClick(@Nullable LivingEntity entity, double cursorX, double cursorY);
    }

    private final String title;

    private final @Nullable RightClickHandler rightClickHandler;

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    private @Nullable RmbDrag rmbDrag;

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
            // When a jigsaw piece is selected, LMB in the viewport means "place" — short-circuit selection and orbit
            // so the user's click doesn't also drag the camera. The placement (anchor + rotation + mirror) comes
            // from the active resolver, not from raw cursor + selection state — for FREE mode the two are identical,
            // but later modes (jigsaw-snap) override rotation to align with a target jigsaw, and we want the click
            // to send what the user is actually seeing in the world preview.
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

            EngineNavigation.performSelectionAt(session, relX, relY);
            EngineNavigation.beginOrbitDrag(session, relX, relY);
            return true;
        }

        if (button == 1) {
            // Arm a potential right-click. Resolves to either a context-menu trigger (release without drag) or a
            // pan (release after dragging past threshold) in mouseDragged / mouseReleased.
            this.rmbDrag = new RmbDrag(mouseX, mouseY, false);
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

        // Convert workspace-logical deltas to raw-pixel deltas so orbit / pan sensitivity is independent of GUI scale.
        var guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        var rawDx = deltaX * guiScale;
        var rawDy = deltaY * guiScale;

        if (button == 0) {
            EngineNavigation.applyOrbitDelta(session, rawDx, rawDy);
            return true;
        }

        if (button == 1 && rmbDrag != null) {
            if (!rmbDrag.dragCommitted) {
                var dx = mouseX - rmbDrag.startX;
                var dy = mouseY - rmbDrag.startY;
                if (dx * dx + dy * dy > RMB_CLICK_VS_DRAG_THRESHOLD_SQ) {
                    rmbDrag = new RmbDrag(rmbDrag.startX, rmbDrag.startY, true);
                }
            }
            if (rmbDrag.dragCommitted) {
                EngineNavigation.applyPanDelta(session, rawDx, rawDy);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var session = EngineMode.get().session();
        if (session == null) {
            return false;
        }

        if (button == 0) {
            EngineNavigation.endOrbitDrag(session);
            return true;
        }

        if (button == 1 && rmbDrag != null) {
            var wasClick = !rmbDrag.dragCommitted;
            var clickX = rmbDrag.startX;
            var clickY = rmbDrag.startY;
            this.rmbDrag = null;

            if (wasClick) {
                // RMB-without-drag while a piece is selected = "undo last placement". This shadows the entity
                // context menu the rightClickHandler would otherwise open; that menu's only useful action
                // (delete entity) doesn't apply when the user is mid-placement, and they need a fast way to
                // unwind a misplaced piece without leaving the viewport. RMB-drag for camera pan is unchanged
                // (handled in mouseDragged via dragCommitted).
                if (JigsawPieceSelection.hasSelection()) {
                    BLib.MOD.networking().sendToServer(C2SUndoPlacementPayload.INSTANCE);
                } else if (rightClickHandler != null) {
                    var relX = (clickX - rectX) / (double) rectWidth;
                    var relY = (clickY - rectY) / (double) rectHeight;
                    EngineNavigation.performSelectionAt(session, relX, relY);
                    // Pull the selected entity (if any) out of the new SelectionManager — performSelectionAt
                    // routes through it now, and the right-click handler still wants the LivingEntity for its
                    // entity-specific context menu (delete, GOAP details, etc.).
                    var selection = com.blib.engine.selection.SelectionManager.current().single();
                    LivingEntity selectedEntity = null;
                    if (selection instanceof com.blib.engine.selection.EntitySelectable es) {
                        selectedEntity = es.entity();
                    }
                    rightClickHandler.onRightClick(selectedEntity, clickX, clickY);
                }
            }
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
        // for the full place-rotate-place loop. Camera zoom is still available via the standard MC scroll outside
        // the workspace; users can also deselect the piece (no clear-selection key yet — TODO Phase 2+) to get
        // zoom back inside the workspace.
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
     * Tracks an in-flight right-mouse-button gesture started by {@link #mouseClicked(double, double, int)}. A press
     * arms the gesture; subsequent {@link #mouseDragged} promotes it to a pan once the cursor passes the click-vs-drag
     * threshold; {@link #mouseReleased} either fires the right-click handler (if not committed to drag) or just clears
     * state.
     */
    private record RmbDrag(
        double startX,
        double startY,
        boolean dragCommitted
    ) {}
}
