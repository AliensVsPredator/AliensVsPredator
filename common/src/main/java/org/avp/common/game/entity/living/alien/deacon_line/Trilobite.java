package org.avp.common.game.entity.living.alien.deacon_line;

import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.avp.api.entity.Morphable;
import org.avp.common.game.entity.AbstractParasite;
import org.avp.common.data.entity.living.alien.deacon_line.TrilobiteBabyData;

public class Trilobite extends AbstractParasite implements Morphable {

    public Trilobite(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO:
    }

    @Override
    public EntityType<?> getEntityTypeForPreviousForm() {
        return TrilobiteBabyData.INSTANCE.getHolder().get();
    }
}
