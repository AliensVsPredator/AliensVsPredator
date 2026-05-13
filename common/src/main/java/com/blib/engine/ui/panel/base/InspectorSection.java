package com.blib.engine.ui.panel.base;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.picking.Selectable;

/**
 * One typed view inside the engine's details/inspector panel. Replaces the prior monolithic switch over selectable
 * types in {@code DetailsPanel} (which had ~104 instance fields covering 7+ inspection domains in a single flat
 * namespace) with a registry of self-contained sections that activate only when their type matches the current
 * selection.
 * <p>
 * Each section is responsible for its own state: cached display data, hit-test rects, expanded/collapsed regions. That
 * state never coexists with state from other sections — the inspector panel only instantiates / shows the section whose
 * {@link #selectableType()} matches the current selection, so stale fields from a previous selection type can no longer
 * cause ghost-clicks (which used to require manually zeroing rect fields like {@code factionSwatchSize = 0} at the
 * start of every render).
 *
 * @param <S> the {@link Selectable} subtype this section inspects (or {@link Selectable} for tool-state sections that
 *            activate regardless of selection)
 */
@ApiStatus.Internal
public interface InspectorSection<S extends Selectable> {

    /** Stable id used to look up the section in the registry; also used for layout persistence. */
    String id();

    /** The selectable subtype this section handles. Used by the panel to route which sections to render. */
    Class<S> selectableType();

    /**
     * Render order among sections that match the same selection. Lower values render higher. Tool state sections that
     * apply to every selection (gizmo mode, collision policy, jigsaw cursor) sit at the top with negative orders.
     */
    default int order() {
        return 0;
    }

    /**
     * Render the section's content. {@code y} is the top of the section's row in the inspector's content coordinate
     * space; the section returns the y-offset consumed (so the inspector knows where to place the next section). The
     * mouse coords are already pre-translated for the current scroll offset.
     */
    int render(GuiGraphics graphics, int x, int y, int width, S target, int mouseX, int mouseY);

    /**
     * Handle a mouse click. {@code mouseX}/{@code mouseY} are in the same content coordinate space as the last render.
     * Returns true if consumed.
     */
    default boolean mouseClicked(double mouseX, double mouseY, int button, S target) {
        return false;
    }

    /** Handle a keyboard event. Returns true if consumed. */
    default boolean keyPressed(int keyCode, int scanCode, int modifiers, S target) {
        return false;
    }
}
