package org.avp.common.game.entity.living.alien.base_line;

import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import org.avp.common.animation.FacehuggerAnimations;
import org.avp.common.game.entity.AbstractFacehugger;

public class Facehugger extends AbstractFacehugger {

    public Facehugger(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        FacehuggerAnimations.bootstrap(this, controllers);
    }
}
