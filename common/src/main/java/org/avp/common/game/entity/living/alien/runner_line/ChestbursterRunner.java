package org.avp.common.game.entity.living.alien.runner_line;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import org.avp.common.ai.AIUtils;
import org.avp.common.data.entity.living.alien.base_line.FacehuggerData;
import org.avp.common.game.entity.type.Morphable;

public class ChestbursterRunner extends Monster implements Morphable, GeoEntity {

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public ChestbursterRunner(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        AIUtils.addChestbursterAI(this, goalSelector);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO:
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public EntityType<?> getEntityTypeForPreviousForm() {
        return FacehuggerData.INSTANCE.getHolder().get();
    }
}
