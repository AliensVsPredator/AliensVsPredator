package com.blib.neoforge.internal;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import com.blib.mod.BLib;
import com.blib.neoforge.internal.event.BLibNeoForgeTerritoryEvents;

@ApiStatus.Internal
@Mod(BLib.MOD_ID)
public class BLibNeoForge {

    public BLibNeoForge() {
        BLib.initialize();
        NeoForge.EVENT_BUS.addListener(BLibNeoForgeTerritoryEvents::onBlockInteract);
        NeoForge.EVENT_BUS.addListener(BLibNeoForgeTerritoryEvents::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(BLibNeoForgeTerritoryEvents::onEntityAttack);
        NeoForge.EVENT_BUS.addListener(BLibNeoForgeTerritoryEvents::onMobGriefing);
    }
}
