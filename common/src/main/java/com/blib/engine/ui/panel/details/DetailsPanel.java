package com.blib.engine.ui.panel.details;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.blib.api.common.faction.v1.ClaimVisibility;
import com.blib.api.common.faction.v1.ProtectionMode;
import com.blib.engine.domain.selection.entity.EntityGizmoMode;
import com.blib.engine.domain.selection.picking.BlockSelectable;
import com.blib.engine.domain.selection.picking.BlockVolumeSelectable;
import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.FactionSelectable;
import com.blib.engine.domain.selection.picking.Selectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.picking.TagSelectable;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.jigsaw.JigsawPieceLibrary;
import com.blib.engine.jigsaw.JigsawPoolLibrary;
import com.blib.engine.jigsaw.placement.JigsawTemplateScanner;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.tag.RegistryEntriesCache;
import com.blib.engine.tag.TagCatalogCache;
import com.blib.engine.tag.TagDraftCache;
import com.blib.engine.tag.TagStagingCache;
import com.blib.engine.territory.ClaimPaintTool;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.PanelPlaceholder;
import com.blib.engine.ui.ProjectContentActionHandler;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.popup.HslColorPickerPopup;
import com.blib.engine.ui.widget.Checkbox;
import com.blib.engine.ui.widget.ScrollContainer;
import com.blib.engine.ui.widget.SearchableSelect;
import com.blib.engine.ui.widget.SegmentedControl;
import com.blib.engine.ui.widget.TextInput;
import com.blib.internal.client.faction.ClientFactionInspectionCache;
import com.blib.internal.client.territory.ClientTerritoryCache;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SAddTagEntryPayload;
import com.blib.mod.common.network.packet.C2SRemoveBlockTagPayload;
import com.blib.mod.common.network.packet.C2SRemoveTagEntryPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionInspectionPayload;
import com.blib.mod.common.network.packet.C2SRequestRegistryEntriesPayload;
import com.blib.mod.common.network.packet.C2SRequestTagCatalogPayload;
import com.blib.mod.common.network.packet.C2SRequestTagDraftPayload;
import com.blib.mod.common.network.packet.C2SSetBlockStatePropertyPayload;
import com.blib.mod.common.network.packet.C2SSetEntityScalePayload;
import com.blib.mod.common.network.packet.C2SSetTagEntryRequiredPayload;
import com.blib.mod.common.network.packet.C2SSetTagReplacePayload;
import com.blib.mod.common.network.packet.C2STranslateEntityPayload;
import com.blib.mod.common.network.packet.C2SUpdateFactionFieldPayload;
import com.blib.mod.common.network.packet.C2SUpdateJigsawBlockPayload;
import com.blib.mod.common.network.packet.TagEntryDraft;

/**
 * Right-side universal inspector. Reads {@link SelectionManager#current} each frame and dispatches to a per-type view;
 * with nothing selected, falls back to a tool-state view that surfaces the gizmo target, the active jigsaw piece, and
 * the placement-mode + collision-policy controls.
 * <p>
 * Block-typed selections (jigsaw blocks today) drop into an editable inspector: name / target / pool / final-state text
 * inputs and an aligned/rollable joint selector. Inputs commit on Enter via the {@link TextInput} {@code onCommit}
 * callback; the segmented control commits on click. Each commit ships a {@link C2SUpdateJigsawBlockPayload} carrying
 * the changed field plus the unchanged BE values, so concurrent edits don't lose any fields the user wasn't actively
 * editing.
 */
@ApiStatus.Internal
public final class DetailsPanel implements Panel {

    private final @Nullable ProjectContentActionHandler actionHandler;

    /**
     * Local registry of inspector sections keyed by selectable subtype. Replaces the previous inline
     * {@code switch (single.type())} dispatch — adding a new selectable type now means dropping a new
     * {@code InspectorSection} into {@code panel.details.section.*} and registering it here. Phase-1 sections delegate
     * back to the {@code internalRender*View} methods on this panel so behavior is identical; phase-2 work absorbs each
     * view's state into its section to truly decompose the god panel.
     */
    private final java.util.List<com.blib.engine.ui.panel.base.InspectorSection<?>> sections;

    public DetailsPanel() {
        this(null);
    }

    public DetailsPanel(@Nullable ProjectContentActionHandler actionHandler) {
        this.actionHandler = actionHandler;
        this.sections = java.util.List.of(
            new com.blib.engine.ui.panel.details.section.EntityInspectorSection(this),
            new com.blib.engine.ui.panel.details.section.BlockInspectorSection(this),
            new com.blib.engine.ui.panel.details.section.BlockVolumeInspectorSection(this),
            new com.blib.engine.ui.panel.details.section.PlacedJigsawPieceInspectorSection(this),
            new com.blib.engine.ui.panel.details.section.FactionInspectorSection(this),
            new com.blib.engine.ui.panel.details.section.TagInspectorSection(this)
        );
    }

    private static final int BACKGROUND_COLOR = 0xFF18181C;

    private static final int SECTION_HEADER_BG_COLOR = 0xFF26262C;

    private static final int HEADER_BAR_BG_COLOR = 0xFF1F1F26;

    private static final int LABEL_COLOR = 0xFF7C8088;

    private static final int VALUE_COLOR = 0xFFD8D8E0;

    private static final int HEADER_TEXT_COLOR = 0xFFB8C0D0;

    private static final int ACCENT_COLOR = 0xFFE6C26B;

    private static final int CONTENT_PADDING = 5;

    private static final int LINE_HEIGHT = 10;

    private static final int LABEL_COLUMN_WIDTH = 70;

    private static final int SECTION_HEADER_HEIGHT = 11;

    private static final int HEADER_BAR_HEIGHT = 14;

    /** Vertical gap between editable rows in the block view. */
    private static final int ROW_GAP = 2;

    /** Bounding-box side for the help-icon hit-test. The glyph itself is a single "?" rendered at this size. */
    private static final int HELP_ICON_SIZE = 7;

    /** Gap between the row label / section header text and the help icon. */
    private static final int HELP_ICON_GAP = 3;

    private static final int HELP_ICON_COLOR = 0xFF606068;

    private static final int HELP_ICON_HOVER_COLOR = 0xFFE6C26B;

    /** Warning palette — used for the Name label + warning icon when this jigsaw's Name is orphaned. */
    private static final int WARN_LABEL_COLOR = 0xFFE6A23C;

    private static final int WARN_ICON_COLOR = 0xFFE6A23C;

    private static final int WARN_ICON_HOVER_COLOR = 0xFFFFC766;

    // Definitions surfaced via the inspector's "?" icons. The workspace tooltip wraps to multiple lines, so each
    // string can spell out the generation flow rather than cramming everything into a single sentence.
    private static final Component HELP_NAME = Component.literal(
        "What this jigsaw is called. Other jigsaws use this as their Target to dock here. The default 'minecraft:empty' is the catch-all most jigsaws use."
    );

    private static final Component HELP_TARGET = Component.literal(
        "The Name a partner jigsaw must have for a connection to fire. After a piece is rolled from the Pool, the generator looks inside it for a jigsaw whose Name equals this Target — that's where the new piece docks."
    );

    private static final Component HELP_POOL = Component.literal(
        "The library of pieces this jigsaw can pull from. When the structure generator reaches this jigsaw, it picks one piece at random (weighted by each pool entry's weight) and tries to attach it via a matching Target ↔ Name connection."
    );

    private static final Component HELP_JOINT = Component.literal(
        "How the partner piece is allowed to rotate around the connection. ALIGNED locks the partner to a specific orientation; ROLLABLE lets it spin freely around the connection axis."
    );

    private static final Component HELP_FINAL_STATE = Component.literal(
        "The block left in this jigsaw's place after the structure generates. Usually air (to leave open space) or a block matching the surroundings (stone, wall, etc.) to seamlessly fill in."
    );

    /** Shown via the Name field's warning icon when no other jigsaw in templates or the loaded world Targets it. */
    private static final Component HELP_NAME_ORPHANED = Component.literal(
        "Orphaned: no other jigsaw in any loaded template or the loaded world has this Name as their Target. Nothing will dock to this jigsaw. Either change Name to one some Target uses, or add a piece whose Target points here."
    );

    private static final Component HELP_FACTION_VISIBILITY = Component.literal(
        "Who can see this faction's claims on the map. Public: anyone. Allied: members and allied factions. Private: members only."
    );

    private static final Component HELP_FACTION_BLOCK_BREAK = Component.literal(
        "Who can break blocks inside this faction's claims. Public: anyone. Allied: members and allied factions. Private: members only."
    );

    private static final Component HELP_FACTION_BLOCK_INTERACT = Component.literal(
        "Who can right-click blocks (doors, chests, buttons, etc.) inside this faction's claims. Public: anyone. Allied: members and allied factions. Private: members only."
    );

    private static final Component HELP_FACTION_ENTITY_INTERACT = Component.literal(
        "Who can interact with this faction's entities — trading with villagers, mounting horses, etc. Public: anyone. Allied: members and allied factions. Private: members only."
    );

    private static final Component HELP_FACTION_NON_LIVING_ATTACK = Component.literal(
        "Who can attack non-living entities owned by this faction — item frames, armor stands, paintings, minecarts, and boats. Public: anyone. Allied: members and allied factions. Private: members only."
    );

    private static final Component HELP_FACTION_ALLOW_PVP = Component.literal(
        "When On, faction members can damage each other. When Off, friendly-fire is blocked between members."
    );

    private static final Component HELP_FACTION_ALLOW_EXPLOSIONS = Component.literal(
        "When On, explosions (TNT, creepers, etc.) can break blocks inside this faction's claims. When Off, claims are protected from blast damage."
    );

    private static final Component HELP_FACTION_ALLOW_MOB_GRIEFING = Component.literal(
        "When On, mobs (creepers, endermen, zombies breaking doors, etc.) can modify blocks inside this faction's claims. When Off, claims are protected from mob block changes."
    );

    private static final Component HELP_FACTION_TERRITORY = Component.literal(
        "Chunks this faction has claimed. Paint Claims enters a viewport mode where LMB-drag claims and RMB-drag unclaims chunks under the cursor. The Territory Map panel offers the same edits on a 2D grid."
    );

    /** Cache TTL for the orphan check; balances responsiveness against the cost of scanning loaded chunks. */
    private static final long ORPHAN_CHECK_INTERVAL_MS = 500L;

    private final SearchableSelect<ResourceLocation> nameSelect = new SearchableSelect<>(
        DetailsPanel::buildNameItems,
        rl -> rl == null ? "(none)" : rl.toString(),
        null,
        DetailsPanel::parseResourceLocation,
        null,
        rl -> commitField(BlockField.NAME, rl == null ? "" : rl.toString())
    );

    private final SearchableSelect<ResourceLocation> targetSelect = new SearchableSelect<>(
        this::buildTargetItemsForCurrentPool,
        rl -> rl == null ? "(none)" : rl.toString(),
        null,
        DetailsPanel::parseResourceLocation,
        null,
        rl -> commitField(BlockField.TARGET, rl == null ? "" : rl.toString())
    );

    private final SearchableSelect<ResourceLocation> poolSelect = new SearchableSelect<>(
        DetailsPanel::buildPoolItems,
        rl -> rl == null ? "(none)" : rl.toString(),
        null,
        rl -> commitField(BlockField.POOL, rl == null ? "" : rl.toString())
    );

    private final SearchableSelect<ResourceLocation> finalStateSelect = new SearchableSelect<>(
        DetailsPanel::buildBlockItems,
        rl -> rl == null ? "(none)" : rl.toString(),
        DetailsPanel::iconForBlock,
        null,
        rl -> commitField(BlockField.FINAL_STATE, rl == null ? "" : rl.toString())
    );

    private final SegmentedControl jointSelector = new SegmentedControl(List.of("ALIGNED", "ROLLABLE"), 0);

    /**
     * Tool toolbar for {@link BlockVolumeSelectable} — picks the active manipulation gizmo. Mirrors the toolbar that
     * used to live in {@code BlockSelectionPanel}'s header; lifted here so the panel can retire and the inspector
     * becomes the single source of selection-shaped controls.
     */
    private final SegmentedControl volumeToolControl = new SegmentedControl(
        List.of("Translate", "Scale", "Move"),
        BlockSelection.GizmoMode.SCALE_VOLUME.ordinal()
    );

    /** Position inputs (min corner X, Y, Z) shown when a {@link BlockVolumeSelectable} is the inspected target. */
    private final TextInput volumePosX = new TextInput("X", v -> commitVolumePosition(0, v));

    private final TextInput volumePosY = new TextInput("Y", v -> commitVolumePosition(1, v));

    private final TextInput volumePosZ = new TextInput("Z", v -> commitVolumePosition(2, v));

    /** Size inputs (X, Y, Z dimensions of the AABB) shown when a {@link BlockVolumeSelectable} is inspected. */
    private final TextInput volumeSizeX = new TextInput("X", v -> commitVolumeSize(0, v));

    private final TextInput volumeSizeY = new TextInput("Y", v -> commitVolumeSize(1, v));

    private final TextInput volumeSizeZ = new TextInput("Z", v -> commitVolumeSize(2, v));

    /**
     * Tool toolbar for {@link EntitySelectable} — picks Translate or Scale gizmo. Mirrors the
     * {@link #volumeToolControl} but with two entries since entities have no MOVE_BLOCKS analog.
     */
    private final SegmentedControl entityToolControl = new SegmentedControl(List.of("Translate", "Scale"), 0);

    /**
     * Position inputs for the entity's X/Y/Z position. Free continuous (doubles), commit via teleport packet on Enter.
     */
    private final TextInput entityPosX = new TextInput("X", v -> commitEntityPosition(0, v));

    private final TextInput entityPosY = new TextInput("Y", v -> commitEntityPosition(1, v));

    private final TextInput entityPosZ = new TextInput("Z", v -> commitEntityPosition(2, v));

    /**
     * Scale input for the entity's {@code Attributes.SCALE}. Single double; commit clamps to [0.1, 4.0] server-side.
     */
    private final TextInput entityScale = new TextInput("Scale", v -> commitEntityScale(v));

    /**
     * Most-recently-inspected entity id. Reset on selection swap so all entity input fields force-resync from the new
     * entity even when their previous content happened to match — without this, switching between two entities with
     * coincidentally-equal positions would leave focus + caret stuck on the previous entity's value.
     */
    private int lastInspectedEntityId = -1;

    /**
     * Faction inspector inputs. Name + color (hex) edit via {@link C2SUpdateFactionFieldPayload}. Five segmented
     * controls handle claim visibility + four protection modes; three more handle the boolean allow flags. All commits
     * round-trip through the server, then a server push refreshes the inspection cache.
     */
    private final TextInput factionName = new TextInput("Name", v -> commitFactionField(C2SUpdateFactionFieldPayload.Field.NAME, v));

    private final TextInput factionColor = new TextInput("#RRGGBB", v -> commitFactionField(C2SUpdateFactionFieldPayload.Field.COLOR, v));

    private final SegmentedControl factionVisibility = new SegmentedControl(List.of("Public", "Allied", "Private"), 0);

    private final SegmentedControl factionBlockBreak = new SegmentedControl(List.of("Public", "Allied", "Private"), 0);

    private final SegmentedControl factionBlockInteract = new SegmentedControl(List.of("Public", "Allied", "Private"), 0);

    private final SegmentedControl factionEntityInteract = new SegmentedControl(List.of("Public", "Allied", "Private"), 0);

    private final SegmentedControl factionNonLivingAttack = new SegmentedControl(List.of("Public", "Allied", "Private"), 0);

    private final SegmentedControl factionAllowPvp = new SegmentedControl(List.of("Off", "On"), 0);

    private final SegmentedControl factionAllowExplosions = new SegmentedControl(List.of("Off", "On"), 0);

    private final SegmentedControl factionAllowMobGriefing = new SegmentedControl(List.of("Off", "On"), 0);

    /**
     * Most-recently-inspected faction id. On selection swap we force-resync inputs and dispatch
     * {@link C2SRequestFactionInspectionPayload} so the cache populates with the new faction's full state.
     */
    private @Nullable ResourceLocation lastInspectedFactionId;

    /**
     * Hit-rect for the color swatch button in the faction Color row, captured each render so click handling can hit-
     * test it and open {@link HslColorPickerPopup} without recomputing geometry. {@code factionSwatchSize == 0} means
     * the inspector isn't showing a faction this frame.
     */
    private int factionSwatchX;

    private int factionSwatchY;

    private int factionSwatchSize;

    /** Current faction color (ARGB) captured during render — feeds the picker popup's initial state on swatch click. */
    private int factionSwatchArgb;

    /**
     * Hit-rect for the Paint Claims toggle in the faction Territory section. {@code factionPaintToggleSize == 0} means
     * the inspector isn't showing a faction this frame (rect should be ignored by the click handler).
     */
    private int factionPaintToggleX;

    private int factionPaintToggleY;

    private int factionPaintToggleW;

    private int factionPaintToggleH;

    /** Hit-rect for the "Delete" button in the placed-jigsaw-piece view. {@code 0} width => no button this frame. */
    private int pieceDeleteBtnX;

    private int pieceDeleteBtnY;

    private int pieceDeleteBtnW;

    private int pieceDeleteBtnH;

    /** Hit-rect for the "Switch to Volume Edit" button in the placed-jigsaw-piece view. */
    private int pieceSwitchBtnX;

    private int pieceSwitchBtnY;

    private int pieceSwitchBtnW;

    private int pieceSwitchBtnH;

    /**
     * Position of the block currently shown in the inspector. When this changes we reset all input contents to the new
     * block's BE state, so a selection swap doesn't leak the previous block's pending edits.
     */
    private @Nullable BlockPos lastInspectedPos;

    /**
     * Last-rendered joint type. When the BE's joint differs from this between frames, an external change happened
     * (server roundtrip from our own commit, or another player's edit) and we need to resync the segmented control —
     * without clobbering whatever segment the user might have just clicked.
     */
    private @Nullable JigsawBlockEntity.JointType lastBeJoint;

    /**
     * Selectable captured during render so the per-field commit callbacks (which run from the keyboard event path, not
     * the render path) know which block they're committing against. Holds any {@link BlockSelectable} — the jigsaw
     * widgets are only built / committed when the block at {@code currentBlock.pos()} is actually a jigsaw.
     */
    private @Nullable BlockSelectable currentBlock;

    /**
     * Tooltip computed during render — set when the cursor hovers a help-icon "?" next to a label or section header.
     * Read by {@link #tooltipText} after render via the workspace's tooltip pass. Reset to null at the top of every
     * render so a stale hover from the previous frame doesn't ghost into this frame's tooltip.
     */
    private @Nullable Component hoveredHelpTooltip;

    /**
     * Cache key + result for the orphan check. {@code lastOrphanCheckPos} is part of the key because the scan excludes
     * the inspected jigsaw itself ("does any *other* jigsaw Target this Name?") — switching to a different block with
     * the same Name could legitimately flip the answer, so the cache must invalidate.
     */
    private @Nullable ResourceLocation lastOrphanCheckName;

    private @Nullable BlockPos lastOrphanCheckPos;

    private boolean lastOrphanResult;

    private long lastOrphanCheckMs;

    // ── Tag inspector view (engaged when SelectionManager.current() is a TagSelectable) ──

    /** Replace toggle: index 0 = Merge (replace=false), index 1 = Replace (replace=true). */
    private final SegmentedControl tagReplaceToggle = new SegmentedControl(List.of("Merge", "Replace"), 0);

    /**
     * View mode toggle: index 0 = Resolved (read-only registry preview), index 1 = Source (project's editable JSON).
     */
    private final SegmentedControl tagViewToggle = new SegmentedControl(List.of("Resolved", "Source"), 0);

    /** Footer add-entry picker. Items are computed from {@link RegistryEntriesCache} for the current registry. */
    private final SearchableSelect<TagPickerItem> tagAddEntrySelect = new SearchableSelect<>(
        this::buildTagAddEntryItems,
        TagPickerItem::displayLabel,
        null,
        item -> {
            if (item != null) {
                commitAddTagEntry(item);
            }
        }
    );

    private final ScrollContainer tagScroll = new ScrollContainer();

    /** Cached entries for the currently-rendered tag. Refreshed every frame from {@link TagDraftCache}. */
    private List<TagEntryDraft> tagCachedEntries = List.of();

    /** Last (registry, tag) we requested a draft for — drift triggers a re-fetch + scroll reset. */
    private @Nullable ResourceLocation tagLastShownRegistry;

    private @Nullable ResourceLocation tagLastShownTag;

    /** Last registry we asked for entries — drift triggers a re-fetch (so the picker has fresh choices). */
    private @Nullable ResourceLocation tagLastFetchedRegistryEntries;

    /** Cached panel rect from the most recent render — needed by the tag view's footer + per-row × hit testing. */
    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    /** Per-row × button rects + the entries they remove; populated each tag-render frame, consumed in mouseClicked. */
    private final List<TagRemoveHit> tagRemoveHits = new ArrayList<>();

    /**
     * Per-row req/opt badge rects + the entries they toggle; populated each tag-render frame, consumed in mouseClicked.
     */
    private final List<TagRequiredToggleHit> tagRequiredToggleHits = new ArrayList<>();

    /**
     * Per-property {@link Checkbox} widgets for boolean block-state properties (waterlogged, snowy, …). Sister map to
     * {@link #genericBlockPropertySelects}: every property in the rebuilt set ends up in exactly one of the two,
     * dispatched by {@code Property} subtype. Keyed by property name. Rebuilt only when the inspected block's pos or
     * block type changes — keeping the widget instance stable across frames lets each track its own rect for click
     * dispatch in {@link #mouseClicked}.
     */
    private final java.util.LinkedHashMap<String, Checkbox> genericBlockPropertyCheckboxes = new java.util.LinkedHashMap<>();

    /**
     * Per-property {@link SearchableSelect} widgets for non-boolean block-state properties (enums like facing/shape,
     * integer properties like power). T is the canonical value string ({@link Property#getName(Comparable)}) so the
     * onSelect lambda can ship it straight into a {@link C2SSetBlockStatePropertyPayload} without re-deriving the
     * stringification.
     */
    private final java.util.LinkedHashMap<String, SearchableSelect<String>> genericBlockPropertySelects = new java.util.LinkedHashMap<>();

    /** Cache key for the per-property widget maps — pos changes ⇒ rebuild. */
    private @Nullable BlockPos genericBlockCachedPos;

    /** Cache key for the per-property widget maps — block type changes (replace-in-place) ⇒ rebuild. */
    private @Nullable net.minecraft.world.level.block.Block genericBlockCachedBlock;

    /**
     * Tag-picker for the generic block inspector's Tags section. Recreated when the inspected block changes (its
     * onSelect lambda captures the block id). itemsProvider closes over {@link TagCatalogCache} + the live tag set so
     * filtering ("don't list tags the block is already in") stays fresh as the user edits.
     */
    private @Nullable SearchableSelect<ResourceLocation> blockTagPicker;

    /** Per-row [×] hit rects from the most recent block-tag render; consumed by {@link #mouseClicked}. */
    private final List<BlockTagRemoveHit> blockTagRowHits = new ArrayList<>();

    /**
     * Active project the catalog was most recently requested for. Lets the inspector fire one
     * {@link C2SRequestTagCatalogPayload} per (engine-session, project) instead of every frame when the cache happens
     * to be empty. Stays valid even after a non-empty response — re-firing the request would be redundant.
     */
    private @Nullable String blockTagCatalogRequestedForProject;

    @Override
    public String title() {
        return "Inspector";
    }

    @Override
    public void onShown() {}

    @Override
    public @Nullable Component tooltipText() {
        return hoveredHelpTooltip;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Inspector depends on a selection in the world (block, entity, faction, etc.) — no world ⇒ no selection
        // logic worth running.
        if (Minecraft.getInstance().level == null) {
            PanelPlaceholder.drawCentered(graphics, x, y, width, height, PanelPlaceholder.NEEDS_WORLD);
            return;
        }

        // Cache panel rect — the tag view needs height-aware layout for its scroll viewport + footer pinning.
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;
        // Reset each frame; row helpers re-set this if any "?" icon is hovered.
        hoveredHelpTooltip = null;
        // Reset the faction swatch rect — only re-set when actually rendering a faction view this frame so clicks
        // on a stale rect don't fire after the selection changes to a non-faction.
        factionSwatchSize = 0;
        factionPaintToggleW = 0;
        pieceDeleteBtnW = 0;
        pieceSwitchBtnW = 0;
        tagRemoveHits.clear();
        tagRequiredToggleHits.clear();

        var font = EngineFont.get();
        var rowY = y;

        var selection = SelectionManager.current();
        var single = selection.single();

        rowY = drawHeaderBar(graphics, font, x, rowY, width, single);

        if (single == null) {
            currentBlock = null;
            internalRenderToolStateView(graphics, font, x, rowY, width, mouseX, mouseY);
        } else {
            // Registry-driven dispatch — pick the first registered section whose selectableType matches the runtime
            // class of the current selection. The legacy {@code switch (single.type())} is gone; adding a new
            // selectable type now means dropping an InspectorSection in {@code panel.details.section.*} and
            // registering it in the constructor's section list, no edits to this method.
            if (!(single instanceof BlockSelectable)) {
                currentBlock = null;
            }
            var section = matchingSection(single);
            if (section != null) {
                dispatchSection(section, graphics, x, rowY, width, single, mouseX, mouseY);
            } else {
                internalRenderGenericView(graphics, font, x, rowY, width, single);
            }
        }
    }

    private @Nullable com.blib.engine.ui.panel.base.InspectorSection<?> matchingSection(Selectable target) {
        for (var s : sections) {
            if (s.selectableType().isInstance(target)) {
                return s;
            }
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void dispatchSection(
        com.blib.engine.ui.panel.base.InspectorSection<?> section,
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        Selectable target,
        int mouseX,
        int mouseY
    ) {
        ((com.blib.engine.ui.panel.base.InspectorSection) section).render(graphics, x, y, width, target, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var single = SelectionManager.current().single();
        if (single instanceof BlockSelectable bs) {
            // Jigsaw widgets are only live when the block at this position is actually a jigsaw — they're rendered as
            // an extra section at the top of the unified block inspector, so they hit-test before the generic
            // property/tag widgets.
            var state = bs.state();
            if (state != null && state.is(net.minecraft.world.level.block.Blocks.JIGSAW)) {
                if (nameSelect.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                if (targetSelect.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                if (poolSelect.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                if (finalStateSelect.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                if (jointSelector.mouseClicked(mouseX, mouseY, button)) {
                    // Joint commits on click rather than on Enter — the segmented control has no commit-keystroke
                    // equivalent. selectedIndex() now reflects the user's choice.
                    commitField(BlockField.JOINT, null);
                    return true;
                }
            }
            // Forward to per-property widgets. Each widget's own onToggle / onSelect lambda handles the packet send,
            // so we just need to dispatch hit tests here. Selects open their popup on a button-row click; the popup
            // itself is screen-managed and consumes future clicks until dismissed.
            for (var checkbox : genericBlockPropertyCheckboxes.values()) {
                if (checkbox.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            for (var select : genericBlockPropertySelects.values()) {
                if (select.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            // Tag-row [×] hits: send a remove-by-id packet for the row's tag. Server scans the project draft for a
            // direct entry matching this block; no-op if the block is in the tag only via tag-ref or upstream.
            if (button == 0) {
                for (var hit : blockTagRowHits) {
                    if (mouseX >= hit.x() && mouseX < hit.x() + hit.w() && mouseY >= hit.y() && mouseY < hit.y() + hit.h()) {
                        var projectName = ProjectSession.activeProjectName();
                        if (projectName == null) {
                            return true;
                        }
                        var blockId = BuiltInRegistries.BLOCK.getKey(bs.state() != null ? bs.state().getBlock() : null);
                        if (blockId == null) {
                            return true;
                        }
                        BLib.MOD.networking()
                            .sendToServer(new C2SRemoveBlockTagPayload(projectName, hit.registryKey(), hit.tagId(), blockId));
                        TagStagingCache.markEntryRemoved(hit.registryKey(), hit.tagId(), false, blockId);
                        return true;
                    }
                }
            }
            if (blockTagPicker != null && blockTagPicker.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return false;
        }
        if (single instanceof BlockVolumeSelectable) {
            if (volumeToolControl.mouseClicked(mouseX, mouseY, button)) {
                BlockSelection.setGizmoMode(BlockSelection.GizmoMode.values()[volumeToolControl.selectedIndex()]);
                return true;
            }
            // TextInputs handle their own focus on click; calling them all is fine since each rejects clicks
            // outside its own rect.
            volumePosX.mouseClicked(mouseX, mouseY, button);
            volumePosY.mouseClicked(mouseX, mouseY, button);
            volumePosZ.mouseClicked(mouseX, mouseY, button);
            volumeSizeX.mouseClicked(mouseX, mouseY, button);
            volumeSizeY.mouseClicked(mouseX, mouseY, button);
            volumeSizeZ.mouseClicked(mouseX, mouseY, button);
            return false;
        }
        if (single instanceof com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable pjs) {
            // Delete button — same C2SDeletePlacedPiece path as the in-world context menu.
            if (
                button == 0
                    && pieceDeleteBtnW > 0
                    && mouseX >= pieceDeleteBtnX
                    && mouseX < pieceDeleteBtnX + pieceDeleteBtnW
                    && mouseY >= pieceDeleteBtnY
                    && mouseY < pieceDeleteBtnY + pieceDeleteBtnH
            ) {
                BLib.MOD.networking()
                    .sendToServer(new com.blib.mod.common.network.packet.C2SDeletePlacedPiecePayload(pjs.id()));
                return true;
            }
            // Explicit "Switch to Volume Edit" — promote without seeding a gizmo so the volume editor opens with
            // whatever mode the user last used there.
            if (
                button == 0
                    && pieceSwitchBtnW > 0
                    && mouseX >= pieceSwitchBtnX
                    && mouseX < pieceSwitchBtnX + pieceSwitchBtnW
                    && mouseY >= pieceSwitchBtnY
                    && mouseY < pieceSwitchBtnY + pieceSwitchBtnH
            ) {
                com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable.promoteToVolume(pjs.id(), null);
                return true;
            }
            // Tool segmented control — clicking any button auto-promotes to volume and seeds the chosen gizmo. Move
            // wouldn't strictly need a promote (move via the gizmo could be made identity-preserving), but v1 polish
            // routes all gizmo-driven flows through the volume editor; identity-preserving moves go through the
            // Position inputs instead.
            if (volumeToolControl.mouseClicked(mouseX, mouseY, button)) {
                var seed = com.blib.engine.domain.selection.volume.BlockSelection.GizmoMode
                    .values()[volumeToolControl.selectedIndex()];
                com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable.promoteToVolume(pjs.id(), seed);
                return true;
            }
            // Size inputs — by user preference, touching any size input auto-switches to volume mode. We forward the
            // click after promoting so the input still takes focus (the rect is identical between piece and volume
            // views), letting the user type immediately without a second click.
            if (
                volumeSizeX.mouseClicked(mouseX, mouseY, button)
                    || volumeSizeY.mouseClicked(mouseX, mouseY, button)
                    || volumeSizeZ.mouseClicked(mouseX, mouseY, button)
            ) {
                com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable.promoteToVolume(
                    pjs.id(),
                    com.blib.engine.domain.selection.volume.BlockSelection.GizmoMode.SCALE_VOLUME
                );
                return true;
            }
            // Position inputs — stay in piece mode; commits go through commitVolumePosition which detects the piece
            // selection and dispatches a C2SMovePlacedPiece instead of the volume's setBounds.
            volumePosX.mouseClicked(mouseX, mouseY, button);
            volumePosY.mouseClicked(mouseX, mouseY, button);
            volumePosZ.mouseClicked(mouseX, mouseY, button);
            return false;
        }
        if (single instanceof EntitySelectable) {
            if (entityToolControl.mouseClicked(mouseX, mouseY, button)) {
                EntityGizmoMode.set(EntityGizmoMode.values()[entityToolControl.selectedIndex()]);
                return true;
            }
            entityPosX.mouseClicked(mouseX, mouseY, button);
            entityPosY.mouseClicked(mouseX, mouseY, button);
            entityPosZ.mouseClicked(mouseX, mouseY, button);
            entityScale.mouseClicked(mouseX, mouseY, button);
            return false;
        }
        if (single instanceof TagSelectable) {
            return handleTagMouseClicked(mouseX, mouseY, button);
        }
        if (single instanceof FactionSelectable factionSel) {
            // TextInputs handle their own focus; calling them all is fine since each rejects clicks outside its rect.
            factionName.mouseClicked(mouseX, mouseY, button);
            factionColor.mouseClicked(mouseX, mouseY, button);

            // Paint Claims toggle — flips ClaimPaintTool on/off and sets its target to the inspected faction. Tool
            // resolves the target each frame from SelectionManager anyway, but setting it eagerly here means the
            // first click after activation already paints the right faction even if the user moves the cursor into
            // the viewport before the next render frame.
            if (
                button == 0
                    && factionPaintToggleW > 0
                    && mouseX >= factionPaintToggleX
                    && mouseX < factionPaintToggleX + factionPaintToggleW
                    && mouseY >= factionPaintToggleY
                    && mouseY < factionPaintToggleY + factionPaintToggleH
            ) {
                if (ClaimPaintTool.isActive() && factionSel.factionId().equals(ClaimPaintTool.paintTarget())) {
                    ClaimPaintTool.deactivate();
                } else {
                    ClaimPaintTool.setPaintTarget(factionSel.factionId());
                    ClaimPaintTool.activate();
                }
                return true;
            }

            // Color swatch click — open the HSL picker anchored below the swatch. Commit pushes the new color
            // through the same C2SUpdateFactionFieldPayload path as typing into the hex input, so the inspection
            // cache and TextInput will resync on the server's next push.
            if (
                button == 0
                    && factionSwatchSize > 0
                    && mouseX >= factionSwatchX
                    && mouseX < factionSwatchX + factionSwatchSize
                    && mouseY >= factionSwatchY
                    && mouseY < factionSwatchY + factionSwatchSize
            ) {
                var anchorCenterX = factionSwatchX + factionSwatchSize / 2;
                var anchorBottomY = factionSwatchY + factionSwatchSize;
                HslColorPickerPopup.openAt(
                    anchorCenterX,
                    anchorBottomY,
                    factionSwatchArgb,
                    argb -> commitFactionField(
                        C2SUpdateFactionFieldPayload.Field.COLOR,
                        String.format(java.util.Locale.ROOT, "#%06X", argb & 0xFFFFFF)
                    )
                );
                return true;
            }

            // Each segmented control commits on click via the matching field discriminator. Map the segmented index
            // back to its source enum's name so the server-side parser (string → enum) round-trips cleanly.
            if (factionVisibility.mouseClicked(mouseX, mouseY, button)) {
                commitFactionField(
                    C2SUpdateFactionFieldPayload.Field.CLAIM_VISIBILITY,
                    ClaimVisibility.values()[factionVisibility.selectedIndex()].name()
                );
                return true;
            }
            if (factionBlockBreak.mouseClicked(mouseX, mouseY, button)) {
                commitFactionField(
                    C2SUpdateFactionFieldPayload.Field.BLOCK_BREAK_PROTECTION,
                    ProtectionMode.values()[factionBlockBreak.selectedIndex()].name()
                );
                return true;
            }
            if (factionBlockInteract.mouseClicked(mouseX, mouseY, button)) {
                commitFactionField(
                    C2SUpdateFactionFieldPayload.Field.BLOCK_INTERACT_PROTECTION,
                    ProtectionMode.values()[factionBlockInteract.selectedIndex()].name()
                );
                return true;
            }
            if (factionEntityInteract.mouseClicked(mouseX, mouseY, button)) {
                commitFactionField(
                    C2SUpdateFactionFieldPayload.Field.ENTITY_INTERACT_PROTECTION,
                    ProtectionMode.values()[factionEntityInteract.selectedIndex()].name()
                );
                return true;
            }
            if (factionNonLivingAttack.mouseClicked(mouseX, mouseY, button)) {
                commitFactionField(
                    C2SUpdateFactionFieldPayload.Field.NONLIVING_ENTITY_ATTACK_PROTECTION,
                    ProtectionMode.values()[factionNonLivingAttack.selectedIndex()].name()
                );
                return true;
            }
            if (factionAllowPvp.mouseClicked(mouseX, mouseY, button)) {
                commitFactionField(
                    C2SUpdateFactionFieldPayload.Field.ALLOW_PVP,
                    Boolean.toString(factionAllowPvp.selectedIndex() == 1)
                );
                return true;
            }
            if (factionAllowExplosions.mouseClicked(mouseX, mouseY, button)) {
                commitFactionField(
                    C2SUpdateFactionFieldPayload.Field.ALLOW_EXPLOSIONS,
                    Boolean.toString(factionAllowExplosions.selectedIndex() == 1)
                );
                return true;
            }
            if (factionAllowMobGriefing.mouseClicked(mouseX, mouseY, button)) {
                commitFactionField(
                    C2SUpdateFactionFieldPayload.Field.ALLOW_MOB_GRIEFING,
                    Boolean.toString(factionAllowMobGriefing.selectedIndex() == 1)
                );
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Top-of-panel bar showing what's currently selected (or "Tool State" when nothing is). Visually distinct from the
     * section headers below so the user always knows which selectable they're inspecting.
     */
    private static int drawHeaderBar(GuiGraphics graphics, Font font, int x, int y, int width, Selectable selection) {
        graphics.fill(x, y, x + width, y + HEADER_BAR_HEIGHT, HEADER_BAR_BG_COLOR);

        var label = selection == null ? "Tool State" : selection.displayName().getString();
        var typeLabel = selection == null ? "" : "  •  " + selection.type().name();
        var truncated = font.plainSubstrByWidth(label + typeLabel, width - 2 * CONTENT_PADDING);
        // +2 compensates for MC font's descender padding so the label visually centers; see MenuBarPanel.
        graphics.drawString(
            font,
            Component.literal(truncated),
            x + CONTENT_PADDING,
            y + (HEADER_BAR_HEIGHT - font.lineHeight + 2) / 2,
            ACCENT_COLOR,
            false
        );
        return y + HEADER_BAR_HEIGHT;
    }

    /**
     * Default view when nothing is selected. The previous version dumped engine / gizmo / placement / captures status
     * sections; that became noise once those subsystems grew their own dedicated panels and live status-bar readouts.
     * Now it just centers a hint pointing the user at how to populate the inspector.
     */
    public void internalRenderToolStateView(GuiGraphics graphics, Font font, int x, int y, int width, int mouseX, int mouseY) {
        var hint = "Pick an entity, block, faction, or tag to inspect its details.";
        var hintWidth = font.width(hint);
        var hintX = x + Math.max(CONTENT_PADDING, (width - hintWidth) / 2);
        var hintY = y + LINE_HEIGHT;
        graphics.drawString(font, Component.literal(hint), hintX, hintY, LABEL_COLOR, false);
    }

    /**
     * Entity-specific view. Mirrors the {@link BlockVolumeSelectable} inspector's surface: a Tool toolbar that picks
     * the gizmo mode, editable Position XYZ inputs that send a teleport packet on Enter, and a Scale input that drives
     * the entity's {@code Attributes.SCALE}. A read-only Info section at the bottom keeps the type / UUID / health
     * readout the previous static-only view exposed.
     * <p>
     * Inputs mirror entity state each frame (skip while focused) so live entity motion / external scale changes flow
     * through to the displayed values without clobbering whatever the user is mid-typing.
     */
    public void internalRenderEntityView(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        int mouseX,
        int mouseY,
        EntitySelectable selectable
    ) {
        var rowY = y;
        var entity = selectable.entity();
        if (entity == null) {
            rowY = drawSectionHeader(graphics, font, x, rowY, width, "Entity");
            rowY += CONTENT_PADDING / 2;
            drawNote(graphics, font, x, rowY, "(unloaded)");
            return;
        }

        // Tool toolbar: Translate / Scale, drives EntityGizmoMode. Mirrors the volume inspector's tool switch.
        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Tool");
        rowY += CONTENT_PADDING / 2;
        var toolBarX = x + CONTENT_PADDING;
        var toolBarW = Math.max(SegmentedControl.HEIGHT * 2, width - 2 * CONTENT_PADDING);
        entityToolControl.setSelectedIndex(EntityGizmoMode.get().ordinal());
        entityToolControl.render(graphics, toolBarX, rowY, toolBarW, mouseX, mouseY);
        rowY += SegmentedControl.HEIGHT + CONTENT_PADDING;

        // Sync inputs from the live entity each frame. Force-resync on selection swap so a new entity's values land
        // unconditionally — the per-input "skip while focused" guard inside syncVolumeInput keeps the user's in-flight
        // typing from being clobbered between frames.
        var force = lastInspectedEntityId != entity.getId();
        syncEntityInputsFromEntity(entity, force);
        lastInspectedEntityId = entity.getId();

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Position");
        rowY += CONTENT_PADDING / 2;
        rowY = renderVolumeXyzRow(graphics, font, x, rowY, width, entityPosX, entityPosY, entityPosZ, mouseX, mouseY);

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Scale");
        rowY += CONTENT_PADDING / 2;
        rowY = drawInputRow(graphics, font, x, rowY, width, "Scale", null, entityScale, mouseX, mouseY);

        // Read-only info — type, uuid, health.
        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Info");
        rowY += CONTENT_PADDING / 2;
        rowY = drawRow(graphics, font, x, rowY, "Type", entity.getType().getDescriptionId());
        rowY = drawRow(graphics, font, x, rowY, "UUID", entity.getStringUUID().substring(0, 8));
        rowY = drawRow(graphics, font, x, rowY, "Health", String.format("%.1f / %.1f", entity.getHealth(), entity.getMaxHealth()));

        rowY = renderEntityFactionsSection(graphics, font, x, rowY, width, entity.getUUID());
    }

    /**
     * Renders a read-only Factions section listing every faction the inspected entity belongs to. The reverse lookup
     * data comes from {@link com.blib.internal.client.faction.ClientEntityFactionsCache}; we kick off the request on
     * first render for this UUID and show a "(loading…)" placeholder until the reply lands. Mutations happen via the
     * right-click "Manage Factions" popup, not here — the inspector is read-only by design so the user has one
     * canonical place to think about membership rather than two overlapping surfaces.
     */
    private int renderEntityFactionsSection(GuiGraphics graphics, Font font, int x, int rowY, int width, java.util.UUID uuid) {
        com.blib.internal.client.faction.ClientEntityFactionsCache.ensureRequested(uuid);
        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Factions");
        rowY += CONTENT_PADDING / 2;

        var factionIds = com.blib.internal.client.faction.ClientEntityFactionsCache.get(uuid);
        if (factionIds == null) {
            rowY = drawRow(graphics, font, x, rowY, "", "(loading…)");
            return rowY;
        }
        if (factionIds.isEmpty()) {
            rowY = drawRow(graphics, font, x, rowY, "", "(no factions)");
            return rowY;
        }

        // Cap inline rows so a heavily-factioned entity doesn't push the inspector off the panel. The right-click
        // popup is the surface for browsing the full list.
        var displayCap = 5;
        var shown = Math.min(displayCap, factionIds.size());
        for (var i = 0; i < shown; i++) {
            var factionId = factionIds.get(i);
            var entry = com.blib.internal.client.faction.ClientFactionDirectoryCache.get(factionId);
            var label = entry != null ? entry.name() : factionId.toString();
            rowY = drawFactionRow(graphics, font, x, rowY, label, entry != null ? entry.color() : 0xFF808088);
        }
        if (factionIds.size() > displayCap) {
            rowY = drawRow(graphics, font, x, rowY, "", "… and " + (factionIds.size() - displayCap) + " more");
        }
        return rowY;
    }

    /**
     * Inline single-line row: small color swatch + faction name. Mirrors the indent of {@link #drawRow} so columns line
     * up between adjacent Info / Factions sections.
     */
    private static int drawFactionRow(GuiGraphics graphics, Font font, int x, int rowY, String name, int argb) {
        var swatchX = x + CONTENT_PADDING;
        var swatchSize = 6;
        var swatchY = rowY + (font.lineHeight - swatchSize) / 2 + 1;
        graphics.fill(swatchX, swatchY, swatchX + swatchSize, swatchY + swatchSize, argb | 0xFF000000);
        graphics.drawString(
            font,
            net.minecraft.network.chat.Component.literal(name),
            swatchX + swatchSize + 4,
            rowY + 1,
            0xFFD0D0D0,
            false
        );
        return rowY + font.lineHeight + 1;
    }

    private void syncEntityInputsFromEntity(net.minecraft.world.entity.LivingEntity entity, boolean force) {
        var pos = entity.position();
        syncVolumeInput(entityPosX, String.format(java.util.Locale.ROOT, "%.2f", pos.x), force);
        syncVolumeInput(entityPosY, String.format(java.util.Locale.ROOT, "%.2f", pos.y), force);
        syncVolumeInput(entityPosZ, String.format(java.util.Locale.ROOT, "%.2f", pos.z), force);

        var attr = entity.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.SCALE);
        var scaleValue = attr == null ? 1.0 : attr.getValue();
        syncVolumeInput(entityScale, String.format(java.util.Locale.ROOT, "%.2f", scaleValue), force);
    }

    private void commitEntityPosition(int axis, String text) {
        var single = SelectionManager.current().single();
        if (!(single instanceof EntitySelectable es)) {
            return;
        }
        var entity = es.entity();
        if (entity == null) {
            return;
        }
        var parsed = parseDouble(text);
        if (parsed == null) {
            // Bad input — re-sync to the entity's current value so the user sees their commit was rejected.
            syncEntityInputsFromEntity(entity, true);
            return;
        }
        var pos = entity.position();
        var newX = pos.x;
        var newY = pos.y;
        var newZ = pos.z;
        switch (axis) {
            case 0 -> newX = parsed;
            case 1 -> newY = parsed;
            case 2 -> newZ = parsed;
            default -> {
                return;
            }
        }
        var mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        var dim = mc.player.level().dimension().location();
        BLib.MOD.networking().sendToServer(new C2STranslateEntityPayload(entity.getId(), newX, newY, newZ, dim));
    }

    private void commitEntityScale(String text) {
        var single = SelectionManager.current().single();
        if (!(single instanceof EntitySelectable es)) {
            return;
        }
        var entity = es.entity();
        if (entity == null) {
            return;
        }
        var parsed = parseDouble(text);
        if (parsed == null) {
            syncEntityInputsFromEntity(entity, true);
            return;
        }
        var clamped = Math.max(0.1, Math.min(4.0, parsed));
        var mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        var dim = mc.player.level().dimension().location();
        BLib.MOD.networking().sendToServer(new C2SSetEntityScalePayload(entity.getId(), clamped, dim));
    }

    private static @Nullable Double parseDouble(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Editable inspector for a {@link FactionSelectable}. Reads from {@link ClientFactionInspectionCache} (populated by
     * the server's inspection push); each input commits via {@link C2SUpdateFactionFieldPayload}. Selection-swap
     * dispatches a fresh {@link C2SRequestFactionInspectionPayload} so the cache catches up to the new faction.
     */
    public void internalRenderFactionView(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        int mouseX,
        int mouseY,
        FactionSelectable selectable
    ) {
        var rowY = y;
        var factionId = selectable.factionId();

        // On selection swap, force-resync inputs and request a fresh inspection snapshot from the server.
        var force = lastInspectedFactionId == null || !lastInspectedFactionId.equals(factionId);
        if (force) {
            BLib.MOD.networking().sendToServer(new C2SRequestFactionInspectionPayload(factionId));
            lastInspectedFactionId = factionId;
        }

        // The cache may not have caught up to the selection yet (or it's stale, mid-request) — show a placeholder.
        var inspection = ClientFactionInspectionCache.current();
        if (inspection == null || !inspection.id().equals(factionId)) {
            rowY = drawSectionHeader(graphics, font, x, rowY, width, "Faction");
            rowY += CONTENT_PADDING / 2;
            drawNote(graphics, font, x, rowY, "(loading…)");
            return;
        }

        syncFactionInputsFromInspection(inspection, force);

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Identity");
        rowY += CONTENT_PADDING / 2;
        rowY = drawInputRow(graphics, font, x, rowY, width, "Name", null, factionName, mouseX, mouseY);
        rowY = drawColorRow(graphics, font, x, rowY, width, inspection.color() | 0xFF000000, mouseX, mouseY);
        rowY = drawRow(graphics, font, x, rowY, "ID", factionId.toString());
        rowY = drawRow(graphics, font, x, rowY, "Type", inspection.typeId().toString());

        rowY = drawSectionHeaderWithHelp(graphics, font, x, rowY, width, "Territory", HELP_FACTION_TERRITORY, mouseX, mouseY);
        rowY += CONTENT_PADDING / 2;
        var chunkCount = ClientTerritoryCache.INSTANCE.chunkCountForFaction(factionId);
        rowY = drawRow(graphics, font, x, rowY, "Chunks", Integer.toString(chunkCount));
        rowY = drawPaintToggleRow(graphics, font, x, rowY, width, factionId, mouseX, mouseY);

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Protection");
        rowY += CONTENT_PADDING / 2;
        // Faction protection/flag labels are wordier than the jigsaw inspector's; widen the label column locally so
        // "Non-Living Attack" and "Entity Interact" don't bleed into the segmented control.
        var factionLabelW = 100;
        rowY = drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Visibility",
            HELP_FACTION_VISIBILITY,
            factionVisibility,
            mouseX,
            mouseY
        );
        rowY = drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Block Break",
            HELP_FACTION_BLOCK_BREAK,
            factionBlockBreak,
            mouseX,
            mouseY
        );
        rowY = drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Block Interact",
            HELP_FACTION_BLOCK_INTERACT,
            factionBlockInteract,
            mouseX,
            mouseY
        );
        rowY = drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Entity Interact",
            HELP_FACTION_ENTITY_INTERACT,
            factionEntityInteract,
            mouseX,
            mouseY
        );
        rowY = drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Non-Living Attack",
            HELP_FACTION_NON_LIVING_ATTACK,
            factionNonLivingAttack,
            mouseX,
            mouseY
        );

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Flags");
        rowY += CONTENT_PADDING / 2;
        rowY = drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "PvP",
            HELP_FACTION_ALLOW_PVP,
            factionAllowPvp,
            mouseX,
            mouseY
        );
        rowY = drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Explosions",
            HELP_FACTION_ALLOW_EXPLOSIONS,
            factionAllowExplosions,
            mouseX,
            mouseY
        );
        rowY = drawLabeledSegmentedRow(
            graphics,
            font,
            x,
            rowY,
            width,
            factionLabelW,
            "Mob Griefing",
            HELP_FACTION_ALLOW_MOB_GRIEFING,
            factionAllowMobGriefing,
            mouseX,
            mouseY
        );
    }

    /**
     * Mirror inspection-cache state into the inspector inputs. Skips text-input updates while focused (so the user's
     * mid-typing doesn't get clobbered by a server push between frames). Segmented controls always re-set since they
     * have no "focused" concept.
     */
    private void syncFactionInputsFromInspection(ClientFactionInspectionCache.ClientFactionInspection inspection, boolean force) {
        if (force || !factionName.isFocused()) {
            factionName.setContent(inspection.name());
        }
        if (force || !factionColor.isFocused()) {
            factionColor.setContent(String.format(java.util.Locale.ROOT, "#%06X", inspection.color() & 0xFFFFFF));
        }
        factionVisibility.setSelectedIndex(inspection.claimVisibility().ordinal());
        factionBlockBreak.setSelectedIndex(inspection.blockBreakProtection().ordinal());
        factionBlockInteract.setSelectedIndex(inspection.blockInteractProtection().ordinal());
        factionEntityInteract.setSelectedIndex(inspection.entityInteractProtection().ordinal());
        factionNonLivingAttack.setSelectedIndex(inspection.nonLivingEntityAttackProtection().ordinal());
        factionAllowPvp.setSelectedIndex(inspection.allowPvp() ? 1 : 0);
        factionAllowExplosions.setSelectedIndex(inspection.allowExplosions() ? 1 : 0);
        factionAllowMobGriefing.setSelectedIndex(inspection.allowMobGriefing() ? 1 : 0);
    }

    private void commitFactionField(C2SUpdateFactionFieldPayload.Field field, String value) {
        var single = SelectionManager.current().single();
        if (!(single instanceof FactionSelectable selectable)) {
            return;
        }
        BLib.MOD.networking().sendToServer(C2SUpdateFactionFieldPayload.of(selectable.factionId(), field, value));
    }

    /**
     * Editable inspector for a placed jigsaw block. Every field reads from a fresh BE snapshot each frame and pushes
     * the value into the corresponding widget — SearchableSelects overwrite their currentValue (no "focused" concept to
     * preserve), the joint segmented control resyncs only on external change. Selection swaps re-key the orphan cache
     * and reload all widgets to the new block's BE state. The Name row gets a special-case warning treatment when its
     * Name isn't Targeted by any other jigsaw in templates or the loaded world.
     */
    /**
     * Render the jigsaw-specific inspector sections (Identity / Joint / Final State) for the given block. Returns the
     * next-row {@code y}. Called from {@link #renderGenericBlockView} only when the block at {@code selectable.pos()}
     * is actually a jigsaw — the jigsaw widgets share state across the inspector lifetime, so an inspector that
     * switched off-jigsaw and back keeps coherent values.
     * <p>
     * No-ops (returns {@code y}) when the live block-entity snapshot can't be read (chunk unloaded). Callers should
     * still render the rest of the generic block view in that case.
     */
    private int renderJigsawSections(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        int mouseX,
        int mouseY,
        BlockSelectable selectable
    ) {
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return y;
        }
        var snap = com.blib.engine.jigsaw.placement.JigsawBlockTarget.snapshot(mc.level, selectable.pos());
        if (snap == null) {
            return y;
        }

        var pos = selectable.pos();
        var poolId = snap.pool().location();
        var finalStateBlockId = extractBlockId(snap.finalState());
        if (!pos.equals(lastInspectedPos)) {
            // Selection swap — reset everything to the new block's BE state.
            nameSelect.setCurrentValue(snap.name());
            targetSelect.setCurrentValue(snap.target());
            poolSelect.setCurrentValue(poolId);
            finalStateSelect.setCurrentValue(finalStateBlockId);
            jointSelector.setSelectedIndex(snap.joint().ordinal());
            lastInspectedPos = pos;
            lastBeJoint = snap.joint();
        } else {
            // Same block — sync from BE each frame. SearchableSelect has no "focused" concept, so overwriting the
            // currentValue is harmless (popup picks always win on click). External edits land immediately.
            if (snap.joint() != lastBeJoint) {
                jointSelector.setSelectedIndex(snap.joint().ordinal());
                lastBeJoint = snap.joint();
            }
            nameSelect.setCurrentValue(snap.name());
            targetSelect.setCurrentValue(snap.target());
            poolSelect.setCurrentValue(poolId);
            finalStateSelect.setCurrentValue(finalStateBlockId);
        }

        var rowY = drawSectionHeader(graphics, font, x, y, width, "Identity");
        rowY += CONTENT_PADDING / 2;
        var orphaned = isNameOrphaned(snap.name(), pos);
        rowY = drawNameRow(graphics, font, x, rowY, width, orphaned, mouseX, mouseY);
        rowY = drawSelectRow(graphics, font, x, rowY, width, "Target", HELP_TARGET, targetSelect, mouseX, mouseY);
        rowY = drawSelectRow(graphics, font, x, rowY, width, "Pool", HELP_POOL, poolSelect, mouseX, mouseY);

        rowY = drawSectionHeaderWithHelp(graphics, font, x, rowY, width, "Joint", HELP_JOINT, mouseX, mouseY);
        rowY += CONTENT_PADDING / 2;
        rowY = drawSegmentedRow(graphics, x, rowY, width, jointSelector, mouseX, mouseY);

        rowY = drawSectionHeaderWithHelp(graphics, font, x, rowY, width, "Final State", HELP_FINAL_STATE, mouseX, mouseY);
        rowY += CONTENT_PADDING / 2;
        rowY = drawSelectRow(graphics, font, x, rowY, width, "Block", null, finalStateSelect, mouseX, mouseY);
        rowY += ROW_GAP;
        return rowY;
    }

    /**
     * Fallback for selectable types that don't have a dedicated view yet. Shows the selectable's display name and its
     * world-bounds center so the user at least sees that something is selected and where it is.
     */
    public static void internalRenderGenericView(GuiGraphics graphics, Font font, int x, int y, int width, Selectable selectable) {
        var rowY = y;
        rowY = drawSectionHeader(graphics, font, x, rowY, width, selectable.type().name());
        rowY += CONTENT_PADDING / 2;
        var pivot = selectable.pivot();
        if (pivot != null) {
            rowY = drawRow(graphics, font, x, rowY, "X", String.format("%.2f", pivot.x));
            rowY = drawRow(graphics, font, x, rowY, "Y", String.format("%.2f", pivot.y));
            rowY = drawRow(graphics, font, x, rowY, "Z", String.format("%.2f", pivot.z));
        }
    }

    /**
     * Unified single-block inspector. Always renders the generic sections (Position, Block id, Properties, Tags, Block
     * Entity); when the block at {@code bs.pos()} is a jigsaw, an additional jigsaw section (Identity / Joint / Final
     * State) is rendered at the top — jigsaw blocks are just blocks with extra editable NBT, so they get every widget a
     * regular block does plus their own.
     */
    public void internalRenderGenericBlockView(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        int mouseX,
        int mouseY,
        BlockSelectable bs
    ) {
        currentBlock = bs;
        var state = bs.state();
        if (state == null) {
            // Chunk unloaded between selection and render — drop the cached widgets so a re-load reseeds them with
            // a fresh property set, and surface a note rather than rendering an empty section.
            genericBlockPropertyCheckboxes.clear();
            genericBlockPropertySelects.clear();
            blockTagPicker = null;
            blockTagRowHits.clear();
            genericBlockCachedPos = null;
            genericBlockCachedBlock = null;
            drawNote(graphics, font, x, y, "Block is no longer loaded.");
            return;
        }

        var pos = bs.pos();
        var block = state.getBlock();
        rebuildGenericBlockPropertyWidgets(state, pos, block);

        var rowY = y;
        // Jigsaw sections at the top — these are the most-edited fields for a jigsaw block (Pool / Target / Joint),
        // so they sit above the universal block sections. For non-jigsaw blocks the helper is a no-op.
        if (state.is(net.minecraft.world.level.block.Blocks.JIGSAW)) {
            rowY = renderJigsawSections(graphics, font, x, rowY, width, mouseX, mouseY, bs);
        }

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Position");
        rowY += CONTENT_PADDING / 2;
        rowY = drawRow(graphics, font, x, rowY, "X", String.valueOf(pos.getX()));
        rowY = drawRow(graphics, font, x, rowY, "Y", String.valueOf(pos.getY()));
        rowY = drawRow(graphics, font, x, rowY, "Z", String.valueOf(pos.getZ()));
        rowY += ROW_GAP;

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Block");
        rowY += CONTENT_PADDING / 2;
        var blockId = BuiltInRegistries.BLOCK.getKey(block);
        rowY = drawRow(graphics, font, x, rowY, "ID", blockId.toString());

        if (!state.getProperties().isEmpty()) {
            rowY += ROW_GAP;
            rowY = drawSectionHeader(graphics, font, x, rowY, width, "Properties");
            rowY += CONTENT_PADDING / 2;
            for (var prop : state.getProperties()) {
                rowY = renderGenericBlockPropertyRow(graphics, font, x, rowY, width, prop, state, mouseX, mouseY);
            }
        }

        var mc = Minecraft.getInstance();

        rowY += ROW_GAP;
        rowY = renderBlockTagsSection(graphics, font, x, rowY, width, mouseX, mouseY, blockId);

        var be = mc.level != null ? mc.level.getBlockEntity(pos) : null;
        if (be != null) {
            rowY += ROW_GAP;
            rowY = drawSectionHeader(graphics, font, x, rowY, width, "Block Entity");
            rowY += CONTENT_PADDING / 2;
            var beTypeId = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType());
            drawRow(graphics, font, x, rowY, "Type", beTypeId != null ? beTypeId.toString() : "?");
        }
    }

    private static final int BLOCK_TAG_ROW_HEIGHT = 12;

    private static final int BLOCK_TAG_REMOVE_BUTTON_WIDTH = 12;

    private static final int BLOCK_TAG_LABEL_PROJECT_NEW = 0xFF80E080;

    private static final int BLOCK_TAG_LABEL_PROJECT_MODIFIED = 0xFF7CB6E0;

    /** Staged-but-unreloaded color — picks up after the user adds a tag, clears on Reload Project. */
    private static final int BLOCK_TAG_LABEL_STAGED = 0xFFE08080;

    private static final int BLOCK_TAG_LABEL_UPSTREAM = 0xFFD0D0D0;

    private static final int BLOCK_TAG_REMOVE_ICON_COLOR = 0xFF7C8088;

    private static final int BLOCK_TAG_REMOVE_ICON_HOVER_COLOR = 0xFFFF6868;

    private static final int BLOCK_TAG_EMPTY_NOTE_COLOR = 0xFF606068;

    /**
     * Render the Tags section: a list of every block-registry tag {@code blockId} currently belongs to (read from
     * {@link net.minecraft.world.level.block.Block#builtInRegistryHolder()}, so vanilla / mods / datapacks / project
     * overrides all show up) plus a SearchableSelect picker for adding a new one. Project-owned tags are colored to
     * match the Tag Browser palette and get an [×] remove button; upstream-only tags render in neutral gray with no
     * button (vanilla JSON has no negation primitive, so the inspector can't remove them).
     * <p>
     * If the tag catalog cache is empty and a project is open, fires one {@link C2SRequestTagCatalogPayload} per
     * project so the picker has data to filter against. The request flag is shared across all blocks in this engine
     * session so we don't re-request every frame.
     */
    private int renderBlockTagsSection(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        int mouseX,
        int mouseY,
        ResourceLocation blockId
    ) {
        blockTagRowHits.clear();

        var rowY = drawSectionHeader(graphics, font, x, y, width, "Tags");
        rowY += CONTENT_PADDING / 2;

        var projectName = ProjectSession.activeProjectName();
        // Refresh the catalog on every project change (TagCatalogCache holds at most one project's data; if the user
        // swapped projects while the inspector was up, the cache may be stale for the new project). One request per
        // project transition — within a project, the cache is updated by other commits (setInProject) without
        // needing a refetch.
        if (projectName != null && !projectName.equals(blockTagCatalogRequestedForProject)) {
            BLib.MOD.networking().sendToServer(new C2SRequestTagCatalogPayload(projectName));
            blockTagCatalogRequestedForProject = projectName;
        }

        var effective = effectiveBlockTags(blockId);

        if (effective.isEmpty()) {
            graphics.drawString(font, Component.literal("(none)"), x + CONTENT_PADDING, rowY, BLOCK_TAG_EMPTY_NOTE_COLOR, false);
            rowY += LINE_HEIGHT + ROW_GAP;
        } else {
            var blockRegistry = Registries.BLOCK.location();
            for (var tagId : effective) {
                var pendingAdd = TagStagingCache.isEntryStagedAdd(blockRegistry, tagId, false, blockId);
                rowY = renderBlockTagRow(graphics, font, x, rowY, width, mouseX, mouseY, blockRegistry, tagId, pendingAdd);
            }
        }

        // "+ Add tag" picker — only meaningful with an open project and a populated catalog. With no project, hide
        // the picker entirely; with no catalog yet, hide and rely on the in-flight request to populate next frame.
        if (projectName != null && blockTagPicker != null && !TagCatalogCache.all().isEmpty()) {
            rowY = drawSelectRow(graphics, font, x, rowY, width, "+ Add tag", null, blockTagPicker, mouseX, mouseY);
        }

        return rowY;
    }

    /**
     * One row of the block-tag list: tag id label colored by inProject status, optional [×] remove button on
     * project-owned rows. The remove rect is recorded into {@link #blockTagRowHits} so {@link #mouseClicked} can
     * dispatch without re-deriving geometry.
     */
    private int renderBlockTagRow(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        int mouseX,
        int mouseY,
        ResourceLocation blockRegistry,
        ResourceLocation tagId,
        boolean isPendingAdd
    ) {
        var inProject = isTagInProject(blockRegistry, tagId);
        var inUpstream = isTagInUpstream(blockRegistry, tagId);
        var equivalentToUpstream = isTagEquivalentToUpstream(blockRegistry, tagId);
        int labelColor;
        if (isPendingAdd) {
            // This block's membership in this tag is staged unreloaded — paint red. After Reload Project the
            // staging clears and the row falls through to the project-new (green) or project-modified (blue) branch
            // below. Unrelated tag-level edits don't affect this row's color (the relationship "block X in tag Y"
            // hasn't changed); the Tag Browser still flags the tag itself as staged.
            labelColor = BLOCK_TAG_LABEL_STAGED;
        } else if (inProject && !equivalentToUpstream && inUpstream) {
            labelColor = BLOCK_TAG_LABEL_PROJECT_MODIFIED;
        } else if (inProject && !equivalentToUpstream) {
            labelColor = BLOCK_TAG_LABEL_PROJECT_NEW;
        } else {
            // equivalentToUpstream → project's JSON has no net effect, render as upstream (gray).
            labelColor = BLOCK_TAG_LABEL_UPSTREAM;
        }

        // X is offered whenever we have a path to remove the entry: the project owns the tag, or the user just added
        // it (we can fire a remove packet that the server can resolve by id). Upstream-only memberships stay
        // read-only since vanilla JSON has no negation primitive. Tag-level staging alone doesn't enable removal —
        // an unrelated edit elsewhere shouldn't unlock this row.
        var removable = inProject || isPendingAdd;

        var labelX = x + CONTENT_PADDING;
        var labelY = y + (BLOCK_TAG_ROW_HEIGHT - font.lineHeight + 2) / 2;
        var rightEdge = x + width - CONTENT_PADDING;
        var removeX = rightEdge - BLOCK_TAG_REMOVE_BUTTON_WIDTH;
        var labelMaxWidth = removable
            ? Math.max(0, removeX - labelX - 2)
            : Math.max(0, rightEdge - labelX);
        var truncated = font.plainSubstrByWidth(tagId.toString(), labelMaxWidth);
        graphics.drawString(font, Component.literal(truncated), labelX, labelY, labelColor, false);

        if (removable) {
            var hovered = mouseX >= removeX
                && mouseX < removeX + BLOCK_TAG_REMOVE_BUTTON_WIDTH
                && mouseY >= y
                && mouseY < y + BLOCK_TAG_ROW_HEIGHT;
            var color = hovered ? BLOCK_TAG_REMOVE_ICON_HOVER_COLOR : BLOCK_TAG_REMOVE_ICON_COLOR;
            var glyphX = removeX + (BLOCK_TAG_REMOVE_BUTTON_WIDTH - font.width("×")) / 2;
            graphics.drawString(font, Component.literal("×"), glyphX, labelY, color, false);
            blockTagRowHits.add(
                new BlockTagRemoveHit(removeX, y, BLOCK_TAG_REMOVE_BUTTON_WIDTH, BLOCK_TAG_ROW_HEIGHT, blockRegistry, tagId)
            );
        }

        return y + BLOCK_TAG_ROW_HEIGHT;
    }

    /**
     * Effective tag set for a block as the inspector should display it: live runtime tags plus any pending adds minus
     * any pending removes. Sorted by id for stable row order across frames.
     */
    private static List<ResourceLocation> effectiveBlockTags(ResourceLocation blockId) {
        var blockRegistry = Registries.BLOCK.location();
        var block = BuiltInRegistries.BLOCK.get(blockId);
        var set = new java.util.TreeSet<ResourceLocation>(java.util.Comparator.comparing(ResourceLocation::toString));
        block.builtInRegistryHolder().tags().map(TagKey::location).forEach(set::add);
        set.addAll(TagStagingCache.stagedDirectAddsFor(blockRegistry, blockId));
        TagStagingCache.stagedDirectRemovesFor(blockRegistry, blockId).forEach(set::remove);
        return List.copyOf(set);
    }

    /**
     * Find the catalog entry for {@code (registry, tagId)} and report whether the project has authored or overridden
     * it. Catalog scan is O(n) per call but n is small (one entry per loaded tag); a map keyed by (registry, tag) would
     * help only if a block has dozens of tags, which is rare.
     */
    private static boolean isTagInProject(ResourceLocation registryKey, ResourceLocation tagId) {
        for (var entry : TagCatalogCache.all()) {
            if (entry.registryKey().equals(registryKey) && entry.tagId().equals(tagId)) {
                return entry.inProject();
            }
        }
        return false;
    }

    private static boolean isTagInUpstream(ResourceLocation registryKey, ResourceLocation tagId) {
        for (var entry : TagCatalogCache.all()) {
            if (entry.registryKey().equals(registryKey) && entry.tagId().equals(tagId)) {
                return entry.inUpstream();
            }
        }
        // Not in catalog at all — must be a runtime-only tag (block.tags() reports it). Conservatively call it
        // upstream so the inspector doesn't paint it as project-owned and offer a no-op remove.
        return true;
    }

    /**
     * Does the catalog say the project's JSON for this tag is byte-equivalent to upstream's contribution? True ⇒ the
     * project owns the tag on disk but the merged result is identical to what upstream produces — neutralize the
     * "modified" coloring so the row doesn't masquerade as a real edit.
     */
    private static boolean isTagEquivalentToUpstream(ResourceLocation registryKey, ResourceLocation tagId) {
        for (var entry : TagCatalogCache.all()) {
            if (entry.registryKey().equals(registryKey) && entry.tagId().equals(tagId)) {
                return entry.equivalentToUpstream();
            }
        }
        return false;
    }

    /**
     * Build the items list for the "+ Add tag" picker: every block-registry catalog entry minus the tags the block is
     * already in. Called by the SearchableSelect's itemsProvider on popup open so it stays in sync with the catalog
     * cache and the live tag membership.
     */
    private List<SearchableSelect.Item<ResourceLocation>> availableBlockTagItems(ResourceLocation blockId) {
        var blockRegistry = Registries.BLOCK.location();
        // Exclude tags already in the effective set (runtime + pending adds − pending removes) so the picker doesn't
        // offer a re-add of something the user just attached, and re-offers tags the user just removed pre-reload.
        var already = new java.util.HashSet<>(effectiveBlockTags(blockId));
        var out = new ArrayList<SearchableSelect.Item<ResourceLocation>>();
        for (var entry : TagCatalogCache.all()) {
            if (!entry.registryKey().equals(blockRegistry)) {
                continue;
            }
            if (already.contains(entry.tagId())) {
                continue;
            }
            out.add(new SearchableSelect.Item<>(entry.tagId(), entry.tagId().toString()));
        }
        out.sort(java.util.Comparator.comparing(item -> item.value().toString()));
        return out;
    }

    /**
     * Render one editable block-state property row, dispatching by property kind: BooleanProperty rows render with a
     * {@link Checkbox} (lighter affordance for two-state values like waterlogged); everything else renders with a
     * {@link SearchableSelect} so users can pick from a dropdown that also handles longer value lists gracefully
     * (search filter helps for properties like {@code power}'s 0–15 range). The visual state is re-synced from the live
     * {@link BlockState} each frame so external edits flow through without waiting on a click.
     */
    private int renderGenericBlockPropertyRow(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        Property<?> prop,
        BlockState state,
        int mouseX,
        int mouseY
    ) {
        var checkbox = genericBlockPropertyCheckboxes.get(prop.getName());
        if (checkbox != null) {
            var current = state.getValue((BooleanProperty) prop);
            checkbox.setChecked(current);
            return drawLabeledCheckboxRow(graphics, font, x, y, prop.getName(), checkbox, mouseX, mouseY);
        }

        var select = genericBlockPropertySelects.get(prop.getName());
        if (select != null) {
            select.setCurrentValue(propertyValueName(prop, state));
            return drawSelectRow(graphics, font, x, y, width, prop.getName(), null, select, mouseX, mouseY);
        }

        // Fallback: property had an empty value set (shouldn't happen for well-formed properties) — read-only label.
        return drawRow(graphics, font, x, y, prop.getName(), propertyValueName(prop, state));
    }

    /**
     * Label + {@link Checkbox} on one row. Same geometry as {@link #drawInputRow}: label takes the left
     * {@link #LABEL_COLUMN_WIDTH} pixels, the checkbox sits at the start of the value column. Baseline-centered against
     * the checkbox's vertical midpoint with the standard +2 descender-padding compensation.
     */
    private int drawLabeledCheckboxRow(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        String label,
        Checkbox checkbox,
        int mouseX,
        int mouseY
    ) {
        var labelY = y + (Checkbox.SIZE - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), x + CONTENT_PADDING, labelY, LABEL_COLOR, false);
        checkbox.render(graphics, x + CONTENT_PADDING + LABEL_COLUMN_WIDTH, y, mouseX, mouseY);
        return y + Checkbox.SIZE + ROW_GAP;
    }

    /**
     * Rebuild the per-property widget cache only when the inspected block changes — same pos and same block type means
     * the property set is identical (BlockState is immutable; replacing-in-place creates a different Block instance
     * only when the registry block changes). Keeping widget instances stable across frames lets each widget track its
     * own click rect / popup state for the click dispatcher.
     */
    private void rebuildGenericBlockPropertyWidgets(BlockState state, BlockPos pos, net.minecraft.world.level.block.Block block) {
        if (pos.equals(genericBlockCachedPos) && block == genericBlockCachedBlock) {
            return;
        }
        genericBlockPropertyCheckboxes.clear();
        genericBlockPropertySelects.clear();
        var immutablePos = pos.immutable();

        // Rebuild the tag picker too — its onSelect lambda captures the block id, so a block swap needs a fresh
        // widget. Project name + filtering against currentTags happens at popup-open / pick time rather than at
        // construction, so switching projects mid-session doesn't strand a stale picker.
        var blockId = BuiltInRegistries.BLOCK.getKey(block);
        blockTagPicker = new SearchableSelect<>(
            () -> availableBlockTagItems(blockId),
            rl -> rl == null ? "(none)" : rl.toString(),
            null,
            picked -> {
                if (picked == null) {
                    return;
                }
                var projectName = ProjectSession.activeProjectName();
                if (projectName == null) {
                    return;
                }
                BLib.MOD.networking()
                    .sendToServer(
                        new C2SAddTagEntryPayload(projectName, Registries.BLOCK.location(), picked, false, blockId, true)
                    );
                TagStagingCache.markEntryAdded(Registries.BLOCK.location(), picked, false, blockId);
            }
        );
        for (var prop : state.getProperties()) {
            var name = prop.getName();
            if (prop instanceof BooleanProperty) {
                var current = state.getValue((BooleanProperty) prop);
                genericBlockPropertyCheckboxes.put(name, new Checkbox(current, value -> {
                    BLib.MOD.networking()
                        .sendToServer(new C2SSetBlockStatePropertyPayload(immutablePos, name, Boolean.toString(value)));
                }));
                continue;
            }
            var values = prop.getPossibleValues();
            if (values.isEmpty()) {
                continue;
            }
            var valStrings = new ArrayList<String>(values.size());
            for (var v : values) {
                valStrings.add(propertyValueName(prop, v));
            }
            var current = propertyValueName(prop, state);
            var items = new ArrayList<SearchableSelect.Item<String>>(valStrings.size());
            for (var s : valStrings) {
                items.add(new SearchableSelect.Item<>(s, s));
            }
            var select = new SearchableSelect<String>(
                () -> items,
                s -> s == null ? "(none)" : s,
                current,
                picked -> {
                    if (picked == null) {
                        return;
                    }
                    BLib.MOD.networking()
                        .sendToServer(new C2SSetBlockStatePropertyPayload(immutablePos, name, picked));
                }
            );
            genericBlockPropertySelects.put(name, select);
        }
        genericBlockCachedPos = immutablePos;
        genericBlockCachedBlock = block;
    }

    /** Raw-typed bridge: invoke {@link Property#getName(Comparable)} against a generic-wildcard property. */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String propertyValueName(Property property, Object value) {
        return property.getName((Comparable) value);
    }

    /** Raw-typed bridge: read the current value of a wildcard property and stringify it. */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static String propertyValueName(Property property, BlockState state) {
        return property.getName((Comparable) state.getValue(property));
    }

    /**
     * Inspector view for a {@link BlockVolumeSelectable}. Three sections: tool toolbar (Translate / Scale / Move) for
     * picking the active gizmo, position XYZ inputs for the AABB's min corner, and size XYZ inputs for the volume
     * dimensions. The tool toolbar mirrors the same control that used to live in {@code BlockSelectionPanel}'s header.
     */
    /**
     * Inspector view for a {@link com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable}. Mirrors the
     * block-volume inspector (tool segmented control, position inputs, size inputs) so users get the familiar shape,
     * plus a Piece metadata section (template id, rotation/mirror, placed-time) and explicit Switch to Volume Edit /
     * Delete buttons.
     * <p>
     * Editability rules — by user preference, only operations that <em>preserve</em> the piece's identity stay in piece
     * mode:
     * <ul>
     * <li><b>Move tool / Position inputs</b>: legal piece edits. Move commits via {@code C2SMovePlacedPiecePayload}
     * (server picks up the blocks, places them at the new min, updates the registry). Position-input commits go through
     * the same payload.</li>
     * <li><b>Translate / Scale tool buttons, Size inputs</b>: not valid for a piece (translating the AABB without
     * moving blocks, or scaling the region, breaks the piece's identity). Clicking any of these auto-promotes to a
     * block-volume selection over the piece's AABB and seeds the volume's gizmo mode accordingly — the user lands
     * directly in the volume editor with their intended tool already armed.</li>
     * <li><b>Switch to Volume Edit</b>: explicit promote button at the bottom. Same as a tool-button auto-switch but
     * without seeding a gizmo (uses the volume's last-chosen mode).</li>
     * </ul>
     * The widgets reused are the volume ones ({@code volumeToolControl}, {@code volumePos*}, {@code volumeSize*})
     * because we want byte-identical visual layout — separate widget instances would risk drift across the two views.
     * Click and commit handlers in {@link #mouseClicked} dispatch by current selection type, so the same widget behaves
     * differently in volume vs piece mode.
     */
    public void internalRenderPlacedJigsawPieceView(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        int mouseX,
        int mouseY,
        com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable selectable
    ) {
        var piece = com.blib.engine.jigsaw.ClientPlacedPieceRegistry.get(selectable.id());
        if (piece == null) {
            drawNote(graphics, font, x, y, "Piece is no longer present.");
            return;
        }

        var rowY = y;

        // Tool — same segmented control as the volume view. In piece mode the selected index isn't tied to
        // BlockSelection.gizmoMode (the piece doesn't have a gizmo mode); default the visible state to Move so the
        // user can see at a glance which op is identity-preserving for a piece.
        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Tool");
        rowY += CONTENT_PADDING / 2;
        var toolBarX = x + CONTENT_PADDING;
        var toolBarW = Math.max(SegmentedControl.HEIGHT * 3, width - 2 * CONTENT_PADDING);
        volumeToolControl.setSelectedIndex(com.blib.engine.domain.selection.volume.BlockSelection.GizmoMode.MOVE_BLOCKS.ordinal());
        volumeToolControl.render(graphics, toolBarX, rowY, toolBarW, mouseX, mouseY);
        rowY += SegmentedControl.HEIGHT + CONTENT_PADDING;

        // Sync the input fields from the piece's AABB so external changes (move commit, etc.) reflect immediately.
        syncVolumeInputsFromPiece(piece, false);

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Position");
        rowY += CONTENT_PADDING / 2;
        rowY = renderVolumeXyzRow(graphics, font, x, rowY, width, volumePosX, volumePosY, volumePosZ, mouseX, mouseY);

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Size");
        rowY += CONTENT_PADDING / 2;
        rowY = renderVolumeXyzRow(graphics, font, x, rowY, width, volumeSizeX, volumeSizeY, volumeSizeZ, mouseX, mouseY);

        var aabb = piece.aabb();
        var sx = aabb.maxX() - aabb.minX() + 1;
        var sy = aabb.maxY() - aabb.minY() + 1;
        var sz = aabb.maxZ() - aabb.minZ() + 1;
        rowY = drawNote(graphics, font, x, rowY, "= " + ((long) sx * sy * sz) + " blocks");
        rowY += CONTENT_PADDING / 2;

        // Piece metadata — non-AABB fields (template id, rotation, mirror, placed-time) live below the AABB controls
        // so the editing-relevant inputs stay near the top of the panel.
        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Piece");
        rowY += CONTENT_PADDING / 2;
        rowY = drawRow(graphics, font, x, rowY, "Template", piece.templateId().toString());
        rowY = drawRow(graphics, font, x, rowY, "Rotation", piece.rotation().name());
        rowY = drawRow(graphics, font, x, rowY, "Mirror", piece.mirror().name());
        var mc = net.minecraft.client.Minecraft.getInstance();
        var nowTick = mc.level == null ? piece.placedAtTick() : mc.level.getGameTime();
        var ticksAgo = Math.max(0, nowTick - piece.placedAtTick());
        rowY = drawRow(graphics, font, x, rowY, "Placed", (ticksAgo / 20) + "s ago");

        rowY += CONTENT_PADDING / 2;
        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Actions");
        rowY += CONTENT_PADDING / 2;

        // Two-button row: Switch to Volume Edit + Delete. Switch is the neutral promote (no gizmo seeded); Delete keeps
        // the danger styling. Both rects are stored so mouseClicked can hit-test without recomputing layout.
        var switchLabel = "Switch to Volume Edit";
        var switchW = font.width(switchLabel) + 12;
        var btnH = TextInput.HEIGHT;
        var switchX = x + CONTENT_PADDING;
        var switchHovered = mouseX >= switchX && mouseX < switchX + switchW && mouseY >= rowY && mouseY < rowY + btnH;
        var switchBg = switchHovered ? 0xFF22222C : 0xFF14141A;
        graphics.fill(switchX, rowY, switchX + switchW, rowY + btnH, switchBg);
        graphics.fill(switchX, rowY, switchX + switchW, rowY + 1, 0xFF353540);
        graphics.fill(switchX, rowY + btnH - 1, switchX + switchW, rowY + btnH, 0xFF353540);
        graphics.fill(switchX, rowY, switchX + 1, rowY + btnH, 0xFF353540);
        graphics.fill(switchX + switchW - 1, rowY, switchX + switchW, rowY + btnH, 0xFF353540);
        graphics.drawString(
            font,
            Component.literal(switchLabel),
            switchX + (switchW - font.width(switchLabel)) / 2,
            rowY + (btnH - font.lineHeight + 2) / 2,
            switchHovered ? 0xFFFFFFFF : 0xFFD0D0D0,
            false
        );
        pieceSwitchBtnX = switchX;
        pieceSwitchBtnY = rowY;
        pieceSwitchBtnW = switchW;
        pieceSwitchBtnH = btnH;

        var deleteLabel = "Delete";
        var deleteW = font.width(deleteLabel) + 12;
        var deleteX = switchX + switchW + 6;
        var deleteHovered = mouseX >= deleteX && mouseX < deleteX + deleteW && mouseY >= rowY && mouseY < rowY + btnH;
        var deleteBg = deleteHovered ? 0xFF3A1F1F : 0xFF14141A;
        graphics.fill(deleteX, rowY, deleteX + deleteW, rowY + btnH, deleteBg);
        graphics.fill(deleteX, rowY, deleteX + deleteW, rowY + 1, 0xFF6E2A2A);
        graphics.fill(deleteX, rowY + btnH - 1, deleteX + deleteW, rowY + btnH, 0xFF6E2A2A);
        graphics.fill(deleteX, rowY, deleteX + 1, rowY + btnH, 0xFF6E2A2A);
        graphics.fill(deleteX + deleteW - 1, rowY, deleteX + deleteW, rowY + btnH, 0xFF6E2A2A);
        graphics.drawString(
            font,
            Component.literal(deleteLabel),
            deleteX + (deleteW - font.width(deleteLabel)) / 2,
            rowY + (btnH - font.lineHeight + 2) / 2,
            deleteHovered ? 0xFFFFB0B0 : 0xFFE6A0A0,
            false
        );
        pieceDeleteBtnX = deleteX;
        pieceDeleteBtnY = rowY;
        pieceDeleteBtnW = deleteW;
        pieceDeleteBtnH = btnH;
    }

    /** Sync the volume widgets' content from a placed piece's AABB. Mirror of {@link #syncVolumeInputsFromAabb}. */
    private void syncVolumeInputsFromPiece(com.blib.mod.common.gameplay.jigsaw.PlacedPiece piece, boolean force) {
        var aabb = piece.aabb();
        var minX = aabb.minX();
        var minY = aabb.minY();
        var minZ = aabb.minZ();
        var sizeX = aabb.maxX() - aabb.minX() + 1;
        var sizeY = aabb.maxY() - aabb.minY() + 1;
        var sizeZ = aabb.maxZ() - aabb.minZ() + 1;
        syncVolumeInput(volumePosX, String.valueOf(minX), force);
        syncVolumeInput(volumePosY, String.valueOf(minY), force);
        syncVolumeInput(volumePosZ, String.valueOf(minZ), force);
        syncVolumeInput(volumeSizeX, String.valueOf(sizeX), force);
        syncVolumeInput(volumeSizeY, String.valueOf(sizeY), force);
        syncVolumeInput(volumeSizeZ, String.valueOf(sizeZ), force);
    }

    public void internalRenderBlockVolumeView(GuiGraphics graphics, Font font, int x, int y, int width, int mouseX, int mouseY) {
        var rowY = y;

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Tool");
        rowY += CONTENT_PADDING / 2;
        var toolBarX = x + CONTENT_PADDING;
        var toolBarW = Math.max(SegmentedControl.HEIGHT * 3, width - 2 * CONTENT_PADDING);
        volumeToolControl.setSelectedIndex(BlockSelection.gizmoMode().ordinal());
        volumeToolControl.render(graphics, toolBarX, rowY, toolBarW, mouseX, mouseY);
        rowY += SegmentedControl.HEIGHT + CONTENT_PADDING;

        // Mirror the AABB into the inputs every frame so external changes (gizmo drag, drag-to-pick re-pick) flow
        // through to the displayed values without clobbering whatever the user might be mid-typing.
        syncVolumeInputsFromAabb(false);

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Position");
        rowY += CONTENT_PADDING / 2;
        rowY = renderVolumeXyzRow(graphics, font, x, rowY, width, volumePosX, volumePosY, volumePosZ, mouseX, mouseY);

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Size");
        rowY += CONTENT_PADDING / 2;
        rowY = renderVolumeXyzRow(graphics, font, x, rowY, width, volumeSizeX, volumeSizeY, volumeSizeZ, mouseX, mouseY);

        // Volume readout — small note under Size so the user can see the block count without having to multiply.
        var aabb = BlockSelection.aabb();
        if (aabb.isPresent()) {
            var box = aabb.get();
            var sx = (long) (box.maxX - box.minX);
            var sy = (long) (box.maxY - box.minY);
            var sz = (long) (box.maxZ - box.minZ);
            drawNote(graphics, font, x, rowY, "= " + (sx * sy * sz) + " blocks");
        }
    }

    /** Render a row of three labeled int inputs (X / Y / Z) at the given y. Returns the next-row y. */
    private static int renderVolumeXyzRow(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        TextInput xIn,
        TextInput yIn,
        TextInput zIn,
        int mouseX,
        int mouseY
    ) {
        var inputsStart = x + CONTENT_PADDING;
        var available = Math.max(0, width - 2 * CONTENT_PADDING - 2 * 3); // 2 gaps × 3px
        var perInput = Math.max(24, available / 3);
        xIn.render(graphics, inputsStart, y, perInput, mouseX, mouseY);
        yIn.render(graphics, inputsStart + perInput + 3, y, perInput, mouseX, mouseY);
        zIn.render(graphics, inputsStart + 2 * (perInput + 3), y, perInput, mouseX, mouseY);
        return y + TextInput.HEIGHT + CONTENT_PADDING;
    }

    private void commitVolumePosition(int axis, String text) {
        // Dispatch by selection type: piece commits go through the identity-preserving move handler; volume commits
        // go through the original setBounds path. The widgets are shared between views, so the commit callback has to
        // resolve which mode is active right now.
        var single = SelectionManager.current().single();
        var parsed = parseInt(text);
        if (parsed == null) {
            if (single instanceof com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable pjs) {
                var piece = com.blib.engine.jigsaw.ClientPlacedPieceRegistry.get(pjs.id());
                if (piece != null) {
                    syncVolumeInputsFromPiece(piece, true);
                }
            } else {
                syncVolumeInputsFromAabb(true);
            }
            return;
        }
        if (single instanceof com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable pjs) {
            var piece = com.blib.engine.jigsaw.ClientPlacedPieceRegistry.get(pjs.id());
            if (piece == null) {
                return;
            }
            var aabb = piece.aabb();
            var newMinX = axis == 0 ? parsed : aabb.minX();
            var newMinY = axis == 1 ? parsed : aabb.minY();
            var newMinZ = axis == 2 ? parsed : aabb.minZ();
            BLib.MOD.networking()
                .sendToServer(
                    new com.blib.mod.common.network.packet.C2SMovePlacedPiecePayload(
                        pjs.id(),
                        new BlockPos(newMinX, newMinY, newMinZ)
                    )
                );
            return;
        }
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return;
        }
        var box = aabb.get();
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var maxX = (int) Math.floor(box.maxX) - 1;
        var maxY = (int) Math.floor(box.maxY) - 1;
        var maxZ = (int) Math.floor(box.maxZ) - 1;
        var sizeX = maxX - minX + 1;
        var sizeY = maxY - minY + 1;
        var sizeZ = maxZ - minZ + 1;
        switch (axis) {
            case 0 -> {
                minX = parsed;
                maxX = parsed + sizeX - 1;
            }
            case 1 -> {
                minY = parsed;
                maxY = parsed + sizeY - 1;
            }
            case 2 -> {
                minZ = parsed;
                maxZ = parsed + sizeZ - 1;
            }
            default -> {
                return;
            }
        }
        BlockSelection.setBounds(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
    }

    private void commitVolumeSize(int axis, String text) {
        var parsed = parseInt(text);
        if (parsed == null || parsed < 1) {
            syncVolumeInputsFromAabb(true);
            return;
        }
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return;
        }
        var box = aabb.get();
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var maxX = (int) Math.floor(box.maxX) - 1;
        var maxY = (int) Math.floor(box.maxY) - 1;
        var maxZ = (int) Math.floor(box.maxZ) - 1;
        switch (axis) {
            case 0 -> maxX = minX + parsed - 1;
            case 1 -> maxY = minY + parsed - 1;
            case 2 -> maxZ = minZ + parsed - 1;
            default -> {
                return;
            }
        }
        BlockSelection.setBounds(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
    }

    private void syncVolumeInputsFromAabb(boolean force) {
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            syncVolumeInput(volumePosX, "", force);
            syncVolumeInput(volumePosY, "", force);
            syncVolumeInput(volumePosZ, "", force);
            syncVolumeInput(volumeSizeX, "", force);
            syncVolumeInput(volumeSizeY, "", force);
            syncVolumeInput(volumeSizeZ, "", force);
            return;
        }
        var box = aabb.get();
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var sizeX = (int) Math.floor(box.maxX) - minX;
        var sizeY = (int) Math.floor(box.maxY) - minY;
        var sizeZ = (int) Math.floor(box.maxZ) - minZ;
        syncVolumeInput(volumePosX, String.valueOf(minX), force);
        syncVolumeInput(volumePosY, String.valueOf(minY), force);
        syncVolumeInput(volumePosZ, String.valueOf(minZ), force);
        syncVolumeInput(volumeSizeX, String.valueOf(sizeX), force);
        syncVolumeInput(volumeSizeY, String.valueOf(sizeY), force);
        syncVolumeInput(volumeSizeZ, String.valueOf(sizeZ), force);
    }

    private static void syncVolumeInput(TextInput input, String value, boolean force) {
        if (force || !input.isFocused()) {
            input.setContent(value);
        }
    }

    private static @Nullable Integer parseInt(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static int drawSectionHeader(GuiGraphics graphics, Font font, int x, int y, int width, String label) {
        graphics.fill(x, y, x + width, y + SECTION_HEADER_HEIGHT, SECTION_HEADER_BG_COLOR);
        graphics.drawString(
            font,
            Component.literal(label),
            x + CONTENT_PADDING,
            // +2 compensates for MC font's descender padding so section headers visually center; see MenuBarPanel.
            y + (SECTION_HEADER_HEIGHT - font.lineHeight + 2) / 2,
            HEADER_TEXT_COLOR,
            false
        );
        return y + SECTION_HEADER_HEIGHT;
    }

    /**
     * Section-header variant that renders a "?" help icon to the right of the label and surfaces {@code helpText} via
     * {@link #tooltipText} when the cursor hovers it. Instance method (rather than static) so it can write the panel's
     * hovered-tooltip field directly.
     */
    private int drawSectionHeaderWithHelp(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        String label,
        Component helpText,
        int mouseX,
        int mouseY
    ) {
        var nextY = drawSectionHeader(graphics, font, x, y, width, label);
        var iconX = x + CONTENT_PADDING + font.width(label) + HELP_ICON_GAP;
        var iconY = y + (SECTION_HEADER_HEIGHT - font.lineHeight + 2) / 2;
        if (drawHelpIcon(graphics, font, iconX, iconY, mouseX, mouseY)) {
            hoveredHelpTooltip = helpText;
        }
        return nextY;
    }

    private static int drawRow(GuiGraphics graphics, Font font, int x, int y, String label, String value) {
        graphics.drawString(font, Component.literal(label), x + CONTENT_PADDING, y, LABEL_COLOR, false);
        graphics.drawString(font, Component.literal(value), x + CONTENT_PADDING + LABEL_COLUMN_WIDTH, y, VALUE_COLOR, false);
        return y + LINE_HEIGHT;
    }

    private static int drawNote(GuiGraphics graphics, Font font, int x, int y, String text) {
        graphics.drawString(font, Component.literal(text), x + CONTENT_PADDING, y, LABEL_COLOR, false);
        return y + LINE_HEIGHT;
    }

    /**
     * Label + {@link TextInput} on one row. Label takes {@link #LABEL_COLUMN_WIDTH} on the left, input fills the rest
     * minus content padding on both sides. The label baseline is centered against the input's text baseline using the
     * same +2 descender-padding compensation used elsewhere in the workspace. When {@code helpText} is non-null, a "?"
     * icon is rendered immediately after the label and contributes to {@link #hoveredHelpTooltip} on hover.
     */
    private int drawInputRow(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        String label,
        @Nullable Component helpText,
        TextInput input,
        int mouseX,
        int mouseY
    ) {
        var labelY = y + (TextInput.HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), x + CONTENT_PADDING, labelY, LABEL_COLOR, false);
        if (helpText != null) {
            var iconX = x + CONTENT_PADDING + font.width(label) + HELP_ICON_GAP;
            if (drawHelpIcon(graphics, font, iconX, labelY, mouseX, mouseY)) {
                hoveredHelpTooltip = helpText;
            }
        }
        var inputX = x + CONTENT_PADDING + LABEL_COLUMN_WIDTH;
        var inputW = Math.max(0, width - LABEL_COLUMN_WIDTH - 2 * CONTENT_PADDING);
        input.render(graphics, inputX, y, inputW, mouseX, mouseY);
        return y + TextInput.HEIGHT + ROW_GAP;
    }

    /**
     * Faction Color row — label + hex {@link TextInput} + a clickable swatch on the right that opens
     * {@link HslColorPickerPopup}. Swatch hit-rect is captured into {@code factionSwatch*} fields so the click handler
     * in {@link #mouseClicked} can hit-test it without recomputing geometry.
     */
    private int drawColorRow(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        int currentArgb,
        int mouseX,
        int mouseY
    ) {
        var label = "Color";
        var labelY = y + (TextInput.HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), x + CONTENT_PADDING, labelY, LABEL_COLOR, false);

        // Swatch sits at the right end of the row; the hex input fills everything in between.
        var swatchSize = TextInput.HEIGHT;
        var swatchGap = 4;
        var swatchX = x + width - CONTENT_PADDING - swatchSize;
        var inputX = x + CONTENT_PADDING + LABEL_COLUMN_WIDTH;
        var inputW = Math.max(0, swatchX - inputX - swatchGap);
        factionColor.render(graphics, inputX, y, inputW, mouseX, mouseY);

        var swatchHovered = mouseX >= swatchX && mouseX < swatchX + swatchSize && mouseY >= y && mouseY < y + swatchSize;
        graphics.fill(swatchX, y, swatchX + swatchSize, y + swatchSize, currentArgb);
        var borderColor = swatchHovered ? HELP_ICON_HOVER_COLOR : LABEL_COLOR;
        graphics.fill(swatchX, y, swatchX + swatchSize, y + 1, borderColor);
        graphics.fill(swatchX, y + swatchSize - 1, swatchX + swatchSize, y + swatchSize, borderColor);
        graphics.fill(swatchX, y, swatchX + 1, y + swatchSize, borderColor);
        graphics.fill(swatchX + swatchSize - 1, y, swatchX + swatchSize, y + swatchSize, borderColor);

        this.factionSwatchX = swatchX;
        this.factionSwatchY = y;
        this.factionSwatchSize = swatchSize;
        this.factionSwatchArgb = currentArgb;
        return y + TextInput.HEIGHT + ROW_GAP;
    }

    /**
     * Faction Territory section: a toggle button row that flips the viewport claim-paint tool on / off. Label reflects
     * current state — "Paint Claims" when off, "Stop Painting" when on and pointing at the inspected faction. Hit-rect
     * captured in {@code factionPaintToggle*} for click dispatch.
     */
    private int drawPaintToggleRow(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        net.minecraft.resources.ResourceLocation factionId,
        int mouseX,
        int mouseY
    ) {
        var paintingThis = ClaimPaintTool.isActive() && factionId.equals(ClaimPaintTool.paintTarget());
        var label = paintingThis ? "Stop Painting" : "Paint Claims";
        var btnW = font.width(label) + 12;
        var btnH = TextInput.HEIGHT;
        var btnX = x + CONTENT_PADDING;
        var btnY = y;

        var hovered = mouseX >= btnX && mouseX < btnX + btnW && mouseY >= btnY && mouseY < btnY + btnH;
        // Bright-on-active so the user can see at a glance whether they're armed; subtle when off.
        var bg = paintingThis ? 0xFF3C3C46 : (hovered ? 0xFF22222C : 0xFF14141A);
        var borderColor = paintingThis ? 0xFFE6C26B : 0xFF353540;
        graphics.fill(btnX, btnY, btnX + btnW, btnY + btnH, bg);
        graphics.fill(btnX, btnY, btnX + btnW, btnY + 1, borderColor);
        graphics.fill(btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, borderColor);
        graphics.fill(btnX, btnY, btnX + 1, btnY + btnH, borderColor);
        graphics.fill(btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, borderColor);

        var textColor = paintingThis ? 0xFFE6C26B : (hovered ? 0xFFFFFFFF : 0xFFD0D0D0);
        var textX = btnX + (btnW - font.width(label)) / 2;
        var textY = btnY + (btnH - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), textX, textY, textColor, false);

        factionPaintToggleX = btnX;
        factionPaintToggleY = btnY;
        factionPaintToggleW = btnW;
        factionPaintToggleH = btnH;
        return y + btnH + ROW_GAP;
    }

    /**
     * Render a small "?" glyph anchored at {@code (iconX, iconY)} with a hover hit-rect of {@link #HELP_ICON_SIZE} px
     * on each side. Returns {@code true} when the cursor is over the icon — caller writes the tooltip field.
     */
    private static boolean drawHelpIcon(GuiGraphics graphics, Font font, int iconX, int iconY, int mouseX, int mouseY) {
        return drawIconGlyph(graphics, font, iconX, iconY, "?", HELP_ICON_COLOR, HELP_ICON_HOVER_COLOR, mouseX, mouseY);
    }

    /**
     * Warning-icon variant. Same hit-rect + tooltip-on-hover semantics as {@link #drawHelpIcon}, but the glyph and
     * palette signal a problem the user should look at rather than a neutral help affordance.
     */
    private static boolean drawWarningIcon(GuiGraphics graphics, Font font, int iconX, int iconY, int mouseX, int mouseY) {
        return drawIconGlyph(graphics, font, iconX, iconY, "!", WARN_ICON_COLOR, WARN_ICON_HOVER_COLOR, mouseX, mouseY);
    }

    private static boolean drawIconGlyph(
        GuiGraphics graphics,
        Font font,
        int iconX,
        int iconY,
        String glyph,
        int color,
        int hoverColor,
        int mouseX,
        int mouseY
    ) {
        var hovered = mouseX >= iconX
            && mouseX < iconX + HELP_ICON_SIZE
            && mouseY >= iconY
            && mouseY < iconY + HELP_ICON_SIZE;
        var c = hovered ? hoverColor : color;
        graphics.drawString(font, Component.literal(glyph), iconX, iconY, c, false);
        return hovered;
    }

    /**
     * Specialized variant of {@link #drawSelectRow} for the Name field: applies orphan-warning styling (warning label
     * color + "!" icon + orphan-explanation tooltip) when {@code orphaned} is true, otherwise renders identically to a
     * help-icon row with {@link #HELP_NAME}.
     */
    private int drawNameRow(GuiGraphics graphics, Font font, int x, int y, int width, boolean orphaned, int mouseX, int mouseY) {
        var labelText = "Name";
        var labelColor = orphaned ? WARN_LABEL_COLOR : LABEL_COLOR;
        var labelY = y + (SearchableSelect.HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(labelText), x + CONTENT_PADDING, labelY, labelColor, false);

        var iconX = x + CONTENT_PADDING + font.width(labelText) + HELP_ICON_GAP;
        var iconHovered = orphaned
            ? drawWarningIcon(graphics, font, iconX, labelY, mouseX, mouseY)
            : drawHelpIcon(graphics, font, iconX, labelY, mouseX, mouseY);
        if (iconHovered) {
            hoveredHelpTooltip = orphaned ? HELP_NAME_ORPHANED : HELP_NAME;
        }

        var ctrlX = x + CONTENT_PADDING + LABEL_COLUMN_WIDTH;
        var ctrlW = Math.max(0, width - LABEL_COLUMN_WIDTH - 2 * CONTENT_PADDING);
        nameSelect.render(graphics, ctrlX, y, ctrlW, mouseX, mouseY);
        return y + SearchableSelect.HEIGHT + ROW_GAP;
    }

    /**
     * True when no other jigsaw — across loaded templates or in the loaded chunks of the world — has {@code thisName}
     * as its Target. Cached for {@link #ORPHAN_CHECK_INTERVAL_MS} so per-frame inspector renders don't re-scan
     * everything; the cache invalidates immediately if the inspected jigsaw's Name itself changes.
     * <p>
     * The check excludes the inspected jigsaw at {@code thisPos} (so a self-loop where this jigsaw's Target equals its
     * own Name doesn't mask the orphan state). Non-loaded chunks contribute nothing — that's an inherent limitation of
     * a client-side check, but acceptable: an out-of-sight jigsaw can't be inspected anyway.
     */
    private boolean isNameOrphaned(ResourceLocation thisName, BlockPos thisPos) {
        var now = System.currentTimeMillis();
        if (
            thisName.equals(lastOrphanCheckName)
                && thisPos.equals(lastOrphanCheckPos)
                && now - lastOrphanCheckMs < ORPHAN_CHECK_INTERVAL_MS
        ) {
            return lastOrphanResult;
        }
        var result = computeOrphanStatus(thisName, thisPos);
        lastOrphanCheckName = thisName;
        lastOrphanCheckPos = thisPos;
        lastOrphanResult = result;
        lastOrphanCheckMs = now;
        return result;
    }

    private static boolean computeOrphanStatus(ResourceLocation thisName, BlockPos thisPos) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return false;
        }

        // 1. Loaded in-world jigsaw blocks. Walk every chunk in the player's render distance and check each
        // block-entity's target. EmptyLevelChunk returns an empty BE map, so unloaded slots are no-ops.
        var renderDist = mc.options.renderDistance().get();
        var centerX = mc.player.chunkPosition().x;
        var centerZ = mc.player.chunkPosition().z;
        for (var dx = -renderDist; dx <= renderDist; dx++) {
            for (var dz = -renderDist; dz <= renderDist; dz++) {
                var chunk = mc.level.getChunk(centerX + dx, centerZ + dz);
                for (var be : chunk.getBlockEntities().values()) {
                    if (
                        be instanceof JigsawBlockEntity je
                            && !be.getBlockPos().equals(thisPos)
                            && je.getTarget().equals(thisName)
                    ) {
                        return false;
                    }
                }
            }
        }

        // 2. Static template content via JigsawPoolLibrary + JigsawTemplateScanner (already cached internally).
        for (var poolId : JigsawPoolLibrary.listPoolIds()) {
            for (var templateId : JigsawPoolLibrary.templateIdsInPool(poolId)) {
                var template = JigsawPieceLibrary.get(templateId);
                if (template == null) {
                    continue;
                }
                for (var jigsaw : JigsawTemplateScanner.jigsawBlocks(template)) {
                    if (jigsaw.target().equals(thisName)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Full-width {@link SegmentedControl} row — no label, since the section header above ("Joint") already names the
     * control.
     */
    private static int drawSegmentedRow(GuiGraphics graphics, int x, int y, int width, SegmentedControl control, int mouseX, int mouseY) {
        var ctrlW = Math.max(0, width - 2 * CONTENT_PADDING);
        control.render(graphics, x + CONTENT_PADDING, y, ctrlW, mouseX, mouseY);
        return y + SegmentedControl.HEIGHT + ROW_GAP;
    }

    /**
     * Labeled {@link SegmentedControl} row — left-side label plus an optional "?" help icon that surfaces
     * {@code helpText} on hover. {@code labelColumnWidth} sizes the left label column independently of
     * {@link #LABEL_COLUMN_WIDTH} so callers with wordier labels (e.g. the faction inspector's protection toggles)
     * don't get their text clipped into the control area.
     */
    private int drawLabeledSegmentedRow(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        int labelColumnWidth,
        String label,
        @Nullable Component helpText,
        SegmentedControl control,
        int mouseX,
        int mouseY
    ) {
        var labelY = y + (SegmentedControl.HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), x + CONTENT_PADDING, labelY, LABEL_COLOR, false);
        if (helpText != null) {
            var iconX = x + CONTENT_PADDING + font.width(label) + HELP_ICON_GAP;
            if (drawHelpIcon(graphics, font, iconX, labelY, mouseX, mouseY)) {
                hoveredHelpTooltip = helpText;
            }
        }
        var ctrlX = x + CONTENT_PADDING + labelColumnWidth;
        var ctrlW = Math.max(0, width - labelColumnWidth - 2 * CONTENT_PADDING);
        control.render(graphics, ctrlX, y, ctrlW, mouseX, mouseY);
        return y + SegmentedControl.HEIGHT + ROW_GAP;
    }

    /**
     * Label + {@link SearchableSelect} on one row. Same geometry as {@link #drawInputRow} so the inspector keeps a
     * consistent grid; only the right-hand widget differs. When {@code helpText} is non-null, a "?" icon is rendered
     * after the label and contributes to {@link #hoveredHelpTooltip} on hover.
     */
    private int drawSelectRow(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        String label,
        @Nullable Component helpText,
        SearchableSelect<?> select,
        int mouseX,
        int mouseY
    ) {
        var labelY = y + (SearchableSelect.HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), x + CONTENT_PADDING, labelY, LABEL_COLOR, false);
        if (helpText != null) {
            var iconX = x + CONTENT_PADDING + font.width(label) + HELP_ICON_GAP;
            if (drawHelpIcon(graphics, font, iconX, labelY, mouseX, mouseY)) {
                hoveredHelpTooltip = helpText;
            }
        }
        var ctrlX = x + CONTENT_PADDING + LABEL_COLUMN_WIDTH;
        var ctrlW = Math.max(0, width - LABEL_COLUMN_WIDTH - 2 * CONTENT_PADDING);
        select.render(graphics, ctrlX, y, ctrlW, mouseX, mouseY);
        return y + SearchableSelect.HEIGHT + ROW_GAP;
    }

    /**
     * Build the block list for the final-state picker. Sorted by namespace+path so the popup is browsable in
     * predictable order; recomputed lazily each time the popup opens (cheap — ~1000 entries, a few µs).
     */
    private static List<SearchableSelect.Item<ResourceLocation>> buildBlockItems() {
        return BuiltInRegistries.BLOCK.keySet()
            .stream()
            .sorted(Comparator.comparing(ResourceLocation::getNamespace).thenComparing(ResourceLocation::getPath))
            .map(id -> new SearchableSelect.Item<>(id, id.toString()))
            .toList();
    }

    /**
     * Build the template-pool list for the pool picker. Backed by {@link JigsawPoolLibrary#listPoolIds} which already
     * enumerates from the integrated server's {@code TEMPLATE_POOL} registry (single-player only); empty outside that
     * context. Items are pre-sorted by the library; we just wrap them.
     */
    private static List<SearchableSelect.Item<ResourceLocation>> buildPoolItems() {
        return JigsawPoolLibrary.listPoolIds()
            .stream()
            .map(id -> new SearchableSelect.Item<>(id, id.toString()))
            .toList();
    }

    /**
     * Build the Name list — every distinct jigsaw {@code target} appearing across all loaded templates. These are the
     * connection points "out there" that a jigsaw could set its Name to in order to be docked to. Unscoped (vs. Target
     * which scopes to a Pool) because Name describes the incoming side: any other jigsaw anywhere could Target this
     * Name, and the dropdown surfaces the universe of Targets that exist to inspire the user's choice.
     * <p>
     * Recomputed every popup-open via the widget's {@code Supplier} so newly-loaded datapack content shows up.
     */
    private static List<SearchableSelect.Item<ResourceLocation>> buildNameItems() {
        var seen = new TreeSet<ResourceLocation>(
            Comparator.comparing(ResourceLocation::getNamespace).thenComparing(ResourceLocation::getPath)
        );
        for (var poolId : JigsawPoolLibrary.listPoolIds()) {
            for (var templateId : JigsawPoolLibrary.templateIdsInPool(poolId)) {
                var template = JigsawPieceLibrary.get(templateId);
                if (template == null) {
                    continue;
                }
                for (var jigsaw : JigsawTemplateScanner.jigsawBlocks(template)) {
                    seen.add(jigsaw.target());
                }
            }
        }
        return seen.stream()
            .map(rl -> new SearchableSelect.Item<>(rl, rl.toString()))
            .toList();
    }

    /**
     * Build the Target list scoped to the jigsaw block's currently-set Pool: every distinct jigsaw {@code name}
     * appearing inside templates referenced by that pool. Picking a Target only makes sense if at least one piece the
     * Pool can spawn has a jigsaw with that Name — anything else is a connection that'll never fire.
     * <p>
     * Returns empty when no Pool is set yet, or when the Pool / its templates are unloaded. The widget shows "(no
     * matches)" in that case; the user can still set a Pool first to populate the dropdown.
     * <p>
     * Recomputed every popup-open via the widget's {@code Supplier}, so changing Pool then opening Target naturally
     * reflects the new scope.
     */
    private List<SearchableSelect.Item<ResourceLocation>> buildTargetItemsForCurrentPool() {
        var poolId = poolSelect.currentValue();
        if (poolId == null) {
            return List.of();
        }
        var seen = new TreeSet<ResourceLocation>(
            Comparator.comparing(ResourceLocation::getNamespace).thenComparing(ResourceLocation::getPath)
        );
        for (var templateId : JigsawPoolLibrary.templateIdsInPool(poolId)) {
            var template = JigsawPieceLibrary.get(templateId);
            if (template == null) {
                continue;
            }
            for (var jigsaw : JigsawTemplateScanner.jigsawBlocks(template)) {
                seen.add(jigsaw.name());
            }
        }
        return seen.stream()
            .map(rl -> new SearchableSelect.Item<>(rl, rl.toString()))
            .toList();
    }

    /**
     * Resolve a block id to an {@link ItemStack} for the picker's row icon. Blocks without an item form (e.g.
     * {@code minecraft:water_cauldron}) yield {@link ItemStack#EMPTY}, which the popup renderer skips silently — the
     * row still shows the label and remains selectable.
     */
    private static ItemStack iconForBlock(ResourceLocation blockId) {
        var block = BuiltInRegistries.BLOCK.get(blockId);
        var item = block.asItem();
        return item == net.minecraft.world.item.Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    /**
     * Extract just the block id from a finalState string. Vanilla allows either {@code "minecraft:stone"} (default
     * state) or {@code "minecraft:stone[half=top]"} (with properties); the picker only displays / selects the block, so
     * anything inside square brackets is ignored. Returns {@code null} if the string is empty or unparseable.
     */
    private static @Nullable ResourceLocation extractBlockId(@Nullable String state) {
        if (state == null || state.isEmpty()) {
            return null;
        }
        var bracket = state.indexOf('[');
        var idPart = bracket >= 0 ? state.substring(0, bracket) : state;
        try {
            return ResourceLocation.parse(idPart);
        } catch (ResourceLocationException e) {
            return null;
        }
    }

    /**
     * Discriminator for which field a commit packet is updating. The packet itself carries every field, so a "name
     * commit" is really "send the new name + the BE snapshot of every other field" — the discriminator just tells the
     * commit helper which input value to trust over the BE snapshot.
     */
    private enum BlockField {
        NAME,
        TARGET,
        POOL,
        JOINT,
        FINAL_STATE
    }

    /**
     * Build and dispatch an update packet for a single field. Other fields come from a fresh BE snapshot rather than
     * from the panel's text-input contents, so a pending edit in one input doesn't accidentally ride along when the
     * user commits a different one (they didn't ask to commit it). Parse failures revert just the failing input to its
     * BE-state value; no packet is sent in that case.
     */
    private void commitField(BlockField field, @Nullable String rawValue) {
        var block = currentBlock;
        if (block == null) {
            return;
        }
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        var snap = com.blib.engine.jigsaw.placement.JigsawBlockTarget.snapshot(mc.level, block.pos());
        if (snap == null) {
            return;
        }

        var name = snap.name();
        var target = snap.target();
        var pool = snap.pool().location();
        var joint = snap.joint().ordinal();
        var finalState = snap.finalState();

        switch (field) {
            case NAME -> {
                // Name comes from a SearchableSelect over template Names with free-text fallback. Free-text is
                // pre-validated by parseResourceLocation in the widget's parser, so values reaching here are
                // already valid; an empty/null commit just bails without sending a packet.
                var parsed = parseResourceLocation(rawValue);
                if (parsed == null) {
                    return;
                }
                name = parsed;
            }
            case TARGET -> {
                // Target comes from a SearchableSelect over jigsaw Names in the current Pool's templates, so values
                // are always parseable. Empty/null commits bail without sending a packet.
                var parsed = parseResourceLocation(rawValue);
                if (parsed == null) {
                    return;
                }
                target = parsed;
            }
            case POOL -> {
                // Pool comes from a SearchableSelect over the registry, so values are always parseable. We still
                // guard against an empty/null commit by bailing without sending a packet (no input to revert,
                // since the widget shows whatever its currentValue is).
                var parsed = parseResourceLocation(rawValue);
                if (parsed == null) {
                    return;
                }
                pool = parsed;
            }
            case JOINT -> {
                joint = jointSelector.selectedIndex();
            }
            case FINAL_STATE -> {
                if (rawValue == null) {
                    return;
                }
                finalState = rawValue;
            }
        }

        BLib.MOD.networking()
            .sendToServer(new C2SUpdateJigsawBlockPayload(block.pos(), name, target, pool, joint, finalState));
    }

    private static @Nullable ResourceLocation parseResourceLocation(@Nullable String raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        try {
            return ResourceLocation.parse(raw);
        } catch (ResourceLocationException e) {
            return null;
        }
    }

    // ── Tag inspector view ──

    private static final int TAG_REPLACE_TOGGLE_WIDTH = 78;

    private static final int TAG_VIEW_TOGGLE_WIDTH = 96;

    private static final int TAG_ROW_HEIGHT = 12;

    private static final int TAG_CHIP_WIDTH = 10;

    private static final int TAG_REMOVE_BUTTON_WIDTH = 12;

    private static final int TAG_REQUIRED_BADGE_WIDTH = 20;

    /** Required state — default, dim text so it doesn't shout. */
    private static final int TAG_REQUIRED_BADGE_REQ_COLOR = 0xFF606068;

    private static final int TAG_REQUIRED_BADGE_REQ_HOVER = 0xFFB0B0B8;

    /** Optional state — accent yellow so non-default rows stand out. */
    private static final int TAG_REQUIRED_BADGE_OPT_COLOR = 0xFFE6C26B;

    private static final int TAG_REQUIRED_BADGE_OPT_HOVER = 0xFFFFD685;

    private static final int TAG_RIGHT_PAD = 4;

    private static final int TAG_FOOTER_HEIGHT = SearchableSelect.HEIGHT + 6;

    private static final int TAG_TOOLBAR_HEIGHT = SearchableSelect.HEIGHT + 6;

    private static final int TAG_REGISTRY_LABEL_COLOR = 0xFF7C8088;

    private static final int TAG_CHIP_TAGREF_COLOR = 0xFF7CB6E0;

    private static final int TAG_CHIP_DIRECT_COLOR = 0xFFE6C26B;

    private static final int TAG_REMOVE_ICON_COLOR = 0xFF7C8088;

    private static final int TAG_REMOVE_ICON_HOVER_COLOR = 0xFFFF6868;

    private static final int TAG_ROW_HOVER_BG = 0xFF1F1F26;

    private static final int TAG_EMPTY_NOTE_COLOR = 0xFF606068;

    /**
     * Render the inspector view for a {@link TagSelectable}. Layout (top-down): a registry meta-line, a Merge/Replace
     * toggle + Reload button row, a scrollable list of entry rows (each with a tag-ref/direct chip, the id, a clickable
     * {@code req}/{@code opt} required-flag badge, and a {@code ×} remove button), and a sticky footer with the
     * Add-entry picker.
     * <p>
     * Reads from {@link TagDraftCache} for the entry list (server-pushed authoritative state — the Tag Editor moved
     * into the inspector but its data flow is unchanged from the original separate-panel design). Sends
     * {@link C2SRequestTagDraftPayload} on first selection / change to populate the cache, and
     * {@link C2SRequestRegistryEntriesPayload} on first registry-change to populate the picker's choices.
     */
    public void internalRenderTagView(GuiGraphics graphics, Font font, int x, int y, int width, int mouseX, int mouseY, TagSelectable tag) {
        var registryKey = tag.registryKey();
        var tagId = tag.tagId();

        // Registry change → fetch its element catalog (one-shot per registry per session).
        if (!registryKey.equals(tagLastFetchedRegistryEntries) && RegistryEntriesCache.get(registryKey) == null) {
            BLib.MOD.networking().sendToServer(new C2SRequestRegistryEntriesPayload(ProjectSession.activeProjectName(), registryKey));
            tagLastFetchedRegistryEntries = registryKey;
        }

        // Tag change → reset scroll + refetch the draft.
        if (!registryKey.equals(tagLastShownRegistry) || !tagId.equals(tagLastShownTag)) {
            tagLastShownRegistry = registryKey;
            tagLastShownTag = tagId;
            tagScroll.reset();
            BLib.MOD.networking()
                .sendToServer(new C2SRequestTagDraftPayload(ProjectSession.activeProjectName(), registryKey, tagId));
        }

        var draft = TagDraftCache.get(registryKey, tagId);
        var resolvedMembers = draft != null ? draft.resolvedMembers() : List.<ResourceLocation>of();
        if (draft != null) {
            tagCachedEntries = draft.entries();
            tagReplaceToggle.setSelectedIndex(draft.replace() ? 1 : 0);
        } else {
            tagCachedEntries = List.of();
        }

        // Registry meta line.
        var rowY = y + ROW_GAP;
        graphics.drawString(font, Component.literal(registryKey.toString()), x + CONTENT_PADDING, rowY, TAG_REGISTRY_LABEL_COLOR, false);
        rowY += LINE_HEIGHT + ROW_GAP;

        // Toolbar row: View toggle (left) + Replace toggle (right of view). Reload Project moved to the global
        // toolbar — applying source edits is a project-level action, not a per-tag one.
        var toolbarY = rowY;
        var viewX = x + CONTENT_PADDING;
        var replaceX = viewX + TAG_VIEW_TOGGLE_WIDTH + 4;
        tagViewToggle.render(graphics, viewX, toolbarY, TAG_VIEW_TOGGLE_WIDTH, mouseX, mouseY);
        tagReplaceToggle.render(graphics, replaceX, toolbarY, TAG_REPLACE_TOGGLE_WIDTH, mouseX, mouseY);
        rowY += TAG_TOOLBAR_HEIGHT;

        // Body region: scrollable list. The Source view leaves room for an Add-entry footer; the Resolved view is
        // read-only and reclaims the footer space for more rows.
        var sourceMode = tagViewToggle.selectedIndex() == 1;
        var bodyY = rowY;
        var panelBottom = rectY + rectHeight;
        var footerY = sourceMode ? (panelBottom - TAG_FOOTER_HEIGHT) : panelBottom;
        var bodyHeight = Math.max(0, footerY - bodyY);
        var listX = x + CONTENT_PADDING;
        var listW = width - 2 * CONTENT_PADDING;

        if (sourceMode) {
            renderTagSourceList(graphics, font, listX, bodyY, listW, bodyHeight, mouseX, mouseY);
            // Footer: Add-entry picker, full-width — only meaningful in Source mode (Resolved is read-only).
            var footerSelectY = footerY + (TAG_FOOTER_HEIGHT - SearchableSelect.HEIGHT) / 2;
            tagAddEntrySelect.render(graphics, listX, footerSelectY, listW, mouseX, mouseY);
        } else {
            renderTagResolvedList(graphics, font, listX, bodyY, listW, bodyHeight, resolvedMembers, mouseX, mouseY);
        }
    }

    private void renderTagSourceList(
        GuiGraphics graphics,
        Font font,
        int listX,
        int bodyY,
        int listW,
        int bodyHeight,
        int mouseX,
        int mouseY
    ) {
        if (tagCachedEntries.isEmpty()) {
            drawCenteredNote(graphics, font, listX, bodyY, listW, bodyHeight, "(no project entries — add one below)");
            return;
        }
        var contentHeight = tagCachedEntries.size() * TAG_ROW_HEIGHT;
        tagScroll.layout(bodyHeight, contentHeight);
        applyRawScissor(graphics, listX, bodyY, listW, bodyHeight);
        try {
            var scrollY = (int) tagScroll.scrollY();
            var firstVisible = Math.max(0, scrollY / TAG_ROW_HEIGHT);
            var lastVisible = Math.min(tagCachedEntries.size() - 1, (scrollY + bodyHeight) / TAG_ROW_HEIGHT);
            for (var i = firstVisible; i <= lastVisible; i++) {
                var entry = tagCachedEntries.get(i);
                var entryY = bodyY + i * TAG_ROW_HEIGHT - scrollY;
                renderTagEntryRow(graphics, font, listX, entryY, listW, entry, mouseX, mouseY);
            }
        } finally {
            graphics.flush();
            RenderSystem.disableScissor();
        }
        tagScroll.renderScrollbar(graphics, listX, bodyY, listW, bodyHeight, mouseX, mouseY);
    }

    private void renderTagResolvedList(
        GuiGraphics graphics,
        Font font,
        int listX,
        int bodyY,
        int listW,
        int bodyHeight,
        List<ResourceLocation> resolvedMembers,
        int mouseX,
        int mouseY
    ) {
        if (resolvedMembers.isEmpty()) {
            drawCenteredNote(graphics, font, listX, bodyY, listW, bodyHeight, "(tag is empty in the live registry)");
            return;
        }
        var contentHeight = resolvedMembers.size() * TAG_ROW_HEIGHT;
        tagScroll.layout(bodyHeight, contentHeight);
        applyRawScissor(graphics, listX, bodyY, listW, bodyHeight);
        try {
            var scrollY = (int) tagScroll.scrollY();
            var firstVisible = Math.max(0, scrollY / TAG_ROW_HEIGHT);
            var lastVisible = Math.min(resolvedMembers.size() - 1, (scrollY + bodyHeight) / TAG_ROW_HEIGHT);
            var rightEdge = listX + listW - ScrollContainer.SCROLLBAR_GUTTER - TAG_RIGHT_PAD;
            var labelMax = Math.max(0, rightEdge - listX - 2 - TAG_CHIP_WIDTH);
            for (var i = firstVisible; i <= lastVisible; i++) {
                var member = resolvedMembers.get(i);
                var entryY = bodyY + i * TAG_ROW_HEIGHT - scrollY;
                var textY = entryY + (TAG_ROW_HEIGHT - font.lineHeight + 2) / 2;
                // Direct-entry chip — every resolved member is an element id (refs are already expanded).
                graphics.drawString(font, Component.literal("▪"), listX + 2, textY, TAG_CHIP_DIRECT_COLOR, false);
                var label = font.plainSubstrByWidth(member.toString(), labelMax);
                graphics.drawString(font, Component.literal(label), listX + 2 + TAG_CHIP_WIDTH, textY, VALUE_COLOR, false);
            }
        } finally {
            graphics.flush();
            RenderSystem.disableScissor();
        }
        tagScroll.renderScrollbar(graphics, listX, bodyY, listW, bodyHeight, mouseX, mouseY);
    }

    private void renderTagEntryRow(GuiGraphics graphics, Font font, int x, int y, int width, TagEntryDraft entry, int mouseX, int mouseY) {
        var hovered = mouseY >= y
            && mouseY < y + TAG_ROW_HEIGHT
            && mouseX >= x
            && mouseX < x + width - ScrollContainer.SCROLLBAR_GUTTER;
        if (hovered) {
            graphics.fill(x, y, x + width - ScrollContainer.SCROLLBAR_GUTTER, y + TAG_ROW_HEIGHT, TAG_ROW_HOVER_BG);
        }

        // Operability gates: in replace mode the project is the sole contributor so every entry is operable; in
        // merge mode an upstream-duplicate entry is a no-op to remove or toggle, so hide the controls. Pending-add
        // entries keep the X even on upstream duplicates so the user can cancel the just-issued staging — undoing
        // a pre-reload add is a real disk-write, not a vanilla-load-time no-op.
        var draft = tagLastShownRegistry != null && tagLastShownTag != null
            ? TagDraftCache.get(tagLastShownRegistry, tagLastShownTag)
            : null;
        var replaceMode = draft != null && draft.replace();
        var isPendingAdd = tagLastShownRegistry != null
            && tagLastShownTag != null
            && TagStagingCache.isEntryStagedAdd(tagLastShownRegistry, tagLastShownTag, entry.isTagRef(), entry.id());
        var removeOperable = replaceMode || !entry.inUpstream() || isPendingAdd;
        var requiredOperable = replaceMode || !entry.inUpstream();

        var rightEdge = x + width - ScrollContainer.SCROLLBAR_GUTTER - TAG_RIGHT_PAD;
        var removeX = rightEdge - TAG_REMOVE_BUTTON_WIDTH;
        // Reserve the badge column only when it'll actually render — otherwise the label gets the extra room.
        var requiredBadgeX = requiredOperable ? (removeX - 2 - TAG_REQUIRED_BADGE_WIDTH) : removeX;
        // If neither control is visible, the label uses the full right edge.
        var labelRight = removeOperable ? requiredBadgeX : rightEdge;
        var textY = y + (TAG_ROW_HEIGHT - font.lineHeight + 2) / 2;

        // Chip — # for tag-ref, ▪ for direct.
        var chip = entry.isTagRef() ? "#" : "▪";
        graphics.drawString(
            font,
            Component.literal(chip),
            x + 2,
            textY,
            entry.isTagRef() ? TAG_CHIP_TAGREF_COLOR : TAG_CHIP_DIRECT_COLOR,
            false
        );

        // Id label color picks up the unified staging scheme: red while the entry is staged unreloaded, otherwise it
        // follows the parent tag's status (project-new → green, project-modified-upstream → blue). The current tag's
        // (registry, id) is set in renderTagView's instance state, so we can look up its catalog entry here without
        // changing the call signature. (opt) is no longer a suffix — it lives in its own clickable badge to the right
        // of the label so the user can toggle the required flag directly.
        var label = (entry.isTagRef() ? "#" : "") + entry.id();
        var labelX = x + 2 + TAG_CHIP_WIDTH;
        var labelMax = Math.max(0, labelRight - labelX - 4);
        var labelColor = entryLabelColor(entry);
        graphics.drawString(font, Component.literal(font.plainSubstrByWidth(label, labelMax)), labelX, textY, labelColor, false);

        // req/opt badge — click toggles the required flag. Hidden for upstream-duplicate entries in merge mode
        // (toggling there would be a vanilla-load-time no-op). Required state stays dim (default); optional state
        // highlights with the accent color so the user spots non-default entries at a glance.
        if (requiredOperable) {
            var badgeText = entry.required() ? "req" : "opt";
            var badgeHovered = mouseX >= requiredBadgeX
                && mouseX < requiredBadgeX + TAG_REQUIRED_BADGE_WIDTH
                && mouseY >= y
                && mouseY < y + TAG_ROW_HEIGHT;
            int badgeColor;
            if (entry.required()) {
                badgeColor = badgeHovered ? TAG_REQUIRED_BADGE_REQ_HOVER : TAG_REQUIRED_BADGE_REQ_COLOR;
            } else {
                badgeColor = badgeHovered ? TAG_REQUIRED_BADGE_OPT_HOVER : TAG_REQUIRED_BADGE_OPT_COLOR;
            }
            var badgeTextX = requiredBadgeX + (TAG_REQUIRED_BADGE_WIDTH - font.width(badgeText)) / 2;
            graphics.drawString(font, Component.literal(badgeText), badgeTextX, textY, badgeColor, false);
            tagRequiredToggleHits.add(
                new TagRequiredToggleHit(requiredBadgeX, y, TAG_REQUIRED_BADGE_WIDTH, TAG_ROW_HEIGHT, entry.rawIndex(), entry.required())
            );
        }

        // × remove button (whole rect is the hot zone; glyph centered). Hidden for upstream-duplicate entries in
        // merge mode unless the user just added this entry — pending adds keep the X so the user can cancel the
        // not-yet-committed staging.
        if (removeOperable) {
            var removeHovered = mouseX >= removeX
                && mouseX < removeX + TAG_REMOVE_BUTTON_WIDTH
                && mouseY >= y
                && mouseY < y + TAG_ROW_HEIGHT;
            var removeText = "×";
            var removeColor = removeHovered ? TAG_REMOVE_ICON_HOVER_COLOR : TAG_REMOVE_ICON_COLOR;
            var removeTextX = removeX + (TAG_REMOVE_BUTTON_WIDTH - font.width(removeText)) / 2;
            graphics.drawString(font, Component.literal(removeText), removeTextX, textY, removeColor, false);
            tagRemoveHits.add(new TagRemoveHit(removeX, y, TAG_REMOVE_BUTTON_WIDTH, TAG_ROW_HEIGHT, entry.rawIndex()));
        }
    }

    /**
     * Color for a tag-entry row's id label. Matches the unified palette: staged adds + tag-level edits render red,
     * project-new tags pull their entries to green, project-modified-upstream tags pull theirs to blue. Falls back to
     * {@link #VALUE_COLOR} when there's no tag in scope (defensive — renderTagEntryRow is only reached from
     * renderTagSourceList, which only runs when a TagSelectable is the current selection).
     */
    private int entryLabelColor(TagEntryDraft entry) {
        var registryKey = tagLastShownRegistry;
        var tagId = tagLastShownTag;
        if (registryKey == null || tagId == null) {
            return VALUE_COLOR;
        }
        // Only THIS entry's staging colors it red — a tag-level edit elsewhere (e.g. a different entry added) keeps
        // already-committed entries in their committed color. The Tag Browser still flags the tag itself as staged.
        if (TagStagingCache.isEntryStagedAdd(registryKey, tagId, entry.isTagRef(), entry.id())) {
            return BLOCK_TAG_LABEL_STAGED;
        }
        // In merge mode, an upstream-duplicate entry is read-only context, not a project edit. Paint it the neutral
        // upstream-gray to match the Tag Browser's "you didn't author this" tint. Replace mode skips this branch
        // because the project is the sole contributor — the row is genuinely operable there.
        var draft = TagDraftCache.get(registryKey, tagId);
        var replaceMode = draft != null && draft.replace();
        if (!replaceMode && entry.inUpstream()) {
            return BLOCK_TAG_LABEL_UPSTREAM;
        }
        var inProject = isTagInProject(registryKey, tagId);
        var inUpstream = isTagInUpstream(registryKey, tagId);
        var equivalentToUpstream = isTagEquivalentToUpstream(registryKey, tagId);
        // equivalentToUpstream → the project's JSON doesn't actually modify the merged tag, so entries shouldn't
        // read as project-owned changes. Drop to the neutral value color for the entry label.
        if (inProject && !equivalentToUpstream && inUpstream) {
            return BLOCK_TAG_LABEL_PROJECT_MODIFIED;
        }
        if (inProject && !equivalentToUpstream) {
            return BLOCK_TAG_LABEL_PROJECT_NEW;
        }
        return VALUE_COLOR;
    }

    private static void drawCenteredNote(GuiGraphics graphics, Font font, int x, int y, int width, int height, String text) {
        var textWidth = font.width(text);
        var noteX = x + (width - textWidth) / 2;
        var noteY = y + (height - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(text), noteX, noteY, TAG_EMPTY_NOTE_COLOR, false);
    }

    private boolean handleTagMouseClicked(double mouseX, double mouseY, int button) {
        var single = SelectionManager.current().single();
        if (!(single instanceof TagSelectable tag)) {
            return false;
        }
        // View toggle (Resolved/Source) — reset scroll on switch since the two lists have unrelated row counts.
        var viewIndexBefore = tagViewToggle.selectedIndex();
        if (tagViewToggle.mouseClicked(mouseX, mouseY, button)) {
            if (tagViewToggle.selectedIndex() != viewIndexBefore) {
                tagScroll.reset();
            }
            return true;
        }
        // Add-entry / × buttons / scroll only fire in Source mode (Resolved is read-only); the controls themselves
        // aren't rendered in Resolved, so their mouseClicked handlers naturally no-op against off-screen rects, but
        // we still call tagScroll.mouseClicked so dragging the scrollbar works in either mode.
        if (tagAddEntrySelect.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        var indexBefore = tagReplaceToggle.selectedIndex();
        if (tagReplaceToggle.mouseClicked(mouseX, mouseY, button)) {
            if (tagReplaceToggle.selectedIndex() != indexBefore) {
                var nextIsReplace = tagReplaceToggle.selectedIndex() == 1;
                if (nextIsReplace && actionHandler != null) {
                    // Switching to Replace is destructive — wipes vanilla / mod / other-pack contributions to this
                    // tag and uses ONLY the project's values. Roll the toggle back visually until the dialog confirms,
                    // so a stray click doesn't quietly nuke upstream entries.
                    tagReplaceToggle.setSelectedIndex(indexBefore);
                    actionHandler.confirm(
                        "Switch to Replace mode?",
                        "Replace mode wipes vanilla and other packs' contributions to this tag — only entries in your "
                            + "project's JSON will end up in the merged tag. Vanilla entries you didn't explicitly add "
                            + "will disappear from this tag after Reload Project.",
                        "Switch to Replace",
                        true,
                        () -> {
                            tagReplaceToggle.setSelectedIndex(1);
                            commitSetTagReplace(tag, true);
                        }
                    );
                } else {
                    commitSetTagReplace(tag, nextIsReplace);
                }
            }
            return true;
        }
        if (tagScroll.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0) {
            for (var hit : tagRemoveHits) {
                if (mouseX >= hit.x() && mouseX < hit.x() + hit.w() && mouseY >= hit.y() && mouseY < hit.y() + hit.h()) {
                    commitRemoveTagEntry(tag, hit.rawIndex());
                    return true;
                }
            }
            for (var hit : tagRequiredToggleHits) {
                if (mouseX >= hit.x() && mouseX < hit.x() + hit.w() && mouseY >= hit.y() && mouseY < hit.y() + hit.h()) {
                    commitSetTagEntryRequired(tag, hit.rawIndex(), !hit.currentRequired());
                    return true;
                }
            }
        }
        return false;
    }

    private void commitAddTagEntry(TagPickerItem item) {
        var single = SelectionManager.current().single();
        if (!(single instanceof TagSelectable tag) || ProjectSession.activeProject() == null) {
            return;
        }
        BLib.MOD.networking()
            .sendToServer(
                new C2SAddTagEntryPayload(
                    ProjectSession.activeProjectName(),
                    tag.registryKey(),
                    tag.tagId(),
                    item.isTagRef(),
                    item.id(),
                    true
                )
            );
        TagStagingCache.markEntryAdded(tag.registryKey(), tag.tagId(), item.isTagRef(), item.id());
        tagAddEntrySelect.setCurrentValue(null);
    }

    private void commitRemoveTagEntry(TagSelectable tag, int rawIndex) {
        if (ProjectSession.activeProject() == null) {
            return;
        }
        // Look up the entry by rawIndex so we can stage the remove with its (isTagRef, id). The lookup is fine to do
        // before the packet send — tagCachedEntries is a frame-stable snapshot of what the user is looking at.
        for (var entry : tagCachedEntries) {
            if (entry.rawIndex() == rawIndex) {
                TagStagingCache.markEntryRemoved(tag.registryKey(), tag.tagId(), entry.isTagRef(), entry.id());
                break;
            }
        }
        BLib.MOD.networking()
            .sendToServer(new C2SRemoveTagEntryPayload(ProjectSession.activeProjectName(), tag.registryKey(), tag.tagId(), rawIndex));
    }

    private void commitSetTagReplace(TagSelectable tag, boolean replace) {
        if (ProjectSession.activeProject() == null) {
            return;
        }
        BLib.MOD.networking()
            .sendToServer(new C2SSetTagReplacePayload(ProjectSession.activeProjectName(), tag.registryKey(), tag.tagId(), replace));
        TagStagingCache.markTagEdited(tag.registryKey(), tag.tagId());
    }

    private void commitSetTagEntryRequired(TagSelectable tag, int rawIndex, boolean required) {
        if (ProjectSession.activeProject() == null) {
            return;
        }
        // Stage the change so the entry repaints red until reload. Look up by rawIndex first so we can use the
        // same (isTagRef, id) key the rest of the staging system uses; toggling required doesn't change either.
        for (var entry : tagCachedEntries) {
            if (entry.rawIndex() == rawIndex) {
                TagStagingCache.markEntryAdded(tag.registryKey(), tag.tagId(), entry.isTagRef(), entry.id());
                break;
            }
        }
        BLib.MOD.networking()
            .sendToServer(
                new C2SSetTagEntryRequiredPayload(ProjectSession.activeProjectName(), tag.registryKey(), tag.tagId(), rawIndex, required)
            );
    }

    /**
     * Build the Add-entry picker items from {@link RegistryEntriesCache} for the current registry. Tag refs first
     * (sorted, displayed with leading {@code #}), then direct entries (sorted). Empty list when the registry's catalog
     * hasn't arrived yet — the popup just shows nothing until the server replies.
     */
    private List<SearchableSelect.Item<TagPickerItem>> buildTagAddEntryItems() {
        var single = SelectionManager.current().single();
        if (!(single instanceof TagSelectable tag)) {
            return List.of();
        }
        var cache = RegistryEntriesCache.get(tag.registryKey());
        if (cache == null) {
            return List.of();
        }
        var out = new ArrayList<SearchableSelect.Item<TagPickerItem>>(cache.elements().size() + cache.tagIds().size());
        var sortedTags = new ArrayList<>(cache.tagIds());
        sortedTags.sort(Comparator.comparing(ResourceLocation::toString));
        for (var rl : sortedTags) {
            out.add(new SearchableSelect.Item<>(new TagPickerItem(true, rl), "#" + rl));
        }
        var sortedElements = new ArrayList<>(cache.elements());
        sortedElements.sort(Comparator.comparing(ResourceLocation::toString));
        for (var rl : sortedElements) {
            out.add(new SearchableSelect.Item<>(new TagPickerItem(false, rl), rl.toString()));
        }
        return out;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Only the tag scroll needs drag handling — other views have no scrolling.
        if (SelectionManager.current().single() instanceof TagSelectable) {
            return tagScroll.mouseDragged(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (SelectionManager.current().single() instanceof TagSelectable) {
            return tagScroll.mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (SelectionManager.current().single() instanceof TagSelectable) {
            if (mouseX < rectX || mouseX >= rectX + rectWidth || mouseY < rectY || mouseY >= rectY + rectHeight) {
                return false;
            }
            return tagScroll.mouseScrolled(scrollY);
        }
        return false;
    }

    /** Item type for the Add-entry picker — distinguishes tag-refs from direct entries. */
    public record TagPickerItem(
        boolean isTagRef,
        ResourceLocation id
    ) {

        public String displayLabel() {
            return (isTagRef ? "#" : "") + id;
        }
    }

    /** Hit-test rect + addressed entry for a per-row × button. */
    private record TagRemoveHit(
        int x,
        int y,
        int w,
        int h,
        int rawIndex
    ) {}

    /** Per-row hit rect for the entry inspector's req/opt badge. {@code currentRequired} feeds the toggle action. */
    private record TagRequiredToggleHit(
        int x,
        int y,
        int w,
        int h,
        int rawIndex,
        boolean currentRequired
    ) {}

    /** Per-row hit rect for the block-inspector Tags section's [×] buttons. */
    private record BlockTagRemoveHit(
        int x,
        int y,
        int w,
        int h,
        ResourceLocation registryKey,
        ResourceLocation tagId
    ) {}

    /**
     * Set the GL scissor to clip drawing to {@code (x, y, w, h)} in this panel's logical-pixel space. Mirrors the
     * raw-scissor idiom from the other engine panels — bypasses the GuiGraphics scissor stack so stale upstream entries
     * can't clip our row area to a smaller residual rect.
     */
    private static void applyRawScissor(GuiGraphics graphics, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) {
            RenderSystem.disableScissor();
            return;
        }
        graphics.flush();

        var matrix = graphics.pose().last().pose();
        var topLeft = matrix.transformPosition((float) x, (float) y, 0f, new Vector3f());
        var bottomRight = matrix.transformPosition((float) (x + w), (float) (y + h), 0f, new Vector3f());

        var window = Minecraft.getInstance().getWindow();
        var winHeight = window.getHeight();
        var guiScale = window.getGuiScale();
        var leftRaw = (int) ((double) topLeft.x * guiScale);
        var bottomRaw = (int) ((double) winHeight - (double) bottomRight.y * guiScale);
        var widthRaw = Math.max(0, (int) ((double) (bottomRight.x - topLeft.x) * guiScale));
        var heightRaw = Math.max(0, (int) ((double) (bottomRight.y - topLeft.y) * guiScale));
        RenderSystem.enableScissor(leftRaw, bottomRaw, widthRaw, heightRaw);
    }
}
