package org.avp.common.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.avp.common.animation.OvamorphAnimations;
import org.avp.common.entity.AVPAbstractOvamorph;

public class Ovamorph extends AVPAbstractOvamorph {

    public Ovamorph(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        OvamorphAnimations.bootstrap(this, controllers);
    }
}
