package com.blib.engine.ui.dock;

import org.jetbrains.annotations.ApiStatus;

/**
 * Opt-in input hooks for {@link Panel} implementations that need to react to mouse / keyboard events. Split from
 * {@link Panel} so a panel that is purely a render surface (e.g. a status bar with no clickable elements) doesn't have
 * to consider 6 input methods just to opt out of all of them.
 * <p>
 * {@link Panel} extends this interface, so every panel today already inherits the no-op defaults — existing
 * implementations keep working unchanged. Future panels that need no input can be re-typed against a hypothetical
 * {@code RenderOnlyPanel} (or simply not override any of these methods); future panels that <em>are</em> input-only
 * (rare) can implement {@code PanelInput} without {@code Panel}.
 */
@ApiStatus.Internal
public interface PanelInput {

    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Pre-structural-hit-test click hook. Runs before the workspace's divider / tab-strip / menu-bar checks so panels
     * can claim clicks on small edge UI (scrollbar thumbs, close buttons, resize handles) that would otherwise be eaten
     * by divider drag at panel boundaries.
     * <p>
     * Returning {@code true} also <strong>captures the mouse</strong>: the screen routes the next {@link #mouseDragged}
     * / {@link #mouseReleased} to this panel regardless of cursor position, so a panel-driven drag (e.g. dragging the
     * scroll thumb) keeps tracking even if the cursor leaves the panel rect.
     * <p>
     * Default returns {@code false} — most panels have no edge UI and don't need capture. Override only for clicks on
     * specific small UI elements; general panel content should still fall through to {@link #mouseClicked}, which runs
     * after divider hit-testing.
     */
    default boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
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
