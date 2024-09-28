package org.avp.common;

import mod.azure.azurelib.common.internal.common.AzureLib;
import org.avp.client.render.particle.AVPParticleTypeRegistry;
import org.avp.common.config.AVPConfig;
import org.avp.common.game.sound.AVPSoundEventRegistry;
import org.avp.common.network.AVPNetworkPayloadHandlerRegistry;
import org.avp.common.registry.AVPBlockEntityDataRegistry;
import org.avp.common.registry.AVPEntityDataRegistry;
import org.avp.common.registry.AVPFuelRegistry;
import org.avp.common.registry.block.AVPAlienBlockRegistry;
import org.avp.common.registry.block.AVPBlockDataRegistry;
import org.avp.common.registry.block.AVPIndustrialBlockRegistry;
import org.avp.common.registry.block.AVPIndustrialGlassBlockRegistry;
import org.avp.common.registry.block.AVPMachineBlockRegistry;
import org.avp.common.registry.block.AVPParadiseBlockRegistry;
import org.avp.common.registry.block.AVPUnidentifiedBlockRegistry;
import org.avp.common.registry.block_entity.AVPSimpleDeferredBlockEntityTypeRegistry;
import org.avp.common.registry.creative_tab.AVPCreativeModeTabRegistry;
import org.avp.common.registry.entity.AVPSimpleDeferredEntityTypeRegistry;
import org.avp.common.registry.item.AVPAmmunitionPartItemRegistry;
import org.avp.common.registry.item.AVPArmorItemRegistry;
import org.avp.common.registry.item.AVPBulletItemRegistry;
import org.avp.common.registry.item.AVPElectronicItemRegistry;
import org.avp.common.registry.item.AVPFoodItemRegistry;
import org.avp.common.registry.item.AVPItemBlockItemRegistry;
import org.avp.common.registry.item.AVPItemRegistry;
import org.avp.common.registry.item.AVPSpawnEggItemRegistry;
import org.avp.common.registry.item.AVPToolItemRegistry;
import org.avp.common.registry.item.AVPWeaponBlueprintItemRegistry;
import org.avp.common.registry.item.AVPWeaponItemRegistry;
import org.avp.common.registry.item.AVPWeaponPartItemRegistry;

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
        AVPAmmunitionPartItemRegistry.INSTANCE.register();
        AVPArmorItemRegistry.INSTANCE.register();
        AVPBulletItemRegistry.INSTANCE.register();
        AVPElectronicItemRegistry.INSTANCE.register();
        AVPFoodItemRegistry.INSTANCE.register();
        AVPItemRegistry.INSTANCE.register();
        AVPToolItemRegistry.INSTANCE.register();
        AVPWeaponBlueprintItemRegistry.INSTANCE.register();
        AVPWeaponItemRegistry.INSTANCE.register();
        AVPWeaponPartItemRegistry.INSTANCE.register();

        // Blocks
        AVPBlockDataRegistry.INSTANCE.register();

        AVPAlienBlockRegistry.INSTANCE.register();
        AVPIndustrialBlockRegistry.INSTANCE.register();
        AVPIndustrialGlassBlockRegistry.INSTANCE.register();
        AVPMachineBlockRegistry.INSTANCE.register();
        AVPParadiseBlockRegistry.INSTANCE.register();
        AVPUnidentifiedBlockRegistry.INSTANCE.register();

        // Item Blocks
        AVPItemBlockItemRegistry.INSTANCE.register();

        // Block Entity Data
        // Must be registered first, sets up block entity type holders.
        AVPBlockEntityDataRegistry.INSTANCE.register();

        // Block Entity Types
        AVPSimpleDeferredBlockEntityTypeRegistry.INSTANCE.register();

        // Sounds
        AVPSoundEventRegistry.INSTANCE.register();

        // Entity Data
        // Must be registered first, sets up entity type holders.
        AVPEntityDataRegistry.INSTANCE.register();

        // Entity Types
        AVPSimpleDeferredEntityTypeRegistry.INSTANCE.register();

        // Spawn Egg Items
        AVPSpawnEggItemRegistry.INSTANCE.register();

        // Particles
        AVPParticleTypeRegistry.INSTANCE.register();

        // It's important to register creative mode tabs last, as entities generate spawn eggs automatically.
        AVPCreativeModeTabRegistry.INSTANCE.register();
    }

    private AVPCommon() {
        throw new UnsupportedOperationException();
    }
}
