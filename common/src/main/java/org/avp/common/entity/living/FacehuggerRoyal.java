package org.avp.common.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.avp.common.animation.FacehuggerRoyalAnimations;
import org.avp.common.entity.AVPAbstractFacehugger;

public class FacehuggerRoyal extends AVPAbstractFacehugger {

    public FacehuggerRoyal(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        FacehuggerRoyalAnimations.bootstrap(this, controllers);
    }
}
