package com.blib.engine.layout;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import com.blib.engine.ui.dock.Orientation;

/**
 * Immutable, serializable mirror of {@link com.blib.engine.ui.dock.DockNode} for the editable body subtree of a layout.
 * The outer trim (menu / toolbar / status bars) is always rebuilt by {@code EngineWorkspaceScreen.buildOuterLayout} on
 * load and never appears here.
 * <p>
 * Body invariant: every leaf in the live tree wraps a {@link com.blib.engine.ui.dock.TabbedPanel}, so {@link Leaf}
 * stores a tab-id list rather than a single panel id. A non-tabbed leaf doesn't exist in the body — single-panel state
 * is a {@link Leaf} with one entry in {@link Leaf#tabs}.
 * <p>
 * {@link Split#orientation} is stored as a string ({@code "HORIZONTAL"} / {@code "VERTICAL"}) for forward-compatibility
 * and hand-editability of the JSON; the runtime conversion to {@link Orientation} happens during hydrate.
 */
@ApiStatus.Internal
public sealed interface BodyNode {

    /**
     * A tab container leaf. {@code tabs} are panel ids resolved against {@link PanelRegistry}; unknown ids are dropped
     * during hydrate. {@code activeIndex} is clamped to the tab count after dropping unknowns.
     */
    record Leaf(
        List<String> tabs,
        int activeIndex
    ) implements BodyNode {

        public Leaf {
            tabs = tabs == null ? List.of() : List.copyOf(tabs);
        }
    }

    /**
     * Binary split between {@code first} and {@code second} along {@code orientation}, with the boundary placed by
     * {@code sizing}. The string-typed orientation is parsed via {@link #parseOrientation} on hydrate.
     */
    record Split(
        String orientation,
        BodyNode first,
        BodyNode second,
        SizingDoc sizing
    ) implements BodyNode {}

    /**
     * Parse an orientation string back to the runtime enum. Defensive against unknown values (treats anything other
     * than {@code "VERTICAL"} as horizontal) so a hand-edited or future-version layout doesn't crash hydrate.
     */
    static Orientation parseOrientation(String s) {
        return "VERTICAL".equalsIgnoreCase(s) ? Orientation.VERTICAL : Orientation.HORIZONTAL;
    }
}
