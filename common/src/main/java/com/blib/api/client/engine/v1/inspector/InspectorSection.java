package com.blib.api.client.engine.v1.inspector;

import net.minecraft.client.gui.GuiGraphics;

/**
 * One contributed view inside the engine workspace's right-side Inspector panel. Implementations declare which
 * selection type they render against via {@link #selectableType()} (e.g.
 * {@link com.blib.api.client.engine.v1.selection.FactionSelection}) and the engine routes selections to all matching
 * sections in registration order. Built-in BLib sections render first; downstream sections chain after at the y the
 * built-in returned, so a contributed faction inspector appears below the stock identity/territory/protection rows.
 *
 * @param <S> the selection type this section handles
 */
public interface InspectorSection<S> {

    /** Stable identifier; used in layout persistence and debug logs. Namespace it with your mod id. */
    String id();

    /**
     * The selection type this section handles. The engine matches via {@code selectableType().isInstance(selection)} so
     * a section can target a concrete public interface like {@code FactionSelection.class}.
     */
    Class<S> selectableType();

    /** Render order among contributed sections sharing a selection type. Lower renders earlier. */
    default int order() {
        return 0;
    }

    /**
     * Render the section. {@code y} is the top of this section's row band in inspector-content coordinates; return the
     * y the next section should start at (typically {@code y + actualHeightDrawn}). Returning {@code y} unchanged is a
     * no-op (e.g. when this section's typeId filter doesn't match the current selection).
     */
    int render(GuiGraphics graphics, int x, int y, int width, S target, int mouseX, int mouseY);

    /** Handle a click within this section's last-rendered band. Returns true if consumed. */
    default boolean mouseClicked(double mouseX, double mouseY, int button, S target) {
        return false;
    }
}
