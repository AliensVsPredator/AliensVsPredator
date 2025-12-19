package com.blib.neoforge.internal;

import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLib;

@ApiStatus.Internal
@Mod(BLib.MOD_ID)
public class BLibNeoForge {

    public BLibNeoForge() {
        BLib.initialize();
    }
}
