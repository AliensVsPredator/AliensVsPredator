package com.blib.engine.layout;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;

import com.blib.engine.ui.dock.Orientation;

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
    FACTION("faction", "Faction"),
    MODELER("modeler", "Modeler"),
    ANIMATION("animation", "Animation"),
    TEXTURE("texture", "Texture");

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
     * {@link com.blib.engine.ui.dock.Sizing} instances that get drag-resized in place.
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
            case GOAP -> goapBody();
            // Jigsaw bottom slot tabs Piece Palette (placement) and Pool Editor (browse pool contents); both surfaces
            // serve the same authoring task.
            case JIGSAW -> threeColumnBody(
                List.of(PanelRegistry.PIECE_PALETTE, PanelRegistry.POOL_EDITOR),
                PanelRegistry.DETAILS
            );
            // Faction layout: left rail is the Faction Browser ("what's authored"); the viewport leaf tabs the 3D
            // Viewport with the Territory Map (two ways to look at the same world data); bottom slot holds the
            // Diplomacy Matrix; the right column stacks the Inspector (FactionSelectable) over the Members panel.
            case FACTION -> factionBody();
            // Modeler layout: Blockbench-style workspace — UV map/textures on the left, viewport in the center, and
            // inspector over outliner on the right.
            case MODELER -> modelerBody();
            // Animation layout: model viewport plus bone-only tree, transform timeline, animation list, keyframe
            // editor, and a restricted modeler inspector for pivot edits.
            case ANIMATION -> animationBody();
            // Texture layout: paint.net-style workspace — texture list on the left, editable image surface in the
            // center, and tool / color / selection controls plus history on the right.
            case TEXTURE -> textureBody();
        };
    }

    /**
     * Body shape for the MODELER template. Left rail stacks UV map over the texture list; center is the viewport;
     * right rail stacks inspector over outliner with a 40/60 split. Side rails stay fixed-width while the viewport
     * flexes.
     */
    private static BodyNode modelerBody() {
        var leftColumn = new BodyNode.Split(
            Orientation.VERTICAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.MODELER_UV_MAP), 0),
            new BodyNode.Leaf(List.of(PanelRegistry.TEXTURES), 0),
            new SizingDoc.FirstFixed(LayoutDefaults.UV_MAP_HEIGHT)
        );
        var rightColumn = new BodyNode.Split(
            Orientation.VERTICAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.MODELER_INSPECTOR), 0),
            new BodyNode.Leaf(List.of(PanelRegistry.MODELER_OUTLINER), 0),
            new SizingDoc.Ratio(0.4f)
        );
        var centerAndRight = new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.MODELER_VIEWPORT), 0),
            rightColumn,
            new SizingDoc.SecondFixed(LayoutDefaults.DETAILS_WIDTH)
        );
        return new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            leftColumn,
            centerAndRight,
            new SizingDoc.FirstFixed(LayoutDefaults.OUTLINER_WIDTH)
        );
    }

    private static BodyNode textureBody() {
        var rightColumn = new BodyNode.Split(
            Orientation.VERTICAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.TEXTURE_INSPECTOR), 0),
            new BodyNode.Leaf(List.of(PanelRegistry.ACTION_STACK), 0),
            new SizingDoc.SecondFixed(LayoutDefaults.CONTENT_BROWSER_HEIGHT)
        );
        var centerAndRight = new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.TEXTURE_VIEWPORT), 0),
            rightColumn,
            new SizingDoc.SecondFixed(LayoutDefaults.DETAILS_WIDTH)
        );
        return new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.TEXTURES), 0),
            centerAndRight,
            new SizingDoc.FirstFixed(LayoutDefaults.OUTLINER_WIDTH)
        );
    }

    private static BodyNode animationBody() {
        var viewportColumn = new BodyNode.Split(
            Orientation.VERTICAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.MODELER_VIEWPORT), 0),
            new BodyNode.Leaf(List.of(PanelRegistry.ANIMATION_TIMELINE), 0),
            new SizingDoc.SecondFixed(LayoutDefaults.CONTENT_BROWSER_HEIGHT)
        );
        var keyframeAndInspector = new BodyNode.Split(
            Orientation.VERTICAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.ANIMATION_KEYFRAME), 0),
            new BodyNode.Leaf(List.of(PanelRegistry.ANIMATION_MODELER_INSPECTOR), 0),
            new SizingDoc.Ratio(0.6f)
        );
        var rightColumn = new BodyNode.Split(
            Orientation.VERTICAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.ANIMATIONS), 0),
            keyframeAndInspector,
            new SizingDoc.FirstFixed(LayoutDefaults.CONTENT_BROWSER_HEIGHT)
        );
        var centerAndRight = new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            viewportColumn,
            rightColumn,
            new SizingDoc.SecondFixed(LayoutDefaults.DETAILS_WIDTH)
        );
        return new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.ANIMATIONS_OUTLINER), 0),
            centerAndRight,
            new SizingDoc.FirstFixed(LayoutDefaults.OUTLINER_WIDTH)
        );
    }

    private static BodyNode goapBody() {
        var viewportColumn = new BodyNode.Split(
            Orientation.VERTICAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.VIEWPORT), 0),
            new BodyNode.Leaf(List.of(PanelRegistry.CONTENT_BROWSER), 0),
            new SizingDoc.SecondFixed(LayoutDefaults.CONTENT_BROWSER_HEIGHT)
        );
        var rightColumn = new BodyNode.Split(
            Orientation.VERTICAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.GOAP_DETAILS), 0),
            new BodyNode.Leaf(List.of(PanelRegistry.PATHFINDING_DEBUG), 0),
            new SizingDoc.SecondFixed(LayoutDefaults.CONTENT_BROWSER_HEIGHT)
        );
        var centerAndRight = new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            viewportColumn,
            rightColumn,
            new SizingDoc.SecondFixed(LayoutDefaults.GOAP_DETAILS_WIDTH)
        );
        return new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.OUTLINER), 0),
            centerAndRight,
            new SizingDoc.FirstFixed(LayoutDefaults.OUTLINER_WIDTH)
        );
    }

    /**
     * Body shape for the FACTION template. Diverges from {@link #threeColumnBody}: the viewport leaf carries a second
     * tab (Territory Map), and the right column is itself a vertical split (Inspector over Members) rather than a
     * single leaf.
     */
    private static BodyNode factionBody() {
        var viewportColumn = new BodyNode.Split(
            Orientation.VERTICAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.VIEWPORT, PanelRegistry.TERRITORY_MAP), 0),
            new BodyNode.Leaf(List.of(PanelRegistry.DIPLOMACY_MATRIX), 0),
            new SizingDoc.SecondFixed(LayoutDefaults.CONTENT_BROWSER_HEIGHT)
        );
        var rightColumn = new BodyNode.Split(
            Orientation.VERTICAL.name(),
            new BodyNode.Leaf(List.of(PanelRegistry.DETAILS), 0),
            new BodyNode.Leaf(List.of(PanelRegistry.FACTION_MEMBERS), 0),
            new SizingDoc.SecondFixed(LayoutDefaults.CONTENT_BROWSER_HEIGHT)
        );
        var centerAndRight = new BodyNode.Split(
            Orientation.HORIZONTAL.name(),
            viewportColumn,
            rightColumn,
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
