package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.session.EngineMode;
import com.blib.engine.session.EngineNavigation;

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

            if (wasClick && rightClickHandler != null) {
                var relX = (clickX - rectX) / (double) rectWidth;
                var relY = (clickY - rectY) / (double) rectHeight;
                EngineNavigation.performSelectionAt(session, relX, relY);
                rightClickHandler.onRightClick(session.selectedEntity(), clickX, clickY);
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
