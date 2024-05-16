package org.avp.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.avp.api.entity.Boss;
import org.avp.common.util.MixinUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MixinMob_PerformBossLogic extends LivingEntity {
    protected MixinMob_PerformBossLogic(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    void readAdditionalSaveData(@NotNull CompoundTag compoundTag, CallbackInfo callbackInfo) {
        if (this instanceof Boss boss && this.hasCustomName()) {
            boss.getBossEvent().setName(this.getDisplayName());
        }
    }

    @Inject(at = @At("TAIL"), method = "customServerAiStep")
    void customServerAiStep(CallbackInfo callbackInfo) {
        if (this instanceof Boss boss) {
            var self = MixinUtils.<Mob>self(this);
            boss.getBossEvent().setProgress(self.getHealth() / self.getMaxHealth());
        }
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        if (this instanceof Boss boss) {
            boss.getBossEvent().addPlayer(serverPlayer);
        }
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        if (this instanceof Boss boss) {
            boss.getBossEvent().removePlayer(serverPlayer);
        }
    }

    @Override
    public void setCustomName(@Nullable Component component) {
        super.setCustomName(component);
        if (this instanceof Boss boss) {
            boss.getBossEvent().setName(this.getDisplayName());
        }
    }
}
