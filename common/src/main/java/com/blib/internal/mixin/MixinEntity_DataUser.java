package com.blib.internal.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blib.common.network.data.DataContainer;
import com.blib.common.network.data.DataUser;

@ApiStatus.Internal
@Mixin(Entity.class)
public abstract class MixinEntity_DataUser implements DataUser {

    @Unique
    private final DataContainer blib$DataContainer = new DataContainer();

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        var self = Entity.class.cast(this);

        if (!self.level().isClientSide && getDataContainer().isDirty()) {
            getDataContainer().syncToClient(self, DataContainer.SyncType.DIRTY);
        }
    }

    @Inject(
        method = "saveWithoutId",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/world/entity/Entity.addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
        )
    )
    private void blib$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfoReturnable<Boolean> cir) {
        blib$DataContainer.save(compoundTag);
    }

    @Inject(
        method = "load",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
        )
    )
    private void blib$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        blib$DataContainer.load(compoundTag);
    }

    @Override
    public DataContainer getDataContainer() {
        return blib$DataContainer;
    }
}
