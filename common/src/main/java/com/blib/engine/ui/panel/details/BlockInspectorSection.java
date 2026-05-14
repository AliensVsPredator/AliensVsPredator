package com.blib.engine.ui.panel.details;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import com.blib.engine.domain.selection.picking.BlockSelectable;
import com.blib.engine.jigsaw.JigsawPieceLibrary;
import com.blib.engine.jigsaw.JigsawPoolLibrary;
import com.blib.engine.jigsaw.placement.JigsawBlockTarget;
import com.blib.engine.jigsaw.placement.JigsawTemplateScanner;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.tag.TagCatalogCache;
import com.blib.engine.tag.TagStagingCache;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.widget.Checkbox;
import com.blib.engine.ui.widget.SearchableSelect;
import com.blib.engine.ui.widget.SegmentedControl;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SAddTagEntryPayload;
import com.blib.mod.common.network.packet.C2SRemoveBlockTagPayload;
import com.blib.mod.common.network.packet.C2SRequestTagCatalogPayload;
import com.blib.mod.common.network.packet.C2SSetBlockStatePropertyPayload;
import com.blib.mod.common.network.packet.C2SUpdateJigsawBlockPayload;

/**
 * Inspector section for {@link BlockSelectable}. Unified single-block inspector — always renders the generic sections
 * (Position, Block id, Properties, Tags, Block Entity); when the block at {@code bs.pos()} is a jigsaw, an additional
 * jigsaw section (Identity / Joint / Final State) is rendered at the top. Jigsaw blocks are just blocks with extra
 * editable NBT, so they get every widget a regular block does plus their own.
 * <p>
 * Block-state property widgets ({@link Checkbox} for boolean properties, {@link SearchableSelect} for everything else)
 * are rebuilt only when the inspected block's pos or block type changes — stable widget instances let each track its
 * own popup state across frames. Jigsaw widgets share state across the inspector lifetime so switching off-jigsaw and
 * back keeps coherent values; selection swaps re-key the orphan cache and reload all widgets from the new block's BE.
 */
@ApiStatus.Internal
public final class BlockInspectorSection implements InspectorSection<BlockSelectable> {

    private final DetailsPanel panel;

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

    /** Cache TTL for the orphan check; balances responsiveness against the cost of scanning loaded chunks. */
    private static final long ORPHAN_CHECK_INTERVAL_MS = 500L;

    private static final int BLOCK_TAG_ROW_HEIGHT = 12;

    private static final int BLOCK_TAG_REMOVE_BUTTON_WIDTH = 12;

    static final int BLOCK_TAG_LABEL_PROJECT_NEW = 0xFF80E080;

    static final int BLOCK_TAG_LABEL_PROJECT_MODIFIED = 0xFF7CB6E0;

    /** Staged-but-unreloaded color — picks up after the user adds a tag, clears on Reload Project. */
    static final int BLOCK_TAG_LABEL_STAGED = 0xFFE08080;

    static final int BLOCK_TAG_LABEL_UPSTREAM = 0xFFD0D0D0;

    private static final int BLOCK_TAG_REMOVE_ICON_COLOR = 0xFF7C8088;

    private static final int BLOCK_TAG_REMOVE_ICON_HOVER_COLOR = 0xFFFF6868;

    private static final int BLOCK_TAG_EMPTY_NOTE_COLOR = 0xFF606068;

    private final SearchableSelect<ResourceLocation> nameSelect = new SearchableSelect<>(
        BlockInspectorSection::buildNameItems,
        rl -> rl == null ? "(none)" : rl.toString(),
        null,
        BlockInspectorSection::parseResourceLocation,
        null,
        rl -> commitField(BlockField.NAME, rl == null ? "" : rl.toString())
    );

    private final SearchableSelect<ResourceLocation> targetSelect = new SearchableSelect<>(
        this::buildTargetItemsForCurrentPool,
        rl -> rl == null ? "(none)" : rl.toString(),
        null,
        BlockInspectorSection::parseResourceLocation,
        null,
        rl -> commitField(BlockField.TARGET, rl == null ? "" : rl.toString())
    );

    private final SearchableSelect<ResourceLocation> poolSelect = new SearchableSelect<>(
        BlockInspectorSection::buildPoolItems,
        rl -> rl == null ? "(none)" : rl.toString(),
        null,
        rl -> commitField(BlockField.POOL, rl == null ? "" : rl.toString())
    );

    private final SearchableSelect<ResourceLocation> finalStateSelect = new SearchableSelect<>(
        BlockInspectorSection::buildBlockItems,
        rl -> rl == null ? "(none)" : rl.toString(),
        BlockInspectorSection::iconForBlock,
        null,
        rl -> commitField(BlockField.FINAL_STATE, rl == null ? "" : rl.toString())
    );

    private final SegmentedControl jointSelector = new SegmentedControl(List.of("ALIGNED", "ROLLABLE"), 0);

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
     * Cache key + result for the orphan check. {@code lastOrphanCheckPos} is part of the key because the scan excludes
     * the inspected jigsaw itself ("does any *other* jigsaw Target this Name?") — switching to a different block with
     * the same Name could legitimately flip the answer, so the cache must invalidate.
     */
    private @Nullable ResourceLocation lastOrphanCheckName;

    private @Nullable BlockPos lastOrphanCheckPos;

    private boolean lastOrphanResult;

    private long lastOrphanCheckMs;

    /**
     * Per-property {@link Checkbox} widgets for boolean block-state properties (waterlogged, snowy, …). Sister map to
     * {@link #genericBlockPropertySelects}: every property in the rebuilt set ends up in exactly one of the two,
     * dispatched by {@code Property} subtype. Keyed by property name. Rebuilt only when the inspected block's pos or
     * block type changes — keeping the widget instance stable across frames lets each track its own rect for click
     * dispatch in {@link #mouseClicked}.
     */
    private final LinkedHashMap<String, Checkbox> genericBlockPropertyCheckboxes = new LinkedHashMap<>();

    /**
     * Per-property {@link SearchableSelect} widgets for non-boolean block-state properties (enums like facing/shape,
     * integer properties like power). T is the canonical value string ({@link Property#getName(Comparable)}) so the
     * onSelect lambda can ship it straight into a {@link C2SSetBlockStatePropertyPayload} without re-deriving the
     * stringification.
     */
    private final LinkedHashMap<String, SearchableSelect<String>> genericBlockPropertySelects = new LinkedHashMap<>();

    /** Cache key for the per-property widget maps — pos changes ⇒ rebuild. */
    private @Nullable BlockPos genericBlockCachedPos;

    /** Cache key for the per-property widget maps — block type changes (replace-in-place) ⇒ rebuild. */
    private @Nullable Block genericBlockCachedBlock;

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

    public BlockInspectorSection(DetailsPanel panel) {
        this.panel = panel;
    }

    @Override
    public String id() {
        return "block";
    }

    @Override
    public Class<BlockSelectable> selectableType() {
        return BlockSelectable.class;
    }

    /**
     * Called by {@link DetailsPanel#render} when the selection isn't a block so a stale {@link #currentBlock} can't
     * slip a commit through.
     */
    void clearCurrentBlock() {
        currentBlock = null;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, BlockSelectable target, int mouseX, int mouseY) {
        return renderBody(graphics, EngineFont.get(), x, y, width, mouseX, mouseY, target);
    }

    /**
     * Unified single-block inspector body. Always renders the generic sections (Position, Block id, Properties, Tags,
     * Block Entity); when the block at {@code bs.pos()} is a jigsaw, an additional jigsaw section (Identity / Joint /
     * Final State) is rendered at the top.
     */
    private int renderBody(GuiGraphics graphics, Font font, int x, int y, int width, int mouseX, int mouseY, BlockSelectable bs) {
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
            return DetailsPanel.drawNote(graphics, font, x, y, "Block is no longer loaded.");
        }

        var pos = bs.pos();
        var block = state.getBlock();
        rebuildGenericBlockPropertyWidgets(state, pos, block);

        var rowY = y;
        // Jigsaw sections at the top — these are the most-edited fields for a jigsaw block (Pool / Target / Joint),
        // so they sit above the universal block sections. For non-jigsaw blocks the helper is a no-op.
        if (state.is(Blocks.JIGSAW)) {
            rowY = renderJigsawSections(graphics, font, x, rowY, width, mouseX, mouseY, bs);
        }

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Position");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "X", String.valueOf(pos.getX()));
        rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "Y", String.valueOf(pos.getY()));
        rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "Z", String.valueOf(pos.getZ()));
        rowY += InspectorStyle.ROW_GAP;

        rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Block");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        var blockId = BuiltInRegistries.BLOCK.getKey(block);
        rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "ID", blockId.toString());

        if (!state.getProperties().isEmpty()) {
            rowY += InspectorStyle.ROW_GAP;
            rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Properties");
            rowY += InspectorStyle.CONTENT_PADDING / 2;
            for (var prop : state.getProperties()) {
                rowY = renderGenericBlockPropertyRow(graphics, font, x, rowY, width, prop, state, mouseX, mouseY);
            }
        }

        var mc = Minecraft.getInstance();

        rowY += InspectorStyle.ROW_GAP;
        rowY = renderBlockTagsSection(graphics, font, x, rowY, width, mouseX, mouseY, blockId);

        var be = mc.level != null ? mc.level.getBlockEntity(pos) : null;
        if (be != null) {
            rowY += InspectorStyle.ROW_GAP;
            rowY = DetailsPanel.drawSectionHeader(graphics, font, x, rowY, width, "Block Entity");
            rowY += InspectorStyle.CONTENT_PADDING / 2;
            var beTypeId = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType());
            rowY = DetailsPanel.drawRow(graphics, font, x, rowY, "Type", beTypeId != null ? beTypeId.toString() : "?");
        }
        return rowY;
    }

    /**
     * Render the jigsaw-specific inspector sections (Identity / Joint / Final State) for the given block. Returns the
     * next-row {@code y}. Called from {@link #renderBody} only when the block at {@code selectable.pos()} is actually a
     * jigsaw — the jigsaw widgets share state across the inspector lifetime, so an inspector that switched off-jigsaw
     * and back keeps coherent values.
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
        var snap = JigsawBlockTarget.snapshot(mc.level, selectable.pos());
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

        var rowY = DetailsPanel.drawSectionHeader(graphics, font, x, y, width, "Identity");
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        var orphaned = isNameOrphaned(snap.name(), pos);
        rowY = drawNameRow(graphics, font, x, rowY, width, orphaned, mouseX, mouseY);
        rowY = panel.drawSelectRow(graphics, font, x, rowY, width, "Target", HELP_TARGET, targetSelect, mouseX, mouseY);
        rowY = panel.drawSelectRow(graphics, font, x, rowY, width, "Pool", HELP_POOL, poolSelect, mouseX, mouseY);

        rowY = panel.drawSectionHeaderWithHelp(graphics, font, x, rowY, width, "Joint", HELP_JOINT, mouseX, mouseY);
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = DetailsPanel.drawSegmentedRow(graphics, x, rowY, width, jointSelector, mouseX, mouseY);

        rowY = panel.drawSectionHeaderWithHelp(graphics, font, x, rowY, width, "Final State", HELP_FINAL_STATE, mouseX, mouseY);
        rowY += InspectorStyle.CONTENT_PADDING / 2;
        rowY = panel.drawSelectRow(graphics, font, x, rowY, width, "Block", null, finalStateSelect, mouseX, mouseY);
        rowY += InspectorStyle.ROW_GAP;
        return rowY;
    }

    /**
     * Render the Tags section: a list of every block-registry tag {@code blockId} currently belongs to (read from
     * {@link Block#builtInRegistryHolder()}, so vanilla / mods / datapacks / project overrides all show up) plus a
     * SearchableSelect picker for adding a new one. Project-owned tags are colored to match the Tag Browser palette and
     * get an [×] remove button; upstream-only tags render in neutral gray with no button (vanilla JSON has no negation
     * primitive, so the inspector can't remove them).
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

        var rowY = DetailsPanel.drawSectionHeader(graphics, font, x, y, width, "Tags");
        rowY += InspectorStyle.CONTENT_PADDING / 2;

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
            graphics.drawString(
                font,
                Component.literal("(none)"),
                x + InspectorStyle.CONTENT_PADDING,
                rowY,
                BLOCK_TAG_EMPTY_NOTE_COLOR,
                false
            );
            rowY += InspectorStyle.LINE_HEIGHT + InspectorStyle.ROW_GAP;
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
            rowY = panel.drawSelectRow(graphics, font, x, rowY, width, "+ Add tag", null, blockTagPicker, mouseX, mouseY);
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

        var labelX = x + InspectorStyle.CONTENT_PADDING;
        var labelY = y + (BLOCK_TAG_ROW_HEIGHT - font.lineHeight + 2) / 2;
        var rightEdge = x + width - InspectorStyle.CONTENT_PADDING;
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
        var set = new TreeSet<ResourceLocation>(Comparator.comparing(ResourceLocation::toString));
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
    static boolean isTagInProject(ResourceLocation registryKey, ResourceLocation tagId) {
        for (var entry : TagCatalogCache.all()) {
            if (entry.registryKey().equals(registryKey) && entry.tagId().equals(tagId)) {
                return entry.inProject();
            }
        }
        return false;
    }

    static boolean isTagInUpstream(ResourceLocation registryKey, ResourceLocation tagId) {
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
    static boolean isTagEquivalentToUpstream(ResourceLocation registryKey, ResourceLocation tagId) {
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
        var already = new HashSet<>(effectiveBlockTags(blockId));
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
        out.sort(Comparator.comparing(item -> item.value().toString()));
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
            return panel.drawSelectRow(graphics, font, x, y, width, prop.getName(), null, select, mouseX, mouseY);
        }

        // Fallback: property had an empty value set (shouldn't happen for well-formed properties) — read-only label.
        return DetailsPanel.drawRow(graphics, font, x, y, prop.getName(), propertyValueName(prop, state));
    }

    /**
     * Label + {@link Checkbox} on one row. Same geometry as {@link DetailsPanel#drawInputRow}: label takes the left
     * {@link InspectorStyle#LABEL_COLUMN_WIDTH} pixels, the checkbox sits at the start of the value column.
     * Baseline-centered against the checkbox's vertical midpoint with the standard +2 descender-padding compensation.
     */
    private static int drawLabeledCheckboxRow(
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
        graphics.drawString(font, Component.literal(label), x + InspectorStyle.CONTENT_PADDING, labelY, InspectorStyle.LABEL_COLOR, false);
        checkbox.render(graphics, x + InspectorStyle.CONTENT_PADDING + InspectorStyle.LABEL_COLUMN_WIDTH, y, mouseX, mouseY);
        return y + Checkbox.SIZE + InspectorStyle.ROW_GAP;
    }

    /**
     * Rebuild the per-property widget cache only when the inspected block changes — same pos and same block type means
     * the property set is identical (BlockState is immutable; replacing-in-place creates a different Block instance
     * only when the registry block changes). Keeping widget instances stable across frames lets each widget track its
     * own click rect / popup state for the click dispatcher.
     */
    private void rebuildGenericBlockPropertyWidgets(BlockState state, BlockPos pos, Block block) {
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
     * Specialized variant of {@link DetailsPanel#drawSelectRow} for the Name field: applies orphan-warning styling
     * (warning label color + "!" icon + orphan-explanation tooltip) when {@code orphaned} is true, otherwise renders
     * identically to a help-icon row with {@link #HELP_NAME}.
     */
    private int drawNameRow(GuiGraphics graphics, Font font, int x, int y, int width, boolean orphaned, int mouseX, int mouseY) {
        var labelText = "Name";
        var labelColor = orphaned ? InspectorStyle.WARN_LABEL_COLOR : InspectorStyle.LABEL_COLOR;
        var labelY = y + (SearchableSelect.HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(labelText), x + InspectorStyle.CONTENT_PADDING, labelY, labelColor, false);

        var iconX = x + InspectorStyle.CONTENT_PADDING + font.width(labelText) + InspectorStyle.HELP_ICON_GAP;
        var iconHovered = orphaned
            ? DetailsPanel.drawWarningIcon(graphics, font, iconX, labelY, mouseX, mouseY)
            : DetailsPanel.drawHelpIcon(graphics, font, iconX, labelY, mouseX, mouseY);
        if (iconHovered) {
            panel.hoveredHelpTooltip = orphaned ? HELP_NAME_ORPHANED : HELP_NAME;
        }

        var ctrlX = x + InspectorStyle.CONTENT_PADDING + InspectorStyle.LABEL_COLUMN_WIDTH;
        var ctrlW = Math.max(0, width - InspectorStyle.LABEL_COLUMN_WIDTH - 2 * InspectorStyle.CONTENT_PADDING);
        nameSelect.render(graphics, ctrlX, y, ctrlW, mouseX, mouseY);
        return y + SearchableSelect.HEIGHT + InspectorStyle.ROW_GAP;
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
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
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
        var snap = JigsawBlockTarget.snapshot(mc.level, block.pos());
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, BlockSelectable bs) {
        // Jigsaw widgets are only live when the block at this position is actually a jigsaw — they're rendered as an
        // extra section at the top of the unified block inspector, so they hit-test before the generic property/tag
        // widgets.
        var state = bs.state();
        if (state != null && state.is(Blocks.JIGSAW)) {
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
        // Forward to per-property widgets. Each widget's own onToggle / onSelect lambda handles the packet send, so we
        // just need to dispatch hit tests here. Selects open their popup on a button-row click; the popup itself is
        // screen-managed and consumes future clicks until dismissed.
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
        // Tag-row [×] hits: send a remove-by-id packet for the row's tag. Server scans the project draft for a direct
        // entry matching this block; no-op if the block is in the tag only via tag-ref or upstream.
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

    /** Per-row hit rect for the block-inspector Tags section's [×] buttons. */
    private record BlockTagRemoveHit(
        int x,
        int y,
        int w,
        int h,
        ResourceLocation registryKey,
        ResourceLocation tagId
    ) {}
}
