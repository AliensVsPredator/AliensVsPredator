package com.blib.engine.ui.workspace;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

import com.blib.engine.layout.ActiveLayoutState;
import com.blib.engine.layout.BodyNode;
import com.blib.engine.layout.LayoutCatalog;
import com.blib.engine.layout.LayoutDoc;
import com.blib.engine.layout.LayoutSnapshot;
import com.blib.engine.layout.LayoutTemplate;
import com.blib.engine.layout.PanelRegistry;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.ui.EngineWorkspaceScreen;
import com.blib.engine.ui.dock.DockNode;
import com.blib.engine.ui.dock.Orientation;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.dock.Sizing;
import com.blib.engine.ui.dock.TabbedPanel;
import com.blib.engine.ui.panel.chrome.MenuBarPanel;
import com.blib.engine.ui.panel.chrome.StatusBarPanel;
import com.blib.engine.ui.panel.details.ModelerInspectorPanel;
import com.blib.engine.ui.panel.outliner.ModelerOutlinerPanel;
import com.blib.engine.ui.panel.viewport.ModelerViewportPanel;

/**
 * In-memory workspace layout mutations + the active-layout-id pointer. Disk I/O still lives in
 * {@link WorkspaceLayoutPersistence}; this controller owns the side that doesn't touch the filesystem:
 * <ul>
 * <li>Which layout id is active across screen re-opens within a JVM session.</li>
 * <li>Loading a body subtree from disk, then wrapping it in the standard menu / status trim.</li>
 * <li>Switching to a different layout id (persisting outgoing + activating incoming).</li>
 * <li>Reset / Reopen-Panel actions that mutate the dock root.</li>
 * <li>Modeler-panel introspection helpers used by Delete-key routing and the action-stack panel.</li>
 * </ul>
 * The screen passes its current root + a {@link PanelRegistry.Context} into each call and receives back the
 * post-mutation root, so the controller doesn't reach into screen state.
 */
@ApiStatus.Internal
public final class WorkspaceLayoutController {

    /**
     * Resolved id of the layout currently shown in the workspace. Sticks across screen re-opens within a JVM session
     * and is persisted to {@code <gameDir>/blib/engine/state.json} on close so subsequent game sessions reopen to the
     * same layout.
     */
    private static String activeLayoutId = LayoutTemplate.DEFAULT.id();

    private WorkspaceLayoutController() {}

    public static String activeLayoutId() {
        return activeLayoutId;
    }

    public static void setActiveLayoutId(String id) {
        activeLayoutId = id;
    }

    /**
     * Load the body subtree for the resolved active layout, falling back to the default template if its file is
     * missing. Updates {@link #activeLayoutId} as a side effect to match the resolution.
     */
    public static DockNode loadActiveLayoutBody(PanelRegistry.Context ctx) {
        var state = ActiveLayoutState.read();
        var projectName = ProjectSession.activeProject() != null ? ProjectSession.activeProjectName() : null;
        var resolvedId = ActiveLayoutState.resolve(projectName, state);
        activeLayoutId = resolvedId;

        var doc = LayoutCatalog.get(resolvedId);
        if (doc == null) {
            activeLayoutId = LayoutTemplate.DEFAULT.id();
            doc = LayoutTemplate.DEFAULT.toDoc();
        }
        return LayoutSnapshot.hydrate(doc.body(), ctx);
    }

    /**
     * Wrap {@code workspaceBody} (the central editable area) in the standard menu-bar / status-bar trim shared by every
     * layout. Splits are pinned to the trim panels' fixed heights so the body fills the remaining space.
     */
    public static DockNode buildOuterLayout(DockNode workspaceBody) {
        var bodyAndStatus = new DockNode.Split(
            Orientation.VERTICAL,
            workspaceBody,
            new DockNode.Leaf(new StatusBarPanel()),
            new Sizing.SecondFixed(StatusBarPanel.HEIGHT)
        );
        return new DockNode.Split(
            Orientation.VERTICAL,
            new DockNode.Leaf(new MenuBarPanel()),
            bodyAndStatus,
            new Sizing.FirstFixed(MenuBarPanel.HEIGHT)
        );
    }

    /**
     * Switch the active layout to {@code newLayoutId}. Persists the outgoing layout's body to disk before switching so
     * any in-session customisations carry over to the next reopen, then loads the incoming layout (falling back to the
     * default template if its file has been deleted in the meantime).
     *
     * @return the new full dock root if the switch happened, or {@code null} if {@code newLayoutId} was already active.
     */
    public static @Nullable DockNode switchLayout(String newLayoutId, DockNode currentRoot, PanelRegistry.Context ctx) {
        if (newLayoutId.equals(activeLayoutId)) {
            return null;
        }
        WorkspaceLayoutPersistence.persistOutgoingLayout(currentRoot, activeLayoutId);
        activeLayoutId = newLayoutId;
        var doc = LayoutCatalog.get(newLayoutId);
        if (doc == null) {
            activeLayoutId = LayoutTemplate.DEFAULT.id();
            doc = LayoutTemplate.DEFAULT.toDoc();
        }
        var bodyRoot = LayoutSnapshot.hydrate(doc.body(), ctx);
        var newRoot = buildOuterLayout(bodyRoot);
        WorkspaceLayoutPersistence.persistActiveSelection(activeLayoutId);
        return newRoot;
    }

    /**
     * Reset semantics:
     * <ul>
     * <li>Built-in template id → rebuild from the canonical code-baked {@link LayoutTemplate#toDoc}.</li>
     * <li>User layout with a {@code templateBase} → rebuild from that template's body but keep the user's id / display
     * name.</li>
     * <li>User layout with no {@code templateBase} → reload from disk (discards in-memory edits since last save).</li>
     * </ul>
     * The reset is then persisted so the user's choice is durable. Returns {@code null} when there's nothing on disk to
     * reload from (caller should no-op).
     */
    public static @Nullable DockNode resetLayout(DockNode currentRoot, PanelRegistry.Context ctx) {
        var current = LayoutCatalog.get(activeLayoutId);
        BodyNodeAndDoc next = computeResetBody(current);
        if (next == null) {
            return null;
        }
        var bodyRoot = LayoutSnapshot.hydrate(next.body, ctx);
        var newRoot = buildOuterLayout(bodyRoot);
        try {
            LayoutCatalog.save(next.docToWrite);
        } catch (java.io.IOException e) {
            org.slf4j.LoggerFactory.getLogger(WorkspaceLayoutController.class)
                .warn("[BLib] resetLayout: failed to persist reset", e);
        }
        return newRoot;
    }

    /**
     * If a panel of {@code panelClass} already exists somewhere in the dock tree, switch its containing
     * {@link TabbedPanel} to that tab and return. Otherwise create a fresh instance via {@code factory} and append it
     * as a tab in the first {@link TabbedPanel} found via depth-first walk. If no {@link TabbedPanel} exists at all
     * (the user has closed everything), the action is a no-op and the user can use Reset Layout to recover.
     */
    public static void reopenPanel(DockNode root, Class<? extends Panel> panelClass, Supplier<Panel> factory) {
        var existingOwner = findOwnerWithPanelOfType(root, panelClass);
        if (existingOwner != null) {
            for (var i = 0; i < existingOwner.tabCount(); i++) {
                if (panelClass.isInstance(existingOwner.tabs().get(i))) {
                    existingOwner.setActiveIndex(i);
                    return;
                }
            }
        }
        var first = findFirstTabbedPanel(root);
        if (first != null) {
            first.insertTab(first.tabCount(), factory.get());
        }
    }

    /**
     * True when the given dock root contains at least one modeler panel (viewport / outliner / inspector). Used by
     * Delete-key routing — modeler scene state is global, but Delete should only dispatch to it when the user's
     * actually looking at modeler UI.
     */
    public static boolean hasModelerPanel(DockNode root) {
        return panelTreeContainsModeler(root);
    }

    /**
     * Convenience for callers without a workspace reference (e.g. the action-stack panel). True when the active screen
     * is an engine workspace whose layout has a modeler panel.
     */
    public static boolean activeLayoutHasModelerPanel() {
        var mc = Minecraft.getInstance();
        return mc.screen instanceof EngineWorkspaceScreen ws && ws.layoutHasModelerPanel();
    }

    private static boolean panelTreeContainsModeler(DockNode node) {
        return switch (node) {
            case DockNode.Leaf leaf -> panelOrTabsContainsModeler(leaf.panel());
            case DockNode.Split split -> panelTreeContainsModeler(split.first()) || panelTreeContainsModeler(split.second());
        };
    }

    private static boolean panelOrTabsContainsModeler(Panel panel) {
        if (isModelerPanel(panel)) {
            return true;
        }
        // TabbedPanel hosts a list of swappable child panels. Walk them so a layout with a modeler tab next to other
        // tabs in the same panel still counts as "has modeler" — the user can switch tabs without changing layouts.
        if (panel instanceof TabbedPanel tp) {
            for (var tab : tp.tabs()) {
                if (isModelerPanel(tab)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isModelerPanel(Panel panel) {
        return panel instanceof ModelerOutlinerPanel
            || panel instanceof ModelerViewportPanel
            || panel instanceof ModelerInspectorPanel;
    }

    private static @Nullable TabbedPanel findFirstTabbedPanel(DockNode node) {
        if (node instanceof DockNode.Leaf leaf && leaf.panel() instanceof TabbedPanel tp) {
            return tp;
        }
        if (node instanceof DockNode.Split split) {
            var f = findFirstTabbedPanel(split.first());
            if (f != null) {
                return f;
            }
            return findFirstTabbedPanel(split.second());
        }
        return null;
    }

    private static @Nullable TabbedPanel findOwnerWithPanelOfType(DockNode node, Class<? extends Panel> panelClass) {
        if (node instanceof DockNode.Leaf leaf && leaf.panel() instanceof TabbedPanel tp) {
            for (var p : tp.tabs()) {
                if (panelClass.isInstance(p)) {
                    return tp;
                }
            }
            return null;
        }
        if (node instanceof DockNode.Split split) {
            var f = findOwnerWithPanelOfType(split.first(), panelClass);
            if (f != null) {
                return f;
            }
            return findOwnerWithPanelOfType(split.second(), panelClass);
        }
        return null;
    }

    private static @Nullable BodyNodeAndDoc computeResetBody(@Nullable LayoutDoc current) {
        if (LayoutCatalog.isTemplateId(activeLayoutId)) {
            var template = LayoutTemplate.byId(activeLayoutId);
            // Defensive null check — isTemplateId is true iff byId is non-null.
            assert template != null;
            var templateDoc = template.toDoc();
            return new BodyNodeAndDoc(templateDoc.body(), templateDoc);
        }
        if (current != null && current.templateBase() != null) {
            var template = LayoutTemplate.byId(current.templateBase());
            if (template != null) {
                var templateBody = template.toDoc().body();
                return new BodyNodeAndDoc(templateBody, current.withBody(templateBody));
            }
        }
        if (current == null) {
            return null;
        }
        return new BodyNodeAndDoc(current.body(), current);
    }

    private record BodyNodeAndDoc(
        BodyNode body,
        LayoutDoc docToWrite
    ) {}
}
