package com.blib.engine.ui;

import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.blib.engine.gizmo.BLibGizmoState;
import com.blib.engine.jigsaw.JigsawPieceLibrary;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.jigsaw.JigsawPoolLibrary;
import com.blib.engine.jigsaw.placement.JigsawPlacementFrameState;
import com.blib.engine.jigsaw.placement.JigsawPlacementOptions;
import com.blib.engine.jigsaw.placement.JigsawTemplateScanner;
import com.blib.engine.jigsaw.placement.JigsawTool;
import com.blib.engine.selection.EntitySelectable;
import com.blib.engine.selection.JigsawBlockSelectable;
import com.blib.engine.selection.Selectable;
import com.blib.engine.selection.SelectionManager;
import com.blib.engine.session.EngineMode;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SUpdateJigsawBlockPayload;

/**
 * Right-side universal inspector. Reads {@link SelectionManager#current} each frame and dispatches to a per-type view;
 * with nothing selected, falls back to a tool-state view that surfaces the gizmo target, the active jigsaw piece, and
 * the placement-mode + collision-policy controls.
 * <p>
 * Block-typed selections (jigsaw blocks today) drop into an editable inspector: name / target / pool / final-state
 * text inputs and an aligned/rollable joint selector. Inputs commit on Enter via the {@link TextInput} {@code onCommit}
 * callback; the segmented control commits on click. Each commit ships a {@link C2SUpdateJigsawBlockPayload} carrying
 * the changed field plus the unchanged BE values, so concurrent edits don't lose any fields the user wasn't actively
 * editing.
 */
@ApiStatus.Internal
public final class DetailsPanel implements Panel {

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
     * Position of the block currently shown in the inspector. When this changes we reset all input contents to the
     * new block's BE state, so a selection swap doesn't leak the previous block's pending edits.
     */
    private @Nullable BlockPos lastInspectedPos;

    /**
     * Last-rendered joint type. When the BE's joint differs from this between frames, an external change happened
     * (server roundtrip from our own commit, or another player's edit) and we need to resync the segmented control
     * — without clobbering whatever segment the user might have just clicked.
     */
    private @Nullable JigsawBlockEntity.JointType lastBeJoint;

    /**
     * Selectable captured during render so the per-field commit callbacks (which run from the keyboard event path,
     * not the render path) know which block they're committing against.
     */
    private @Nullable JigsawBlockSelectable currentBlock;

    /**
     * Tooltip computed during render — set when the cursor hovers a help-icon "?" next to a label or section
     * header. Read by {@link #tooltipText} after render via the workspace's tooltip pass. Reset to null at the top
     * of every render so a stale hover from the previous frame doesn't ghost into this frame's tooltip.
     */
    private @Nullable Component hoveredHelpTooltip;

    /**
     * Cache key + result for the orphan check. {@code lastOrphanCheckPos} is part of the key because the scan
     * excludes the inspected jigsaw itself ("does any *other* jigsaw Target this Name?") — switching to a different
     * block with the same Name could legitimately flip the answer, so the cache must invalidate.
     */
    private @Nullable ResourceLocation lastOrphanCheckName;

    private @Nullable BlockPos lastOrphanCheckPos;

    private boolean lastOrphanResult;

    private long lastOrphanCheckMs;

    @Override
    public String title() {
        return "Inspector";
    }

    @Override
    public @Nullable Component tooltipText() {
        return hoveredHelpTooltip;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);
        // Reset each frame; row helpers re-set this if any "?" icon is hovered.
        hoveredHelpTooltip = null;

        var font = Minecraft.getInstance().font;
        var rowY = y;

        var selection = SelectionManager.current();
        var single = selection.single();

        rowY = drawHeaderBar(graphics, font, x, rowY, width, single);

        if (single == null) {
            currentBlock = null;
            renderToolStateView(graphics, font, x, rowY, width);
        } else {
            switch (single.type()) {
                case ENTITY -> {
                    currentBlock = null;
                    renderEntityView(graphics, font, x, rowY, width, (EntitySelectable) single);
                }
                case BLOCK -> {
                    if (single instanceof JigsawBlockSelectable jigsawBlock) {
                        renderBlockView(graphics, font, x, rowY, width, mouseX, mouseY, jigsawBlock);
                    } else {
                        currentBlock = null;
                        renderGenericView(graphics, font, x, rowY, width, single);
                    }
                }
                default -> {
                    currentBlock = null;
                    renderGenericView(graphics, font, x, rowY, width, single);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Only forward to widgets when a jigsaw block is the active selection — text inputs and the joint selector
        // live exclusively in the block view, so clicks outside that view shouldn't ever hit them.
        if (!(SelectionManager.current().single() instanceof JigsawBlockSelectable)) {
            return false;
        }
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
     * Default view when nothing is selected: shows the tool's current state across engine + gizmo + jigsaw placement
     * subsystems. Mirrors the spec's "show something useful when nothing is selected — global tool settings or a hint
     * message" requirement (§3.3).
     */
    private static void renderToolStateView(GuiGraphics graphics, Font font, int x, int y, int width) {
        var rowY = y;
        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Engine");
        rowY += CONTENT_PADDING / 2;
        rowY = drawRow(graphics, font, x, rowY, "Mode", EngineMode.get().isActive() ? "ON" : "OFF");

        var gizmoSnapshot = BLibGizmoState.lastRender();
        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Gizmo");
        rowY += CONTENT_PADDING / 2;
        if (gizmoSnapshot == null) {
            rowY = drawNote(graphics, font, x, rowY, "(no tunable target)");
        } else {
            rowY = drawRow(graphics, font, x, rowY, "Item", gizmoSnapshot.itemId().toString());
            rowY = drawRow(graphics, font, x, rowY, "Mode", gizmoSnapshot.mode().name());
            rowY = drawRow(graphics, font, x, rowY, "Context", gizmoSnapshot.displayContext().name());
        }

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Placement");
        rowY += CONTENT_PADDING / 2;
        var pieceId = JigsawPieceSelection.selectedId();
        if (pieceId == null) {
            rowY = drawNote(graphics, font, x, rowY, "(no piece selected)");
        } else {
            rowY = drawRow(graphics, font, x, rowY, "Piece", pieceId.getPath());
            rowY = drawRow(graphics, font, x, rowY, "Mode", JigsawTool.activeMode().displayName());
            rowY = drawRow(graphics, font, x, rowY, "Rotation", JigsawPieceSelection.rotation().name());
            rowY = drawRow(graphics, font, x, rowY, "Mirror", JigsawPieceSelection.mirror().name());
            rowY = drawRow(graphics, font, x, rowY, "Policy", JigsawPlacementOptions.collisionPolicy().name());
            rowY = drawRow(graphics, font, x, rowY, "Grid", String.valueOf(JigsawPlacementOptions.gridSize()));
            if (JigsawPlacementFrameState.placement() != null) {
                rowY = drawRow(graphics, font, x, rowY, "Collisions", String.valueOf(JigsawPlacementFrameState.collisionCount()));
            }
        }
    }

    /**
     * Entity-specific view. Pulls the live entity off the {@link EntitySelectable} and surfaces a few useful fields
     * (type, position, health for living entities). The selectable handles staleness — if the entity has unloaded since
     * selection, {@code entity()} returns {@code null} and we degrade gracefully.
     */
    private static void renderEntityView(GuiGraphics graphics, Font font, int x, int y, int width, EntitySelectable selectable) {
        var rowY = y;
        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Entity");
        rowY += CONTENT_PADDING / 2;

        var entity = selectable.entity();
        if (entity == null) {
            rowY = drawNote(graphics, font, x, rowY, "(unloaded)");
            return;
        }

        rowY = drawRow(graphics, font, x, rowY, "Type", entity.getType().getDescriptionId());
        rowY = drawRow(graphics, font, x, rowY, "UUID", entity.getStringUUID().substring(0, 8));
        var pos = entity.position();
        rowY = drawRow(graphics, font, x, rowY, "X", String.format("%.2f", pos.x));
        rowY = drawRow(graphics, font, x, rowY, "Y", String.format("%.2f", pos.y));
        rowY = drawRow(graphics, font, x, rowY, "Z", String.format("%.2f", pos.z));
        rowY = drawRow(graphics, font, x, rowY, "Health", String.format("%.1f / %.1f", entity.getHealth(), entity.getMaxHealth()));
    }

    /**
     * Editable inspector for a placed jigsaw block. Every field reads from a fresh BE snapshot each frame and pushes
     * the value into the corresponding widget — SearchableSelects overwrite their currentValue (no "focused" concept
     * to preserve), the joint segmented control resyncs only on external change. Selection swaps re-key the orphan
     * cache and reload all widgets to the new block's BE state. The Name row gets a special-case warning treatment
     * when its Name isn't Targeted by any other jigsaw in templates or the loaded world.
     */
    private void renderBlockView(GuiGraphics graphics, Font font, int x, int y, int width, int mouseX, int mouseY, JigsawBlockSelectable selectable) {
        currentBlock = selectable;
        var snap = selectable.snapshot();
        if (snap == null) {
            var rowY = drawSectionHeader(graphics, font, x, y, width, "Jigsaw Block");
            rowY += CONTENT_PADDING / 2;
            drawNote(graphics, font, x, rowY, "(unloaded)");
            return;
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
    }

    /**
     * Fallback for selectable types that don't have a dedicated view yet. Shows the selectable's display name and its
     * world-bounds center so the user at least sees that something is selected and where it is.
     */
    private static void renderGenericView(GuiGraphics graphics, Font font, int x, int y, int width, Selectable selectable) {
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
     * Section-header variant that renders a "?" help icon to the right of the label and surfaces {@code helpText}
     * via {@link #tooltipText} when the cursor hovers it. Instance method (rather than static) so it can write the
     * panel's hovered-tooltip field directly.
     */
    private int drawSectionHeaderWithHelp(GuiGraphics graphics, Font font, int x, int y, int width, String label, Component helpText, int mouseX, int mouseY) {
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
     * same +2 descender-padding compensation used elsewhere in the workspace. When {@code helpText} is non-null, a
     * "?" icon is rendered immediately after the label and contributes to {@link #hoveredHelpTooltip} on hover.
     */
    private int drawInputRow(GuiGraphics graphics, Font font, int x, int y, int width, String label, @Nullable Component helpText, TextInput input, int mouseX, int mouseY) {
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
     * Render a small "?" glyph anchored at {@code (iconX, iconY)} with a hover hit-rect of {@link #HELP_ICON_SIZE}
     * px on each side. Returns {@code true} when the cursor is over the icon — caller writes the tooltip field.
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

    private static boolean drawIconGlyph(GuiGraphics graphics, Font font, int iconX, int iconY, String glyph, int color, int hoverColor, int mouseX, int mouseY) {
        var hovered = mouseX >= iconX
            && mouseX < iconX + HELP_ICON_SIZE
            && mouseY >= iconY
            && mouseY < iconY + HELP_ICON_SIZE;
        var c = hovered ? hoverColor : color;
        graphics.drawString(font, Component.literal(glyph), iconX, iconY, c, false);
        return hovered;
    }

    /**
     * Specialized variant of {@link #drawSelectRow} for the Name field: applies orphan-warning styling (warning
     * label color + "!" icon + orphan-explanation tooltip) when {@code orphaned} is true, otherwise renders
     * identically to a help-icon row with {@link #HELP_NAME}.
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
     * The check excludes the inspected jigsaw at {@code thisPos} (so a self-loop where this jigsaw's Target equals
     * its own Name doesn't mask the orphan state). Non-loaded chunks contribute nothing — that's an inherent
     * limitation of a client-side check, but acceptable: an out-of-sight jigsaw can't be inspected anyway.
     */
    private boolean isNameOrphaned(ResourceLocation thisName, BlockPos thisPos) {
        var now = System.currentTimeMillis();
        if (thisName.equals(lastOrphanCheckName)
            && thisPos.equals(lastOrphanCheckPos)
            && now - lastOrphanCheckMs < ORPHAN_CHECK_INTERVAL_MS) {
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
                    if (be instanceof JigsawBlockEntity je
                        && !be.getBlockPos().equals(thisPos)
                        && je.getTarget().equals(thisName)) {
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
     * Label + {@link SearchableSelect} on one row. Same geometry as {@link #drawInputRow} so the inspector keeps a
     * consistent grid; only the right-hand widget differs. When {@code helpText} is non-null, a "?" icon is rendered
     * after the label and contributes to {@link #hoveredHelpTooltip} on hover.
     */
    private int drawSelectRow(GuiGraphics graphics, Font font, int x, int y, int width, String label, @Nullable Component helpText, SearchableSelect<?> select, int mouseX, int mouseY) {
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
        return BuiltInRegistries.BLOCK.keySet().stream()
            .sorted(Comparator.comparing(ResourceLocation::getNamespace).thenComparing(ResourceLocation::getPath))
            .map(id -> new SearchableSelect.Item<>(id, id.toString()))
            .toList();
    }

    /**
     * Build the template-pool list for the pool picker. Backed by {@link JigsawPoolLibrary#listPoolIds} which
     * already enumerates from the integrated server's {@code TEMPLATE_POOL} registry (single-player only); empty
     * outside that context. Items are pre-sorted by the library; we just wrap them.
     */
    private static List<SearchableSelect.Item<ResourceLocation>> buildPoolItems() {
        return JigsawPoolLibrary.listPoolIds().stream()
            .map(id -> new SearchableSelect.Item<>(id, id.toString()))
            .toList();
    }

    /**
     * Build the Name list — every distinct jigsaw {@code target} appearing across all loaded templates. These are
     * the connection points "out there" that a jigsaw could set its Name to in order to be docked to. Unscoped (vs.
     * Target which scopes to a Pool) because Name describes the incoming side: any other jigsaw anywhere could
     * Target this Name, and the dropdown surfaces the universe of Targets that exist to inspire the user's choice.
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
     * appearing inside templates referenced by that pool. Picking a Target only makes sense if at least one piece
     * the Pool can spawn has a jigsaw with that Name — anything else is a connection that'll never fire.
     * <p>
     * Returns empty when no Pool is set yet, or when the Pool / its templates are unloaded. The widget shows
     * "(no matches)" in that case; the user can still set a Pool first to populate the dropdown.
     * <p>
     * Recomputed every popup-open via the widget's {@code Supplier}, so changing Pool then opening Target
     * naturally reflects the new scope.
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
     * {@code minecraft:water_cauldron}) yield {@link ItemStack#EMPTY}, which the popup renderer skips silently —
     * the row still shows the label and remains selectable.
     */
    private static ItemStack iconForBlock(ResourceLocation blockId) {
        var block = BuiltInRegistries.BLOCK.get(blockId);
        var item = block.asItem();
        return item == net.minecraft.world.item.Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    /**
     * Extract just the block id from a finalState string. Vanilla allows either {@code "minecraft:stone"} (default
     * state) or {@code "minecraft:stone[half=top]"} (with properties); the picker only displays / selects the block,
     * so anything inside square brackets is ignored. Returns {@code null} if the string is empty or unparseable.
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
     * user commits a different one (they didn't ask to commit it). Parse failures revert just the failing input to
     * its BE-state value; no packet is sent in that case.
     */
    private void commitField(BlockField field, @Nullable String rawValue) {
        var block = currentBlock;
        if (block == null) {
            return;
        }
        var snap = block.snapshot();
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
}
