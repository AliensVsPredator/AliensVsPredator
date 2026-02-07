package com.blib.internal.mixin;

import com.just.goap.StateKey;
import com.just.goap.state.Blackboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Blackboard.class)
public interface MixinBlackboard_Accessor {

    @Accessor(value = "stateMap")
    Map<StateKey<?>, Object> getStateMap();
}
