package com.blib.internal.mixin;

import com.just.goap.Agent;
import com.just.goap.state.SensingWorldState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Agent.class)
public interface MixinAgent_Accessor {

    @Accessor(value = "currentWorldState")
    SensingWorldState<?> getCurrentWorldState();
}
