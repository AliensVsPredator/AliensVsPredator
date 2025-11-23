package com.avp;

import com.alien.Alien;
import com.human.Human;
import com.human.common.gameplay.level.patrol.MarinePatrolSpawner;
import com.lib.common.gameplay.gene.Genes;
import mod.azure.azurelib.common.config.Config;
import mod.azure.azurelib.common.config.ConfigHolder;
import mod.azure.azurelib.common.config.ConfigHolderRegistry;
import mod.azure.azurelib.common.config.format.ConfigFormats;
import mod.azure.azurelib.common.config.format.IConfigFormatHandler;
import mod.azure.azurelib.common.config.io.ConfigIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avp.common.config.AVPConfig;
import com.avp.common.data.AVPReloadListeners;
import com.avp.common.data.fixer.migration.AVPDataMigrations;
import com.avp.common.gameplay.worldgen.biome.NukedAshPlacement;
import com.avp.common.network.AVPPacketDirectionRegistry;
import com.avp.common.network.AVPServerPacketHandlerRegistry;
import com.avp.common.registry.init.AVPArmorMaterials;
import com.avp.common.registry.init.AVPBlockEntityTypes;
import com.avp.common.registry.init.AVPCommands;
import com.avp.common.registry.init.AVPCompostingChances;
import com.avp.common.registry.init.AVPDataKeys;
import com.avp.common.registry.init.AVPDecoratedPotPatterns;
import com.avp.common.registry.init.AVPEntitySpawns;
import com.avp.common.registry.init.AVPFuels;
import com.avp.common.registry.init.AVPMobEffects;
import com.avp.common.registry.init.AVPRecipes;
import com.avp.common.registry.init.AVPSoundEvents;
import com.avp.common.registry.init.AVPVillagerPoiTypes;
import com.avp.common.registry.init.AVPVillagerProfessions;
import com.avp.common.registry.init.AVPVillagerTrades;
import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.block.CoreBlocks;
import com.avp.common.registry.init.creative_mode_tab.AVPCreativeModeTabs;
import com.avp.common.registry.init.entity_type.AVPEntityTypes;
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPBlockItems;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.common.registry.key.AVPBiomeKeys;
import com.avp.common.registry.key.AVPJukeboxSongKeys;
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

        Alien.initialize();
        Human.initialize();

        AVPBlockEntityTypes.initialize();
        CoreBlocks.initialize();
        AVPBlocks.initialize();
        AVPItems.initialize();
        AVPBlockItems.initialize();
        AVPArmorMaterials.initialize();
        AVPArmorItems.initialize();
        AVPCreativeModeTabs.initialize();
        AVPEntityTypes.initialize();
        AVPCommands.initialize();
        AVPDecoratedPotPatterns.initialize();
        AVPSoundEvents.initialize();
        AVPJukeboxSongKeys.initialize();
        AVPPacketDirectionRegistry.initialize();
        AVPDataKeys.initialize();
        AVPServerPacketHandlerRegistry.initialize();
        AVPRecipes.initialize();
        AVPMobEffects.initialize();
        AVPBiomeKeys.initialize();
        AVPVillagerPoiTypes.initialize();
        AVPVillagerProfessions.initialize();

        // Functionality
        AVPCompostingChances.initialize();
        AVPEntitySpawns.initialize();
        AVPFuels.initialize();
        AVPVillagerTrades.initialize();
        Genes.initialize();

        // Listeners/Events
        AVPReloadListeners.initialize();

        // Data Migration
        AVPDataMigrations.initialize();
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
