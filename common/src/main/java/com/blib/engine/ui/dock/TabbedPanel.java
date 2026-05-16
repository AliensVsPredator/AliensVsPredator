package com.blib.engine.ui.dock;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.layout.PanelScissor;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.panel.viewport.ViewportPanel;

/**
 * A leaf container that hosts a list of child panels as switchable tabs. Renders a tab strip across the top with the
 * active tab's title highlighted and an {@code ×} close button per tab; below the strip, the active tab gets the
 * remaining content area to render into.
 * <p>
 * Tab interaction (click-to-switch, click-X-to-close, drag-to-move) is split between this class and
 * {@link com.blib.engine.ui.EngineWorkspaceScreen}: the panel exposes hit-test queries against its tab strip, and the
 * screen owns the drag state machine so tabs can move <em>between</em> {@code TabbedPanel}s as well as within one.
 * <p>
 * Input forwarding to the active tab lives in {@link DelegatingPanel} — adding a new {@code Panel} method only requires
 * a single override there; this class no longer needs to spell out every forward.
 * <p>
 * An empty {@code TabbedPanel} renders as just a tab-strip background — the user can drop a tab from another panel into
 * it to repopulate.
 */
@ApiStatus.Internal
public final class TabbedPanel extends DelegatingPanel {

    public static final int TAB_BAR_HEIGHT = 12;

    private static final int TAB_PADDING_X = 4;

    private static final int TAB_LABEL_TO_CLOSE_GAP = 4;

    private static final int CLOSE_BUTTON_SIZE = 6;

    private static final int TAB_SCROLL_STEP = 48;

    private static final int OVERFLOW_BUTTON_WIDTH = 14;

    private static final String OVERFLOW_BUTTON_LABEL = "▾";

    private static final int STRIP_BG_COLOR = 0xFF161618;

    private static final int TAB_INACTIVE_BG_COLOR = 0xFF1F1F23;

    private static final int TAB_ACTIVE_BG_COLOR = 0xFF2F2F36;

    private static final int TAB_HOVER_BG_COLOR = 0xFF353540;

    private static final int TAB_TEXT_INACTIVE_COLOR = 0xFF888892;

    private static final int TAB_TEXT_ACTIVE_COLOR = 0xFFE0E0E0;

    private static final int TAB_SEPARATOR_COLOR = 0xFF101013;

    private static final int OVERFLOW_BUTTON_BG_COLOR = 0xFF24242A;

    private static final int OVERFLOW_BUTTON_HOVER_BG_COLOR = 0xFF353540;

    private static final int CLOSE_ICON_COLOR = 0xFF888892;

    private static final int CLOSE_ICON_HOVER_COLOR = 0xFFFF6868;

    private static final int CONTENT_BORDER_COLOR = 0xFF2A2A30;

    /**
     * Default backdrop drawn into the content area when the active tab isn't a viewport. The world + HUD render to the
     * full main RT before the workspace draws panels, so without this fill, any non-viewport panel would let the
     * full-resolution world bleed through. Viewport tabs intentionally skip the fill so the compositor's downsampled
     * blit shows through.
     */
    private static final int CONTENT_DEFAULT_BG_COLOR = 0xFF14141A;

    private final List<Panel> tabs = new ArrayList<>();

    private int activeIndex;

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    /** Per-frame tab geometry, keyed by tab index, used for hit testing. Rebuilt on every render. */
    private final List<TabRect> tabRects = new ArrayList<>();

    private int tabScrollX;

    private int tabContentWidth;

    private int tabViewportWidth;

    private boolean revealActiveTab = true;

    private int lastTabViewportWidth = -1;

    private boolean overflowButtonVisible;

    private int overflowButtonX;

    private int overflowButtonY;

    public TabbedPanel(Panel... initial) {
        Collections.addAll(this.tabs, initial);
        // Fire onShown for the initially-active tab so panels with "freshly-opened" state (e.g. scroll positions to
        // reset) get notified the same way they would via setActiveIndex / insertTab. Without this, dragging a tab
        // into a brand-new TabbedPanel via split skipped the onShown notification and the panel kept its old state.
        if (!this.tabs.isEmpty()) {
            this.tabs.get(0).onShown();
        }
    }

    public List<Panel> tabs() {
        return tabs;
    }

    public int tabCount() {
        return tabs.size();
    }

    public int activeIndex() {
        return activeIndex;
    }

    public @Nullable Panel activeTab() {
        return tabs.isEmpty() ? null : tabs.get(activeIndex);
    }

    public void setActiveIndex(int index) {
        if (index >= 0 && index < tabs.size()) {
            var changed = this.activeIndex != index;
            this.activeIndex = index;
            revealActiveTab = true;
            if (changed) {
                tabs.get(index).onShown();
            }
        }
    }

    public void removeTab(int index) {
        if (index < 0 || index >= tabs.size()) {
            return;
        }
        var removed = tabs.get(index);
        var previousActive = activeTab();
        tabs.remove(index);
        if (tabs.isEmpty()) {
            activeIndex = 0;
            tabScrollX = 0;
            return;
        }
        if (previousActive != null && previousActive != removed && tabs.contains(previousActive)) {
            activeIndex = tabs.indexOf(previousActive);
            revealActiveTab = true;
            return;
        }
        activeIndex = Math.min(index, tabs.size() - 1);
        revealActiveTab = true;
        tabs.get(activeIndex).onShown();
    }

    public void removeTabsExcept(int index) {
        if (index < 0 || index >= tabs.size()) {
            return;
        }
        var kept = tabs.get(index);
        var changedActive = activeTab() != kept;
        tabs.clear();
        tabs.add(kept);
        activeIndex = 0;
        tabScrollX = 0;
        revealActiveTab = true;
        if (changedActive) {
            kept.onShown();
        }
    }

    public void removeTabsAfter(int index) {
        if (index < 0 || index >= tabs.size() - 1) {
            return;
        }
        var previousActive = activeTab();
        tabs.subList(index + 1, tabs.size()).clear();
        if (previousActive != null && tabs.contains(previousActive)) {
            activeIndex = tabs.indexOf(previousActive);
            revealActiveTab = true;
            return;
        }
        activeIndex = Math.min(index, tabs.size() - 1);
        revealActiveTab = true;
        tabs.get(activeIndex).onShown();
    }

    public void insertTab(int index, Panel tab) {
        var clamped = Math.max(0, Math.min(tabs.size(), index));
        tabs.add(clamped, tab);
        activeIndex = clamped;
        revealActiveTab = true;
        // Newly-inserted tab becomes active immediately — give it the same onShown notification a setActiveIndex would.
        tab.onShown();
    }

    @Override
    public String title() {
        var active = activeTab();
        return active != null ? active.title() : "Empty";
    }

    @Override
    public boolean hasChrome() {
        // We render our own tab strip in place of a title bar.
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;

        renderTabStrip(graphics, x, y, width, mouseX, mouseY);

        var contentY = y + TAB_BAR_HEIGHT;
        var contentHeight = Math.max(0, height - TAB_BAR_HEIGHT);
        graphics.fill(x, contentY, x + width, contentY + 1, CONTENT_BORDER_COLOR);

        var active = activeTab();
        if (!(active instanceof ViewportPanel) && contentHeight > 1) {
            graphics.fill(x, contentY + 1, x + width, y + height, CONTENT_DEFAULT_BG_COLOR);
        }

        if (active != null && contentHeight > 1) {
            active.render(graphics, x, contentY + 1, width, contentHeight - 1, mouseX, mouseY, partialTick);
        }
    }

    private void renderTabStrip(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        graphics.fill(x, y, x + width, y + TAB_BAR_HEIGHT, STRIP_BG_COLOR);
        tabRects.clear();

        var font = EngineFont.get();
        tabContentWidth = computeTabContentWidth(font);
        overflowButtonVisible = tabContentWidth > width;
        tabViewportWidth = Math.max(0, width - (overflowButtonVisible ? OVERFLOW_BUTTON_WIDTH : 0));
        overflowButtonX = x + Math.max(0, width - OVERFLOW_BUTTON_WIDTH);
        overflowButtonY = y;
        var viewportChanged = tabViewportWidth != lastTabViewportWidth;
        lastTabViewportWidth = tabViewportWidth;
        tabScrollX = clampTabScroll(tabScrollX);
        if (revealActiveTab || (viewportChanged && !isActiveTabVisible(font))) {
            revealActiveTab(font);
            revealActiveTab = false;
        }

        PanelScissor.enable(graphics, UiRect.of(x, y, tabViewportWidth, TAB_BAR_HEIGHT));
        var cursorX = x - tabScrollX;

        for (var i = 0; i < tabs.size(); i++) {
            var label = tabs.get(i).title();
            var labelWidth = font.width(label);
            var tabWidth = tabWidth(font, i);

            var tabRight = cursorX + tabWidth;
            var hovered = mouseX >= cursorX && mouseX < tabRight && mouseY >= y && mouseY < y + TAB_BAR_HEIGHT;
            var bg = i == activeIndex ? TAB_ACTIVE_BG_COLOR : (hovered ? TAB_HOVER_BG_COLOR : TAB_INACTIVE_BG_COLOR);
            graphics.fill(cursorX, y, tabRight, y + TAB_BAR_HEIGHT, bg);

            var textColor = i == activeIndex ? TAB_TEXT_ACTIVE_COLOR : TAB_TEXT_INACTIVE_COLOR;
            // +2 compensates for MC font's descender padding so tab labels visually center; see MenuBarPanel for
            // details.
            var textY = y + (TAB_BAR_HEIGHT - font.lineHeight + 2) / 2;
            graphics.drawString(font, Component.literal(label), cursorX + TAB_PADDING_X, textY, textColor, false);

            var closeX0 = cursorX + TAB_PADDING_X + labelWidth + TAB_LABEL_TO_CLOSE_GAP;
            var closeY0 = y + (TAB_BAR_HEIGHT - CLOSE_BUTTON_SIZE) / 2;
            var closeX1 = closeX0 + CLOSE_BUTTON_SIZE;
            var closeY1 = closeY0 + CLOSE_BUTTON_SIZE;

            var closeHovered = mouseX >= closeX0 && mouseX < closeX1 && mouseY >= closeY0 && mouseY < closeY1;
            drawCloseIcon(graphics, closeX0, closeY0, closeX1, closeY1, closeHovered ? CLOSE_ICON_HOVER_COLOR : CLOSE_ICON_COLOR);

            graphics.fill(tabRight, y, tabRight + 1, y + TAB_BAR_HEIGHT, TAB_SEPARATOR_COLOR);

            tabRects.add(new TabRect(cursorX, tabRight, closeX0, closeY0, closeX1, closeY1));
            cursorX = tabRight + 1;
        }
        PanelScissor.disable(graphics);

        if (overflowButtonVisible) {
            renderOverflowButton(graphics, mouseX, mouseY);
        }
    }

    /**
     * Diagonal-cross "×" drawn as two 1-px diagonals between the corners of the close button rect. Looks crisp at MC's
     * pixel-fitted GUI scale; pure {@code graphics.fill} so no font / texture dependency.
     */
    private static void drawCloseIcon(GuiGraphics graphics, int x0, int y0, int x1, int y1, int color) {
        var w = x1 - x0;
        var h = y1 - y0;
        for (var i = 0; i < Math.min(w, h); i++) {
            graphics.fill(x0 + i, y0 + i, x0 + i + 1, y0 + i + 1, color);
            graphics.fill(x0 + i, y1 - 1 - i, x0 + i + 1, y1 - i, color);
        }
    }

    private void renderOverflowButton(GuiGraphics graphics, int mouseX, int mouseY) {
        var hovered = hitOverflowButtonAt(mouseX, mouseY);
        var bg = hovered ? OVERFLOW_BUTTON_HOVER_BG_COLOR : OVERFLOW_BUTTON_BG_COLOR;
        var buttonWidth = overflowButtonWidth();
        graphics.fill(overflowButtonX, overflowButtonY, overflowButtonX + buttonWidth, overflowButtonY + TAB_BAR_HEIGHT, bg);
        graphics.fill(overflowButtonX, overflowButtonY, overflowButtonX + 1, overflowButtonY + TAB_BAR_HEIGHT, TAB_SEPARATOR_COLOR);

        var font = EngineFont.get();
        var labelWidth = font.width(OVERFLOW_BUTTON_LABEL);
        var labelX = overflowButtonX + (buttonWidth - labelWidth) / 2;
        var labelY = overflowButtonY + (TAB_BAR_HEIGHT - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(OVERFLOW_BUTTON_LABEL), labelX, labelY, TAB_TEXT_ACTIVE_COLOR, false);
    }

    public int rectX() {
        return rectX;
    }

    public int rectY() {
        return rectY;
    }

    public int rectWidth() {
        return rectWidth;
    }

    public int rectHeight() {
        return rectHeight;
    }

    public boolean isInTabStrip(double mouseX, double mouseY) {
        return mouseX >= rectX && mouseX < rectX + rectWidth && mouseY >= rectY && mouseY < rectY + TAB_BAR_HEIGHT;
    }

    public boolean scrollTabStrip(double scrollX, double scrollY) {
        var maxScroll = maxTabScroll();
        if (maxScroll <= 0) {
            return false;
        }
        var delta = 0;
        if (scrollX != 0.0) {
            delta += (int) Math.round(scrollX * TAB_SCROLL_STEP);
        }
        if (scrollY != 0.0) {
            delta -= (int) Math.round(scrollY * TAB_SCROLL_STEP);
        }
        if (delta != 0) {
            tabScrollX = clampTabScroll(tabScrollX + delta);
            revealActiveTab = false;
        }
        return true;
    }

    public boolean hitOverflowButtonAt(double mouseX, double mouseY) {
        return overflowButtonVisible
            && mouseX >= overflowButtonX
            && mouseX < overflowButtonX + overflowButtonWidth()
            && mouseY >= overflowButtonY
            && mouseY < overflowButtonY + TAB_BAR_HEIGHT;
    }

    public int overflowMenuAnchorX() {
        return overflowButtonX;
    }

    public int overflowMenuAnchorY() {
        return overflowButtonY + TAB_BAR_HEIGHT + 1;
    }

    public List<HiddenTab> hiddenTabs() {
        if (!overflowButtonVisible || tabs.isEmpty()) {
            return List.of();
        }
        var font = EngineFont.get();
        var hidden = new ArrayList<HiddenTab>();
        for (var i = 0; i < tabs.size(); i++) {
            if (!isTabFullyVisible(font, i)) {
                hidden.add(new HiddenTab(i, tabs.get(i).title(), i == activeIndex));
            }
        }
        return hidden;
    }

    /** Returns the tab index hit by {@code (mouseX, mouseY)} in the tab strip, or -1 if none. */
    public int hitTabAt(double mouseX, double mouseY) {
        if (!isInTabStrip(mouseX, mouseY)) {
            return -1;
        }
        if (mouseX >= rectX + tabViewportWidth) {
            return -1;
        }
        for (var i = 0; i < tabRects.size(); i++) {
            var r = tabRects.get(i);
            if (mouseX >= r.left && mouseX < r.right) {
                return i;
            }
        }
        return -1;
    }

    public boolean hitCloseAt(double mouseX, double mouseY, int tabIndex) {
        if (tabIndex < 0 || tabIndex >= tabRects.size()) {
            return false;
        }
        var r = tabRects.get(tabIndex);
        return mouseX >= r.closeX0 && mouseX < r.closeX1 && mouseY >= r.closeY0 && mouseY < r.closeY1;
    }

    /**
     * Where to insert a dropped tab given the cursor's X position over the strip — left of any tab whose mid-x is past
     * the cursor, else at the end. Returns {@code 0..tabs.size()}.
     */
    public int dropInsertionIndex(double mouseX) {
        if (tabRects.isEmpty()) {
            return 0;
        }
        for (var i = 0; i < tabRects.size(); i++) {
            var r = tabRects.get(i);
            var midX = (r.left + r.right) / 2.0;
            if (mouseX < midX) {
                return i;
            }
        }
        return tabRects.size();
    }

    @Override
    protected @Nullable Panel activeChild() {
        return activeTab();
    }

    @Override
    protected boolean inContentArea(double mouseX, double mouseY) {
        // Tab-strip clicks are owned by the workspace screen so tab drag can span panels; everything below the strip
        // is content and forwards to the active tab.
        return mouseY >= rectY + TAB_BAR_HEIGHT;
    }

    private record TabRect(
        int left,
        int right,
        int closeX0,
        int closeY0,
        int closeX1,
        int closeY1
    ) {}

    public record HiddenTab(
        int index,
        String title,
        boolean active
    ) {}

    private int computeTabContentWidth(Font font) {
        var width = 0;
        for (var i = 0; i < tabs.size(); i++) {
            width += tabWidth(font, i) + 1;
        }
        return width;
    }

    private int tabWidth(Font font, int index) {
        var labelWidth = font.width(tabs.get(index).title());
        return TAB_PADDING_X + labelWidth + TAB_LABEL_TO_CLOSE_GAP + CLOSE_BUTTON_SIZE + TAB_PADDING_X;
    }

    private int tabLeft(Font font, int index) {
        var left = 0;
        for (var i = 0; i < index && i < tabs.size(); i++) {
            left += tabWidth(font, i) + 1;
        }
        return left;
    }

    private boolean isActiveTabVisible(Font font) {
        if (tabs.isEmpty() || tabViewportWidth <= 0) {
            return true;
        }
        return isTabFullyVisible(font, activeIndex);
    }

    private boolean isTabFullyVisible(Font font, int index) {
        if (index < 0 || index >= tabs.size() || tabViewportWidth <= 0) {
            return false;
        }
        var left = tabLeft(font, index);
        var right = left + tabWidth(font, index);
        return left >= tabScrollX && right <= tabScrollX + tabViewportWidth;
    }

    private void revealActiveTab(Font font) {
        if (tabs.isEmpty() || tabViewportWidth <= 0) {
            tabScrollX = 0;
            return;
        }
        var left = tabLeft(font, activeIndex);
        var right = left + tabWidth(font, activeIndex);
        if (left < tabScrollX) {
            tabScrollX = left;
        } else if (right > tabScrollX + tabViewportWidth) {
            tabScrollX = right - tabViewportWidth;
        }
        tabScrollX = clampTabScroll(tabScrollX);
    }

    private int maxTabScroll() {
        return Math.max(0, tabContentWidth - tabViewportWidth);
    }

    private int clampTabScroll(int value) {
        return Math.max(0, Math.min(maxTabScroll(), value));
    }

    private int overflowButtonWidth() {
        return Math.min(OVERFLOW_BUTTON_WIDTH, Math.max(0, rectWidth));
    }
}
