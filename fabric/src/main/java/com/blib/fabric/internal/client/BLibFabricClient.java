package com.blib.fabric.internal.client;

import net.fabricmc.api.ClientModInitializer;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.fabric.BLibFabricEnginePlatform;
import com.blib.engine.platform.EngineBootstrap;
import com.blib.fabric.internal.client.shader.BLibFabricShaders;
import com.blib.internal.client.BLibClient;

@ApiStatus.Internal
public class BLibFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BLibClient.initialize();
        BLibFabricShaders.register();
        BLibFabricGOAPDebugHUD.register();
        BLibFabricPathfindingNavDebugHUD.register();
        EngineBootstrap.install(new BLibFabricEnginePlatform());
    }
}
