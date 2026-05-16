package com.blib.engine.ui.panel.details;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

import com.blib.engine.domain.selection.picking.BlockSelectable;
import com.blib.engine.domain.selection.picking.Selectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.picking.TagSelectable;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.PanelPlaceholder;
import com.blib.engine.ui.ProjectContentActionHandler;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.panel.base.InspectorSectionRegistry;
import com.blib.engine.ui.widget.ScrollContainer;
import com.blib.engine.ui.widget.SearchableSelect;
import com.blib.engine.ui.widget.SegmentedControl;
import com.blib.engine.ui.widget.TextInput;

/**
 * Right-side universal inspector. Reads {@link SelectionManager#current} each frame and dispatches to a per-type
 * {@link InspectorSection}; with nothing selected, falls back to a tool-state hint pointing the user at how to populate
 * the inspector. Each selectable subtype owns its widgets / hit-rects / commit plumbing in its own section file — this
 * panel is now just the dispatcher plus a handful of shared row / icon helpers that every section composes against.
 */
@ApiStatus.Internal
public final class DetailsPanel implements Panel {

    private final @Nullable ProjectContentActionHandler actionHandler;

    /** Read-only accessor used by sibling sections that need to spawn destructive-confirm dialogs. */
    @Nullable
    ProjectContentActionHandler actionHandler() {
        return actionHandler;
    }

    // Per-type inspector sections — each owns its widgets, render, and click handling for one selectable subtype.
    // Stored as typed fields so DetailsPanel can borrow widgets across sections where needed (e.g. the placed-piece
    // section reuses the volume section's position/size text inputs since piece editing piggybacks on volume input
    // plumbing); the sections list below feeds the registry-driven dispatch in render() / mouseClicked().
    final EntityInspectorSection entitySection = new EntityInspectorSection(this);

    final BlockInspectorSection blockSection = new BlockInspectorSection(this);

    final BlockVolumeInspectorSection volumeSection = new BlockVolumeInspectorSection(this);

    final PlacedJigsawPieceInspectorSection pieceSection = new PlacedJigsawPieceInspectorSection(this);

    final FactionInspectorSection factionSection = new FactionInspectorSection(this);

    final TagInspectorSection tagSection = new TagInspectorSection(this);

    private final List<InspectorSection<?>> sections = List.of(
        entitySection,
        blockSection,
        volumeSection,
        pieceSection,
        factionSection,
        tagSection
    );

    private final ScrollViewport contentScroll = new ScrollViewport();

    public DetailsPanel() {
        this(null);
    }

    public DetailsPanel(@Nullable ProjectContentActionHandler actionHandler) {
        this.actionHandler = actionHandler;
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

    private static final int MIN_SCROLL_CONTENT_WIDTH = 240;

    private static final int MIN_CONTROL_CONTENT_WIDTH = 130;

    private static final ThreadLocal<ContentBounds> CONTENT_BOUNDS = new ThreadLocal<>();

    /** Vertical gap between editable rows. */
    private static final int ROW_GAP = 2;

    /** Bounding-box side for the help-icon hit-test. The glyph itself is a single "?" rendered at this size. */
    private static final int HELP_ICON_SIZE = 7;

    /** Gap between the row label / section header text and the help icon. */
    private static final int HELP_ICON_GAP = 3;

    private static final int HELP_ICON_COLOR = 0xFF606068;

    private static final int HELP_ICON_HOVER_COLOR = 0xFFE6C26B;

    private static final int WARN_ICON_COLOR = 0xFFE6A23C;

    private static final int WARN_ICON_HOVER_COLOR = 0xFFFFC766;

    /**
     * Tooltip computed during render — set when the cursor hovers a help-icon "?" next to a label or section header.
     * Read by {@link #tooltipText} after render via the workspace's tooltip pass. Reset to null at the top of every
     * render so a stale hover from the previous frame doesn't ghost into this frame's tooltip.
     */
    @Nullable
    Component hoveredHelpTooltip;

    private int measuredContentHeight;

    private int measuredContentWidth = MIN_SCROLL_CONTENT_WIDTH;

    private @Nullable String measuredSelectionKey;

    // Cached panel rect — needed by sections that lay out against the panel's full extent (e.g. the tag view's pinned
    // footer). Package-default so sibling section files can read it.
    int rectX;

    int rectY;

    int rectWidth;

    int rectHeight;

    @Override
    public String title() {
        return "Inspector";
    }

    @Override
    public void onShown() {
        contentScroll.reset();
        measuredContentHeight = 0;
        measuredContentWidth = MIN_SCROLL_CONTENT_WIDTH;
        measuredSelectionKey = null;
    }

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
            contentScroll.clear();
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
        // Reset per-section hit rects so clicks on stale rects don't fire after the selection swaps.
        factionSection.resetHitRects();
        pieceSection.resetHitRects();
        tagSection.resetHitRects();

        var font = EngineFont.get();
        var rowY = y;

        var selection = SelectionManager.current();
        var single = selection.single();
        resetMeasuredContentIfSelectionChanged(single);

        rowY = drawHeaderBar(graphics, font, x, rowY, width, single);

        if (single instanceof TagSelectable) {
            contentScroll.clear();
            var bodyViewport = UiRect.of(x, rowY, width, Math.max(0, y + height - rowY));
            try (var ignored = UiText.captureTruncatedTextTooltips(mouseX, mouseY, bodyViewport, this::setHoveredTooltip)) {
                renderInspectorContent(graphics, font, x, rowY, width, single, mouseX, mouseY);
            }
            return;
        }

        var viewport = UiRect.of(x, rowY, width, Math.max(0, y + height - rowY));
        if (viewport.isEmpty()) {
            contentScroll.clear();
            return;
        }

        var baseContentWidth = baseContentWidth(viewport);
        var frame = contentScroll.begin(graphics, viewport, Math.max(baseContentWidth, measuredContentWidth), measuredContentHeight);
        var previousBounds = CONTENT_BOUNDS.get();
        var bounds = new ContentBounds(frame.contentX() + baseContentWidth);
        CONTENT_BOUNDS.set(bounds);
        try (var ignored = UiText.captureTruncatedTextTooltips(mouseX, mouseY, frame.visibleContentRect(), this::setHoveredTooltip)) {
            var contentMouseX = contentScroll.containsVisibleContent(mouseX, mouseY) ? mouseX : Integer.MIN_VALUE;
            var contentMouseY = contentScroll.containsVisibleContent(mouseX, mouseY) ? mouseY : Integer.MIN_VALUE;
            var contentBottom = renderInspectorContent(
                graphics,
                font,
                frame.contentX(),
                frame.contentY(),
                frame.contentWidth(),
                single,
                contentMouseX,
                contentMouseY
            );
            measuredContentHeight = Math.max(0, contentBottom - frame.contentY() + CONTENT_PADDING);
            measuredContentWidth = Math.max(baseContentWidth, bounds.contentWidth(frame.contentX()));
        } finally {
            if (previousBounds == null) {
                CONTENT_BOUNDS.remove();
            } else {
                CONTENT_BOUNDS.set(previousBounds);
            }
            contentScroll.end(graphics, mouseX, mouseY);
        }
    }

    private int renderInspectorContent(
        GuiGraphics graphics,
        Font font,
        int x,
        int rowY,
        int width,
        @Nullable Selectable single,
        int mouseX,
        int mouseY
    ) {
        if (single == null) {
            blockSection.clearCurrentBlock();
            return internalRenderToolStateView(graphics, font, x, rowY, width, mouseX, mouseY);
        }

        // Registry-driven dispatch — pick the first registered section whose selectableType matches the runtime class
        // of the current selection. The legacy {@code switch (single.type())} is gone; adding a new selectable type now
        // means dropping an InspectorSection in {@code panel.details.*} and registering it in the constructor's
        // section list, no edits to this method.
        if (!(single instanceof BlockSelectable)) {
            blockSection.clearCurrentBlock();
        }
        var section = matchingSection(single);
        if (section != null) {
            rowY = dispatchSection(section, graphics, x, rowY, width, single, mouseX, mouseY);
        } else {
            rowY = internalRenderGenericView(graphics, font, x, rowY, width, single);
        }
        // After the built-in section, chain any externally-registered sections (registered via the public
        // com.blib.api.client.engine.v1.inspector.InspectorSectionRegistry facade) at the y the built-in section
        // returned. Downstream sections that don't apply to the current selection should just return y unchanged.
        for (var contributed : InspectorSectionRegistry.matching(single)) {
            rowY = dispatchSection(contributed, graphics, x, rowY, width, single, mouseX, mouseY);
        }
        return rowY;
    }

    private void setHoveredTooltip(Component tooltip) {
        hoveredHelpTooltip = tooltip;
    }

    private @Nullable InspectorSection<?> matchingSection(Selectable target) {
        for (var s : sections) {
            if (s.selectableType().isInstance(target)) {
                return s;
            }
        }
        return null;
    }

    private void resetMeasuredContentIfSelectionChanged(@Nullable Selectable selection) {
        var key = selection == null ? "<tool-state>" : selection.type().name() + ":" + selection.displayName().getString();
        if (Objects.equals(key, measuredSelectionKey)) {
            return;
        }
        measuredSelectionKey = key;
        measuredContentHeight = 0;
        measuredContentWidth = MIN_SCROLL_CONTENT_WIDTH;
        contentScroll.reset();
    }

    private static int baseContentWidth(UiRect viewport) {
        return Math.max(MIN_SCROLL_CONTENT_WIDTH, viewport.width() - ScrollContainer.SCROLLBAR_GUTTER);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static int dispatchSection(
        InspectorSection<?> section,
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        Selectable target,
        int mouseX,
        int mouseY
    ) {
        return ((InspectorSection) section).render(graphics, x, y, width, target, mouseX, mouseY);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static boolean dispatchSectionClick(
        InspectorSection<?> section,
        double mouseX,
        double mouseY,
        int button,
        Selectable target
    ) {
        return ((InspectorSection) section).mouseClicked(mouseX, mouseY, button, target);
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        if (SelectionManager.current().single() instanceof TagSelectable) {
            return false;
        }
        return contentScroll.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var single = SelectionManager.current().single();
        if (single == null) {
            return false;
        }
        if (!(single instanceof TagSelectable)) {
            if (contentScroll.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (!contentScroll.containsVisibleContent(mouseX, mouseY)) {
                return false;
            }
        }
        var section = matchingSection(single);
        if (section != null && dispatchSectionClick(section, mouseX, mouseY, button, single)) {
            return true;
        }
        // Fall through to contributed sections so downstream debug views can handle clicks (e.g. row hits, buttons).
        for (var contributed : InspectorSectionRegistry.matching(single)) {
            if (dispatchSectionClick(contributed, mouseX, mouseY, button, single)) {
                return true;
            }
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
    public int internalRenderToolStateView(GuiGraphics graphics, Font font, int x, int y, int width, int mouseX, int mouseY) {
        var hint = "Pick an entity, block, faction, or tag to inspect its details.";
        var hintX = x + CONTENT_PADDING;
        var hintY = y + LINE_HEIGHT;
        var hintWidth = Math.max(0, width - 2 * CONTENT_PADDING);
        var hintHeight = UiText.measureWrappedHeight(font, hint, hintWidth);
        var bounds = UiRect.of(hintX, hintY, hintWidth, hintHeight);
        var drawnHeight = UiText.drawWrappedCentered(graphics, font, hint, bounds, LABEL_COLOR);
        trackContentRight(x + width);
        return hintY + Math.max(LINE_HEIGHT, drawnHeight);
    }

    /**
     * Fallback for selectable types that don't have a dedicated view yet. Shows the selectable's display name and its
     * world-bounds center so the user at least sees that something is selected and where it is.
     */
    public static int internalRenderGenericView(GuiGraphics graphics, Font font, int x, int y, int width, Selectable selectable) {
        var rowY = y;
        rowY = drawSectionHeader(graphics, font, x, rowY, width, selectable.type().name());
        rowY += CONTENT_PADDING / 2;
        var pivot = selectable.pivot();
        if (pivot != null) {
            rowY = drawRow(graphics, font, x, rowY, "X", String.format("%.2f", pivot.x));
            rowY = drawRow(graphics, font, x, rowY, "Y", String.format("%.2f", pivot.y));
            rowY = drawRow(graphics, font, x, rowY, "Z", String.format("%.2f", pivot.z));
        }
        return rowY;
    }

    static @Nullable Integer parseInt(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static void trackContentRight(int right) {
        var bounds = CONTENT_BOUNDS.get();
        if (bounds != null) {
            bounds.includeRight(right);
        }
    }

    static void trackControlContentRight(int controlX) {
        trackControlContentRight(controlX, 0);
    }

    static void trackControlContentRight(int controlX, int trailingWidth) {
        trackContentRight(controlX + MIN_CONTROL_CONTENT_WIDTH + trailingWidth + CONTENT_PADDING);
    }

    private static void trackTextRight(Font font, int x, String text) {
        trackContentRight(x + font.width(text) + CONTENT_PADDING);
    }

    static int drawSectionHeader(GuiGraphics graphics, Font font, int x, int y, int width, String label) {
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
        trackTextRight(font, x + CONTENT_PADDING, label);
        return y + SECTION_HEADER_HEIGHT;
    }

    /**
     * Section-header variant that renders a "?" help icon to the right of the label and surfaces {@code helpText} via
     * {@link #tooltipText} when the cursor hovers it. Instance method (rather than static) so it can write the panel's
     * hovered-tooltip field directly.
     */
    int drawSectionHeaderWithHelp(
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
        trackContentRight(iconX + HELP_ICON_SIZE + CONTENT_PADDING);
        return nextY;
    }

    static int drawRow(GuiGraphics graphics, Font font, int x, int y, String label, String value) {
        graphics.drawString(font, Component.literal(label), x + CONTENT_PADDING, y, LABEL_COLOR, false);
        graphics.drawString(font, Component.literal(value), x + CONTENT_PADDING + LABEL_COLUMN_WIDTH, y, VALUE_COLOR, false);
        trackTextRight(font, x + CONTENT_PADDING, label);
        trackTextRight(font, x + CONTENT_PADDING + LABEL_COLUMN_WIDTH, value);
        return y + LINE_HEIGHT;
    }

    static int drawClippedRow(GuiGraphics graphics, Font font, int x, int y, int width, String label, String value) {
        var labelX = x + CONTENT_PADDING;
        var valueX = labelX + LABEL_COLUMN_WIDTH;
        var rowRight = x + width - CONTENT_PADDING;
        UiText.drawClipped(graphics, font, label, labelX, y, Math.max(0, LABEL_COLUMN_WIDTH), LABEL_COLOR);
        UiText.drawClipped(graphics, font, value, valueX, y, Math.max(0, rowRight - valueX), VALUE_COLOR);
        trackTextRight(font, labelX, label);
        return y + LINE_HEIGHT;
    }

    static int drawNote(GuiGraphics graphics, Font font, int x, int y, int width, String text) {
        var noteX = x + CONTENT_PADDING;
        var noteWidth = Math.max(0, width - 2 * CONTENT_PADDING);
        var drawnHeight = UiText.drawWrapped(graphics, font, text, noteX, y, noteWidth, LABEL_COLOR);
        trackContentRight(x + width);
        return y + Math.max(LINE_HEIGHT, drawnHeight);
    }

    /**
     * Label + {@link TextInput} on one row. Label takes {@link #LABEL_COLUMN_WIDTH} on the left, input fills the rest
     * minus content padding on both sides. The label baseline is centered against the input's text baseline using the
     * same +2 descender-padding compensation used elsewhere in the workspace. When {@code helpText} is non-null, a "?"
     * icon is rendered immediately after the label and contributes to {@link #hoveredHelpTooltip} on hover.
     */
    int drawInputRow(
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
        trackTextRight(font, x + CONTENT_PADDING, label);
        if (helpText != null) {
            var iconX = x + CONTENT_PADDING + font.width(label) + HELP_ICON_GAP;
            if (drawHelpIcon(graphics, font, iconX, labelY, mouseX, mouseY)) {
                hoveredHelpTooltip = helpText;
            }
            trackContentRight(iconX + HELP_ICON_SIZE + CONTENT_PADDING);
        }
        var inputX = x + CONTENT_PADDING + LABEL_COLUMN_WIDTH;
        var inputW = Math.max(0, width - LABEL_COLUMN_WIDTH - 2 * CONTENT_PADDING);
        input.render(graphics, inputX, y, inputW, mouseX, mouseY);
        trackControlContentRight(inputX);
        return y + TextInput.HEIGHT + ROW_GAP;
    }

    /**
     * Render a small "?" glyph anchored at {@code (iconX, iconY)} with a hover hit-rect of {@link #HELP_ICON_SIZE} px
     * on each side. Returns {@code true} when the cursor is over the icon — caller writes the tooltip field.
     */
    static boolean drawHelpIcon(GuiGraphics graphics, Font font, int iconX, int iconY, int mouseX, int mouseY) {
        return drawIconGlyph(graphics, font, iconX, iconY, "?", HELP_ICON_COLOR, HELP_ICON_HOVER_COLOR, mouseX, mouseY);
    }

    /**
     * Warning-icon variant. Same hit-rect + tooltip-on-hover semantics as {@link #drawHelpIcon}, but the glyph and
     * palette signal a problem the user should look at rather than a neutral help affordance.
     */
    static boolean drawWarningIcon(GuiGraphics graphics, Font font, int iconX, int iconY, int mouseX, int mouseY) {
        return drawIconGlyph(graphics, font, iconX, iconY, "!", WARN_ICON_COLOR, WARN_ICON_HOVER_COLOR, mouseX, mouseY);
    }

    static boolean drawIconGlyph(
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
        trackContentRight(iconX + HELP_ICON_SIZE + CONTENT_PADDING);
        return hovered;
    }

    /**
     * Full-width {@link SegmentedControl} row — no label, since the section header above (e.g. "Joint") already names
     * the control.
     */
    static int drawSegmentedRow(GuiGraphics graphics, int x, int y, int width, SegmentedControl control, int mouseX, int mouseY) {
        var ctrlW = Math.max(0, width - 2 * CONTENT_PADDING);
        control.render(graphics, x + CONTENT_PADDING, y, ctrlW, mouseX, mouseY);
        trackControlContentRight(x + CONTENT_PADDING);
        return y + SegmentedControl.HEIGHT + ROW_GAP;
    }

    /**
     * Labeled {@link SegmentedControl} row — left-side label plus an optional "?" help icon that surfaces
     * {@code helpText} on hover. {@code labelColumnWidth} sizes the left label column independently of
     * {@link #LABEL_COLUMN_WIDTH} so callers with wordier labels (e.g. the faction inspector's protection toggles)
     * don't get their text clipped into the control area.
     */
    int drawLabeledSegmentedRow(
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
        trackTextRight(font, x + CONTENT_PADDING, label);
        if (helpText != null) {
            var iconX = x + CONTENT_PADDING + font.width(label) + HELP_ICON_GAP;
            if (drawHelpIcon(graphics, font, iconX, labelY, mouseX, mouseY)) {
                hoveredHelpTooltip = helpText;
            }
            trackContentRight(iconX + HELP_ICON_SIZE + CONTENT_PADDING);
        }
        var ctrlX = x + CONTENT_PADDING + labelColumnWidth;
        var ctrlW = Math.max(0, width - labelColumnWidth - 2 * CONTENT_PADDING);
        control.render(graphics, ctrlX, y, ctrlW, mouseX, mouseY);
        trackControlContentRight(ctrlX);
        return y + SegmentedControl.HEIGHT + ROW_GAP;
    }

    /**
     * Label + {@link SearchableSelect} on one row. Same geometry as {@link #drawInputRow} so the inspector keeps a
     * consistent grid; only the right-hand widget differs. When {@code helpText} is non-null, a "?" icon is rendered
     * after the label and contributes to {@link #hoveredHelpTooltip} on hover.
     */
    int drawSelectRow(
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
        trackTextRight(font, x + CONTENT_PADDING, label);
        if (helpText != null) {
            var iconX = x + CONTENT_PADDING + font.width(label) + HELP_ICON_GAP;
            if (drawHelpIcon(graphics, font, iconX, labelY, mouseX, mouseY)) {
                hoveredHelpTooltip = helpText;
            }
            trackContentRight(iconX + HELP_ICON_SIZE + CONTENT_PADDING);
        }
        var ctrlX = x + CONTENT_PADDING + LABEL_COLUMN_WIDTH;
        var ctrlW = Math.max(0, width - LABEL_COLUMN_WIDTH - 2 * CONTENT_PADDING);
        select.render(graphics, ctrlX, y, ctrlW, mouseX, mouseY);
        trackControlContentRight(ctrlX);
        return y + SearchableSelect.HEIGHT + ROW_GAP;
    }

    /** Empty-list placeholder note centred in a list area. Used by Tag + Block sections. */
    static void drawCenteredNote(GuiGraphics graphics, Font font, int x, int y, int width, int height, String text) {
        UiText.drawWrappedCentered(graphics, font, text, UiRect.of(x, y, width, height), 0xFF606068);
        trackContentRight(x + width);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (SelectionManager.current().single() instanceof TagSelectable) {
            return tagSection.mouseDragged(mouseX, mouseY, button);
        }
        return contentScroll.mouseDragged(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (SelectionManager.current().single() instanceof TagSelectable) {
            return tagSection.mouseReleased(mouseX, mouseY, button);
        }
        return contentScroll.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (SelectionManager.current().single() instanceof TagSelectable) {
            return tagSection.mouseScrolled(mouseX, mouseY, scrollY);
        }
        return contentScroll.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private static final class ContentBounds {

        private int right;

        private ContentBounds(int initialRight) {
            this.right = initialRight;
        }

        private void includeRight(int nextRight) {
            right = Math.max(right, nextRight);
        }

        private int contentWidth(int contentX) {
            return Math.max(0, right - contentX);
        }
    }

    /**
     * Set the GL scissor to clip drawing to {@code (x, y, w, h)} in this panel's logical-pixel space. Mirrors the
     * raw-scissor idiom from the other engine panels — bypasses the GuiGraphics scissor stack so stale upstream entries
     * can't clip our row area to a smaller residual rect.
     */
    static void applyRawScissor(GuiGraphics graphics, int x, int y, int w, int h) {
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
