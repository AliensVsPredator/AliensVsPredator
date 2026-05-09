package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A leaf container that hosts a list of child panels as switchable tabs. Renders a tab strip across the top with the
 * active tab's title highlighted and an {@code ×} close button per tab; below the strip, the active tab gets the
 * remaining content area to render into.
 * <p>
 * Tab interaction (click-to-switch, click-X-to-close, drag-to-move) is split between this class and
 * {@link EngineWorkspaceScreen}: the panel exposes hit-test queries against its tab strip, and the screen owns the drag
 * state machine so tabs can move <em>between</em> {@code TabbedPanel}s as well as within one.
 * <p>
 * An empty {@code TabbedPanel} renders as just a tab-strip background — the user can drop a tab from another panel into
 * it to repopulate.
 */
@ApiStatus.Internal
public final class TabbedPanel implements Panel {

    public static final int TAB_BAR_HEIGHT = 12;

    private static final int TAB_PADDING_X = 4;

    private static final int TAB_LABEL_TO_CLOSE_GAP = 4;

    private static final int CLOSE_BUTTON_SIZE = 6;

    private static final int STRIP_BG_COLOR = 0xFF161618;

    private static final int TAB_INACTIVE_BG_COLOR = 0xFF1F1F23;

    private static final int TAB_ACTIVE_BG_COLOR = 0xFF2F2F36;

    private static final int TAB_HOVER_BG_COLOR = 0xFF353540;

    private static final int TAB_TEXT_INACTIVE_COLOR = 0xFF888892;

    private static final int TAB_TEXT_ACTIVE_COLOR = 0xFFE0E0E0;

    private static final int TAB_SEPARATOR_COLOR = 0xFF101013;

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
            if (changed) {
                tabs.get(index).onShown();
            }
        }
    }

    public void removeTab(int index) {
        if (index < 0 || index >= tabs.size()) {
            return;
        }
        tabs.remove(index);
        if (activeIndex >= tabs.size()) {
            activeIndex = Math.max(0, tabs.size() - 1);
        }
    }

    public void insertTab(int index, Panel tab) {
        var clamped = Math.max(0, Math.min(tabs.size(), index));
        tabs.add(clamped, tab);
        activeIndex = clamped;
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

        var font = Minecraft.getInstance().font;
        var cursorX = x;

        for (var i = 0; i < tabs.size(); i++) {
            var label = tabs.get(i).title();
            var labelWidth = font.width(label);
            var tabWidth = TAB_PADDING_X + labelWidth + TAB_LABEL_TO_CLOSE_GAP + CLOSE_BUTTON_SIZE + TAB_PADDING_X;

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

    /** Returns the tab index hit by {@code (mouseX, mouseY)} in the tab strip, or -1 if none. */
    public int hitTabAt(double mouseX, double mouseY) {
        if (!isInTabStrip(mouseX, mouseY)) {
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Tab-strip clicks are handled by the workspace screen (so tab drag can span panels). Only delegate
        // content-area clicks to the active tab.
        if (mouseY < rectY + TAB_BAR_HEIGHT) {
            return false;
        }
        var active = activeTab();
        return active != null && active.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (mouseY < rectY + TAB_BAR_HEIGHT) {
            return false;
        }
        var active = activeTab();
        return active != null && active.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (mouseY < rectY + TAB_BAR_HEIGHT) {
            return false;
        }
        var active = activeTab();
        return active != null && active.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseY < rectY + TAB_BAR_HEIGHT) {
            return false;
        }
        var active = activeTab();
        return active != null && active.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public @Nullable Component tooltipText() {
        // Forward to the active tab — the active tab's render() received the mouse coords and already cached its
        // own tooltip if applicable. Tab-strip tooltips (e.g. full title on hover) aren't implemented here yet.
        var active = activeTab();
        return active != null ? active.tooltipText() : null;
    }

    private record TabRect(
        int left,
        int right,
        int closeX0,
        int closeY0,
        int closeX1,
        int closeY1
    ) {}
}
