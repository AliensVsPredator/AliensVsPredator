package com.blib.internal.mixin.server;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.common.territory.BLibTerritoryManager;

@Mixin(Explosion.class)
public abstract class MixinExplosion_TerritoryProtection {

    @Shadow
    @Final
    private Level level;

    @Shadow
    public abstract java.util.List<BlockPos> getToBlow();

    @Inject(at = @At("HEAD"), method = "finalizeExplosion")
    private void blib$removeProtectedBlocks(boolean spawnParticles, CallbackInfo ci) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        getToBlow().removeIf(blockPos -> {
            var chunkPos = new ChunkPos(blockPos);

            return !BLibTerritoryManager.INSTANCE.allowExplosionsAt(serverLevel, chunkPos);
        });
    }
}
