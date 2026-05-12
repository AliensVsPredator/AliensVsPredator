package com.blib.engine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.blib.engine.input.Input;
import com.blib.engine.input.Keybinding;
import com.blib.engine.input.KeybindingProfile;
import com.blib.engine.input.KeybindingProfileCatalog;
import com.blib.engine.input.Keybindings;

/**
 * Preferences modal — keybinding editor backed by named, persisted {@link KeybindingProfile}s. Apply/Cancel semantics:
 * edits stage in {@link #stagedOverrides} until the user clicks Apply (writes to disk + refreshes
 * {@link com.blib.engine.input.ActiveKeybindings}). Cancel discards.
 * <p>
 * Conflict policy: blocks within the same category prefix (the text before the first {@code .} in the binding id) to
 * preserve the engine's cross-category overlaps (e.g. {@code T} can be both {@code jigsaw.cycle_mode} and
 * {@code gizmo.translate} since context-disambiguates them). The chip refuses the rebind and surfaces an inline error
 * for {@link #CONFLICT_DISPLAY_TICKS} frames.
 * <p>
 * Lifecycle mirrors {@link ManageLayoutsDialog}: the host (EngineWorkspaceScreen) owns the field, dispatches input
 * through it before panels, and clears it via the onClose callback.
 */
@ApiStatus.Internal
public final class PreferencesDialog {

    private static final int DIM_COLOR = 0x80000000;

    private static final int BG_COLOR = 0xFF1F1F26;

    private static final int PANEL_BG_COLOR = 0xFF14141A;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int TITLE_COLOR = 0xFFE6C26B;

    private static final int LABEL_COLOR = 0xFFD0D0D0;

    private static final int META_COLOR = 0xFF808088;

    private static final int ERROR_COLOR = 0xFFE06868;

    private static final int MODIFIED_COLOR = 0xFFE6C26B;

    private static final int ROW_HOVER_BG = 0xFF1A1A22;

    private static final int CATEGORY_ACTIVE_BG = 0xFF22222C;

    private static final int BUTTON_BG = 0xFF14141A;

    private static final int BUTTON_BG_HOVER = 0xFF22222C;

    private static final int BUTTON_BG_DISABLED = 0xFF101013;

    private static final int BUTTON_BORDER = 0xFF353540;

    private static final int BUTTON_TEXT = 0xFFD0D0D0;

    private static final int BUTTON_DISABLED_TEXT = 0xFF606068;

    private static final int BOX_WIDTH = 560;

    private static final int BOX_HEIGHT = 420;

    private static final int BOX_PAD_X = 14;

    private static final int BOX_PAD_Y = 12;

    private static final int HEADER_HEIGHT = 16;

    private static final int CONTROL_ROW_HEIGHT = 14;

    private static final int CONTROL_ROW_GAP = 6;

    private static final int CATEGORY_RAIL_WIDTH = 130;

    private static final int CATEGORY_ROW_HEIGHT = 16;

    private static final int RAIL_PAD = 8;

    private static final int ROW_HEIGHT = 18;

    private static final int ROW_PAD_X = 8;

    private static final int CHIP_WIDTH = 100;

    private static final int RESET_BUTTON_WIDTH = 18;

    private static final int FOOTER_BUTTON_HEIGHT = 14;

    private static final int FOOTER_BUTTON_WIDTH = 80;

    private static final int FOOTER_BUTTON_GAP = 8;

    private static final int CONFLICT_DISPLAY_TICKS = 120; // ~6 seconds @ 20 fps if render fires that fast.

    private final Runnable onClose;

    private final Runnable onNewProfile;

    private final java.util.function.Consumer<KeybindingProfile> onRenameProfile;

    private final java.util.function.Consumer<KeybindingProfile> onDuplicateProfile;

    private final java.util.function.Consumer<KeybindingProfile> onDeleteProfile;

    private String activeProfileId;

    private Map<String, Input> stagedOverrides;

    private List<KeybindingProfile> profiles;

    private final TextInput searchInput;

    private @Nullable String selectedCategory;

    private @Nullable KeyCaptureWidget capturingChip;

    private @Nullable String capturingBindingId;

    private @Nullable String inlineError;

    private int inlineErrorTicks;

    private double scrollOffset;

    private int visibleRowsHeight;

    private int contentHeight;

    // Per-frame click targets — recomputed each render so we don't track them between frames.
    private final List<RowRect> rowRects = new ArrayList<>();

    private final List<CategoryRect> categoryRects = new ArrayList<>();

    private @Nullable Rect profileDropdownRect;

    private @Nullable Rect newButtonRect;

    private @Nullable Rect renameButtonRect;

    private @Nullable Rect duplicateButtonRect;

    private @Nullable Rect deleteButtonRect;

    private @Nullable Rect cancelButtonRect;

    private @Nullable Rect applyButtonRect;

    private @Nullable Rect okButtonRect;

    private @Nullable DropdownMenu profileMenu;

    public PreferencesDialog(
        Runnable onClose,
        Runnable onNewProfile,
        java.util.function.Consumer<KeybindingProfile> onRenameProfile,
        java.util.function.Consumer<KeybindingProfile> onDuplicateProfile,
        java.util.function.Consumer<KeybindingProfile> onDeleteProfile
    ) {
        this.onClose = onClose;
        this.onNewProfile = onNewProfile;
        this.onRenameProfile = onRenameProfile;
        this.onDuplicateProfile = onDuplicateProfile;
        this.onDeleteProfile = onDeleteProfile;
        var current = KeybindingProfileCatalog.getActive();
        this.activeProfileId = current.id();
        this.stagedOverrides = new LinkedHashMap<>(current.overrides());
        this.profiles = KeybindingProfileCatalog.listAll();
        this.searchInput = new TextInput("Search bindings…");
    }

    /** Re-read profiles from the catalog. Call after profile create/rename/duplicate/delete completes. */
    public void refreshProfiles() {
        this.profiles = KeybindingProfileCatalog.listAll();
    }

    /** Switch to a different profile, discarding any staged edits silently (host should confirm-discard upstream). */
    public void loadProfile(String id) {
        var profile = KeybindingProfileCatalog.get(id);
        if (profile == null) {
            profile = KeybindingProfile.defaultProfile();
        }
        this.activeProfileId = profile.id();
        this.stagedOverrides = new LinkedHashMap<>(profile.overrides());
        this.searchInput.setContent("");
        this.selectedCategory = null;
        this.scrollOffset = 0;
        cancelCapture();
    }

    public boolean hasUnsavedChanges() {
        var disk = KeybindingProfileCatalog.get(activeProfileId);
        var diskOverrides = disk != null ? disk.overrides() : Map.<String, Input>of();
        return !stagedOverrides.equals(diskOverrides);
    }

    public void render(GuiGraphics graphics, int screenWidth, int screenHeight, int mouseX, int mouseY) {
        if (inlineErrorTicks > 0) {
            inlineErrorTicks--;
            if (inlineErrorTicks == 0) {
                inlineError = null;
            }
        }

        graphics.fill(0, 0, screenWidth, screenHeight, DIM_COLOR);

        var boxX = (screenWidth - BOX_WIDTH) / 2;
        var boxY = (screenHeight - BOX_HEIGHT) / 2;

        graphics.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + BOX_HEIGHT, BG_COLOR);
        drawBorder(graphics, boxX, boxY, BOX_WIDTH, BOX_HEIGHT, BORDER_COLOR);

        var font = EngineFont.get();

        // Title row + modified indicator.
        var title = "Preferences — Keybindings";
        graphics.drawString(font, Component.literal(title), boxX + BOX_PAD_X, boxY + BOX_PAD_Y, TITLE_COLOR, false);
        if (hasUnsavedChanges()) {
            var modifiedX = boxX + BOX_PAD_X + font.width(title) + 8;
            graphics.drawString(font, Component.literal("● modified"), modifiedX, boxY + BOX_PAD_Y, MODIFIED_COLOR, false);
        }

        // Profile-management control row: [Profile ▼] [New] [Rename] [Duplicate] [Delete].
        var controlY = boxY + BOX_PAD_Y + HEADER_HEIGHT;
        var controlX = boxX + BOX_PAD_X;
        var profileLabel = "Profile: ";
        graphics.drawString(font, Component.literal(profileLabel), controlX, controlY + 2, META_COLOR, false);
        controlX += font.width(profileLabel) + 4;
        var dropdownWidth = 140;
        var activeProfile = profileById(activeProfileId);
        var dropdownLabel = (activeProfile != null ? activeProfile.displayName() : activeProfileId) + " ▼";
        profileDropdownRect = renderButton(
            graphics,
            controlX,
            controlY,
            dropdownWidth,
            CONTROL_ROW_HEIGHT,
            dropdownLabel,
            mouseX,
            mouseY,
            BUTTON_TEXT,
            true
        );
        controlX += dropdownWidth + FOOTER_BUTTON_GAP;
        newButtonRect = renderButton(graphics, controlX, controlY, 50, CONTROL_ROW_HEIGHT, "+ New", mouseX, mouseY, BUTTON_TEXT, true);
        controlX += 50 + 4;
        var canEditProfile = activeProfile != null && !activeProfile.isDefault();
        renameButtonRect = renderButton(
            graphics,
            controlX,
            controlY,
            60,
            CONTROL_ROW_HEIGHT,
            "Rename",
            mouseX,
            mouseY,
            canEditProfile ? BUTTON_TEXT : BUTTON_DISABLED_TEXT,
            canEditProfile
        );
        controlX += 60 + 4;
        duplicateButtonRect = renderButton(
            graphics,
            controlX,
            controlY,
            70,
            CONTROL_ROW_HEIGHT,
            "Duplicate",
            mouseX,
            mouseY,
            BUTTON_TEXT,
            true
        );
        controlX += 70 + 4;
        deleteButtonRect = renderButton(
            graphics,
            controlX,
            controlY,
            50,
            CONTROL_ROW_HEIGHT,
            "Delete",
            mouseX,
            mouseY,
            canEditProfile ? 0xFFE06868 : BUTTON_DISABLED_TEXT,
            canEditProfile
        );

        // Search row.
        var searchY = controlY + CONTROL_ROW_HEIGHT + CONTROL_ROW_GAP;
        var searchWidth = BOX_WIDTH - 2 * BOX_PAD_X;
        searchInput.render(graphics, boxX + BOX_PAD_X, searchY, searchWidth, mouseX, mouseY);

        // Body region: left rail + right bindings list.
        var bodyTopY = searchY + CONTROL_ROW_HEIGHT + CONTROL_ROW_GAP;
        var bodyBottomY = boxY + BOX_HEIGHT - BOX_PAD_Y - FOOTER_BUTTON_HEIGHT - CONTROL_ROW_GAP;
        var bodyHeight = bodyBottomY - bodyTopY;

        renderCategoryRail(graphics, boxX + BOX_PAD_X, bodyTopY, CATEGORY_RAIL_WIDTH, bodyHeight, mouseX, mouseY);

        var bindingsX = boxX + BOX_PAD_X + CATEGORY_RAIL_WIDTH + 8;
        var bindingsWidth = boxX + BOX_WIDTH - BOX_PAD_X - bindingsX;
        renderBindingsList(graphics, bindingsX, bodyTopY, bindingsWidth, bodyHeight, mouseX, mouseY);

        // Inline conflict error (over the binding list).
        if (inlineError != null) {
            graphics.drawString(font, Component.literal(inlineError), bindingsX, bodyBottomY - font.lineHeight - 4, ERROR_COLOR, false);
        }

        // Footer: Cancel / Apply / OK.
        var footerY = boxY + BOX_HEIGHT - BOX_PAD_Y - FOOTER_BUTTON_HEIGHT;
        var footerRightEdge = boxX + BOX_WIDTH - BOX_PAD_X;
        var okX = footerRightEdge - FOOTER_BUTTON_WIDTH;
        var applyX = okX - FOOTER_BUTTON_GAP - FOOTER_BUTTON_WIDTH;
        var cancelX = applyX - FOOTER_BUTTON_GAP - FOOTER_BUTTON_WIDTH;
        var unsaved = hasUnsavedChanges();
        cancelButtonRect = renderButton(
            graphics,
            cancelX,
            footerY,
            FOOTER_BUTTON_WIDTH,
            FOOTER_BUTTON_HEIGHT,
            "Cancel",
            mouseX,
            mouseY,
            BUTTON_TEXT,
            true
        );
        applyButtonRect = renderButton(
            graphics,
            applyX,
            footerY,
            FOOTER_BUTTON_WIDTH,
            FOOTER_BUTTON_HEIGHT,
            "Apply",
            mouseX,
            mouseY,
            unsaved ? BUTTON_TEXT : BUTTON_DISABLED_TEXT,
            unsaved
        );
        okButtonRect = renderButton(
            graphics,
            okX,
            footerY,
            FOOTER_BUTTON_WIDTH,
            FOOTER_BUTTON_HEIGHT,
            "OK",
            mouseX,
            mouseY,
            BUTTON_TEXT,
            true
        );

        // Profile dropdown overlay (drawn last so it stacks over the rest).
        if (profileMenu != null) {
            profileMenu.render(graphics, mouseX, mouseY);
        }
    }

    private void renderCategoryRail(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        graphics.fill(x, y, x + width, y + height, PANEL_BG_COLOR);
        drawBorder(graphics, x, y, width, height, BORDER_COLOR);

        categoryRects.clear();
        var font = EngineFont.get();

        var rowY = y + RAIL_PAD;
        // "All" pseudo-category at the top.
        rowY = renderCategoryRow(graphics, x + 2, rowY, width - 4, mouseX, mouseY, null, "All", Keybindings.defaults().size(), font);

        for (var cat : collectCategories()) {
            var count = countInCategory(cat);
            rowY = renderCategoryRow(graphics, x + 2, rowY, width - 4, mouseX, mouseY, cat, Keybindings.categoryLabel(cat), count, font);
        }
    }

    private int renderCategoryRow(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int mouseX,
        int mouseY,
        @Nullable String catKey,
        String label,
        int count,
        net.minecraft.client.gui.Font font
    ) {
        var isSelected = java.util.Objects.equals(selectedCategory, catKey);
        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + CATEGORY_ROW_HEIGHT;
        if (isSelected) {
            graphics.fill(x, y, x + width, y + CATEGORY_ROW_HEIGHT, CATEGORY_ACTIVE_BG);
        } else if (hovered) {
            graphics.fill(x, y, x + width, y + CATEGORY_ROW_HEIGHT, ROW_HOVER_BG);
        }

        var labelTextY = y + (CATEGORY_ROW_HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), x + 6, labelTextY, LABEL_COLOR, false);
        var countText = "(" + count + ")";
        var countWidth = font.width(countText);
        graphics.drawString(font, Component.literal(countText), x + width - countWidth - 6, labelTextY, META_COLOR, false);

        categoryRects.add(new CategoryRect(catKey, new Rect(x, y, width, CATEGORY_ROW_HEIGHT)));
        return y + CATEGORY_ROW_HEIGHT + 1;
    }

    private void renderBindingsList(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        graphics.fill(x, y, x + width, y + height, PANEL_BG_COLOR);
        drawBorder(graphics, x, y, width, height, BORDER_COLOR);

        visibleRowsHeight = height - 2 * RAIL_PAD;
        rowRects.clear();
        var rows = filteredBindings();
        contentHeight = rows.size() * (ROW_HEIGHT + 2);
        var maxScroll = Math.max(0, contentHeight - visibleRowsHeight);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));

        var clipTop = y + RAIL_PAD;
        var clipBottom = y + height - RAIL_PAD;
        // No scissor: GuiGraphics.enableScissor runs in screen-pixel space, but EngineWorkspaceScreen renders with a
        // SCALE-transformed pose, so a scissor box specified in logical pixels lands in the wrong physical region and
        // clips the entire content out. Skip-rows-outside-visible below is enough — we lose partial-row clipping at
        // the top/bottom edges, which is acceptable for v1.

        var font = EngineFont.get();
        var rowY = clipTop - (int) scrollOffset;

        for (var i = 0; i < rows.size(); i++) {
            var binding = rows.get(i);
            // Hard skip: only render rows fully within the visible band. Partial rows would extend past the panel
            // border without scissor clipping (see note above).
            if (rowY + ROW_HEIGHT > clipBottom || rowY < clipTop) {
                rowY += ROW_HEIGHT + 2;
                continue;
            }
            var hovered = mouseX >= x + 4 && mouseX < x + width - 4 && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
            if (hovered) {
                graphics.fill(x + 4, rowY, x + width - 4, rowY + ROW_HEIGHT, ROW_HOVER_BG);
            }

            // Label (left).
            var labelTextY = rowY + (ROW_HEIGHT - font.lineHeight + 2) / 2;
            graphics.drawString(font, Component.literal(binding.label()), x + ROW_PAD_X, labelTextY, LABEL_COLOR, false);

            // Reset button (right).
            var resetX = x + width - ROW_PAD_X - RESET_BUTTON_WIDTH;
            var resetY = rowY + (ROW_HEIGHT - CONTROL_ROW_HEIGHT) / 2;
            var hasOverride = stagedOverrides.containsKey(binding.id());
            var resetRect = renderButton(
                graphics,
                resetX,
                resetY,
                RESET_BUTTON_WIDTH,
                CONTROL_ROW_HEIGHT,
                "↺",
                mouseX,
                mouseY,
                hasOverride ? BUTTON_TEXT : BUTTON_DISABLED_TEXT,
                hasOverride
            );

            // Chip (between label and reset).
            var chipX = resetX - 6 - CHIP_WIDTH;
            var chipY = rowY + (ROW_HEIGHT - KeyCaptureWidget.HEIGHT) / 2;
            var resolvedInput = stagedOverrides.getOrDefault(binding.id(), binding.input());
            renderChip(graphics, chipX, chipY, CHIP_WIDTH, resolvedInput, binding.id(), mouseX, mouseY);

            var chipRect = new Rect(chipX, chipY, CHIP_WIDTH, KeyCaptureWidget.HEIGHT);
            rowRects.add(new RowRect(binding, chipRect, resetRect));

            rowY += ROW_HEIGHT + 2;
        }

        // Scrollbar if needed.
        if (contentHeight > visibleRowsHeight) {
            var trackX = x + width - 4;
            var trackTop = clipTop;
            var trackHeight = visibleRowsHeight;
            graphics.fill(trackX, trackTop, trackX + 2, trackTop + trackHeight, 0xFF2A2A30);
            var thumbHeight = Math.max(8, (int) (trackHeight * (visibleRowsHeight / (double) contentHeight)));
            var thumbY = trackTop + (int) (scrollOffset / (double) Math.max(1, maxScroll) * (trackHeight - thumbHeight));
            graphics.fill(trackX, thumbY, trackX + 2, thumbY + thumbHeight, 0xFF555560);
        }
    }

    private void renderChip(GuiGraphics graphics, int x, int y, int width, Input resolved, String bindingId, int mouseX, int mouseY) {
        var armed = capturingChip != null && capturingBindingId != null && capturingBindingId.equals(bindingId);
        if (armed) {
            capturingChip.render(graphics, x, y, width, mouseX, mouseY);
            return;
        }
        // Static chip for the resolved input.
        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + KeyCaptureWidget.HEIGHT;
        var bg = hovered ? 0xFF1F1F26 : 0xFF14141A;
        graphics.fill(x, y, x + width, y + KeyCaptureWidget.HEIGHT, bg);
        graphics.fill(x, y, x + width, y + 1, BORDER_COLOR);
        graphics.fill(x, y + KeyCaptureWidget.HEIGHT - 1, x + width, y + KeyCaptureWidget.HEIGHT, BORDER_COLOR);
        graphics.fill(x, y, x + 1, y + KeyCaptureWidget.HEIGHT, BORDER_COLOR);
        graphics.fill(x + width - 1, y, x + width, y + KeyCaptureWidget.HEIGHT, BORDER_COLOR);

        var font = EngineFont.get();
        var text = resolved.format();
        if (text.isEmpty()) {
            text = "(none)";
        }
        var textWidth = font.width(text);
        var hasOverride = stagedOverrides.containsKey(bindingId);
        var textColor = hasOverride ? MODIFIED_COLOR : LABEL_COLOR;
        graphics.drawString(
            font,
            Component.literal(text),
            x + (width - textWidth) / 2,
            y + (KeyCaptureWidget.HEIGHT - font.lineHeight + 2) / 2,
            textColor,
            false
        );
    }

    private List<Keybinding> filteredBindings() {
        var query = searchInput.content().toLowerCase(Locale.ROOT);
        var out = new ArrayList<Keybinding>();
        for (var kb : Keybindings.defaults()) {
            if (selectedCategory != null && !Keybindings.categoryOf(kb.id()).equals(selectedCategory)) {
                continue;
            }
            if (
                !query.isEmpty()
                    && !kb.label().toLowerCase(Locale.ROOT).contains(query)
                    && !kb.id().toLowerCase(Locale.ROOT).contains(query)
            ) {
                continue;
            }
            out.add(kb);
        }
        return out;
    }

    private List<String> collectCategories() {
        // Preserve declaration order via LinkedHashMap.
        var seen = new LinkedHashMap<String, Integer>();
        for (var kb : Keybindings.defaults()) {
            var cat = Keybindings.categoryOf(kb.id());
            seen.merge(cat, 1, Integer::sum);
        }
        return new ArrayList<>(seen.keySet());
    }

    private int countInCategory(String cat) {
        var c = 0;
        for (var kb : Keybindings.defaults()) {
            if (Keybindings.categoryOf(kb.id()).equals(cat)) {
                c++;
            }
        }
        return c;
    }

    private @Nullable KeybindingProfile profileById(String id) {
        for (var p : profiles) {
            if (p.id().equals(id)) {
                return p;
            }
        }
        return KeybindingProfileCatalog.get(id);
    }

    // ============================== Input dispatch ==============================

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Profile dropdown menu absorbs first if open.
        if (profileMenu != null) {
            if (profileMenu.isInside(mouseX, mouseY)) {
                var idx = profileMenu.hitItemAt(mouseX, mouseY);
                if (idx >= 0) {
                    profileMenu.itemAt(idx).action().run();
                }
                profileMenu = null;
                return true;
            }
            profileMenu = null;
            return true;
        }

        // If a chip is armed, route to it for capture (any click finalizes).
        if (capturingChip != null && capturingChip.isArmed()) {
            // Run through the capture widget; its onCaptured callback applies the staged change.
            capturingChip.mouseClicked(mouseX, mouseY, button);
            return true;
        }

        if (searchInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // Profile dropdown opens.
        if (profileDropdownRect != null && profileDropdownRect.contains(mouseX, mouseY) && button == 0) {
            openProfileMenu();
            return true;
        }

        if (newButtonRect != null && newButtonRect.contains(mouseX, mouseY) && button == 0) {
            onNewProfile.run();
            return true;
        }

        var active = profileById(activeProfileId);
        if (renameButtonRect != null && renameButtonRect.contains(mouseX, mouseY) && button == 0) {
            if (active != null && !active.isDefault()) {
                onRenameProfile.accept(active);
            }
            return true;
        }
        if (duplicateButtonRect != null && duplicateButtonRect.contains(mouseX, mouseY) && button == 0) {
            if (active != null) {
                onDuplicateProfile.accept(active);
            }
            return true;
        }
        if (deleteButtonRect != null && deleteButtonRect.contains(mouseX, mouseY) && button == 0) {
            if (active != null && !active.isDefault()) {
                onDeleteProfile.accept(active);
            }
            return true;
        }

        for (var cr : categoryRects) {
            if (cr.rect.contains(mouseX, mouseY) && button == 0) {
                selectedCategory = cr.catKey;
                scrollOffset = 0;
                return true;
            }
        }

        for (var rr : rowRects) {
            if (rr.chipRect.contains(mouseX, mouseY) && button == 0) {
                beginCapture(rr.binding);
                return true;
            }
            if (rr.resetRect.contains(mouseX, mouseY) && button == 0) {
                if (stagedOverrides.remove(rr.binding.id()) != null) {
                    clearError();
                }
                return true;
            }
        }

        if (cancelButtonRect != null && cancelButtonRect.contains(mouseX, mouseY) && button == 0) {
            cancelCapture();
            onClose.run();
            return true;
        }
        if (applyButtonRect != null && applyButtonRect.contains(mouseX, mouseY) && button == 0) {
            applyChanges();
            return true;
        }
        if (okButtonRect != null && okButtonRect.contains(mouseX, mouseY) && button == 0) {
            applyChanges();
            onClose.run();
            return true;
        }

        // Outside-clicks absorbed but no-op (consistent with other modal dialogs).
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (capturingChip != null && capturingChip.isArmed()) {
            // Capture priority: the widget consumes the next key, including Esc (which cancels capture only — not the
            // dialog). All other keys complete the capture.
            return capturingChip.keyPressed(keyCode, scanCode, modifiers);
        }
        if (searchInput.isFocused() && searchInput.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            cancelCapture();
            onClose.run();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (hasUnsavedChanges()) {
                applyChanges();
                onClose.run();
                return true;
            }
        }
        return true;
    }

    public boolean charTyped(char ch, int modifiers) {
        if (searchInput.isFocused()) {
            return searchInput.charTyped(ch, modifiers);
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (capturingChip != null && capturingChip.isArmed()) {
            return capturingChip.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        scrollOffset -= scrollY * 20;
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (searchInput.isFocused() && button == 0) {
            searchInput.mouseDraggedExtend(mouseX);
            return true;
        }
        return false;
    }

    // ============================== Apply / capture helpers ==============================

    private void applyChanges() {
        var active = profileById(activeProfileId);
        if (active == null) {
            return;
        }
        var updated = active.withOverrides(stagedOverrides);
        try {
            KeybindingProfileCatalog.save(updated);
        } catch (java.io.IOException e) {
            inlineError = "Failed to write profile: " + e.getMessage();
            inlineErrorTicks = CONFLICT_DISPLAY_TICKS;
            return;
        }
        // Refresh staged baseline to the just-saved snapshot.
        this.stagedOverrides = new LinkedHashMap<>(updated.overrides());
        refreshProfiles();
    }

    private void beginCapture(Keybinding binding) {
        var startingInput = stagedOverrides.getOrDefault(binding.id(), binding.input());
        var bindingId = binding.id();
        this.capturingBindingId = bindingId;
        this.capturingChip = new KeyCaptureWidget(
            () -> startingInput,
            captured -> applyCapture(binding, captured),
            this::cancelCapture
        );
        // Click-to-arm: the widget's mouseClicked already toggles armed on click; we need to bypass that one-shot
        // and arm directly since we already received the click on this row's chip.
        // We do this by simulating arm via mouseClicked on the chip's own rect — but that's circular. Simpler: set
        // armed via a dedicated method. Since the widget doesn't expose a setter, we feed it a fake click event by
        // calling mouseClicked with coordinates inside its own rect after the next render assigns them. Cleaner: just
        // add an arm() method.
        this.capturingChip.armDirectly();
    }

    private void applyCapture(Keybinding binding, Input captured) {
        var conflict = findConflict(binding.id(), captured);
        if (conflict != null) {
            inlineError = "Conflicts with " + conflict.label() + " (" + Keybindings.categoryLabel(Keybindings.categoryOf(conflict.id()))
                + ")";
            inlineErrorTicks = CONFLICT_DISPLAY_TICKS;
            cancelCapture();
            return;
        }
        if (sameTrigger(captured, binding.input())) {
            // Setting to the default = remove the override (keeps the profile sparse).
            stagedOverrides.remove(binding.id());
        } else {
            stagedOverrides.put(binding.id(), captured);
        }
        clearError();
        cancelCapture();
    }

    private void cancelCapture() {
        this.capturingChip = null;
        this.capturingBindingId = null;
    }

    private void clearError() {
        this.inlineError = null;
        this.inlineErrorTicks = 0;
    }

    /**
     * Returns the other binding in the same category that already uses {@code proposed}, or null if no conflict.
     * Cross-category collisions are intentionally allowed.
     */
    private @Nullable Keybinding findConflict(String selfId, Input proposed) {
        var prefix = Keybindings.categoryOf(selfId);
        for (var d : Keybindings.defaults()) {
            if (d.id().equals(selfId)) {
                continue;
            }
            if (!Keybindings.categoryOf(d.id()).equals(prefix)) {
                continue;
            }
            var dResolved = stagedOverrides.getOrDefault(d.id(), d.input());
            if (sameTrigger(dResolved, proposed)) {
                return d;
            }
        }
        return null;
    }

    private static boolean sameTrigger(Input a, Input b) {
        return switch (a) {
            case Input.Key k -> b instanceof Input.Key bk && k.keyCode() == bk.keyCode()
                && (k.modifierMask() & Input.DISPLAY_MOD_MASK) == (bk.modifierMask() & Input.DISPLAY_MOD_MASK);
            case Input.MouseButton mb -> b instanceof Input.MouseButton bm && mb.button() == bm.button()
                && (mb.modifierMask() & Input.DISPLAY_MOD_MASK) == (bm.modifierMask() & Input.DISPLAY_MOD_MASK);
            case Input.MouseDrag md -> b instanceof Input.MouseDrag bd && md.button() == bd.button()
                && (md.modifierMask() & Input.DISPLAY_MOD_MASK) == (bd.modifierMask() & Input.DISPLAY_MOD_MASK);
            case Input.Scroll s -> b instanceof Input.Scroll bs
                && (s.modifierMask() & Input.DISPLAY_MOD_MASK) == (bs.modifierMask() & Input.DISPLAY_MOD_MASK);
            case Input.Modifier m -> b instanceof Input.Modifier bm
                && (m.modifierMask() & Input.DISPLAY_MOD_MASK) == (bm.modifierMask() & Input.DISPLAY_MOD_MASK);
        };
    }

    private void openProfileMenu() {
        if (profileDropdownRect == null) {
            return;
        }
        var items = new ArrayList<DropdownMenu.Item>();
        for (var p : profiles) {
            var pid = p.id();
            var label = p.displayName() + (pid.equals(activeProfileId) ? "  ●" : "");
            items.add(new DropdownMenu.Item(label, () -> requestSwitchProfile(pid)));
        }
        this.profileMenu = new DropdownMenu(profileDropdownRect.x, profileDropdownRect.y + profileDropdownRect.h, items);
    }

    private void requestSwitchProfile(String id) {
        if (id.equals(activeProfileId)) {
            return;
        }
        if (hasUnsavedChanges()) {
            // The host's confirm-discard flow normally guards this, but we have no host hook here. For v1, discard
            // silently to avoid blocking — users can re-enter Apply if they had pending edits they wanted to keep.
            // TODO follow-up: route through a ConfirmDialog via a host callback.
        }
        loadProfile(id);
    }

    // ============================== Drawing helpers ==============================

    private static Rect renderButton(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int height,
        String label,
        int mouseX,
        int mouseY,
        int textColor,
        boolean enabled
    ) {
        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        var bg = !enabled ? BUTTON_BG_DISABLED : (hovered ? BUTTON_BG_HOVER : BUTTON_BG);
        graphics.fill(x, y, x + width, y + height, bg);
        graphics.fill(x, y, x + width, y + 1, BUTTON_BORDER);
        graphics.fill(x, y + height - 1, x + width, y + height, BUTTON_BORDER);
        graphics.fill(x, y, x + 1, y + height, BUTTON_BORDER);
        graphics.fill(x + width - 1, y, x + width, y + height, BUTTON_BORDER);

        var font = EngineFont.get();
        var textX = x + (width - font.width(label)) / 2;
        var textY = y + (height - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(label), textX, textY, textColor, false);
        return new Rect(x, y, width, height);
    }

    private static void drawBorder(GuiGraphics graphics, int x, int y, int w, int h, int color) {
        graphics.fill(x, y, x + w, y + 1, color);
        graphics.fill(x, y + h - 1, x + w, y + h, color);
        graphics.fill(x, y, x + 1, y + h, color);
        graphics.fill(x + w - 1, y, x + w, y + h, color);
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

    private record RowRect(
        Keybinding binding,
        Rect chipRect,
        Rect resetRect
    ) {}

    private record CategoryRect(
        @Nullable String catKey,
        Rect rect
    ) {}
}
