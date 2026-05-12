package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.dismemberment.v1.Dismemberable;
import com.blib.api.common.dismemberment.v1.LimbDismemberer;
import com.blib.engine.blockselection.BlockSelection;
import com.blib.engine.blockselection.BlockSelectionOps;
import com.blib.engine.input.ActiveKeybindings;
import com.blib.engine.input.KeybindingProfile;
import com.blib.engine.input.KeybindingProfileCatalog;
import com.blib.engine.input.Keybindings;
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
import com.blib.engine.modeler.ModelerFilePicker;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.ModelerSceneLoader;
import com.blib.engine.selection.SelectionManager;
import com.blib.engine.session.EngineMode;
import com.blib.engine.session.NavigationMode;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.spawn.EntitySpawnSelection;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SDeleteProjectPayload;
import com.blib.mod.common.network.packet.C2SDismemberAllLimbsPayload;
import com.blib.mod.common.network.packet.C2SDismemberLimbPayload;
import com.blib.mod.common.network.packet.C2SGOAPTrackPayload;
import com.blib.mod.common.network.packet.C2SRedoActionPayload;
import com.blib.mod.common.network.packet.C2SReloadProjectPayload;
import com.blib.mod.common.network.packet.C2SRemoveEntityPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionDirectoryPayload;
import com.blib.mod.common.network.packet.C2SUndoActionPayload;

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
     * the boundary line is treated as a divider drag-start. Tuned to {@code 2} so the 5-pixel hit zone exactly brackets
     * the 4-pixel visible highlight stripe ({@code boundary ± 2}) plus 1 slop pixel — clicks anywhere on the highlight
     * reliably start a drag, and the slop swallows sub-pixel cursor jitter at the boundary.
     * <p>
     * Made public so panels with no edge UI (e.g. {@link ViewportPanel}) can yield clicks in the divider band.
     */
    public static final int DIVIDER_HIT_PX = 2;

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

    /**
     * Workspace execution mode. {@link Mode#IN_GAME} is the legacy {@code /blib engine} flow, opened inside a world via
     * the project picker. {@link Mode#MENU_OVERLAY} is the TitleScreen-launched flow — no world / project / server is
     * required, and the workspace persists on top of menus, rendering whichever screen the user is "on" inside its
     * viewport panel via {@link #wrappedScreen}.
     */
    public enum Mode {
        IN_GAME,
        MENU_OVERLAY
    }

    private final Mode mode;

    /**
     * Menu-overlay mode only. The {@link Screen} being shown inside the viewport panel — initially the TitleScreen
     * passed at construction, then swapped each time the wrapped screen calls {@code Minecraft.setScreen} (intercepted
     * by {@code MixinMinecraft_EngineScreenRedirect}). Null in {@link Mode#IN_GAME} mode and once a world has loaded.
     */
    private @Nullable Screen wrappedScreen;

    /**
     * Set true around the explicit close path ({@link #closeEngine}) so the setScreen redirect mixin lets the call pass
     * through to vanilla rather than re-wrapping the new screen.
     */
    private static volatile boolean preparingToClose;

    /**
     * Tracks whether {@link #wrappedScreen}'s {@code added()} / {@code init()} lifecycle has fired since it became the
     * wrapped screen. Engine's {@code init()} is re-invoked by vanilla on every window resize (via
     * {@code Screen.rebuildWidgets}), so without a guard we'd fire {@code added()} every resize — some screens treat
     * that as a fresh activation and reload async state, etc. {@link #setWrappedScreen} resets this on swap, and the
     * resize path forwards to the wrapped screen directly without going back through {@code added()}.
     */
    private boolean wrappedScreenAdded;

    /**
     * Set true around the wrapped screen's render pass so {@code MixinScreen_EngineSkipBlur} can detect it and skip
     * {@link net.minecraft.client.gui.screens.Screen#renderBlurredBackground} — that vanilla method ends with
     * {@code mainRT.bindWrite(false)}, which silently swaps our offscreen RT out from under the wrapped screen mid
     * render and sends every widget / text draw to the main RT instead. Skipping is fine because the blur targets the
     * main RT, which we don't composit; the panorama backdrop already serves as the menu-mode visual.
     */
    private static volatile boolean inWrappedScreenRender;

    /** Read by {@code MixinScreen_EngineSkipBlur}. True while {@link #render} is mid-wrappedScreen-pass. */
    public static boolean isInWrappedScreenRender() {
        return inWrappedScreenRender;
    }

    private DockNode root;

    private @Nullable ActiveDrag activeDrag;

    private @Nullable TabDrag tabDrag;

    private @Nullable DropdownMenu openMenu;

    /**
     * One-level cascading submenu of {@link #openMenu} (e.g. the Dismember… → limb list on entity right-click). Only
     * non-null while {@link #openMenu} is also non-null, and always cleared in lock-step with it so a stale child can't
     * outlive its parent. We only support a single level of nesting — nothing in the workspace UI needs deeper.
     */
    private @Nullable DropdownMenu openSubmenu;

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
     * Modal preferences dialog (keybinding editor). Same lifecycle as the others. {@link LayoutNameDialog} can stack on
     * top when the user creates / renames / duplicates a profile.
     */
    private @Nullable PreferencesDialog preferencesDialog;

    // ---------------------------------------------------------------------------------------------
    // Modal z-order tracking
    //
    // Every modal dialog field above is also tracked in {@link #modalOrder} — an ordered list of tags representing the
    // open-time sequence so render + input dispatch can route to the topmost one. Without this, opening a confirm
    // sub-dialog from a parent modal (e.g. Delete Profile from PreferencesDialog) renders the confirm UNDERNEATH the
    // parent because the parent's render check fires later in the static if-chain.
    //
    // We don't require open/close sites to touch {@code modalOrder} explicitly — {@link #syncModalOrder} runs at the
    // top of every render and diffs the field state against the last-known state. Newly-set fields get appended to the
    // top of the stack; newly-cleared fields get removed. Re-opening a closed modal pushes it to the top again. This
    // means callers can keep doing {@code this.fooDialog = new FooDialog(...)} / {@code = null} without coupling to
    // the stack.
    // ---------------------------------------------------------------------------------------------

    private static final String MODAL_CONFIRM = "confirm";

    private static final String MODAL_CAPTURE = "capture";

    private static final String MODAL_LAYOUT_NAME = "layoutName";

    private static final String MODAL_MANAGE_LAYOUTS = "manageLayouts";

    private static final String MODAL_PREFERENCES = "preferences";

    private final java.util.List<String> modalOrder = new java.util.ArrayList<>();

    private boolean lastConfirmOpen;

    private boolean lastCaptureOpen;

    private boolean lastLayoutNameOpen;

    private boolean lastManageLayoutsOpen;

    private boolean lastPreferencesOpen;

    /**
     * Reconcile {@link #modalOrder} with the current dialog-field state. Call at the top of every render frame and
     * before any input-dispatch pass that consults the stack — both check for transitions since the last call and
     * append newly-opened modals to the top of the stack, or remove newly-closed ones.
     */
    private void syncModalOrder() {
        syncOne(MODAL_CONFIRM, confirmDialog != null, lastConfirmOpen);
        lastConfirmOpen = confirmDialog != null;
        syncOne(MODAL_CAPTURE, captureDialog != null, lastCaptureOpen);
        lastCaptureOpen = captureDialog != null;
        syncOne(MODAL_LAYOUT_NAME, layoutNameDialog != null, lastLayoutNameOpen);
        lastLayoutNameOpen = layoutNameDialog != null;
        syncOne(MODAL_MANAGE_LAYOUTS, manageLayoutsDialog != null, lastManageLayoutsOpen);
        lastManageLayoutsOpen = manageLayoutsDialog != null;
        syncOne(MODAL_PREFERENCES, preferencesDialog != null, lastPreferencesOpen);
        lastPreferencesOpen = preferencesDialog != null;
    }

    private void syncOne(String tag, boolean openNow, boolean openLast) {
        if (openNow && !openLast) {
            // Newly opened — push to top.
            modalOrder.remove(tag);
            modalOrder.add(tag);
        } else if (!openNow && openLast) {
            modalOrder.remove(tag);
        }
    }

    private boolean isAnyModalOpen() {
        return confirmDialog != null
            || captureDialog != null
            || layoutNameDialog != null
            || manageLayoutsDialog != null
            || preferencesDialog != null;
    }

    private @Nullable String topModalTag() {
        syncModalOrder();
        return modalOrder.isEmpty() ? null : modalOrder.get(modalOrder.size() - 1);
    }

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

    /** Resolved id of the layout currently shown in the workspace. Read by status-bar / picker UI for display. */
    public static String activeLayoutId() {
        return activeLayoutId;
    }

    /** Legacy entry: {@code /blib engine} from in-world. Project picker must have set an active project. */
    public EngineWorkspaceScreen() {
        this(Mode.IN_GAME, null);
    }

    /**
     * Menu-overlay entry: open the workspace from {@link net.minecraft.client.gui.screens.TitleScreen} with no world or
     * project loaded. The passed-in screen becomes the initial wrapped screen rendered inside the viewport; the
     * setScreen redirect mixin then keeps the engine wrapping whatever screen the user navigates to until a world
     * finishes loading.
     */
    public EngineWorkspaceScreen(@Nullable Screen initialWrapped) {
        this(Mode.MENU_OVERLAY, initialWrapped);
    }

    private EngineWorkspaceScreen(Mode mode, @Nullable Screen initialWrapped) {
        super(Component.literal("BLib Engine"));
        this.mode = mode;
        // Sanitize initialWrapped: never wrap a top-level engine screen (the picker, or another engine workspace).
        // Either would render BLib UI inside the viewport, and Cancel on a nested picker would set wrappedScreen=null,
        // leaving the viewport composit reading the engine's own previous frame (OBS-style recursion).
        if (initialWrapped instanceof ProjectPickerScreen || initialWrapped instanceof EngineWorkspaceScreen) {
            initialWrapped = null;
        }
        // MENU_OVERLAY without a wrapped screen and without a world has nothing for the viewport to composit (no world
        // on the main RT, no wrapped-screen RT). Fall back to a fresh TitleScreen so the user sees the main menu inside
        // the viewport rather than the recursive engine-in-engine artifact.
        if (mode == Mode.MENU_OVERLAY && initialWrapped == null && Minecraft.getInstance().level == null) {
            initialWrapped = new net.minecraft.client.gui.screens.TitleScreen();
        }
        this.wrappedScreen = initialWrapped;

        // Layout + keybinding catalogs are needed by both modes (the keybinding rebind UI lives in the workspace; the
        // layout catalog provides the modeler template menu mode renders).
        LayoutCatalog.initialize();
        KeybindingProfileCatalog.initialize();

        if (mode == Mode.MENU_OVERLAY) {
            // Load whichever layout the user last had active (same path as the in-game flow). Menu mode never writes
            // back to state.json on close (see removed()), so reading here is purely a load — the user's preferred
            // layout choice persists untouched across game state transitions.
            var bodyRoot = loadActiveLayoutBody();
            this.root = buildOuterLayout(bodyRoot);
            return;
        }

        // The IN_GAME workspace expects a project to be active before reaching here — the picker (ProjectPickerScreen)
        // is the only entry point, and it sets ProjectSession.activeProject before transitioning. If somehow we're
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
            this::openContentConfirm
        );
    }

    /**
     * {@link ProjectContentActionHandler} adapter — the content browser asks the screen to spawn a destructive confirm
     * dialog. Wired into both {@link #panelCtx} and the Window-menu's "Reopen Project Contents" so a panel created via
     * either path gets the same modal behavior.
     */
    private void openContentConfirm(String title, String message, String confirmLabel, boolean destructive, Runnable onConfirm) {
        this.confirmDialog = new ConfirmDialog(title, message, confirmLabel, "Cancel", destructive, onConfirm, () -> {});
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

        return new DockNode.Split(
            Orientation.VERTICAL,
            new DockNode.Leaf(new MenuBarPanel()),
            bodyAndStatus,
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
     * Walk past the trim wrappers built by {@link #buildOuterLayout} to reach the body subtree. The expected shape is
     * {@code Split(V, Leaf(MenuBar), Split(V, body, Leaf(StatusBar)))} with the trim splits pinned to
     * {@link MenuBarPanel#HEIGHT} / {@link StatusBarPanel#HEIGHT}.
     * <p>
     * The peel runs in a loop so layout files that were previously double-wrapped — by an older build whose
     * {@code extractBodyRoot} didn't recognize the trim shape and serialized the full tree as the "body" — heal
     * themselves on the next save (each surviving wrapper layer gets stripped). Without the loop, double-wrapped files
     * would keep accreting a layer per layout switch.
     */
    private static DockNode extractBodyRoot(DockNode root) {
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

    public Mode workspaceMode() {
        return mode;
    }

    /**
     * True while the workspace is currently wrapping another screen (showing it inside the viewport rect). Includes
     * both the menu-overlay mode (engine opened from the title screen, wrapping menu navigation) and the in-world
     * "pause inside engine" case (ESC opens PauseScreen as the wrapped screen). The setScreen redirect mixin uses this
     * to decide whether to capture {@link Minecraft#setScreen} calls or let them pass through — when a wrap is active,
     * any setScreen call from the wrapped screen (e.g. PauseScreen → Back to Game → setScreen(null)) becomes a wrap
     * swap instead of an engine close.
     */
    public boolean isWrappingScreen() {
        return wrappedScreen != null;
    }

    /** Used by the setScreen redirect mixin to recognise the explicit close path and stand down. */
    public static boolean isPreparingToClose() {
        return preparingToClose;
    }

    /**
     * Swap the wrapped screen — called by the setScreen redirect mixin when the currently-wrapped screen tries to
     * navigate away (e.g. TitleScreen → SelectWorldScreen). Replicates the lifecycle calls vanilla
     * {@link Minecraft#setScreen} makes (removed → added → BufferUploader.reset → init) so the wrapped screen sees the
     * same hooks fire as it would if it had become the active screen. Without {@code added()}, screens like
     * {@code SelectWorldScreen} that schedule work via {@link Screen#added} (Realms notifications widget pool, social
     * integrations, etc.) never finish initializing and render broken.
     */
    public void setWrappedScreen(@Nullable Screen next) {
        // Same sanitization as the constructor: top-level engine screens (picker, another workspace) must never be
        // wrapped — they're meant to replace the engine, not nest inside its viewport.
        if (next instanceof ProjectPickerScreen || next instanceof EngineWorkspaceScreen) {
            next = null;
        }
        // MENU_OVERLAY without a world needs *some* wrapped screen to give the viewport something to composit. Without
        // one, the world-path composit reads the engine's own previous frame and recurses infinitely (OBS effect).
        // Common trigger: a wrapped screen (PauseScreen, picker) closes via setScreen(null) → redirect mixin → here.
        if (next == null && mode == Mode.MENU_OVERLAY && Minecraft.getInstance().level == null) {
            next = new net.minecraft.client.gui.screens.TitleScreen();
        }
        var prev = this.wrappedScreen;
        this.wrappedScreen = next;
        this.wrappedScreenAdded = false;
        if (prev != null && prev != next) {
            prev.removed();
        }
        if (next != null && next != prev && minecraft != null) {
            next.added();
            com.mojang.blaze3d.vertex.BufferUploader.reset();
            next.init(minecraft, width, height);
            this.wrappedScreenAdded = true;
        }
        // Offscreen RT may need to resize if the new screen has different intrinsic dimensions — let the next render
        // tick re-allocate.
        EngineWorkspaceCompositor.clear();
    }

    public @Nullable Screen wrappedScreen() {
        return wrappedScreen;
    }

    /**
     * Explicit close entrypoint — bypasses the setScreen redirect mixin so the engine actually disappears rather than
     * just dropping its wrapped screen. In-world {@code setScreen(null)} returns to game; with no world vanilla
     * substitutes a fresh TitleScreen. Wired to the Project menu's "Close Engine" item.
     */
    public static void closeEngine() {
        preparingToClose = true;
        try {
            Minecraft.getInstance().setScreen(null);
        } finally {
            preparingToClose = false;
        }
    }

    @Override
    protected void init() {
        super.init();
        // First call (initial open from constructor) fires the full vanilla lifecycle on the wrapped screen so it
        // sees the same hooks it would as the active screen. Subsequent calls (from resize via rebuildWidgets) skip
        // the lifecycle — they're a no-op for the wrapped screen; resize() handles layout updates directly below.
        if (wrappedScreen != null && !wrappedScreenAdded) {
            wrappedScreen.added();
            com.mojang.blaze3d.vertex.BufferUploader.reset();
            wrappedScreen.init(minecraft, width, height);
            wrappedScreenAdded = true;
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        super.resize(mc, width, height);
        if (wrappedScreen != null) {
            wrappedScreen.resize(mc, width, height);
        }
        // Offscreen RT is sized to the main RT; reallocate lazily next frame.
        EngineWorkspaceCompositor.clear();
    }

    @Override
    public void tick() {
        super.tick();
        // Forward to the wrapped screen so animated screens (LevelLoadingScreen progress bar, ReceivingLevelScreen)
        // keep advancing while the engine is on top of them.
        if (wrappedScreen != null) {
            wrappedScreen.tick();
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        // Esc never closes the engine anymore — that's the B keybinding's job. Esc instead opens PauseScreen as a
        // wrapped screen when a world is loaded (see keyPressed). Returning false here prevents vanilla's keyPressed
        // from calling onClose, which would otherwise short-circuit the wrap-pause flow.
        return false;
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
        // Menu-overlay mode: lazy entry into engine mode once a world finally loads. EngineMode.enter() guards on
        // mc.player == null and is idempotent, so polling here each frame until the player exists is cheap. Also
        // freeze the integrated server here — the IN_GAME path captures on construction, but MENU_OVERLAY doesn't
        // have a world at construction time; we have to wait until the world is up. Gating on session==null keeps
        // this one-shot (subsequent renders see a non-null session and skip the block, so savedFrozenState isn't
        // overwritten on re-entry).
        if (mode == Mode.MENU_OVERLAY && Minecraft.getInstance().player != null && EngineMode.get().session() == null) {
            EngineTickControl.captureAndPause();
            EngineMode.get().enter();
            var lazySession = EngineMode.get().session();
            if (lazySession != null) {
                lazySession.setMode(NavigationMode.ORBIT);
            }
        }

        // Menu-overlay mode: render the wrapped screen into a dedicated offscreen RT, then blit only that into the
        // viewport rect. Isolating the render in its own framebuffer keeps the wrapped screen's font batching from
        // leaking onto the main RT outside the viewport (intermediate graphics.flush()es turn out NOT to be enough —
        // some MC text RenderTypes survive across them and re-render at the next flush, ending up on top of the
        // engine's panel chrome). Cursor coords are remapped to the wrapped screen's own coord space so hover lights
        // up the widget the user is visually pointing at inside the downsampled viewport rect.
        if (wrappedScreen != null) {
            var wrappedCoords = mapToWrappedCoords(mouseX, mouseY);
            int wrappedMouseX = wrappedCoords != null ? (int) wrappedCoords[0] : -1;
            int wrappedMouseY = wrappedCoords != null ? (int) wrappedCoords[1] : -1;
            var mc = Minecraft.getInstance();
            var mainRT = mc.getMainRenderTarget();
            EngineWorkspaceCompositor.bindWrappedScreenTarget();
            inWrappedScreenRender = true;
            try {
                wrappedScreen.renderWithTooltip(graphics, wrappedMouseX, wrappedMouseY, partialTick);
                graphics.flush();
            } finally {
                inWrappedScreenRender = false;
                mainRT.bindWrite(true);
            }
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        // In-world path reads the main RT (which holds vanilla's world + HUD render). Menu-overlay path reads the
        // dedicated wrappedScreenRT instead. Both feed the same downsample → viewport-rect blit pattern.
        var viewportRect = findViewportRect(root, 0, 0, logicalWidth(), logicalHeight());
        if (viewportRect != null) {
            if (wrappedScreen != null) {
                compositWrappedIntoViewport(viewportRect);
            } else {
                compositWorldIntoViewport(viewportRect);
            }
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
        if (openSubmenu != null && openSubmenu.isInside(logicalMouseX, logicalMouseY)) {
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
        // Any modal dialog suppresses panel hover state.
        if (isAnyModalOpen()) {
            panelMouseX = OFFSCREEN_MOUSE;
            panelMouseY = OFFSCREEN_MOUSE;
        }

        renderNode(graphics, root, 0, 0, logicalWidth, logicalHeight, panelMouseX, panelMouseY, partialTick);
        // Use the OFFSCREEN-substituted coords so dividers and tab-drag indicators don't light up under an open modal.
        renderHoveredDivider(graphics, panelMouseX, panelMouseY);
        renderTabDragOverlay(graphics, panelMouseX, panelMouseY);

        if (openMenu != null) {
            openMenu.render(graphics, logicalMouseX, logicalMouseY);
        }
        if (openSubmenu != null) {
            openSubmenu.render(graphics, logicalMouseX, logicalMouseY);
        }
        // Render modals in open-order so a child dialog (e.g. a Delete-Profile confirm spawned from PreferencesDialog)
        // sits on top of its parent. The order is reconciled with field state by syncModalOrder so individual open /
        // close sites don't need to push or pop manually.
        syncModalOrder();
        for (var tag : modalOrder) {
            switch (tag) {
                case MODAL_CONFIRM -> {
                    if (confirmDialog != null) {
                        confirmDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
                    }
                }
                case MODAL_CAPTURE -> {
                    if (captureDialog != null) {
                        captureDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
                    }
                }
                case MODAL_MANAGE_LAYOUTS -> {
                    if (manageLayoutsDialog != null) {
                        manageLayoutsDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
                    }
                }
                case MODAL_PREFERENCES -> {
                    if (preferencesDialog != null) {
                        preferencesDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
                    }
                }
                case MODAL_LAYOUT_NAME -> {
                    if (layoutNameDialog != null) {
                        layoutNameDialog.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
                    }
                }
                default -> {}
            }
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
        var font = EngineFont.get();
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
        var font = EngineFont.get();
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
     * (bottom-left origin, in raw window pixels) and ask the compositor to downsample-blit the main RT into it.
     */
    private void compositWorldIntoViewport(LogicalRect rect) {
        var raw = logicalRectToRawFramebuffer(rect);
        EngineWorkspaceCompositor.composit(raw[0], raw[1], raw[2], raw[3]);
    }

    /**
     * Same framebuffer-coord transform, but sourcing pixels from the wrapped-screen offscreen RT rather than the main
     * RT.
     */
    private void compositWrappedIntoViewport(LogicalRect rect) {
        var raw = logicalRectToRawFramebuffer(rect);
        EngineWorkspaceCompositor.blitWrappedToViewport(raw[0], raw[1], raw[2], raw[3]);
    }

    /** Returns {@code {x, y, w, h}} in raw bottom-origin framebuffer pixels for the given workspace-logical rect. */
    private int[] logicalRectToRawFramebuffer(LogicalRect rect) {
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
        return new int[] { rawX, rawY, rawW, rawH };
    }

    /**
     * Remap an engine-screen mouse coord (raw GUI-scaled pixels, top-origin) into the wrapped screen's coordinate space
     * (where the wrapped screen was {@code init}'d with engine.width × engine.height). Result is the same coordinate
     * space the wrapped screen uses internally for its widgets. Returns null if cursor is outside the viewport rect —
     * caller should not forward in that case.
     */
    private @Nullable double[] mapToWrappedCoords(double mouseX, double mouseY) {
        if (wrappedScreen == null) {
            return null;
        }
        var viewportRect = findViewportRect(root, 0, 0, logicalWidth(), logicalHeight());
        if (viewportRect == null) {
            return null;
        }
        double logicalX = mouseX / SCALE;
        double logicalY = mouseY / SCALE;
        if (
            logicalX < viewportRect.x()
                || logicalX >= viewportRect.x() + viewportRect.width()
                || logicalY < viewportRect.y()
                || logicalY >= viewportRect.y() + viewportRect.height()
        ) {
            return null;
        }
        // Viewport rect in screen-space (GUI-scaled) pixels.
        double vx = viewportRect.x() * SCALE;
        double vy = viewportRect.y() * SCALE;
        double vw = viewportRect.width() * SCALE;
        double vh = viewportRect.height() * SCALE;
        double wx = (mouseX - vx) * ((double) width / vw);
        double wy = (mouseY - vy) * ((double) height / vh);
        return new double[] { wx, wy };
    }

    /**
     * Is anything modal absorbing this frame's events? Used by the wrapped-screen forwarding helpers to know whether
     * they should defer or claim — modals / open menus / popups must keep their click-to-close + key behavior even when
     * the cursor is over the viewport rect.
     */
    private boolean engineModalAbsorbing() {
        return topModalTag() != null
            || openMenu != null
            || openSubmenu != null
            || SearchableSelect.getOpenPopup() != null
            || HslColorPickerPopup.getOpenPopup() != null
            || FactionManagePopup.getOpenPopup() != null
            || TextInput.getFocused() != null;
    }

    @Override
    public void removed() {
        super.removed();

        // Menu-overlay mode: lighter cleanup than IN_GAME — skipping persistOutgoingLayout / persistActiveSelection
        // avoids clobbering the user's preferred in-game layout, and skipping the cache invalidation cascade is fine
        // because MENU_OVERLAY workflows don't usually populate the project-scoped caches. Selection / freeze state
        // is still cleared so the live game doesn't keep rendering engine UI after B-toggle.
        if (mode == Mode.MENU_OVERLAY) {
            EngineWorkspaceCompositor.clear();
            if (wrappedScreen != null) {
                wrappedScreen.removed();
                wrappedScreen = null;
            }
            EngineMode.get().exit();
            // Restore the integrated server's previous freeze state — the lazy-enter path in render() called
            // captureAndPause once the world loaded, so we have to pair it with a restore on close. No-op when
            // captureAndPause never ran (engine closed before any world was loaded).
            EngineTickControl.restore();
            // Block-volume selection lives on a static singleton and isn't cleared by EngineMode.exit() — without
            // this, the wireframe AABB and any other engine selection visuals would linger in the world after B-toggle
            // because they read from BlockSelection / SelectionManager regardless of the renderer's isActive gate.
            // (Renderers also self-gate on isActive, but state cleanup keeps the system internally consistent.)
            BlockSelection.clear();
            SelectionManager.clear();
            JigsawPieceSelection.clear();
            EntitySpawnSelection.clear();
            com.blib.engine.selection.EngineHoverProbe.clear();
            com.blib.engine.territory.ClaimPaintTool.deactivate();
            SearchableSelect.closeOpenPopup();
            HslColorPickerPopup.closeOpenPopup();
            FactionManagePopup.closeOpenPopup();
            EngineCursor.reset();
            return;
        }

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
        // Project state persists across engine sessions so B-toggle reopens the same project without going through
        // the picker again. Switching projects is an explicit File→Open action inside the workspace. Transient picker
        // bits (callback, available-list) get refreshed by the picker itself on next open, so no clearing here.
        ProjectDraftCache.clear();
        com.blib.engine.tag.TagDraftCache.clear();
        com.blib.engine.tag.TagCatalogCache.clear();
        com.blib.engine.tag.RegistryEntriesCache.clear();
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
        // Top modal absorbs / handles scroll. Most modals just absorb (no scrollable content); preferences handles
        // its own list scroll.
        var topScrollTag = topModalTag();
        if (topScrollTag != null) {
            if (MODAL_PREFERENCES.equals(topScrollTag) && preferencesDialog != null) {
                return preferencesDialog.mouseScrolled(mouseX / SCALE, mouseY / SCALE, scrollX, scrollY);
            }
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
        // Wrapped-screen forward — scrolls over the viewport rect reach the wrapped screen (e.g. world list scroll in
        // SelectWorldScreen). Done before the openMenu / openSubmenu absorbers so the wrapped screen's scroll wins on
        // hover regardless of an unrelated dropdown elsewhere on the engine bar.
        if (wrappedScreen != null && !engineModalAbsorbing()) {
            var wrappedCoords = mapToWrappedCoords(mouseX, mouseY);
            if (wrappedCoords != null && wrappedScreen.mouseScrolled(wrappedCoords[0], wrappedCoords[1], scrollX, scrollY)) {
                return true;
            }
        }
        // Scroll-wheel events that land on an open menu shouldn't tunnel through to the scroll containers of panels
        // below — consume them.
        if (openMenu != null && openMenu.isInside(logicalX, logicalY)) {
            return true;
        }
        if (openSubmenu != null && openSubmenu.isInside(logicalX, logicalY)) {
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
        // Highlight stripe is intentionally a fixed 4-pixel band straddling the boundary line, regardless of the
        // hit-zone width. {@link #DIVIDER_HIT_PX} controls click accuracy; this constant controls how visible the
        // divider is on hover. The hit zone always covers ≥ the highlight (DIVIDER_HIT_PX ≥ 2 by design).
        if (dragger.split.orientation() == Orientation.HORIZONTAL) {
            graphics.fill(bx - 2, by, bx + 2, by + dragger.parentHeight, DIVIDER_HIGHLIGHT_COLOR);
        } else {
            graphics.fill(bx, by - 2, bx + dragger.parentWidth, by + 2, DIVIDER_HIGHLIGHT_COLOR);
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
        // Route through the topmost modal — same priority rules as keyPressed.
        var topModalChar = topModalTag();
        if (topModalChar != null) {
            switch (topModalChar) {
                case MODAL_CONFIRM, MODAL_CAPTURE, MODAL_MANAGE_LAYOUTS -> {
                    return true;
                }
                case MODAL_LAYOUT_NAME -> {
                    if (layoutNameDialog != null) {
                        return layoutNameDialog.charTyped(ch, modifiers);
                    }
                    return true;
                }
                case MODAL_PREFERENCES -> {
                    if (preferencesDialog != null) {
                        return preferencesDialog.charTyped(ch, modifiers);
                    }
                    return true;
                }
                default -> {
                    return true;
                }
            }
        }
        var focused = TextInput.getFocused();
        if (focused != null && focused.charTyped(ch, modifiers)) {
            return true;
        }
        // Wrapped-screen forward — char input reaches the wrapped screen's text fields (e.g. world-name field in
        // CreateWorldScreen) when no engine widget claims the character.
        if (wrappedScreen != null && wrappedScreen.charTyped(ch, modifiers)) {
            return true;
        }
        return super.charTyped(ch, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Top-of-stack modal handles key events first. Sub-dialogs (confirm spawned over preferences, layout-name
        // spawned over manage-layouts, etc.) sit above their parent in modalOrder so the topmost gets first crack.
        var topModalKey = topModalTag();
        if (topModalKey != null) {
            switch (topModalKey) {
                case MODAL_CONFIRM -> {
                    if (confirmDialog != null && confirmDialog.keyPressed(keyCode)) {
                        confirmDialog = null;
                    }
                    // Swallow non-Esc keys too — typing into nothing while a confirm is pending would feel
                    // unresponsive.
                    return true;
                }
                case MODAL_CAPTURE -> {
                    if (captureDialog != null) {
                        return captureDialog.keyPressed(keyCode, scanCode, modifiers);
                    }
                    return true;
                }
                case MODAL_LAYOUT_NAME -> {
                    if (layoutNameDialog != null) {
                        return layoutNameDialog.keyPressed(keyCode, scanCode, modifiers);
                    }
                    return true;
                }
                case MODAL_MANAGE_LAYOUTS -> {
                    if (manageLayoutsDialog != null) {
                        return manageLayoutsDialog.keyPressed(keyCode, scanCode, modifiers);
                    }
                    return true;
                }
                case MODAL_PREFERENCES -> {
                    if (preferencesDialog != null) {
                        return preferencesDialog.keyPressed(keyCode, scanCode, modifiers);
                    }
                    return true;
                }
                default -> {
                    return true;
                }
            }
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

        // Wrapped-screen forward — in menu-overlay mode the wrapped screen owns Esc (TitleScreen ignores it,
        // PauseScreen closes itself, dialogs cancel, etc.). Also forwards non-Esc keys (Enter, Tab) so wrapped
        // widgets see them. Done after the focused-TextInput check so engine text editing keeps the keystrokes when
        // an engine input is focused.
        if (wrappedScreen != null && wrappedScreen.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        // Esc cascades through transient state before reaching the "open pause menu" fallback: claim paint mode → held
        // piece → entity spawn selection → general selection → wrap PauseScreen (in-world only). This gives users a
        // single "get me out" key that doesn't immediately surface the pause menu when they're mid-edit.
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
            // In-world with no wrapped screen → open the vanilla PauseScreen inside the engine. The setScreen
            // redirect mixin keeps it wrapped (engine stays on top); PauseScreen's "Back to Game" → setScreen(null)
            // will drop the wrap, returning the viewport to the world. Pressing B is still the way to close the
            // engine itself.
            if (wrappedScreen == null && Minecraft.getInstance().level != null) {
                setWrappedScreen(new net.minecraft.client.gui.screens.PauseScreen(true));
                return true;
            }
        }

        // Ctrl+Z = universal undo. Modeler layout routes to the client-side modeler history (scene is heap-only,
        // no server roundtrip needed); everywhere else routes to the server-side ActionHistory (blocks, entities,
        // chunk claims, project metadata). The split mirrors Delete's layout-aware routing — same context check.
        if (ActiveKeybindings.matchesKey(Keybindings.UNDO, keyCode, modifiers)) {
            if (layoutHasModelerPanel()) {
                com.blib.engine.modeler.history.ModelerActionHistory.undo();
            } else {
                BLib.MOD.networking().sendToServer(C2SUndoActionPayload.INSTANCE);
            }
            return true;
        }

        if (ActiveKeybindings.matchesKey(Keybindings.REDO, keyCode, modifiers)) {
            if (layoutHasModelerPanel()) {
                com.blib.engine.modeler.history.ModelerActionHistory.redo();
            } else {
                BLib.MOD.networking().sendToServer(C2SRedoActionPayload.INSTANCE);
            }
            return true;
        }

        // F5 = Reload Project. Mirrors the File menu entry. Wipes the tag-staging overlay since reload catches the
        // runtime registry up to disk — the red staging tint is no longer meaningful, so rows settle into green / blue.
        if (ActiveKeybindings.matchesKey(Keybindings.RELOAD_PROJECT, keyCode, modifiers)) {
            if (ProjectSession.activeProject() != null) {
                BLib.MOD.networking().sendToServer(new C2SReloadProjectPayload(ProjectSession.activeProjectName()));
                com.blib.engine.tag.TagStagingCache.clear();
            }
            return true;
        }

        // Space = play / pause toggle. Mirrors the play / pause button on ViewportTransportToolbar. Text-input focus
        // is already gated above, so typing a space into a search box doesn't freeze the world.
        if (ActiveKeybindings.matchesKey(Keybindings.VIEWPORT_PLAY_PAUSE, keyCode, modifiers)) {
            EngineTickControl.toggle();
            return true;
        }

        // Placement-mode hotkeys: R cycles rotation forward (clockwise), M cycles mirror, T toggles between FREE
        // and JIGSAW_SNAP placement modes. Gated by an active piece selection so these keys don't steal input from
        // other potential editor tools later. Suppressed while a text input is focused (handled above), so typing
        // them into the search box won't rotate the world preview / change modes.
        if (JigsawPieceSelection.hasSelection()) {
            if (ActiveKeybindings.matchesKey(Keybindings.JIGSAW_ROTATE, keyCode, modifiers)) {
                JigsawPieceSelection.cycleRotation(1);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.JIGSAW_MIRROR, keyCode, modifiers)) {
                JigsawPieceSelection.cycleMirror();
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.JIGSAW_CYCLE_MODE, keyCode, modifiers)) {
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
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_TRANSLATE, keyCode, modifiers)) {
                com.blib.engine.entityselection.EntityGizmoMode.set(com.blib.engine.entityselection.EntityGizmoMode.TRANSLATE);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_SCALE, keyCode, modifiers)) {
                com.blib.engine.entityselection.EntityGizmoMode.set(com.blib.engine.entityselection.EntityGizmoMode.SCALE);
                return true;
            }
            // GIZMO_MOVE_BLOCKS intentionally not handled here — entity gizmo has no MOVE_BLOCKS analog.
        } else {
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_TRANSLATE, keyCode, modifiers)) {
                BlockSelection.setGizmoMode(BlockSelection.GizmoMode.TRANSLATE_VOLUME);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_SCALE, keyCode, modifiers)) {
                BlockSelection.setGizmoMode(BlockSelection.GizmoMode.SCALE_VOLUME);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.GIZMO_MOVE_BLOCKS, keyCode, modifiers)) {
                BlockSelection.setGizmoMode(BlockSelection.GizmoMode.MOVE_BLOCKS);
                return true;
            }
        }

        // Clipboard hotkeys: Ctrl+C / Ctrl+X / Ctrl+V for copy / cut / paste, Delete for clear. Gated on no focused
        // text input so the muscle-memory of Ctrl+C in a name field doesn't accidentally copy blocks instead of text.
        // BlockSelectionOps self-gates on AABB presence + volume cap; clicks/keys without a valid AABB are no-ops.
        if (TextInput.getFocused() == null) {
            if (ActiveKeybindings.matchesKey(Keybindings.COPY, keyCode, modifiers)) {
                BlockSelectionOps.copy(false);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.CUT, keyCode, modifiers)) {
                BlockSelectionOps.copy(true);
                return true;
            }
            if (ActiveKeybindings.matchesKey(Keybindings.PASTE, keyCode, modifiers)) {
                BlockSelectionOps.paste();
                return true;
            }
        }
        if (TextInput.getFocused() == null && ActiveKeybindings.matchesKey(Keybindings.DELETE, keyCode, modifiers)) {
            // Modeler-layout delete takes priority and always consumes the key. The modeler scene's selection state is
            // separate from the world {@link SelectionManager}, so when the user is looking at modeler UI we route
            // Delete there exclusively — falling through to world-delete on a "nothing to delete" or root-bone case
            // would surprise the user by killing a stale world entity that isn't visible in the modeler layout.
            if (layoutHasModelerPanel()) {
                ModelerScene.get().deleteSelection();
                return true;
            }
            var deleteSel = SelectionManager.current().single();
            if (deleteSel instanceof com.blib.engine.selection.EntitySelectable es) {
                // Mirrors the context-menu "Delete Entity" gate — players aren't deletable, the server would reject
                // anyway but the no-op feels nicer with a client-side check.
                var entity = es.entity();
                if (entity != null && !(entity instanceof net.minecraft.world.entity.player.Player)) {
                    BLib.MOD.networking()
                        .sendToServer(new com.blib.mod.common.network.packet.C2SRemoveEntityPayload(entity.getId()));
                }
                return true;
            }
            if (deleteSel instanceof com.blib.engine.selection.BlockSelectable bs) {
                deleteSingleBlock(bs.pos());
                return true;
            }
            BlockSelectionOps.delete();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Delete a single inspected block by reusing the volume-delete packet with a degenerate one-block AABB. Avoids a
     * parallel "delete one block" packet — the server's volume delete already special-cases tiny volumes, and routing
     * through the same handler keeps op-gating + edit logging consistent. The
     * {@link com.blib.engine.selection.BlockSelectable#isValid} check that prunes the now-air block from
     * {@link SelectionManager} fires naturally on the next read, so no explicit clear is needed here.
     */
    private static void deleteSingleBlock(net.minecraft.core.BlockPos pos) {
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        var dim = mc.player.level().dimension().location();
        BLib.MOD.networking()
            .sendToServer(new com.blib.mod.common.network.packet.C2SDeleteSelectionPayload(pos, pos, dim));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var logicalX = mouseX / SCALE;
        var logicalY = mouseY / SCALE;

        // Top-of-stack modal absorbs the click — sub-dialogs (Delete confirm spawned from Preferences, etc.) sit
        // above their parent in modalOrder, so the topmost gets first crack. Outside-clicks are still swallowed by
        // returning true so they don't reach panels under the dim.
        var topTag = topModalTag();
        if (topTag != null) {
            switch (topTag) {
                case MODAL_CONFIRM -> {
                    if (confirmDialog != null && confirmDialog.mouseClicked(logicalX, logicalY, button)) {
                        confirmDialog = null;
                    }
                }
                case MODAL_CAPTURE -> {
                    if (captureDialog != null) {
                        captureDialog.mouseClicked(logicalX, logicalY, button);
                    }
                }
                case MODAL_LAYOUT_NAME -> {
                    if (layoutNameDialog != null) {
                        layoutNameDialog.mouseClicked(logicalX, logicalY, button);
                    }
                }
                case MODAL_MANAGE_LAYOUTS -> {
                    if (manageLayoutsDialog != null) {
                        manageLayoutsDialog.mouseClicked(logicalX, logicalY, button);
                    }
                }
                case MODAL_PREFERENCES -> {
                    if (preferencesDialog != null) {
                        preferencesDialog.mouseClicked(logicalX, logicalY, button);
                    }
                }
                default -> {}
            }
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

        // 0d) Wrapped-screen (menu-overlay mode): clicks inside the viewport rect forward to the wrapped screen so
        // the user can navigate menus through the viewport. Engine modals / popups above consumed first; engine
        // dropdowns below come after so chip-menu clicks still work over the viewport rect.
        if (wrappedScreen != null && !engineModalAbsorbing()) {
            var wrappedCoords = mapToWrappedCoords(mouseX, mouseY);
            if (wrappedCoords != null && wrappedScreen.mouseClicked(wrappedCoords[0], wrappedCoords[1], button)) {
                return true;
            }
        }

        // 0) An open dropdown takes priority: clicking an item fires it; clicking outside just closes the menu.
        // Submenu is checked first (innermost wins); clicks inside the parent menu re-spawn the submenu when they
        // land on a submenu-parent row, or close everything and run the action when they land on a leaf row.
        if (openSubmenu != null && button == 0 && openSubmenu.isInside(logicalX, logicalY)) {
            var subIdx = openSubmenu.hitItemAt(logicalX, logicalY);
            if (subIdx >= 0) {
                var subItem = openSubmenu.itemAt(subIdx);
                openMenu = null;
                openSubmenu = null;
                subItem.action().run();
                return true;
            }
            // Inside submenu but on a border / dead row: consume and keep both menus open.
            return true;
        }
        if (openMenu != null) {
            if (button == 0) {
                var idx = openMenu.hitItemAt(logicalX, logicalY);
                if (idx >= 0) {
                    var item = openMenu.itemAt(idx);
                    if (item.hasSubmenu()) {
                        // (Re-)spawn the cascading submenu. The parent menu stays open.
                        openSubmenu = DropdownMenu.spawnSubmenu(
                            openMenu,
                            idx,
                            item.children(),
                            logicalWidth(),
                            logicalHeight()
                        );
                        return true;
                    }
                    openMenu = null;
                    openSubmenu = null;
                    item.action().run();
                    return true;
                }
            }
            openMenu = null;
            openSubmenu = null;
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
                        openSubmenu = null;
                    }
                    return true;
                }
            }

            // 2) Panel-internal high-priority UI (scrollbar thumb, close buttons, etc.). Runs before the divider so a
            // scrollbar at the right edge of a panel adjacent to a vertical dock split isn't eaten by divider drag.
            // A true return also captures subsequent drag / release for this panel — see #capturedPanel.
            var preDivider = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
            if (preDivider != null && preDivider.mouseClickedCapture(logicalX, logicalY, button)) {
                this.capturedPanel = preDivider;
                return true;
            }

            // 3) Divider drag start — must run before the tab-strip check so the bottom rows of the visible divider
            // highlight (which fall inside the adjacent panel's title bar / tab strip) can still start a drag instead
            // of leaking into a tab-strip click. Scrollbar / edge UI is already protected by step 2's panel capture.
            var divider = findDivider(root, 0, 0, logicalWidth(), logicalHeight(), (int) logicalX, (int) logicalY);
            if (divider != null && isResizable(divider.split.sizing())) {
                this.activeDrag = new ActiveDrag(divider);
                return true;
            }

            // 4) Tab strip click: switch active, close, or arm a tab drag.
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
        if (isAnyModalOpen()) {
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

        // Wrapped-screen forward — releases over the viewport rect end any in-progress wrapped-screen interaction
        // (button release, slider end). Captured-panel below claims releases for engine drags-in-progress.
        if (wrappedScreen != null && !engineModalAbsorbing() && capturedPanel == null) {
            var wrappedCoords = mapToWrappedCoords(mouseX, mouseY);
            if (wrappedCoords != null && wrappedScreen.mouseReleased(wrappedCoords[0], wrappedCoords[1], button)) {
                return true;
            }
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
        if (openSubmenu != null && openSubmenu.isInside(logicalX, logicalY)) {
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
        // Top modal absorbs / handles drags. Preferences forwards drags (for its TextInput); other modals just absorb.
        var topDragTag = topModalTag();
        if (topDragTag != null) {
            if (MODAL_PREFERENCES.equals(topDragTag) && preferencesDialog != null) {
                return preferencesDialog.mouseDragged(mouseX / SCALE, mouseY / SCALE, button, deltaX, deltaY);
            }
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

        // Wrapped-screen forward — drags over the viewport rect (e.g. slider drags in Options) reach the wrapped
        // screen. Captured-panel + activeDrag checks below cover engine-internal drags.
        if (wrappedScreen != null && !engineModalAbsorbing() && capturedPanel == null && activeDrag == null) {
            var wrappedCoords = mapToWrappedCoords(mouseX, mouseY);
            if (wrappedCoords != null && wrappedScreen.mouseDragged(wrappedCoords[0], wrappedCoords[1], button, deltaX, deltaY)) {
                return true;
            }
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
        if (openSubmenu != null && openSubmenu.isInside(logicalX, logicalY)) {
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

    /**
     * Only {@link Sizing.Ratio} splits are user-resizable. {@link Sizing.FirstFixed} / {@link Sizing.SecondFixed}
     * anchor one side at a pixel count (menu bars, status bars, toolbars), so dragging their boundary would just snap
     * back — we hide the divider entirely rather than expose a no-op handle.
     */
    private static boolean isResizable(Sizing sizing) {
        return sizing instanceof Sizing.Ratio;
    }

    private static @Nullable DividerHit findDivider(DockNode node, int x, int y, int width, int height, int mouseX, int mouseY) {
        if (!(node instanceof DockNode.Split split)) {
            return null;
        }

        // Fixed splits aren't draggable, so don't claim their boundary on hover — fall through and let the recursion
        // find a real (Ratio) divider further down the tree, if any.
        var resizable = isResizable(split.sizing());

        if (split.orientation() == Orientation.HORIZONTAL) {
            var firstWidth = DockNode.boundary(split.sizing(), width);
            var boundaryX = x + firstWidth;
            if (resizable && Math.abs(mouseX - boundaryX) <= DIVIDER_HIT_PX && mouseY >= y && mouseY < y + height) {
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
            if (resizable && Math.abs(mouseY - boundaryY) <= DIVIDER_HIT_PX && mouseX >= x && mouseX < x + width) {
                return new DividerHit(split, x, y, width, height);
            }
            var inFirst = findDivider(split.first(), x, y, width, firstHeight, mouseX, mouseY);
            if (inFirst != null) {
                return inFirst;
            }
            return findDivider(split.second(), x, y + firstHeight, width, height - firstHeight, mouseX, mouseY);
        }
    }

    /**
     * True when the active layout contains at least one modeler panel (viewport / outliner / inspector). Used by the
     * Delete handler to gate modeler-scene delete behavior — the modeler scene state is global, but Delete should only
     * dispatch to it when the user's actually looking at modeler UI, not when they happen to have a stale modeler
     * selection in some unrelated layout.
     */
    public boolean layoutHasModelerPanel() {
        return panelTreeContainsModeler(root);
    }

    /**
     * Convenience for callers that don't already hold a workspace reference (e.g. the action-stack panel, which is
     * agnostic to its host screen). Returns true when the currently-active screen is a workspace whose layout has a
     * modeler panel. False on any other screen state.
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
            case MenuBarPanel.CHIP_PROJECT -> buildProjectMenu(anchorX, anchorY);
            case MenuBarPanel.CHIP_EDIT -> buildEditMenu(anchorX, anchorY);
            case MenuBarPanel.CHIP_VIEW -> buildViewMenu(anchorX, anchorY);
            case MenuBarPanel.CHIP_WINDOW -> buildWindowMenu(anchorX, anchorY);
            case MenuBarPanel.CHIP_LAYOUT -> buildLayoutMenu(anchorX, anchorY);
            default -> null;
        };
    }

    /**
     * Edit dropdown. Undo and Redo do the same thing as Ctrl+Z / Ctrl+Y — route to the modeler history in the modeler
     * layout, server-side history elsewhere. We don't grey them out by stack size because the dropdown is built once on
     * open; both backends silently no-op if the relevant stack is empty.
     */
    private DropdownMenu buildEditMenu(int anchorX, int anchorY) {
        var items = new java.util.ArrayList<DropdownMenu.Item>();
        items.add(new DropdownMenu.Item("Undo", this::dispatchUndo));
        items.add(new DropdownMenu.Item("Redo", this::dispatchRedo));
        items.add(new DropdownMenu.Item("Preferences…", this::openPreferencesDialog));
        return new DropdownMenu(anchorX, anchorY, items);
    }

    private void dispatchUndo() {
        if (layoutHasModelerPanel()) {
            com.blib.engine.modeler.history.ModelerActionHistory.undo();
        } else {
            BLib.MOD.networking().sendToServer(C2SUndoActionPayload.INSTANCE);
        }
    }

    private void dispatchRedo() {
        if (layoutHasModelerPanel()) {
            com.blib.engine.modeler.history.ModelerActionHistory.redo();
        } else {
            BLib.MOD.networking().sendToServer(C2SRedoActionPayload.INSTANCE);
        }
    }

    private void openPreferencesDialog() {
        this.preferencesDialog = new PreferencesDialog(
            () -> this.preferencesDialog = null,
            this::openPreferencesNewProfile,
            this::openPreferencesRenameProfile,
            this::openPreferencesDuplicateProfile,
            this::openPreferencesDeleteProfile
        );
    }

    private void openPreferencesNewProfile() {
        this.layoutNameDialog = new LayoutNameDialog(
            LayoutNameDialog.Mode.PROFILE_NEW,
            "",
            id -> KeybindingProfileCatalog.idAvailable(id),
            this::confirmCreateProfile,
            () -> this.layoutNameDialog = null
        );
    }

    private void confirmCreateProfile(String displayName) {
        this.layoutNameDialog = null;
        try {
            var id = KeybindingProfileCatalog.suggestId(displayName);
            var profile = KeybindingProfile.empty(id, displayName);
            KeybindingProfileCatalog.save(profile);
            KeybindingProfileCatalog.setActive(id);
            if (preferencesDialog != null) {
                preferencesDialog.refreshProfiles();
                preferencesDialog.loadProfile(id);
            }
        } catch (java.io.IOException e) {
            org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] confirmCreateProfile: write failed", e);
        }
    }

    private void openPreferencesRenameProfile(KeybindingProfile profile) {
        this.layoutNameDialog = new LayoutNameDialog(
            LayoutNameDialog.Mode.PROFILE_RENAME,
            profile.displayName(),
            id -> KeybindingProfileCatalog.idAvailable(id) || id.equals(profile.id()),
            newName -> confirmRenameProfile(profile, newName),
            () -> this.layoutNameDialog = null
        );
    }

    private void confirmRenameProfile(KeybindingProfile profile, String newDisplayName) {
        this.layoutNameDialog = null;
        try {
            var renamed = profile.withDisplayName(newDisplayName);
            KeybindingProfileCatalog.save(renamed);
            if (preferencesDialog != null) {
                preferencesDialog.refreshProfiles();
                preferencesDialog.loadProfile(profile.id());
            }
        } catch (java.io.IOException e) {
            org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] confirmRenameProfile: write failed", e);
        }
    }

    private void openPreferencesDuplicateProfile(KeybindingProfile profile) {
        var initial = profile.displayName() + " (copy)";
        this.layoutNameDialog = new LayoutNameDialog(
            LayoutNameDialog.Mode.PROFILE_DUPLICATE,
            initial,
            KeybindingProfileCatalog::idAvailable,
            newName -> confirmDuplicateProfile(profile, newName),
            () -> this.layoutNameDialog = null
        );
    }

    private void confirmDuplicateProfile(KeybindingProfile source, String newDisplayName) {
        this.layoutNameDialog = null;
        try {
            var newId = KeybindingProfileCatalog.suggestId(newDisplayName);
            var copy = new KeybindingProfile(KeybindingProfile.CURRENT_VERSION, newId, newDisplayName, source.overrides());
            KeybindingProfileCatalog.save(copy);
            KeybindingProfileCatalog.setActive(newId);
            if (preferencesDialog != null) {
                preferencesDialog.refreshProfiles();
                preferencesDialog.loadProfile(newId);
            }
        } catch (java.io.IOException e) {
            org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] confirmDuplicateProfile: write failed", e);
        }
    }

    private void openPreferencesDeleteProfile(KeybindingProfile profile) {
        this.confirmDialog = new ConfirmDialog(
            "Delete Profile",
            "Delete profile '" + profile.displayName() + "'? This cannot be undone.",
            "Delete",
            "Cancel",
            true,
            () -> confirmDeleteProfile(profile),
            () -> this.confirmDialog = null
        );
    }

    private void confirmDeleteProfile(KeybindingProfile profile) {
        this.confirmDialog = null;
        try {
            KeybindingProfileCatalog.delete(profile.id());
            if (preferencesDialog != null) {
                preferencesDialog.refreshProfiles();
                preferencesDialog.loadProfile(KeybindingProfileCatalog.getActive().id());
            }
        } catch (java.io.IOException e) {
            org.slf4j.LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] confirmDeleteProfile: delete failed", e);
        }
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
     * FILE menu — disk-level open actions that aren't scoped to a project. Currently just the modeler import; grows
     * here if more general-purpose file ops appear later. Project lifecycle (New / Open / Reload / Delete) lives on its
     * own top-level {@link MenuBarPanel#CHIP_PROJECT} menu — see {@link #buildProjectMenu}.
     */
    private DropdownMenu buildFileMenu(int anchorX, int anchorY) {
        var items = new java.util.ArrayList<DropdownMenu.Item>();
        items.add(new DropdownMenu.Item("Open Model from File…", EngineWorkspaceScreen::openGeoModelFromFile));
        return new DropdownMenu(anchorX, anchorY, items);
    }

    /**
     * PROJECT menu — project lifecycle CRUD. Create / Open close the workspace and open the picker (workspace's
     * removed() clears ProjectSession; the picker's onConfirmedOpen rebuilds the workspace after a successful Open).
     * Reload / Delete act on the active project; both are inert when no project is active (which shouldn't happen
     * post-picker-gating but is defensive).
     */
    private DropdownMenu buildProjectMenu(int anchorX, int anchorY) {
        var hasProject = ProjectSession.activeProject() != null;
        var hasWorld = Minecraft.getInstance().level != null;
        var items = new java.util.ArrayList<DropdownMenu.Item>();

        // Project CRUD requires the integrated server (server-side handlers for the picker / reload / delete payloads).
        // With no world, all four items are inert — surface that in the label.
        var noWorldSuffix = hasWorld ? "" : " (no world)";
        items.add(new DropdownMenu.Item("Create…" + noWorldSuffix, () -> {
            if (!hasWorld) {
                return;
            }
            openPicker(true);
        }));
        items.add(new DropdownMenu.Item("Open…" + noWorldSuffix, () -> {
            if (!hasWorld) {
                return;
            }
            openPicker(false);
        }));
        items.add(
            new DropdownMenu.Item(hasProject ? "Reload" : "Reload" + (hasWorld ? " (no project)" : noWorldSuffix), () -> {
                if (!hasWorld || !hasProject) {
                    return;
                }
                BLib.MOD.networking().sendToServer(new C2SReloadProjectPayload(ProjectSession.activeProjectName()));
                // Wipe the tag-staging overlay — reload makes the runtime registry catch up to disk, so the red
                // staging tint is no longer meaningful (rows settle into green / blue based on committed state).
                com.blib.engine.tag.TagStagingCache.clear();
            })
        );
        items.add(
            new DropdownMenu.Item(hasProject ? "Delete…" : "Delete…" + (hasWorld ? " (no project)" : noWorldSuffix), () -> {
                if (!hasWorld || !hasProject) {
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
        // Always-available exit: bypasses the setScreen redirect via the preparingToClose flag so the engine actually
        // unwinds instead of trapping the user in overlay mode.
        items.add(new DropdownMenu.Item("Close Engine", EngineWorkspaceScreen::closeEngine));
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
     * Handler for "FILE → Open Model from File…". Opens the OS-native open-file dialog via
     * {@link ModelerFilePicker#pickGeoModel} and, on a successful pick, hands the path to
     * {@link ModelerSceneLoader#loadFromFile} which replaces the modeler's active scene. No-op on cancel; errors are
     * logged inside the loader.
     */
    private static void openGeoModelFromFile() {
        var picked = ModelerFilePicker.pickGeoModel();
        if (picked != null) {
            ModelerSceneLoader.loadFromFile(picked);
        }
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

            @Override
            public void onRightClickPiece(java.util.UUID pieceId, double cursorX, double cursorY) {
                onViewportRightClickPiece(pieceId, cursorX, cursorY);
            }
        };
    }

    private void onViewportRightClick(@Nullable LivingEntity entity, double cursorX, double cursorY) {
        if (entity == null) {
            this.openMenu = null;
            this.openSubmenu = null;
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
            if (entity instanceof Dismemberable) {
                var remaining = LimbDismemberer.getRemainingDefinitions(entity);
                if (!remaining.isEmpty()) {
                    var limbItems = new java.util.ArrayList<DropdownMenu.Item>();
                    limbItems.add(
                        new DropdownMenu.Item("All", () -> {
                            BLib.MOD.networking().sendToServer(new C2SDismemberAllLimbsPayload(entityId));
                        })
                    );
                    for (var def : remaining) {
                        var label = prettifyLimbName(def.id().getPath());
                        var limbId = def.id();
                        limbItems.add(
                            new DropdownMenu.Item(label, () -> {
                                BLib.MOD.networking().sendToServer(new C2SDismemberLimbPayload(entityId, limbId));
                            })
                        );
                    }
                    items.add(new DropdownMenu.Item("Dismember…", () -> {}, limbItems));
                }
            }
            items.add(
                new DropdownMenu.Item("Delete Entity", () -> {
                    BLib.MOD.networking().sendToServer(new C2SRemoveEntityPayload(entityId));
                })
            );
        }

        this.openMenu = new DropdownMenu(menuX, menuY, items);
        this.openSubmenu = null;
    }

    /**
     * Turn a limb id's path component ("left_arm", "head") into a display label ("Left Arm", "Head") for the Dismember
     * submenu. Splits on underscores and title-cases each segment; preserves any other characters in case authors used
     * mixed-case ids.
     */
    private static String prettifyLimbName(String path) {
        var parts = path.split("_");
        var sb = new StringBuilder();
        for (var i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            var part = parts[i];
            if (part.isEmpty()) {
                continue;
            }
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                sb.append(part.substring(1));
            }
        }
        return sb.toString();
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
        this.openSubmenu = null;
    }

    /**
     * Right-click on a placed jigsaw piece. Mirrors the block-volume context menu so users get the same affordances
     * (Capture / Cut / Copy / Delete) plus an Open Inspector entry. Capture / Cut / Copy work by promoting the piece to
     * a block-volume selection covering its AABB and then dispatching the existing
     * {@link com.blib.engine.blockselection.BlockSelectionOps}; Delete keeps the identity-aware
     * {@link com.blib.mod.common.network.packet.C2SDeletePlacedPiecePayload} path so the registry entry is removed, not
     * just the blocks.
     * <p>
     * Cut on a piece is implemented as Copy-then-DeletePiece — the existing volume Cut would clear the blocks but leave
     * the piece record orphaned (a ghost piece outline over empty air); the explicit delete-piece call avoids that.
     */
    private void onViewportRightClickPiece(java.util.UUID pieceId, double cursorX, double cursorY) {
        var items = new java.util.ArrayList<DropdownMenu.Item>();
        items.add(
            new DropdownMenu.Item("Open Inspector", () -> {
                // Mutual exclusion: the volume wireframe must not coexist with a piece selection — the inspector
                // shows the piece, so the volume outline would be visual noise representing nothing inspectable.
                // performSelectionAt clears the volume on every single-thing LMB pick; the context-menu path needs
                // the same call.
                BlockSelection.clearVolume();
                SelectionManager.selectSingle(new com.blib.engine.selection.PlacedJigsawPieceSelectable(pieceId));
            })
        );
        items.add(
            new DropdownMenu.Item("Capture…", () -> {
                com.blib.engine.selection.PlacedJigsawPieceSelectable.promoteToVolume(pieceId, null);
                this.captureDialog = new CaptureDialog(() -> this.captureDialog = null);
            })
        );
        items.add(
            new DropdownMenu.Item("Cut", () -> {
                com.blib.engine.selection.PlacedJigsawPieceSelectable.promoteToVolume(pieceId, null);
                com.blib.engine.blockselection.BlockSelectionOps.copy(false);
                BLib.MOD.networking()
                    .sendToServer(new com.blib.mod.common.network.packet.C2SDeletePlacedPiecePayload(pieceId));
            })
        );
        items.add(
            new DropdownMenu.Item("Copy", () -> {
                com.blib.engine.selection.PlacedJigsawPieceSelectable.promoteToVolume(pieceId, null);
                com.blib.engine.blockselection.BlockSelectionOps.copy(false);
            })
        );
        items.add(
            new DropdownMenu.Item("Delete", () -> {
                BLib.MOD.networking()
                    .sendToServer(new com.blib.mod.common.network.packet.C2SDeletePlacedPiecePayload(pieceId));
            })
        );
        this.openMenu = new DropdownMenu((int) cursorX, (int) cursorY, items);
        this.openSubmenu = null;
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
