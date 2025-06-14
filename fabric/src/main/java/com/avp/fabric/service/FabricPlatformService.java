package com.avp.fabric.service;

import com.lib.common.util.Version;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.service.PlatformService;

public class FabricPlatformService implements PlatformService {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public @Nullable Version getModVersion() {
        var container = FabricLoader.getInstance().getModContainer(AVP.MOD_ID);

        return container
            .map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString())
            .map(Version::parse)
            .orElse(null);
    }

}
