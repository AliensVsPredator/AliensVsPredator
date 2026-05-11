package com.blib.engine.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.blib.engine.session.ProjectSession;
import com.blib.engine.tag.RegistryEntriesCache;
import com.blib.engine.tag.TagCatalogCache;
import com.blib.engine.tag.TagDraftCache;
import com.blib.engine.tag.TagSelection;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SAddTagEntryPayload;
import com.blib.mod.common.network.packet.C2SReloadProjectPayload;
import com.blib.mod.common.network.packet.C2SRemoveTagEntryPayload;
import com.blib.mod.common.network.packet.C2SRequestRegistryEntriesPayload;
import com.blib.mod.common.network.packet.C2SRequestTagDraftPayload;
import com.blib.mod.common.network.packet.C2SSetTagReplacePayload;
import com.blib.mod.common.network.packet.TagEntryDraft;

/**
 * Editor for one tag in the active project. Header has a registry-type {@link SearchableSelect}, a tag-id select
 * filtered to the chosen registry, a Merge/Replace toggle, and a Reload button. The body is a scrollable list of entry
 * rows — direct entries (e.g. {@code minecraft:cherry_log}) and tag references (e.g. {@code #minecraft:logs_that_burn})
 * interleaved in array-index order, each with a {@code ×} remove button. The footer is an Add-entry picker whose items
 * combine valid direct entries and existing tag refs for the selected registry; tag refs sort first and display with a
 * {@code #} prefix.
 * <p>
 * Reads from {@link TagDraftCache} (server-pushed authoritative state) so user edits show pre-reload, mirroring how
 * {@link PoolEditorPanel} reads from {@code ProjectDraftCache}. Reload sends the existing
 * {@link C2SReloadProjectPayload} — every per-edit packet has already written through to disk.
 */
@ApiStatus.Internal
public final class TagEditorPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int HEADER_BG_COLOR = 0xFF1F1F26;

    private static final int HEADER_LABEL_COLOR = 0xFF7C8088;

    private static final int ROW_TEXT_COLOR = 0xFFD0D0D0;

    private static final int ROW_HOVER_BG = 0xFF1F1F26;

    private static final int EMPTY_NOTE_COLOR = 0xFF606068;

    private static final int CHIP_TAGREF_COLOR = 0xFF7CB6E0;

    private static final int CHIP_DIRECT_COLOR = 0xFFE6C26B;

    private static final int CONTENT_PADDING = 5;

    private static final int HEADER_BAR_HEIGHT = SearchableSelect.HEIGHT + 4;

    private static final int ROW_HEIGHT = 14;

    private static final int CHIP_WIDTH = 12;

    private static final int RIGHT_PAD = 4;

    private static final int SCROLLBAR_GUTTER = 5;

    private static final int REMOVE_BUTTON_WIDTH = 12;

    private static final int REMOVE_ICON_COLOR = 0xFF7C8088;

    private static final int REMOVE_ICON_HOVER_COLOR = 0xFFFF6868;

    private static final int FOOTER_HEIGHT = SearchableSelect.HEIGHT + 4;

    private static final int RELOAD_BUTTON_WIDTH = 56;

    private static final int RELOAD_BUTTON_HEIGHT = SearchableSelect.HEIGHT;

    private static final int RELOAD_BUTTON_BG = 0xFF14141A;

    private static final int RELOAD_BUTTON_BG_HOVER = 0xFF1A1A22;

    private static final int RELOAD_BUTTON_BORDER = 0xFF353540;

    private static final int RELOAD_BUTTON_BORDER_HOVER = 0xFF4F8FFF;

    private static final int RELOAD_BUTTON_TEXT = 0xFFD0D0D0;

    private static final int RELOAD_BUTTON_DISABLED_TEXT = 0xFF606068;

    private static final int RELOAD_BUTTON_RELOADED_TEXT = 0xFF80E080;

    private static final long RELOADING_FEEDBACK_MS = 200L;

    private static final long RELOADED_FEEDBACK_MS = 2200L;

    private static final int REPLACE_TOGGLE_WIDTH = 78;

    private static final List<String> REPLACE_LABELS = List.of("Merge", "Replace");

    private final SearchableSelect<ResourceLocation> registrySelect = new SearchableSelect<>(
        TagEditorPanel::buildRegistryItems,
        rl -> rl == null ? "(pick registry)" : rl.toString(),
        null,
        rl -> {}
    );

    private final SearchableSelect<ResourceLocation> tagSelect = new SearchableSelect<>(
        this::buildTagItems,
        rl -> rl == null ? "(pick tag)" : rl.toString(),
        null,
        rl -> {}
    );

    private final SegmentedControl replaceToggle = new SegmentedControl(REPLACE_LABELS, 0);

    private final SearchableSelect<TagPickerItem> addEntrySelect = new SearchableSelect<>(
        this::buildAddEntryItems,
        TagPickerItem::displayLabel,
        null,
        item -> {
            if (item != null) {
                commitAdd(item);
            }
        }
    );

    private final ScrollContainer scroll = new ScrollContainer();

    /** Panel rect from the most recent render — needed by mouseClicked for hit-testing the per-row × buttons. */
    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    /** Last (registry, tag) we requested a draft for — drift triggers a re-fetch. */
    private @Nullable ResourceLocation lastShownRegistry;

    private @Nullable ResourceLocation lastShownTag;

    /** Last registry we asked for entries — drift triggers a re-fetch (so the picker has fresh choices). */
    private @Nullable ResourceLocation lastFetchedRegistryEntries;

    /** Cached entries for the currently-rendered tag. Refreshed every frame from {@link TagDraftCache}. */
    private List<TagEntryDraft> cachedEntries = List.of();

    /** When > 0, drives the Reload button's "Reloading…" → "✓ Reloaded" → idle state machine. */
    private long lastReloadAttemptMs;

    @Override
    public String title() {
        return "Tag Editor";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Consume any pending tag-open request from the browser. One-shot — applying drives the existing drift-check
        // on the next paragraph so the body re-loads naturally without a parallel reload path.
        var requested = TagSelection.requested();
        if (requested != null) {
            if (!requested.registryKey().equals(registrySelect.currentValue())) {
                registrySelect.setCurrentValue(requested.registryKey());
            }
            if (!requested.tagId().equals(tagSelect.currentValue())) {
                tagSelect.setCurrentValue(requested.tagId());
            }
            TagSelection.clear();
        }

        renderHeader(graphics, x, y, width, mouseX, mouseY);

        var bodyY = y + HEADER_BAR_HEIGHT;
        var bodyHeight = Math.max(0, height - HEADER_BAR_HEIGHT - FOOTER_HEIGHT);
        renderBody(graphics, x, bodyY, width, bodyHeight, mouseX, mouseY);

        var footerY = y + height - FOOTER_HEIGHT;
        renderFooter(graphics, x, footerY, width, mouseX, mouseY);
    }

    private void renderHeader(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        graphics.fill(x, y, x + width, y + HEADER_BAR_HEIGHT, HEADER_BG_COLOR);

        var font = Minecraft.getInstance().font;
        var selectY = y + (HEADER_BAR_HEIGHT - SearchableSelect.HEIGHT) / 2;

        // Layout (left → right): "Type" label + select, "Tag" label + select, Replace toggle, Reload button.
        var typeLabelText = "Type";
        var typeLabelWidth = font.width(typeLabelText);
        var labelTextY = selectY + (SearchableSelect.HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(typeLabelText), x + CONTENT_PADDING, labelTextY, HEADER_LABEL_COLOR, false);

        var reloadX = x + width - CONTENT_PADDING - RELOAD_BUTTON_WIDTH;
        var replaceX = reloadX - 6 - REPLACE_TOGGLE_WIDTH;

        // Half the available middle space goes to the registry select, half to the tag select.
        var middleStart = x + CONTENT_PADDING + typeLabelWidth + 6;
        var middleEnd = replaceX - 6;
        var tagLabelText = "Tag";
        var tagLabelWidth = font.width(tagLabelText);
        var middleSpan = Math.max(0, middleEnd - middleStart - tagLabelWidth - 12);
        var registryW = middleSpan / 2;
        var tagW = middleSpan - registryW;

        registrySelect.render(graphics, middleStart, selectY, registryW, mouseX, mouseY);

        var tagLabelX = middleStart + registryW + 6;
        graphics.drawString(font, Component.literal(tagLabelText), tagLabelX, labelTextY, HEADER_LABEL_COLOR, false);
        tagSelect.render(graphics, tagLabelX + tagLabelWidth + 6, selectY, tagW, mouseX, mouseY);

        replaceToggle.render(graphics, replaceX, selectY, REPLACE_TOGGLE_WIDTH, mouseX, mouseY);

        renderReloadButton(graphics, reloadX, selectY, mouseX, mouseY);
    }

    private void renderReloadButton(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        var enabled = ProjectSession.activeProject() != null;
        var hovered = enabled
            && mouseX >= x
            && mouseX < x + RELOAD_BUTTON_WIDTH
            && mouseY >= y
            && mouseY < y + RELOAD_BUTTON_HEIGHT;

        var bg = hovered ? RELOAD_BUTTON_BG_HOVER : RELOAD_BUTTON_BG;
        var border = hovered ? RELOAD_BUTTON_BORDER_HOVER : RELOAD_BUTTON_BORDER;
        graphics.fill(x, y, x + RELOAD_BUTTON_WIDTH, y + RELOAD_BUTTON_HEIGHT, bg);
        graphics.fill(x, y, x + RELOAD_BUTTON_WIDTH, y + 1, border);
        graphics.fill(x, y + RELOAD_BUTTON_HEIGHT - 1, x + RELOAD_BUTTON_WIDTH, y + RELOAD_BUTTON_HEIGHT, border);
        graphics.fill(x, y, x + 1, y + RELOAD_BUTTON_HEIGHT, border);
        graphics.fill(x + RELOAD_BUTTON_WIDTH - 1, y, x + RELOAD_BUTTON_WIDTH, y + RELOAD_BUTTON_HEIGHT, border);

        var font = Minecraft.getInstance().font;
        var label = reloadButtonLabel();
        var color = !enabled
            ? RELOAD_BUTTON_DISABLED_TEXT
            : (label.startsWith("✓") ? RELOAD_BUTTON_RELOADED_TEXT : RELOAD_BUTTON_TEXT);
        var labelWidth = font.width(label);
        var textX = x + (RELOAD_BUTTON_WIDTH - labelWidth) / 2;
        var textY = y + (RELOAD_BUTTON_HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), textX, textY, color, false);
    }

    private String reloadButtonLabel() {
        if (lastReloadAttemptMs <= 0) {
            return "Reload";
        }
        var elapsed = System.currentTimeMillis() - lastReloadAttemptMs;
        if (elapsed < RELOADING_FEEDBACK_MS) {
            return "Reloading…";
        }
        if (elapsed < RELOADED_FEEDBACK_MS) {
            return "✓ Reloaded";
        }
        return "Reload";
    }

    private void renderBody(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        if (ProjectSession.activeProject() == null) {
            drawCenteredNote(graphics, x, y, width, height, "(no project open — File → Open Project)");
            return;
        }
        var registryKey = registrySelect.currentValue();
        var tagId = tagSelect.currentValue();
        if (registryKey == null) {
            drawCenteredNote(graphics, x, y, width, height, "(pick a registry)");
            return;
        }

        // Registry change → fetch its element catalog (one-shot per registry per session).
        if (!registryKey.equals(lastFetchedRegistryEntries) && RegistryEntriesCache.get(registryKey) == null) {
            BLib.MOD.networking().sendToServer(new C2SRequestRegistryEntriesPayload(ProjectSession.activeProjectName(), registryKey));
            lastFetchedRegistryEntries = registryKey;
        }

        if (tagId == null) {
            drawCenteredNote(graphics, x, y, width, height, "(pick a tag)");
            return;
        }

        // Tag change (or registry change without tag-id-still-valid) → reset scroll + refetch the draft.
        if (!registryKey.equals(lastShownRegistry) || !tagId.equals(lastShownTag)) {
            lastShownRegistry = registryKey;
            lastShownTag = tagId;
            scroll.reset();
            BLib.MOD.networking()
                .sendToServer(new C2SRequestTagDraftPayload(ProjectSession.activeProjectName(), registryKey, tagId));
        }

        var draft = TagDraftCache.get(registryKey, tagId);
        if (draft != null) {
            cachedEntries = draft.entries();
            // Sync the Replace toggle from the cached draft. (Not user-driven via mouse; mouseClicked handles changes.)
            replaceToggle.setSelectedIndex(draft.replace() ? 1 : 0);
        } else {
            cachedEntries = List.of();
        }

        if (cachedEntries.isEmpty()) {
            drawCenteredNote(graphics, x, y, width, height, "(no entries)");
            return;
        }

        var contentHeight = cachedEntries.size() * ROW_HEIGHT;
        scroll.layout(height, contentHeight);

        applyRawScissor(graphics, x, y, width, height);
        try {
            var scrollY = (int) scroll.scrollY();
            var firstVisible = Math.max(0, scrollY / ROW_HEIGHT);
            var lastVisible = Math.min(cachedEntries.size() - 1, (scrollY + height) / ROW_HEIGHT);
            for (var i = firstVisible; i <= lastVisible; i++) {
                var rowY = y + i * ROW_HEIGHT - scrollY;
                renderRow(graphics, cachedEntries.get(i), x, rowY, width, mouseX, mouseY);
            }
        } finally {
            graphics.flush();
            RenderSystem.disableScissor();
        }

        scroll.renderScrollbar(graphics, x, y, width, height, mouseX, mouseY);
    }

    private void renderRow(GuiGraphics graphics, TagEntryDraft entry, int x, int y, int width, int mouseX, int mouseY) {
        var hovered = mouseY >= y
            && mouseY < y + ROW_HEIGHT
            && mouseX >= x
            && mouseX < x + width - SCROLLBAR_GUTTER;
        if (hovered) {
            graphics.fill(x, y, x + width - SCROLLBAR_GUTTER, y + ROW_HEIGHT, ROW_HOVER_BG);
        }

        var font = Minecraft.getInstance().font;
        var rightEdge = x + width - SCROLLBAR_GUTTER - RIGHT_PAD;
        var removeX = rightEdge - REMOVE_BUTTON_WIDTH;

        // Chip ("#" for tag-ref, "▪" for direct).
        var chipText = entry.isTagRef() ? "#" : "▪";
        var chipColor = entry.isTagRef() ? CHIP_TAGREF_COLOR : CHIP_DIRECT_COLOR;
        var chipX = x + CONTENT_PADDING;
        var textY = y + (ROW_HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(chipText), chipX, textY, chipColor, false);

        // Id text. Tag-refs are displayed with a leading # in the label (so the row reads like the JSON form).
        var idLabel = (entry.isTagRef() ? "#" : "") + entry.id().toString() + (entry.required() ? "" : "  (opt)");
        var idTextX = chipX + CHIP_WIDTH;
        var idMaxWidth = Math.max(0, removeX - idTextX - 6);
        var truncated = font.plainSubstrByWidth(idLabel, idMaxWidth);
        graphics.drawString(font, Component.literal(truncated), idTextX, textY, ROW_TEXT_COLOR, false);

        // × remove button.
        var removeHovered = mouseX >= removeX
            && mouseX < removeX + REMOVE_BUTTON_WIDTH
            && mouseY >= y
            && mouseY < y + ROW_HEIGHT;
        var removeColor = removeHovered ? REMOVE_ICON_HOVER_COLOR : REMOVE_ICON_COLOR;
        var removeText = "×";
        var removeTextWidth = font.width(removeText);
        var removeTextX = removeX + (REMOVE_BUTTON_WIDTH - removeTextWidth) / 2;
        graphics.drawString(font, Component.literal(removeText), removeTextX, textY, removeColor, false);
    }

    private void renderFooter(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        graphics.fill(x, y, x + width, y + FOOTER_HEIGHT, HEADER_BG_COLOR);

        var ready = registrySelect.currentValue() != null && tagSelect.currentValue() != null;
        if (!ready) {
            var font = Minecraft.getInstance().font;
            var note = "(pick a registry + tag to enable adding)";
            var textY = y + (FOOTER_HEIGHT - font.lineHeight + 2) / 2;
            graphics.drawString(font, Component.literal(note), x + CONTENT_PADDING, textY, EMPTY_NOTE_COLOR, false);
            return;
        }
        var selectY = y + (FOOTER_HEIGHT - SearchableSelect.HEIGHT) / 2;
        var selectX = x + CONTENT_PADDING;
        var selectW = Math.max(0, width - 2 * CONTENT_PADDING);
        addEntrySelect.render(graphics, selectX, selectY, selectW, mouseX, mouseY);
    }

    private void commitAdd(TagPickerItem item) {
        var registryKey = registrySelect.currentValue();
        var tagId = tagSelect.currentValue();
        if (registryKey == null || tagId == null || ProjectSession.activeProject() == null) {
            return;
        }
        BLib.MOD.networking()
            .sendToServer(
                new C2SAddTagEntryPayload(
                    ProjectSession.activeProjectName(),
                    registryKey,
                    tagId,
                    item.isTagRef(),
                    item.id(),
                    true
                )
            );
        addEntrySelect.setCurrentValue(null);
    }

    private void commitRemove(int rawIndex) {
        var registryKey = registrySelect.currentValue();
        var tagId = tagSelect.currentValue();
        if (registryKey == null || tagId == null || ProjectSession.activeProject() == null) {
            return;
        }
        BLib.MOD.networking()
            .sendToServer(new C2SRemoveTagEntryPayload(ProjectSession.activeProjectName(), registryKey, tagId, rawIndex));
    }

    private void commitSetReplace(boolean replace) {
        var registryKey = registrySelect.currentValue();
        var tagId = tagSelect.currentValue();
        if (registryKey == null || tagId == null || ProjectSession.activeProject() == null) {
            return;
        }
        BLib.MOD.networking()
            .sendToServer(new C2SSetTagReplacePayload(ProjectSession.activeProjectName(), registryKey, tagId, replace));
    }

    private static void drawCenteredNote(GuiGraphics graphics, int x, int y, int width, int height, String text) {
        var font = Minecraft.getInstance().font;
        var textWidth = font.width(text);
        var noteX = x + (width - textWidth) / 2;
        var noteY = y + (height - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(text), noteX, noteY, EMPTY_NOTE_COLOR, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (registrySelect.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (tagSelect.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (addEntrySelect.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        // Replace toggle.
        var indexBefore = replaceToggle.selectedIndex();
        if (replaceToggle.mouseClicked(mouseX, mouseY, button)) {
            if (replaceToggle.selectedIndex() != indexBefore) {
                commitSetReplace(replaceToggle.selectedIndex() == 1);
            }
            return true;
        }
        if (handleReloadButtonClick(mouseX, mouseY, button)) {
            return true;
        }
        if (scroll.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        // Per-row × remove buttons.
        var removeIndex = findRemoveClickedRawIndex(mouseX, mouseY, button);
        if (removeIndex != null) {
            commitRemove(removeIndex);
            return true;
        }
        return false;
    }

    private boolean handleReloadButtonClick(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (ProjectSession.activeProject() == null) {
            return false;
        }
        var reloadX = rectX + rectWidth - CONTENT_PADDING - RELOAD_BUTTON_WIDTH;
        var reloadY = rectY + (HEADER_BAR_HEIGHT - RELOAD_BUTTON_HEIGHT) / 2;
        if (mouseX < reloadX || mouseX >= reloadX + RELOAD_BUTTON_WIDTH) {
            return false;
        }
        if (mouseY < reloadY || mouseY >= reloadY + RELOAD_BUTTON_HEIGHT) {
            return false;
        }
        BLib.MOD.networking().sendToServer(new C2SReloadProjectPayload(ProjectSession.activeProjectName()));
        lastReloadAttemptMs = System.currentTimeMillis();
        return true;
    }

    private @Nullable Integer findRemoveClickedRawIndex(double mouseX, double mouseY, int button) {
        if (button != 0 || cachedEntries.isEmpty()) {
            return null;
        }
        var bodyY = rectY + HEADER_BAR_HEIGHT;
        var bodyHeight = Math.max(0, rectHeight - HEADER_BAR_HEIGHT - FOOTER_HEIGHT);
        if (mouseY < bodyY || mouseY >= bodyY + bodyHeight) {
            return null;
        }
        var removeX = rectX + rectWidth - SCROLLBAR_GUTTER - RIGHT_PAD - REMOVE_BUTTON_WIDTH;
        if (mouseX < removeX || mouseX >= removeX + REMOVE_BUTTON_WIDTH) {
            return null;
        }
        var scrollY = (int) scroll.scrollY();
        var rowAtClick = (int) ((mouseY - bodyY + scrollY) / ROW_HEIGHT);
        if (rowAtClick < 0 || rowAtClick >= cachedEntries.size()) {
            return null;
        }
        return cachedEntries.get(rowAtClick).rawIndex();
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
        scroll.reset();
    }

    private static List<SearchableSelect.Item<ResourceLocation>> buildRegistryItems() {
        return TagCatalogCache.groupedByRegistry()
            .keySet()
            .stream()
            .sorted(Comparator.comparing(ResourceLocation::toString))
            .map(rl -> new SearchableSelect.Item<>(rl, rl.toString()))
            .toList();
    }

    private List<SearchableSelect.Item<ResourceLocation>> buildTagItems() {
        var registryKey = registrySelect.currentValue();
        if (registryKey == null) {
            return List.of();
        }
        var entries = TagCatalogCache.groupedByRegistry().get(registryKey);
        if (entries == null) {
            return List.of();
        }
        return entries.stream()
            .map(e -> new SearchableSelect.Item<>(e.tagId(), e.tagId().toString()))
            .toList();
    }

    /**
     * Build the Add-entry picker items. Tag refs first (sorted), then direct entries (sorted). Display labels carry the
     * {@code #} prefix on tag-refs so the row reads like the JSON form. Returns an empty list if the registry's entry
     * catalog hasn't arrived yet — the user sees an empty popup until the server replies.
     */
    private List<SearchableSelect.Item<TagPickerItem>> buildAddEntryItems() {
        var registryKey = registrySelect.currentValue();
        if (registryKey == null) {
            return List.of();
        }
        var cache = RegistryEntriesCache.get(registryKey);
        if (cache == null) {
            return List.of();
        }
        var out = new ArrayList<SearchableSelect.Item<TagPickerItem>>(cache.elements().size() + cache.tagIds().size());
        var sortedTags = new ArrayList<>(cache.tagIds());
        sortedTags.sort(Comparator.comparing(ResourceLocation::toString));
        for (var rl : sortedTags) {
            out.add(new SearchableSelect.Item<>(new TagPickerItem(true, rl), "#" + rl.toString()));
        }
        var sortedElements = new ArrayList<>(cache.elements());
        sortedElements.sort(Comparator.comparing(ResourceLocation::toString));
        for (var rl : sortedElements) {
            out.add(new SearchableSelect.Item<>(new TagPickerItem(false, rl), rl.toString()));
        }
        return out;
    }

    /** Item type for the Add-entry picker — distinguishes tag-refs from direct entries. */
    public record TagPickerItem(
        boolean isTagRef,
        ResourceLocation id
    ) {

        public String displayLabel() {
            return (isTagRef ? "#" : "") + id.toString();
        }
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
}
