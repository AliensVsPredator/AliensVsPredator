package com.blib.engine.layout;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import com.blib.engine.ui.dock.DockNode;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.dock.TabbedPanel;

/**
 * Pure conversion between a live {@link DockNode} body subtree and its serialized {@link BodyNode} form. The body
 * subtree is the tree below the workspace's outer trim — i.e. what {@code EngineWorkspaceScreen.buildBody} produces.
 * <p>
 * {@link #capture} expects every leaf to wrap a {@link TabbedPanel}; that's the body invariant. Tabs whose panel class
 * isn't registered with {@link PanelRegistry} are silently dropped from the captured form (logged at WARN once per
 * occurrence) — losing them is preferable to refusing to save the rest of the layout because of one rogue panel type.
 * <p>
 * {@link #hydrate} is the inverse: each {@link BodyNode.Leaf} becomes a {@code Leaf(new TabbedPanel(...))} and unknown
 * ids are skipped with a log warning. {@link BodyNode.Leaf#activeIndex} is clamped to the resulting tab count, so a
 * layout written with three tabs but loaded with two (unknown panel removed in between) doesn't blow up on display.
 */
@ApiStatus.Internal
public final class LayoutSnapshot {

    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutSnapshot.class);

    private LayoutSnapshot() {}

    public static BodyNode capture(DockNode bodyRoot) {
        return switch (bodyRoot) {
            case DockNode.Leaf leaf -> captureLeaf(leaf);
            case DockNode.Split split -> new BodyNode.Split(
                split.orientation().name(),
                capture(split.first()),
                capture(split.second()),
                SizingDoc.of(split.sizing())
            );
        };
    }

    private static BodyNode.Leaf captureLeaf(DockNode.Leaf leaf) {
        if (!(leaf.panel() instanceof TabbedPanel tabbed)) {
            // Body invariant violated — non-trim leaves are always TabbedPanels in this codebase. Capturing as an
            // empty leaf is safer than throwing here; on the next reopen the slot will simply be empty and the user
            // can repopulate via the Window menu.
            LOGGER.warn(
                "[BLib] LayoutSnapshot.capture: unexpected non-tabbed leaf {}; capturing as empty",
                leaf.panel().getClass().getSimpleName()
            );
            return new BodyNode.Leaf(java.util.List.of(), 0);
        }
        var ids = new ArrayList<String>(tabbed.tabCount());
        for (var p : tabbed.tabs()) {
            var id = PanelRegistry.idOf(p);
            if (id != null) {
                ids.add(id);
            } else {
                LOGGER.warn("[BLib] LayoutSnapshot.capture: dropping unregistered panel {}", p.getClass().getSimpleName());
            }
        }
        var clampedActive = ids.isEmpty() ? 0 : Math.max(0, Math.min(tabbed.activeIndex(), ids.size() - 1));
        return new BodyNode.Leaf(ids, clampedActive);
    }

    public static DockNode hydrate(BodyNode body, PanelRegistry.Context ctx) {
        return switch (body) {
            case BodyNode.Leaf leaf -> hydrateLeaf(leaf, ctx);
            case BodyNode.Split split -> new DockNode.Split(
                BodyNode.parseOrientation(split.orientation()),
                hydrate(split.first(), ctx),
                hydrate(split.second(), ctx),
                split.sizing().toMutable()
            );
        };
    }

    private static DockNode.Leaf hydrateLeaf(BodyNode.Leaf leaf, PanelRegistry.Context ctx) {
        var panels = new ArrayList<Panel>(leaf.tabs().size());
        for (var id : leaf.tabs()) {
            var panel = PanelRegistry.create(id, ctx);
            if (panel != null) {
                panels.add(panel);
            } else {
                LOGGER.warn("[BLib] LayoutSnapshot.hydrate: unknown panel id '{}'; skipping", id);
            }
        }
        var tabbed = new TabbedPanel(panels.toArray(new Panel[0]));
        if (!panels.isEmpty()) {
            var clamped = Math.max(0, Math.min(leaf.activeIndex(), panels.size() - 1));
            tabbed.setActiveIndex(clamped);
        }
        return new DockNode.Leaf(tabbed);
    }
}
