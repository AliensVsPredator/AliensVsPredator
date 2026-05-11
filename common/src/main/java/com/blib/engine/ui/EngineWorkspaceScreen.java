package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.blockselection.BlockSelection;
import com.blib.engine.blockselection.BlockSelectionOps;
import com.blib.engine.jigsaw.JigsawPieceLibrary;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.jigsaw.JigsawPieceThumbnailCache;
import com.blib.engine.jigsaw.JigsawPlacementCursor;
import com.blib.engine.jigsaw.JigsawPoolLibrary;
import com.blib.engine.jigsaw.ProjectDraftCache;
import com.blib.engine.jigsaw.placement.JigsawPlacementFrameState;
import com.blib.engine.jigsaw.placement.JigsawPlacementOptions;
import com.blib.engine.jigsaw.placement.JigsawTemplateScanner;
import com.blib.engine.layout.ActiveLayoutState;
import com.blib.engine.layout.LayoutCatalog;
import com.blib.engine.layout.LayoutDoc;
import com.blib.engine.layout.LayoutSnapshot;
import com.blib.engine.layout.LayoutTemplate;
import com.blib.engine.layout.PanelRegistry;
import com.blib.engine.selection.SelectionManager;
import com.blib.engine.session.EngineMode;
import com.blib.engine.session.NavigationMode;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.spawn.EntitySpawnSelection;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SDeleteProjectPayload;
import com.blib.mod.common.network.packet.C2SGOAPTrackPayload;
import com.blib.mod.common.network.packet.C2SReloadProjectPayload;
import com.blib.mod.common.network.packet.C2SRemoveEntityPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionDirectoryPayload;
import com.blib.mod.common.network.packet.C2SUndoPlacementPayload;

/**
 * Top-level editor screen for the BLib Engine. The viewport is divided into a tree of docked regions by a
 * {@link DockNode}; each leaf wraps a {@link Panel}. Content leaves wrap their panels in a {@link TabbedPanel} so users
 * can swap, close, and drag tabs between panels at runtime; trim leaves (menu / toolbar / status bars) hold raw panels
 * and don't tab.
 * <p>
 * Drawing is wrapped in a {@link #SCALE} pose scale so layout uses logical pixels (~2.67× the GUI-units screen at
 * 0.375). Mouse coordinates from event callbacks are scaled the same way before tree-walks.
 * <p>
 * Layout sketch (Default template — see {@link LayoutTemplate} for the other built-in starting points):
 *
 * <pre>
 * +-------------------------------------------------------------+
 * | Menu bar (full-bleed)                                       |
 * +-------------------------------------------------------------+
 * | Toolbar (play/pause centered)                               |
 * +----------+--------------------------------+-----------------+
 * | Outliner |   Viewport                     |   Details       |
 * |  (tab)   |   (downsampled world+HUD)      |    (tab)        |
 * |          +--------------------------------+                 |
 * |          |   Content Browser (tab)        |                 |
 * +----------+--------------------------------+-----------------+
 * | Status bar (full-bleed)                                     |
 * +-------------------------------------------------------------+
 * </pre>
 *
 * Dividers between resizable regions can be dragged to repartition space; trim bars (menu / toolbar / status) are
 * pinned. Tabs can be clicked to switch, ×'d to close, and dragged across {@code TabbedPanel}s to rearrange.
 * <p>
 * Other layouts ({@code GOAP}, {@code JIGSAW}) keep the same skeleton but swap the right-side or bottom panel for a
 * tool tuned to their workflow. Layouts are switched via the {@code Layout} menu chip and persisted to
 * {@code <gameDir>/blib/engine/layouts/} so customizations survive game restarts. The active layout is per-project
 * (with a global fallback) — see {@link com.blib.engine.layout.ActiveLayoutState}.
 */
@ApiStatus.Internal
public final class EngineWorkspaceScreen extends Screen {

    /**
     * Pose scale applied to the entire workspace render. {@code 0.375f = 0.5 × 0.75} — three-quarters of the GOAP debug
     * HUD's text size, so editor text is denser without sacrificing legibility. Layout uses logical pixels; physical
     * size equals {@code logical × SCALE}.
     */
    private static final float SCALE = 0.375f;

    /**
     * Mouse-pixel half-thickness of a divider's hit zone, in logical pixels. A click within {@code DIVIDER_HIT_PX} of
     * the boundary line is treated as a divider drag-start.
     */
    private static final int DIVIDER_HIT_PX = 4;

    /**
     * Floor on any panel size during a divider drag (logical pixels). Prevents the user from collapsing a panel to zero
     * width / height where it would be unrecoverable without resetting the workspace.
     */
    private static final int MIN_PANEL_SIZE_PX = 24;

    /**
     * Squared cursor-motion threshold (logical pixels) to promote a pending tab click into an active drag. Below this,
     * a press-and-release on a tab is just an "activate this tab" click.
     */
    private static final double TAB_DRAG_THRESHOLD_SQ = 16.0;

    private static final int DIVIDER_HIGHLIGHT_COLOR = 0xFF4F8FFF;

    private static final int TAB_GHOST_BG_COLOR = 0xCC2C2C32;

    private static final int TAB_GHOST_TEXT_COLOR = 0xFFE0E0E0;

    private static final int TAB_DROP_TARGET_COLOR = 0x404F8FFF;

    private static final int TOOLTIP_BG_COLOR = 0xF01A1A1F;

    private static final int TOOLTIP_BORDER_COLOR = 0xFF353540;

    private static final int TOOLTIP_TEXT_COLOR = 0xFFD0D0D0;

    /**
     * Sentinel mouse coordinate used in place of the real one when the cursor is over an overlay (e.g. an open
     * dropdown) so panels rendered underneath universally fail their contains-point hover checks.
     * {@link Integer#MIN_VALUE} is far enough out that no reasonable rect will overlap it but small enough to survive
     * {@code int} arithmetic.
     */
    private static final int OFFSCREEN_MOUSE = Integer.MIN_VALUE / 2;

    private DockNode root;

    private @Nullable ActiveDrag activeDrag;

    private @Nullable TabDrag tabDrag;

    private @Nullable DropdownMenu openMenu;

    /**
     * Modal yes/no confirmation overlay for destructive actions (FILE → Delete Project). When non-null, takes priority
     * over every other input pathway and dims the underlying workspace.
     */
    private @Nullable ConfirmDialog confirmDialog;

    /**
     * Modal Capture dialog opened from the viewport's right-click context menu. Same modal lifecycle as
     * {@link #confirmDialog} — render after panels, mouseClicked / keyPressed take priority, cleared on close.
     */
    private @Nullable CaptureDialog captureDialog;

    /**
     * Modal text-input dialog for Save-As / Rename / Duplicate / New-from-Template flows. Shares the
     * {@link #confirmDialog} / {@link #captureDialog} lifecycle pattern.
     */
    private @Nullable LayoutNameDialog layoutNameDialog;

    /**
     * Modal layout-management dialog (list view with per-row actions). Same lifecycle as the others.
     */
    private @Nullable ManageLayoutsDialog manageLayoutsDialog;

    /**
     * Panel that captured the mouse via {@link Panel#mouseClickedCapture}. While non-null, {@link #mouseDragged} and
     * {@link #mouseReleased} route to this panel before any other handling, so a panel-driven drag (scrollbar, etc.)
     * tracks the cursor even when it leaves the panel rect. Cleared on {@code mouseReleased}.
     */
    private @Nullable Panel capturedPanel;

    /**
     * Id of the layout the user is currently editing. Sticks across screen re-opens within the same JVM session and is
     * persisted to {@code <gameDir>/blib/engine/state.json} on close so subsequent game sessions reopen to the same
     * layout. Resolution honors per-project overrides (see {@link ActiveLayoutState#resolve}); this field caches the
     * resolved id for the active session.
     */
    private static String activeLayoutId = LayoutTemplate.DEFAULT.id();

    public EngineWorkspaceScreen() {
        super(Component.literal("BLib Engine"));

        // The workspace expects a project to be active before reaching here — the picker (ProjectPickerScreen) is
        // the only entry point, and it sets ProjectSession.activeProject before transitioning. If somehow we're
        // constructed without one (programming error or a future code path that bypasses the picker), don't
        // pause / freeze the server: defer until a project is opened. Esc will still close the screen cleanly.
        if (ProjectSession.activeProject() == null) {
            return;
        }

        // Freeze the integrated server immediately on open so the world is paused while the user works in the editor.
        // The previous freeze state is captured here and restored in removed() so we don't unfreeze a server that the
        // user had already frozen via /tick freeze before opening the workspace.
        EngineTickControl.captureAndPause();

        // Engine mode (the freecam) is owned by the workspace now — there's no /blib engine fly|orbit anymore. Enter
        // engine mode in ORBIT navigation; the viewport panel forwards LMB drag / scroll / click events to apply.
        EngineMode.get().enter();
        var session = EngineMode.get().session();
        if (session != null) {
            session.setMode(NavigationMode.ORBIT);
        }

        // Drop any cached template list from a previous workspace session — the user might have reloaded data, added
        // a datapack, or switched worlds in between, so re-enumerate on entry. Same for pool data: pools reference
        // templates by id, so a datapack reload could invalidate the pool→templates map even if the template list
        // looks the same.
        JigsawPieceLibrary.invalidate();
        JigsawPoolLibrary.invalidate();

        // Request the faction directory eagerly: the chunk claim overlay reads faction colors from
        // ClientFactionDirectoryCache and falls back to grey when the cache is empty. The Faction Browser panel
        // also requests this on show, but it isn't part of every layout (default doesn't include it), so without
        // this kick the overlay stays grey until the user opens that panel. The cache is cleared in removed().
        BLib.MOD.networking().sendToServer(C2SRequestFactionDirectoryPayload.INSTANCE);

        // Seed built-in templates on first run (idempotent — does nothing if files already exist), then resolve the
        // active layout id from disk-backed state and load its body subtree. The outer trim is always rebuilt fresh.
        LayoutCatalog.initialize();
        var bodyRoot = loadActiveLayoutBody();
        this.root = buildOuterLayout(bodyRoot);
    }

    /**
     * Resolve the active layout id (per-project override → global → default fallback), load its {@link LayoutDoc}, and
     * hydrate the body subtree. Falls back to {@link LayoutTemplate#DEFAULT}'s code-baked body if the resolved id has
     * no readable file on disk — guarantees the workspace always opens to <em>something</em>.
     */
    private DockNode loadActiveLayoutBody() {
        var state = ActiveLayoutState.read();
        var projectName = ProjectSession.activeProject() != null ? ProjectSession.activeProjectName() : null;
        var resolvedId = ActiveLayoutState.resolve(projectName, state);
        activeLayoutId = resolvedId;

        var doc = LayoutCatalog.get(resolvedId);
        if (doc == null) {
            // Resolved id has no file (deleted externally, or seed failed). Fall back to a fresh default template.
            activeLayoutId = LayoutTemplate.DEFAULT.id();
            doc = LayoutTemplate.DEFAULT.toDoc();
        }
        return LayoutSnapshot.hydrate(doc.body(), panelCtx());
    }

    private PanelRegistry.Context panelCtx() {
        return new PanelRegistry.Context(
            buildViewportRightClickHandler(),
            this::onViewportRightClick,
            this::openContentDeleteConfirm
        );
    }

    /**
     * {@link ProjectContentActionHandler} adapter — the content browser asks the screen to spawn a destructive confirm
     * dialog. Wired into both {@link #panelCtx} and the Window-menu's "Reopen Project Contents" so a panel created via
     * either path gets the same modal behavior.
     */
    private void openContentDeleteConfirm(String title, String message, Runnable onConfirm) {
        this.confirmDialog = new ConfirmDialog(title, message, "Delete", "Cancel", true, onConfirm, () -> {});
    }

    /**
     * Wraps {@code workspaceBody} (the central editable area) in the standard menu-bar / toolbar / status-bar trim
     * shared by every layout. Splits are pinned to the trim panels' fixed heights so the body fills the remaining
     * space.
     */
    private DockNode buildOuterLayout(DockNode workspaceBody) {
        var bodyAndStatus = new DockNode.Split(
            Orientation.VERTICAL,
            workspaceBody,
            new DockNode.Leaf(new StatusBarPanel()),
            new Sizing.SecondFixed(StatusBarPanel.HEIGHT)
        );

        var toolbarAndBelow = new DockNode.Split(
            Orientation.VERTICAL,
            new DockNode.Leaf(new ToolbarPanel()),
            bodyAndStatus,
            new Sizing.FirstFixed(ToolbarPanel.HEIGHT)
        );

        return new DockNode.Split(
            Orientation.VERTICAL,
            new DockNode.Leaf(new MenuBarPanel()),
            toolbarAndBelow,
            new Sizing.FirstFixed(MenuBarPanel.HEIGHT)
        );
    }

    /**
     * Switch the active layout to {@code newLayoutId}. Persists the outgoing layout's body to disk before switching so
     * any in-session customizations carry over to the next reopen, then loads the incoming layout (falling back to the
     * default template if its file has been deleted in the meantime).
     */
    private void switchLayout(String newLayoutId) {
        if (newLayoutId.equals(activeLayoutId)) {
            return;
        }
        persistOutgoingLayout();
        activeLayoutId = newLayoutId;
        var doc = LayoutCatalog.get(newLayoutId);
        if (doc == null) {
            activeLayoutId = LayoutTemplate.DEFAULT.id();
            doc = LayoutTemplate.DEFAULT.toDoc();
        }
        var bodyRoot = LayoutSnapshot.hydrate(doc.body(), panelCtx());
        this.root = buildOuterLayout(bodyRoot);
        persistActiveSelection();
        // Refresh the manage dialog if it's open so the active marker tracks the switch.
        if (manageLayoutsDialog != null) {
            manageLayoutsDialog.setActiveLayoutId(activeLayoutId);
            manageLayoutsDialog.refresh();
        }
    }

    /**
     * Capture the current body subtree into the active layout's {@link LayoutDoc} and write it to disk. No-op if no
     * file exists for the active id (e.g. the layout was deleted out from under us); the next persist after a fresh
     * load will succeed.
     */
    private void persistOutgoingLayout() {
        var existing = LayoutCatalog.get(activeLayoutId);
        if (existing == null) {
            return;
        }
        var capturedBody = LayoutSnapshot.capture(extractBodyRoot(this.root));
        var updated = existing.withBody(capturedBody);
        try {
            LayoutCatalog.save(updated);
        } catch (java.io.IOException e) {
            org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class)
                .warn("[BLib] persistOutgoingLayout: failed to save '{}'", activeLayoutId, e);
        }
    }

    /**
     * Update {@code state.json} with the active layout id, scoped to the active project if there is one. Called from
     * {@code switchLayout} and {@code removed()} so per-project active-layout memory survives game restarts.
     */
    private void persistActiveSelection() {
        var state = ActiveLayoutState.read();
        var projectName = ProjectSession.activeProject() != null ? ProjectSession.activeProjectName() : null;
        var newState = projectName != null && !projectName.isEmpty()
            ? state.withProjectActive(projectName, activeLayoutId)
            : state.withGlobalActive(activeLayoutId);
        ActiveLayoutState.write(newState);
    }

    /**
     * Walk past the trim wrappers built by {@link #buildOuterLayout} to reach the body subtree. The structure is always
     * {@code Split(V, Leaf(MenuBar), Split(V, Leaf(Toolbar), Split(V, body, Leaf(StatusBar))))}; defensively falls back
     * to {@code root} itself if anything doesn't match (shouldn't happen with our own builder, but keeps capture from
     * blowing up on hand-corrupted in-memory state).
     */
    private static DockNode extractBodyRoot(DockNode root) {
        if (
            root instanceof DockNode.Split outer
                && outer.second() instanceof DockNode.Split toolbarAndBelow
                && toolbarAndBelow.second() instanceof DockNode.Split bodyAndStatus
        ) {
            return bodyAndStatus.first();
        }
        return root;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * Suppress the default screen background so the workspace overlays the live world without dimming it. Panels that
     * want a backdrop fill it themselves; the viewport panel hosts a downsampled blit of the world+HUD via
     * {@link EngineWorkspaceCompositor}.
     */
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        // The world + HUD have already been rendered to the main RT at full window resolution by the time screen
        // render runs. Downsample-blit them into the viewport panel rect so the user sees the player-eye view shrunk
        // to fit the viewport, with all proportions / FOV / HUD layout intact.
        var viewportRect = findViewportRect(root, 0, 0, logicalWidth(), logicalHeight());
        if (viewportRect != null) {
            compositWorldIntoViewport(viewportRect);
        }

        var pose = graphics.pose();
        pose.pushPose();
        pose.scale(SCALE, SCALE, 1.0f);

        int logicalWidth = logicalWidth();
        int logicalHeight = logicalHeight();
        int logicalMouseX = (int) (mouseX / SCALE);
        int logicalMouseY = (int) (mouseY / SCALE);

        // While a dropdown menu is open AND the cursor is over the menu rect, panels under the menu must not see the
        // mouse — otherwise their hover-state code (segmented-control buttons, viewport selection highlights, tab-
        // strip hover, content-browser cells, etc.) lights up under the menu, leaking interaction state through the
        // popover. Substituting a sentinel "off-screen" mouse position for the panel render walk makes every panel's
        // contains-point check fail uniformly without requiring each panel to know about the menu. Divider / tab-drag
        // / tooltip overlays already check {@code openMenu} themselves and stay suppressed.
        int panelMouseX = logicalMouseX;
        int panelMouseY = logicalMouseY;
        if (openMenu != null && openMenu.isInside(logicalMouseX, logicalMouseY)) {
            panelMouseX = OFFSCREEN_MOUSE;
            panelMouseY = OFFSCREEN_MOUSE;
        }
        var openPopup = SearchableSelect.getOpenPopup();
        if (openPopup != null && openPopup.isInside(logicalMouseX, logicalMouseY)) {
            panelMouseX = OFFSCREEN_MOUSE;
            panelMouseY = OFFSCREEN_MOUSE;
        }
        var openColorPopup = HslColorPickerPopup.getOpenPopup();
        if (openColorPopup != null && openColorPopup.isInside(logicalMouseX, logicalMouseY)) {
            panelMouseX = OFFSCREEN_MOUSE;
            panelMouseY = OFFSCREEN_MOUSE;
        }
        var openFactionMgmtPopup = FactionManagePopup.getOpenPopup();
        if (openFactionMgmtPopup != null && openFactionMgmtPopup.isInside(logicalMouseX, logicalMouseY)) {
            panelMouseX = OFFSCREEN_MOUSE;
            panelMouseY = OFFSCREEN_MOUSE;
        }
        // Confirm dialog is fully modal — every panel underneath must lose hover state.
        if (confirmDialog != null) {
            panelMouseX = OFFSCREEN_MOUSE;
            panelMouseY = OFFSCREEN_MOUSE;
        }
        if (captureDialog != null) {
            panelMouseX = OFFSCREEN_MOUSE;
            panelMouseY = OFFSCREEN_MOUSE;
        }
        if (layoutNameDialog != null || manageLayoutsDialog != null) {
            panelMouseX = OFFSCREEN_MOUSE;
            panelMouseY = OFFSCREEN_MOUSE;
        }

        renderNode(graphics, root, 0, 0, logicalWidth, logicalHeight, panelMouseX, panelMouseY, partialTick);
        renderHoveredDivider(graphics, logicalMouseX, logicalMouseY);
        renderTabDragOverlay(graphics, logicalMouseX, logicalMouseY);

        if (openMenu != null) {
            openMenu.render(graphics, logicalMouseX, logicalMouseY);
        }
        if (confirmDialog != null) {
            confirmDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
        }
        if (captureDialog != null) {
            captureDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
        }
        if (manageLayoutsDialog != null) {
            manageLayoutsDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
        }
        // LayoutNameDialog renders on top of ManageLayoutsDialog because Save-As / Rename / Duplicate / etc. opened
        // from the manage modal stack a second sheet on top of it; rendering it last keeps it visible above the list.
        if (layoutNameDialog != null) {
            layoutNameDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
        }
        if (openPopup != null) {
            openPopup.render(graphics, logicalMouseX, logicalMouseY, logicalWidth, logicalHeight);
        }
        if (openColorPopup != null) {
            openColorPopup.render(graphics, logicalMouseX, logicalMouseY, logicalWidth, logicalHeight);
        }
        var openFactionPopup = FactionManagePopup.getOpenPopup();
        if (openFactionPopup != null) {
            openFactionPopup.render(graphics, logicalMouseX, logicalMouseY, logicalWidth, logicalHeight);
        }

        renderHoverTooltip(graphics, logicalMouseX, logicalMouseY);

        pose.popPose();

        // OS cursor swap: crosshair while a piece is held and the cursor is over the viewport, default elsewhere.
        // Done after pose.popPose() because we're working in logical coords (which we already computed inside the
        // scaled section) and don't need the matrix anymore — GLFW takes raw window-space cursor info from the OS.
        if (viewportRect != null) {
            EngineCursor.update(
                logicalMouseX,
                logicalMouseY,
                viewportRect.x(),
                viewportRect.y(),
                viewportRect.width(),
                viewportRect.height(),
                JigsawPieceSelection.hasSelection() || EntitySpawnSelection.hasSelection()
            );
        } else {
            EngineCursor.reset();
        }
    }

    /**
     * Asks the panel under the cursor for tooltip text and, if any, draws it as a small floating box near the cursor.
     * Suppressed while a dropdown menu is open (tooltips would visually fight with the menu) and while a divider or tab
     * is being dragged (the user's focus is on the drag, not the panel beneath).
     */
    private void renderHoverTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (openMenu != null || activeDrag != null || (tabDrag != null && tabDrag.active)) {
            return;
        }
        // Suppress tooltips while the color picker is open — they'd float behind the popup and read as junk.
        if (HslColorPickerPopup.getOpenPopup() != null) {
            return;
        }
        // Same suppression for the faction-management popup.
        if (FactionManagePopup.getOpenPopup() != null) {
            return;
        }
        var leaf = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), mouseX, mouseY);
        if (leaf == null) {
            return;
        }
        var tip = leaf.tooltipText();
        if (tip == null) {
            return;
        }
        drawTooltipBox(graphics, tip, mouseX, mouseY);
    }

    /**
     * Maximum tooltip body width before {@link Font#split} wraps. Picked so multi-sentence help text breaks across 3-4
     * lines at typical workspace logical-pixel sizes — wide enough to avoid awkward 1-2 word lines, narrow enough that
     * the tooltip doesn't span half the screen.
     */
    private static final int TOOLTIP_MAX_WIDTH = 240;

    /**
     * Draws a tooltip for {@code text} positioned next to the cursor, kept inside the workspace bounds. Wraps long text
     * via {@link Font#split} so multi-sentence help text renders as multiple lines instead of overflowing past the
     * right edge. Manual rendering (rather than {@code GuiGraphics.renderTooltip}) so the styling matches the
     * workspace's flat dark theme and so we control sizing in workspace-logical pixels.
     */
    private void drawTooltipBox(GuiGraphics graphics, Component text, int mouseX, int mouseY) {
        var font = Minecraft.getInstance().font;
        var paddingX = 3;
        var paddingY = 2;
        var lineHeight = font.lineHeight;

        var lines = font.split(text, TOOLTIP_MAX_WIDTH - 2 * paddingX);
        if (lines.isEmpty()) {
            return;
        }
        var textWidth = 0;
        for (var line : lines) {
            textWidth = Math.max(textWidth, font.width(line));
        }

        var boxW = textWidth + paddingX * 2;
        var boxH = lines.size() * lineHeight + paddingY * 2;

        // Default position: just to the right of and below the cursor, with a small offset.
        var tipX = mouseX + 8;
        var tipY = mouseY + 8;
        // Keep the box inside the workspace; flip to the other side of the cursor if it'd overflow right/bottom.
        if (tipX + boxW > logicalWidth()) {
            tipX = mouseX - 4 - boxW;
        }
        if (tipY + boxH > logicalHeight()) {
            tipY = mouseY - 4 - boxH;
        }
        // Background + 1-px border in the same flat-dark style the rest of the workspace uses.
        graphics.fill(tipX, tipY, tipX + boxW, tipY + boxH, TOOLTIP_BG_COLOR);
        graphics.fill(tipX, tipY, tipX + boxW, tipY + 1, TOOLTIP_BORDER_COLOR);
        graphics.fill(tipX, tipY + boxH - 1, tipX + boxW, tipY + boxH, TOOLTIP_BORDER_COLOR);
        graphics.fill(tipX, tipY, tipX + 1, tipY + boxH, TOOLTIP_BORDER_COLOR);
        graphics.fill(tipX + boxW - 1, tipY, tipX + boxW, tipY + boxH, TOOLTIP_BORDER_COLOR);

        // Top-anchored layout: first line at tipY + paddingY (+1 for descender padding so the glyph sits visually
        // centered on its baseline, mirroring the single-line math from before), subsequent lines stacked by
        // lineHeight.
        var lineY = tipY + paddingY + 1;
        for (var line : lines) {
            graphics.drawString(font, line, tipX + paddingX, lineY, TOOLTIP_TEXT_COLOR, false);
            lineY += lineHeight;
        }
    }

    private void renderTabDragOverlay(GuiGraphics graphics, int mouseX, int mouseY) {
        if (tabDrag == null || !tabDrag.active) {
            return;
        }

        // Highlight the drop target — strip area for tab merges, half-rect for edge splits, full content for
        // center-zone merges.
        var target = findTabbedPanelAt(mouseX, mouseY);
        if (target != null) {
            if (target.isInTabStrip(mouseX, mouseY)) {
                graphics.fill(
                    target.rectX(),
                    target.rectY(),
                    target.rectX() + target.rectWidth(),
                    target.rectY() + TabbedPanel.TAB_BAR_HEIGHT,
                    TAB_DROP_TARGET_COLOR
                );
            } else {
                renderDropZoneOverlay(graphics, target, computeDropZoneInContent(target, mouseX, mouseY));
            }
        }

        // Ghost: a translucent tab-shaped chip floating with the cursor.
        var font = Minecraft.getInstance().font;
        var label = tabDrag.tab.title();
        var labelWidth = font.width(label);
        var w = labelWidth + 12;
        var h = TabbedPanel.TAB_BAR_HEIGHT;
        var x = mouseX - w / 2;
        var y = mouseY - h / 2;
        graphics.fill(x, y, x + w, y + h, TAB_GHOST_BG_COLOR);
        // +2 compensates for MC font's descender padding so the tab-drag ghost label visually centers; see
        // MenuBarPanel.
        graphics.drawString(font, Component.literal(label), x + 6, y + (h - font.lineHeight + 2) / 2, TAB_GHOST_TEXT_COLOR, false);
    }

    /**
     * Map the cursor's position over a {@link TabbedPanel}'s content area (excluding the tab strip) to a
     * {@link DropZone}. The middle 50% × 50% of the content rect is the {@code CENTER} (tab-merge) zone; outside that
     * inner rect, the closest edge defines the split direction.
     */
    private static DropZone computeDropZoneInContent(TabbedPanel target, double mouseX, double mouseY) {
        var contentY = target.rectY() + TabbedPanel.TAB_BAR_HEIGHT;
        var contentH = Math.max(1, target.rectHeight() - TabbedPanel.TAB_BAR_HEIGHT);
        var contentW = Math.max(1, target.rectWidth());

        var relX = (mouseX - target.rectX()) / contentW;
        var relY = (mouseY - contentY) / contentH;

        if (relX > 0.25 && relX < 0.75 && relY > 0.25 && relY < 0.75) {
            return DropZone.CENTER;
        }

        var distLeft = relX;
        var distRight = 1.0 - relX;
        var distTop = relY;
        var distBottom = 1.0 - relY;
        var minDist = Math.min(Math.min(distLeft, distRight), Math.min(distTop, distBottom));

        if (minDist == distLeft) {
            return DropZone.LEFT;
        }
        if (minDist == distRight) {
            return DropZone.RIGHT;
        }
        if (minDist == distTop) {
            return DropZone.TOP;
        }
        return DropZone.BOTTOM;
    }

    private static void renderDropZoneOverlay(GuiGraphics graphics, TabbedPanel target, DropZone zone) {
        var x = target.rectX();
        var w = target.rectWidth();
        var contentY = target.rectY() + TabbedPanel.TAB_BAR_HEIGHT;
        var contentH = Math.max(0, target.rectHeight() - TabbedPanel.TAB_BAR_HEIGHT);

        var x0 = x;
        var y0 = contentY;
        var x1 = x + w;
        var y1 = contentY + contentH;
        switch (zone) {
            case CENTER -> {
                // full content rect
            }
            case TOP -> y1 = contentY + contentH / 2;
            case BOTTOM -> y0 = contentY + contentH / 2;
            case LEFT -> x1 = x + w / 2;
            case RIGHT -> x0 = x + w / 2;
        }
        graphics.fill(x0, y0, x1, y1, TAB_DROP_TARGET_COLOR);
    }

    /**
     * Convert the viewport panel's logical rect (top-left origin, in workspace logical pixels) to GL framebuffer coords
     * (bottom-left origin, in raw window pixels) and ask the compositor to downsample-blit world+HUD into it.
     */
    private void compositWorldIntoViewport(LogicalRect rect) {
        var window = Minecraft.getInstance().getWindow();
        var guiScale = window.getGuiScale();
        var rawWindowHeight = window.getHeight();

        var screenX = rect.x() * SCALE;
        var screenY = rect.y() * SCALE;
        var screenW = rect.width() * SCALE;
        var screenH = rect.height() * SCALE;

        var rawX = (int) Math.round(screenX * guiScale);
        var rawY = (int) Math.round(rawWindowHeight - (screenY + screenH) * guiScale);
        var rawW = (int) Math.round(screenW * guiScale);
        var rawH = (int) Math.round(screenH * guiScale);

        EngineWorkspaceCompositor.composit(rawX, rawY, rawW, rawH);
    }

    @Override
    public void removed() {
        super.removed();
        // Persist the active layout's customized state to disk and update state.json so the next /blib engine — even
        // across game restarts — reopens to the same arrangement of tabs, splits, and active panels. The active-id
        // write also captures any per-project memory so switching projects later restores per-project preferences.
        persistOutgoingLayout();
        persistActiveSelection();
        EngineWorkspaceCompositor.clear();
        EngineMode.get().exit();
        EngineTickControl.restore();
        JigsawPieceSelection.clear();
        com.blib.engine.jigsaw.JigsawPoolSelection.clear();
        com.blib.engine.projectcontents.ProjectContents.clear();
        com.blib.internal.client.faction.ClientFactionDirectoryCache.clear();
        com.blib.internal.client.faction.ClientFactionInspectionCache.clear();
        com.blib.internal.client.faction.ClientFactionMembersCache.clear();
        com.blib.internal.client.faction.ClientEntityFactionsCache.clear();
        EntitySpawnSelection.clear();
        JigsawPlacementCursor.clearViewportRect();
        JigsawPieceThumbnailCache.clear();
        JigsawTemplateScanner.clear();
        JigsawPlacementFrameState.clear();
        // Cascades through TransformedTemplateCache, CollisionScanner, and JigsawPreviewMeshCache so the per-piece
        // GPU vertex buffers are closed before the workspace exits — otherwise they'd linger until the next workspace
        // open re-invalidated them from the constructor.
        JigsawPieceLibrary.invalidate();
        JigsawPlacementOptions.reset();
        SelectionManager.clear();
        EngineCursor.reset();
        SearchableSelect.closeOpenPopup();
        HslColorPickerPopup.closeOpenPopup();
        FactionManagePopup.closeOpenPopup();
        com.blib.engine.territory.ClaimPaintTool.deactivate();
        com.blib.engine.selection.EngineHoverProbe.clear();
        // Project state does not persist across engine sessions — closing the workspace returns the user to a
        // "no project open" state so the next /blib engine starts at the picker again.
        ProjectSession.clear();
        ProjectDraftCache.clear();
        com.blib.engine.tag.TagDraftCache.clear();
        com.blib.engine.tag.TagCatalogCache.clear();
        com.blib.engine.tag.RegistryEntriesCache.clear();
        com.blib.engine.tag.TagSelection.clear();
        // Capture selection is workspace-session-only too — corners and mode reset between engine opens.
        BlockSelection.clear();
        // Clear the AABB scale gizmo's hover/drag state so a stray drag-in-progress at close doesn't try to
        // continue against fresh state on the next engine open.
        com.blib.engine.blockselection.BlockSelectionScaleGizmo.clear();
        com.blib.engine.blockselection.BlockSelectionTranslateGizmo.clear();
        com.blib.engine.entityselection.EntityTranslateGizmo.clear();
        com.blib.engine.entityselection.EntityScaleGizmo.clear();
        // Drop the captured render-frame matrices — they referenced the engine's camera; the next engine open
        // will repopulate from the first render frame.
        com.blib.engine.session.EngineCameraFrame.clear();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // Modal dialog absorbs all scroll events so panels under the dim don't scroll while the user is deciding.
        if (confirmDialog != null || captureDialog != null || layoutNameDialog != null || manageLayoutsDialog != null) {
            return true;
        }
        var logicalX = mouseX / SCALE;
        var logicalY = mouseY / SCALE;
        // SearchableSelect popup gets first claim on scroll wheel — its filtered list scrolls. Cursor outside the
        // popup falls through, mirroring the menu pattern.
        var openPopup = SearchableSelect.getOpenPopup();
        if (openPopup != null && openPopup.mouseScrolled(logicalX, logicalY, scrollY)) {
            return true;
        }
        // Color picker absorbs scroll events over its rect — no scrollable content, but we don't want underlying
        // panels reacting to the wheel while the picker is open.
        var openColorPopup = HslColorPickerPopup.getOpenPopup();
        if (openColorPopup != null && openColorPopup.mouseScrolled(logicalX, logicalY, scrollY)) {
            return true;
        }
        // Faction-management popup: its row list scrolls vertically when the faction directory overflows.
        var openFactionPopup = FactionManagePopup.getOpenPopup();
        if (openFactionPopup != null && openFactionPopup.mouseScrolled(logicalX, logicalY, scrollY)) {
            return true;
        }
        // Scroll-wheel events that land on an open menu shouldn't tunnel through to the scroll containers of panels
        // below — consume them.
        if (openMenu != null && openMenu.isInside(logicalX, logicalY)) {
            return true;
        }
        var leaf = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
        if (leaf != null && leaf.mouseScrolled(logicalX, logicalY, scrollX, scrollY)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    /**
     * Walks the dock tree to find the rect (in logical pixels) of the workspace's active viewport — the inner rect of
     * the {@link TabbedPanel} whose active tab is a {@link ViewportPanel}, with the tab strip excluded. Returns null if
     * no panel currently shows the viewport.
     */
    private static @Nullable LogicalRect findViewportRect(DockNode node, int x, int y, int width, int height) {
        return switch (node) {
            case DockNode.Leaf leaf -> {
                var panel = leaf.panel();
                if (panel instanceof TabbedPanel tabbed && tabbed.activeTab() instanceof ViewportPanel) {
                    var contentY = y + TabbedPanel.TAB_BAR_HEIGHT + 1;
                    var contentH = Math.max(0, height - TabbedPanel.TAB_BAR_HEIGHT - 1);
                    yield new LogicalRect(x, contentY, width, contentH);
                }
                yield null;
            }
            case DockNode.Split split -> {
                if (split.orientation() == Orientation.HORIZONTAL) {
                    var firstWidth = DockNode.boundary(split.sizing(), width);
                    var inFirst = findViewportRect(split.first(), x, y, firstWidth, height);
                    if (inFirst != null) {
                        yield inFirst;
                    }
                    yield findViewportRect(split.second(), x + firstWidth, y, width - firstWidth, height);
                }
                var firstHeight = DockNode.boundary(split.sizing(), height);
                var inFirst = findViewportRect(split.first(), x, y, width, firstHeight);
                if (inFirst != null) {
                    yield inFirst;
                }
                yield findViewportRect(split.second(), x, y + firstHeight, width, height - firstHeight);
            }
        };
    }

    private record LogicalRect(
        int x,
        int y,
        int width,
        int height
    ) {}

    private void renderHoveredDivider(GuiGraphics graphics, int mouseX, int mouseY) {
        // While a dropdown menu is open, suppress divider hover feedback — the dropdown should consume hover
        // interactions over its area, and showing a divider highlight underneath misleads the user into thinking
        // they can drag a divider through the menu.
        if (activeDrag == null && openMenu != null) {
            return;
        }
        var dragger = activeDrag != null
            ? activeDrag.divider
            : findDivider(root, 0, 0, logicalWidth(), logicalHeight(), mouseX, mouseY);
        if (dragger == null) {
            return;
        }

        var bx = dragger.boundaryStartX();
        var by = dragger.boundaryStartY();
        if (dragger.split.orientation() == Orientation.HORIZONTAL) {
            graphics.fill(bx - 1, by, bx + 1, by + dragger.parentHeight, DIVIDER_HIGHLIGHT_COLOR);
        } else {
            graphics.fill(bx, by - 1, bx + dragger.parentWidth, by + 1, DIVIDER_HIGHLIGHT_COLOR);
        }
    }

    private static void renderNode(
        GuiGraphics graphics,
        DockNode node,
        int x,
        int y,
        int width,
        int height,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        switch (node) {
            case DockNode.Leaf leaf -> {
                var panel = leaf.panel();
                if (panel.hasChrome()) {
                    var inner = PanelChrome.draw(graphics, x, y, width, height, panel.title());
                    panel.render(graphics, inner.x(), inner.y(), inner.width(), inner.height(), mouseX, mouseY, partialTick);
                } else {
                    panel.render(graphics, x, y, width, height, mouseX, mouseY, partialTick);
                }
            }
            case DockNode.Split split -> {
                if (split.orientation() == Orientation.HORIZONTAL) {
                    var firstWidth = DockNode.boundary(split.sizing(), width);
                    renderNode(graphics, split.first(), x, y, firstWidth, height, mouseX, mouseY, partialTick);
                    renderNode(graphics, split.second(), x + firstWidth, y, width - firstWidth, height, mouseX, mouseY, partialTick);
                } else {
                    var firstHeight = DockNode.boundary(split.sizing(), height);
                    renderNode(graphics, split.first(), x, y, width, firstHeight, mouseX, mouseY, partialTick);
                    renderNode(graphics, split.second(), x, y + firstHeight, width, height - firstHeight, mouseX, mouseY, partialTick);
                }
            }
        }
    }

    @Override
    public boolean charTyped(char ch, int modifiers) {
        // Layout-name dialog has its own TextInput and routes char input through it. Manage dialog has no text input
        // so it just absorbs char events as a hard modal.
        if (layoutNameDialog != null) {
            return layoutNameDialog.charTyped(ch, modifiers);
        }
        if (manageLayoutsDialog != null) {
            return true;
        }
        var focused = TextInput.getFocused();
        if (focused != null && focused.charTyped(ch, modifiers)) {
            return true;
        }
        return super.charTyped(ch, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Confirm dialog absorbs Esc (treats it as Cancel) before any other Esc handling so a stray tap doesn't
        // cascade into clearing piece selection / closing the workspace.
        if (confirmDialog != null && confirmDialog.keyPressed(keyCode)) {
            confirmDialog = null;
            return true;
        }
        if (confirmDialog != null) {
            // While the dialog is open, swallow other key events too — typing into nothing while a confirm is
            // pending would feel unresponsive.
            return true;
        }
        if (captureDialog != null) {
            return captureDialog.keyPressed(keyCode, scanCode, modifiers);
        }
        // LayoutNameDialog stacks over ManageLayoutsDialog (Save-As / Rename / Duplicate sheet), so route to it first.
        if (layoutNameDialog != null) {
            return layoutNameDialog.keyPressed(keyCode, scanCode, modifiers);
        }
        if (manageLayoutsDialog != null) {
            return manageLayoutsDialog.keyPressed(keyCode, scanCode, modifiers);
        }
        // Esc closes an open SearchableSelect popup BEFORE TextInput dispatch — otherwise the popup's focused
        // search input would consume Esc as "defocus" and leave the popup visible-but-unfocused, which is confusing.
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE && SearchableSelect.getOpenPopup() != null) {
            SearchableSelect.closeOpenPopup();
            TextInput.clearFocus();
            return true;
        }
        // Esc closes an open color-picker popup too.
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE && HslColorPickerPopup.getOpenPopup() != null) {
            HslColorPickerPopup.closeOpenPopup();
            return true;
        }
        // Same for the faction-management popup.
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE && FactionManagePopup.getOpenPopup() != null) {
            FactionManagePopup.closeOpenPopup();
            return true;
        }

        var focused = TextInput.getFocused();
        if (focused != null && focused.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        // Esc cascades through transient state before closing the workspace: claim paint mode → held piece → entity
        // spawn selection → general selection → fall through to super.keyPressed (which closes the screen). This
        // gives users a single "get me out" key that doesn't immediately exit when they're mid-edit.
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            if (com.blib.engine.territory.ClaimPaintTool.isActive()) {
                com.blib.engine.territory.ClaimPaintTool.deactivate();
                return true;
            }
            if (JigsawPieceSelection.hasSelection()) {
                JigsawPieceSelection.clear();
                return true;
            }
            if (EntitySpawnSelection.hasSelection()) {
                EntitySpawnSelection.clear();
                return true;
            }
            if (!SelectionManager.current().isEmpty()) {
                SelectionManager.clear();
                return true;
            }
        }

        // Ctrl+Z = universal undo. Works regardless of whether a piece is held — the placement history is server-
        // side and decoupled from the held piece. Ctrl+Y / redo isn't wired yet (PlacementHistory is a one-way stack;
        // see plan for follow-up scope).
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_Z && Screen.hasControlDown() && !Screen.hasShiftDown()) {
            BLib.MOD.networking().sendToServer(C2SUndoPlacementPayload.INSTANCE);
            return true;
        }

        // Placement-mode hotkeys: R cycles rotation forward (clockwise), M cycles mirror, T toggles between FREE
        // and JIGSAW_SNAP placement modes. Gated by an active piece selection so these keys don't steal input from
        // other potential editor tools later. Suppressed while a text input is focused (handled above), so typing
        // them into the search box won't rotate the world preview / change modes.
        if (JigsawPieceSelection.hasSelection()) {
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_R) {
                JigsawPieceSelection.cycleRotation(1);
                return true;
            }
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_M) {
                JigsawPieceSelection.cycleMirror();
                return true;
            }
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_T) {
                com.blib.engine.jigsaw.placement.JigsawTool.cycleNextImplementedMode();
                return true;
            }
        }

        // Tool hotkeys: T / S / M for Translate / Scale / Move-Blocks. Mirrors Blender's G/S/R muscle memory.
        // Auto-switches between block-volume and entity gizmo modes based on the active selection — same keys, the
        // selection type decides which gizmo state changes. M is intentionally block-only since entities have no
        // analog to MOVE_BLOCKS.
        var tssel = SelectionManager.current().single();
        if (tssel instanceof com.blib.engine.selection.EntitySelectable) {
            switch (keyCode) {
                case org.lwjgl.glfw.GLFW.GLFW_KEY_T -> {
                    com.blib.engine.entityselection.EntityGizmoMode.set(com.blib.engine.entityselection.EntityGizmoMode.TRANSLATE);
                    return true;
                }
                case org.lwjgl.glfw.GLFW.GLFW_KEY_S -> {
                    com.blib.engine.entityselection.EntityGizmoMode.set(com.blib.engine.entityselection.EntityGizmoMode.SCALE);
                    return true;
                }
                // M intentionally falls through — entity gizmo has no MOVE_BLOCKS analog. We don't redirect M to
                // block gizmos either since the user's selection is an entity, not a block volume.
            }
        } else {
            switch (keyCode) {
                case org.lwjgl.glfw.GLFW.GLFW_KEY_T -> {
                    BlockSelection.setGizmoMode(BlockSelection.GizmoMode.TRANSLATE_VOLUME);
                    return true;
                }
                case org.lwjgl.glfw.GLFW.GLFW_KEY_S -> {
                    BlockSelection.setGizmoMode(BlockSelection.GizmoMode.SCALE_VOLUME);
                    return true;
                }
                case org.lwjgl.glfw.GLFW.GLFW_KEY_M -> {
                    BlockSelection.setGizmoMode(BlockSelection.GizmoMode.MOVE_BLOCKS);
                    return true;
                }
            }
        }

        // Clipboard hotkeys: Ctrl+C / Ctrl+X / Ctrl+V for copy / cut / paste, Delete for clear. Gated on no focused
        // text input so the muscle-memory of Ctrl+C in a name field doesn't accidentally copy blocks instead of text.
        // BlockSelectionOps self-gates on AABB presence + volume cap; clicks/keys without a valid AABB are no-ops.
        if (TextInput.getFocused() == null && Screen.hasControlDown() && !Screen.hasShiftDown()) {
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_C) {
                BlockSelectionOps.copy(false);
                return true;
            }
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_X) {
                BlockSelectionOps.copy(true);
                return true;
            }
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_V) {
                BlockSelectionOps.paste();
                return true;
            }
        }
        if (TextInput.getFocused() == null && keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE) {
            BlockSelectionOps.delete();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var logicalX = mouseX / SCALE;
        var logicalY = mouseY / SCALE;

        // Confirm dialog has top priority — modal until the user picks confirm or cancel. Outside-clicks consumed
        // (no click-through to panels below) but ignored by the dialog itself; destructive actions require an
        // explicit decision via the buttons or Esc.
        if (confirmDialog != null) {
            if (confirmDialog.mouseClicked(logicalX, logicalY, button)) {
                confirmDialog = null;
            }
            return true;
        }
        if (captureDialog != null) {
            captureDialog.mouseClicked(logicalX, logicalY, button);
            return true;
        }
        // LayoutNameDialog (when stacked, e.g. Save-As opened from Manage) gets first crack so its TextInput
        // and confirm/cancel buttons see clicks before the ManageLayoutsDialog list does.
        if (layoutNameDialog != null) {
            layoutNameDialog.mouseClicked(logicalX, logicalY, button);
            return true;
        }
        if (manageLayoutsDialog != null) {
            manageLayoutsDialog.mouseClicked(logicalX, logicalY, button);
            return true;
        }

        // Clear focus before dispatch — panels with text inputs will re-focus their own inputs in their
        // mouseClicked handlers if the click hit the input rect, otherwise focus stays cleared (click-outside
        // defocus).
        TextInput.clearFocus();

        // 0a) An open SearchableSelect popup takes priority over everything else (search input, scrollbar, list
        // rows). Click outside the popup closes it and falls through so a click on a menu chip / another widget
        // still gets a chance to run in the same gesture.
        var openPopup = SearchableSelect.getOpenPopup();
        if (openPopup != null) {
            if (openPopup.isInside(logicalX, logicalY)) {
                openPopup.mouseClicked(logicalX, logicalY, button);
                return true;
            }
            SearchableSelect.closeOpenPopup();
        }
        // 0b) Color picker popup — same outside-click-closes pattern.
        var openColorPopup = HslColorPickerPopup.getOpenPopup();
        if (openColorPopup != null) {
            if (openColorPopup.isInside((int) logicalX, (int) logicalY)) {
                openColorPopup.mouseClicked(logicalX, logicalY, button);
                return true;
            }
            HslColorPickerPopup.closeOpenPopup();
        }
        // 0c) Faction management popup — same outside-click-closes pattern.
        var openFactionPopup = FactionManagePopup.getOpenPopup();
        if (openFactionPopup != null) {
            if (openFactionPopup.isInside(logicalX, logicalY)) {
                openFactionPopup.mouseClicked(logicalX, logicalY, button);
                return true;
            }
            FactionManagePopup.closeOpenPopup();
        }

        // 0) An open dropdown takes priority: clicking an item fires it; clicking outside just closes the menu.
        if (openMenu != null) {
            if (button == 0) {
                var idx = openMenu.hitItemAt(logicalX, logicalY);
                if (idx >= 0) {
                    var item = openMenu.itemAt(idx);
                    openMenu = null;
                    item.action().run();
                    return true;
                }
            }
            openMenu = null;
            // Only fall through to chip-click handling below if the cursor landed on another menu chip — that lets
            // the user close-and-reopen by clicking a different chip in one motion. Anything else (clicks on
            // dividers, tab strips, panel content) is consumed so dropdown clicks never accidentally start a
            // divider drag or activate the panel beneath the menu.
            var underClose = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
            if (!(underClose instanceof MenuBarPanel menuBarUnderClose) || menuBarUnderClose.hitChipAt(logicalX, logicalY) == null) {
                return true;
            }
        }

        if (button == 0) {
            // 1) Menu-bar chip click: open dropdown.
            var underCursor = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
            if (underCursor instanceof MenuBarPanel menuBar) {
                var chip = menuBar.hitChipAt(logicalX, logicalY);
                if (chip != null) {
                    var menu = buildMenuFor(chip, menuBar);
                    if (menu != null) {
                        openMenu = menu;
                    }
                    return true;
                }
            }

            // 2) Tab strip click: switch active, close, or arm a tab drag.
            var tabbed = findTabbedPanelAt((int) logicalX, (int) logicalY);
            if (tabbed != null && tabbed.isInTabStrip(logicalX, logicalY)) {
                var tabIdx = tabbed.hitTabAt(logicalX, logicalY);
                if (tabIdx >= 0) {
                    if (tabbed.hitCloseAt(logicalX, logicalY, tabIdx)) {
                        tabbed.removeTab(tabIdx);
                        simplifyDockTree();
                        return true;
                    }
                    tabbed.setActiveIndex(tabIdx);
                    this.tabDrag = new TabDrag(tabbed, tabIdx, tabbed.tabs().get(tabIdx), logicalX, logicalY);
                    return true;
                }
                // Click on empty tab-strip space — no-op but consume so it doesn't fall through to content.
                return true;
            }

            // 3) Panel-internal high-priority UI (scrollbar thumb, close buttons, etc.). Runs before divider so a
            // scrollbar at the right edge of a panel adjacent to a vertical dock split isn't eaten by divider drag.
            // A true return also captures subsequent drag / release for this panel — see #capturedPanel.
            var preDivider = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
            if (preDivider != null && preDivider.mouseClickedCapture(logicalX, logicalY, button)) {
                this.capturedPanel = preDivider;
                return true;
            }

            // 4) Divider drag start.
            var divider = findDivider(root, 0, 0, logicalWidth(), logicalHeight(), (int) logicalX, (int) logicalY);
            if (divider != null && isResizable(divider.split.sizing())) {
                this.activeDrag = new ActiveDrag(divider);
                return true;
            }
        }

        // 5) Otherwise, delegate to the panel under the cursor for content-area handling.
        var leaf = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);

        // Allow non-LMB capture too: the viewport claims MMB so its camera drag stays routed even when the cursor
        // leaves the viewport rect mid-stroke. LMB capture for scrollbars / edge UI is handled in step 3 above.
        if (leaf != null && button != 0 && leaf.mouseClickedCapture(logicalX, logicalY, button)) {
            this.capturedPanel = leaf;
            return true;
        }

        if (leaf != null && leaf.mouseClicked(logicalX, logicalY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Modal dialog absorbs releases so a drag started before it opened doesn't propagate to panels behind it.
        if (confirmDialog != null || captureDialog != null || layoutNameDialog != null || manageLayoutsDialog != null) {
            return true;
        }
        var logicalX = mouseX / SCALE;
        var logicalY = mouseY / SCALE;

        // SearchableSelect popup gets first crack at releases (so its scrollbar drag finishes cleanly).
        var openPopup = SearchableSelect.getOpenPopup();
        if (openPopup != null && openPopup.mouseReleased(logicalX, logicalY, button)) {
            return true;
        }
        // Color picker — end hue/SL drag.
        var openColorPopup = HslColorPickerPopup.getOpenPopup();
        if (openColorPopup != null && openColorPopup.mouseReleased(logicalX, logicalY, button)) {
            return true;
        }

        // End any text-input drag-select on LMB release. The input keeps its caret + selection; only the static
        // drag pointer clears so future drags don't keep extending its selection.
        if (button == 0 && TextInput.getDragSelecting() != null) {
            TextInput.endDragSelection();
            return true;
        }

        // Captured panel sees the release first regardless of cursor position, then the capture clears. Wrapped in
        // try/finally so a misbehaving panel can't leave us in a stuck-captured state.
        if (capturedPanel != null) {
            var panel = capturedPanel;
            this.capturedPanel = null;
            try {
                panel.mouseReleased(logicalX, logicalY, button);
            } catch (Throwable t) {
                // Swallow — release should never crash the workspace; the capture is already cleared.
            }
            return true;
        }

        if (button == 0 && tabDrag != null) {
            if (tabDrag.active) {
                completeTabDrop(tabDrag, logicalX, logicalY);
                simplifyDockTree();
            }
            tabDrag = null;
            return true;
        }

        if (button == 0 && activeDrag != null) {
            activeDrag = null;
            return true;
        }

        // Releases over an open menu shouldn't reach panels below it.
        if (openMenu != null && openMenu.isInside(logicalX, logicalY)) {
            return true;
        }

        var leaf = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
        if (leaf != null && leaf.mouseReleased(logicalX, logicalY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Modal dialog absorbs drags so divider / tab / panel drags can't continue under the dim.
        if (confirmDialog != null || captureDialog != null || layoutNameDialog != null || manageLayoutsDialog != null) {
            return true;
        }
        var logicalX = mouseX / SCALE;
        var logicalY = mouseY / SCALE;

        // SearchableSelect popup scrollbar drag.
        var openPopup = SearchableSelect.getOpenPopup();
        if (openPopup != null && openPopup.mouseDragged(logicalX, logicalY, button, deltaX, deltaY)) {
            return true;
        }
        // Color picker — drag in hue ring or S/L square continues to update the color.
        var openColorPopup = HslColorPickerPopup.getOpenPopup();
        if (openColorPopup != null && openColorPopup.mouseDragged(logicalX, logicalY, button, deltaX, deltaY)) {
            return true;
        }

        // Text-input drag-select: the focused input owns LMB drag while in progress, regardless of where the
        // cursor is now. Routed via the static dragSelecting pointer rather than per-panel forwarding so we don't
        // need every host panel to plumb mouseDragged through to its TextInput.
        var dragInput = TextInput.getDragSelecting();
        if (dragInput != null && button == 0) {
            dragInput.mouseDraggedExtend(logicalX);
            return true;
        }

        // Captured panel gets every drag event regardless of cursor position. Critical for panel-driven drags
        // (scroll thumb etc.) — without capture, the screen routes by cursor position and the drag would die the
        // moment the cursor left the panel rect.
        if (capturedPanel != null) {
            capturedPanel.mouseDragged(logicalX, logicalY, button, deltaX, deltaY);
            return true;
        }

        if (tabDrag != null) {
            if (!tabDrag.active) {
                var dx = logicalX - tabDrag.startX;
                var dy = logicalY - tabDrag.startY;
                if (dx * dx + dy * dy > TAB_DRAG_THRESHOLD_SQ) {
                    tabDrag.active = true;
                }
            }
            return true;
        }

        if (activeDrag != null) {
            applyDrag(activeDrag.divider, logicalX, logicalY);
            return true;
        }

        // Drags over an open menu shouldn't hit panels below it.
        if (openMenu != null && openMenu.isInside(logicalX, logicalY)) {
            return true;
        }

        var leaf = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
        if (leaf != null && leaf.mouseDragged(logicalX, logicalY, button, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    /**
     * Finalize a tab drag at release time. Behavior depends on the drop zone:
     * <ul>
     * <li>Tab strip or content {@code CENTER}: tab merges into the target's tab list (reorder if same source, insert
     * otherwise).</li>
     * <li>Content edge ({@code TOP/BOTTOM/LEFT/RIGHT}): the target leaf is replaced in the dock tree with a new
     * {@link DockNode.Split} containing the original target on one side and a new {@link TabbedPanel} (holding the
     * dragged tab) on the other. A subsequent {@link #simplifyDockTree()} call collapses the source if it's now
     * empty.</li>
     * </ul>
     * Cursor outside any tab panel: drag cancels, tab stays put.
     */
    private void completeTabDrop(TabDrag drag, double logicalX, double logicalY) {
        var target = findTabbedPanelAt((int) logicalX, (int) logicalY);
        if (target == null) {
            return;
        }

        var zone = target.isInTabStrip(logicalX, logicalY)
            ? DropZone.CENTER
            : computeDropZoneInContent(target, logicalX, logicalY);

        if (zone == DropZone.CENTER) {
            mergeTab(drag, target, logicalX);
            return;
        }

        // Edge drop → split the target panel.
        drag.source.removeTab(drag.sourceIndex);
        splitPanel(target, drag.tab, zone);
    }

    private static void mergeTab(TabDrag drag, TabbedPanel target, double logicalX) {
        if (target == drag.source) {
            var dropIdx = target.dropInsertionIndex(logicalX);
            if (dropIdx == drag.sourceIndex || dropIdx == drag.sourceIndex + 1) {
                return;
            }
            target.removeTab(drag.sourceIndex);
            if (dropIdx > drag.sourceIndex) {
                dropIdx--;
            }
            target.insertTab(dropIdx, drag.tab);
        } else {
            drag.source.removeTab(drag.sourceIndex);
            var dropIdx = target.dropInsertionIndex(logicalX);
            target.insertTab(dropIdx, drag.tab);
        }
    }

    /**
     * Replace {@code target}'s leaf in the dock tree with a fresh {@link DockNode.Split} containing two leaves: the
     * original target panel (now wrapped in a new leaf) on one side, and a new {@link TabbedPanel} holding
     * {@code droppedTab} on the other. Side determined by {@code zone}; default 50/50 ratio.
     */
    private void splitPanel(TabbedPanel target, Panel droppedTab, DropZone zone) {
        var existingLeaf = new DockNode.Leaf(target);
        var newLeaf = new DockNode.Leaf(new TabbedPanel(droppedTab));
        var sizing = new Sizing.Ratio(0.5f);

        var newSplit = switch (zone) {
            case TOP -> new DockNode.Split(Orientation.VERTICAL, newLeaf, existingLeaf, sizing);
            case BOTTOM -> new DockNode.Split(Orientation.VERTICAL, existingLeaf, newLeaf, sizing);
            case LEFT -> new DockNode.Split(Orientation.HORIZONTAL, newLeaf, existingLeaf, sizing);
            case RIGHT -> new DockNode.Split(Orientation.HORIZONTAL, existingLeaf, newLeaf, sizing);
            case CENTER -> throw new IllegalStateException("CENTER is not a split zone");
        };

        this.root = replaceTabbedPanel(this.root, target, newSplit);
    }

    private static DockNode replaceTabbedPanel(DockNode node, TabbedPanel target, DockNode replacement) {
        if (node instanceof DockNode.Leaf leaf && leaf.panel() == target) {
            return replacement;
        }
        if (node instanceof DockNode.Split split) {
            var first = replaceTabbedPanel(split.first(), target, replacement);
            var second = replaceTabbedPanel(split.second(), target, replacement);
            if (first == split.first() && second == split.second()) {
                return split;
            }
            return new DockNode.Split(split.orientation(), first, second, split.sizing());
        }
        return node;
    }

    private enum DropZone {
        CENTER,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    private static void applyDrag(DividerHit divider, double mouseLogicalX, double mouseLogicalY) {
        if (divider.split.orientation() == Orientation.HORIZONTAL) {
            var newBoundary = (int) Math.round(mouseLogicalX - divider.parentX);
            var clamped = Math.max(MIN_PANEL_SIZE_PX, Math.min(divider.parentWidth - MIN_PANEL_SIZE_PX, newBoundary));
            updateSizing(divider.split.sizing(), clamped, divider.parentWidth);
        } else {
            var newBoundary = (int) Math.round(mouseLogicalY - divider.parentY);
            var clamped = Math.max(MIN_PANEL_SIZE_PX, Math.min(divider.parentHeight - MIN_PANEL_SIZE_PX, newBoundary));
            updateSizing(divider.split.sizing(), clamped, divider.parentHeight);
        }
    }

    private static void updateSizing(Sizing sizing, int newBoundary, int parentSize) {
        switch (sizing) {
            case Sizing.Ratio r -> r.value = Math.max(0.01f, Math.min(0.99f, (float) newBoundary / parentSize));
            case Sizing.FirstFixed f -> f.pixels = newBoundary;
            case Sizing.SecondFixed s -> s.pixels = parentSize - newBoundary;
        }
    }

    private static boolean isResizable(Sizing sizing) {
        return true;
    }

    private static @Nullable DividerHit findDivider(DockNode node, int x, int y, int width, int height, int mouseX, int mouseY) {
        if (!(node instanceof DockNode.Split split)) {
            return null;
        }

        if (split.orientation() == Orientation.HORIZONTAL) {
            var firstWidth = DockNode.boundary(split.sizing(), width);
            var boundaryX = x + firstWidth;
            if (Math.abs(mouseX - boundaryX) <= DIVIDER_HIT_PX && mouseY >= y && mouseY < y + height) {
                return new DividerHit(split, x, y, width, height);
            }
            var inFirst = findDivider(split.first(), x, y, firstWidth, height, mouseX, mouseY);
            if (inFirst != null) {
                return inFirst;
            }
            return findDivider(split.second(), x + firstWidth, y, width - firstWidth, height, mouseX, mouseY);
        } else {
            var firstHeight = DockNode.boundary(split.sizing(), height);
            var boundaryY = y + firstHeight;
            if (Math.abs(mouseY - boundaryY) <= DIVIDER_HIT_PX && mouseX >= x && mouseX < x + width) {
                return new DividerHit(split, x, y, width, height);
            }
            var inFirst = findDivider(split.first(), x, y, width, firstHeight, mouseX, mouseY);
            if (inFirst != null) {
                return inFirst;
            }
            return findDivider(split.second(), x, y + firstHeight, width, height - firstHeight, mouseX, mouseY);
        }
    }

    private static @Nullable Panel panelAt(DockNode node, int x, int y, int width, int height, double mouseX, double mouseY) {
        return switch (node) {
            case DockNode.Leaf leaf -> {
                if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
                    yield leaf.panel();
                }
                yield null;
            }
            case DockNode.Split split -> {
                if (split.orientation() == Orientation.HORIZONTAL) {
                    var firstWidth = DockNode.boundary(split.sizing(), width);
                    if (mouseX < x + firstWidth) {
                        yield panelAt(split.first(), x, y, firstWidth, height, mouseX, mouseY);
                    }
                    yield panelAt(split.second(), x + firstWidth, y, width - firstWidth, height, mouseX, mouseY);
                } else {
                    var firstHeight = DockNode.boundary(split.sizing(), height);
                    if (mouseY < y + firstHeight) {
                        yield panelAt(split.first(), x, y, width, firstHeight, mouseX, mouseY);
                    }
                    yield panelAt(split.second(), x, y + firstHeight, width, height - firstHeight, mouseX, mouseY);
                }
            }
        };
    }

    private @Nullable TabbedPanel findTabbedPanelAt(int mouseX, int mouseY) {
        var leaf = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), mouseX, mouseY);
        return leaf instanceof TabbedPanel tp ? tp : null;
    }

    /**
     * Build the dropdown menu for the clicked menu-bar chip. Anchored just below the chip's screen rect. Returns
     * {@code null} for chips that don't have menus implemented yet (so Edit / View are inert no-ops for now).
     */
    private @Nullable DropdownMenu buildMenuFor(String chipName, MenuBarPanel menuBar) {
        var chipRect = menuBar.chipRect(chipName);
        if (chipRect == null) {
            return null;
        }
        var anchorX = chipRect.x();
        var anchorY = chipRect.y() + chipRect.height() + 1;

        return switch (chipName) {
            case MenuBarPanel.CHIP_FILE -> buildFileMenu(anchorX, anchorY);
            case MenuBarPanel.CHIP_VIEW -> buildViewMenu(anchorX, anchorY);
            case MenuBarPanel.CHIP_WINDOW -> buildWindowMenu(anchorX, anchorY);
            case MenuBarPanel.CHIP_LAYOUT -> buildLayoutMenu(anchorX, anchorY);
            default -> null;
        };
    }

    /**
     * View dropdown — toggles that affect what's drawn in the viewport without changing project state. Items use a
     * leading "✓" prefix when on / blank prefix when off ({@link DropdownMenu} doesn't have a checkbox UI, so the
     * label-prefix idiom is the cheapest way to convey toggle state).
     */
    private DropdownMenu buildViewMenu(int anchorX, int anchorY) {
        var items = new java.util.ArrayList<DropdownMenu.Item>();
        var territoryOn = com.blib.engine.territory.ClaimPaintTool.isOverlayVisible();
        items.add(
            new DropdownMenu.Item(
                (territoryOn ? "✓ " : "   ") + "Show Territory Claims",
                com.blib.engine.territory.ClaimPaintTool::toggleOverlayVisible
            )
        );
        return new DropdownMenu(anchorX, anchorY, items);
    }

    /**
     * Build the WINDOW dropdown by iterating {@link PanelRegistry} — every body-eligible panel gets a "Reopen <title>"
     * entry where {@code <title>} comes from the panel's own {@link Panel#title()} method. This is the single
     * source-of-truth fix for menu / tab label drift: adding a new panel to {@link PanelRegistry} automatically adds it
     * here, and renaming a panel's title automatically updates the menu label since both paths read the same string.
     * <p>
     * Each menu item's reopen-factory routes through {@link PanelRegistry#create} so panels with constructor args
     * (viewport's right-click handler, content-browser's confirm handler) get wired correctly without per-panel manual
     * factory closures.
     */
    private DropdownMenu buildWindowMenu(int anchorX, int anchorY) {
        var items = new java.util.ArrayList<DropdownMenu.Item>();
        for (var id : PanelRegistry.orderedIds()) {
            var sample = PanelRegistry.create(id, panelCtx());
            if (sample == null) {
                continue;
            }
            var displayName = sample.title();
            var panelClass = sample.getClass();
            items.add(
                new DropdownMenu.Item(
                    "Reopen " + displayName,
                    () -> reopenPanel(panelClass, () -> PanelRegistry.create(id, panelCtx()))
                )
            );
        }
        items.add(new DropdownMenu.Item("Reset Layout", this::resetLayout));
        return new DropdownMenu(anchorX, anchorY, items);
    }

    /**
     * If a panel of {@code panelClass} already exists somewhere in the dock tree, switch its containing
     * {@link TabbedPanel} to that tab and return. Otherwise create a fresh instance via {@code factory} and append it
     * as a tab in the first {@link TabbedPanel} found via depth-first walk. If no {@code TabbedPanel} exists at all
     * (the user has closed everything), the action is a no-op and the user can use Reset Layout to recover.
     */
    private void reopenPanel(Class<? extends Panel> panelClass, java.util.function.Supplier<Panel> factory) {
        var existingOwner = findOwnerWithPanelOfType(this.root, panelClass);
        if (existingOwner != null) {
            for (var i = 0; i < existingOwner.tabCount(); i++) {
                if (panelClass.isInstance(existingOwner.tabs().get(i))) {
                    existingOwner.setActiveIndex(i);
                    return;
                }
            }
        }
        var first = findFirstTabbedPanel(this.root);
        if (first != null) {
            first.insertTab(first.tabCount(), factory.get());
        }
    }

    private void resetLayout() {
        // Reset semantics:
        // - Built-in template id → rebuild from the canonical code-baked LayoutTemplate.toDoc().
        // - User layout with a templateBase → rebuild from that template's body but keep the user's id/displayName.
        // - User layout with no templateBase → reload from disk (discards in-memory edits since last save).
        // The reset is then persisted so the user's choice is durable.
        var current = LayoutCatalog.get(activeLayoutId);
        BodyNodeAndDoc next = computeResetBody(current);
        if (next == null) {
            return;
        }
        var bodyRoot = LayoutSnapshot.hydrate(next.body, panelCtx());
        this.root = buildOuterLayout(bodyRoot);
        try {
            LayoutCatalog.save(next.docToWrite);
        } catch (java.io.IOException e) {
            org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class)
                .warn("[BLib] resetLayout: failed to persist reset", e);
        }
    }

    private BodyNodeAndDoc computeResetBody(@Nullable LayoutDoc current) {
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
        // No template baseline: reload from disk to drop in-memory edits since last save. Returning null signals the
        // caller to no-op when there's nothing on disk to reload from.
        if (current == null) {
            return null;
        }
        return new BodyNodeAndDoc(current.body(), current);
    }

    private record BodyNodeAndDoc(
        com.blib.engine.layout.BodyNode body,
        LayoutDoc docToWrite
    ) {}

    /**
     * LAYOUT menu — full create/manage flow. Top section lists every layout (•-prefixed for active), separator, then
     * Save-As / Rename / Duplicate / Delete / Reset, separator, "New from <Template>…" entries (one per template,
     * inline since {@link DropdownMenu} doesn't support submenus), then Manage Layouts… and Show Layouts Folder.
     */
    private DropdownMenu buildLayoutMenu(int anchorX, int anchorY) {
        var items = new java.util.ArrayList<DropdownMenu.Item>();
        for (var doc : LayoutCatalog.listAll()) {
            var prefix = doc.id().equals(activeLayoutId) ? "• " : "  ";
            var suffix = LayoutCatalog.isTemplateId(doc.id()) ? "  (template)" : "";
            items.add(new DropdownMenu.Item(prefix + doc.displayName() + suffix, () -> switchLayout(doc.id())));
        }
        items.add(new DropdownMenu.Item("────────────", () -> {}));
        items.add(new DropdownMenu.Item("Save As New…", this::openSaveAsDialog));
        items.add(new DropdownMenu.Item("Rename…", this::openRenameDialog));
        items.add(new DropdownMenu.Item("Duplicate…", this::openDuplicateDialog));
        items.add(new DropdownMenu.Item("Delete…", this::openDeleteConfirm));
        var current = LayoutCatalog.get(activeLayoutId);
        var canReset = LayoutCatalog.isTemplateId(activeLayoutId)
            || (current != null && current.templateBase() != null);
        if (canReset) {
            items.add(new DropdownMenu.Item("Reset to Template", this::resetLayout));
        }
        items.add(new DropdownMenu.Item("────────────", () -> {}));
        for (var t : LayoutTemplate.all()) {
            items.add(new DropdownMenu.Item("New from " + t.displayName() + "…", () -> openNewFromTemplateDialog(t)));
        }
        items.add(new DropdownMenu.Item("────────────", () -> {}));
        items.add(new DropdownMenu.Item("Manage Layouts…", this::openManageLayoutsDialog));
        items.add(new DropdownMenu.Item("Show Layouts Folder", this::openLayoutsFolder));
        return new DropdownMenu(anchorX, anchorY, items);
    }

    private void openSaveAsDialog() {
        this.layoutNameDialog = new LayoutNameDialog(
            LayoutNameDialog.Mode.SAVE_AS,
            "",
            LayoutCatalog::idAvailable,
            displayName -> {
                var newId = LayoutCatalog.suggestId(displayName);
                var capturedBody = LayoutSnapshot.capture(extractBodyRoot(this.root));
                var now = java.time.Instant.now().toString();
                var templateBase = LayoutCatalog.isTemplateId(activeLayoutId) ? activeLayoutId : null;
                var doc = new LayoutDoc(LayoutDoc.CURRENT_VERSION, newId, displayName, templateBase, now, now, capturedBody);
                try {
                    LayoutCatalog.save(doc);
                    activeLayoutId = newId;
                    persistActiveSelection();
                } catch (java.io.IOException e) {
                    org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Save As failed", e);
                }
                this.layoutNameDialog = null;
            },
            () -> this.layoutNameDialog = null
        );
    }

    private void openRenameDialog() {
        var current = LayoutCatalog.get(activeLayoutId);
        if (current == null) {
            return;
        }
        // Rename can collide on its own id (no-op rename → keep the existing); availability check excludes the
        // current id so the user can confirm without an "already exists" complaint when they only edited casing.
        var currentId = activeLayoutId;
        this.layoutNameDialog = new LayoutNameDialog(
            LayoutNameDialog.Mode.RENAME,
            current.displayName(),
            id -> id.equals(currentId) || LayoutCatalog.idAvailable(id),
            displayName -> {
                var newId = LayoutCatalog.suggestId(displayName);
                var renamed = current.withDisplayName(displayName);
                try {
                    if (newId.equals(currentId)) {
                        // Display-name-only change.
                        LayoutCatalog.save(renamed);
                    } else {
                        // Id change → write under the new id, then delete the old file.
                        LayoutCatalog.save(renamed.withId(newId));
                        LayoutCatalog.delete(currentId);
                        // Rewire active-state references from old id → new id.
                        var state = ActiveLayoutState.read();
                        ActiveLayoutState.write(state.withoutLayout(currentId));
                        activeLayoutId = newId;
                        persistActiveSelection();
                    }
                } catch (java.io.IOException e) {
                    org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Rename failed", e);
                }
                if (manageLayoutsDialog != null) {
                    manageLayoutsDialog.setActiveLayoutId(activeLayoutId);
                    manageLayoutsDialog.refresh();
                }
                this.layoutNameDialog = null;
            },
            () -> this.layoutNameDialog = null
        );
    }

    private void openDuplicateDialog() {
        var current = LayoutCatalog.get(activeLayoutId);
        if (current == null) {
            return;
        }
        this.layoutNameDialog = new LayoutNameDialog(
            LayoutNameDialog.Mode.DUPLICATE,
            current.displayName() + " Copy",
            LayoutCatalog::idAvailable,
            displayName -> {
                var newId = LayoutCatalog.suggestId(displayName);
                var now = java.time.Instant.now().toString();
                var capturedBody = LayoutSnapshot.capture(extractBodyRoot(this.root));
                var copy = new LayoutDoc(
                    LayoutDoc.CURRENT_VERSION,
                    newId,
                    displayName,
                    current.templateBase(),
                    now,
                    now,
                    capturedBody
                );
                try {
                    LayoutCatalog.save(copy);
                    activeLayoutId = newId;
                    persistActiveSelection();
                } catch (java.io.IOException e) {
                    org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Duplicate failed", e);
                }
                if (manageLayoutsDialog != null) {
                    manageLayoutsDialog.setActiveLayoutId(activeLayoutId);
                    manageLayoutsDialog.refresh();
                }
                this.layoutNameDialog = null;
            },
            () -> this.layoutNameDialog = null
        );
    }

    private void openDeleteConfirm() {
        var current = LayoutCatalog.get(activeLayoutId);
        if (current == null) {
            return;
        }
        var isTemplate = LayoutCatalog.isTemplateId(activeLayoutId);
        var message = isTemplate
            ? "Delete '" + current.displayName()
                + "'? This is a template — it will be re-seeded with default content next time the workspace opens."
            : "Delete '" + current.displayName() + "'? This cannot be undone.";
        var deletedId = activeLayoutId;
        this.confirmDialog = new ConfirmDialog(
            "Delete layout?",
            message,
            "Delete",
            "Cancel",
            true,
            () -> {
                try {
                    LayoutCatalog.delete(deletedId);
                } catch (java.io.IOException e) {
                    org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Delete failed", e);
                }
                var state = ActiveLayoutState.read();
                ActiveLayoutState.write(state.withoutLayout(deletedId));
                // Switch off the deleted layout to whatever resolves now (default fallback if nothing else).
                var newId = ActiveLayoutState.resolve(
                    ProjectSession.activeProject() != null ? ProjectSession.activeProjectName() : null,
                    ActiveLayoutState.read()
                );
                activeLayoutId = LayoutTemplate.DEFAULT.id(); // Force the equality check in switchLayout to fire.
                switchLayout(newId);
                if (manageLayoutsDialog != null) {
                    manageLayoutsDialog.setActiveLayoutId(activeLayoutId);
                    manageLayoutsDialog.refresh();
                }
            },
            () -> {}
        );
    }

    private void openNewFromTemplateDialog(LayoutTemplate template) {
        this.layoutNameDialog = new LayoutNameDialog(
            LayoutNameDialog.Mode.NEW_FROM_TEMPLATE,
            template.displayName() + " (custom)",
            LayoutCatalog::idAvailable,
            displayName -> {
                var newId = LayoutCatalog.suggestId(displayName);
                var now = java.time.Instant.now().toString();
                var doc = new LayoutDoc(
                    LayoutDoc.CURRENT_VERSION,
                    newId,
                    displayName,
                    template.id(),
                    now,
                    now,
                    template.toDoc().body()
                );
                try {
                    LayoutCatalog.save(doc);
                    persistOutgoingLayout();
                    activeLayoutId = newId;
                    var loaded = LayoutCatalog.get(newId);
                    var bodyRoot = LayoutSnapshot.hydrate(
                        loaded != null ? loaded.body() : doc.body(),
                        panelCtx()
                    );
                    this.root = buildOuterLayout(bodyRoot);
                    persistActiveSelection();
                } catch (java.io.IOException e) {
                    org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] New from Template failed", e);
                }
                if (manageLayoutsDialog != null) {
                    manageLayoutsDialog.setActiveLayoutId(activeLayoutId);
                    manageLayoutsDialog.refresh();
                }
                this.layoutNameDialog = null;
            },
            () -> this.layoutNameDialog = null
        );
    }

    private void openManageLayoutsDialog() {
        this.manageLayoutsDialog = new ManageLayoutsDialog(
            activeLayoutId,
            () -> this.manageLayoutsDialog = null,
            doc -> switchLayout(doc.id()),
            this::renameFromManage,
            this::duplicateFromManage,
            this::deleteFromManage
        );
    }

    private void openLayoutsFolder() {
        try {
            com.blib.engine.layout.LayoutStorage.ensureRootExists();
        } catch (java.io.IOException ignored) {
            // Best-effort — openUri below will fail loudly if the directory still isn't reachable.
        }
        net.minecraft.Util.getPlatform().openUri(com.blib.engine.layout.LayoutStorage.layoutsRoot().toUri());
    }

    private void renameFromManage(LayoutDoc doc) {
        var existingId = doc.id();
        this.layoutNameDialog = new LayoutNameDialog(
            LayoutNameDialog.Mode.RENAME,
            doc.displayName(),
            id -> id.equals(existingId) || LayoutCatalog.idAvailable(id),
            displayName -> {
                var newId = LayoutCatalog.suggestId(displayName);
                try {
                    if (newId.equals(existingId)) {
                        LayoutCatalog.save(doc.withDisplayName(displayName));
                    } else {
                        LayoutCatalog.save(doc.withDisplayName(displayName).withId(newId));
                        LayoutCatalog.delete(existingId);
                        var state = ActiveLayoutState.read();
                        ActiveLayoutState.write(state.withoutLayout(existingId));
                        if (existingId.equals(activeLayoutId)) {
                            activeLayoutId = newId;
                            persistActiveSelection();
                        }
                    }
                } catch (java.io.IOException e) {
                    org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Rename (manage) failed", e);
                }
                if (manageLayoutsDialog != null) {
                    manageLayoutsDialog.setActiveLayoutId(activeLayoutId);
                    manageLayoutsDialog.refresh();
                }
                this.layoutNameDialog = null;
            },
            () -> this.layoutNameDialog = null
        );
    }

    private void duplicateFromManage(LayoutDoc doc) {
        this.layoutNameDialog = new LayoutNameDialog(
            LayoutNameDialog.Mode.DUPLICATE,
            doc.displayName() + " Copy",
            LayoutCatalog::idAvailable,
            displayName -> {
                var newId = LayoutCatalog.suggestId(displayName);
                var now = java.time.Instant.now().toString();
                var copy = new LayoutDoc(
                    LayoutDoc.CURRENT_VERSION,
                    newId,
                    displayName,
                    doc.templateBase(),
                    now,
                    now,
                    doc.body()
                );
                try {
                    LayoutCatalog.save(copy);
                } catch (java.io.IOException e) {
                    org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Duplicate (manage) failed", e);
                }
                if (manageLayoutsDialog != null) {
                    manageLayoutsDialog.refresh();
                }
                this.layoutNameDialog = null;
            },
            () -> this.layoutNameDialog = null
        );
    }

    private void deleteFromManage(LayoutDoc doc) {
        var deletedId = doc.id();
        var isTemplate = LayoutCatalog.isTemplateId(deletedId);
        var message = isTemplate
            ? "Delete '" + doc.displayName()
                + "'? This is a template — it will be re-seeded with default content next time the workspace opens."
            : "Delete '" + doc.displayName() + "'? This cannot be undone.";
        this.confirmDialog = new ConfirmDialog(
            "Delete layout?",
            message,
            "Delete",
            "Cancel",
            true,
            () -> {
                try {
                    LayoutCatalog.delete(deletedId);
                } catch (java.io.IOException e) {
                    org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Delete (manage) failed", e);
                }
                var state = ActiveLayoutState.read();
                ActiveLayoutState.write(state.withoutLayout(deletedId));
                if (deletedId.equals(activeLayoutId)) {
                    var newId = ActiveLayoutState.resolve(
                        ProjectSession.activeProject() != null ? ProjectSession.activeProjectName() : null,
                        ActiveLayoutState.read()
                    );
                    activeLayoutId = LayoutTemplate.DEFAULT.id();
                    switchLayout(newId);
                }
                if (manageLayoutsDialog != null) {
                    manageLayoutsDialog.setActiveLayoutId(activeLayoutId);
                    manageLayoutsDialog.refresh();
                }
            },
            () -> {}
        );
    }

    /**
     * FILE menu — project management entry points. "New Project" / "Open Project" close the workspace and open the
     * picker (the workspace's removed() clears ProjectSession; the picker's onConfirmedOpen rebuilds the workspace
     * after a successful Open). "Reload Project" / "Delete Project" act on the active project; both are inert when no
     * project is active (which shouldn't happen post-picker-gating but is defensive).
     */
    private DropdownMenu buildFileMenu(int anchorX, int anchorY) {
        var hasProject = ProjectSession.activeProject() != null;
        var items = new java.util.ArrayList<DropdownMenu.Item>();
        items.add(new DropdownMenu.Item("New Project…", () -> openPicker(true)));
        items.add(new DropdownMenu.Item("Open Project…", () -> openPicker(false)));
        items.add(
            new DropdownMenu.Item(hasProject ? "Reload Project" : "Reload Project (no project)", () -> {
                if (!hasProject) {
                    return;
                }
                BLib.MOD.networking().sendToServer(new C2SReloadProjectPayload(ProjectSession.activeProjectName()));
            })
        );
        items.add(
            new DropdownMenu.Item(hasProject ? "Delete Project…" : "Delete Project… (no project)", () -> {
                if (!hasProject) {
                    return;
                }
                // Destructive — gate behind the modal confirm. On confirm we fire the delete and bounce back to
                // the picker (the workspace is meaningless once its project disappears, and the picker will
                // refresh its list when the S2CProjectListPayload arrives).
                var name = ProjectSession.activeProjectName();
                this.confirmDialog = new ConfirmDialog(
                    "Delete project?",
                    "Are you sure you want to delete '" + name + "'? This will remove the entire datapack folder "
                        + "from the world's datapacks directory and cannot be undone.",
                    "Delete",
                    "Cancel",
                    true,
                    () -> {
                        BLib.MOD.networking().sendToServer(new C2SDeleteProjectPayload(name));
                        openPicker(false);
                    },
                    () -> {}
                );
            })
        );
        return new DropdownMenu(anchorX, anchorY, items);
    }

    private void openPicker(boolean createMode) {
        Minecraft.getInstance().setScreen(new ProjectPickerScreen(EngineWorkspaceScreen::reopenWorkspace, createMode));
    }

    /** Used as the {@link ProjectPickerScreen} {@code onConfirmedOpen} callback so the picker can re-enter us. */
    private static void reopenWorkspace() {
        Minecraft.getInstance().setScreen(new EngineWorkspaceScreen());
    }

    /**
     * Right-click in the viewport — opens a context menu at the cursor anchored as a {@link DropdownMenu}. When the
     * cursor was over a living entity, items include "View GOAP Details" (dispatches a {@link C2SGOAPTrackPayload} and
     * opens the {@link GOAPDetailsPanel}) and "Delete Entity" (dispatches a {@link C2SRemoveEntityPayload}). The delete
     * option is hidden for players since deleting other players via this menu would be inappropriate; the server-side
     * handler also rejects player targets as a safety net. Empty-space right-clicks just close any existing menu.
     */
    /**
     * Builds the viewport's right-click handler. Anonymous class rather than method reference because
     * {@link ViewportPanel.RightClickHandler} now has a second method ({@code onRightClickVolume}) the screen needs to
     * override. Bound to {@code this} so both callbacks dispatch to the screen's instance methods.
     */
    private ViewportPanel.RightClickHandler buildViewportRightClickHandler() {
        return new ViewportPanel.RightClickHandler() {

            @Override
            public void onRightClick(@Nullable LivingEntity entity, double cursorX, double cursorY) {
                onViewportRightClick(entity, cursorX, cursorY);
            }

            @Override
            public void onRightClickVolume(double cursorX, double cursorY) {
                onViewportRightClickVolume(cursorX, cursorY);
            }
        };
    }

    private void onViewportRightClick(@Nullable LivingEntity entity, double cursorX, double cursorY) {
        if (entity == null) {
            this.openMenu = null;
            return;
        }

        var entityId = entity.getId();
        var entityUuid = entity.getUUID();
        var entityDisplayName = entity.getName().getString();
        var menuX = (int) cursorX;
        var menuY = (int) cursorY;
        var items = new java.util.ArrayList<DropdownMenu.Item>();
        items.add(
            new DropdownMenu.Item("View GOAP Details", () -> {
                BLib.MOD.networking().sendToServer(new C2SGOAPTrackPayload(entityId));
                reopenPanel(GOAPDetailsPanel.class, GOAPDetailsPanel::new);
            })
        );
        items.add(
            new DropdownMenu.Item("Manage Factions", () -> {
                // Anchor the popup at the original right-click point — by the time the menu item fires, the menu
                // itself has been dismissed, but the user expects the popup to land where their click was.
                FactionManagePopup.openAt(menuX, menuY, entityUuid, entityDisplayName);
            })
        );
        if (!(entity instanceof net.minecraft.world.entity.player.Player)) {
            items.add(
                new DropdownMenu.Item("Delete Entity", () -> {
                    BLib.MOD.networking().sendToServer(new C2SRemoveEntityPayload(entityId));
                })
            );
        }

        this.openMenu = new DropdownMenu(menuX, menuY, items);
    }

    /**
     * Right-click on the block-volume selection. Opens a context menu with the operations the old Selection panel
     * surfaced inline: Capture (opens a dialog), Cut, Copy, Paste, Delete. Ops are non-destructive when their
     * preconditions aren't met (BlockSelectionOps self-gates), so disabled-state rendering isn't strictly necessary for
     * v1 — clicking a no-op item just does nothing.
     */
    private void onViewportRightClickVolume(double cursorX, double cursorY) {
        var items = new java.util.ArrayList<DropdownMenu.Item>();
        items.add(
            new DropdownMenu.Item("Capture…", () -> {
                this.captureDialog = new CaptureDialog(() -> this.captureDialog = null);
            })
        );
        items.add(new DropdownMenu.Item("Cut", () -> com.blib.engine.blockselection.BlockSelectionOps.copy(true)));
        items.add(new DropdownMenu.Item("Copy", () -> com.blib.engine.blockselection.BlockSelectionOps.copy(false)));
        items.add(new DropdownMenu.Item("Paste", () -> com.blib.engine.blockselection.BlockSelectionOps.paste()));
        items.add(new DropdownMenu.Item("Delete", () -> com.blib.engine.blockselection.BlockSelectionOps.delete()));
        this.openMenu = new DropdownMenu((int) cursorX, (int) cursorY, items);
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

    /**
     * After a tab close or move, walk the dock tree and replace any {@link DockNode.Split} whose child is an empty
     * {@link TabbedPanel} with the non-empty sibling, so the surviving panel grows into the freed space. Trim leaves
     * (menu / toolbar / status) are never considered empty so they're preserved. If both children of a split are empty
     * (degenerate state, e.g. the user closed every tab), the first child is kept arbitrarily.
     */
    private void simplifyDockTree() {
        this.root = simplify(this.root);
    }

    private static DockNode simplify(DockNode node) {
        if (!(node instanceof DockNode.Split split)) {
            return node;
        }
        var first = simplify(split.first());
        var second = simplify(split.second());
        var firstEmpty = isEmptyTabbedPanel(first);
        var secondEmpty = isEmptyTabbedPanel(second);

        if (firstEmpty && !secondEmpty) {
            return second;
        }
        if (secondEmpty && !firstEmpty) {
            return first;
        }
        if (firstEmpty && secondEmpty) {
            return first;
        }
        if (first == split.first() && second == split.second()) {
            return split;
        }
        return new DockNode.Split(split.orientation(), first, second, split.sizing());
    }

    private static boolean isEmptyTabbedPanel(DockNode node) {
        return node instanceof DockNode.Leaf leaf
            && leaf.panel() instanceof TabbedPanel tp
            && tp.tabCount() == 0;
    }

    private int logicalWidth() {
        return (int) (this.width / SCALE);
    }

    private int logicalHeight() {
        return (int) (this.height / SCALE);
    }

    private record DividerHit(
        DockNode.Split split,
        int parentX,
        int parentY,
        int parentWidth,
        int parentHeight
    ) {

        int boundaryStartX() {
            if (split.orientation() == Orientation.HORIZONTAL) {
                return parentX + DockNode.boundary(split.sizing(), parentWidth);
            }
            return parentX;
        }

        int boundaryStartY() {
            if (split.orientation() == Orientation.VERTICAL) {
                return parentY + DockNode.boundary(split.sizing(), parentHeight);
            }
            return parentY;
        }
    }

    private record ActiveDrag(DividerHit divider) {}

    /**
     * Mutable tab-drag state. Created on tab press; turns {@link #active} once the cursor moves past the threshold;
     * cleared on release. The tab itself stays in {@link #source} during the drag — only on a successful drop does the
     * source actually lose it.
     */
    private static final class TabDrag {

        final TabbedPanel source;

        final int sourceIndex;

        final Panel tab;

        final double startX;

        final double startY;

        boolean active;

        TabDrag(TabbedPanel source, int sourceIndex, Panel tab, double startX, double startY) {
            this.source = source;
            this.sourceIndex = sourceIndex;
            this.tab = tab;
            this.startX = startX;
            this.startY = startY;
        }
    }
}
