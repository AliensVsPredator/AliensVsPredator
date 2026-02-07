package com.blib.internal.mixin;

import com.just.goap.plan.Plan;
import com.just.goap.state.Blackboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Plan.class)
public interface MixinPlan_Accessor {

    @Accessor(value = "currentActionIndex")
    int getCurrentActionIndex();

    @Accessor(value = "actionBlackboard")
    Blackboard getActionBlackboard();
}
