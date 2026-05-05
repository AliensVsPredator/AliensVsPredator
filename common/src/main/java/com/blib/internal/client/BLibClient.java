package com.blib.internal.client;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.mod.v1.BLibClientMod;
import com.blib.api.client.render.v1.dismemberment.BuiltInModelPartResolvers;
import com.blib.api.client.render.v1.dismemberment.LimbEntityRenderer;
import com.blib.internal.client.posteffect.BLibBuiltInPostEffects;
import com.blib.mod.BLib;
import com.blib.mod.common.registry.init.BLibEntityTypes;

public final class BLibClient {

    @ApiStatus.Internal
    public static final BLibClientMod MOD = BLibClientMod.createFor(BLib.MOD);

    @ApiStatus.Internal
    public static void initialize() {
        MOD.initialize(BLibClient::runInitialization);
    }

    private static void runInitialization() {
        MOD.registries().registerEntityRenderer(BLibEntityTypes.DISMEMBERED_LIMB, LimbEntityRenderer::new);
        BuiltInModelPartResolvers.register();
        BLibBuiltInPostEffects.register();
    }

    @ApiStatus.Internal
    private BLibClient() {
        throw new UnsupportedOperationException();
    }
}
