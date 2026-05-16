package com.blib.engine.ui.dock;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A single docked region of an {@link com.blib.engine.ui.EngineWorkspaceScreen}. The workspace's dock tree resolves
 * panel rectangles each frame; the panel is responsible for filling the inner content area passed to {@link #render}.
 * <p>
 * Coordinates are in screen pixels. The {@code (x, y)} passed to {@link #render} is the top-left of the panel's content
 * area, after {@link PanelChrome} has reserved space for the title bar and border.
 */
@ApiStatus.Internal
public interface Panel extends PanelInput {

    String title();

    /**
     * Whether this panel should be rendered with a title bar + border via {@link PanelChrome}. Editor trim like menu
     * bars and status bars return {@code false} to render full-bleed; {@link TabbedPanel} also returns {@code false}
     * because it draws its own tab strip in place of standard chrome. Primary content panels return {@code true}.
     */
    default boolean hasChrome() {
        return true;
    }

    /**
     * Whether this panel is immutable editor trim (menu bar, status bar) whose surrounding split should never be
     * user-resizable. Distinct from {@link #hasChrome()}: a {@link TabbedPanel} also lacks chrome but is <em>not</em>
     * trim — its surrounding splits must stay draggable so docked panels can be resized against each other.
     */
    default boolean isTrim() {
        return false;
    }

    void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick);

    /**
     * Called when this panel becomes the active tab in its containing {@link TabbedPanel} after previously not being
     * active (or on first show). Panels that maintain transient state — scroll positions, expanded sections, etc. — use
     * this to reset to a "freshly-opened" view, since the user likely expects content from the top after navigating
     * away and back.
     */
    default void onShown() {}

    /**
     * Reset transient panel UI state on request from container chrome, such as the tab context menu. The default
     * mirrors {@link #onShown()} because most existing panels already put scroll/filter refresh behavior there.
     * Panels with heavier reset semantics can override this without changing tab-menu plumbing.
     */
    default void resetPanel() {
        onShown();
    }

    /**
     * Returns the tooltip text to display for the cursor's last-rendered position, or {@code null} if no tooltip
     * applies. Panels compute and cache this during {@link #render} (which already receives mouse coordinates), and the
     * workspace queries it after the render pass to draw a tooltip near the cursor.
     */
    default @Nullable Component tooltipText() {
        return null;
    }

    /**
     * Optional small status marker rendered in this panel's tab. Intended for state the user should notice while the
     * panel is tabbed away, such as unsaved edits or background work.
     */
    default @Nullable TabIndicator tabIndicator() {
        return null;
    }

    record TabIndicator(
        int color,
        @Nullable Component tooltip
    ) {}

}
