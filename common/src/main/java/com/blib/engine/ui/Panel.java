package com.blib.engine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A single docked region of an {@link EngineWorkspaceScreen}. The workspace's dock tree resolves panel rectangles each
 * frame; the panel is responsible for filling the inner content area passed to {@link #render}.
 * <p>
 * Coordinates are in screen pixels. The {@code (x, y)} passed to {@link #render} is the top-left of the panel's content
 * area, after {@link PanelChrome} has reserved space for the title bar and border.
 */
@ApiStatus.Internal
public interface Panel {

    String title();

    /**
     * Whether this panel should be rendered with a title bar + border via {@link PanelChrome}. Editor trim like menu
     * bars and status bars return {@code false} to render full-bleed; primary content panels return {@code true}.
     */
    default boolean hasChrome() {
        return true;
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
     * Returns the tooltip text to display for the cursor's last-rendered position, or {@code null} if no tooltip
     * applies. Panels compute and cache this during {@link #render} (which already receives mouse coordinates), and the
     * workspace queries it after the render pass to draw a tooltip near the cursor.
     */
    default @Nullable Component tooltipText() {
        return null;
    }

    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    default boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }

    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
}
