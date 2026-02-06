package com.blib.internal.client.goap;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

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

    private @Nullable S2CGOAPDebugPayload latestPayload;

    private GOAPDebugHud() {}

    public void update(S2CGOAPDebugPayload payload) {
        this.latestPayload = payload;
    }

    public void clear() {
        this.latestPayload = null;
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
        var lines = buildLines(agent, selectedIndex, agents.size());
        var font = net.minecraft.client.Minecraft.getInstance().font;

        var maxWidth = 0;

        for (var line : lines) {
            maxWidth = Math.max(maxWidth, font.width(line.text()));
        }

        var totalHeight = lines.size() * LINE_HEIGHT + PADDING * 2;
        var totalWidth = maxWidth + PADDING * 2;

        graphics.fill(PADDING, PADDING, PADDING + totalWidth, PADDING + totalHeight, BACKGROUND_COLOR);

        var y = PADDING + PADDING;

        for (var line : lines) {
            graphics.drawString(font, line.text(), PADDING + PADDING, y, line.color(), false);
            y += LINE_HEIGHT;
        }
    }

    private java.util.List<HudLine> buildLines(GOAPAgentDebugData agent, int selectedIndex, int totalAgents) {
        var lines = new java.util.ArrayList<HudLine>();

        lines.add(
            new HudLine(
                "GOAP: %s (%d/%d)".formatted(agent.entityName(), selectedIndex + 1, totalAgents),
                HEADER_COLOR
            )
        );

        lines.add(
            new HudLine(
                "Tick: %d | Plans: %d".formatted(agent.agentTick(), agent.plans().size()),
                TEXT_COLOR
            )
        );

        if (agent.plans().isEmpty()) {
            lines.add(new HudLine("No active plans.", TEXT_COLOR));
        } else {
            for (var i = 0; i < agent.plans().size(); i++) {
                var plan = agent.plans().get(i);
                lines.add(new HudLine("", TEXT_COLOR));
                formatPlan(lines, plan, i);
            }
        }

        return lines;
    }

    private void formatPlan(java.util.List<HudLine> lines, GOAPPlanDebugData plan, int planIndex) {
        lines.add(
            new HudLine(
                "Plan #%d: [%s] \"%s\" (%.1f)".formatted(planIndex, plan.planState(), plan.goalName(), plan.initialCost()),
                TEXT_COLOR
            )
        );

        for (var i = 0; i < plan.actionNames().size(); i++) {
            var actionName = plan.actionNames().get(i);
            var isCurrent = i == plan.currentActionIndex();
            var marker = isCurrent ? ">> " : "   ";
            var color = isCurrent ? HIGHLIGHT_COLOR : TEXT_COLOR;

            lines.add(
                new HudLine(
                    "  %s%d. %s".formatted(marker, i + 1, actionName),
                    color
                )
            );
        }
    }

    private record HudLine(
        String text,
        int color
    ) {}
}
