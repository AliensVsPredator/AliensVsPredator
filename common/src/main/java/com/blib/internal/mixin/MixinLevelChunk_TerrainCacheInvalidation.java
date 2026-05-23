package com.blib.internal.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blib.api.common.pathfinding.v1.cache.TerrainCacheRegistry;

@Mixin(LevelChunk.class)
public abstract class MixinLevelChunk_TerrainCacheInvalidation {

    @Shadow
    public abstract Level getLevel();

    @Inject(at = @At("RETURN"), method = "setBlockState")
    private void onSetBlockState(BlockPos pos, BlockState state, boolean flag, CallbackInfoReturnable<BlockState> cir) {
        if (cir.getReturnValue() != null) {
            TerrainCacheRegistry.onBlockChanged(getLevel(), pos);
        }
    }
}
