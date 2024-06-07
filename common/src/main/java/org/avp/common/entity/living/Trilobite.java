package org.avp.common.entity.living;

import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.avp.api.entity.Morphable;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.common.entity.data.TrilobiteBabyData;

public class Trilobite extends AVPAbstractParasite implements Morphable {

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
