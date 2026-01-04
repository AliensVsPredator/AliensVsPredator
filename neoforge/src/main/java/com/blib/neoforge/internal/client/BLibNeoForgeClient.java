package com.blib.neoforge.internal.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLib;
import com.blib.internal.client.BLibClient;

@ApiStatus.Internal
@Mod(value = BLib.MOD_ID, dist = Dist.CLIENT)
public class BLibNeoForgeClient {

    public BLibNeoForgeClient() {
        BLibClient.initialize();
    }
}
