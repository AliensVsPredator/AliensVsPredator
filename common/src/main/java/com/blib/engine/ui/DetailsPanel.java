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

import com.blib.engine.gizmo.BLibGizmoState;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.jigsaw.placement.JigsawPlacementFrameState;
import com.blib.engine.jigsaw.placement.JigsawPlacementOptions;
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

    private final TextInput nameInput = new TextInput("namespace:name", value -> commitField(BlockField.NAME, value));

    private final TextInput targetInput = new TextInput("namespace:target", value -> commitField(BlockField.TARGET, value));

    private final TextInput poolInput = new TextInput("namespace:pool/id", value -> commitField(BlockField.POOL, value));

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

    @Override
    public String title() {
        return "Inspector";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

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
        if (nameInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (targetInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (poolInput.mouseClicked(mouseX, mouseY, button)) {
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
     * Editable inspector for a placed jigsaw block. Each field reads from a fresh BE snapshot, with text inputs
     * resynced from BE state when not focused (so an external edit shows up while preserving in-flight typing).
     * Selection swaps reset all inputs unconditionally so we never leak edits across blocks.
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
        var poolStr = snap.pool().location().toString();
        var finalStateBlockId = extractBlockId(snap.finalState());
        if (!pos.equals(lastInspectedPos)) {
            // Selection swap — reset everything to the new block's BE state. This deliberately overrides focused
            // inputs too: the previous block's pending edits don't carry over to a different block.
            nameInput.setContent(snap.name().toString());
            targetInput.setContent(snap.target().toString());
            poolInput.setContent(poolStr);
            finalStateSelect.setCurrentValue(finalStateBlockId);
            jointSelector.setSelectedIndex(snap.joint().ordinal());
            lastInspectedPos = pos;
            lastBeJoint = snap.joint();
        } else {
            // Same block — sync only fields the user isn't actively editing. Keeps external edits visible (other
            // player rewrites the pool, or our own commit roundtrip lands) without clobbering in-flight typing.
            if (snap.joint() != lastBeJoint) {
                jointSelector.setSelectedIndex(snap.joint().ordinal());
                lastBeJoint = snap.joint();
            }
            if (!nameInput.isFocused()) {
                nameInput.setContent(snap.name().toString());
            }
            if (!targetInput.isFocused()) {
                targetInput.setContent(snap.target().toString());
            }
            if (!poolInput.isFocused()) {
                poolInput.setContent(poolStr);
            }
            // SearchableSelect has no concept of "focused" — the popup, if open, is for picking a new value, so
            // overwriting currentValue from BE state mid-popup is harmless (the popup's own selection wins on click).
            finalStateSelect.setCurrentValue(finalStateBlockId);
        }

        var rowY = drawSectionHeader(graphics, font, x, y, width, "Identity");
        rowY += CONTENT_PADDING / 2;
        rowY = drawInputRow(graphics, font, x, rowY, width, "Name", nameInput, mouseX, mouseY);
        rowY = drawInputRow(graphics, font, x, rowY, width, "Target", targetInput, mouseX, mouseY);
        rowY = drawInputRow(graphics, font, x, rowY, width, "Pool", poolInput, mouseX, mouseY);

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Joint");
        rowY += CONTENT_PADDING / 2;
        rowY = drawSegmentedRow(graphics, x, rowY, width, jointSelector, mouseX, mouseY);

        rowY = drawSectionHeader(graphics, font, x, rowY, width, "Final State");
        rowY += CONTENT_PADDING / 2;
        rowY = drawSelectRow(graphics, font, x, rowY, width, "Block", finalStateSelect, mouseX, mouseY);
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
     * same +2 descender-padding compensation used elsewhere in the workspace.
     */
    private static int drawInputRow(GuiGraphics graphics, Font font, int x, int y, int width, String label, TextInput input, int mouseX, int mouseY) {
        var labelY = y + (TextInput.HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), x + CONTENT_PADDING, labelY, LABEL_COLOR, false);
        var inputX = x + CONTENT_PADDING + LABEL_COLUMN_WIDTH;
        var inputW = Math.max(0, width - LABEL_COLUMN_WIDTH - 2 * CONTENT_PADDING);
        input.render(graphics, inputX, y, inputW, mouseX, mouseY);
        return y + TextInput.HEIGHT + ROW_GAP;
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
     * consistent grid; only the right-hand widget differs.
     */
    private static int drawSelectRow(GuiGraphics graphics, Font font, int x, int y, int width, String label, SearchableSelect<?> select, int mouseX, int mouseY) {
        var labelY = y + (SearchableSelect.HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), x + CONTENT_PADDING, labelY, LABEL_COLOR, false);
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
                var parsed = parseResourceLocation(rawValue);
                if (parsed == null) {
                    nameInput.setContent(name.toString());
                    return;
                }
                name = parsed;
            }
            case TARGET -> {
                var parsed = parseResourceLocation(rawValue);
                if (parsed == null) {
                    targetInput.setContent(target.toString());
                    return;
                }
                target = parsed;
            }
            case POOL -> {
                var parsed = parseResourceLocation(rawValue);
                if (parsed == null) {
                    poolInput.setContent(pool.toString());
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
