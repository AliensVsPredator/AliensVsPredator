package com.blib.engine.ui.dialog;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import com.blib.engine.input.Input;
import com.blib.engine.input.Keybinding;
import com.blib.engine.input.KeybindingProfile;
import com.blib.engine.input.KeybindingProfileCatalog;
import com.blib.engine.input.Keybindings;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.widget.DropdownMenu;
import com.blib.engine.ui.widget.KeyCaptureWidget;
import com.blib.engine.ui.widget.TextInput;

/**
 * Preferences modal — keybinding editor backed by named, persisted {@link KeybindingProfile}s. Apply/Cancel semantics:
 * edits stage in {@link #stagedOverrides} until the user clicks Apply (writes to disk + refreshes
 * {@link com.blib.engine.input.ActiveKeybindings}). Cancel discards.
 * <p>
 * Conflict policy: cross-category overlaps are allowed (e.g. {@code T} can be both {@code jigsaw.cycle_mode} and
 * {@code gizmo.translate} — context disambiguates them). Within a single category, rebinds that collide are still
 * permitted but their chips render red, Apply is disabled, and a "N conflicts" hint sits next to Apply. The user has to
 * resolve the collision before committing — but doesn't lose the rebind they just made while figuring out which binding
 * to move.
 * <p>
 * Cancel guards: when the user has staged-but-not-applied changes that <i>could</i> be applied (no conflicts), Cancel
 * pops a nested {@link ConfirmDialog} asking to discard. Cancel with conflicts or no unsaved edits closes silently.
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

    /** Height of the "EDIT" / "JIGSAW" / etc. group headers shown in the All view. */
    private static final int HEADER_ROW_HEIGHT = 14;

    private static final int ROW_PAD_X = 8;

    private static final int CHIP_WIDTH = 100;

    private static final int RESET_BUTTON_WIDTH = 18;

    private static final int FOOTER_BUTTON_HEIGHT = 14;

    private static final int FOOTER_BUTTON_WIDTH = 80;

    private static final int FOOTER_BUTTON_GAP = 8;

    private static final int CONFLICT_DISPLAY_TICKS = 120; // ~6 seconds @ 20 fps if render fires that fast.

    private final Runnable onClose;

    private final Runnable onNewProfile;

    private final Consumer<KeybindingProfile> onRenameProfile;

    private final Consumer<KeybindingProfile> onDuplicateProfile;

    private final Consumer<KeybindingProfile> onDeleteProfile;

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

    private @Nullable Rect resetAllButtonRect;

    private @Nullable Rect cancelButtonRect;

    private @Nullable Rect applyButtonRect;

    private @Nullable Rect okButtonRect;

    private @Nullable DropdownMenu profileMenu;

    /**
     * Nested confirm dialog for the "discard unsaved changes?" flow when Cancel is pressed with applyable staged edits.
     * Renders + intercepts input on top of the preferences dialog. Cleared when the confirm resolves either way.
     */
    private @Nullable ConfirmDialog discardConfirm;

    /** Per-frame set of binding ids that collide with another binding in the same category — used for red coloring. */
    private Set<String> conflictedIds = Set.of();

    public PreferencesDialog(
        Runnable onClose,
        Runnable onNewProfile,
        Consumer<KeybindingProfile> onRenameProfile,
        Consumer<KeybindingProfile> onDuplicateProfile,
        Consumer<KeybindingProfile> onDeleteProfile
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

        // Recompute conflicts each frame from current staging. Cheap enough at 31 bindings (~960 comparisons) that
        // caching with invalidation isn't worth the complexity.
        this.conflictedIds = computeConflicts();

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

        // Search row + "Reset all" button (right-aligned).
        var searchY = controlY + CONTROL_ROW_HEIGHT + CONTROL_ROW_GAP;
        var resetAllWidth = 80;
        var searchWidth = BOX_WIDTH - 2 * BOX_PAD_X - resetAllWidth - FOOTER_BUTTON_GAP;
        searchInput.render(graphics, boxX + BOX_PAD_X, searchY, searchWidth, mouseX, mouseY);
        var resetAllX = boxX + BOX_WIDTH - BOX_PAD_X - resetAllWidth;
        // Enabled iff there's at least one override staged for the current profile.
        var canResetAll = !stagedOverrides.isEmpty();
        resetAllButtonRect = renderButton(
            graphics,
            resetAllX,
            searchY,
            resetAllWidth,
            CONTROL_ROW_HEIGHT,
            "Reset all",
            mouseX,
            mouseY,
            canResetAll ? BUTTON_TEXT : BUTTON_DISABLED_TEXT,
            canResetAll
        );

        // Body region: left rail + right bindings list.
        var bodyTopY = searchY + CONTROL_ROW_HEIGHT + CONTROL_ROW_GAP;
        var bodyBottomY = boxY + BOX_HEIGHT - BOX_PAD_Y - FOOTER_BUTTON_HEIGHT - CONTROL_ROW_GAP;
        var bodyHeight = bodyBottomY - bodyTopY;

        renderCategoryRail(graphics, boxX + BOX_PAD_X, bodyTopY, CATEGORY_RAIL_WIDTH, bodyHeight, mouseX, mouseY);

        var bindingsX = boxX + BOX_PAD_X + CATEGORY_RAIL_WIDTH + 8;
        var bindingsWidth = boxX + BOX_WIDTH - BOX_PAD_X - bindingsX;
        renderBindingsList(graphics, bindingsX, bodyTopY, bindingsWidth, bodyHeight, mouseX, mouseY);

        // Footer: Cancel / Apply / OK.
        var footerY = boxY + BOX_HEIGHT - BOX_PAD_Y - FOOTER_BUTTON_HEIGHT;
        var footerRightEdge = boxX + BOX_WIDTH - BOX_PAD_X;
        var okX = footerRightEdge - FOOTER_BUTTON_WIDTH;
        var applyX = okX - FOOTER_BUTTON_GAP - FOOTER_BUTTON_WIDTH;
        var cancelX = applyX - FOOTER_BUTTON_GAP - FOOTER_BUTTON_WIDTH;
        var unsaved = hasUnsavedChanges();
        var conflicts = hasConflicts();
        var canApply = unsaved && !conflicts;
        var canOk = !conflicts;

        // Conflict / status hint to the left of the buttons. Renders red when there are conflicts so the user
        // immediately sees why Apply is disabled; falls back to dim "● modified" / blank.
        if (conflicts) {
            var n = conflictedIds.size();
            var msg = n + (n == 1 ? " conflict" : " conflicts") + " — resolve to apply";
            graphics.drawString(
                font,
                Component.literal(msg),
                boxX + BOX_PAD_X,
                footerY + (FOOTER_BUTTON_HEIGHT - font.lineHeight + 2) / 2,
                ERROR_COLOR,
                false
            );
        } else if (inlineError != null) {
            graphics.drawString(
                font,
                Component.literal(inlineError),
                boxX + BOX_PAD_X,
                footerY + (FOOTER_BUTTON_HEIGHT - font.lineHeight + 2) / 2,
                ERROR_COLOR,
                false
            );
        }

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
            canApply ? BUTTON_TEXT : BUTTON_DISABLED_TEXT,
            canApply
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
            canOk ? BUTTON_TEXT : BUTTON_DISABLED_TEXT,
            canOk
        );

        // Profile dropdown overlay (drawn last so it stacks over the rest).
        if (profileMenu != null) {
            profileMenu.render(graphics, mouseX, mouseY);
        }

        // Nested discard-confirm renders on top of everything.
        if (discardConfirm != null) {
            discardConfirm.render(graphics, screenWidth, screenHeight, mouseX, mouseY);
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
        Font font
    ) {
        var isSelected = Objects.equals(selectedCategory, catKey);
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
        var showHeaders = selectedCategory == null;

        // Pre-compute content height accounting for inserted category headers in All view.
        contentHeight = 0;
        String tallyLastCat = null;
        for (var b : rows) {
            var cat = Keybindings.categoryOf(b.id());
            if (showHeaders && !cat.equals(tallyLastCat)) {
                contentHeight += HEADER_ROW_HEIGHT + 2;
                tallyLastCat = cat;
            }
            contentHeight += ROW_HEIGHT + 2;
        }
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
        String lastCat = null;

        for (var binding : rows) {
            var cat = Keybindings.categoryOf(binding.id());
            // Section header before the first binding of each new category in All view.
            if (showHeaders && !cat.equals(lastCat)) {
                if (rowY + HEADER_ROW_HEIGHT <= clipBottom && rowY >= clipTop) {
                    renderCategoryHeader(graphics, font, x + ROW_PAD_X, rowY, width - 2 * ROW_PAD_X, Keybindings.categoryLabel(cat));
                }
                rowY += HEADER_ROW_HEIGHT + 2;
                lastCat = cat;
            }

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

    private void renderCategoryHeader(GuiGraphics graphics, Font font, int x, int y, int width, String label) {
        var upper = label.toUpperCase(Locale.ROOT);
        var textWidth = font.width(upper);
        var textY = y + (HEADER_ROW_HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(upper), x, textY, TITLE_COLOR, false);
        // Thin separator line from end-of-text to the right edge, gives the header a sectioning feel without a heavy
        // background fill.
        var lineX = x + textWidth + 6;
        var lineY = y + HEADER_ROW_HEIGHT / 2;
        if (lineX < x + width) {
            graphics.fill(lineX, lineY, x + width, lineY + 1, BORDER_COLOR);
        }
    }

    private void renderChip(GuiGraphics graphics, int x, int y, int width, Input resolved, String bindingId, int mouseX, int mouseY) {
        var armed = capturingChip != null && capturingBindingId != null && capturingBindingId.equals(bindingId);
        if (armed) {
            capturingChip.render(graphics, x, y, width, mouseX, mouseY);
            return;
        }
        // Static chip for the resolved input.
        var conflicted = conflictedIds.contains(bindingId);
        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + KeyCaptureWidget.HEIGHT;
        var bg = hovered ? 0xFF1F1F26 : 0xFF14141A;
        var border = conflicted ? ERROR_COLOR : BORDER_COLOR;
        graphics.fill(x, y, x + width, y + KeyCaptureWidget.HEIGHT, bg);
        graphics.fill(x, y, x + width, y + 1, border);
        graphics.fill(x, y + KeyCaptureWidget.HEIGHT - 1, x + width, y + KeyCaptureWidget.HEIGHT, border);
        graphics.fill(x, y, x + 1, y + KeyCaptureWidget.HEIGHT, border);
        graphics.fill(x + width - 1, y, x + width, y + KeyCaptureWidget.HEIGHT, border);

        var font = EngineFont.get();
        var text = resolved.format();
        if (text.isEmpty()) {
            text = "(none)";
        }
        var textWidth = font.width(text);
        var hasOverride = stagedOverrides.containsKey(bindingId);
        // Color priority: red (conflicted) > yellow (modified) > grey (default).
        int textColor;
        if (conflicted) {
            textColor = ERROR_COLOR;
        } else if (hasOverride) {
            textColor = MODIFIED_COLOR;
        } else {
            textColor = LABEL_COLOR;
        }
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
        // Nested discard confirm absorbs every click while open.
        if (discardConfirm != null) {
            discardConfirm.mouseClicked(mouseX, mouseY, button);
            return true;
        }

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
        // Click landed outside the search input — defocus so a subsequent chip rebind doesn't bleed the captured key
        // into the search field via charTyped (TextInput defocuses only on a click that hits another input, not on a
        // click that hits non-input UI).
        TextInput.clearFocus();

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

        if (resetAllButtonRect != null && resetAllButtonRect.contains(mouseX, mouseY) && button == 0) {
            if (!stagedOverrides.isEmpty()) {
                stagedOverrides.clear();
                clearError();
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
            requestCancel();
            return true;
        }
        if (applyButtonRect != null && applyButtonRect.contains(mouseX, mouseY) && button == 0) {
            // Apply is gated visually; double-check here so a stale click during a conflict doesn't sneak through.
            if (hasUnsavedChanges() && !hasConflicts()) {
                applyChanges();
            }
            return true;
        }
        if (okButtonRect != null && okButtonRect.contains(mouseX, mouseY) && button == 0) {
            if (!hasConflicts()) {
                applyChanges();
                onClose.run();
            }
            return true;
        }

        // Outside-clicks absorbed but no-op (consistent with other modal dialogs).
        return true;
    }

    /**
     * Cancel flow: if the user has applyable unsaved changes (no conflicts), prompt to discard. Otherwise close
     * silently — there's nothing to lose (clean staging) or nothing they could have kept anyway (conflicts blocked
     * Apply, so the staged-but-invalid edits don't count as committed intent).
     */
    private void requestCancel() {
        cancelCapture();
        if (hasUnsavedChanges() && !hasConflicts()) {
            this.discardConfirm = new ConfirmDialog(
                "Discard changes?",
                "You have unsaved keybinding changes for this profile.",
                "Discard",
                "Keep editing",
                true,
                () -> {
                    discardConfirm = null;
                    onClose.run();
                },
                () -> discardConfirm = null
            );
            return;
        }
        onClose.run();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Discard-confirm intercepts. Its keyPressed handles Esc → cancel-confirm; everything else is swallowed so
        // the underlying preferences dialog doesn't keep editing while the confirm is up.
        if (discardConfirm != null) {
            discardConfirm.keyPressed(keyCode);
            return true;
        }
        if (capturingChip != null && capturingChip.isArmed()) {
            // Capture priority: the widget consumes the next key, including Esc (which cancels capture only — not the
            // dialog). All other keys complete the capture.
            return capturingChip.keyPressed(keyCode, scanCode, modifiers);
        }
        if (searchInput.isFocused() && searchInput.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            requestCancel();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (hasUnsavedChanges() && !hasConflicts()) {
                applyChanges();
                onClose.run();
                return true;
            }
        }
        return true;
    }

    public boolean charTyped(char ch, int modifiers) {
        if (discardConfirm != null) {
            return true;
        }
        if (searchInput.isFocused()) {
            return searchInput.charTyped(ch, modifiers);
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (discardConfirm != null) {
            return true;
        }
        if (capturingChip != null && capturingChip.isArmed()) {
            return capturingChip.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        scrollOffset -= scrollY * 20;
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (discardConfirm != null) {
            return true;
        }
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
        } catch (IOException e) {
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
        // Conflicts are now permitted at capture time — the user can rebind freely while figuring out which collision
        // to resolve. The conflict is surfaced via red chip coloring in render and gates Apply / OK.
        if (sameTrigger(captured, binding.input())) {
            // Setting to the default = remove the override (keeps the profile sparse).
            stagedOverrides.remove(binding.id());
        } else {
            stagedOverrides.put(binding.id(), captured);
        }
        clearError();
        cancelCapture();
    }

    /**
     * Pairwise scan for bindings whose resolved {@link Input} matches another binding in the same category. Returns the
     * ids of every binding involved in a collision (both sides). O(N²) in the binding count; N=31 so ~960 comparisons
     * per render which is well under render budget.
     */
    private Set<String> computeConflicts() {
        var defaults = Keybindings.defaults();
        var conflicts = new HashSet<String>();
        for (var i = 0; i < defaults.size(); i++) {
            var a = defaults.get(i);
            var ai = stagedOverrides.getOrDefault(a.id(), a.input());
            var aCat = Keybindings.categoryOf(a.id());
            for (var j = i + 1; j < defaults.size(); j++) {
                var b = defaults.get(j);
                if (!Keybindings.categoryOf(b.id()).equals(aCat)) {
                    continue;
                }
                var bi = stagedOverrides.getOrDefault(b.id(), b.input());
                if (sameTrigger(ai, bi)) {
                    conflicts.add(a.id());
                    conflicts.add(b.id());
                }
            }
        }
        return conflicts;
    }

    private boolean hasConflicts() {
        return !conflictedIds.isEmpty();
    }

    private void cancelCapture() {
        this.capturingChip = null;
        this.capturingBindingId = null;
    }

    private void clearError() {
        this.inlineError = null;
        this.inlineErrorTicks = 0;
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
