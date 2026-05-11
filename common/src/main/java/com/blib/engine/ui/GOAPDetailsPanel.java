package com.blib.engine.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.blib.mod.client.render.goap.GOAPDebugHUD;
import com.blib.mod.client.render.goap.model.GOAPAgentDebugData;

/**
 * Editor inspector for the currently-tracked GOAP agent. Reads from {@link GOAPDebugHUD#latestPayload()} (which the
 * existing server packet handler populates), so it shows live data whenever someone has run {@code /goap track} or the
 * engine has otherwise pinned an entity for tracking.
 * <p>
 * Two modes via a {@link SegmentedControl}: <strong>Agent</strong> shows identity / plan list / blackboards, and
 * <strong>World State</strong> shows the agent's sensor map filtered by a {@link TextInput} search box. Both modes
 * render their content into a shared scrollable area so the panel works at any height.
 */
@ApiStatus.Internal
public final class GOAPDetailsPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int CONTENT_PADDING = 6;

    private static final int LINE_HEIGHT = 10;

    private static final int SECTION_GAP = 3;

    /**
     * Reserved width on the right side of a row for the scrollbar — so row text truncates before reaching the
     * scrollbar's hover region instead of being abruptly chopped by the scissor.
     */
    private static final int LABEL_COLOR = 0xFF888892;

    private static final int VALUE_COLOR = 0xFFD0D0D0;

    private static final int HEADER_COLOR = 0xFFB8C0D0;

    private static final int ACCENT_COLOR = 0xFFE6C26B;

    private static final int PLAN_RUNNING_COLOR = 0xFFE0E0E0;

    private static final int PLAN_DONE_COLOR = 0xFF80FF80;

    private static final int PLAN_FAIL_COLOR = 0xFFFF6868;

    private final SegmentedControl modeSwitcher = new SegmentedControl(List.of("Agent", "Sensors"), 0);

    private final TextInput worldStateFilter = new TextInput("Filter sensor keys…");

    /**
     * Independent scroll positions per mode. A single shared {@code ScrollContainer} would carry the Sensors-mode
     * scroll position across to Agent mode (and vice versa), which made the panel show the bottom of the Agent content
     * with the top scrolled out of view after looking at a long Sensors list.
     */
    private final ScrollContainer agentScroll = new ScrollContainer();

    private final ScrollContainer sensorsScroll = new ScrollContainer();

    /** Tracks the previously-rendered mode so we can reset the new mode's scroll on switch. */
    private int lastMode = -1;

    /**
     * UUID of the last agent we rendered. When the tracked entity changes (user right-clicked a different mob to view
     * its GOAP), reset both scrolls — without this, the new agent's content opens in whatever scrolled state the
     * previous agent's was left in, hiding the top of the new agent's data.
     */
    private @org.jetbrains.annotations.Nullable String lastAgentUuid;

    /**
     * Cached tooltip text for the currently-hovered row, computed during {@link #render} (which has the mouse
     * coordinates and the row geometry). Null when no row is hovered or the hovered row's text fits without truncation.
     * Read by {@link #tooltipText()}.
     */
    private @org.jetbrains.annotations.Nullable Component hoveredTooltip;

    @Override
    public void onShown() {
        // The user just navigated to this tab (or it was just created). Reset both modes' scrolls so they always see
        // content from the top — the alternative (preserving scroll across hide/show) hid the top of the agent data
        // when the user re-opened the panel after scrolling.
        agentScroll.reset();
        sensorsScroll.reset();
    }

    @Override
    public Component tooltipText() {
        return hoveredTooltip;
    }

    private int scrollAreaX;

    private int scrollAreaY;

    private int scrollAreaWidth;

    private int scrollAreaHeight;

    @Override
    public String title() {
        return "GOAP Details";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        var cursorY = y + 1;
        modeSwitcher.render(graphics, x + 1, cursorY, width - 2, mouseX, mouseY);
        cursorY += SegmentedControl.HEIGHT + 1;

        var mode = modeSwitcher.selectedIndex();
        if (mode != lastMode) {
            // Mode just changed (or first frame) — reset the new mode's scroll so its content opens at the top.
            activeScroll().reset();
            lastMode = mode;
        }

        // Reset scroll for both modes when the tracked agent changes — viewing a new entity should always show its
        // content from the top, not at whatever scroll position the previous entity was left at.
        var payload = GOAPDebugHUD.INSTANCE.latestPayload();
        if (payload != null && !payload.agents().isEmpty()) {
            var idx = payload.selectedIndex();
            if (idx >= 0 && idx < payload.agents().size()) {
                var currentUuid = payload.agents().get(idx).entityUuid();
                if (!currentUuid.equals(lastAgentUuid)) {
                    agentScroll.reset();
                    sensorsScroll.reset();
                    lastAgentUuid = currentUuid;
                }
            }
        }

        if (mode == 1) {
            cursorY += SECTION_GAP;
            worldStateFilter.render(graphics, x + CONTENT_PADDING, cursorY, width - 2 * CONTENT_PADDING, mouseX, mouseY);
            cursorY += TextInput.HEIGHT + SECTION_GAP;
        }

        scrollAreaX = x;
        scrollAreaY = cursorY;
        scrollAreaWidth = width;
        scrollAreaHeight = Math.max(0, y + height - cursorY);

        var rows = buildRows();
        var contentHeight = rows.size() * LINE_HEIGHT + 2 * CONTENT_PADDING;
        var scroll = activeScroll();
        scroll.layout(scrollAreaHeight, contentHeight);

        // Apply scissor via RenderSystem directly, bypassing GuiGraphics.enableScissor — that one intersects with
        // whatever scissor entries are already on the GuiGraphics stack. Mods/vanilla overlays that don't fully clean
        // up their scissor state (e.g. REI, other HUD callbacks) leave stale entries on that stack, so a normal
        // enableScissor would intersect our rect with a much smaller stale rect, clipping the top of our rows. This
        // path computes the GL scissor coords ourselves and writes them directly, so the stale stack is irrelevant.
        applyRawScissor(graphics, scrollAreaX, scrollAreaY, scrollAreaWidth, scrollAreaHeight);

        var font = EngineFont.get();
        // Subtract the scroll offset from the starting Y so a positive scroll position pushes content upward; rows
        // above the scissor are clipped, rows below render normally. Cast to int because drawString takes int coords
        // and we don't want sub-pixel jitter as the user wheel-scrolls.
        var rowY = scrollAreaY + CONTENT_PADDING - (int) scroll.scrollY();
        // Width available for row text. Subtract scrollbar gutter + a couple px right padding so truncation kicks in
        // before the scissor would chop letters mid-glyph.
        var rightReserve = ScrollContainer.SCROLLBAR_GUTTER + 2;
        // Reset the hovered-row tooltip each frame; the loop below sets it if the cursor is over a truncated row.
        hoveredTooltip = null;
        // Whether the cursor is currently in the scroll area at all — gates tooltip detection so we don't pick up the
        // cursor over the seg control / outside the panel.
        var cursorInScrollArea = mouseX >= scrollAreaX
            && mouseX < scrollAreaX + scrollAreaWidth
            && mouseY >= scrollAreaY
            && mouseY < scrollAreaY + scrollAreaHeight;
        for (var row : rows) {
            var rowX = scrollAreaX + CONTENT_PADDING + row.indent();
            var maxWidth = Math.max(0, scrollAreaX + scrollAreaWidth - rowX - rightReserve);
            graphics.drawString(
                font,
                Component.literal(truncateToWidth(font, row.label(), maxWidth)),
                rowX,
                rowY,
                row.color(),
                false
            );
            // Tooltip detection: if the cursor is over this row's rect AND the full label wouldn't fit at maxWidth,
            // remember the full text. Only one row can be hovered at a time, so the last hit wins (rows are drawn
            // top-to-bottom and don't overlap).
            if (
                cursorInScrollArea
                    && mouseY >= rowY
                    && mouseY < rowY + LINE_HEIGHT
                    && !row.label().isEmpty()
                    && font.width(row.label()) > maxWidth
            ) {
                hoveredTooltip = Component.literal(row.label());
            }
            rowY += LINE_HEIGHT;
        }

        // Flush any pending draws while the scissor is still active, then disable. We use RenderSystem.disableScissor
        // (mirroring our raw enable) so we don't pop anything off the GuiGraphics stack we never pushed onto.
        graphics.flush();
        RenderSystem.disableScissor();

        scroll.renderScrollbar(graphics, scrollAreaX, scrollAreaY, scrollAreaWidth, scrollAreaHeight, mouseX, mouseY);
    }

    /**
     * Set the GL scissor to clip drawing to {@code (x, y, w, h)} in this panel's logical-pixel space, bypassing
     * {@link GuiGraphics#enableScissor} (which intersects with the GuiGraphics scissor stack — that stack accumulates
     * stale entries from upstream HUD code in the wild and would clip our rect to whatever residue is on top).
     * <p>
     * The transform mirrors what {@code GuiGraphics.applyScissor} does internally: take the rect through the current
     * pose matrix to get screen-logical coords, then convert to raw pixels via guiScale, then convert screen Y (top-
     * origin) to GL Y (bottom-origin) using the framebuffer height.
     */
    private static void applyRawScissor(GuiGraphics graphics, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) {
            // Empty area — nothing to clip into. Just disable any stray scissor and bail.
            RenderSystem.disableScissor();
            return;
        }
        // Flush whatever's queued so it's drawn under the OLD scissor state, not ours.
        graphics.flush();

        // Transform the rect through the current pose matrix to get screen-logical coords. We do this manually rather
        // than via ScreenRectangle.transformAxisAligned because that API isn't present in our remap of MC 1.21.1.
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

    private ScrollContainer activeScroll() {
        return modeSwitcher.selectedIndex() == 0 ? agentScroll : sensorsScroll;
    }

    private List<Row> buildRows() {
        var payload = GOAPDebugHUD.INSTANCE.latestPayload();
        if (payload == null || payload.agents().isEmpty()) {
            return List.of(
                new Row(0, "No agent tracked.", LABEL_COLOR),
                new Row(0, "Run /goap track <entity> to start.", LABEL_COLOR)
            );
        }

        var idx = payload.selectedIndex();
        if (idx < 0 || idx >= payload.agents().size()) {
            return List.of(new Row(0, "(invalid selection)", LABEL_COLOR));
        }

        var agent = payload.agents().get(idx);
        return modeSwitcher.selectedIndex() == 0 ? buildAgentRows(agent) : buildWorldStateRows(agent);
    }

    private static List<Row> buildAgentRows(GOAPAgentDebugData agent) {
        var rows = new ArrayList<Row>();
        rows.add(new Row(0, "Name: " + agent.entityName(), VALUE_COLOR));
        var uuidShort = agent.entityUuid().length() >= 8 ? agent.entityUuid().substring(0, 8) : agent.entityUuid();
        rows.add(new Row(0, "UUID: " + uuidShort, LABEL_COLOR));
        rows.add(new Row(0, "Pos: " + agent.posX() + "  " + agent.posY() + "  " + agent.posZ(), LABEL_COLOR));
        rows.add(new Row(0, "Tick: " + agent.agentTick(), LABEL_COLOR));
        rows.add(emptyRow());

        rows.add(new Row(0, "Graph", HEADER_COLOR));
        rows.add(new Row(8, "goals: " + agent.graphGoalCount() + "    actions: " + agent.graphActionCount(), VALUE_COLOR));
        rows.add(emptyRow());

        rows.add(new Row(0, "Plans (" + agent.plans().size() + ")", HEADER_COLOR));
        if (agent.plans().isEmpty()) {
            rows.add(new Row(8, "(none)", LABEL_COLOR));
        } else {
            for (var plan : agent.plans()) {
                var color = colorForPlanState(plan.planState());
                rows.add(new Row(8, plan.goalName(), color));
                rows.add(new Row(16, "state: " + plan.planState() + "    cost: " + plan.initialCost(), LABEL_COLOR));
                for (var i = 0; i < plan.actionNames().size(); i++) {
                    var current = i == plan.currentActionIndex();
                    var marker = current ? "▶ " : "  ";
                    var actionColor = current ? ACCENT_COLOR : VALUE_COLOR;
                    rows.add(new Row(16, marker + plan.actionNames().get(i), actionColor));
                }
                if (!plan.actionBlackboard().isEmpty()) {
                    rows.add(new Row(16, "action blackboard:", LABEL_COLOR));
                    appendBlackboard(rows, plan.actionBlackboard());
                }
                if (!plan.planBlackboard().isEmpty()) {
                    rows.add(new Row(16, "plan blackboard:", LABEL_COLOR));
                    appendBlackboard(rows, plan.planBlackboard());
                }
                rows.add(emptyRow());
            }
        }
        return rows;
    }

    private List<Row> buildWorldStateRows(GOAPAgentDebugData agent) {
        var rows = new ArrayList<Row>();
        var filter = worldStateFilter.content().toLowerCase(Locale.ROOT);

        // Iterate the agent's full sensor key set rather than just worldState.entrySet() — sensors that haven't
        // produced a value yet (or whose value is the type's default) often don't appear in worldState, but the user
        // still wants to see the key listed (matching the GOAP debug HUD's behavior on the right side of the screen).
        var allKeys = agent.graphSensorKeys();
        var worldState = agent.worldState();

        var matchedKeys = allKeys.stream()
            .filter(key -> filter.isEmpty() || key.toLowerCase(Locale.ROOT).contains(filter))
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .toList();

        rows.add(
            new Row(
                0,
                "Sensors (" + matchedKeys.size() + (filter.isEmpty() ? "" : " of " + allKeys.size()) + ")",
                HEADER_COLOR
            )
        );
        rows.add(emptyRow());

        if (matchedKeys.isEmpty()) {
            var msg = allKeys.isEmpty()
                ? "(no sensors)"
                : filter.isEmpty() ? "(no sensors)" : "(no keys match \"" + filter + "\")";
            rows.add(new Row(8, msg, LABEL_COLOR));
        } else {
            for (var key : matchedKeys) {
                rows.add(new Row(0, key, LABEL_COLOR));
                var value = worldState.get(key);
                // Show the value if present; otherwise mark as unset so the user knows the sensor exists but hasn't
                // produced a value (vs. it producing an actual empty-string value).
                if (value == null || value.isEmpty()) {
                    rows.add(new Row(8, "(unset)", LABEL_COLOR));
                } else {
                    rows.add(new Row(8, value, VALUE_COLOR));
                }
            }
        }
        return rows;
    }

    private static void appendBlackboard(List<Row> rows, Map<String, String> blackboard) {
        blackboard.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> rows.add(new Row(24, entry.getKey() + ": " + entry.getValue(), VALUE_COLOR)));
    }

    private static int colorForPlanState(String state) {
        return switch (state) {
            case "FINISHED" -> PLAN_DONE_COLOR;
            case "ABORTED", "INVALID" -> PLAN_FAIL_COLOR;
            default -> PLAN_RUNNING_COLOR;
        };
    }

    private static Row emptyRow() {
        return new Row(0, "", LABEL_COLOR);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (modeSwitcher.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (modeSwitcher.selectedIndex() == 1 && worldStateFilter.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        var inScrollArea = mouseX >= scrollAreaX
            && mouseX < scrollAreaX + scrollAreaWidth
            && mouseY >= scrollAreaY
            && mouseY < scrollAreaY + scrollAreaHeight;
        if (!inScrollArea) {
            return false;
        }
        return activeScroll().mouseScrolled(scrollY);
    }

    private record Row(
        int indent,
        String label,
        int color
    ) {}

    /**
     * Trim {@code text} so it renders in {@code maxWidth} pixels, appending "…" if anything was cut. Uses
     * {@link net.minecraft.client.gui.Font#plainSubstrByWidth} so the cut lands on a glyph boundary instead of
     * mid-character.
     */
    private static String truncateToWidth(net.minecraft.client.gui.Font font, String text, int maxWidth) {
        if (maxWidth <= 0) {
            return "";
        }
        if (font.width(text) <= maxWidth) {
            return text;
        }
        var ellipsisWidth = font.width("…");
        var allowance = Math.max(0, maxWidth - ellipsisWidth);
        return font.plainSubstrByWidth(text, allowance) + "…";
    }
}
