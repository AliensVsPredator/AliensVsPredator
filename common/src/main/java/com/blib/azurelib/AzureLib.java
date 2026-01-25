package com.blib.azurelib;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.blib.azurelib.common.platform.Services;
import com.blib.azurelib.common.render.armor.compat.ShoulderSurfingCompat;
import com.blib.mod.BLib;

public final class AzureLib {

    public static final Logger LOGGER = LogManager.getLogger(AzureLib.class);

    public static boolean hasInitialized;

    private AzureLib() {
        throw new UnsupportedOperationException();
    }

    public static void initialize() {
        if (!hasInitialized) {
            Services.INITIALIZER.initialize();
        }

        hasInitialized = true;
        ShoulderSurfingCompat.init();
    }

    public static ResourceLocation modResource(String path) {
        return BLib.MOD.resources().createLocation(path);
    }
}
