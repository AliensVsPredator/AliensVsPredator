package com.blib.engine.ui.panel.details;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.picking.TagSelectable;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.tag.RegistryEntriesCache;
import com.blib.engine.tag.TagDraftCache;
import com.blib.engine.tag.TagStagingCache;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.widget.ScrollContainer;
import com.blib.engine.ui.widget.SearchableSelect;
import com.blib.engine.ui.widget.SegmentedControl;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SAddTagEntryPayload;
import com.blib.mod.common.network.packet.C2SRemoveTagEntryPayload;
import com.blib.mod.common.network.packet.C2SRequestRegistryEntriesPayload;
import com.blib.mod.common.network.packet.C2SRequestTagDraftPayload;
import com.blib.mod.common.network.packet.C2SSetTagEntryRequiredPayload;
import com.blib.mod.common.network.packet.C2SSetTagReplacePayload;
import com.blib.mod.common.network.packet.TagEntryDraft;

/**
 * Inspector section for {@link TagSelectable}. Surfaces the project's editable view of a tag plus a read-only Resolved
 * preview of the merged registry result. Supports per-entry required-flag toggle, per-entry remove, Add-entry footer
 * picker, and a Merge/Replace mode toggle (Replace gates behind a destructive-confirm dialog).
 */
@ApiStatus.Internal
public final class TagInspectorSection implements InspectorSection<TagSelectable> {

    private static final int TAG_REPLACE_TOGGLE_WIDTH = 78;

    private static final int TAG_VIEW_TOGGLE_WIDTH = 96;

    private static final int TAG_ROW_HEIGHT = 12;

    private static final int TAG_CHIP_WIDTH = 10;

    private static final int TAG_REMOVE_BUTTON_WIDTH = 12;

    private static final int TAG_REQUIRED_BADGE_WIDTH = 20;

    private static final int TAG_REQUIRED_BADGE_REQ_COLOR = 0xFF606068;

    private static final int TAG_REQUIRED_BADGE_REQ_HOVER = 0xFFB0B0B8;

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

    private final DetailsPanel panel;

    private final SegmentedControl tagReplaceToggle = new SegmentedControl(List.of("Merge", "Replace"), 0);

    private final SegmentedControl tagViewToggle = new SegmentedControl(List.of("Resolved", "Source"), 0);

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

    private List<TagEntryDraft> tagCachedEntries = List.of();

    private @Nullable ResourceLocation tagLastShownRegistry;

    private @Nullable ResourceLocation tagLastShownTag;

    private @Nullable ResourceLocation tagLastFetchedRegistryEntries;

    private final List<TagRemoveHit> tagRemoveHits = new ArrayList<>();

    private final List<TagRequiredToggleHit> tagRequiredToggleHits = new ArrayList<>();

    public TagInspectorSection(DetailsPanel panel) {
        this.panel = panel;
    }

    @Override
    public String id() {
        return "tag";
    }

    @Override
    public Class<TagSelectable> selectableType() {
        return TagSelectable.class;
    }

    /** Called each frame from {@link DetailsPanel#render} so stale hit-rects don't fire after the selection swaps. */
    void resetHitRects() {
        tagRemoveHits.clear();
        tagRequiredToggleHits.clear();
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, TagSelectable target, int mouseX, int mouseY) {
        return renderBody(graphics, EngineFont.get(), x, y, width, mouseX, mouseY, target);
    }

    private int renderBody(GuiGraphics graphics, Font font, int x, int y, int width, int mouseX, int mouseY, TagSelectable tag) {
        var registryKey = tag.registryKey();
        var tagId = tag.tagId();

        if (!registryKey.equals(tagLastFetchedRegistryEntries) && RegistryEntriesCache.get(registryKey) == null) {
            BLib.MOD.networking().sendToServer(new C2SRequestRegistryEntriesPayload(ProjectSession.activeProjectName(), registryKey));
            tagLastFetchedRegistryEntries = registryKey;
        }

        if (!registryKey.equals(tagLastShownRegistry) || !tagId.equals(tagLastShownTag)) {
            tagLastShownRegistry = registryKey;
            tagLastShownTag = tagId;
            tagScroll.reset();
            BLib.MOD.networking().sendToServer(new C2SRequestTagDraftPayload(ProjectSession.activeProjectName(), registryKey, tagId));
        }

        var draft = TagDraftCache.get(registryKey, tagId);
        var resolvedMembers = draft != null ? draft.resolvedMembers() : List.<ResourceLocation>of();
        if (draft != null) {
            tagCachedEntries = draft.entries();
            tagReplaceToggle.setSelectedIndex(draft.replace() ? 1 : 0);
        } else {
            tagCachedEntries = List.of();
        }

        var rowY = y + InspectorStyle.ROW_GAP;
        graphics.drawString(
            font,
            Component.literal(registryKey.toString()),
            x + InspectorStyle.CONTENT_PADDING,
            rowY,
            TAG_REGISTRY_LABEL_COLOR,
            false
        );
        rowY += InspectorStyle.LINE_HEIGHT + InspectorStyle.ROW_GAP;

        var toolbarY = rowY;
        var viewX = x + InspectorStyle.CONTENT_PADDING;
        var replaceX = viewX + TAG_VIEW_TOGGLE_WIDTH + 4;
        tagViewToggle.render(graphics, viewX, toolbarY, TAG_VIEW_TOGGLE_WIDTH, mouseX, mouseY);
        tagReplaceToggle.render(graphics, replaceX, toolbarY, TAG_REPLACE_TOGGLE_WIDTH, mouseX, mouseY);
        rowY += TAG_TOOLBAR_HEIGHT;

        var sourceMode = tagViewToggle.selectedIndex() == 1;
        var bodyY = rowY;
        var panelBottom = panel.rectY + panel.rectHeight;
        var footerY = sourceMode ? (panelBottom - TAG_FOOTER_HEIGHT) : panelBottom;
        var bodyHeight = Math.max(0, footerY - bodyY);
        var listX = x + InspectorStyle.CONTENT_PADDING;
        var listW = width - 2 * InspectorStyle.CONTENT_PADDING;

        if (sourceMode) {
            renderTagSourceList(graphics, font, listX, bodyY, listW, bodyHeight, mouseX, mouseY);
            var footerSelectY = footerY + (TAG_FOOTER_HEIGHT - SearchableSelect.HEIGHT) / 2;
            tagAddEntrySelect.render(graphics, listX, footerSelectY, listW, mouseX, mouseY);
        } else {
            renderTagResolvedList(graphics, font, listX, bodyY, listW, bodyHeight, resolvedMembers, mouseX, mouseY);
        }
        // Tag content fills to the bottom of the panel (with an optional footer area in source mode); report that as
        // the consumed height so any chained sections register as effectively below the panel and don't ghost into
        // the scrollable list area.
        return panelBottom;
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
            DetailsPanel.drawCenteredNote(graphics, font, listX, bodyY, listW, bodyHeight, "(no project entries — add one below)");
            return;
        }
        var contentHeight = tagCachedEntries.size() * TAG_ROW_HEIGHT;
        tagScroll.layout(bodyHeight, contentHeight);
        DetailsPanel.applyRawScissor(graphics, listX, bodyY, listW, bodyHeight);
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
            DetailsPanel.drawCenteredNote(graphics, font, listX, bodyY, listW, bodyHeight, "(tag is empty in the live registry)");
            return;
        }
        var contentHeight = resolvedMembers.size() * TAG_ROW_HEIGHT;
        tagScroll.layout(bodyHeight, contentHeight);
        DetailsPanel.applyRawScissor(graphics, listX, bodyY, listW, bodyHeight);
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
                graphics.drawString(font, Component.literal("▪"), listX + 2, textY, TAG_CHIP_DIRECT_COLOR, false);
                var label = font.plainSubstrByWidth(member.toString(), labelMax);
                graphics.drawString(font, Component.literal(label), listX + 2 + TAG_CHIP_WIDTH, textY, InspectorStyle.VALUE_COLOR, false);
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
        var requiredBadgeX = requiredOperable ? (removeX - 2 - TAG_REQUIRED_BADGE_WIDTH) : removeX;
        var labelRight = removeOperable ? requiredBadgeX : rightEdge;
        var textY = y + (TAG_ROW_HEIGHT - font.lineHeight + 2) / 2;

        var chip = entry.isTagRef() ? "#" : "▪";
        graphics.drawString(
            font,
            Component.literal(chip),
            x + 2,
            textY,
            entry.isTagRef() ? TAG_CHIP_TAGREF_COLOR : TAG_CHIP_DIRECT_COLOR,
            false
        );

        var label = (entry.isTagRef() ? "#" : "") + entry.id();
        var labelX = x + 2 + TAG_CHIP_WIDTH;
        var labelMax = Math.max(0, labelRight - labelX - 4);
        var labelColor = entryLabelColor(entry);
        graphics.drawString(font, Component.literal(font.plainSubstrByWidth(label, labelMax)), labelX, textY, labelColor, false);

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

    private int entryLabelColor(TagEntryDraft entry) {
        var registryKey = tagLastShownRegistry;
        var tagId = tagLastShownTag;
        if (registryKey == null || tagId == null) {
            return InspectorStyle.VALUE_COLOR;
        }
        if (TagStagingCache.isEntryStagedAdd(registryKey, tagId, entry.isTagRef(), entry.id())) {
            return BlockInspectorSection.BLOCK_TAG_LABEL_STAGED;
        }
        var draft = TagDraftCache.get(registryKey, tagId);
        var replaceMode = draft != null && draft.replace();
        if (!replaceMode && entry.inUpstream()) {
            return BlockInspectorSection.BLOCK_TAG_LABEL_UPSTREAM;
        }
        var inProject = BlockInspectorSection.isTagInProject(registryKey, tagId);
        var inUpstream = BlockInspectorSection.isTagInUpstream(registryKey, tagId);
        var equivalentToUpstream = BlockInspectorSection.isTagEquivalentToUpstream(registryKey, tagId);
        if (inProject && !equivalentToUpstream && inUpstream) {
            return BlockInspectorSection.BLOCK_TAG_LABEL_PROJECT_MODIFIED;
        }
        if (inProject && !equivalentToUpstream) {
            return BlockInspectorSection.BLOCK_TAG_LABEL_PROJECT_NEW;
        }
        return InspectorStyle.VALUE_COLOR;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, TagSelectable tag) {
        var viewIndexBefore = tagViewToggle.selectedIndex();
        if (tagViewToggle.mouseClicked(mouseX, mouseY, button)) {
            if (tagViewToggle.selectedIndex() != viewIndexBefore) {
                tagScroll.reset();
            }
            return true;
        }
        if (tagAddEntrySelect.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        var indexBefore = tagReplaceToggle.selectedIndex();
        if (tagReplaceToggle.mouseClicked(mouseX, mouseY, button)) {
            if (tagReplaceToggle.selectedIndex() != indexBefore) {
                var nextIsReplace = tagReplaceToggle.selectedIndex() == 1;
                var actionHandler = panel.actionHandler();
                if (nextIsReplace && actionHandler != null) {
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

    boolean mouseDragged(double mouseX, double mouseY, int button) {
        return tagScroll.mouseDragged(mouseX, mouseY, button);
    }

    boolean mouseReleased(double mouseX, double mouseY, int button) {
        return tagScroll.mouseReleased(mouseX, mouseY, button);
    }

    boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (
            mouseX < panel.rectX || mouseX >= panel.rectX + panel.rectWidth || mouseY < panel.rectY || mouseY >= panel.rectY
                + panel.rectHeight
        ) {
            return false;
        }
        return tagScroll.mouseScrolled(scrollY);
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

    /** Item type for the Add-entry picker — distinguishes tag-refs from direct entries. */
    public record TagPickerItem(
        boolean isTagRef,
        ResourceLocation id
    ) {

        public String displayLabel() {
            return (isTagRef ? "#" : "") + id;
        }
    }

    private record TagRemoveHit(
        int x,
        int y,
        int w,
        int h,
        int rawIndex
    ) {}

    private record TagRequiredToggleHit(
        int x,
        int y,
        int w,
        int h,
        int rawIndex,
        boolean currentRequired
    ) {}
}
