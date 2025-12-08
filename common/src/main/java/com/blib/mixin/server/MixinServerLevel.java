package com.blib.mixin.server;

import com.blib.server.BlockBreakProgressManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel {

    @Inject(at = @At("HEAD"), method = "onBlockStateChange")
    public void blib$onBlockStateChange(BlockPos pos, BlockState blockState, BlockState newState, CallbackInfo callbackInfo) {
        var serverLevel = ServerLevel.class.cast(this);
        BlockBreakProgressManager.resetProgress(serverLevel, pos);
    }
}
