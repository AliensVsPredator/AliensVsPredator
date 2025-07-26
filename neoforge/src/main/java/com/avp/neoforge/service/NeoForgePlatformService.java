package com.avp.neoforge.service;

import com.lib.common.util.Version;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.service.PlatformService;

public class NeoForgePlatformService implements PlatformService {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public @Nullable Version getModVersion() {
        return ModList.get()
            .getModContainerById(AVP.MOD_ID)
            .map(mod -> mod.getModInfo().getVersion().toString())
            .map(Version::parse)
            .orElse(null);
    }

}
