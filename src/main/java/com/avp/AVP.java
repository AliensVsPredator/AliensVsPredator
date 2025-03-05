package com.avp;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block.CompostingChanceRegistry;
import com.avp.common.block.DecoratedPotPatterns;
import com.avp.common.block.DispenserBlockBehaviors;
import com.avp.common.block.FlammableBlockRegistry;
import com.avp.common.block.entity.BlockEntityTypes;
import com.avp.common.block_item.AVPBlockItems;
import com.avp.common.command.Commands;
import com.avp.common.component.DataComponents;
import com.avp.common.config.Config;
import com.avp.common.config.ConfigContainer;
import com.avp.common.config.ConfigProperties;
import com.avp.common.config.Configs;
import com.avp.common.config.io.ConfigLoader;
import com.avp.common.config.io.ConfigSaver;
import com.avp.common.config.template.ConfigTemplate;
import com.avp.common.config.template.ConfigTemplates;
import com.avp.common.creative_mode_tab.initializer.BlocksCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.ColoredBlocksCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.CombatCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.IngredientsCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.SpawnEggsCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.ToolsAndUtilitiesCreativeModeTabInitializer;
import com.avp.common.entity.spawn.SpawnPlacements;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.fuel.AVPFuelRegistry;
import com.avp.common.item.AVPItems;
import com.avp.common.item.ArmorItems;
import com.avp.common.item.SpawnEggItems;
import com.avp.common.level.gameevent.AVPGameEvents;
import com.avp.common.lifecycle.Infections;
import com.avp.common.lifecycle.Lifecycles;
import com.avp.common.menu.MenuTypes;
import com.avp.common.network.CommonPacketRegistry;
import com.avp.common.network.ServerPacketHandlerRegistry;
import com.avp.common.particle.AVPParticleTypes;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.common.worldgen.WorldGen;
import com.avp.data.loot.LootTableModifier;

public class AVP implements ModInitializer {

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

    @Override
    public void onInitialize() {
        ConfigProperties.initialize();

        // Core
        BlockEntityTypes.initialize();
        AVPBlocks.initialize();
        AVPItems.initialize();
        AVPBlockItems.initialize();
        ArmorItems.initialize();
        SpawnEggItems.initialize();
        AVPEntityTypes.initialize();
        Infections.initialize();
        Lifecycles.initialize();
        AVPParticleTypes.initialize();
        MenuTypes.initialize();
        DataComponents.initialize();
        DecoratedPotPatterns.initialize();
        WorldGen.initialize();
        AVPSoundEvents.initialize();
        AVPGameEvents.initialize();
        CommonPacketRegistry.initialize();
        ServerPacketHandlerRegistry.initialize();

        // Creative Tabs
        BlocksCreativeModeTabInitializer.initialize();
        ColoredBlocksCreativeModeTabInitializer.initialize();
        CombatCreativeModeTabInitializer.initialize();
        IngredientsCreativeModeTabInitializer.initialize();
        SpawnEggsCreativeModeTabInitializer.initialize();
        ToolsAndUtilitiesCreativeModeTabInitializer.initialize();

        // Functionality
        CompostingChanceRegistry.initialize();
        DispenserBlockBehaviors.initialize();
        LootTableModifier.initialize();
        SpawnPlacements.initialize();
        FlammableBlockRegistry.initialize();
        AVPFuelRegistry.initialize();
        Commands.initialize();
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
