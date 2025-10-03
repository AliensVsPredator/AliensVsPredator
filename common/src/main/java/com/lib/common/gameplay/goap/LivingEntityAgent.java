package com.lib.common.gameplay.goap;

import com.just.goap.Agent;
import com.just.goap.graph.Graph;
import net.minecraft.world.entity.LivingEntity;

public class LivingEntityAgent<T extends LivingEntity> {

    private final Agent<T> agent;

    private boolean isEnabled;

    public LivingEntityAgent() {
        this.agent = Agent.create();
        this.isEnabled = true;
    }

    public void update(Graph<T> graph, T context) {
        if (!isEnabled || context.isDeadOrDying()) {
            return;
        }

        agent.update(graph, context);
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
