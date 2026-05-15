package com.blib.engine.layout;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blib.engine.ui.ProjectContentActionHandler;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.panel.action.ActionStackPanel;
import com.blib.engine.ui.panel.content.ContentBrowserPanel;
import com.blib.engine.ui.panel.details.DetailsPanel;
import com.blib.engine.ui.panel.details.GOAPDetailsPanel;
import com.blib.engine.ui.panel.details.ModelerInspectorPanel;
import com.blib.engine.ui.panel.entity.EntityPalettePanel;
import com.blib.engine.ui.panel.faction.DiplomacyMatrixPanel;
import com.blib.engine.ui.panel.faction.FactionBrowserPanel;
import com.blib.engine.ui.panel.faction.FactionMembersPanel;
import com.blib.engine.ui.panel.jigsaw.PiecePalettePanel;
import com.blib.engine.ui.panel.jigsaw.PoolEditorPanel;
import com.blib.engine.ui.panel.outliner.ModelerOutlinerPanel;
import com.blib.engine.ui.panel.outliner.OutlinerPanel;
import com.blib.engine.ui.panel.pathfinding.PathfindingDebugPanel;
import com.blib.engine.ui.panel.tag.TagBrowserPanel;
import com.blib.engine.ui.panel.territory.TerritoryMapPanel;
import com.blib.engine.ui.panel.texture.TextureInspectorPanel;
import com.blib.engine.ui.panel.texture.TextureViewportPanel;
import com.blib.engine.ui.panel.texture.TexturesPanel;
import com.blib.engine.ui.panel.uvmap.UvMapPanel;
import com.blib.engine.ui.panel.viewport.ModelerViewportPanel;
import com.blib.engine.ui.panel.viewport.ViewportPanel;
import com.blib.engine.ui.popup.EntityContextMenuHandler;
import com.blib.engine.ui.popup.PanelMenuOpener;

/**
 * String-id ↔ {@link Panel} factory mapping for panels that may appear in the editable body region of a layout. Layouts
 * are persisted as JSON; serialized leaves carry a panel id (e.g. {@code "outliner"}), and on load this registry
 * rebuilds the corresponding {@link Panel} instance via {@link #create}.
 * <p>
 * Trim panels (menu bar, toolbar, status bar) are <em>not</em> registered — they are screen-level chrome added back by
 * {@code EngineWorkspaceScreen.buildOuterLayout} after the body is hydrated, never serialized.
 * <p>
 * The reverse mapping ({@link #idOf}) is keyed by panel class so {@link com.blib.engine.ui.dock.TabbedPanel} contents
 * can be captured back to ids without each panel needing to expose its own id getter.
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
        ProjectContentActionHandler projectContentActionHandler,
        PanelMenuOpener panelMenuOpener
    ) {}

    @FunctionalInterface
    public interface PanelFactory {

        Panel create(Context ctx);
    }

    public enum Domain {
        WORLD("World"),
        PROJECT("Project & Data"),
        JIGSAW("Jigsaw"),
        FACTION("Factions & Territory"),
        AI_DEBUG("AI & Pathfinding"),
        MODELER("Modeler"),
        TEXTURE("Textures"),
        HISTORY("History");

        private final String label;

        Domain(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    public static final String VIEWPORT = "viewport";

    public static final String OUTLINER = "outliner";

    public static final String DETAILS = "details";

    public static final String CONTENT_BROWSER = "content_browser";

    public static final String PIECE_PALETTE = "piece_palette";

    public static final String POOL_EDITOR = "pool_editor";

    public static final String GOAP_DETAILS = "goap_details";

    public static final String PATHFINDING_DEBUG = "pathfinding_debug";

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

    public static final String MODELER_UV_MAP = "modeler_uv_map";

    public static final String TEXTURES = "textures";

    public static final String MODELER_TEXTURES = "modeler_textures";

    public static final String TEXTURE_VIEWPORT = "texture_viewport";

    public static final String TEXTURE_INSPECTOR = "texture_inspector";

    private static final Map<String, PanelFactory> FACTORIES = new LinkedHashMap<>();

    private static final Map<Class<? extends Panel>, String> IDS_BY_CLASS = new LinkedHashMap<>();

    private static final Map<String, Domain> DOMAINS_BY_ID = new LinkedHashMap<>();

    static {
        register(
            VIEWPORT,
            ViewportPanel.class,
            Domain.WORLD,
            ctx -> new ViewportPanel("Viewport", ctx.viewportRightClickHandler(), ctx.projectContentActionHandler())
        );
        register(OUTLINER, OutlinerPanel.class, Domain.WORLD, ctx -> new OutlinerPanel(ctx.entityContextMenuHandler()));
        register(DETAILS, DetailsPanel.class, Domain.WORLD, ctx -> new DetailsPanel(ctx.projectContentActionHandler()));
        register(ENTITY_PALETTE, EntityPalettePanel.class, Domain.WORLD, ctx -> new EntityPalettePanel());
        register(CONTENT_BROWSER, ContentBrowserPanel.class, Domain.PROJECT, ctx -> new ContentBrowserPanel(ctx.projectContentActionHandler()));
        register(TAG_BROWSER, TagBrowserPanel.class, Domain.PROJECT, ctx -> new TagBrowserPanel());
        register(PIECE_PALETTE, PiecePalettePanel.class, Domain.JIGSAW, ctx -> new PiecePalettePanel());
        register(POOL_EDITOR, PoolEditorPanel.class, Domain.JIGSAW, ctx -> new PoolEditorPanel());
        register(FACTION_BROWSER, FactionBrowserPanel.class, Domain.FACTION, ctx -> new FactionBrowserPanel(ctx.projectContentActionHandler()));
        register(DIPLOMACY_MATRIX, DiplomacyMatrixPanel.class, Domain.FACTION, ctx -> new DiplomacyMatrixPanel());
        register(FACTION_MEMBERS, FactionMembersPanel.class, Domain.FACTION, ctx -> new FactionMembersPanel(ctx.projectContentActionHandler()));
        register(TERRITORY_MAP, TerritoryMapPanel.class, Domain.FACTION, ctx -> new TerritoryMapPanel());
        register(GOAP_DETAILS, GOAPDetailsPanel.class, Domain.AI_DEBUG, ctx -> new GOAPDetailsPanel());
        register(PATHFINDING_DEBUG, PathfindingDebugPanel.class, Domain.AI_DEBUG, ctx -> new PathfindingDebugPanel());
        register(MODELER_VIEWPORT, ModelerViewportPanel.class, Domain.MODELER, ctx -> new ModelerViewportPanel(ctx.panelMenuOpener()));
        register(MODELER_OUTLINER, ModelerOutlinerPanel.class, Domain.MODELER, ctx -> new ModelerOutlinerPanel());
        register(MODELER_INSPECTOR, ModelerInspectorPanel.class, Domain.MODELER, ctx -> new ModelerInspectorPanel());
        register(MODELER_UV_MAP, UvMapPanel.class, Domain.MODELER, ctx -> new UvMapPanel());
        register(TEXTURES, TexturesPanel.class, Domain.TEXTURE, ctx -> new TexturesPanel(ctx.panelMenuOpener()));
        registerAlias(MODELER_TEXTURES, Domain.TEXTURE, ctx -> new TexturesPanel(ctx.panelMenuOpener()));
        register(TEXTURE_VIEWPORT, TextureViewportPanel.class, Domain.TEXTURE, ctx -> new TextureViewportPanel());
        register(TEXTURE_INSPECTOR, TextureInspectorPanel.class, Domain.TEXTURE, ctx -> new TextureInspectorPanel());
        register(ACTION_STACK, ActionStackPanel.class, Domain.HISTORY, ctx -> new ActionStackPanel());
    }

    private PanelRegistry() {}

    private static void register(String id, Class<? extends Panel> panelClass, Domain domain, PanelFactory factory) {
        FACTORIES.put(id, factory);
        IDS_BY_CLASS.put(panelClass, id);
        DOMAINS_BY_ID.put(id, domain);
    }

    private static void registerAlias(String id, Domain domain, PanelFactory factory) {
        FACTORIES.put(id, factory);
        DOMAINS_BY_ID.put(id, domain);
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
     * Reverse lookup by panel class. Used during capture: walk a {@link com.blib.engine.ui.dock.TabbedPanel}'s tabs and
     * emit the registered id for each. Panels without a registered id are dropped from the captured form (the layout
     * still saves; the unregistered tab simply won't reappear on next load).
     */
    public static @Nullable String idOf(Panel panel) {
        return IDS_BY_CLASS.get(panel.getClass());
    }

    public static Set<String> knownIds() {
        return Set.copyOf(FACTORIES.keySet());
    }

    /**
     * Registered ids in insertion order. Use this (rather than {@link #knownIds}) when stable iteration order matters —
     * layout capture and the Window menu both use this so their order stays in lock-step with panel registration.
     */
    public static List<String> orderedIds() {
        return List.copyOf(IDS_BY_CLASS.values());
    }

    public static List<Domain> orderedDomains() {
        var domains = new ArrayList<Domain>();
        var ids = orderedIds();
        for (var domain : Domain.values()) {
            for (var id : ids) {
                if (domain == domainOf(id)) {
                    domains.add(domain);
                    break;
                }
            }
        }
        return List.copyOf(domains);
    }

    public static List<String> orderedIds(Domain domain) {
        var ids = new ArrayList<String>();
        for (var id : orderedIds()) {
            if (domain == domainOf(id)) {
                ids.add(id);
            }
        }
        return List.copyOf(ids);
    }

    public static Domain domainOf(String id) {
        return DOMAINS_BY_ID.getOrDefault(id, Domain.WORLD);
    }
}
