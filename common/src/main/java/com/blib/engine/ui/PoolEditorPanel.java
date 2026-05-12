package com.blib.engine.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blib.engine.jigsaw.JigsawPieceLibrary;
import com.blib.engine.jigsaw.JigsawPieceThumbnailCache;
import com.blib.engine.jigsaw.JigsawPoolLibrary;
import com.blib.engine.jigsaw.ProjectDraftCache;
import com.blib.engine.session.ProjectSession;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SAddPoolElementPayload;
import com.blib.mod.common.network.packet.C2SRemovePoolElementPayload;
import com.blib.mod.common.network.packet.C2SRequestPoolDraftPayload;
import com.blib.mod.common.network.packet.C2SUpdatePoolElementPayload;

/**
 * Browser + inline editor for the contents of a single template pool. Header has a {@link SearchableSelect} to pick
 * which pool to inspect; the body is a scrollable vertical list of element rows — thumbnail, template id, an editable
 * weight field, and an editable projection segmented control. Thumbnails come from the same
 * {@link JigsawPieceThumbnailCache} that {@link PiecePalettePanel} uses.
 * <p>
 * Element data is read from the active project's {@link ProjectDraftCache} — server-pushed authoritative state for the
 * project's pool JSON. When the project hasn't yet authored an override for the selected pool, the panel falls back to
 * {@link JigsawPoolLibrary#elementsInPool} so users see the in-game state as a starting point.
 * <p>
 * Edits ({@link C2SUpdatePoolElementPayload} / {@link C2SAddPoolElementPayload} / {@link C2SRemovePoolElementPayload})
 * write straight to the active project's datapack JSON on disk — the live {@code Registries#TEMPLATE_POOL} object is
 * left untouched. Live structure generation only reflects the edits after the user clicks the header "Reload" button
 * (the toolbar's Reload Project button kicks off the server reload). All edit packets are no-ops without an active
 * {@link ProjectSession}.
 * <p>
 * Nested children of a {@code ListPoolElement} display alongside top-level elements but render the weight and
 * projection as plain (non-editable) text — they don't have a stable {@code rawIndex} for the server to address.
 */
@ApiStatus.Internal
public final class PoolEditorPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int HEADER_BG_COLOR = 0xFF1F1F26;

    private static final int HEADER_LABEL_COLOR = 0xFF7C8088;

    private static final int ROW_TEXT_COLOR = 0xFFD0D0D0;

    private static final int ROW_META_COLOR = 0xFF808088;

    private static final int ROW_HOVER_BG = 0xFF1F1F26;

    private static final int EMPTY_NOTE_COLOR = 0xFF606068;

    private static final int CONTENT_PADDING = 5;

    private static final int HEADER_BAR_HEIGHT = SearchableSelect.HEIGHT + 4;

    /** Single line per row: thumbnail + id text + editable weight + projection. Sized to fit SegmentedControl. */
    private static final int ROW_HEIGHT = 24;

    private static final int THUMB_SIZE = 18;

    private static final int THUMB_PAD = 2;

    private static final int TEXT_LEFT_OFFSET = THUMB_PAD + THUMB_SIZE + 4;

    private static final int RIGHT_PAD = 4;

    /** Width of the inline weight TextInput per row — room for ~4-digit weights (vanilla rarely exceeds two). */
    private static final int WEIGHT_INPUT_WIDTH = 28;

    /** Width of the projection SegmentedControl per row — fits "Rigid" / "Match" labels. */
    private static final int PROJECTION_WIDTH = 78;

    /** Gap between widgets within the right-side widget block. */
    private static final int WIDGET_GAP = 4;

    private static final List<String> PROJECTION_LABELS = List.of("Rigid", "Match");

    /** Per-row "×" remove button width. */
    private static final int REMOVE_BUTTON_WIDTH = 12;

    private static final int REMOVE_ICON_COLOR = 0xFF7C8088;

    private static final int REMOVE_ICON_HOVER_COLOR = 0xFFFF6868;

    /** Footer area below the scroll body, hosting the Add-piece select. Same height as the header for symmetry. */
    private static final int FOOTER_HEIGHT = SearchableSelect.HEIGHT + 4;

    private final SearchableSelect<ResourceLocation> poolSelect = new SearchableSelect<>(
        PoolEditorPanel::buildPoolItems,
        rl -> rl == null ? "(pick a pool)" : rl.toString(),
        null,
        rl -> {
            // Selection callback fires on click; the next render picks up the change via poolSelect.currentValue().
        }
    );

    private final ScrollContainer scroll = new ScrollContainer();

    /** Cached element list — recomputed when the selected pool id changes or after a commit invalidates it. */
    private @Nullable ResourceLocation lastShownPool;

    private List<JigsawPoolLibrary.PoolElementInfo> cachedElements = List.of();

    /**
     * Per-element-row widgets keyed by {@code rawIndex}. Lazily populated as rows are first rendered; cleared when the
     * selected pool changes (since rawIndex space is per-pool). Non-editable rows ({@code rawIndex == -1}) skip widget
     * creation entirely and render plain text instead.
     */
    private final Map<Integer, TextInput> weightInputs = new HashMap<>();

    private final Map<Integer, SegmentedControl> projectionSelects = new HashMap<>();

    /**
     * Footer "Add piece" picker — selecting a template fires {@link C2SAddPoolElementPayload} and resets the select's
     * value to {@code null} so the placeholder shows again for the next add. Items come from
     * {@link JigsawPieceLibrary#listIds()} (every loaded template).
     */
    private final SearchableSelect<ResourceLocation> addPieceSelect = new SearchableSelect<>(
        PoolEditorPanel::buildAddPieceItems,
        rl -> rl == null ? "(add piece…)" : rl.toString(),
        null,
        rl -> {
            if (rl != null) {
                commitAdd(rl);
            }
        }
    );

    /** Cached panel rect from the most recent render — needed by mouseClicked for hit-testing the per-row × buttons. */
    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    @Override
    public String title() {
        return "Pool Editor";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        // Cache panel rect for mouseClicked's row hit-testing (× buttons live at panel-relative positions; without
        // the cache the click handler would have to recompute layout from scratch with no anchor).
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Pool editor needs a server (for save / fetch) and an active project (pools are project-scoped).
        if (Minecraft.getInstance().level == null) {
            PanelPlaceholder.drawCentered(graphics, x, y, width, height, PanelPlaceholder.NEEDS_WORLD);
            return;
        }
        if (ProjectSession.activeProject() == null) {
            PanelPlaceholder.drawCentered(graphics, x, y, width, height, PanelPlaceholder.NEEDS_PROJECT);
            return;
        }

        // Consume any pending pool-open request from the content browser (or other cross-panel callers). One-shot:
        // applying the value to the SearchableSelect drives the existing lastShownPool drift-check on the next render
        // pass, so the body re-loads the pool's contents naturally without a parallel reload path.
        var requestedPool = com.blib.engine.jigsaw.JigsawPoolSelection.requested();
        if (requestedPool != null && !requestedPool.equals(poolSelect.currentValue())) {
            poolSelect.setCurrentValue(requestedPool);
            com.blib.engine.jigsaw.JigsawPoolSelection.clear();
        }

        // Reset the per-frame thumbnail render budget once at the top of our render — same idiom PiecePalettePanel
        // uses, so we don't compete unfairly if both panels render in the same frame.
        JigsawPieceThumbnailCache.beginFrame();

        renderHeader(graphics, x, y, width, mouseX, mouseY);

        var bodyY = y + HEADER_BAR_HEIGHT;
        var bodyHeight = Math.max(0, height - HEADER_BAR_HEIGHT - FOOTER_HEIGHT);
        renderBody(graphics, x, bodyY, width, bodyHeight, mouseX, mouseY);

        var footerY = y + height - FOOTER_HEIGHT;
        renderFooter(graphics, x, footerY, width, mouseX, mouseY);
    }

    private void renderHeader(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        graphics.fill(x, y, x + width, y + HEADER_BAR_HEIGHT, HEADER_BG_COLOR);

        var font = EngineFont.get();
        var labelText = "Pool";
        var labelWidth = font.width(labelText);
        var selectY = y + (HEADER_BAR_HEIGHT - SearchableSelect.HEIGHT) / 2;
        var labelY = selectY + (SearchableSelect.HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(labelText), x + CONTENT_PADDING, labelY, HEADER_LABEL_COLOR, false);

        var selectX = x + CONTENT_PADDING + labelWidth + 6;
        var selectW = Math.max(0, width - (selectX - x) - CONTENT_PADDING);
        poolSelect.render(graphics, selectX, selectY, selectW, mouseX, mouseY);
    }

    private void renderFooter(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        graphics.fill(x, y, x + width, y + FOOTER_HEIGHT, HEADER_BG_COLOR);

        var poolSelected = poolSelect.currentValue() != null;
        if (!poolSelected) {
            // No pool to add to — render a muted note where the select would go.
            var font = EngineFont.get();
            var note = "(pick a pool to enable adding)";
            var textY = y + (FOOTER_HEIGHT - font.lineHeight + 2) / 2;
            graphics.drawString(font, Component.literal(note), x + CONTENT_PADDING, textY, EMPTY_NOTE_COLOR, false);
            return;
        }

        var selectY = y + (FOOTER_HEIGHT - SearchableSelect.HEIGHT) / 2;
        var selectX = x + CONTENT_PADDING;
        var selectW = Math.max(0, width - 2 * CONTENT_PADDING);
        addPieceSelect.render(graphics, selectX, selectY, selectW, mouseX, mouseY);
    }

    private void renderBody(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        if (ProjectSession.activeProject() == null) {
            drawCenteredNote(graphics, x, y, width, height, "(no project open — File → Open Project)");
            return;
        }
        var poolId = poolSelect.currentValue();

        if (poolId == null) {
            drawCenteredNote(graphics, x, y, width, height, "(pick a pool)");
            return;
        }

        // Pool selection changed → reset per-pool widget state and request the project's draft for this pool from
        // the server. Until the S2CPoolDraftPayload reply lands, the cache may be empty for this pool and we fall
        // back to JigsawPoolLibrary's registry read for an immediate display.
        if (!poolId.equals(lastShownPool)) {
            lastShownPool = poolId;
            scroll.reset();
            weightInputs.clear();
            projectionSelects.clear();
            BLib.MOD.networking().sendToServer(new C2SRequestPoolDraftPayload(ProjectSession.activeProjectName(), poolId));
        }

        // Prefer the project's authoritative state (fed by S2CPoolDraftPayload); fall back to the registry's view
        // for pools the project hasn't yet touched. The registry's view is what's currently in-game; the draft
        // cache reflects on-disk state, which may have unreloaded edits the registry doesn't see.
        var draft = ProjectDraftCache.get(poolId);
        if (draft != null) {
            var converted = new java.util.ArrayList<JigsawPoolLibrary.PoolElementInfo>(draft.size());
            for (var d : draft) {
                converted.add(ProjectDraftCache.toPoolElementInfo(d));
            }
            cachedElements = converted;
        } else {
            cachedElements = JigsawPoolLibrary.elementsInPool(poolId);
        }

        if (cachedElements.isEmpty()) {
            drawCenteredNote(graphics, x, y, width, height, "(no template-bearing elements)");
            return;
        }

        var contentHeight = cachedElements.size() * ROW_HEIGHT;
        scroll.layout(height, contentHeight);

        applyRawScissor(graphics, x, y, width, height);
        try {
            var scrollY = (int) scroll.scrollY();
            var firstVisible = Math.max(0, scrollY / ROW_HEIGHT);
            var lastVisible = Math.min(cachedElements.size() - 1, (scrollY + height) / ROW_HEIGHT);
            for (var i = firstVisible; i <= lastVisible; i++) {
                var rowY = y + i * ROW_HEIGHT - scrollY;
                renderRow(graphics, cachedElements.get(i), x, rowY, width, mouseX, mouseY);
            }
        } finally {
            graphics.flush();
            RenderSystem.disableScissor();
        }

        scroll.renderScrollbar(graphics, x, y, width, height, mouseX, mouseY);
    }

    /**
     * Compute the x-coordinates of the right-side widget block for a row. Shared between render and click hit-testing
     * so the × button rect matches what's drawn. {@code rowWidth} is the panel width.
     */
    private record RowLayout(
        int weightX,
        int projectionX,
        int removeX,
        int leftEdgeOfBlock
    ) {}

    private static RowLayout rowLayout(int rowX, int rowWidth) {
        var rightEdge = rowX + rowWidth - ScrollContainer.SCROLLBAR_GUTTER - RIGHT_PAD;
        var removeX = rightEdge - REMOVE_BUTTON_WIDTH;
        var projectionX = removeX - WIDGET_GAP - PROJECTION_WIDTH;
        var weightX = projectionX - WIDGET_GAP - WEIGHT_INPUT_WIDTH;
        return new RowLayout(weightX, projectionX, removeX, weightX);
    }

    private void renderRow(
        GuiGraphics graphics,
        JigsawPoolLibrary.PoolElementInfo element,
        int x,
        int y,
        int width,
        int mouseX,
        int mouseY
    ) {
        var hovered = mouseY >= y
            && mouseY < y + ROW_HEIGHT
            && mouseX >= x
            && mouseX < x + width - ScrollContainer.SCROLLBAR_GUTTER;
        if (hovered) {
            graphics.fill(x, y, x + width - ScrollContainer.SCROLLBAR_GUTTER, y + ROW_HEIGHT, ROW_HOVER_BG);
        }

        // Thumbnail. JigsawPieceLibrary.get returns null if the template hasn't loaded yet — fall back to a tiny
        // placeholder rect so the row layout stays stable.
        var template = JigsawPieceLibrary.get(element.templateId());
        if (template != null) {
            JigsawPieceThumbnailCache.draw(
                graphics,
                element.templateId(),
                template,
                x + THUMB_PAD,
                y + (ROW_HEIGHT - THUMB_SIZE) / 2,
                THUMB_SIZE,
                THUMB_SIZE
            );
        } else {
            graphics.fill(
                x + THUMB_PAD,
                y + (ROW_HEIGHT - THUMB_SIZE) / 2,
                x + THUMB_PAD + THUMB_SIZE,
                y + (ROW_HEIGHT - THUMB_SIZE) / 2 + THUMB_SIZE,
                0xFF1A1A22
            );
        }

        var font = EngineFont.get();
        var layout = rowLayout(x, width);

        if (element.editable()) {
            var weightInput = weightInputs.computeIfAbsent(
                element.rawIndex(),
                idx -> new TextInput(
                    "",
                    value -> commitWeight(idx, value)
                )
            );
            // Sync weight from BE state when the user isn't actively typing — keeps display correct after server-
            // side roundtrips and external edits without clobbering in-flight typing.
            if (!weightInput.isFocused()) {
                weightInput.setContent(String.valueOf(element.weight()));
            }
            var weightY = y + (ROW_HEIGHT - TextInput.HEIGHT) / 2;
            weightInput.render(graphics, layout.weightX(), weightY, WEIGHT_INPUT_WIDTH, mouseX, mouseY);

            var projectionSelect = projectionSelects.computeIfAbsent(element.rawIndex(), idx -> new SegmentedControl(PROJECTION_LABELS, 0));
            // Always sync from current state — segmented controls have no "focused" concept; user clicks commit
            // immediately via the click forwarder below, so this never races with a pending edit.
            projectionSelect.setSelectedIndex(element.projection().ordinal());
            var projectionY = y + (ROW_HEIGHT - SegmentedControl.HEIGHT) / 2;
            projectionSelect.render(graphics, layout.projectionX(), projectionY, PROJECTION_WIDTH, mouseX, mouseY);

            // × remove button. Hover detection treats the entire button rect as hot, not just the glyph.
            var removeHovered = mouseX >= layout.removeX()
                && mouseX < layout.removeX() + REMOVE_BUTTON_WIDTH
                && mouseY >= y
                && mouseY < y + ROW_HEIGHT;
            var removeColor = removeHovered ? REMOVE_ICON_HOVER_COLOR : REMOVE_ICON_COLOR;
            var removeText = "×";
            var removeTextWidth = font.width(removeText);
            var removeTextX = layout.removeX() + (REMOVE_BUTTON_WIDTH - removeTextWidth) / 2;
            var removeTextY = y + (ROW_HEIGHT - font.lineHeight + 2) / 2;
            graphics.drawString(font, Component.literal(removeText), removeTextX, removeTextY, removeColor, false);
        } else {
            // Non-editable row (ListPoolElement child) — render the same info as plain text in muted color, no
            // remove button (these aren't directly addressable).
            var meta = String.valueOf(element.weight()) + "  " + element.projection().name();
            var metaWidth = font.width(meta);
            var metaX = x + width - ScrollContainer.SCROLLBAR_GUTTER - RIGHT_PAD - metaWidth;
            // +2 compensates for MC font's descender padding; matches the workspace's text-centering convention.
            var textY = y + (ROW_HEIGHT - font.lineHeight + 2) / 2;
            graphics.drawString(font, Component.literal(meta), metaX, textY, ROW_META_COLOR, false);
        }

        // Left-side: template id, truncated to whatever room remains before the widget block.
        var idText = element.templateId().toString();
        var idMaxWidth = Math.max(0, layout.leftEdgeOfBlock() - (x + TEXT_LEFT_OFFSET) - 4);
        var truncated = font.plainSubstrByWidth(idText, idMaxWidth);
        var idTextY = y + (ROW_HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(truncated), x + TEXT_LEFT_OFFSET, idTextY, ROW_TEXT_COLOR, false);
    }

    /**
     * Parse the typed text as a positive int and ship it to the server. Invalid input reverts the input's content to
     * whatever the cached element list says (the server is authoritative; we don't trust whatever the user typed).
     * Empty / non-numeric input drops silently — same UX as the inspector's ResourceLocation-typed inputs.
     */
    private void commitWeight(int rawIndex, String text) {
        if (lastShownPool == null) {
            return;
        }
        int parsed;
        try {
            parsed = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            // Revert input to the cached weight (re-sync next frame would do the same, but be explicit here for the
            // case where the cache is fresh and the parse genuinely failed).
            var input = weightInputs.get(rawIndex);
            if (input != null) {
                input.setContent(String.valueOf(currentWeightFor(rawIndex)));
            }
            return;
        }
        var newWeight = Math.max(1, parsed);
        sendUpdate(rawIndex, newWeight, currentProjectionOrdinalFor(rawIndex));
    }

    /**
     * Send a projection update for a row. Called from {@link #mouseClicked} when a SegmentedControl click changes the
     * selected index relative to the cached element state.
     */
    private void commitProjection(int rawIndex, int newProjectionOrdinal) {
        sendUpdate(rawIndex, currentWeightFor(rawIndex), newProjectionOrdinal);
    }

    private void sendUpdate(int rawIndex, int newWeight, int newProjectionOrdinal) {
        if (lastShownPool == null || ProjectSession.activeProject() == null) {
            return;
        }
        BLib.MOD.networking()
            .sendToServer(
                new C2SUpdatePoolElementPayload(
                    ProjectSession.activeProjectName(),
                    lastShownPool,
                    rawIndex,
                    newWeight,
                    newProjectionOrdinal
                )
            );
    }

    /**
     * Add commit — fires when the footer "Add piece" select picks a template. Sends the packet and resets the select so
     * the placeholder shows again. The new row appears in the list automatically once the server has processed the
     * packet (renderBody re-fetches every frame).
     */
    private void commitAdd(ResourceLocation templateId) {
        var poolId = poolSelect.currentValue();
        if (poolId == null || ProjectSession.activeProject() == null) {
            return;
        }
        BLib.MOD.networking()
            .sendToServer(new C2SAddPoolElementPayload(ProjectSession.activeProjectName(), poolId, templateId, 1, 0 /*
                                                                                                                     * RIGID
                                                                                                                     */));
        addPieceSelect.setCurrentValue(null);
    }

    /**
     * Remove commit — fires from the per-row × click. Sends the packet then nukes the per-row widget caches: the
     * removal renumbers all rawIndex values >= removed index, so the rawIndex→widget mapping is no longer correct.
     * Widgets get re-instantiated lazily on the next render.
     */
    private void commitRemove(int rawIndex) {
        var poolId = poolSelect.currentValue();
        if (poolId == null || ProjectSession.activeProject() == null) {
            return;
        }
        BLib.MOD.networking()
            .sendToServer(new C2SRemovePoolElementPayload(ProjectSession.activeProjectName(), poolId, rawIndex));
        weightInputs.clear();
        projectionSelects.clear();
    }

    /** Look up a row's current weight from the cached element list — fallback to 1 if the row vanished. */
    private int currentWeightFor(int rawIndex) {
        for (var element : cachedElements) {
            if (element.rawIndex() == rawIndex) {
                return element.weight();
            }
        }
        return 1;
    }

    /** Look up a row's current projection ordinal from the cached element list — fallback to 0 (RIGID). */
    private int currentProjectionOrdinalFor(int rawIndex) {
        for (var element : cachedElements) {
            if (element.rawIndex() == rawIndex) {
                return element.projection().ordinal();
            }
        }
        return StructureTemplatePool.Projection.RIGID.ordinal();
    }

    private static void drawCenteredNote(GuiGraphics graphics, int x, int y, int width, int height, String text) {
        var font = EngineFont.get();
        var textWidth = font.width(text);
        var noteX = x + (width - textWidth) / 2;
        var noteY = y + (height - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(text), noteX, noteY, EMPTY_NOTE_COLOR, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (poolSelect.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (addPieceSelect.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (scroll.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        // Forward to per-row widgets. Iterating both maps each click is fine — both have at most as many entries
        // as visible rows + a few off-screen leftovers from past scroll positions.
        for (var entry : weightInputs.entrySet()) {
            if (entry.getValue().mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        for (var entry : projectionSelects.entrySet()) {
            var rawIndex = entry.getKey();
            var control = entry.getValue();
            var indexBefore = control.selectedIndex();
            if (control.mouseClicked(mouseX, mouseY, button)) {
                if (control.selectedIndex() != indexBefore) {
                    commitProjection(rawIndex, control.selectedIndex());
                }
                return true;
            }
        }
        // Per-row × remove buttons. Hit-test against the cached panel rect + scroll offset; only editable rows
        // (rawIndex >= 0) actually have a button drawn.
        var removeIndex = findRemoveClickedRawIndex(mouseX, mouseY, button);
        if (removeIndex != null) {
            commitRemove(removeIndex);
            return true;
        }
        return false;
    }

    /**
     * Walk the visible rows and check if the click landed on any of their × buttons. Returns the matched element's
     * rawIndex, or null. Computes row positions the same way render does (panel rect + scroll offset + ROW_HEIGHT
     * stride) so the click rect always matches what's drawn.
     */
    private @Nullable Integer findRemoveClickedRawIndex(double mouseX, double mouseY, int button) {
        if (button != 0 || cachedElements.isEmpty()) {
            return null;
        }
        var bodyY = rectY + HEADER_BAR_HEIGHT;
        var bodyHeight = Math.max(0, rectHeight - HEADER_BAR_HEIGHT - FOOTER_HEIGHT);
        if (mouseY < bodyY || mouseY >= bodyY + bodyHeight) {
            return null;
        }
        var layout = rowLayout(rectX, rectWidth);
        if (mouseX < layout.removeX() || mouseX >= layout.removeX() + REMOVE_BUTTON_WIDTH) {
            return null;
        }
        var scrollY = (int) scroll.scrollY();
        var rowAtClick = (int) ((mouseY - bodyY + scrollY) / ROW_HEIGHT);
        if (rowAtClick < 0 || rowAtClick >= cachedElements.size()) {
            return null;
        }
        var element = cachedElements.get(rowAtClick);
        if (!element.editable()) {
            return null;
        }
        return element.rawIndex();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return scroll.mouseDragged(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return scroll.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return scroll.mouseScrolled(scrollY);
    }

    @Override
    public void onShown() {
        // When the user switches back to this tab, scroll back to the top — same convention as GOAPDetailsPanel.
        scroll.reset();
    }

    private static List<SearchableSelect.Item<ResourceLocation>> buildPoolItems() {
        return JigsawPoolLibrary.listPoolIds()
            .stream()
            .sorted(Comparator.comparing(ResourceLocation::getNamespace).thenComparing(ResourceLocation::getPath))
            .map(id -> new SearchableSelect.Item<>(id, id.toString()))
            .toList();
    }

    /**
     * Items for the footer's "Add piece" picker — every loaded structure template, sorted by namespace+path. No icons
     * (templates have no natural item icon; thumbnails would need a different rendering path than the
     * SearchableSelect's ItemStack-typed iconProvider expects).
     */
    private static List<SearchableSelect.Item<ResourceLocation>> buildAddPieceItems() {
        return JigsawPieceLibrary.listIds()
            .stream()
            .sorted(Comparator.comparing(ResourceLocation::getNamespace).thenComparing(ResourceLocation::getPath))
            .map(id -> new SearchableSelect.Item<>(id, id.toString()))
            .toList();
    }

    /**
     * Set the GL scissor to clip drawing to {@code (x, y, w, h)} in this panel's logical-pixel space. Mirrors the
     * raw-scissor idiom from {@link PiecePalettePanel} / {@link GOAPDetailsPanel} — bypasses the GuiGraphics scissor
     * stack so stale upstream entries can't clip our row area to a smaller residual rect.
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
