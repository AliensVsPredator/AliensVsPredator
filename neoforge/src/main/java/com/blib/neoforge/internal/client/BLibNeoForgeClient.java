package com.blib.neoforge.internal.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.BLibClient;
import com.blib.mod.BLib;

@ApiStatus.Internal
@Mod(value = BLib.MOD_ID, dist = Dist.CLIENT)
public class BLibNeoForgeClient {

    public BLibNeoForgeClient() {
        BLibClient.initialize();
        BLibNeoForgeGOAPDebugHUD.register();
        BLibNeoForgePathfindingNavDebugHUD.register();
    }
}
