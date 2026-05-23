package com.blib.internal.mixin.server;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

import com.blib.internal.common.event.BLibGlobalEvents;

@Mixin(ChunkMap.class)
public abstract class MixinChunkMap_ChunkLoadEvent {

    @Shadow
    @Final
    ServerLevel level;

    @Unique
    private final Set<ChunkPos> blib$firedChunks = new HashSet<>();

    @Inject(at = @At("TAIL"), method = "onFullChunkStatusChange")
    private void blib$onChunkLoad(ChunkPos pos, FullChunkStatus fullChunkStatus, CallbackInfo ci) {
        if (!fullChunkStatus.isOrAfter(FullChunkStatus.FULL)) {
            return;
        }

        if (!blib$firedChunks.add(pos)) {
            return;
        }

        var listeners = BLibGlobalEvents.CHUNK_LOAD.listeners();

        if (listeners.isEmpty()) {
            return;
        }

        var chunk = level.getChunk(pos.x, pos.z);

        if (!(chunk instanceof LevelChunk levelChunk)) {
            return;
        }

        for (var listener : listeners) {
            listener.invoke(level, levelChunk);
        }
    }
}
