package org.avp.fabric;

import net.fabricmc.api.ModInitializer;

import org.avp.common.AVPCommon;
import org.avp.common.game.block.AVPDispenserBlockBehaviors;
import org.avp.common.registry.alien.infection.AVPAlienInfectionRegistry;
import org.avp.common.registry.alien.maturation.AVPAlienMaturationRegistry;
import org.avp.fabric.common.command.AVPFabricCommands;
import org.avp.fabric.common.data.AVPFabricLootTableModifier;
import org.avp.fabric.common.registry.AVPFabricFuelRegistry;
import org.avp.fabric.common.worldgen.AVPFabricEntitySpawns;
import org.avp.fabric.common.worldgen.AVPFabricWorldGenFeatures;
import org.avp.fabric.service.FabricEntityAttributeRegistry;

public class AVPFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // Use Fabric to bootstrap the Common mod.
        AVPCommon.init();

        // Dispenser behavior registration.
        AVPDispenserBlockBehaviors.INSTANCE.registerAll();

        // Fuel registration.
        AVPFabricFuelRegistry.register();

        // Alien lifecycle registration.
        AVPAlienInfectionRegistry.INSTANCE.register();
        AVPAlienMaturationRegistry.INSTANCE.register();

        // Remaining steps.
        AVPFabricEntitySpawns.addEntitySpawns();
        AVPFabricLootTableModifier.registerVanillaLootTableModifications();
        AVPFabricWorldGenFeatures.INSTANCE.register();
        FabricEntityAttributeRegistry.INSTANCE.register();
        AVPFabricCommands.register();
    }
}
