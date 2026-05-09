package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
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
 * Modifier state for the MMB gesture is latched at press time, not live-sampled, so a Shift release mid-drag won't
 * flip orbit→pan unexpectedly. Mouse coords arrive in workspace logical pixels; deltas are converted to raw-pixel
 * equivalent via the GUI scale so orbit / pan / dolly sensitivities feel the same regardless of MC's GUI scale.
 */
@ApiStatus.Internal
public final class ViewportPanel implements Panel {

    /** Notified when the user right-clicks (without a held piece) inside the viewport. */
    public interface RightClickHandler {

        void onRightClick(@Nullable LivingEntity entity, double cursorX, double cursorY);
    }

    private final String title;

    private final @Nullable RightClickHandler rightClickHandler;

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    private @Nullable MmbDrag mmbDrag;

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
     * Capture MMB presses ahead of normal click dispatch so the workspace routes the entire MMB drag — including
     * frames where the cursor leaves the viewport rect — back to this panel. Without capture, fast pans that move
     * the cursor off the rect would have their drags routed to whatever panel the cursor happens to land on, which
     * either dies on the floor or yanks UI scrollbars unexpectedly. LMB / RMB stay on the standard click path.
     */
    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        if (button != 2 || !inRect(mouseX, mouseY)) {
            return false;
        }

        var session = EngineMode.get().session();
        if (session == null) {
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

            EngineNavigation.performSelectionAt(session, relX, relY);
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
                EngineNavigation.performSelectionAt(session, relX, relY);
                var selection = com.blib.engine.selection.SelectionManager.current().single();
                LivingEntity selectedEntity = null;
                if (selection instanceof com.blib.engine.selection.EntitySelectable es) {
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
