package com.lib.common.gameplay.goap;

import com.just.goap.graph.Graph;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface GOAPUser<T extends LivingEntity> {

    @Nullable
    Graph<T> getCurrentGraph();

    default @Nullable LivingEntityAgent<T> getGOAPAgentOrNull() {
        return null;
    }
}
