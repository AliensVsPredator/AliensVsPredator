package org.avp.common.entity.living;

import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.avp.common.animation.FacehuggerAnimations;
import org.avp.common.entity.AVPAbstractParasite;
import org.jetbrains.annotations.NotNull;

public class Facehugger extends AVPAbstractParasite {

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public Facehugger(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        FacehuggerAnimations.bootstrap(this, controllers);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
