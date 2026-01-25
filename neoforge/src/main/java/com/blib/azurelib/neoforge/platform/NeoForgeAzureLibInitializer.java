package com.blib.azurelib.neoforge.platform;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import com.blib.azurelib.common.cache.AzureLibCache;
import com.blib.azurelib.common.platform.services.AzureLibInitializer;

public class NeoForgeAzureLibInitializer implements AzureLibInitializer {

    @Override
    public void initialize() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            AzureLibCache.registerReloadListener();
        }
    }
}
