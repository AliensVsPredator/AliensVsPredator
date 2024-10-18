package org.avp.neoforge.common.event;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import org.avp.common.game.block.AVPDispenserBlockBehaviors;

public class AVPCommonSetupEvent {

    public static void handleCommonSetupEvent(FMLCommonSetupEvent fmlCommonSetupEvent) {
        fmlCommonSetupEvent.enqueueWork(AVPDispenserBlockBehaviors.INSTANCE::registerAll);
    }

    private AVPCommonSetupEvent() {
        throw new UnsupportedOperationException();
    }
}
