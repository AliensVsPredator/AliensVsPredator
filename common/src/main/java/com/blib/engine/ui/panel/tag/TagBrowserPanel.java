package com.blib.engine.ui.panel.tag;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.picking.TagSelectable;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.tag.TagCatalogCache;
import com.blib.engine.tag.TagStagingCache;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.PanelPlaceholder;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.widget.ScrollContainer;
import com.blib.engine.ui.widget.SegmentedControl;
import com.blib.engine.ui.widget.TextInput;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SCreateTagPayload;
import com.blib.mod.common.network.packet.C2SRequestTagCatalogPayload;
import com.blib.mod.common.network.packet.TagCatalogEntry;

/**
 * Browser of every tag in the live registry plus any project-only tags. Rendered as collapsible sections, one per
 * registry, sorted by registry id. Top row has a substring filter, a namespace prefix filter, a Project / All toggle,
 * and a Refresh button. Per-section {@code +} opens an inline Create popup that captures a new tag id (default-prefixed
 * with the project's namespace) and sends {@link C2SCreateTagPayload}.
 * <p>
 * Clicking anywhere on an entry row selects that tag via {@link SelectionManager#selectSingle} — the inspector's
 * {@code case TAG} branch picks it up and renders the editable view (entries list, Replace toggle, Reload button,
 * Add-entry picker).
 * <p>
 * The body is virtualized: only rows within the visible Y range are rendered + hit-tested. Without this, expanding all
 * sections (≈700 tags total in vanilla 1.21.1) drops framerate noticeably because per-row string measurement dominates
 * the per-frame work.
 */
@ApiStatus.Internal
public final class TagBrowserPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int HEADER_BG_COLOR = 0xFF1A1A22;

    private static final int HEADER_BG_HOVER_COLOR = 0xFF22222C;

    private static final int HEADER_TEXT_COLOR = 0xFFB0B0B0;

    private static final int HEADER_COUNT_COLOR = 0xFF707078;

    private static final int ROW_BG_HOVER_COLOR = 0xFF1F1F26;

    private static final int ROW_BG_SELECTED_COLOR = 0xFF2C3F5C;

    private static final int ROW_TEXT_COLOR = 0xFFD0D0D0;

    private static final int ROW_TEXT_HOVER_COLOR = 0xFFFFFFFF;

    /** Project-authored tag that doesn't exist in any upstream pack (vanilla / mods). Mirrors git "new file" green. */
    private static final int ROW_PROJECT_NEW_TINT = 0xFF80E080;

    /** Staged-but-unreloaded tint — picks up after any edit until Reload Project clears the staging cache. */
    private static final int ROW_PROJECT_STAGED_TINT = 0xFFE08080;

    /**
     * Project-authored tag that ALSO exists in an upstream pack — the project is overriding it. Mirrors git "modified"
     * blue.
     */
    private static final int ROW_PROJECT_MODIFIED_TINT = 0xFF7CB6E0;

    private static final int EMPTY_TEXT_COLOR = 0xFF606068;

    private static final int CREATE_BUTTON_COLOR = 0xFF80E080;

    private static final int POPUP_DIM_COLOR = 0xC0000000;

    private static final int POPUP_BG_COLOR = 0xFF1F1F26;

    private static final int POPUP_BORDER_COLOR = 0xFF353540;

    private static final int POPUP_TITLE_COLOR = 0xFFE6C26B;

    private static final int POPUP_ERROR_COLOR = 0xFFE06868;

    private static final int CONTENT_PADDING = 5;

    private static final int SEARCH_GAP_BELOW = 4;

    private static final int HEADER_HEIGHT = 12;

    private static final int ROW_HEIGHT = 12;

    private static final int CARET_WIDTH = 6;

    private static final int CREATE_BUTTON_WIDTH = 12;

    private static final int TOP_ROW_HEIGHT = TextInput.HEIGHT;

    private static final int NAMESPACE_INPUT_WIDTH = 80;

    private static final int PROJECT_TOGGLE_WIDTH = 78;

    private static final List<String> PROJECT_TOGGLE_LABELS = List.of("All", "Project");

    /** Palette used to color section accents. Hash registry id → modulo into this for a stable per-registry color. */
    private static final int[] ACCENT_PALETTE = {
        0xFFE6C26B,
        0xFF7CB6E0,
        0xFFB6E2A1,
        0xFFE08AC0,
        0xFFE08A8A,
        0xFF8AE0E0,
        0xFFE0C68A,
        0xFFB68AE0,
    };

    private final TextInput searchInput = new TextInput("Filter…");

    private final TextInput namespaceInput = new TextInput("Namespace…");

    private final SegmentedControl projectToggle = new SegmentedControl(PROJECT_TOGGLE_LABELS, 0);

    private final ScrollContainer scroll = new ScrollContainer();

    private final Set<ResourceLocation> collapsed = new HashSet<>();

    /** Per-frame hit lists, populated by render and consumed by mouseClicked. Only visible items get entries. */
    private final List<RowHit> rowHits = new ArrayList<>();

    private final List<HeaderHit> headerHits = new ArrayList<>();

    private final List<CreateHit> createHits = new ArrayList<>();

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    private @Nullable String lastFetchedProject;

    /** When non-null, an inline Create-tag popup is being shown for the captured registry. Modal within the panel. */
    private @Nullable CreateTagPopup createPopup;

    @Override
    public String title() {
        return "Tag Browser";
    }

    @Override
    public void onShown() {
        scroll.reset();
        var projectName = ProjectSession.activeProjectName();
        if (!projectName.isEmpty()) {
            requestCatalog(projectName);
            lastFetchedProject = projectName;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;
        rowHits.clear();
        headerHits.clear();
        createHits.clear();

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Tag browser reads the server-side tag catalog (works even without a project — the "All" filter mode shows
        // every tag) but needs the integrated server to be running.
        if (Minecraft.getInstance().level == null) {
            PanelPlaceholder.drawCentered(graphics, x, y, width, height, PanelPlaceholder.NEEDS_WORLD);
            return;
        }

        var projectName = ProjectSession.activeProjectName();
        if (!projectName.isEmpty() && !projectName.equals(lastFetchedProject)) {
            requestCatalog(projectName);
            lastFetchedProject = projectName;
        }

        var font = EngineFont.get();
        var topRowY = y + CONTENT_PADDING;

        // Right-aligned: project toggle. Then namespace input. Search input takes the rest. The catalog re-fetches
        // automatically on workspace open / project swap, and the inspector pushes a fresh draft after every edit
        // (which flips the row's inProject flag locally) — no manual Refresh button needed.
        var toggleX = x + width - CONTENT_PADDING - PROJECT_TOGGLE_WIDTH;
        var nsX = toggleX - 4 - NAMESPACE_INPUT_WIDTH;
        var searchW = Math.max(0, nsX - (x + CONTENT_PADDING) - 4);

        searchInput.render(graphics, x + CONTENT_PADDING, topRowY, searchW, mouseX, mouseY);
        namespaceInput.render(graphics, nsX, topRowY, NAMESPACE_INPUT_WIDTH, mouseX, mouseY);
        projectToggle.render(graphics, toggleX, topRowY, PROJECT_TOGGLE_WIDTH, mouseX, mouseY);

        var listX = x + CONTENT_PADDING;
        var listY = topRowY + TOP_ROW_HEIGHT + SEARCH_GAP_BELOW;
        var listW = width - 2 * CONTENT_PADDING;
        var listH = Math.max(0, height - (listY - y) - CONTENT_PADDING);
        if (listH <= 0) {
            return;
        }

        if (projectName.isEmpty()) {
            graphics.drawString(font, Component.literal("(no project open)"), listX, listY, EMPTY_TEXT_COLOR, false);
            return;
        }

        var grouped = TagCatalogCache.groupedByRegistry();
        if (grouped.isEmpty()) {
            graphics.drawString(font, Component.literal("(loading…)"), listX, listY, EMPTY_TEXT_COLOR, false);
            return;
        }

        var query = searchInput.content().toLowerCase(Locale.ROOT).trim();
        var nsQuery = namespaceInput.content().toLowerCase(Locale.ROOT).trim();
        var projectOnly = projectToggle.selectedIndex() == 1;

        // Filtered view: one entry per registry that has matching tags. We pre-filter into a flat list so the
        // virtualization pass (below) is a simple linear walk with early-exit when we leave the viewport.
        var filtered = new LinkedHashMap<ResourceLocation, List<TagCatalogEntry>>();
        for (var entry : grouped.entrySet()) {
            var matched = new ArrayList<TagCatalogEntry>();
            for (var ce : entry.getValue()) {
                if (projectOnly && !ce.inProject()) {
                    continue;
                }
                if (!query.isEmpty() && !ce.tagId().toString().toLowerCase(Locale.ROOT).contains(query)) {
                    continue;
                }
                if (!nsQuery.isEmpty() && !ce.tagId().getNamespace().toLowerCase(Locale.ROOT).startsWith(nsQuery)) {
                    continue;
                }
                matched.add(ce);
            }
            if (!matched.isEmpty()) {
                filtered.put(entry.getKey(), matched);
            }
        }

        if (filtered.isEmpty()) {
            graphics.drawString(font, Component.literal("(no matches)"), listX, listY, EMPTY_TEXT_COLOR, false);
            return;
        }

        // Total content height for scrollbar.
        var contentHeight = 0;
        for (var entry : filtered.entrySet()) {
            contentHeight += HEADER_HEIGHT;
            if (collapsed.contains(entry.getKey())) {
                continue;
            }
            contentHeight += entry.getValue().size() * ROW_HEIGHT;
        }
        scroll.layout(listH, contentHeight);

        var selectedTag = currentlySelectedTag();

        applyRawScissor(graphics, listX, listY, listW, listH);
        try {
            var scrollY = (int) scroll.scrollY();
            var visibleTop = listY;
            var visibleBottom = listY + listH;
            var cursorY = listY - scrollY;

            outer:
            for (var entry : filtered.entrySet()) {
                var registryKey = entry.getKey();
                // Section header — render only if visible; always advance cursor + record collapse-toggle hit (for
                // the visible ones).
                if (cursorY + HEADER_HEIGHT > visibleTop && cursorY < visibleBottom) {
                    renderSection(graphics, listX, cursorY, listW, registryKey, entry.getValue().size(), mouseX, mouseY);
                }
                cursorY += HEADER_HEIGHT;
                if (cursorY >= visibleBottom) {
                    // Everything from here down is below the viewport — skip the remaining work entirely.
                    break;
                }
                if (collapsed.contains(registryKey)) {
                    continue;
                }
                var rows = entry.getValue();
                // Compute the slice of rows that intersect the viewport.
                var sectionTop = cursorY;
                var firstVisibleRow = Math.max(0, (visibleTop - sectionTop) / ROW_HEIGHT);
                var lastVisibleRow = Math.min(rows.size() - 1, (visibleBottom - sectionTop) / ROW_HEIGHT);
                if (firstVisibleRow > lastVisibleRow) {
                    // Whole section is outside the viewport — advance by the actual section height. The bare
                    // {@code firstVisibleRow * ROW_HEIGHT} advance is wrong here because (a) when the section is
                    // above the viewport, firstVisibleRow can exceed rows.size(), over-advancing the cursor and
                    // making later sections render at a constant Y regardless of scroll position; and (b) when
                    // it's below, lastVisibleRow can be -1, causing the skipped-below adjustment to overshoot too.
                    cursorY += rows.size() * ROW_HEIGHT;
                } else {
                    // Skip past rows entirely above the viewport without rendering.
                    cursorY += firstVisibleRow * ROW_HEIGHT;
                    for (var i = firstVisibleRow; i <= lastVisibleRow; i++) {
                        if (cursorY >= visibleBottom) {
                            break outer;
                        }
                        renderRow(graphics, listX, cursorY, listW, rows.get(i), selectedTag, mouseX, mouseY);
                        cursorY += ROW_HEIGHT;
                    }
                    // Advance past rows below the visible slice without rendering.
                    var skipped = rows.size() - 1 - lastVisibleRow;
                    if (skipped > 0) {
                        cursorY += skipped * ROW_HEIGHT;
                    }
                }
            }
        } finally {
            graphics.flush();
            RenderSystem.disableScissor();
        }

        scroll.renderScrollbar(graphics, listX, listY, listW, listH, mouseX, mouseY);

        if (createPopup != null) {
            renderCreatePopup(graphics, x, y, width, height, mouseX, mouseY);
        }
    }

    private void renderSection(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        ResourceLocation registryKey,
        int count,
        int mouseX,
        int mouseY
    ) {
        // Reserve the scrollbar gutter so the header background and right-aligned + button stay clear of it.
        var rowRight = x + width - ScrollContainer.SCROLLBAR_GUTTER;
        var hovered = mouseX >= x && mouseX < rowRight && mouseY >= y && mouseY < y + HEADER_HEIGHT;
        var bg = hovered ? HEADER_BG_HOVER_COLOR : HEADER_BG_COLOR;
        graphics.fill(x, y, rowRight, y + HEADER_HEIGHT, bg);
        graphics.fill(x, y, x + 2, y + HEADER_HEIGHT, accentColor(registryKey));

        var font = EngineFont.get();
        var caret = collapsed.contains(registryKey) ? "▸" : "▾";
        var textY = y + (HEADER_HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(caret), x + 4, textY, HEADER_TEXT_COLOR, false);
        graphics.drawString(font, Component.literal(registryKey.toString()), x + 4 + CARET_WIDTH + 2, textY, HEADER_TEXT_COLOR, false);

        // Right side: count + create (+) button.
        var createX = rowRight - 4 - CREATE_BUTTON_WIDTH;
        graphics.drawString(
            font,
            Component.literal("+"),
            createX + (CREATE_BUTTON_WIDTH - font.width("+")) / 2,
            textY,
            CREATE_BUTTON_COLOR,
            false
        );
        createHits.add(new CreateHit(new Rect(createX, y, CREATE_BUTTON_WIDTH, HEADER_HEIGHT), registryKey));

        var countLabel = "(" + count + ")";
        var countX = createX - 6 - font.width(countLabel);
        graphics.drawString(font, Component.literal(countLabel), countX, textY, HEADER_COUNT_COLOR, false);

        headerHits.add(new HeaderHit(x, y, rowRight - x, HEADER_HEIGHT, registryKey));
    }

    private void renderRow(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        TagCatalogEntry ce,
        @Nullable TagSelectable selectedTag,
        int mouseX,
        int mouseY
    ) {
        // Reserve the scrollbar gutter so the row hover background, label truncation, and click hit-test all stop
        // before the bar.
        var rowRight = x + width - ScrollContainer.SCROLLBAR_GUTTER;
        var selected = selectedTag != null
            && selectedTag.registryKey().equals(ce.registryKey())
            && selectedTag.tagId().equals(ce.tagId());
        var hovered = mouseX >= x && mouseX < rowRight && mouseY >= y && mouseY < y + ROW_HEIGHT;
        if (selected) {
            graphics.fill(x, y, rowRight, y + ROW_HEIGHT, ROW_BG_SELECTED_COLOR);
        } else if (hovered) {
            graphics.fill(x, y, rowRight, y + ROW_HEIGHT, ROW_BG_HOVER_COLOR);
        }

        var font = EngineFont.get();
        var textY = y + (ROW_HEIGHT - font.lineHeight + 2) / 2;

        var labelMaxWidth = Math.max(0, rowRight - x - 16);
        var truncated = font.plainSubstrByWidth(ce.tagId().toString(), labelMaxWidth);
        int labelColor;
        if (TagStagingCache.isTagStaged(ce.registryKey(), ce.tagId())) {
            // Any unreloaded edit (new tag, entry add/remove, replace toggle) lights the row red until reload, at
            // which point the staging clears and the row falls through to the green/blue/neutral logic below.
            labelColor = ROW_PROJECT_STAGED_TINT;
        } else if (ce.inProject() && !ce.equivalentToUpstream() && ce.inUpstream()) {
            labelColor = ROW_PROJECT_MODIFIED_TINT;
        } else if (ce.inProject() && !ce.equivalentToUpstream()) {
            labelColor = ROW_PROJECT_NEW_TINT;
        } else {
            // equivalentToUpstream → project JSON exists but doesn't modify the merged tag. Treat as upstream-only
            // visually: there's no reason for the project to claim this tag, so it shouldn't read as "yours".
            labelColor = hovered || selected ? ROW_TEXT_HOVER_COLOR : ROW_TEXT_COLOR;
        }
        graphics.drawString(font, Component.literal(truncated), x + 12, textY, labelColor, false);

        rowHits.add(new RowHit(x, y, rowRight - x, ROW_HEIGHT, ce.registryKey(), ce.tagId()));
    }

    private void renderCreatePopup(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        graphics.fill(x, y, x + width, y + height, POPUP_DIM_COLOR);

        var popupW = Math.min(280, width - 2 * CONTENT_PADDING);
        var popupH = 64;
        var popupX = x + (width - popupW) / 2;
        var popupY = y + (height - popupH) / 2;
        graphics.fill(popupX, popupY, popupX + popupW, popupY + popupH, POPUP_BG_COLOR);
        graphics.fill(popupX, popupY, popupX + popupW, popupY + 1, POPUP_BORDER_COLOR);
        graphics.fill(popupX, popupY + popupH - 1, popupX + popupW, popupY + popupH, POPUP_BORDER_COLOR);
        graphics.fill(popupX, popupY, popupX + 1, popupY + popupH, POPUP_BORDER_COLOR);
        graphics.fill(popupX + popupW - 1, popupY, popupX + popupW, popupY + popupH, POPUP_BORDER_COLOR);

        var font = EngineFont.get();
        var title = "Create tag in " + createPopup.registryKey;
        graphics.drawString(font, Component.literal(title), popupX + 8, popupY + 6, POPUP_TITLE_COLOR, false);

        var inputY = popupY + 22;
        var inputW = popupW - 16;
        createPopup.input.render(graphics, popupX + 8, inputY, inputW, mouseX, mouseY);

        var parsed = ResourceLocation.tryParse(createPopup.input.content().trim());
        var validationY = inputY + TextInput.HEIGHT + 4;
        if (createPopup.input.content().trim().isEmpty()) {
            graphics.drawString(
                font,
                Component.literal("(type a tag id, e.g. namespace:path)"),
                popupX + 8,
                validationY,
                EMPTY_TEXT_COLOR,
                false
            );
        } else if (parsed == null) {
            graphics.drawString(font, Component.literal("Invalid tag id"), popupX + 8, validationY, POPUP_ERROR_COLOR, false);
        } else {
            graphics.drawString(font, Component.literal("Press Enter to create"), popupX + 8, validationY, EMPTY_TEXT_COLOR, false);
        }
    }

    private static int accentColor(ResourceLocation registryKey) {
        var hash = Math.abs(registryKey.toString().hashCode());
        return ACCENT_PALETTE[hash % ACCENT_PALETTE.length];
    }

    private static @Nullable TagSelectable currentlySelectedTag() {
        var single = SelectionManager.current().single();
        return single instanceof TagSelectable ts ? ts : null;
    }

    private static void requestCatalog(String projectName) {
        BLib.MOD.networking().sendToServer(new C2SRequestTagCatalogPayload(projectName));
    }

    private void openCreatePopup(ResourceLocation registryKey) {
        var projectName = ProjectSession.activeProjectName();
        var defaultPrefix = projectName.isEmpty() ? "" : projectName + ":";
        var input = new TextInput("namespace:path", text -> commitCreate(registryKey, text));
        input.setContent(defaultPrefix);
        input.focus();
        createPopup = new CreateTagPopup(registryKey, input);
    }

    private void commitCreate(ResourceLocation registryKey, String text) {
        var trimmed = text.trim();
        var parsed = ResourceLocation.tryParse(trimmed);
        if (parsed == null) {
            return;
        }
        var projectName = ProjectSession.activeProjectName();
        if (projectName.isEmpty()) {
            return;
        }
        BLib.MOD.networking().sendToServer(new C2SCreateTagPayload(projectName, registryKey, parsed));
        // Mark the new tag as staged so it paints red until Reload Project — it exists on disk but the runtime
        // registry doesn't know about it yet.
        TagStagingCache.markTagEdited(registryKey, parsed);
        // Drive selection to the new tag so the inspector lands on it immediately.
        SelectionManager.selectSingle(new TagSelectable(registryKey, parsed));
        createPopup = null;
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        return scroll.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (createPopup != null) {
            // Modal: forward to the popup's input; clicks elsewhere dismiss.
            if (createPopup.input.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            createPopup = null;
            return true;
        }
        if (searchInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (namespaceInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (projectToggle.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button != 0) {
            return false;
        }
        // Per-section + button takes priority over the section's collapse toggle.
        for (var ch : createHits) {
            if (ch.rect.contains(mouseX, mouseY)) {
                openCreatePopup(ch.registryKey);
                return true;
            }
        }
        // Row click → select (full-row click target; no per-row buttons).
        for (var rh : rowHits) {
            if (mouseX >= rh.x && mouseX < rh.x + rh.w && mouseY >= rh.y && mouseY < rh.y + rh.h) {
                SelectionManager.selectSingle(new TagSelectable(rh.registryKey, rh.tagId));
                return true;
            }
        }
        // Section header click → toggle collapse. Falls through to here only when no row / + button consumed.
        for (var hh : headerHits) {
            if (mouseX >= hh.x && mouseX < hh.x + hh.w && mouseY >= hh.y && mouseY < hh.y + hh.h) {
                if (collapsed.contains(hh.registryKey)) {
                    collapsed.remove(hh.registryKey);
                } else {
                    collapsed.add(hh.registryKey);
                }
                return true;
            }
        }
        return false;
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
        if (mouseX < rectX || mouseX >= rectX + rectWidth || mouseY < rectY || mouseY >= rectY + rectHeight) {
            return false;
        }
        return scroll.mouseScrolled(scrollY);
    }

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

    private record Rect(
        int x,
        int y,
        int w,
        int h
    ) {

        boolean contains(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    private record HeaderHit(
        int x,
        int y,
        int w,
        int h,
        ResourceLocation registryKey
    ) {}

    private record RowHit(
        int x,
        int y,
        int w,
        int h,
        ResourceLocation registryKey,
        ResourceLocation tagId
    ) {}

    private record CreateHit(
        Rect rect,
        ResourceLocation registryKey
    ) {}

    /** Inline modal-within-panel state for the Create-tag popup. */
    private static final class CreateTagPopup {

        final ResourceLocation registryKey;

        final TextInput input;

        CreateTagPopup(ResourceLocation registryKey, TextInput input) {
            this.registryKey = registryKey;
            this.input = input;
        }
    }
}
