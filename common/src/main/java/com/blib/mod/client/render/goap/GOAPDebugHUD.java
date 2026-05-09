package com.blib.mod.client.render.goap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import com.blib.engine.ui.EngineWorkspaceScreen;
import com.blib.mod.client.render.goap.panel.GOAPDebugAgentPanel;
import com.blib.mod.client.render.goap.panel.GOAPDebugWorldStatePanel;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;

@ApiStatus.Internal
public final class GOAPDebugHUD {

    public static final GOAPDebugHUD INSTANCE = new GOAPDebugHUD();

    private @Nullable S2CGOAPDebugPayload latestPayload;

    private final Map<Integer, GOAPDebugAgentDisplayState> agentStates;

    private GOAPDebugHUD() {
        this.agentStates = new HashMap<>();
    }

    public void update(S2CGOAPDebugPayload payload) {
        this.latestPayload = payload;

        for (var agent : payload.agents()) {
            var displayState = agentStates.computeIfAbsent(agent.entityId(), id -> new GOAPDebugAgentDisplayState());
            displayState.update(agent);
        }
    }

    /**
     * Latest payload received from the server, or {@code null} if no agent has been tracked yet (or the user ran
     * {@code /goap untrack}). Read by the engine workspace's GOAP details panel so it doesn't need its own packet
     * subscription.
     */
    public @Nullable S2CGOAPDebugPayload latestPayload() {
        return latestPayload;
    }

    public @Nullable GOAPDebugAgentDisplayState displayStateFor(int entityId) {
        return agentStates.get(entityId);
    }

    public void render(GuiGraphics graphics, float partialTick) {
        if (latestPayload == null || latestPayload.agents().isEmpty()) {
            return;
        }

        // Suppress the HUD overlay while the engine workspace is open — the GOAP Details panel inside the workspace
        // is the primary surface for this data, and double-rendering it (once as fixed-position HUD, once inside the
        // panel) caused visual confusion (the HUD didn't follow the panel when the user moved it, and the rendering
        // overlapped at the default tab position).
        if (Minecraft.getInstance().screen instanceof EngineWorkspaceScreen) {
            return;
        }

        var agents = latestPayload.agents();
        var selectedIndex = latestPayload.selectedIndex();

        if (selectedIndex < 0 || selectedIndex >= agents.size()) {
            return;
        }

        var agent = agents.get(selectedIndex);
        var displayState = agentStates.get(agent.entityId());
        var font = Minecraft.getInstance().font;

        var pose = graphics.pose();
        pose.pushPose();
        pose.scale(0.5f, 0.5f, 1.0f);

        GOAPDebugAgentPanel.INSTANCE.render(graphics, font, agent, selectedIndex, agents.size(), displayState);

        var screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth() * 2;
        GOAPDebugWorldStatePanel.INSTANCE.render(graphics, font, agent, latestPayload, screenWidth);

        pose.popPose();
    }

    public void clear() {
        this.latestPayload = null;

        agentStates.clear();
        GOAPDebugWorldStatePanel.INSTANCE.clear();
    }
}
