package com.blib.engine.ui;

import com.mojang.blaze3d.vertex.BufferUploader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.function.Supplier;

import com.blib.engine.command.api.Command;
import com.blib.engine.command.api.CommandBus;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.input.KeybindingProfile;
import com.blib.engine.input.KeybindingProfileCatalog;
import com.blib.engine.jigsaw.JigsawPieceLibrary;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.jigsaw.JigsawPoolLibrary;
import com.blib.engine.layout.ActiveLayoutState;
import com.blib.engine.layout.LayoutCatalog;
import com.blib.engine.layout.LayoutDoc;
import com.blib.engine.layout.LayoutSnapshot;
import com.blib.engine.layout.LayoutStorage;
import com.blib.engine.layout.LayoutTemplate;
import com.blib.engine.layout.PanelRegistry;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.platform.spi.EngineRenderState;
import com.blib.engine.session.EngineMode;
import com.blib.engine.session.NavigationMode;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.spawn.EntitySpawnSelection;
import com.blib.engine.tag.TagStagingCache;
import com.blib.engine.territory.ClaimPaintTool;
import com.blib.engine.ui.dialog.CaptureDialog;
import com.blib.engine.ui.dialog.ConfirmDialog;
import com.blib.engine.ui.dialog.LayoutNameDialog;
import com.blib.engine.ui.dialog.ManageLayoutsDialog;
import com.blib.engine.ui.dialog.PreferencesDialog;
import com.blib.engine.ui.dock.DockNode;
import com.blib.engine.ui.dock.Orientation;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.dock.PanelChrome;
import com.blib.engine.ui.dock.TabbedPanel;
import com.blib.engine.ui.panel.chrome.MenuBarPanel;
import com.blib.engine.ui.panel.viewport.ViewportPanel;
import com.blib.engine.ui.popup.FactionManagePopup;
import com.blib.engine.ui.popup.HslColorPickerPopup;
import com.blib.engine.ui.screen.ProjectPickerScreen;
import com.blib.engine.ui.widget.DropdownMenu;
import com.blib.engine.ui.widget.SearchableSelect;
import com.blib.engine.ui.widget.TextInput;
import com.blib.engine.ui.workspace.HoverOverlayRenderer;
import com.blib.engine.ui.workspace.ViewportContextMenuHandler;
import com.blib.engine.ui.workspace.WorkspaceDialogController;
import com.blib.engine.ui.workspace.WorkspaceHotkeyDispatcher;
import com.blib.engine.ui.workspace.WorkspaceLayoutController;
import com.blib.engine.ui.workspace.WorkspaceLayoutPersistence;
import com.blib.engine.ui.workspace.dock.DividerDragController;
import com.blib.engine.ui.workspace.dock.DockTreeHitTest;
import com.blib.engine.ui.workspace.dock.DockTreeMutator;
import com.blib.engine.ui.workspace.dock.TabDragController;
import com.blib.engine.ui.workspace.menubar.MenuBarController;

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
     * Squared cursor-motion threshold (logical pixels) to promote a pending tab click into an active drag. Below this,
     * a press-and-release on a tab is just an "activate this tab" click.
     */
    private static final double TAB_DRAG_THRESHOLD_SQ = 16.0;

    private static final int TAB_GHOST_BG_COLOR = 0xCC2C2C32;

    private static final int TAB_GHOST_TEXT_COLOR = 0xFFE0E0E0;

    private static final int TAB_DROP_TARGET_COLOR = 0x404F8FFF;

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
     * Mixin-visible render-state flags ({@code preparingToClose}, {@code inWrappedScreenRender}) now live on
     * {@link EngineRenderState} so the mixin layer can import a narrow SPI instead of this whole screen class. The
     * screen still drives the flags; mixins read them via the SPI.
     */

    /**
     * Tracks whether {@link #wrappedScreen}'s {@code added()} / {@code init()} lifecycle has fired since it became the
     * wrapped screen. Engine's {@code init()} is re-invoked by vanilla on every window resize (via
     * {@code Screen.rebuildWidgets}), so without a guard we'd fire {@code added()} every resize — some screens treat
     * that as a fresh activation and reload async state, etc. {@link #setWrappedScreen} resets this on swap, and the
     * resize path forwards to the wrapped screen directly without going back through {@code added()}.
     */
    private boolean wrappedScreenAdded;

    private DockNode root;

    private final DividerDragController dragController = new DividerDragController();

    private final TabDragController tabDrag = new TabDragController();

    /**
     * Owns the workspace's menu-bar dropdown state — open menu, optional submenu, hover-driven cascade behaviour, and
     * the per-chip menu builders. The screen forwards every menu lifecycle call to this controller so adding a new menu
     * item is contained to {@link MenuBarController} + the matching action on {@link MenuBarActionsImpl}.
     */
    private final MenuBarController menuBar = new MenuBarController(new MenuBarActionsImpl());

    /**
     * Owns the five workspace modal dialogs (confirm, capture, layout-name, manage-layouts, preferences) and the modal
     * z-order stack. The screen forwards render + input dispatch through this controller.
     */
    private final WorkspaceDialogController dialogs =
        new WorkspaceDialogController();

    private final CommandBus commands = new CommandBus();

    private final ViewportContextMenuHandler viewportContextMenu = new ViewportContextMenuHandler(
        commands,
        new ViewportContextMenuHostImpl()
    );

    private final WorkspaceHotkeyDispatcher hotkeys =
        new WorkspaceHotkeyDispatcher(new WorkspaceHotkeyHostImpl());

    /**
     * Panel that captured the mouse via {@link Panel#mouseClickedCapture}. While non-null, {@link #mouseDragged} and
     * {@link #mouseReleased} route to this panel before any other handling, so a panel-driven drag (scrollbar, etc.)
     * tracks the cursor even when it leaves the panel rect. Cleared on {@code mouseReleased}.
     */
    private @Nullable Panel capturedPanel;

    /** Resolved id of the layout currently shown in the workspace. Read by status-bar / picker UI for display. */
    public static String activeLayoutId() {
        return WorkspaceLayoutController.activeLayoutId();
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
            initialWrapped = new TitleScreen();
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
            this.root = WorkspaceLayoutController.buildOuterLayout(bodyRoot);
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
        commands.dispatch(new Command.RequestFactionDirectory());

        var bodyRoot = loadActiveLayoutBody();
        this.root = WorkspaceLayoutController.buildOuterLayout(bodyRoot);
    }

    /**
     * Resolve the active layout id (per-project override → global → default fallback), load its {@link LayoutDoc}, and
     * hydrate the body subtree. Falls back to {@link LayoutTemplate#DEFAULT}'s code-baked body if the resolved id has
     * no readable file on disk — guarantees the workspace always opens to <em>something</em>.
     */
    private DockNode loadActiveLayoutBody() {
        return WorkspaceLayoutController.loadActiveLayoutBody(panelCtx());
    }

    private PanelRegistry.Context panelCtx() {
        return new PanelRegistry.Context(
            viewportContextMenu,
            viewportContextMenu,
            this::openContentConfirm,
            menuBar::open
        );
    }

    /**
     * {@link ProjectContentActionHandler} adapter — the content browser asks the screen to spawn a destructive confirm
     * dialog. Wired into both {@link #panelCtx} and the Window-menu's "Reopen Project Contents" so a panel created via
     * either path gets the same modal behavior.
     */
    private void openContentConfirm(String title, String message, String confirmLabel, boolean destructive, Runnable onConfirm) {
        dialogs.setConfirmDialog(new ConfirmDialog(title, message, confirmLabel, "Cancel", destructive, onConfirm, () -> {}));
    }

    /**
     * Switch the active layout to {@code newLayoutId}. Persists the outgoing layout's body to disk before switching so
     * any in-session customisations carry over to the next reopen, then loads the incoming layout.
     */
    private void switchLayout(String newLayoutId) {
        var newRoot = WorkspaceLayoutController.switchLayout(newLayoutId, this.root, panelCtx());
        if (newRoot == null) {
            return;
        }
        this.root = newRoot;
        var mld = dialogs.manageLayoutsDialog();
        if (mld != null) {
            mld.setActiveLayoutId(WorkspaceLayoutController.activeLayoutId());
            mld.refresh();
        }
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

    /**
     * Used by the setScreen redirect mixin to recognise the explicit close path and stand down. Backed by
     * {@link EngineRenderState#isPreparingToClose()} — exposed here as a shim so existing call sites within the screen
     * keep working unchanged.
     */
    public static boolean isPreparingToClose() {
        return EngineRenderState.isPreparingToClose();
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
            next = new TitleScreen();
        }
        var prev = this.wrappedScreen;
        this.wrappedScreen = next;
        this.wrappedScreenAdded = false;
        if (prev != null && prev != next) {
            prev.removed();
        }
        if (next != null && next != prev && minecraft != null) {
            next.added();
            BufferUploader.reset();
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
        EngineRenderState.setPreparingToClose(true);
        try {
            Minecraft.getInstance().setScreen(null);
        } finally {
            EngineRenderState.setPreparingToClose(false);
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
            BufferUploader.reset();
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
            EngineRenderState.setInWrappedScreenRender(true);
            try {
                wrappedScreen.renderWithTooltip(graphics, wrappedMouseX, wrappedMouseY, partialTick);
                graphics.flush();
            } finally {
                EngineRenderState.setInWrappedScreenRender(false);
                mainRT.bindWrite(true);
            }
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        // In-world path reads the main RT (which holds vanilla's world + HUD render). Menu-overlay path reads the
        // dedicated wrappedScreenRT instead. Both feed the same downsample → viewport-rect blit pattern.
        var viewportRect = findViewportRect(root, 0, 0, logicalWidth(), logicalHeight());
        if (viewportRect != null) {
            if (wrappedScreen != null) {
                EngineWorkspaceCompositor.compositWrappedIntoLogicalRect(
                    viewportRect.x(),
                    viewportRect.y(),
                    viewportRect.width(),
                    viewportRect.height(),
                    SCALE
                );
            } else {
                EngineWorkspaceCompositor.compositWorldIntoLogicalRect(
                    viewportRect.x(),
                    viewportRect.y(),
                    viewportRect.width(),
                    viewportRect.height(),
                    SCALE
                );
            }
        }

        var pose = graphics.pose();
        pose.pushPose();
        pose.scale(SCALE, SCALE, 1.0f);

        int logicalWidth = logicalWidth();
        int logicalHeight = logicalHeight();
        int logicalMouseX = (int) (mouseX / SCALE);
        int logicalMouseY = (int) (mouseY / SCALE);

        // Hover-driven submenu spawning. Runs once per frame so the user can mouse over a parent-menu item and see
        // its submenu cascade without clicking. Must come BEFORE the "is cursor over a menu" panel-mouse-suppress
        // checks below so the just-spawned submenu is considered when masking panel hover state.
        menuBar.updateHoverSubmenu(logicalMouseX, logicalMouseY, logicalWidth, logicalHeight);

        // While a dropdown menu is open AND the cursor is over the menu rect, panels under the menu must not see the
        // mouse — otherwise their hover-state code (segmented-control buttons, viewport selection highlights, tab-
        // strip hover, content-browser cells, etc.) lights up under the menu, leaking interaction state through the
        // popover. Substituting a sentinel "off-screen" mouse position for the panel render walk makes every panel's
        // contains-point check fail uniformly without requiring each panel to know about the menu. Divider / tab-drag
        // / tooltip overlays already check {@code openMenu} themselves and stay suppressed.
        int panelMouseX = logicalMouseX;
        int panelMouseY = logicalMouseY;
        if (menuBar.isInsideOpenMenu(logicalMouseX, logicalMouseY)) {
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
        if (dialogs.isAnyOpen()) {
            panelMouseX = OFFSCREEN_MOUSE;
            panelMouseY = OFFSCREEN_MOUSE;
        }

        renderNode(graphics, root, 0, 0, logicalWidth, logicalHeight, panelMouseX, panelMouseY, partialTick);
        // Use the OFFSCREEN-substituted coords so dividers and tab-drag indicators don't light up under an open modal.
        HoverOverlayRenderer.renderHoveredDivider(
            graphics,
            root,
            logicalWidth,
            logicalHeight,
            panelMouseX,
            panelMouseY,
            DIVIDER_HIT_PX,
            dragController.isActive(),
            dragController.active()
        );
        tabDrag.renderOverlay(graphics, panelMouseX, panelMouseY, root, logicalWidth, logicalHeight);

        menuBar.render(graphics, logicalMouseX, logicalMouseY);
        // Modal dialogs render after panels in z-order so a child modal (e.g. a Delete-Profile confirm spawned from
        // Preferences) sits on top of its parent. Open/close lifecycle is managed by the dialog controller.
        dialogs.render(graphics, logicalWidth, logicalHeight, logicalMouseX, logicalMouseY);
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

        // Tooltips are suppressed while any visible overlay (menu, popup, drag) is active so the box doesn't fight
        // with the overlay. The screen knows about all of these; the helper just renders what's allowed.
        var tooltipsAllowed = !menuBar.isInsideOpenMenu(logicalMouseX, logicalMouseY)
            && !dragController.isActive()
            && !tabDrag.isActive()
            && HslColorPickerPopup.getOpenPopup() == null
            && FactionManagePopup.getOpenPopup() == null;
        HoverOverlayRenderer.renderHoverTooltip(
            graphics,
            root,
            logicalWidth,
            logicalHeight,
            logicalMouseX,
            logicalMouseY,
            tooltipsAllowed
        );

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
        return dialogs.topTag() != null
            || menuBar.isAnyMenuOpen()
            || SearchableSelect.getOpenPopup() != null
            || HslColorPickerPopup.getOpenPopup() != null
            || FactionManagePopup.getOpenPopup() != null
            || TextInput.getFocused() != null;
    }

    @Override
    public void removed() {
        super.removed();

        // Persist the active layout's customized state to disk and update state.json so the next /blib engine — even
        // across game restarts — reopens to the same arrangement of tabs, splits, and active panels. The active-id
        // write also captures any per-project memory so switching projects later restores per-project preferences.
        // Skipped in MENU_OVERLAY mode so a B-toggle from the title screen doesn't clobber the user's in-game layout.
        if (mode == Mode.IN_GAME) {
            WorkspaceLayoutPersistence.persistOutgoingLayout(
                this.root,
                WorkspaceLayoutController.activeLayoutId()
            );
            WorkspaceLayoutPersistence.persistActiveSelection(
                WorkspaceLayoutController.activeLayoutId()
            );
        } else if (wrappedScreen != null) {
            wrappedScreen.removed();
            wrappedScreen = null;
        }

        // Workspace-screen-scoped UI state (compositor, popups, cursor) — not session-scoped, so it stays here.
        EngineWorkspaceCompositor.clear();
        SearchableSelect.closeOpenPopup();
        HslColorPickerPopup.closeOpenPopup();
        FactionManagePopup.closeOpenPopup();
        EngineCursor.reset();

        // Tear down the session — all session-scoped singletons (selection, gizmos, caches) register their cleanup
        // on EngineMode.enter()'s session scope and unwind in LIFO order here. Adding a new transient singleton means
        // one scope.onClose() line in EngineMode.enter() and zero edits to this method.
        EngineMode.get().exit();

        // Restore the integrated server's previous freeze state — the lazy-enter path in render() called
        // captureAndPause once the world loaded, so we have to pair it with a restore on close. No-op when
        // captureAndPause never ran (engine closed before any world was loaded).
        EngineTickControl.restore();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // Top modal absorbs / handles scroll. Most modals just absorb (no scrollable content); preferences handles
        // its own list scroll.
        if (dialogs.isAnyOpen()) {
            if (dialogs.handleMouseScrolled(mouseX / SCALE, mouseY / SCALE, scrollX, scrollY)) {
                return true;
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
        if (menuBar.isInsideOpenMenu(logicalX, logicalY)) {
            return true;
        }
        var leaf = DockTreeHitTest.panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
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
        // Route char input through any open modal — only the two text-input modals (LayoutName, Preferences) consume
        // chars; the rest absorb them so background panels don't receive keystrokes under the dim.
        if (dialogs.isAnyOpen()) {
            var lnd = dialogs.layoutNameDialog();
            if (lnd != null) {
                return lnd.charTyped(ch, modifiers);
            }
            var pd = dialogs.preferencesDialog();
            if (pd != null) {
                return pd.charTyped(ch, modifiers);
            }
            return true;
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
        // spawned over manage-layouts, etc.) sit above their parent so the topmost gets first crack.
        if (dialogs.handleKeyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (dialogs.isAnyOpen()) {
            return true;
        }
        // Esc closes an open SearchableSelect popup BEFORE TextInput dispatch — otherwise the popup's focused
        // search input would consume Esc as "defocus" and leave the popup visible-but-unfocused, which is confusing.
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && SearchableSelect.getOpenPopup() != null) {
            SearchableSelect.closeOpenPopup();
            TextInput.clearFocus();
            return true;
        }
        // Esc closes an open color-picker popup too.
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && HslColorPickerPopup.getOpenPopup() != null) {
            HslColorPickerPopup.closeOpenPopup();
            return true;
        }
        // Same for the faction-management popup.
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && FactionManagePopup.getOpenPopup() != null) {
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
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (ClaimPaintTool.isActive()) {
                ClaimPaintTool.deactivate();
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
                setWrappedScreen(new PauseScreen(true));
                return true;
            }
        }

        // Delegate to the workspace hotkey dispatcher — undo/redo, reload, play-pause, jigsaw R/M/T, modeler gizmos,
        // world gizmos, clipboard, Delete. Returns true if any hotkey consumed the key.
        if (hotkeys.dispatch(keyCode, modifiers)) {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Delete a single inspected block by reusing the volume-delete packet with a degenerate one-block AABB. Avoids a
     * parallel "delete one block" packet — the server's volume delete already special-cases tiny volumes, and routing
     * through the same handler keeps op-gating + edit logging consistent. The
     * {@link com.blib.engine.domain.selection.picking.BlockSelectable#isValid} check that prunes the now-air block from
     * {@link SelectionManager} fires naturally on the next read, so no explicit clear is needed here.
     */
    private void deleteSingleBlock(BlockPos pos) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        var dim = mc.player.level().dimension().location();
        commands.dispatch(new Command.DeleteBlockVolume(pos, pos, dim));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var logicalX = mouseX / SCALE;
        var logicalY = mouseY / SCALE;

        // Top-of-stack modal absorbs the click — sub-dialogs (Delete confirm spawned from Preferences, etc.) sit
        // above their parent in z-order, so the topmost gets first crack. Outside-clicks are still swallowed by
        // returning true so they don't reach panels under the dim.
        if (dialogs.handleMouseClicked(logicalX, logicalY, button)) {
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
        // 0) An open dropdown takes priority. The controller handles item dispatch + submenu cascade entirely; the
        // CLOSED_TRY_CHIP_REOPEN outcome only falls through if the cursor landed on another menu chip, so
        // close-and-reopen across chips in one click still works.
        var menuOutcome = menuBar.handleClick(logicalX, logicalY, button, logicalWidth(), logicalHeight());
        if (menuOutcome == MenuBarController.ClickOutcome.CONSUMED) {
            return true;
        }
        if (menuOutcome == MenuBarController.ClickOutcome.CLOSED_TRY_CHIP_REOPEN) {
            var underClose = DockTreeHitTest.panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
            if (!(underClose instanceof MenuBarPanel menuBarUnderClose) || menuBarUnderClose.hitChipAt(logicalX, logicalY) == null) {
                return true;
            }
        }

        if (button == 0) {
            // 1) Menu-bar chip click: open dropdown.
            var underCursor = DockTreeHitTest.panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
            if (underCursor instanceof MenuBarPanel menuBarPanel) {
                var chip = menuBarPanel.hitChipAt(logicalX, logicalY);
                if (chip != null) {
                    var menu = buildMenuFor(chip, menuBarPanel);
                    if (menu != null) {
                        menuBar.open(menu);
                    }
                    return true;
                }
            }

            // 2) Panel-internal high-priority UI (scrollbar thumb, close buttons, etc.). Runs before the divider so a
            // scrollbar at the right edge of a panel adjacent to a vertical dock split isn't eaten by divider drag.
            // A true return also captures subsequent drag / release for this panel — see #capturedPanel.
            var preDivider = DockTreeHitTest.panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
            if (preDivider != null && preDivider.mouseClickedCapture(logicalX, logicalY, button)) {
                this.capturedPanel = preDivider;
                return true;
            }

            // 3) Divider drag start — must run before the tab-strip check so the bottom rows of the visible divider
            // highlight (which fall inside the adjacent panel's title bar / tab strip) can still start a drag instead
            // of leaking into a tab-strip click. Scrollbar / edge UI is already protected by step 2's panel capture.
            var divider = DockTreeHitTest.findDivider(
                root,
                0,
                0,
                logicalWidth(),
                logicalHeight(),
                (int) logicalX,
                (int) logicalY,
                DIVIDER_HIT_PX
            );
            if (divider != null) {
                this.dragController.begin(divider);
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
                    this.tabDrag.begin(tabbed, tabIdx, tabbed.tabs().get(tabIdx), logicalX, logicalY);
                    return true;
                }
                // Click on empty tab-strip space — no-op but consume so it doesn't fall through to content.
                return true;
            }
        }

        // 5) Otherwise, delegate to the panel under the cursor for content-area handling.
        var leaf = DockTreeHitTest.panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);

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
        if (dialogs.isAnyOpen()) {
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

        if (button == 0 && tabDrag.isPending()) {
            if (tabDrag.isActive()) {
                this.root = tabDrag.completeDrop(logicalX, logicalY, this.root, logicalWidth(), logicalHeight());
                simplifyDockTree();
            }
            tabDrag.cancel();
            return true;
        }

        if (button == 0 && dragController.isActive()) {
            dragController.end();
            return true;
        }

        // Releases over an open menu shouldn't reach panels below it.
        if (menuBar.isInsideOpenMenu(logicalX, logicalY)) {
            return true;
        }

        var leaf = DockTreeHitTest.panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
        if (leaf != null && leaf.mouseReleased(logicalX, logicalY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Top modal absorbs / handles drags. Preferences forwards drags (for its TextInput); other modals just absorb.
        if (dialogs.isAnyOpen()) {
            var pd = dialogs.preferencesDialog();
            if (pd != null) {
                return pd.mouseDragged(mouseX / SCALE, mouseY / SCALE, button, deltaX, deltaY);
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
        if (wrappedScreen != null && !engineModalAbsorbing() && capturedPanel == null && !dragController.isActive()) {
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

        if (tabDrag.isPending()) {
            tabDrag.promoteIfFarEnough(logicalX, logicalY, TAB_DRAG_THRESHOLD_SQ);
            return true;
        }

        if (dragController.apply(logicalX, logicalY)) {
            return true;
        }

        // Drags over an open menu shouldn't hit panels below it.
        if (menuBar.isInsideOpenMenu(logicalX, logicalY)) {
            return true;
        }

        var leaf = DockTreeHitTest.panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
        if (leaf != null && leaf.mouseDragged(logicalX, logicalY, button, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    /**
     * True when the active layout contains at least one modeler panel (viewport / outliner / inspector). Used by the
     * Delete handler to gate modeler-scene delete behavior — the modeler scene state is global, but Delete should only
     * dispatch to it when the user's actually looking at modeler UI, not when they happen to have a stale modeler
     * selection in some unrelated layout.
     */
    public boolean layoutHasModelerPanel() {
        return WorkspaceLayoutController.hasModelerPanel(root);
    }

    /**
     * Convenience for callers without a workspace reference (e.g. the action-stack panel). Delegates to the controller,
     * which checks the active screen.
     */
    public static boolean activeLayoutHasModelerPanel() {
        return WorkspaceLayoutController.activeLayoutHasModelerPanel();
    }

    private @Nullable TabbedPanel findTabbedPanelAt(int mouseX, int mouseY) {
        return DockTreeHitTest.findTabbedPanelAt(root, logicalWidth(), logicalHeight(), mouseX, mouseY);
    }

    /**
     * Build the dropdown menu for the clicked menu-bar chip. Anchored just below the chip's screen rect. Returns
     * {@code null} for chips with no menu wired up.
     */
    private @Nullable DropdownMenu buildMenuFor(String chipName, MenuBarPanel menuBarPanel) {
        var chipRect = menuBarPanel.chipRect(chipName);
        if (chipRect == null) {
            return null;
        }
        var anchorX = chipRect.x();
        var anchorY = chipRect.y() + chipRect.height() + 1;

        return switch (chipName) {
            case MenuBarPanel.CHIP_PROJECT -> menuBar.buildProjectMenu(anchorX, anchorY);
            case MenuBarPanel.CHIP_EDIT -> menuBar.buildEditMenu(anchorX, anchorY);
            case MenuBarPanel.CHIP_VIEW -> menuBar.buildViewMenu(anchorX, anchorY);
            case MenuBarPanel.CHIP_WINDOW -> menuBar.buildWindowMenu(anchorX, anchorY);
            case MenuBarPanel.CHIP_LAYOUT -> menuBar.buildLayoutMenu(anchorX, anchorY);
            default -> null;
        };
    }

    /**
     * Glue between {@link MenuBarController} and this screen — each menu item's action delegates back through this
     * inner class so the controller has no direct dependency on {@link EngineWorkspaceScreen}.
     */
    private final class MenuBarActionsImpl implements MenuBarController.Actions {

        @Override
        public void dispatchUndo() {
            EngineWorkspaceScreen.this.dispatchUndo();
        }

        @Override
        public void dispatchRedo() {
            EngineWorkspaceScreen.this.dispatchRedo();
        }

        @Override
        public void openPreferencesDialog() {
            EngineWorkspaceScreen.this.openPreferencesDialog();
        }

        @Override
        public void switchLayout(String id) {
            EngineWorkspaceScreen.this.switchLayout(id);
        }

        @Override
        public void openSaveAsDialog() {
            EngineWorkspaceScreen.this.openSaveAsDialog();
        }

        @Override
        public void openRenameDialog() {
            EngineWorkspaceScreen.this.openRenameDialog();
        }

        @Override
        public void openDuplicateDialog() {
            EngineWorkspaceScreen.this.openDuplicateDialog();
        }

        @Override
        public void openDeleteConfirm() {
            EngineWorkspaceScreen.this.openDeleteConfirm();
        }

        @Override
        public void resetLayout() {
            EngineWorkspaceScreen.this.resetLayout();
        }

        @Override
        public void openNewFromTemplateDialog(LayoutTemplate template) {
            EngineWorkspaceScreen.this.openNewFromTemplateDialog(template);
        }

        @Override
        public void openManageLayoutsDialog() {
            EngineWorkspaceScreen.this.openManageLayoutsDialog();
        }

        @Override
        public void openLayoutsFolder() {
            EngineWorkspaceScreen.this.openLayoutsFolder();
        }

        @Override
        public PanelRegistry.Context panelCtx() {
            return EngineWorkspaceScreen.this.panelCtx();
        }

        @Override
        public void reopenPanel(Class<? extends Panel> panelClass, Supplier<Panel> factory) {
            EngineWorkspaceScreen.this.reopenPanel(panelClass, factory);
        }

        @Override
        public void openPicker(boolean createMode) {
            EngineWorkspaceScreen.this.openPicker(createMode);
        }

        @Override
        public void reloadProject() {
            commands.dispatch(new Command.ReloadProject(ProjectSession.activeProjectName()));
            // Wipe the tag-staging overlay — reload makes the runtime registry catch up to disk, so the red staging
            // tint is no longer meaningful (rows settle into green / blue based on committed state).
            TagStagingCache.clear();
        }

        @Override
        public void requestDeleteProjectConfirm(String projectName, Runnable onConfirm) {
            dialogs.setConfirmDialog(
                new ConfirmDialog(
                    "Delete project?",
                    "Are you sure you want to delete '" + projectName + "'? This will remove the entire datapack folder "
                        + "from the world's datapacks directory and cannot be undone.",
                    "Delete",
                    "Cancel",
                    true,
                    onConfirm,
                    () -> {}
                )
            );
        }

        @Override
        public void deleteProject(String name) {
            commands.dispatch(new Command.DeleteProject(name));
        }

        @Override
        public void closeEngine() {
            EngineWorkspaceScreen.closeEngine();
        }

        @Override
        public String activeLayoutId() {
            return WorkspaceLayoutController.activeLayoutId();
        }
    }

    private void dispatchUndo() {
        if (layoutHasModelerPanel()) {
            ModelerActionHistory.undo();
        } else {
            commands.dispatch(new Command.UndoAction());
        }
    }

    private void dispatchRedo() {
        if (layoutHasModelerPanel()) {
            ModelerActionHistory.redo();
        } else {
            commands.dispatch(new Command.RedoAction());
        }
    }

    private void openPreferencesDialog() {
        dialogs.setPreferencesDialog(
            new PreferencesDialog(
                () -> dialogs.setPreferencesDialog(null),
                this::openPreferencesNewProfile,
                this::openPreferencesRenameProfile,
                this::openPreferencesDuplicateProfile,
                this::openPreferencesDeleteProfile
            )
        );
    }

    private void openPreferencesNewProfile() {
        dialogs.setLayoutNameDialog(
            new LayoutNameDialog(
                LayoutNameDialog.Mode.PROFILE_NEW,
                "",
                id -> KeybindingProfileCatalog.idAvailable(id),
                this::confirmCreateProfile,
                () -> dialogs.setLayoutNameDialog(null)
            )
        );
    }

    private void confirmCreateProfile(String displayName) {
        dialogs.setLayoutNameDialog(null);
        try {
            var id = KeybindingProfileCatalog.suggestId(displayName);
            var profile = KeybindingProfile.empty(id, displayName);
            KeybindingProfileCatalog.save(profile);
            KeybindingProfileCatalog.setActive(id);
            var pd = dialogs.preferencesDialog();
            if (pd != null) {
                pd.refreshProfiles();
                pd.loadProfile(id);
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] confirmCreateProfile: write failed", e);
        }
    }

    private void openPreferencesRenameProfile(KeybindingProfile profile) {
        dialogs.setLayoutNameDialog(
            new LayoutNameDialog(
                LayoutNameDialog.Mode.PROFILE_RENAME,
                profile.displayName(),
                id -> KeybindingProfileCatalog.idAvailable(id) || id.equals(profile.id()),
                newName -> confirmRenameProfile(profile, newName),
                () -> dialogs.setLayoutNameDialog(null)
            )
        );
    }

    private void confirmRenameProfile(KeybindingProfile profile, String newDisplayName) {
        dialogs.setLayoutNameDialog(null);
        try {
            var renamed = profile.withDisplayName(newDisplayName);
            KeybindingProfileCatalog.save(renamed);
            var pd = dialogs.preferencesDialog();
            if (pd != null) {
                pd.refreshProfiles();
                pd.loadProfile(profile.id());
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] confirmRenameProfile: write failed", e);
        }
    }

    private void openPreferencesDuplicateProfile(KeybindingProfile profile) {
        var initial = profile.displayName() + " (copy)";
        dialogs.setLayoutNameDialog(
            new LayoutNameDialog(
                LayoutNameDialog.Mode.PROFILE_DUPLICATE,
                initial,
                KeybindingProfileCatalog::idAvailable,
                newName -> confirmDuplicateProfile(profile, newName),
                () -> dialogs.setLayoutNameDialog(null)
            )
        );
    }

    private void confirmDuplicateProfile(KeybindingProfile source, String newDisplayName) {
        dialogs.setLayoutNameDialog(null);
        try {
            var newId = KeybindingProfileCatalog.suggestId(newDisplayName);
            var copy = new KeybindingProfile(KeybindingProfile.CURRENT_VERSION, newId, newDisplayName, source.overrides());
            KeybindingProfileCatalog.save(copy);
            KeybindingProfileCatalog.setActive(newId);
            var pd = dialogs.preferencesDialog();
            if (pd != null) {
                pd.refreshProfiles();
                pd.loadProfile(newId);
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] confirmDuplicateProfile: write failed", e);
        }
    }

    private void openPreferencesDeleteProfile(KeybindingProfile profile) {
        dialogs.setConfirmDialog(
            new ConfirmDialog(
                "Delete Profile",
                "Delete profile '" + profile.displayName() + "'? This cannot be undone.",
                "Delete",
                "Cancel",
                true,
                () -> confirmDeleteProfile(profile),
                () -> dialogs.setConfirmDialog(null)
            )
        );
    }

    private void confirmDeleteProfile(KeybindingProfile profile) {
        dialogs.setConfirmDialog(null);
        try {
            KeybindingProfileCatalog.delete(profile.id());
            var pd = dialogs.preferencesDialog();
            if (pd != null) {
                pd.refreshProfiles();
                pd.loadProfile(KeybindingProfileCatalog.getActive().id());
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] confirmDeleteProfile: delete failed", e);
        }
    }

    /**
     * If a panel of {@code panelClass} already exists somewhere in the dock tree, switch its containing
     * {@link TabbedPanel} to that tab and return. Otherwise create a fresh instance via {@code factory} and append it
     * as a tab in the first {@link TabbedPanel} found via depth-first walk. If no {@code TabbedPanel} exists at all
     * (the user has closed everything), the action is a no-op and the user can use Reset Layout to recover.
     */
    private void reopenPanel(Class<? extends Panel> panelClass, Supplier<Panel> factory) {
        WorkspaceLayoutController.reopenPanel(this.root, panelClass, factory);
    }

    private void resetLayout() {
        var newRoot = WorkspaceLayoutController.resetLayout(this.root, panelCtx());
        if (newRoot != null) {
            this.root = newRoot;
        }
    }

    private void openSaveAsDialog() {
        dialogs.setLayoutNameDialog(
            new LayoutNameDialog(
                LayoutNameDialog.Mode.SAVE_AS,
                "",
                LayoutCatalog::idAvailable,
                displayName -> {
                    var newId = LayoutCatalog.suggestId(displayName);
                    var capturedBody = LayoutSnapshot.capture(
                        WorkspaceLayoutPersistence.extractBodyRoot(this.root)
                    );
                    var now = Instant.now().toString();
                    var templateBase = LayoutCatalog.isTemplateId(WorkspaceLayoutController.activeLayoutId())
                        ? WorkspaceLayoutController.activeLayoutId()
                        : null;
                    var doc = new LayoutDoc(LayoutDoc.CURRENT_VERSION, newId, displayName, templateBase, now, now, capturedBody);
                    try {
                        LayoutCatalog.save(doc);
                        WorkspaceLayoutController.setActiveLayoutId(newId);
                        WorkspaceLayoutPersistence.persistActiveSelection(newId);
                    } catch (IOException e) {
                        LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Save As failed", e);
                    }
                    dialogs.setLayoutNameDialog(null);
                },
                () -> dialogs.setLayoutNameDialog(null)
            )
        );
    }

    private void openRenameDialog() {
        var current = LayoutCatalog.get(WorkspaceLayoutController.activeLayoutId());
        if (current == null) {
            return;
        }
        // Rename can collide on its own id (no-op rename → keep the existing); availability check excludes the
        // current id so the user can confirm without an "already exists" complaint when they only edited casing.
        var currentId = WorkspaceLayoutController.activeLayoutId();
        dialogs.setLayoutNameDialog(
            new LayoutNameDialog(
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
                            WorkspaceLayoutController.setActiveLayoutId(newId);
                            WorkspaceLayoutPersistence.persistActiveSelection(
                                WorkspaceLayoutController.activeLayoutId()
                            );
                        }
                    } catch (IOException e) {
                        LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Rename failed", e);
                    }
                    if (dialogs.manageLayoutsDialog() != null) {
                        dialogs.manageLayoutsDialog().setActiveLayoutId(WorkspaceLayoutController.activeLayoutId());
                        dialogs.manageLayoutsDialog().refresh();
                    }
                    dialogs.setLayoutNameDialog(null);
                },
                () -> dialogs.setLayoutNameDialog(null)
            )
        );
    }

    private void openDuplicateDialog() {
        var current = LayoutCatalog.get(WorkspaceLayoutController.activeLayoutId());
        if (current == null) {
            return;
        }
        dialogs.setLayoutNameDialog(
            new LayoutNameDialog(
                LayoutNameDialog.Mode.DUPLICATE,
                current.displayName() + " Copy",
                LayoutCatalog::idAvailable,
                displayName -> {
                    var newId = LayoutCatalog.suggestId(displayName);
                    var now = Instant.now().toString();
                    var capturedBody = LayoutSnapshot.capture(
                        WorkspaceLayoutPersistence.extractBodyRoot(this.root)
                    );
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
                        WorkspaceLayoutController.setActiveLayoutId(newId);
                        WorkspaceLayoutPersistence.persistActiveSelection(newId);
                    } catch (IOException e) {
                        LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Duplicate failed", e);
                    }
                    var mld = dialogs.manageLayoutsDialog();
                    if (mld != null) {
                        mld.setActiveLayoutId(WorkspaceLayoutController.activeLayoutId());
                        mld.refresh();
                    }
                    dialogs.setLayoutNameDialog(null);
                },
                () -> dialogs.setLayoutNameDialog(null)
            )
        );
    }

    private void openDeleteConfirm() {
        var current = LayoutCatalog.get(WorkspaceLayoutController.activeLayoutId());
        if (current == null) {
            return;
        }
        var isTemplate = LayoutCatalog.isTemplateId(WorkspaceLayoutController.activeLayoutId());
        var message = isTemplate
            ? "Delete '" + current.displayName()
                + "'? This is a template — it will be re-seeded with default content next time the workspace opens."
            : "Delete '" + current.displayName() + "'? This cannot be undone.";
        var deletedId = WorkspaceLayoutController.activeLayoutId();
        dialogs.setConfirmDialog(
            new ConfirmDialog(
                "Delete layout?",
                message,
                "Delete",
                "Cancel",
                true,
                () -> {
                    try {
                        LayoutCatalog.delete(deletedId);
                    } catch (IOException e) {
                        LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Delete failed", e);
                    }
                    var state = ActiveLayoutState.read();
                    ActiveLayoutState.write(state.withoutLayout(deletedId));
                    // Switch off the deleted layout to whatever resolves now (default fallback if nothing else).
                    var newId = ActiveLayoutState.resolve(
                        ProjectSession.activeProject() != null ? ProjectSession.activeProjectName() : null,
                        ActiveLayoutState.read()
                    );
                    WorkspaceLayoutController.setActiveLayoutId(LayoutTemplate.DEFAULT.id()); // Force the equality
                                                                                              // check in switchLayout
                                                                                              // to fire.
                    switchLayout(newId);
                    var mld = dialogs.manageLayoutsDialog();
                    if (mld != null) {
                        mld.setActiveLayoutId(WorkspaceLayoutController.activeLayoutId());
                        mld.refresh();
                    }
                },
                () -> {}
            )
        );
    }

    private void openNewFromTemplateDialog(LayoutTemplate template) {
        dialogs.setLayoutNameDialog(
            new LayoutNameDialog(
                LayoutNameDialog.Mode.NEW_FROM_TEMPLATE,
                template.displayName() + " (custom)",
                LayoutCatalog::idAvailable,
                displayName -> {
                    var newId = LayoutCatalog.suggestId(displayName);
                    var now = Instant.now().toString();
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
                        WorkspaceLayoutPersistence.persistOutgoingLayout(
                            this.root,
                            WorkspaceLayoutController.activeLayoutId()
                        );
                        WorkspaceLayoutController.setActiveLayoutId(newId);
                        var loaded = LayoutCatalog.get(newId);
                        var bodyRoot = LayoutSnapshot.hydrate(
                            loaded != null ? loaded.body() : doc.body(),
                            panelCtx()
                        );
                        this.root = WorkspaceLayoutController.buildOuterLayout(bodyRoot);
                        WorkspaceLayoutPersistence.persistActiveSelection(
                            WorkspaceLayoutController.activeLayoutId()
                        );
                    } catch (IOException e) {
                        LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] New from Template failed", e);
                    }
                    var mld = dialogs.manageLayoutsDialog();
                    if (mld != null) {
                        mld.setActiveLayoutId(WorkspaceLayoutController.activeLayoutId());
                        mld.refresh();
                    }
                    dialogs.setLayoutNameDialog(null);
                },
                () -> dialogs.setLayoutNameDialog(null)
            )
        );
    }

    private void openManageLayoutsDialog() {
        dialogs.setManageLayoutsDialog(
            new ManageLayoutsDialog(
                WorkspaceLayoutController.activeLayoutId(),
                () -> dialogs.setManageLayoutsDialog(null),
                doc -> switchLayout(doc.id()),
                this::renameFromManage,
                this::duplicateFromManage,
                this::deleteFromManage
            )
        );
    }

    private void openLayoutsFolder() {
        try {
            LayoutStorage.ensureRootExists();
        } catch (IOException ignored) {
            // Best-effort — openUri below will fail loudly if the directory still isn't reachable.
        }
        Util.getPlatform().openUri(LayoutStorage.layoutsRoot().toUri());
    }

    private void renameFromManage(LayoutDoc doc) {
        var existingId = doc.id();
        dialogs.setLayoutNameDialog(
            new LayoutNameDialog(
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
                            if (existingId.equals(WorkspaceLayoutController.activeLayoutId())) {
                                WorkspaceLayoutController.setActiveLayoutId(newId);
                                WorkspaceLayoutPersistence.persistActiveSelection(
                                    WorkspaceLayoutController.activeLayoutId()
                                );
                            }
                        }
                    } catch (IOException e) {
                        LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Rename (manage) failed", e);
                    }
                    var mld = dialogs.manageLayoutsDialog();
                    if (mld != null) {
                        mld.setActiveLayoutId(WorkspaceLayoutController.activeLayoutId());
                        mld.refresh();
                    }
                    dialogs.setLayoutNameDialog(null);
                },
                () -> dialogs.setLayoutNameDialog(null)
            )
        );
    }

    private void duplicateFromManage(LayoutDoc doc) {
        dialogs.setLayoutNameDialog(
            new LayoutNameDialog(
                LayoutNameDialog.Mode.DUPLICATE,
                doc.displayName() + " Copy",
                LayoutCatalog::idAvailable,
                displayName -> {
                    var newId = LayoutCatalog.suggestId(displayName);
                    var now = Instant.now().toString();
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
                    } catch (IOException e) {
                        LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Duplicate (manage) failed", e);
                    }
                    var mld = dialogs.manageLayoutsDialog();
                    if (mld != null) {
                        mld.refresh();
                    }
                    dialogs.setLayoutNameDialog(null);
                },
                () -> dialogs.setLayoutNameDialog(null)
            )
        );
    }

    private void deleteFromManage(LayoutDoc doc) {
        var deletedId = doc.id();
        var isTemplate = LayoutCatalog.isTemplateId(deletedId);
        var message = isTemplate
            ? "Delete '" + doc.displayName()
                + "'? This is a template — it will be re-seeded with default content next time the workspace opens."
            : "Delete '" + doc.displayName() + "'? This cannot be undone.";
        dialogs.setConfirmDialog(
            new ConfirmDialog(
                "Delete layout?",
                message,
                "Delete",
                "Cancel",
                true,
                () -> {
                    try {
                        LayoutCatalog.delete(deletedId);
                    } catch (IOException e) {
                        LoggerFactory.getLogger(EngineWorkspaceScreen.class).warn("[BLib] Delete (manage) failed", e);
                    }
                    var state = ActiveLayoutState.read();
                    ActiveLayoutState.write(state.withoutLayout(deletedId));
                    if (deletedId.equals(WorkspaceLayoutController.activeLayoutId())) {
                        var newId = ActiveLayoutState.resolve(
                            ProjectSession.activeProject() != null ? ProjectSession.activeProjectName() : null,
                            ActiveLayoutState.read()
                        );
                        WorkspaceLayoutController.setActiveLayoutId(LayoutTemplate.DEFAULT.id());
                        switchLayout(newId);
                    }
                    var mld = dialogs.manageLayoutsDialog();
                    if (mld != null) {
                        mld.setActiveLayoutId(WorkspaceLayoutController.activeLayoutId());
                        mld.refresh();
                    }
                },
                () -> {}
            )
        );
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
    /** Bridges the workspace screen to the {@link ViewportContextMenuHandler}'s host hooks. */
    /** Bridges {@link WorkspaceHotkeyDispatcher} to this screen's command bus and modeler-panel introspection. */
    private final class WorkspaceHotkeyHostImpl implements WorkspaceHotkeyDispatcher.Host {

        @Override
        public CommandBus commands() {
            return EngineWorkspaceScreen.this.commands;
        }

        @Override
        public boolean layoutHasModelerPanel() {
            return EngineWorkspaceScreen.this.layoutHasModelerPanel();
        }

        @Override
        public void deleteSingleBlock(BlockPos pos) {
            EngineWorkspaceScreen.this.deleteSingleBlock(pos);
        }
    }

    private final class ViewportContextMenuHostImpl implements ViewportContextMenuHandler.Host {

        @Override
        public void openMenu(DropdownMenu menu) {
            menuBar.open(menu);
        }

        @Override
        public void closeMenu() {
            menuBar.closeAll();
        }

        @Override
        public void reopenPanel(Class<? extends Panel> panelClass, Supplier<Panel> factory) {
            EngineWorkspaceScreen.this.reopenPanel(panelClass, factory);
        }

        @Override
        public void openCaptureDialog() {
            dialogs.setCaptureDialog(new CaptureDialog(() -> dialogs.setCaptureDialog(null)));
        }
    }

    /**
     * After a tab close or move, walk the dock tree and replace any {@link DockNode.Split} whose child is an empty
     * {@link TabbedPanel} with the non-empty sibling, so the surviving panel grows into the freed space. Trim leaves
     * (menu / toolbar / status) are never considered empty so they're preserved. If both children of a split are empty
     * (degenerate state, e.g. the user closed every tab), the first child is kept arbitrarily.
     */
    private void simplifyDockTree() {
        this.root = DockTreeMutator.simplify(this.root);
    }

    private int logicalWidth() {
        return (int) (this.width / SCALE);
    }

    private int logicalHeight() {
        return (int) (this.height / SCALE);
    }

}
