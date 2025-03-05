package com.avp.core.common.util;

import mod.azure.azurelib.common.internal.common.registry.AzureBlocksRegistry;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.world.entity.Entity;

public class GunLightUtil {

    public static void spawnLightSource(Entity entity) {
        var lightBlockPos = AzureLibUtil.findFreeSpace(entity.level(), entity.blockPosition(), 2);

        if (entity.level().isClientSide()) {
            return;
        }

        if (lightBlockPos == null) {
            return;
        }

        entity.level().setBlockAndUpdate(lightBlockPos, AzureBlocksRegistry.TICKING_LIGHT_BLOCK.get().defaultBlockState());
    }
}
