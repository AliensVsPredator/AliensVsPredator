package org.avp.common;

import mod.azure.azurelib.common.internal.common.AzureLib;

import org.avp.common.block.*;
import org.avp.common.config.AVPConfig;
import org.avp.common.creative_tab.AVPCreativeModeTabs;
import org.avp.common.entity.*;
import org.avp.common.entity.spawn.AVPEntitySpawns;
import org.avp.common.item.*;
import org.avp.common.network.AVPNetworkPayloadHandlerRegistry;
import org.avp.common.registry.AVPFuelRegistry;
import org.avp.common.sound.AVPSoundEvents;
import org.avp.common.tag.AVPBiomeTags;
import org.avp.common.tag.AVPBlockTags;
import org.avp.common.tag.AVPEntityTags;
import org.avp.common.worldgen.feature.AVPOreFeatures;

public class AVPCommon {

    public static void init() {
        // Initialize AzureLib.
        AzureLib.initialize();

        // AVPConfig initialization.
        AVPConfig.init();

        // Networking
        AVPNetworkPayloadHandlerRegistry.forceInitialization();

        // World Gen resource keys
        AVPOreFeatures.forceInitialization();

        // Fuel
        AVPFuelRegistry.forceInitialization();

        // Tags
        AVPBiomeTags.forceInitialization();
        AVPBlockTags.forceInitialization();
        AVPEntityTags.forceInitialization();

        // Items
        AVPAmmunitionPartItems.forceInitialization();
        AVPArmorItems.forceInitialization();
        AVPBulletItems.forceInitialization();
        AVPElectronicItems.forceInitialization();
        AVPFoodItems.forceInitialization();
        AVPItems.forceInitialization();
        AVPToolItems.forceInitialization();
        AVPWeaponBlueprintItems.forceInitialization();
        AVPWeaponItems.forceInitialization();
        AVPWeaponPartItems.forceInitialization();

        // Blocks
        AVPAlienBlocks.forceInitialization();
        AVPBlocks.forceInitialization();
        AVPEngineerBlocks.forceInitialization();
        AVPIndustrialBlocks.forceInitialization();
        AVPOreBlocks.forceInitialization();
        AVPPaddingBlocks.forceInitialization();
        AVPParadiseBlocks.forceInitialization();
        AVPTempleBlocks.forceInitialization();
        AVPUnidentifiedBlocks.forceInitialization();
        AVPYautjaShipBlocks.forceInitialization();

        // Entities
        AVPBaseAlienEntityTypes.forceInitialization();
        AVPEngineerEntityTypes.forceInitialization();
        AVPEntityTypes.forceInitialization();
        AVPExoticAlienEntityTypes.forceInitialization();
        AVPPrometheusAlienEntityTypes.forceInitialization();
        AVPRunnerAlienEntityTypes.forceInitialization();
        AVPYautjaEntityTypes.forceInitialization();
        AVPEntitySpawns.forceInitialization();

        // It's important to register creative mode tabs last, as entities generate spawn eggs automatically.
        AVPCreativeModeTabs.register();

        // Sounds
        AVPSoundEvents.forceInitialization();
    }

    private AVPCommon() {
        throw new UnsupportedOperationException();
    }
}
