package com.blib.internal.mixin.server;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blib.internal.common.event.BLibGlobalEvents;

@Mixin(ChunkMap.class)
public abstract class MixinChunkMap_SaveChunkData {

    @Shadow
    @Final
    ServerLevel level;

    @Inject(at = @At("RETURN"), method = "save")
    private void blib$onSave(ChunkAccess chunk, CallbackInfoReturnable<Boolean> ci) {
        var didSave = ci.getReturnValue();

        if (!didSave) {
            return;
        }

        var listeners = BLibGlobalEvents.CHUNK_SAVE.listeners();

        if (listeners.isEmpty()) {
            return;
        }

        var pos = chunk.getPos();

        for (var listener : listeners) {
            listener.invoke(level, pos);
        }
    }
}
