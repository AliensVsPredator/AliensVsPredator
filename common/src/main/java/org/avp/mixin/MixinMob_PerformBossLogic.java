package org.avp.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.avp.api.entity.Boss;
import org.avp.common.util.MixinUtils;

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
            var level = self.level();

            boss.getBossEvent().setProgress(self.getHealth() / self.getMaxHealth());

            if (self.tickCount % 20 == 0) {
                var nearbyPlayers = level.getNearbyPlayers(TargetingConditions.DEFAULT, self, self.getBoundingBox().inflate(16));
                nearbyPlayers.forEach(player -> boss.getBossEvent().addPlayer((ServerPlayer) player));

                var playersToRemove = boss.getBossEvent()
                    .getPlayers()
                    .stream()
                    .filter(serverPlayer -> !nearbyPlayers.contains(serverPlayer))
                    .toList();

                playersToRemove.forEach(boss.getBossEvent()::removePlayer);
            }
        }
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        // TODO: Need to do boss encounter "start"-related logic here eventually.
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
