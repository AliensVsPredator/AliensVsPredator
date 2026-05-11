package com.blib.engine.layout;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;

import com.blib.engine.ui.Orientation;

/**
 * The set of built-in layout templates shipped with BLib. Each template produces a fresh {@link LayoutDoc} on demand
 * via {@link #toDoc} — used both as the first-run seed (written to {@code <gameDir>/blib/engine/layouts/} on initial
 * catalog init) and as the source of truth for "Reset to Template" (which rebuilds the active layout's body from the
 * matching template).
 * <p>
 * Templates are <em>mutable</em> after seeding — once written to disk they're indistinguishable from any user layout
 * and survive customizations across game sessions. The "Reset to Template" action is what restores the canonical
 * baseline.
 * <p>
 * To add a new template: extend the enum, add a {@link #buildBody} branch, and the menu auto-populates via
 * {@link #all}. The id must match {@link LayoutDoc#ID_PATTERN}.
 */
@ApiStatus.Internal
public enum LayoutTemplate {

    DEFAULT("default", "Default"),
    GOAP("goap", "GOAP"),
    JIGSAW("jigsaw", "Jigsaw"),
    FACTION("faction", "Faction");

    private final String id;

    private final String displayName;

    LayoutTemplate(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    /**
     * Build a fresh {@link LayoutDoc} for this template. Each call creates new {@link SizingDoc} instances so the
     * returned doc is fully independent of any previous one — important because hydrate produces mutable
     * {@link com.blib.engine.ui.Sizing} instances that get drag-resized in place.
     */
    public LayoutDoc toDoc() {
        var now = Instant.now().toString();
        return new LayoutDoc(
            LayoutDoc.CURRENT_VERSION,
            id,
            displayName,
            null,
            now,
            now,
            buildBody()
        );
    }

    /**
     * Body subtree for this template. Mirrors what the old {@code EngineWorkspaceScreen.buildBody} switch produced —
     * outliner | (viewport / bottom) | right-panel — with the right and bottom slots customized per template.
     */
    private BodyNode buildBody() {
        return switch (this) {
            case DEFAULT -> threeColumnBody(
                List.of(PanelRegistry.CONTENT_BROWSER),
                PanelRegistry.DETAILS
            );
            case GOAP -> threeColumnBody(
                List.of(PanelRegistry.CONTENT_BROWSER),
                PanelRegistry.GOAP_DETAILS
            );
            // Jigsaw bottom slot tabs Piece Palette (placement) and Pool Editor (browse pool contents); both surfaces
            // serve the same authoring task.
            case JIGSAW -> threeColumnBody(
                List.of(PanelRegistry.PIECE_PALETTE, PanelRegistry.POOL_EDITOR),
                PanelRegistry.DETAILS
            );
            // Faction layout: replace the outliner with the Faction Browser (left rail = "what's authored"); bottom-tab
            // slot pairs the Diplomacy Matrix with the Members panel; Inspector renders the FactionSelectable on the
            // right.
            case FACTION -> factionBody();
        };
    }

    /**
     * Body shape for the FACTION template. Same proportions as {@link #threeColumnBody} but with the left slot bound to
     * the Faction Browser instead of the Outliner.
     */
    private static BodyNode factionBody() {
        var viewportColumn = new BodyNode.Split(
            Orientation.VERTICAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.VIEWPORT), 0),
            new BodyNode.Leaf(List.of(PanelRegistry.DIPLOMACY_MATRIX, PanelRegistry.FACTION_MEMBERS), 0),
            new SizingDoc.SecondFixed(LayoutDefaults.CONTENT_BROWSER_HEIGHT)
        );
        var centerAndRight = new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            viewportColumn,
            new BodyNode.Leaf(List.of(PanelRegistry.DETAILS), 0),
            new SizingDoc.SecondFixed(LayoutDefaults.DETAILS_WIDTH)
        );
        return new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.FACTION_BROWSER), 0),
            centerAndRight,
            new SizingDoc.FirstFixed(LayoutDefaults.OUTLINER_WIDTH)
        );
    }

    /**
     * Build the canonical three-column body shape: outliner on the left (fixed width), viewport stacked over a tab
     * group of bottom panels in the middle (flex), and a right-side detail panel (fixed width).
     */
    private static BodyNode threeColumnBody(List<String> bottomPanelIds, String rightPanelId) {
        var viewportColumn = new BodyNode.Split(
            Orientation.VERTICAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.VIEWPORT), 0),
            new BodyNode.Leaf(bottomPanelIds, 0),
            new SizingDoc.SecondFixed(LayoutDefaults.CONTENT_BROWSER_HEIGHT)
        );
        var centerAndRight = new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            viewportColumn,
            new BodyNode.Leaf(List.of(rightPanelId), 0),
            new SizingDoc.SecondFixed(LayoutDefaults.DETAILS_WIDTH)
        );
        return new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.OUTLINER), 0),
            centerAndRight,
            new SizingDoc.FirstFixed(LayoutDefaults.OUTLINER_WIDTH)
        );
    }

    public static @Nullable LayoutTemplate byId(String id) {
        for (var t : values()) {
            if (t.id.equals(id)) {
                return t;
            }
        }
        return null;
    }

    public static List<LayoutTemplate> all() {
        return List.of(values());
    }
}
