package com.blib.neoforge;

import com.blib.BLibMod;
import com.blib.neoforge.service.impl.NeoForgeBLibEventServiceImpl;
import com.blib.neoforge.service.impl.NeoForgeBLibRegistryServiceImpl;
import com.blib.service.BLibServices;
import net.neoforged.bus.api.IEventBus;

public class BLibNeoForge {

    public static void finalizeMod(BLibMod mod, IEventBus eventBus) {
        ((NeoForgeBLibRegistryServiceImpl) BLibServices.REGISTRY).finalize(mod, eventBus);
        ((NeoForgeBLibEventServiceImpl) BLibServices.EVENT).finalize(mod);
    }

    private BLibNeoForge() {
        throw new UnsupportedOperationException();
    }
}
