package com.blib.neoforge.internal.client.service.impl;

import net.neoforged.fml.ModList;

import com.blib.internal.client.service.BLibClientIrisCompatService;

public final class BLibNeoForgeClientIrisCompatServiceImpl implements BLibClientIrisCompatService {

    @Override
    public boolean isShaderModActive() {
        var modList = ModList.get();

        if (modList == null) {
            return false;
        }

        return modList.isLoaded("oculus") || modList.isLoaded("iris");
    }
}
