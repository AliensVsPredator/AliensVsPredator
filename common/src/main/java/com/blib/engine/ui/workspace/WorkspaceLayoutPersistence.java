package com.blib.engine.ui.workspace;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;

import com.blib.engine.layout.ActiveLayoutState;
import com.blib.engine.layout.LayoutCatalog;
import com.blib.engine.layout.LayoutDoc;
import com.blib.engine.layout.LayoutSnapshot;
import com.blib.engine.layout.LayoutTemplate;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.ui.dock.DockNode;
import com.blib.engine.ui.dock.Sizing;
import com.blib.engine.ui.panel.chrome.MenuBarPanel;
import com.blib.engine.ui.panel.chrome.StatusBarPanel;

/**
 * Workspace layout persistence: capture the current dock tree to {@code <gameDir>/blib/engine/layouts/} and update
 * {@code state.json} with the active layout id. Extracted from {@code EngineWorkspaceScreen} so the screen no longer
 * owns disk-IO logic; the screen calls into this class with its current root + active id.
 * <p>
 * Step 5 of the engine architecture refactor isolates the persistence concern. Future passes can pull dialog
 * coordination, panel construction, and input dispatch into sibling classes following the same pattern, leaving the
 * screen with pure render orchestration.
 */
@ApiStatus.Internal
public final class WorkspaceLayoutPersistence {

    private WorkspaceLayoutPersistence() {}

    /**
     * Capture the dock tree (with trim wrappers peeled) and write it to the layout catalog under
     * {@code activeLayoutId}. Returns {@code true} when a file was successfully written, {@code false} when the save
     * couldn't be performed (so callers can decide whether to retry).
     * <p>
     * When the disk file is missing or unparseable but the id matches a built-in template, we reconstruct a fresh
     * {@link LayoutDoc} from the template's metadata (preserving the in-memory body) rather than silently no-op'ing.
     * Without this fallback, a deleted or corrupt template-file caused every subsequent save attempt for that id to
     * skip — the in-memory customizations would never reach disk, and the next session would load the template default.
     * This was the modeler-specific persistence failure: if {@code modeler.json} ever ended up missing (corrupted seed,
     * manual deletion, etc.), every save attempt silently no-op'd while the default-layout save kept working because
     * {@code default.json} was intact.
     */
    public static boolean persistOutgoingLayout(DockNode root, String activeLayoutId) {
        var capturedBody = LayoutSnapshot.capture(extractBodyRoot(root));
        var existing = LayoutCatalog.get(activeLayoutId);
        LayoutDoc doc;
        if (existing != null) {
            doc = existing.withBody(capturedBody);
        } else {
            var template = LayoutTemplate.byId(activeLayoutId);
            if (template == null) {
                // A user layout whose disk file vanished — we have no displayName / createdAt history to reconstruct
                // from. Skip rather than guess; the caller can re-create via Save As if they want.
                return false;
            }
            var now = Instant.now().toString();
            doc = new LayoutDoc(LayoutDoc.CURRENT_VERSION, activeLayoutId, template.displayName(), null, now, now, capturedBody);
        }
        try {
            LayoutCatalog.save(doc);
            return true;
        } catch (IOException e) {
            LoggerFactory.getLogger(WorkspaceLayoutPersistence.class)
                .warn("[BLib] persistOutgoingLayout: failed to save '{}'", activeLayoutId, e);
            return false;
        }
    }

    /**
     * Update {@code state.json} with the active layout id, scoped to the active project if there is one. The screen
     * calls this from {@code switchLayout} and {@code removed()} so per-project active-layout memory survives game
     * restarts.
     */
    public static void persistActiveSelection(String activeLayoutId) {
        var state = ActiveLayoutState.read();
        var projectName = ProjectSession.activeProject() != null ? ProjectSession.activeProjectName() : null;
        var newState = projectName != null && !projectName.isEmpty()
            ? state.withProjectActive(projectName, activeLayoutId)
            : state.withGlobalActive(activeLayoutId);
        ActiveLayoutState.write(newState);
    }

    /**
     * Walk past the trim wrappers built by the workspace's outer-layout construction to reach the body subtree. The
     * expected shape is {@code Split(V, Leaf(MenuBar), Split(V, body, Leaf(StatusBar)))} with the trim splits pinned to
     * {@link MenuBarPanel#HEIGHT} / {@link StatusBarPanel#HEIGHT}.
     * <p>
     * The peel runs in a loop so layout files that were previously double-wrapped — by an older build whose extractor
     * didn't recognize the trim shape and serialized the full tree as the "body" — heal themselves on the next save
     * (each surviving wrapper layer gets stripped). Without the loop, double-wrapped files would keep accreting a layer
     * per layout switch.
     */
    public static DockNode extractBodyRoot(DockNode root) {
        // Match by SIZING rather than leaf-panel type: a freshly built tree has a real MenuBarPanel / StatusBarPanel
        // leaf, but after a save / load round-trip those trim leaves become empty TabbedPanels (LayoutSnapshot has no
        // id for non-tabbed panels). The sizing pins are stable across both forms.
        var current = root;
        while (
            current instanceof DockNode.Split outer
                && outer.first() instanceof DockNode.Leaf
                && outer.sizing() instanceof Sizing.FirstFixed menuSizing
                && menuSizing.pixels == MenuBarPanel.HEIGHT
                && outer.second() instanceof DockNode.Split bodyAndStatus
                && bodyAndStatus.second() instanceof DockNode.Leaf
                && bodyAndStatus.sizing() instanceof Sizing.SecondFixed statusSizing
                && statusSizing.pixels == StatusBarPanel.HEIGHT
        ) {
            current = bodyAndStatus.first();
        }
        return current;
    }
}
