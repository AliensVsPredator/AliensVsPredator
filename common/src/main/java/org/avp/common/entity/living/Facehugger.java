package org.avp.common.entity.living;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.avp.api.entity.Parasite;
import org.avp.api.entity.data.sync.SyncedDataHandle;
import org.avp.api.entity.data.sync.SyncedDataSerializer;
import org.avp.common.animation.FacehuggerAnimations;
import org.avp.common.entity.ai.parasite.ParasiteBrain;
import org.jetbrains.annotations.NotNull;

public class Facehugger extends Monster implements GeoEntity, Parasite {

    private static final EntityDataAccessor<Boolean> IS_FERTILE = SynchedEntityData.defineId(Facehugger.class, EntityDataSerializers.BOOLEAN);

    private final ParasiteBrain goapBrain;

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public final SyncedDataHandle<Boolean> isFertile;

    public Facehugger(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.goapBrain = new ParasiteBrain(this);
        this.isFertile = SyncedDataHandle.attach("IsFertile", true, this, IS_FERTILE, SyncedDataSerializer.BOOLEAN);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            goapBrain.tick();
        }
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

    @Override
    public boolean isFertile() {
        return isFertile.get();
    }

    @Override
    public void setFertile(boolean isFertile) {
        this.isFertile.set(isFertile);
    }
}
