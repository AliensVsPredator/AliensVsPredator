package com.blib.engine.layout;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blib.engine.ui.ActionStackPanel;
import com.blib.engine.ui.ContentBrowserPanel;
import com.blib.engine.ui.DetailsPanel;
import com.blib.engine.ui.DiplomacyMatrixPanel;
import com.blib.engine.ui.EntityContextMenuHandler;
import com.blib.engine.ui.EntityPalettePanel;
import com.blib.engine.ui.FactionBrowserPanel;
import com.blib.engine.ui.FactionMembersPanel;
import com.blib.engine.ui.GOAPDetailsPanel;
import com.blib.engine.ui.ModelerInspectorPanel;
import com.blib.engine.ui.ModelerOutlinerPanel;
import com.blib.engine.ui.ModelerViewportPanel;
import com.blib.engine.ui.OutlinerPanel;
import com.blib.engine.ui.Panel;
import com.blib.engine.ui.PiecePalettePanel;
import com.blib.engine.ui.PoolEditorPanel;
import com.blib.engine.ui.ProjectContentActionHandler;
import com.blib.engine.ui.TagBrowserPanel;
import com.blib.engine.ui.TerritoryMapPanel;
import com.blib.engine.ui.ViewportPanel;

/**
 * String-id ↔ {@link Panel} factory mapping for panels that may appear in the editable body region of a layout. Layouts
 * are persisted as JSON; serialized leaves carry a panel id (e.g. {@code "outliner"}), and on load this registry
 * rebuilds the corresponding {@link Panel} instance via {@link #create}.
 * <p>
 * Trim panels (menu bar, toolbar, status bar) are <em>not</em> registered — they are screen-level chrome added back by
 * {@code EngineWorkspaceScreen.buildOuterLayout} after the body is hydrated, never serialized.
 * <p>
 * The reverse mapping ({@link #idOf}) is keyed by panel class so {@link com.blib.engine.ui.TabbedPanel} contents can be
 * captured back to ids without each panel needing to expose its own id getter.
 */
@ApiStatus.Internal
public final class PanelRegistry {

    /**
     * Context handed to factories that need wiring back into the engine workspace. Holds screen-instance-bound
     * callbacks that body panels invoke to request screen-level UI (right-click context menu) or screen-routed world
     * interaction (viewport right-click).
     */
    public record Context(
        ViewportPanel.RightClickHandler viewportRightClickHandler,
        EntityContextMenuHandler entityContextMenuHandler,
        ProjectContentActionHandler projectContentActionHandler
    ) {}

    @FunctionalInterface
    public interface PanelFactory {

        Panel create(Context ctx);
    }

    public static final String VIEWPORT = "viewport";

    public static final String OUTLINER = "outliner";

    public static final String DETAILS = "details";

    public static final String CONTENT_BROWSER = "content_browser";

    public static final String PIECE_PALETTE = "piece_palette";

    public static final String POOL_EDITOR = "pool_editor";

    public static final String GOAP_DETAILS = "goap_details";

    public static final String ENTITY_PALETTE = "entity_palette";

    public static final String FACTION_BROWSER = "faction_browser";

    public static final String DIPLOMACY_MATRIX = "diplomacy_matrix";

    public static final String FACTION_MEMBERS = "faction_members";

    public static final String TERRITORY_MAP = "territory_map";

    public static final String TAG_BROWSER = "tag_browser";

    public static final String ACTION_STACK = "action_stack";

    public static final String MODELER_VIEWPORT = "modeler_viewport";

    public static final String MODELER_OUTLINER = "modeler_outliner";

    public static final String MODELER_INSPECTOR = "modeler_inspector";

    private static final Map<String, PanelFactory> FACTORIES = new LinkedHashMap<>();

    private static final Map<Class<? extends Panel>, String> IDS_BY_CLASS = new LinkedHashMap<>();

    static {
        register(
            VIEWPORT,
            ViewportPanel.class,
            ctx -> new ViewportPanel("Viewport", ctx.viewportRightClickHandler(), ctx.projectContentActionHandler())
        );
        register(OUTLINER, OutlinerPanel.class, ctx -> new OutlinerPanel(ctx.entityContextMenuHandler()));
        register(DETAILS, DetailsPanel.class, ctx -> new DetailsPanel(ctx.projectContentActionHandler()));
        register(CONTENT_BROWSER, ContentBrowserPanel.class, ctx -> new ContentBrowserPanel(ctx.projectContentActionHandler()));
        register(PIECE_PALETTE, PiecePalettePanel.class, ctx -> new PiecePalettePanel());
        register(POOL_EDITOR, PoolEditorPanel.class, ctx -> new PoolEditorPanel());
        register(GOAP_DETAILS, GOAPDetailsPanel.class, ctx -> new GOAPDetailsPanel());
        register(ENTITY_PALETTE, EntityPalettePanel.class, ctx -> new EntityPalettePanel());
        register(FACTION_BROWSER, FactionBrowserPanel.class, ctx -> new FactionBrowserPanel(ctx.projectContentActionHandler()));
        register(DIPLOMACY_MATRIX, DiplomacyMatrixPanel.class, ctx -> new DiplomacyMatrixPanel());
        register(FACTION_MEMBERS, FactionMembersPanel.class, ctx -> new FactionMembersPanel(ctx.projectContentActionHandler()));
        register(TERRITORY_MAP, TerritoryMapPanel.class, ctx -> new TerritoryMapPanel());
        register(TAG_BROWSER, TagBrowserPanel.class, ctx -> new TagBrowserPanel());
        register(ACTION_STACK, ActionStackPanel.class, ctx -> new ActionStackPanel());
        register(MODELER_VIEWPORT, ModelerViewportPanel.class, ctx -> new ModelerViewportPanel());
        register(MODELER_OUTLINER, ModelerOutlinerPanel.class, ctx -> new ModelerOutlinerPanel());
        register(MODELER_INSPECTOR, ModelerInspectorPanel.class, ctx -> new ModelerInspectorPanel());
    }

    private PanelRegistry() {}

    private static void register(String id, Class<? extends Panel> panelClass, PanelFactory factory) {
        FACTORIES.put(id, factory);
        IDS_BY_CLASS.put(panelClass, id);
    }

    /**
     * Build a fresh panel instance for {@code id}. Returns {@code null} if the id is unknown — callers (typically
     * {@code LayoutSnapshot.hydrate}) should log a warning and skip the entry rather than crashing the whole layout
     * load over a single unrecognized panel.
     */
    public static @Nullable Panel create(String id, Context ctx) {
        var factory = FACTORIES.get(id);
        return factory == null ? null : factory.create(ctx);
    }

    /**
     * Reverse lookup by panel class. Used during capture: walk a {@link com.blib.engine.ui.TabbedPanel}'s tabs and emit
     * the registered id for each. Panels without a registered id are dropped from the captured form (the layout still
     * saves; the unregistered tab simply won't reappear on next load).
     */
    public static @Nullable String idOf(Panel panel) {
        return IDS_BY_CLASS.get(panel.getClass());
    }

    public static Set<String> knownIds() {
        return Set.copyOf(FACTORIES.keySet());
    }

    /**
     * Registered ids in insertion order. Use this (rather than {@link #knownIds}) when stable iteration order matters —
     * the Window menu builds its "Reopen X" entries by walking this list so the menu stays in lock-step with whatever
     * panels are registered, in the order they were declared.
     */
    public static List<String> orderedIds() {
        return List.copyOf(FACTORIES.keySet());
    }
}
