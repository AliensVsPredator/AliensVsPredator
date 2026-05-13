package com.blib.engine.ui.popup;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.panel.chrome.ModelerMenuBar;
import com.blib.engine.ui.widget.DropdownMenu;

/**
 * Bridge that lets a body panel open a screen-level {@link DropdownMenu} overlay. The workspace owns the active menu
 * rendering and click-absorber state, so panel-local menu bars (e.g. {@link ModelerMenuBar}) request the workspace to
 * host their dropdown rather than duplicating the overlay plumbing inside the panel.
 * <p>
 * Wired via {@link com.blib.engine.layout.PanelRegistry.Context} so panels declare the dependency at construction and
 * stay loosely coupled to the screen instance.
 */
@ApiStatus.Internal
@FunctionalInterface
public interface PanelMenuOpener {

    /** Replace the workspace's currently-open dropdown with {@code menu}. Pass a freshly-built menu each click. */
    void open(DropdownMenu menu);
}
