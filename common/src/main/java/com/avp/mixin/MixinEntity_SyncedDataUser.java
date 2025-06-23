package com.avp.mixin;

import com.lib.common.network.SyncedDataContainer;
import com.lib.common.network.SyncedDataUser;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity_SyncedDataUser implements SyncedDataUser {

    @Unique
    private final SyncedDataContainer avp$syncedDataContainer = new SyncedDataContainer();

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        var self = Entity.class.cast(this);

        if (!self.level().isClientSide && getSyncedDataContainer().isDirty()) {
            getSyncedDataContainer().syncToClient(self, SyncedDataContainer.SyncType.DIRTY);
        }
    }

    @Override
    public SyncedDataContainer getSyncedDataContainer() {
        return avp$syncedDataContainer;
    }
}
