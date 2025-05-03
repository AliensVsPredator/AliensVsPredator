package com.avp;

import mod.azure.azurelib.common.api.common.config.Config;
import mod.azure.azurelib.common.internal.common.config.ConfigHolder;
import mod.azure.azurelib.common.internal.common.config.ConfigHolderRegistry;
import mod.azure.azurelib.common.internal.common.config.format.ConfigFormats;
import mod.azure.azurelib.common.internal.common.config.format.IConfigFormatHandler;
import mod.azure.azurelib.common.internal.common.config.io.ConfigIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block.AVPCompostingChanceRegistry;
import com.avp.common.block.AVPDecoratedPotPatterns;
import com.avp.common.block.entity.AVPBlockEntityTypes;
import com.avp.common.command.AVPCommands;
import com.avp.common.component.AVPDataComponents;
import com.avp.common.config.AVPConfig;
import com.avp.common.creative_mode_tab.AVPCreativeModeTabs;
import com.avp.common.effect.AVPMobEffects;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.fuel.AVPFuelRegistry;
import com.avp.common.item.AVPArmorItems;
import com.avp.common.item.AVPBlockItems;
import com.avp.common.item.AVPItems;
import com.avp.common.item.AVPSpawnEggItems;
import com.avp.common.level.gameevent.AVPGameEvents;
import com.avp.common.lifecycle.AVPAlienInfections;
import com.avp.common.lifecycle.AVPAlienLifecycles;
import com.avp.common.menu.AVPMenuTypes;
import com.avp.common.network.AVPPacketDirectionRegistry;
import com.avp.common.network.AVPServerPacketHandlerRegistry;
import com.avp.common.particle.AVPParticleTypes;
import com.avp.common.patrols.MarinePatrolSpawner;
import com.avp.common.profession.AVPPointOfInterests;
import com.avp.common.profession.AVPProfessions;
import com.avp.common.profession.AVPTrades;
import com.avp.common.recipe.AVPRecipes;
import com.avp.common.sound.AVPJukeboxSongs;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.common.worldgen.biome.AVPBiomes;
import com.avp.common.worldgen.biome.NukedAshPlacement;
import com.avp.service.Services;

public class AVP {

    public static final String MOD_ID = "avp";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static AVPConfig config;

    public static final MarinePatrolSpawner CUSTOM_SPAWNER = new MarinePatrolSpawner();

    public static final NukedAshPlacement NUKED_ASH_PLACEMENT = new NukedAshPlacement();

    public static void initialize() {
        AVP.config = registerConfig(AVPConfig.class, ConfigFormats.json()).getConfigInstance();

        LOGGER.info("Initializing AVP for platform '{}'", Services.PLATFORM.getPlatformName());

        AVPBlockEntityTypes.initialize();
        AVPBlocks.initialize();
        AVPItems.initialize();
        AVPBlockItems.initialize();
        AVPArmorItems.initialize();
        AVPSpawnEggItems.initialize();
        AVPEntityTypes.initialize();
        AVPAlienInfections.initialize();
        AVPAlienLifecycles.initialize();
        AVPParticleTypes.initialize();
        AVPMenuTypes.initialize();
        AVPCreativeModeTabs.initialize();
        AVPCommands.initialize();
        AVPDataComponents.initialize();
        AVPDecoratedPotPatterns.initialize();
        AVPSoundEvents.initialize();
        AVPJukeboxSongs.initialize();
        AVPGameEvents.initialize();
        AVPPacketDirectionRegistry.initialize();
        AVPServerPacketHandlerRegistry.initialize();
        AVPRecipes.initialize();
        AVPMobEffects.initialize();
        AVPBiomes.initialize();
        AVPPointOfInterests.initialize();
        AVPProfessions.initialize();

        // Functionality
        AVPCompostingChanceRegistry.initialize();
        AVPFuelRegistry.initialize();
        AVPTrades.initialize();
    }

    /**
     * Registers your config class. Config will be immediately loaded upon calling.
     *
     * @param configClass   Your config class
     * @param formatFactory File format to be used by this config class. You can use values from {@link ConfigFormats}
     *                      for example.
     * @param <C>           Config type
     * @return Config holder containing your config instance. You obtain it by calling
     *         {@link ConfigHolder#getConfigInstance()} method.
     */
    public static <C> ConfigHolder<C> registerConfig(Class<C> configClass, IConfigFormatHandler formatFactory) {
        var config = configClass.getAnnotation(Config.class);

        if (config == null) {
            throw new IllegalArgumentException("Config class must be annotated with '@Config' annotation");
        }

        var id = config.id();
        var filename = config.filename();

        if (filename.isEmpty()) {
            filename = id;
        }

        var group = config.group();

        if (group.isEmpty()) {
            group = id;
        }

        var holder = new ConfigHolder<>(configClass, id, filename, group, formatFactory);
        ConfigHolderRegistry.registerConfig(holder);

        if (configClass.getAnnotation(Config.NoAutoSync.class) == null) {
            ConfigIO.FILE_WATCH_MANAGER.addTrackedConfig(holder);
        }

        return holder;
    }
}
