package org.avp.common.game.entity.living.alien.beluga_line;

import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.avp.common.game.entity.AbstractParasite;

public class Octohugger extends AbstractParasite {

    public Octohugger(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO:
    }
}
