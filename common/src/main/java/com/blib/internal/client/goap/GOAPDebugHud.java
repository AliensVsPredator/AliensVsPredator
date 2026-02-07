package com.blib.internal.client.goap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blib.internal.common.goap.GOAPDebugTracker;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload.GOAPAgentDebugData;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload.GOAPPlanDebugData;

@ApiStatus.Internal
public final class GOAPDebugHud {

    public static final GOAPDebugHud INSTANCE = new GOAPDebugHud();

    private static final int PADDING = 4;

    private static final int LINE_HEIGHT = 10;

    private static final int BACKGROUND_COLOR = 0xAA000000;

    private static final int TEXT_COLOR = 0xFFFFFFFF;

    private static final int HIGHLIGHT_COLOR = 0xFFFFFF00;

    private static final int HEADER_COLOR = 0xFF55FFFF;

    private static final int COMPLETED_COLOR = 0xFF55FF55;

    private static final int FAILED_COLOR = 0xFFFF5555;

    private static final int BLACKBOARD_COLOR = 0xFFAAAAAA;

    private static final int GRAPH_COLOR = 0xFFCC88FF;

    private static final long GHOST_DURATION_MS = 2000;

    private static final long WORLD_STATE_PAGE_INTERVAL_MS = 5000;

    private @Nullable S2CGOAPDebugPayload latestPayload;

    private final Map<Integer, AgentDisplayState> agentStates = new HashMap<>();

    private int worldStatePage;

    private long lastWorldStatePageTime;

    private GOAPDebugHud() {}

    public void update(S2CGOAPDebugPayload payload) {
        this.latestPayload = payload;

        for (var agent : payload.agents()) {
            var displayState = agentStates.computeIfAbsent(agent.entityId(), id -> new AgentDisplayState());
            displayState.update(agent);
        }
    }

    public void clear() {
        this.latestPayload = null;
        this.agentStates.clear();
        this.worldStatePage = 0;
        this.lastWorldStatePageTime = 0;
    }

    public void render(GuiGraphics graphics, float partialTick) {
        if (latestPayload == null || latestPayload.agents().isEmpty()) {
            return;
        }

        var agents = latestPayload.agents();
        var selectedIndex = latestPayload.selectedIndex();

        if (selectedIndex < 0 || selectedIndex >= agents.size()) {
            return;
        }

        var agent = agents.get(selectedIndex);
        var displayState = agentStates.get(agent.entityId());
        var lines = buildLines(agent, selectedIndex, agents.size(), displayState);
        var font = Minecraft.getInstance().font;

        var pose = graphics.pose();
        pose.pushPose();
        pose.scale(0.5f, 0.5f, 1.0f);

        renderPanel(graphics, font, lines, PADDING, PADDING);

        var screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth() * 2;
        renderWorldStatePanel(graphics, font, agent, latestPayload, screenWidth);

        pose.popPose();
    }

    private void renderPanel(GuiGraphics graphics, net.minecraft.client.gui.Font font, List<HudLine> lines, int x, int y) {
        var maxWidth = 0;

        for (var line : lines) {
            maxWidth = Math.max(maxWidth, font.width(line.text()));
        }

        var totalHeight = lines.size() * LINE_HEIGHT + PADDING * 2;
        var totalWidth = maxWidth + PADDING * 2;

        graphics.fill(x, y, x + totalWidth, y + totalHeight, BACKGROUND_COLOR);

        var textY = y + PADDING;

        for (var line : lines) {
            graphics.drawString(font, line.text(), x + PADDING, textY, line.color(), false);
            textY += LINE_HEIGHT;
        }
    }

    private List<HudLine> buildLines(
        GOAPAgentDebugData agent,
        int selectedIndex,
        int totalAgents,
        @Nullable AgentDisplayState displayState
    ) {
        var lines = new ArrayList<HudLine>();

        lines.add(
            new HudLine(
                "GOAP: %s (%d/%d)".formatted(agent.entityName(), selectedIndex + 1, totalAgents),
                HEADER_COLOR
            )
        );

        lines.add(
            new HudLine(
                "UUID: %s | Pos: %d, %d, %d".formatted(agent.entityUuid(), agent.posX(), agent.posY(), agent.posZ()),
                BLACKBOARD_COLOR
            )
        );

        var displayPlans = displayState != null ? displayState.getDisplayPlans() : List.<DisplayPlan>of();
        var activePlanCount = agent.plans().size();

        lines.add(
            new HudLine(
                "Tick: %d | Plans: %d".formatted(agent.agentTick(), activePlanCount),
                TEXT_COLOR
            )
        );

        lines.add(
            new HudLine(
                "Graph: %d goals, %d actions, %d sensors".formatted(
                    agent.graphGoalCount(),
                    agent.graphActionCount(),
                    agent.graphSensorKeys().size()
                ),
                GRAPH_COLOR
            )
        );

        if (displayPlans.isEmpty()) {
            lines.add(new HudLine("No active plans.", TEXT_COLOR));
        } else {
            for (var i = 0; i < displayPlans.size(); i++) {
                var displayPlan = displayPlans.get(i);
                lines.add(new HudLine("", TEXT_COLOR));
                formatPlan(lines, displayPlan, i);
            }
        }

        return lines;
    }

    private static final String SEPARATOR = " : ";

    private void renderWorldStatePanel(
        GuiGraphics graphics,
        net.minecraft.client.gui.Font font,
        GOAPAgentDebugData agent,
        S2CGOAPDebugPayload payload,
        int screenWidth
    ) {
        var keys = new ArrayList<>(agent.graphSensorKeys());
        keys.sort(String.CASE_INSENSITIVE_ORDER);
        var worldState = agent.worldState();
        var totalKeys = keys.size();
        var pageSize = GOAPDebugTracker.WORLD_STATE_PAGE_SIZE;
        var totalPages = Math.max(1, (totalKeys + pageSize - 1) / pageSize);

        int currentPage;

        if (payload.worldStateAutoPage()) {
            var now = System.currentTimeMillis();

            if (now - lastWorldStatePageTime >= WORLD_STATE_PAGE_INTERVAL_MS) {
                worldStatePage = (worldStatePage + 1) % totalPages;
                lastWorldStatePageTime = now;
            }

            if (worldStatePage >= totalPages) {
                worldStatePage = 0;
            }

            currentPage = worldStatePage;
        } else {
            currentPage = Math.clamp(payload.worldStatePage(), 0, totalPages - 1);
        }

        var header = "World State (%d/%d)".formatted(currentPage + 1, totalPages);

        var maxKeyWidth = 0;

        for (var key : keys) {
            maxKeyWidth = Math.max(maxKeyWidth, font.width(key));
        }

        var separatorWidth = font.width(SEPARATOR);
        var valueWidth = font.width("0".repeat(32));
        var lineWidth = maxKeyWidth + separatorWidth + valueWidth;
        var totalWidth = Math.max(lineWidth, font.width(header)) + PADDING * 2;
        var lineCount = Math.min(pageSize, totalKeys - currentPage * pageSize) + 1;
        var totalHeight = lineCount * LINE_HEIGHT + PADDING * 2;

        var panelX = screenWidth - totalWidth - PADDING;

        graphics.fill(panelX, PADDING, panelX + totalWidth, PADDING + totalHeight, BACKGROUND_COLOR);

        var textX = panelX + PADDING;
        var y = PADDING + PADDING;

        graphics.drawString(font, header, textX, y, HEADER_COLOR, false);
        y += LINE_HEIGHT;

        var start = currentPage * pageSize;
        var end = Math.min(start + pageSize, totalKeys);
        var valueX = textX + maxKeyWidth + separatorWidth;

        for (var i = start; i < end; i++) {
            var key = keys.get(i);
            var value = worldState.getOrDefault(key, "");
            var hasValue = !value.isEmpty();
            var color = hasValue ? TEXT_COLOR : BLACKBOARD_COLOR;

            graphics.drawString(font, key, textX, y, color, false);
            graphics.drawString(font, SEPARATOR, textX + maxKeyWidth, y, color, false);

            if (hasValue) {
                graphics.drawString(font, value, valueX, y, color, false);
            }

            y += LINE_HEIGHT;
        }
    }

    private void formatPlan(List<HudLine> lines, DisplayPlan displayPlan, int planIndex) {
        var plan = displayPlan.plan;
        var planColor = displayPlan.getColor();

        lines.add(
            new HudLine(
                "Plan #%d: [%s] \"%s\" (%.1f)".formatted(
                    planIndex,
                    displayPlan.isGhost() ? displayPlan.terminalState : plan.planState(),
                    plan.goalName(),
                    plan.initialCost()
                ),
                planColor
            )
        );

        if (!plan.planBlackboard().isEmpty()) {
            for (var entry : plan.planBlackboard().entrySet()) {
                lines.add(
                    new HudLine(
                        "  %s: %s".formatted(entry.getKey(), entry.getValue()),
                        BLACKBOARD_COLOR
                    )
                );
            }
        }

        for (var i = 0; i < plan.actionNames().size(); i++) {
            var actionName = plan.actionNames().get(i);
            int color;
            String marker;
            boolean isCurrent;

            if (displayPlan.isGhost()) {
                color = planColor;
                marker = "   ";
                isCurrent = false;
            } else {
                isCurrent = i == plan.currentActionIndex();
                marker = isCurrent ? ">> " : "   ";
                color = isCurrent ? HIGHLIGHT_COLOR : TEXT_COLOR;
            }

            lines.add(
                new HudLine(
                    "  %s%d. %s".formatted(marker, i + 1, actionName),
                    color
                )
            );

            if (isCurrent && !plan.actionBlackboard().isEmpty()) {
                for (var entry : plan.actionBlackboard().entrySet()) {
                    lines.add(
                        new HudLine(
                            "        %s: %s".formatted(entry.getKey(), entry.getValue()),
                            BLACKBOARD_COLOR
                        )
                    );
                }
            }
        }
    }

    private static boolean isTerminalState(String planState) {
        return "FINISHED".equals(planState) || "ABORTED".equals(planState) || "INVALID".equals(planState);
    }

    private static boolean isFailedState(String planState) {
        return "ABORTED".equals(planState) || "INVALID".equals(planState);
    }

    static final class AgentDisplayState {

        private final Map<String, DisplayPlan> activePlansByGoal = new HashMap<>();

        private final List<DisplayPlan> ghostPlans = new ArrayList<>();

        void update(GOAPAgentDebugData agent) {
            var now = System.currentTimeMillis();
            var incomingGoals = new HashMap<String, GOAPPlanDebugData>();

            for (var plan : agent.plans()) {
                incomingGoals.put(plan.goalName(), plan);
            }

            // Remove ghost plans whose goal was replanned, or whose timer expired.
            ghostPlans.removeIf(ghost -> incomingGoals.containsKey(ghost.plan.goalName()) || ghost.isExpired(now));

            // Detect plans that disappeared or reached a terminal state -> become ghosts.
            var previousGoals = new HashMap<>(activePlansByGoal);
            activePlansByGoal.clear();

            for (var entry : previousGoals.entrySet()) {
                var goalName = entry.getKey();
                var prev = entry.getValue();

                if (!incomingGoals.containsKey(goalName)) {
                    // Plan disappeared entirely. Treat as finished (server removed it).
                    ghostPlans.add(new DisplayPlan(prev.plan, "FINISHED", now));
                }
            }

            // Process incoming plans.
            for (var plan : agent.plans()) {
                if (isTerminalState(plan.planState())) {
                    // Plan reached a terminal state but is still reported. Ghost it.
                    ghostPlans.add(new DisplayPlan(plan, plan.planState(), now));
                } else {
                    activePlansByGoal.put(plan.goalName(), new DisplayPlan(plan, null, 0));
                }

                // If this goal had a ghost (e.g. failed then replanned), remove the ghost.
                ghostPlans.removeIf(ghost -> ghost.plan.goalName().equals(plan.goalName()));
            }
        }

        List<DisplayPlan> getDisplayPlans() {
            var now = System.currentTimeMillis();
            var result = new ArrayList<DisplayPlan>();

            // Active plans first.
            result.addAll(activePlansByGoal.values());

            // Then non-expired ghosts.
            for (var ghost : ghostPlans) {
                if (!ghost.isExpired(now)) {
                    result.add(ghost);
                }
            }

            return result;
        }
    }

    static final class DisplayPlan {

        final GOAPPlanDebugData plan;

        final @Nullable String terminalState;

        final long ghostStartTime;

        DisplayPlan(GOAPPlanDebugData plan, @Nullable String terminalState, long ghostStartTime) {
            this.plan = plan;
            this.terminalState = terminalState;
            this.ghostStartTime = ghostStartTime;
        }

        boolean isGhost() {
            return terminalState != null;
        }

        boolean isExpired(long now) {
            return isGhost() && (now - ghostStartTime) >= GHOST_DURATION_MS;
        }

        int getColor() {
            if (!isGhost()) {
                return TEXT_COLOR;
            }

            return isFailedState(terminalState) ? FAILED_COLOR : COMPLETED_COLOR;
        }
    }

    private record HudLine(
        String text,
        int color
    ) {}
}
