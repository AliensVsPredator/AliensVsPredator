package com.blib.api.common.goap.v1;

import com.just.ai.goap.Agent;
import com.just.ai.goap.graph.Graph;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface GOAPUser<T extends LivingEntity> {

    @Nullable
    Graph<T> blib$getGOAPGraphOrNull();

    default Agent.Builder<T> blib$applyGOAPAgentProperties(Agent.Builder<T> agentBuilder) {
        return agentBuilder;
    }

    default @Nullable LivingEntityAgent<T> blib$getGOAPAgentOrNull() {
        return null;
    }
}
