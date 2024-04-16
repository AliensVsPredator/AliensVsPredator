package org.avp.fabric;

import net.fabricmc.api.ModInitializer;

import org.avp.common.AVPCommon;
import org.avp.fabric.common.AVPFabricCommands;
import org.avp.fabric.common.data.AVPFabricLootTableModifier;
import org.avp.fabric.common.registry.AVPDeferredBlockRegistry;
import org.avp.fabric.common.registry.AVPDeferredCreativeTabRegistry;
import org.avp.fabric.common.registry.AVPDeferredItemRegistry;
import org.avp.fabric.common.registry.AVPFabricFuelRegistry;
import org.avp.fabric.common.worldgen.AVPFabricEntitySpawns;
import org.avp.fabric.common.worldgen.AVPFabricWorldGenFeatures;
import org.avp.fabric.service.FabricEntityAttributeRegistry;

public class AVPFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // Use Fabric to bootstrap the Common mod.
        AVPCommon.init();

        // Deferred registration step.
        AVPDeferredItemRegistry.registerAll();
        AVPDeferredBlockRegistry.registerAll();
        AVPDeferredCreativeTabRegistry.registerAll();

        // Fuel registration.
        AVPFabricFuelRegistry.register();

        // Remaining steps.
        AVPFabricEntitySpawns.addEntitySpawns();
        AVPFabricLootTableModifier.registerVanillaLootTableModifications();
        AVPFabricWorldGenFeatures.forceInitialization();
        FabricEntityAttributeRegistry.getInstance().register();
        AVPFabricCommands.register();
    }
}
