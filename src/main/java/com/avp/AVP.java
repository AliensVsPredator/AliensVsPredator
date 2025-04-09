package com.avp;

import com.avp.common.profession.AVPTrades;
import mod.azure.azurelib.common.api.common.config.Config;
import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.config.ConfigHolder;
import mod.azure.azurelib.common.internal.common.config.ConfigHolderRegistry;
import mod.azure.azurelib.common.internal.common.config.format.ConfigFormats;
import mod.azure.azurelib.common.internal.common.config.format.IConfigFormatHandler;
import mod.azure.azurelib.common.internal.common.config.io.ConfigIO;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block.CompostingChanceRegistry;
import com.avp.common.block.DecoratedPotPatterns;
import com.avp.common.block.DispenserBlockBehaviors;
import com.avp.common.block.FlammableBlockRegistry;
import com.avp.common.block.entity.BlockEntityTypes;
import com.avp.common.block_item.AVPBlockItems;
import com.avp.common.command.Commands;
import com.avp.common.component.DataComponents;
import com.avp.common.config.*;
import com.avp.common.creative_mode_tab.initializer.BlocksCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.ColoredBlocksCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.CombatCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.IngredientsCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.SpawnEggsCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.ToolsAndUtilitiesCreativeModeTabInitializer;
import com.avp.common.effect.AVPEffects;
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
import com.avp.common.patrols.MarinePatrolSpawner;
import com.avp.common.profession.AVPProfessions;
import com.avp.common.recipe.AVPRecipes;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.common.worldgen.NukedAshPlacement;
import com.avp.common.worldgen.WorldGen;
import com.avp.common.worldgen.biome.AVPBiomes;
import com.avp.data.loot.LootTableModifier;

public class AVP implements ModInitializer {

    public static final String MOD_ID = "avp";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static AVPConfig config;

    private final MarinePatrolSpawner customSpawner = new MarinePatrolSpawner();

    private final NukedAshPlacement nukedAshPlacement = new NukedAshPlacement();

    @Override
    public void onInitialize() {
        AzureLib.initialize();
        config = registerConfig(AVPConfig.class, ConfigFormats.json()).getConfigInstance();

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
        AVPRecipes.initialize();
        AVPEffects.initialize();
        AVPBiomes.initialize();
        AVPProfessions.initialize();

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
        ServerTickEvents.START_WORLD_TICK.register(this::onWorldTick);
        AVPTrades.initialize();
    }

    private void onWorldTick(ServerLevel serverLevel) {
        customSpawner.tick(serverLevel, serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING), true);
        nukedAshPlacement.tick(serverLevel);
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
