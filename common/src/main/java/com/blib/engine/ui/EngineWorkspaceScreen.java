package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.session.EngineMode;
import com.blib.engine.session.NavigationMode;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SGOAPTrackPayload;
import com.blib.mod.common.network.packet.C2SRemoveEntityPayload;

/**
 * Top-level editor screen for the BLib Engine. The viewport is divided into a tree of docked regions by a
 * {@link DockNode}; each leaf wraps a {@link Panel}. Content leaves wrap their panels in a {@link TabbedPanel} so users
 * can swap, close, and drag tabs between panels at runtime; trim leaves (menu / toolbar / status bars) hold raw panels
 * and don't tab.
 * <p>
 * Drawing is wrapped in a {@link #SCALE} pose scale so layout uses logical pixels (~2.67× the GUI-units screen at
 * 0.375). Mouse coordinates from event callbacks are scaled the same way before tree-walks.
 * <p>
 * Layout sketch:
 *
 * <pre>
 * +-------------------------------------------------------------+
 * | Menu bar (full-bleed)                                       |
 * +-------------------------------------------------------------+
 * | Toolbar (play/pause centered)                               |
 * +----------+--------------------------------+-----------------+
 * | Outliner |   Viewport                     |   Details       |
 * |  (tab)   |   (downsampled world+HUD)      |    (tab)        |
 * |          +--------------------------------+                 |
 * |          |   Content Browser (tab)        |                 |
 * +----------+--------------------------------+-----------------+
 * | Status bar (full-bleed)                                     |
 * +-------------------------------------------------------------+
 * </pre>
 *
 * Dividers between resizable regions can be dragged to repartition space; trim bars (menu / toolbar / status) are
 * pinned. Tabs can be clicked to switch, ×'d to close, and dragged across {@code TabbedPanel}s to rearrange.
 */
@ApiStatus.Internal
public final class EngineWorkspaceScreen extends Screen {

    /**
     * Pose scale applied to the entire workspace render. {@code 0.375f = 0.5 × 0.75} — three-quarters of the GOAP debug
     * HUD's text size, so editor text is denser without sacrificing legibility. Layout uses logical pixels; physical
     * size equals {@code logical × SCALE}.
     */
    private static final float SCALE = 0.375f;

    private static final int OUTLINER_WIDTH_DEFAULT = 150;

    private static final int DETAILS_WIDTH_DEFAULT = 190;

    private static final int CONTENT_BROWSER_HEIGHT_DEFAULT = 140;

    /**
     * Mouse-pixel half-thickness of a divider's hit zone, in logical pixels. A click within {@code DIVIDER_HIT_PX} of
     * the boundary line is treated as a divider drag-start.
     */
    private static final int DIVIDER_HIT_PX = 4;

    /**
     * Floor on any panel size during a divider drag (logical pixels). Prevents the user from collapsing a panel to zero
     * width / height where it would be unrecoverable without resetting the workspace.
     */
    private static final int MIN_PANEL_SIZE_PX = 24;

    /**
     * Squared cursor-motion threshold (logical pixels) to promote a pending tab click into an active drag. Below this,
     * a press-and-release on a tab is just an "activate this tab" click.
     */
    private static final double TAB_DRAG_THRESHOLD_SQ = 16.0;

    private static final int DIVIDER_HIGHLIGHT_COLOR = 0xFF4F8FFF;

    private static final int TAB_GHOST_BG_COLOR = 0xCC2C2C32;

    private static final int TAB_GHOST_TEXT_COLOR = 0xFFE0E0E0;

    private static final int TAB_DROP_TARGET_COLOR = 0x404F8FFF;

    private static final int TOOLTIP_BG_COLOR = 0xF01A1A1F;

    private static final int TOOLTIP_BORDER_COLOR = 0xFF353540;

    private static final int TOOLTIP_TEXT_COLOR = 0xFFD0D0D0;

    /**
     * Sentinel mouse coordinate used in place of the real one when the cursor is over an overlay (e.g. an open
     * dropdown) so panels rendered underneath universally fail their contains-point hover checks.
     * {@link Integer#MIN_VALUE} is far enough out that no reasonable rect will overlap it but small enough to survive
     * {@code int} arithmetic.
     */
    private static final int OFFSCREEN_MOUSE = Integer.MIN_VALUE / 2;

    private DockNode root;

    private @Nullable ActiveDrag activeDrag;

    private @Nullable TabDrag tabDrag;

    private @Nullable DropdownMenu openMenu;

    public EngineWorkspaceScreen() {
        super(Component.literal("BLib Engine"));

        // Freeze the integrated server immediately on open so the world is paused while the user works in the editor.
        // The previous freeze state is captured here and restored in removed() so we don't unfreeze a server that the
        // user had already frozen via /tick freeze before opening the workspace.
        EngineTickControl.captureAndPause();

        // Engine mode (the freecam) is owned by the workspace now — there's no /blib engine fly|orbit anymore. Enter
        // engine mode in ORBIT navigation; the viewport panel forwards LMB drag / scroll / click events to apply.
        EngineMode.get().enter();
        var session = EngineMode.get().session();
        if (session != null) {
            session.setMode(NavigationMode.ORBIT);
        }

        this.root = buildDefaultLayout();
    }

    /**
     * Build the workspace's default dock tree from scratch with fresh panel + sizing instances. Called on screen
     * construction and on Window → Reset Layout. Non-static so we can wire {@code this::onViewportRightClick} into each
     * fresh {@link ViewportPanel}.
     */
    private DockNode buildDefaultLayout() {
        var viewportColumn = new DockNode.Split(
            Orientation.VERTICAL,
            new DockNode.Leaf(new TabbedPanel(new ViewportPanel("Viewport", this::onViewportRightClick))),
            new DockNode.Leaf(new TabbedPanel(new ContentBrowserPanel())),
            new Sizing.SecondFixed(CONTENT_BROWSER_HEIGHT_DEFAULT)
        );

        var centerAndDetails = new DockNode.Split(
            Orientation.HORIZONTAL,
            viewportColumn,
            new DockNode.Leaf(new TabbedPanel(new DetailsPanel())),
            new Sizing.SecondFixed(DETAILS_WIDTH_DEFAULT)
        );

        var workspaceBody = new DockNode.Split(
            Orientation.HORIZONTAL,
            new DockNode.Leaf(new TabbedPanel(new OutlinerPanel())),
            centerAndDetails,
            new Sizing.FirstFixed(OUTLINER_WIDTH_DEFAULT)
        );

        var bodyAndStatus = new DockNode.Split(
            Orientation.VERTICAL,
            workspaceBody,
            new DockNode.Leaf(new StatusBarPanel()),
            new Sizing.SecondFixed(StatusBarPanel.HEIGHT)
        );

        var toolbarAndBelow = new DockNode.Split(
            Orientation.VERTICAL,
            new DockNode.Leaf(new ToolbarPanel()),
            bodyAndStatus,
            new Sizing.FirstFixed(ToolbarPanel.HEIGHT)
        );

        return new DockNode.Split(
            Orientation.VERTICAL,
            new DockNode.Leaf(new MenuBarPanel()),
            toolbarAndBelow,
            new Sizing.FirstFixed(MenuBarPanel.HEIGHT)
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * Suppress the default screen background so the workspace overlays the live world without dimming it. Panels that
     * want a backdrop fill it themselves; the viewport panel hosts a downsampled blit of the world+HUD via
     * {@link EngineWorkspaceCompositor}.
     */
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        // The world + HUD have already been rendered to the main RT at full window resolution by the time screen
        // render runs. Downsample-blit them into the viewport panel rect so the user sees the player-eye view shrunk
        // to fit the viewport, with all proportions / FOV / HUD layout intact.
        var viewportRect = findViewportRect(root, 0, 0, logicalWidth(), logicalHeight());
        if (viewportRect != null) {
            compositWorldIntoViewport(viewportRect);
        }

        var pose = graphics.pose();
        pose.pushPose();
        pose.scale(SCALE, SCALE, 1.0f);

        int logicalWidth = logicalWidth();
        int logicalHeight = logicalHeight();
        int logicalMouseX = (int) (mouseX / SCALE);
        int logicalMouseY = (int) (mouseY / SCALE);

        // While a dropdown menu is open AND the cursor is over the menu rect, panels under the menu must not see the
        // mouse — otherwise their hover-state code (segmented-control buttons, viewport selection highlights, tab-
        // strip hover, content-browser cells, etc.) lights up under the menu, leaking interaction state through the
        // popover. Substituting a sentinel "off-screen" mouse position for the panel render walk makes every panel's
        // contains-point check fail uniformly without requiring each panel to know about the menu. Divider / tab-drag
        // / tooltip overlays already check {@code openMenu} themselves and stay suppressed.
        int panelMouseX = logicalMouseX;
        int panelMouseY = logicalMouseY;
        if (openMenu != null && openMenu.isInside(logicalMouseX, logicalMouseY)) {
            panelMouseX = OFFSCREEN_MOUSE;
            panelMouseY = OFFSCREEN_MOUSE;
        }

        renderNode(graphics, root, 0, 0, logicalWidth, logicalHeight, panelMouseX, panelMouseY, partialTick);
        renderHoveredDivider(graphics, logicalMouseX, logicalMouseY);
        renderTabDragOverlay(graphics, logicalMouseX, logicalMouseY);

        if (openMenu != null) {
            openMenu.render(graphics, logicalMouseX, logicalMouseY);
        }

        renderHoverTooltip(graphics, logicalMouseX, logicalMouseY);

        pose.popPose();
    }

    /**
     * Asks the panel under the cursor for tooltip text and, if any, draws it as a small floating box near the cursor.
     * Suppressed while a dropdown menu is open (tooltips would visually fight with the menu) and while a divider or tab
     * is being dragged (the user's focus is on the drag, not the panel beneath).
     */
    private void renderHoverTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (openMenu != null || activeDrag != null || (tabDrag != null && tabDrag.active)) {
            return;
        }
        var leaf = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), mouseX, mouseY);
        if (leaf == null) {
            return;
        }
        var tip = leaf.tooltipText();
        if (tip == null) {
            return;
        }
        drawTooltipBox(graphics, tip, mouseX, mouseY);
    }

    /**
     * Draws a single-line tooltip for {@code text} positioned next to the cursor, kept inside the workspace bounds.
     * Manual rendering rather than {@code GuiGraphics.renderTooltip} so the styling matches the workspace's flat dark
     * theme and so we control sizing in workspace-logical pixels (vanilla's tooltip is sized in raw GUI pixels and
     * looks oversized inside our 0.375-scaled workspace).
     */
    private void drawTooltipBox(GuiGraphics graphics, Component text, int mouseX, int mouseY) {
        var font = Minecraft.getInstance().font;
        var textWidth = font.width(text);
        var lineHeight = font.lineHeight;
        var paddingX = 3;
        var paddingY = 2;
        var boxW = textWidth + paddingX * 2;
        var boxH = lineHeight + paddingY * 2;
        // Default position: just to the right of and below the cursor, with a small offset.
        var tipX = mouseX + 8;
        var tipY = mouseY + 8;
        // Keep the box inside the workspace; flip to the other side of the cursor if it'd overflow right/bottom.
        if (tipX + boxW > logicalWidth()) {
            tipX = mouseX - 4 - boxW;
        }
        if (tipY + boxH > logicalHeight()) {
            tipY = mouseY - 4 - boxH;
        }
        // Background + 1-px border in the same flat-dark style the rest of the workspace uses.
        graphics.fill(tipX, tipY, tipX + boxW, tipY + boxH, TOOLTIP_BG_COLOR);
        graphics.fill(tipX, tipY, tipX + boxW, tipY + 1, TOOLTIP_BORDER_COLOR);
        graphics.fill(tipX, tipY + boxH - 1, tipX + boxW, tipY + boxH, TOOLTIP_BORDER_COLOR);
        graphics.fill(tipX, tipY, tipX + 1, tipY + boxH, TOOLTIP_BORDER_COLOR);
        graphics.fill(tipX + boxW - 1, tipY, tipX + boxW, tipY + boxH, TOOLTIP_BORDER_COLOR);
        // Center the visible glyph within the box (rather than just using paddingY for the top), with the +1 to
        // compensate for MC font's descender padding — see MenuBarPanel.
        var textY = tipY + (boxH - lineHeight + 1) / 2;
        graphics.drawString(font, text, tipX + paddingX, textY, TOOLTIP_TEXT_COLOR, false);
    }

    private void renderTabDragOverlay(GuiGraphics graphics, int mouseX, int mouseY) {
        if (tabDrag == null || !tabDrag.active) {
            return;
        }

        // Highlight the drop target — strip area for tab merges, half-rect for edge splits, full content for
        // center-zone merges.
        var target = findTabbedPanelAt(mouseX, mouseY);
        if (target != null) {
            if (target.isInTabStrip(mouseX, mouseY)) {
                graphics.fill(
                    target.rectX(),
                    target.rectY(),
                    target.rectX() + target.rectWidth(),
                    target.rectY() + TabbedPanel.TAB_BAR_HEIGHT,
                    TAB_DROP_TARGET_COLOR
                );
            } else {
                renderDropZoneOverlay(graphics, target, computeDropZoneInContent(target, mouseX, mouseY));
            }
        }

        // Ghost: a translucent tab-shaped chip floating with the cursor.
        var font = Minecraft.getInstance().font;
        var label = tabDrag.tab.title();
        var labelWidth = font.width(label);
        var w = labelWidth + 12;
        var h = TabbedPanel.TAB_BAR_HEIGHT;
        var x = mouseX - w / 2;
        var y = mouseY - h / 2;
        graphics.fill(x, y, x + w, y + h, TAB_GHOST_BG_COLOR);
        // +2 compensates for MC font's descender padding so the tab-drag ghost label visually centers; see
        // MenuBarPanel.
        graphics.drawString(font, Component.literal(label), x + 6, y + (h - font.lineHeight + 2) / 2, TAB_GHOST_TEXT_COLOR, false);
    }

    /**
     * Map the cursor's position over a {@link TabbedPanel}'s content area (excluding the tab strip) to a
     * {@link DropZone}. The middle 50% × 50% of the content rect is the {@code CENTER} (tab-merge) zone; outside that
     * inner rect, the closest edge defines the split direction.
     */
    private static DropZone computeDropZoneInContent(TabbedPanel target, double mouseX, double mouseY) {
        var contentY = target.rectY() + TabbedPanel.TAB_BAR_HEIGHT;
        var contentH = Math.max(1, target.rectHeight() - TabbedPanel.TAB_BAR_HEIGHT);
        var contentW = Math.max(1, target.rectWidth());

        var relX = (mouseX - target.rectX()) / contentW;
        var relY = (mouseY - contentY) / contentH;

        if (relX > 0.25 && relX < 0.75 && relY > 0.25 && relY < 0.75) {
            return DropZone.CENTER;
        }

        var distLeft = relX;
        var distRight = 1.0 - relX;
        var distTop = relY;
        var distBottom = 1.0 - relY;
        var minDist = Math.min(Math.min(distLeft, distRight), Math.min(distTop, distBottom));

        if (minDist == distLeft) {
            return DropZone.LEFT;
        }
        if (minDist == distRight) {
            return DropZone.RIGHT;
        }
        if (minDist == distTop) {
            return DropZone.TOP;
        }
        return DropZone.BOTTOM;
    }

    private static void renderDropZoneOverlay(GuiGraphics graphics, TabbedPanel target, DropZone zone) {
        var x = target.rectX();
        var w = target.rectWidth();
        var contentY = target.rectY() + TabbedPanel.TAB_BAR_HEIGHT;
        var contentH = Math.max(0, target.rectHeight() - TabbedPanel.TAB_BAR_HEIGHT);

        var x0 = x;
        var y0 = contentY;
        var x1 = x + w;
        var y1 = contentY + contentH;
        switch (zone) {
            case CENTER -> {
                // full content rect
            }
            case TOP -> y1 = contentY + contentH / 2;
            case BOTTOM -> y0 = contentY + contentH / 2;
            case LEFT -> x1 = x + w / 2;
            case RIGHT -> x0 = x + w / 2;
        }
        graphics.fill(x0, y0, x1, y1, TAB_DROP_TARGET_COLOR);
    }

    /**
     * Convert the viewport panel's logical rect (top-left origin, in workspace logical pixels) to GL framebuffer coords
     * (bottom-left origin, in raw window pixels) and ask the compositor to downsample-blit world+HUD into it.
     */
    private void compositWorldIntoViewport(LogicalRect rect) {
        var window = Minecraft.getInstance().getWindow();
        var guiScale = window.getGuiScale();
        var rawWindowHeight = window.getHeight();

        var screenX = rect.x() * SCALE;
        var screenY = rect.y() * SCALE;
        var screenW = rect.width() * SCALE;
        var screenH = rect.height() * SCALE;

        var rawX = (int) Math.round(screenX * guiScale);
        var rawY = (int) Math.round(rawWindowHeight - (screenY + screenH) * guiScale);
        var rawW = (int) Math.round(screenW * guiScale);
        var rawH = (int) Math.round(screenH * guiScale);

        EngineWorkspaceCompositor.composit(rawX, rawY, rawW, rawH);
    }

    @Override
    public void removed() {
        super.removed();
        EngineWorkspaceCompositor.clear();
        EngineMode.get().exit();
        EngineTickControl.restore();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        var logicalX = mouseX / SCALE;
        var logicalY = mouseY / SCALE;
        // Scroll-wheel events that land on an open menu shouldn't tunnel through to the scroll containers of panels
        // below — consume them.
        if (openMenu != null && openMenu.isInside(logicalX, logicalY)) {
            return true;
        }
        var leaf = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
        if (leaf != null && leaf.mouseScrolled(logicalX, logicalY, scrollX, scrollY)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    /**
     * Walks the dock tree to find the rect (in logical pixels) of the workspace's active viewport — the inner rect of
     * the {@link TabbedPanel} whose active tab is a {@link ViewportPanel}, with the tab strip excluded. Returns null if
     * no panel currently shows the viewport.
     */
    private static @Nullable LogicalRect findViewportRect(DockNode node, int x, int y, int width, int height) {
        return switch (node) {
            case DockNode.Leaf leaf -> {
                var panel = leaf.panel();
                if (panel instanceof TabbedPanel tabbed && tabbed.activeTab() instanceof ViewportPanel) {
                    var contentY = y + TabbedPanel.TAB_BAR_HEIGHT + 1;
                    var contentH = Math.max(0, height - TabbedPanel.TAB_BAR_HEIGHT - 1);
                    yield new LogicalRect(x, contentY, width, contentH);
                }
                yield null;
            }
            case DockNode.Split split -> {
                if (split.orientation() == Orientation.HORIZONTAL) {
                    var firstWidth = DockNode.boundary(split.sizing(), width);
                    var inFirst = findViewportRect(split.first(), x, y, firstWidth, height);
                    if (inFirst != null) {
                        yield inFirst;
                    }
                    yield findViewportRect(split.second(), x + firstWidth, y, width - firstWidth, height);
                }
                var firstHeight = DockNode.boundary(split.sizing(), height);
                var inFirst = findViewportRect(split.first(), x, y, width, firstHeight);
                if (inFirst != null) {
                    yield inFirst;
                }
                yield findViewportRect(split.second(), x, y + firstHeight, width, height - firstHeight);
            }
        };
    }

    private record LogicalRect(
        int x,
        int y,
        int width,
        int height
    ) {}

    private void renderHoveredDivider(GuiGraphics graphics, int mouseX, int mouseY) {
        // While a dropdown menu is open, suppress divider hover feedback — the dropdown should consume hover
        // interactions over its area, and showing a divider highlight underneath misleads the user into thinking
        // they can drag a divider through the menu.
        if (activeDrag == null && openMenu != null) {
            return;
        }
        var dragger = activeDrag != null
            ? activeDrag.divider
            : findDivider(root, 0, 0, logicalWidth(), logicalHeight(), mouseX, mouseY);
        if (dragger == null) {
            return;
        }

        var bx = dragger.boundaryStartX();
        var by = dragger.boundaryStartY();
        if (dragger.split.orientation() == Orientation.HORIZONTAL) {
            graphics.fill(bx - 1, by, bx + 1, by + dragger.parentHeight, DIVIDER_HIGHLIGHT_COLOR);
        } else {
            graphics.fill(bx, by - 1, bx + dragger.parentWidth, by + 1, DIVIDER_HIGHLIGHT_COLOR);
        }
    }

    private static void renderNode(
        GuiGraphics graphics,
        DockNode node,
        int x,
        int y,
        int width,
        int height,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        switch (node) {
            case DockNode.Leaf leaf -> {
                var panel = leaf.panel();
                if (panel.hasChrome()) {
                    var inner = PanelChrome.draw(graphics, x, y, width, height, panel.title());
                    panel.render(graphics, inner.x(), inner.y(), inner.width(), inner.height(), mouseX, mouseY, partialTick);
                } else {
                    panel.render(graphics, x, y, width, height, mouseX, mouseY, partialTick);
                }
            }
            case DockNode.Split split -> {
                if (split.orientation() == Orientation.HORIZONTAL) {
                    var firstWidth = DockNode.boundary(split.sizing(), width);
                    renderNode(graphics, split.first(), x, y, firstWidth, height, mouseX, mouseY, partialTick);
                    renderNode(graphics, split.second(), x + firstWidth, y, width - firstWidth, height, mouseX, mouseY, partialTick);
                } else {
                    var firstHeight = DockNode.boundary(split.sizing(), height);
                    renderNode(graphics, split.first(), x, y, width, firstHeight, mouseX, mouseY, partialTick);
                    renderNode(graphics, split.second(), x, y + firstHeight, width, height - firstHeight, mouseX, mouseY, partialTick);
                }
            }
        }
    }

    @Override
    public boolean charTyped(char ch, int modifiers) {
        var focused = TextInput.getFocused();
        if (focused != null && focused.charTyped(ch, modifiers)) {
            return true;
        }
        return super.charTyped(ch, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        var focused = TextInput.getFocused();
        if (focused != null && focused.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var logicalX = mouseX / SCALE;
        var logicalY = mouseY / SCALE;

        // Clear focus before dispatch — panels with text inputs will re-focus their own inputs in their
        // mouseClicked handlers if the click hit the input rect, otherwise focus stays cleared (click-outside
        // defocus).
        TextInput.clearFocus();

        // 0) An open dropdown takes priority: clicking an item fires it; clicking outside just closes the menu.
        if (openMenu != null) {
            if (button == 0) {
                var idx = openMenu.hitItemAt(logicalX, logicalY);
                if (idx >= 0) {
                    var item = openMenu.itemAt(idx);
                    openMenu = null;
                    item.action().run();
                    return true;
                }
            }
            openMenu = null;
            // Only fall through to chip-click handling below if the cursor landed on another menu chip — that lets
            // the user close-and-reopen by clicking a different chip in one motion. Anything else (clicks on
            // dividers, tab strips, panel content) is consumed so dropdown clicks never accidentally start a
            // divider drag or activate the panel beneath the menu.
            var underClose = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
            if (!(underClose instanceof MenuBarPanel menuBarUnderClose) || menuBarUnderClose.hitChipAt(logicalX, logicalY) == null) {
                return true;
            }
        }

        if (button == 0) {
            // 1) Menu-bar chip click: open dropdown.
            var underCursor = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
            if (underCursor instanceof MenuBarPanel menuBar) {
                var chip = menuBar.hitChipAt(logicalX, logicalY);
                if (chip != null) {
                    var menu = buildMenuFor(chip, menuBar);
                    if (menu != null) {
                        openMenu = menu;
                    }
                    return true;
                }
            }

            // 2) Tab strip click: switch active, close, or arm a tab drag.
            var tabbed = findTabbedPanelAt((int) logicalX, (int) logicalY);
            if (tabbed != null && tabbed.isInTabStrip(logicalX, logicalY)) {
                var tabIdx = tabbed.hitTabAt(logicalX, logicalY);
                if (tabIdx >= 0) {
                    if (tabbed.hitCloseAt(logicalX, logicalY, tabIdx)) {
                        tabbed.removeTab(tabIdx);
                        simplifyDockTree();
                        return true;
                    }
                    tabbed.setActiveIndex(tabIdx);
                    this.tabDrag = new TabDrag(tabbed, tabIdx, tabbed.tabs().get(tabIdx), logicalX, logicalY);
                    return true;
                }
                // Click on empty tab-strip space — no-op but consume so it doesn't fall through to content.
                return true;
            }

            // 3) Divider drag start.
            var divider = findDivider(root, 0, 0, logicalWidth(), logicalHeight(), (int) logicalX, (int) logicalY);
            if (divider != null && isResizable(divider.split.sizing())) {
                this.activeDrag = new ActiveDrag(divider);
                return true;
            }
        }

        // 4) Otherwise, delegate to the panel under the cursor for content-area handling.
        var leaf = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
        if (leaf != null && leaf.mouseClicked(logicalX, logicalY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var logicalX = mouseX / SCALE;
        var logicalY = mouseY / SCALE;

        if (button == 0 && tabDrag != null) {
            if (tabDrag.active) {
                completeTabDrop(tabDrag, logicalX, logicalY);
                simplifyDockTree();
            }
            tabDrag = null;
            return true;
        }

        if (button == 0 && activeDrag != null) {
            activeDrag = null;
            return true;
        }

        // Releases over an open menu shouldn't reach panels below it.
        if (openMenu != null && openMenu.isInside(logicalX, logicalY)) {
            return true;
        }

        var leaf = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
        if (leaf != null && leaf.mouseReleased(logicalX, logicalY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        var logicalX = mouseX / SCALE;
        var logicalY = mouseY / SCALE;

        if (tabDrag != null) {
            if (!tabDrag.active) {
                var dx = logicalX - tabDrag.startX;
                var dy = logicalY - tabDrag.startY;
                if (dx * dx + dy * dy > TAB_DRAG_THRESHOLD_SQ) {
                    tabDrag.active = true;
                }
            }
            return true;
        }

        if (activeDrag != null) {
            applyDrag(activeDrag.divider, logicalX, logicalY);
            return true;
        }

        // Drags over an open menu shouldn't hit panels below it.
        if (openMenu != null && openMenu.isInside(logicalX, logicalY)) {
            return true;
        }

        var leaf = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), logicalX, logicalY);
        if (leaf != null && leaf.mouseDragged(logicalX, logicalY, button, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    /**
     * Finalize a tab drag at release time. Behavior depends on the drop zone:
     * <ul>
     * <li>Tab strip or content {@code CENTER}: tab merges into the target's tab list (reorder if same source, insert
     * otherwise).</li>
     * <li>Content edge ({@code TOP/BOTTOM/LEFT/RIGHT}): the target leaf is replaced in the dock tree with a new
     * {@link DockNode.Split} containing the original target on one side and a new {@link TabbedPanel} (holding the
     * dragged tab) on the other. A subsequent {@link #simplifyDockTree()} call collapses the source if it's now
     * empty.</li>
     * </ul>
     * Cursor outside any tab panel: drag cancels, tab stays put.
     */
    private void completeTabDrop(TabDrag drag, double logicalX, double logicalY) {
        var target = findTabbedPanelAt((int) logicalX, (int) logicalY);
        if (target == null) {
            return;
        }

        var zone = target.isInTabStrip(logicalX, logicalY)
            ? DropZone.CENTER
            : computeDropZoneInContent(target, logicalX, logicalY);

        if (zone == DropZone.CENTER) {
            mergeTab(drag, target, logicalX);
            return;
        }

        // Edge drop → split the target panel.
        drag.source.removeTab(drag.sourceIndex);
        splitPanel(target, drag.tab, zone);
    }

    private static void mergeTab(TabDrag drag, TabbedPanel target, double logicalX) {
        if (target == drag.source) {
            var dropIdx = target.dropInsertionIndex(logicalX);
            if (dropIdx == drag.sourceIndex || dropIdx == drag.sourceIndex + 1) {
                return;
            }
            target.removeTab(drag.sourceIndex);
            if (dropIdx > drag.sourceIndex) {
                dropIdx--;
            }
            target.insertTab(dropIdx, drag.tab);
        } else {
            drag.source.removeTab(drag.sourceIndex);
            var dropIdx = target.dropInsertionIndex(logicalX);
            target.insertTab(dropIdx, drag.tab);
        }
    }

    /**
     * Replace {@code target}'s leaf in the dock tree with a fresh {@link DockNode.Split} containing two leaves: the
     * original target panel (now wrapped in a new leaf) on one side, and a new {@link TabbedPanel} holding
     * {@code droppedTab} on the other. Side determined by {@code zone}; default 50/50 ratio.
     */
    private void splitPanel(TabbedPanel target, Panel droppedTab, DropZone zone) {
        var existingLeaf = new DockNode.Leaf(target);
        var newLeaf = new DockNode.Leaf(new TabbedPanel(droppedTab));
        var sizing = new Sizing.Ratio(0.5f);

        var newSplit = switch (zone) {
            case TOP -> new DockNode.Split(Orientation.VERTICAL, newLeaf, existingLeaf, sizing);
            case BOTTOM -> new DockNode.Split(Orientation.VERTICAL, existingLeaf, newLeaf, sizing);
            case LEFT -> new DockNode.Split(Orientation.HORIZONTAL, newLeaf, existingLeaf, sizing);
            case RIGHT -> new DockNode.Split(Orientation.HORIZONTAL, existingLeaf, newLeaf, sizing);
            case CENTER -> throw new IllegalStateException("CENTER is not a split zone");
        };

        this.root = replaceTabbedPanel(this.root, target, newSplit);
    }

    private static DockNode replaceTabbedPanel(DockNode node, TabbedPanel target, DockNode replacement) {
        if (node instanceof DockNode.Leaf leaf && leaf.panel() == target) {
            return replacement;
        }
        if (node instanceof DockNode.Split split) {
            var first = replaceTabbedPanel(split.first(), target, replacement);
            var second = replaceTabbedPanel(split.second(), target, replacement);
            if (first == split.first() && second == split.second()) {
                return split;
            }
            return new DockNode.Split(split.orientation(), first, second, split.sizing());
        }
        return node;
    }

    private enum DropZone {
        CENTER,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    private static void applyDrag(DividerHit divider, double mouseLogicalX, double mouseLogicalY) {
        if (divider.split.orientation() == Orientation.HORIZONTAL) {
            var newBoundary = (int) Math.round(mouseLogicalX - divider.parentX);
            var clamped = Math.max(MIN_PANEL_SIZE_PX, Math.min(divider.parentWidth - MIN_PANEL_SIZE_PX, newBoundary));
            updateSizing(divider.split.sizing(), clamped, divider.parentWidth);
        } else {
            var newBoundary = (int) Math.round(mouseLogicalY - divider.parentY);
            var clamped = Math.max(MIN_PANEL_SIZE_PX, Math.min(divider.parentHeight - MIN_PANEL_SIZE_PX, newBoundary));
            updateSizing(divider.split.sizing(), clamped, divider.parentHeight);
        }
    }

    private static void updateSizing(Sizing sizing, int newBoundary, int parentSize) {
        switch (sizing) {
            case Sizing.Ratio r -> r.value = Math.max(0.01f, Math.min(0.99f, (float) newBoundary / parentSize));
            case Sizing.FirstFixed f -> f.pixels = newBoundary;
            case Sizing.SecondFixed s -> s.pixels = parentSize - newBoundary;
        }
    }

    private static boolean isResizable(Sizing sizing) {
        return true;
    }

    private static @Nullable DividerHit findDivider(DockNode node, int x, int y, int width, int height, int mouseX, int mouseY) {
        if (!(node instanceof DockNode.Split split)) {
            return null;
        }

        if (split.orientation() == Orientation.HORIZONTAL) {
            var firstWidth = DockNode.boundary(split.sizing(), width);
            var boundaryX = x + firstWidth;
            if (Math.abs(mouseX - boundaryX) <= DIVIDER_HIT_PX && mouseY >= y && mouseY < y + height) {
                return new DividerHit(split, x, y, width, height);
            }
            var inFirst = findDivider(split.first(), x, y, firstWidth, height, mouseX, mouseY);
            if (inFirst != null) {
                return inFirst;
            }
            return findDivider(split.second(), x + firstWidth, y, width - firstWidth, height, mouseX, mouseY);
        } else {
            var firstHeight = DockNode.boundary(split.sizing(), height);
            var boundaryY = y + firstHeight;
            if (Math.abs(mouseY - boundaryY) <= DIVIDER_HIT_PX && mouseX >= x && mouseX < x + width) {
                return new DividerHit(split, x, y, width, height);
            }
            var inFirst = findDivider(split.first(), x, y, width, firstHeight, mouseX, mouseY);
            if (inFirst != null) {
                return inFirst;
            }
            return findDivider(split.second(), x, y + firstHeight, width, height - firstHeight, mouseX, mouseY);
        }
    }

    private static @Nullable Panel panelAt(DockNode node, int x, int y, int width, int height, double mouseX, double mouseY) {
        return switch (node) {
            case DockNode.Leaf leaf -> {
                if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
                    yield leaf.panel();
                }
                yield null;
            }
            case DockNode.Split split -> {
                if (split.orientation() == Orientation.HORIZONTAL) {
                    var firstWidth = DockNode.boundary(split.sizing(), width);
                    if (mouseX < x + firstWidth) {
                        yield panelAt(split.first(), x, y, firstWidth, height, mouseX, mouseY);
                    }
                    yield panelAt(split.second(), x + firstWidth, y, width - firstWidth, height, mouseX, mouseY);
                } else {
                    var firstHeight = DockNode.boundary(split.sizing(), height);
                    if (mouseY < y + firstHeight) {
                        yield panelAt(split.first(), x, y, width, firstHeight, mouseX, mouseY);
                    }
                    yield panelAt(split.second(), x, y + firstHeight, width, height - firstHeight, mouseX, mouseY);
                }
            }
        };
    }

    private @Nullable TabbedPanel findTabbedPanelAt(int mouseX, int mouseY) {
        var leaf = panelAt(root, 0, 0, logicalWidth(), logicalHeight(), mouseX, mouseY);
        return leaf instanceof TabbedPanel tp ? tp : null;
    }

    /**
     * Build the dropdown menu for the clicked menu-bar chip. Anchored just below the chip's screen rect. Returns
     * {@code null} for chips that don't have menus implemented yet (so File / Edit / View are inert no-ops for now).
     */
    private @Nullable DropdownMenu buildMenuFor(String chipName, MenuBarPanel menuBar) {
        var chipRect = menuBar.chipRect(chipName);
        if (chipRect == null) {
            return null;
        }
        var anchorX = chipRect.x();
        var anchorY = chipRect.y() + chipRect.height() + 1;

        return switch (chipName) {
            case MenuBarPanel.CHIP_WINDOW -> new DropdownMenu(
                anchorX,
                anchorY,
                java.util.List.of(
                    new DropdownMenu.Item("Reopen Outliner", () -> reopenPanel(OutlinerPanel.class, OutlinerPanel::new)),
                    new DropdownMenu.Item(
                        "Reopen Viewport",
                        () -> reopenPanel(ViewportPanel.class, () -> new ViewportPanel("Viewport", this::onViewportRightClick))
                    ),
                    new DropdownMenu.Item("Reopen Details", () -> reopenPanel(DetailsPanel.class, DetailsPanel::new)),
                    new DropdownMenu.Item("Reopen Content Browser", () -> reopenPanel(ContentBrowserPanel.class, ContentBrowserPanel::new)),
                    new DropdownMenu.Item("Reopen GOAP Details", () -> reopenPanel(GOAPDetailsPanel.class, GOAPDetailsPanel::new)),
                    new DropdownMenu.Item("Reset Layout", this::resetLayout)
                )
            );
            default -> null;
        };
    }

    /**
     * If a panel of {@code panelClass} already exists somewhere in the dock tree, switch its containing
     * {@link TabbedPanel} to that tab and return. Otherwise create a fresh instance via {@code factory} and append it
     * as a tab in the first {@link TabbedPanel} found via depth-first walk. If no {@code TabbedPanel} exists at all
     * (the user has closed everything), the action is a no-op and the user can use Reset Layout to recover.
     */
    private void reopenPanel(Class<? extends Panel> panelClass, java.util.function.Supplier<Panel> factory) {
        var existingOwner = findOwnerWithPanelOfType(this.root, panelClass);
        if (existingOwner != null) {
            for (var i = 0; i < existingOwner.tabCount(); i++) {
                if (panelClass.isInstance(existingOwner.tabs().get(i))) {
                    existingOwner.setActiveIndex(i);
                    return;
                }
            }
        }
        var first = findFirstTabbedPanel(this.root);
        if (first != null) {
            first.insertTab(first.tabCount(), factory.get());
        }
    }

    private void resetLayout() {
        this.root = buildDefaultLayout();
    }

    /**
     * Right-click in the viewport — opens a context menu at the cursor anchored as a {@link DropdownMenu}. When the
     * cursor was over a living entity, items include "View GOAP Details" (dispatches a {@link C2SGOAPTrackPayload} and
     * opens the {@link GOAPDetailsPanel}) and "Delete Entity" (dispatches a {@link C2SRemoveEntityPayload}). The delete
     * option is hidden for players since deleting other players via this menu would be inappropriate; the server-side
     * handler also rejects player targets as a safety net. Empty-space right-clicks just close any existing menu.
     */
    private void onViewportRightClick(@Nullable LivingEntity entity, double cursorX, double cursorY) {
        if (entity == null) {
            this.openMenu = null;
            return;
        }

        var entityId = entity.getId();
        var items = new java.util.ArrayList<DropdownMenu.Item>();
        items.add(
            new DropdownMenu.Item("View GOAP Details", () -> {
                BLib.MOD.networking().sendToServer(new C2SGOAPTrackPayload(entityId));
                reopenPanel(GOAPDetailsPanel.class, GOAPDetailsPanel::new);
            })
        );
        if (!(entity instanceof net.minecraft.world.entity.player.Player)) {
            items.add(
                new DropdownMenu.Item("Delete Entity", () -> {
                    BLib.MOD.networking().sendToServer(new C2SRemoveEntityPayload(entityId));
                })
            );
        }

        this.openMenu = new DropdownMenu((int) cursorX, (int) cursorY, items);
    }

    private static @Nullable TabbedPanel findFirstTabbedPanel(DockNode node) {
        if (node instanceof DockNode.Leaf leaf && leaf.panel() instanceof TabbedPanel tp) {
            return tp;
        }
        if (node instanceof DockNode.Split split) {
            var f = findFirstTabbedPanel(split.first());
            if (f != null) {
                return f;
            }
            return findFirstTabbedPanel(split.second());
        }
        return null;
    }

    private static @Nullable TabbedPanel findOwnerWithPanelOfType(DockNode node, Class<? extends Panel> panelClass) {
        if (node instanceof DockNode.Leaf leaf && leaf.panel() instanceof TabbedPanel tp) {
            for (var p : tp.tabs()) {
                if (panelClass.isInstance(p)) {
                    return tp;
                }
            }
            return null;
        }
        if (node instanceof DockNode.Split split) {
            var f = findOwnerWithPanelOfType(split.first(), panelClass);
            if (f != null) {
                return f;
            }
            return findOwnerWithPanelOfType(split.second(), panelClass);
        }
        return null;
    }

    /**
     * After a tab close or move, walk the dock tree and replace any {@link DockNode.Split} whose child is an empty
     * {@link TabbedPanel} with the non-empty sibling, so the surviving panel grows into the freed space. Trim leaves
     * (menu / toolbar / status) are never considered empty so they're preserved. If both children of a split are empty
     * (degenerate state, e.g. the user closed every tab), the first child is kept arbitrarily.
     */
    private void simplifyDockTree() {
        this.root = simplify(this.root);
    }

    private static DockNode simplify(DockNode node) {
        if (!(node instanceof DockNode.Split split)) {
            return node;
        }
        var first = simplify(split.first());
        var second = simplify(split.second());
        var firstEmpty = isEmptyTabbedPanel(first);
        var secondEmpty = isEmptyTabbedPanel(second);

        if (firstEmpty && !secondEmpty) {
            return second;
        }
        if (secondEmpty && !firstEmpty) {
            return first;
        }
        if (firstEmpty && secondEmpty) {
            return first;
        }
        if (first == split.first() && second == split.second()) {
            return split;
        }
        return new DockNode.Split(split.orientation(), first, second, split.sizing());
    }

    private static boolean isEmptyTabbedPanel(DockNode node) {
        return node instanceof DockNode.Leaf leaf
            && leaf.panel() instanceof TabbedPanel tp
            && tp.tabCount() == 0;
    }

    private int logicalWidth() {
        return (int) (this.width / SCALE);
    }

    private int logicalHeight() {
        return (int) (this.height / SCALE);
    }

    private record DividerHit(
        DockNode.Split split,
        int parentX,
        int parentY,
        int parentWidth,
        int parentHeight
    ) {

        int boundaryStartX() {
            if (split.orientation() == Orientation.HORIZONTAL) {
                return parentX + DockNode.boundary(split.sizing(), parentWidth);
            }
            return parentX;
        }

        int boundaryStartY() {
            if (split.orientation() == Orientation.VERTICAL) {
                return parentY + DockNode.boundary(split.sizing(), parentHeight);
            }
            return parentY;
        }
    }

    private record ActiveDrag(DividerHit divider) {}

    /**
     * Mutable tab-drag state. Created on tab press; turns {@link #active} once the cursor moves past the threshold;
     * cleared on release. The tab itself stays in {@link #source} during the drag — only on a successful drop does the
     * source actually lose it.
     */
    private static final class TabDrag {

        final TabbedPanel source;

        final int sourceIndex;

        final Panel tab;

        final double startX;

        final double startY;

        boolean active;

        TabDrag(TabbedPanel source, int sourceIndex, Panel tab, double startX, double startY) {
            this.source = source;
            this.sourceIndex = sourceIndex;
            this.tab = tab;
            this.startX = startX;
            this.startY = startY;
        }
    }
}
