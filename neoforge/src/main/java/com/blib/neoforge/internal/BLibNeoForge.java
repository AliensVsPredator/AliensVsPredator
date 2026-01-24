package com.blib.neoforge.internal;

import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.BLibAPI;
import com.blib.mod.BLib;

@ApiStatus.Internal
@Mod(BLib.MOD_ID)
public class BLibNeoForge {

    public BLibNeoForge() {
        BLibAPI.initialize();
    }
}
