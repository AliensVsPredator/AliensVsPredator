package com.blib.mod.client.render.goap.panel;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.blib.mod.client.render.goap.GOAPDebugAgentDisplayState;
import com.blib.mod.client.render.goap.constant.GOAPDebugHUDConstants;
import com.blib.mod.client.render.goap.model.DisplayPlan;
import com.blib.mod.client.render.goap.model.GOAPAgentDebugData;
import com.blib.mod.client.render.goap.model.HUDLine;

@ApiStatus.Internal
public final class GOAPDebugAgentPanel {

    public static final GOAPDebugAgentPanel INSTANCE = new GOAPDebugAgentPanel();

    private GOAPDebugAgentPanel() {}

    public void render(
        GuiGraphics graphics,
        Font font,
        GOAPAgentDebugData agent,
        int selectedIndex,
        int totalAgents,
        @Nullable GOAPDebugAgentDisplayState displayState
    ) {
        var lines = buildLines(agent, selectedIndex, totalAgents, displayState);
        renderPanel(graphics, font, lines, GOAPDebugHUDConstants.PADDING, GOAPDebugHUDConstants.PADDING);
    }

    private void renderPanel(GuiGraphics graphics, Font font, List<HUDLine> lines, int x, int y) {
        var maxWidth = 0;

        for (var line : lines) {
            maxWidth = Math.max(maxWidth, font.width(line.text()));
        }

        var totalHeight = lines.size() * GOAPDebugHUDConstants.LINE_HEIGHT + GOAPDebugHUDConstants.PADDING * 2;
        var totalWidth = maxWidth + GOAPDebugHUDConstants.PADDING * 2;

        graphics.fill(x, y, x + totalWidth, y + totalHeight, GOAPDebugHUDConstants.BACKGROUND_COLOR);

        var textY = y + GOAPDebugHUDConstants.PADDING;

        for (var line : lines) {
            graphics.drawString(font, line.text(), x + GOAPDebugHUDConstants.PADDING, textY, line.color(), false);
            textY += GOAPDebugHUDConstants.LINE_HEIGHT;
        }
    }

    private List<HUDLine> buildLines(
        GOAPAgentDebugData agent,
        int selectedIndex,
        int totalAgents,
        @Nullable GOAPDebugAgentDisplayState displayState
    ) {
        var lines = new ArrayList<HUDLine>();

        lines.add(
            new HUDLine(
                "GOAP: %s (%d/%d)".formatted(agent.entityName(), selectedIndex + 1, totalAgents),
                GOAPDebugHUDConstants.HEADER_COLOR
            )
        );

        lines.add(
            new HUDLine(
                "UUID: %s | Pos: %d, %d, %d".formatted(agent.entityUuid(), agent.posX(), agent.posY(), agent.posZ()),
                GOAPDebugHUDConstants.BLACKBOARD_COLOR
            )
        );

        var displayPlans = displayState != null ? displayState.getDisplayPlans() : List.<DisplayPlan>of();
        var activePlanCount = agent.plans().size();

        lines.add(
            new HUDLine(
                "Tick: %d | Plans: %d".formatted(agent.agentTick(), activePlanCount),
                GOAPDebugHUDConstants.TEXT_COLOR
            )
        );

        lines.add(
            new HUDLine(
                "Graph: %d goals, %d actions, %d sensors".formatted(
                    agent.graphGoalCount(),
                    agent.graphActionCount(),
                    agent.graphSensorKeys().size()
                ),
                GOAPDebugHUDConstants.GRAPH_COLOR
            )
        );

        if (displayPlans.isEmpty()) {
            lines.add(new HUDLine("No active plans.", GOAPDebugHUDConstants.TEXT_COLOR));
        } else {
            for (var i = 0; i < displayPlans.size(); i++) {
                var displayPlan = displayPlans.get(i);
                lines.add(new HUDLine("", GOAPDebugHUDConstants.TEXT_COLOR));
                formatPlan(lines, displayPlan, i);
            }
        }

        return lines;
    }

    private void formatPlan(List<HUDLine> lines, DisplayPlan displayPlan, int planIndex) {
        var plan = displayPlan.plan();
        var planColor = displayPlan.getColor();

        lines.add(
            new HUDLine(
                "Plan #%d: [%s] \"%s\" (%.1f)".formatted(
                    planIndex,
                    displayPlan.isGhost() ? displayPlan.terminalState() : plan.planState(),
                    plan.goalName(),
                    plan.initialCost()
                ),
                planColor
            )
        );

        if (!plan.planBlackboard().isEmpty()) {
            for (var entry : plan.planBlackboard().entrySet()) {
                lines.add(
                    new HUDLine(
                        "  %s: %s".formatted(entry.getKey(), entry.getValue()),
                        GOAPDebugHUDConstants.BLACKBOARD_COLOR
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
                color = isCurrent ? GOAPDebugHUDConstants.HIGHLIGHT_COLOR : GOAPDebugHUDConstants.TEXT_COLOR;
            }

            lines.add(
                new HUDLine(
                    "  %s%d. %s".formatted(marker, i + 1, actionName),
                    color
                )
            );

            if (isCurrent && !plan.actionBlackboard().isEmpty()) {
                for (var entry : plan.actionBlackboard().entrySet()) {
                    lines.add(
                        new HUDLine(
                            "        %s: %s".formatted(entry.getKey(), entry.getValue()),
                            GOAPDebugHUDConstants.BLACKBOARD_COLOR
                        )
                    );
                }
            }
        }
    }

}
