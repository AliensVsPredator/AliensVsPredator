package com.blib.neoforge.internal.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.neoforge.BLibNeoForgeEnginePlatform;
import com.blib.engine.platform.EngineBootstrap;
import com.blib.internal.client.BLibClient;
import com.blib.mod.BLib;
import com.blib.neoforge.internal.client.shader.BLibNeoForgeShaders;

@ApiStatus.Internal
@Mod(value = BLib.MOD_ID, dist = Dist.CLIENT)
public class BLibNeoForgeClient {

    public BLibNeoForgeClient(IEventBus modEventBus) {
        BLibClient.initialize();
        BLibNeoForgeShaders.register(modEventBus);
        BLibNeoForgeGOAPDebugHUD.register();
        BLibNeoForgePathfindingNavDebugHUD.register();
        EngineBootstrap.install(new BLibNeoForgeEnginePlatform(modEventBus));
    }
}
