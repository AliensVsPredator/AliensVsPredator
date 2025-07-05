package com.avp.mixin.server;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.avp.server.ServerLevelManager;
import com.avp.server.ServerLevelManagerAccessor;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel_RunTickRoutines implements ServerLevelManagerAccessor {

    @Unique
    private final ServerLevelManager avp$serverLevelManager = new ServerLevelManager();

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        var serverLevel = ServerLevel.class.cast(this);
        avp$serverLevelManager.tick(serverLevel);
    }

    @Override
    public ServerLevelManager getServerLevelManager() {
        return avp$serverLevelManager;
    }
}
