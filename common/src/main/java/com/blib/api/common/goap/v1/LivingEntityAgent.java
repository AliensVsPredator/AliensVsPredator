package com.blib.api.common.goap.v1;

import com.just.ai.goap.Agent;
import com.just.ai.goap.graph.Graph;
import com.just.ai.goap.plan.ReplanPolicies;
import com.just.ai.goap.plan.executor.impl.ConcurrentPlanExecutor;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.UnaryOperator;

import com.blib.api.common.goap.v1.plan.ActionMaskPlanResolver;

public class LivingEntityAgent<T extends LivingEntity> {

    private final Agent<T> agent;

    private boolean isEnabled;

    public LivingEntityAgent(
        T actor,
        UnaryOperator<Agent.Builder<T>> builderUnaryOperator
    ) {
        this.agent = Agent.builder(actor)
            .withPlanExecutor(
                ConcurrentPlanExecutor.<T>builder()
                    .withPlanResolver(new ActionMaskPlanResolver<>())
                    .withMaxConcurrentPlans(5)
                    .build()
            )
            .withReplanPolicy(ReplanPolicies.ifNoActivePlans())
            .apply(builderUnaryOperator)
            .build();
        this.isEnabled = true;
    }

    public void update(Graph<T> graph) {
        var actor = agent.getActor();

        if (!isEnabled || actor.isDeadOrDying()) {
            return;
        }

        agent.update(graph);
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Agent<T> getBackingAgent() {
        return agent;
    }
}
