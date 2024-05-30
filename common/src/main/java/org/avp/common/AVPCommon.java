package org.avp.common;

import mod.azure.azurelib.common.internal.common.AzureLib;

import org.avp.client.render.particle.AVPParticleTypes;
import org.avp.common.block.*;
import org.avp.common.block.entity.data.AVPBlockEntityDataRegistry;
import org.avp.common.config.AVPConfig;
import org.avp.common.creative_tab.AVPCreativeModeTabs;
import org.avp.common.entity.data.AVPEntityDataRegistry;
import org.avp.common.item.*;
import org.avp.common.network.AVPNetworkPayloadHandlerRegistry;
import org.avp.common.registry.AVPFuelRegistry;
import org.avp.common.registry.AVPSimpleDeferredBlockEntityTypeRegistry;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.sound.AVPSoundEvents;

public class AVPCommon {

    public static void init() {
        // Initialize AzureLib.
        AzureLib.initialize();

        // AVPConfig initialization.
        AVPConfig.init();

        // Networking
        AVPNetworkPayloadHandlerRegistry.forceInitialization();

        // Fuel
        AVPFuelRegistry.INSTANCE.register();

        // Items
        AVPAmmunitionPartItems.INSTANCE.register();
        AVPArmorItems.INSTANCE.register();
        AVPBulletItems.INSTANCE.register();
        AVPElectronicItems.INSTANCE.register();
        AVPFoodItems.INSTANCE.register();
        AVPItems.INSTANCE.register();
        AVPToolItems.INSTANCE.register();
        AVPWeaponBlueprintItems.INSTANCE.register();
        AVPWeaponItems.INSTANCE.register();
        AVPWeaponPartItems.INSTANCE.register();

        // Blocks
        AVPAlienBlocks.INSTANCE.register();
        AVPBlocks.INSTANCE.register();
        AVPEngineerBlocks.INSTANCE.register();
        AVPIndustrialBlocks.INSTANCE.register();
        AVPIndustrialGlassBlocks.INSTANCE.register();
        AVPMachineBlocks.INSTANCE.register();
        AVPOreBlocks.INSTANCE.register();
        AVPPaddingBlocks.INSTANCE.register();
        AVPParadiseBlocks.INSTANCE.register();
        AVPTempleBlocks.INSTANCE.register();
        AVPUnidentifiedBlocks.INSTANCE.register();
        AVPYautjaShipBlocks.INSTANCE.register();

        // Item Blocks
        AVPItemBlockItems.INSTANCE.register();

        // Block Entity Data
        // Must be registered first, sets up block entity type holders.
        AVPBlockEntityDataRegistry.INSTANCE.register();

        // Block Entity Types
        AVPSimpleDeferredBlockEntityTypeRegistry.INSTANCE.register();

        // Sounds
        AVPSoundEvents.INSTANCE.register();

        // Entity Data
        // Must be registered first, sets up entity type holders.
        AVPEntityDataRegistry.INSTANCE.register();

        // Entity Types
        AVPSimpleDeferredEntityTypeRegistry.INSTANCE.register();

        // Spawn Egg Items
        AVPSpawnEggItems.INSTANCE.register();

        // Particles
        AVPParticleTypes.INSTANCE.register();

        // It's important to register creative mode tabs last, as entities generate spawn eggs automatically.
        AVPCreativeModeTabs.INSTANCE.register();
    }

    private AVPCommon() {
        throw new UnsupportedOperationException();
    }
}
