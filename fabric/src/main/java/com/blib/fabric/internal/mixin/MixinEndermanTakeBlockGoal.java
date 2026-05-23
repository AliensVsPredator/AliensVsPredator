package com.blib.fabric.internal.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blib.internal.common.territory.BLibTerritoryManager;

@Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanTakeBlockGoal")
public abstract class MixinEndermanTakeBlockGoal {

    @Shadow
    @Final
    private EnderMan enderman;

    @Inject(at = @At("HEAD"), method = "canUse", cancellable = true)
    private void blib$preventTakeInClaimedChunk(CallbackInfoReturnable<Boolean> cir) {
        if (enderman.level() instanceof ServerLevel serverLevel) {
            var chunkPos = new ChunkPos(enderman.blockPosition());

            if (!BLibTerritoryManager.INSTANCE.allowMobGriefingAt(serverLevel, chunkPos)) {
                cir.setReturnValue(false);
            }
        }
    }
}
