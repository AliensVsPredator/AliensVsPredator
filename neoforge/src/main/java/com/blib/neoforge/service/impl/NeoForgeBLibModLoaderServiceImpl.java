package com.blib.neoforge.service.impl;

import com.blib.service.BLibModLoaderService;
import com.lib.common.util.Version;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

public class NeoForgeBLibModLoaderServiceImpl implements BLibModLoaderService {

    @Override
    public String getModLoaderName() {
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
    public @Nullable Version getModVersion(String modId) {
        return ModList.get()
            .getModContainerById(modId)
            .map(mod -> mod.getModInfo().getVersion().toString())
            .map(Version::parse)
            .orElse(null);
    }

}
