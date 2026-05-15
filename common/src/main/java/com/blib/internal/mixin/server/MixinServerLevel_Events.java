package com.blib.internal.mixin.server;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.common.event.BLibGlobalEvents;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel_Events {

    @Inject(at = @At("TAIL"), method = "save")
    private void blib$onSave(ProgressListener progressListener, boolean flush, boolean skipSave, CallbackInfo ci) {
        if (skipSave) {
            return;
        }

        var listeners = BLibGlobalEvents.LEVEL_SAVE.listeners();

        if (listeners.isEmpty()) {
            return;
        }

        var self = ServerLevel.class.cast(this);

        for (var listener : listeners) {
            listener.invoke(self);
        }
    }

    @Inject(at = @At("TAIL"), method = "unload")
    public void blib$onUnload(LevelChunk chunk, CallbackInfo ci) {
        var listeners = BLibGlobalEvents.CHUNK_UNLOAD.listeners();

        if (listeners.isEmpty()) {
            return;
        }

        var self = ServerLevel.class.cast(this);

        for (var listener : listeners) {
            listener.invoke(self, chunk);
        }
    }
}
