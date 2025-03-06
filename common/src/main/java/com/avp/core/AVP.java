package com.avp.core;

import com.avp.core.common.armor.ArmorMaterials;
import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.block.AVPCompostingChanceRegistry;
import com.avp.core.common.block.DecoratedPotPatterns;
import com.avp.core.common.block.DispenserBlockBehaviors;
import com.avp.core.common.block.FlammableBlockRegistry;
import com.avp.core.common.block.entity.BlockEntityTypes;
import com.avp.core.common.block_item.AVPBlockItems;
import com.avp.core.common.command.AVPCommands;
import com.avp.core.common.component.DataComponents;
import com.avp.core.common.config.Config;
import com.avp.core.common.config.ConfigContainer;
import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.config.Configs;
import com.avp.core.common.config.io.ConfigLoader;
import com.avp.core.common.config.io.ConfigSaver;
import com.avp.core.common.config.template.ConfigTemplate;
import com.avp.core.common.config.template.ConfigTemplates;
import com.avp.core.common.creative_mode_tab.CreativeModeTabs;
import com.avp.core.common.entity.spawn.SpawnPlacements;
import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.core.common.fuel.AVPFuelRegistry;
import com.avp.core.common.item.AVPItems;
import com.avp.core.common.item.ArmorItems;
import com.avp.core.common.item.SpawnEggItems;
import com.avp.core.common.level.gameevent.AVPGameEvents;
import com.avp.core.common.lifecycle.Infections;
import com.avp.core.common.lifecycle.Lifecycles;
import com.avp.core.common.menu.MenuTypes;
import com.avp.core.common.network.CommonPacketRegistry;
import com.avp.core.common.particle.AVPParticleTypes;
import com.avp.core.common.sound.AVPSoundEvents;
import com.avp.core.common.worldgen.WorldGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AVP {

    public static final String MOD_ID = "avp";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Map<String, ConfigContainer> CONFIGS = new HashMap<>();

    public static final Config HIVES_CONFIG;

    public static final Config SPAWNING_CONFIG;

    public static final Config STATS_CONFIG;

    public static final Config WEAPONS_CONFIG;

    static {
        HIVES_CONFIG = loadConfig(Configs.HIVES, ConfigTemplates.HIVES);
        SPAWNING_CONFIG = loadConfig(Configs.SPAWNING, ConfigTemplates.SPAWNING);
        STATS_CONFIG = loadConfig(Configs.STATS, ConfigTemplates.STATS);
        WEAPONS_CONFIG = loadConfig(Configs.WEAPONS, ConfigTemplates.WEAPONS);
    }

    public static void initialize() {
        ConfigProperties.initialize();

        // Core
        AVPSoundEvents.initialize();
        BlockEntityTypes.initialize();
        AVPBlocks.initialize();
        AVPItems.initialize();
        ArmorMaterials.initialize();
        AVPBlockItems.initialize();
        ArmorItems.initialize();
        SpawnEggItems.initialize();
        AVPEntityTypes.initialize();
//        Infections.initialize();
//        Lifecycles.initialize();
        AVPParticleTypes.initialize();
        MenuTypes.initialize();
        DataComponents.initialize();
        DecoratedPotPatterns.initialize();
        WorldGen.initialize();
        AVPGameEvents.initialize();
        CommonPacketRegistry.initialize();

        // Creative Tabs
        CreativeModeTabs.initialize();

        // Functionality
        AVPCompostingChanceRegistry.initialize();
        DispenserBlockBehaviors.initialize();
        SpawnPlacements.initialize();
        FlammableBlockRegistry.initialize();
        AVPFuelRegistry.initialize();
        AVPCommands.initialize();
    }

    private static Config loadConfig(Config base, ConfigTemplate template) {
        var name = base.name();
        var loadedConfig = ConfigLoader.load(name)
            .orElse(Config.empty(name));
        var mergedConfig = base.merge(loadedConfig);

        ConfigSaver.save(mergedConfig, template);

        CONFIGS.put(mergedConfig.name(), new ConfigContainer(base, mergedConfig, template));

        return mergedConfig;
    }
}
