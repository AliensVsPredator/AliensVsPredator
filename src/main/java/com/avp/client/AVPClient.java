package com.avp.client;

import com.avp.client.particle.IrradiatedAcidParticleProvider;
import com.avp.client.render.block.DeskTerminalRenderer;
import com.avp.client.render.block.ResonatorRenderer;
import com.avp.client.render.block.TripMineRenderer;
import com.avp.common.block_item.AVPBlockItems;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;

import java.util.function.Function;
import java.util.function.Supplier;

import com.avp.client.input.keybind.AVPKeybindingRegistry;
import com.avp.client.network.AVPClientPacketHandlerRegistry;
import com.avp.client.particle.AcidParticleProvider;
import com.avp.client.particle.BlueAcidParticleProvider;
import com.avp.client.render.armor.*;
import com.avp.client.render.block.SentryTurretRenderer;
import com.avp.client.render.entity.*;
import com.avp.client.render.entity.parasite.facehugger.FacehuggerRenderer;
import com.avp.client.render.item.*;
import com.avp.client.screen.ArmorCaseScreen;
import com.avp.client.screen.IndustrialFurnaceScreen;
import com.avp.common.block.AVPBlocks;
import com.avp.common.block.entity.BlockEntityTypes;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.item.AVPItems;
import com.avp.common.item.ArmorItems;
import com.avp.common.menu.MenuTypes;
import com.avp.common.particle.AVPParticleTypes;

public class AVPClient implements ClientModInitializer {

    private static final Function<String, Supplier<AzItemRenderer>> ITEM_RENDERER_SUPPLIER_FACTORY = name -> () -> new SimpleItemRenderer(
        name
    );

    @Override
    public void onInitializeClient() {
        // Blocks
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.FERROALUMINUM_CHAIN_FENCE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.FERROALUMINUM_GRATE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.IRRADIATED_RESIN_VEIN, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.IRRADIATED_RESIN_WEB, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.ABERRANT_RESIN_VEIN, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.ABERRANT_RESIN_WEB, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.NETHER_RESIN_VEIN, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.NETHER_RESIN_WEB, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.RESIN_VEIN, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.RESIN_WEB, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.STEEL_BARS, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.STEEL_CHAIN_FENCE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.STEEL_GRATE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.TITANIUM_CHAIN_FENCE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.TITANIUM_GRATE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.RAZOR_WIRE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.FERROALUMINUM_DOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.STEEL_DOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.TITANIUM_DOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.FERROALUMINUM_TRAP_DOOR, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.ROYAL_JELLY_BLOCK, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.TRINITITE_BLOCK, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.FERROALUMINUM_GRATE_SLAB, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.FERROALUMINUM_GRATE_STAIRS, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.STEEL_GRATE_SLAB, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.STEEL_GRATE_STAIRS, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.TITANIUM_GRATE_SLAB, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.TITANIUM_GRATE_STAIRS, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.INDUSTRIAL_GLASS, RenderType.cutout());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values()
            .forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent()));
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.INDUSTRIAL_GLASS_PANE, RenderType.cutout());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values()
            .forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent()));

        // Armors
        AzArmorRendererRegistry.register(
            ChitinArmorRenderer::new,
            ArmorItems.CHITIN_HELMET,
            ArmorItems.CHITIN_CHESTPLATE,
            ArmorItems.CHITIN_LEGGINGS,
            ArmorItems.CHITIN_BOOTS
        );
        AzArmorRendererRegistry.register(
            JunglePredatorArmorRenderer::new,
            ArmorItems.JUNGLE_PREDATOR_HELMET,
            ArmorItems.JUNGLE_PREDATOR_CHESTPLATE,
            ArmorItems.JUNGLE_PREDATOR_LEGGINGS,
            ArmorItems.JUNGLE_PREDATOR_BOOTS
        );
        AzArmorRendererRegistry.register(
            MK50ArmorRenderer::new,
            ArmorItems.MK50_HELMET,
            ArmorItems.MK50_CHESTPLATE,
            ArmorItems.MK50_LEGGINGS,
            ArmorItems.MK50_BOOTS
        );
        AzArmorRendererRegistry.register(
            NetherChitinArmorRenderer::new,
            ArmorItems.NETHER_CHITIN_HELMET,
            ArmorItems.NETHER_CHITIN_CHESTPLATE,
            ArmorItems.NETHER_CHITIN_LEGGINGS,
            ArmorItems.NETHER_CHITIN_BOOTS
        );
        AzArmorRendererRegistry.register(
            PlatedChitinArmorRenderer::new,
            ArmorItems.PLATED_CHITIN_HELMET,
            ArmorItems.PLATED_CHITIN_CHESTPLATE,
            ArmorItems.PLATED_CHITIN_LEGGINGS,
            ArmorItems.PLATED_CHITIN_BOOTS
        );
        AzArmorRendererRegistry.register(
            PlatedNetherChitinArmorRenderer::new,
            ArmorItems.PLATED_NETHER_CHITIN_HELMET,
            ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE,
            ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS,
            ArmorItems.PLATED_NETHER_CHITIN_BOOTS
        );
        AzArmorRendererRegistry.register(
            PressureArmorRenderer::new,
            ArmorItems.PRESSURE_HELMET,
            ArmorItems.PRESSURE_CHESTPLATE,
            ArmorItems.PRESSURE_LEGGINGS,
            ArmorItems.PRESSURE_BOOTS
        );
        AzArmorRendererRegistry.register(
            TacticalArmorRenderer::new,
            ArmorItems.TACTICAL_HELMET,
            ArmorItems.TACTICAL_CHESTPLATE,
            ArmorItems.TACTICAL_LEGGINGS,
            ArmorItems.TACTICAL_BOOTS
        );
        AzArmorRendererRegistry.register(
            TacticalCamoArmorRenderer::new,
            ArmorItems.TACTICAL_CAMO_HELMET,
            ArmorItems.TACTICAL_CAMO_CHESTPLATE,
            ArmorItems.TACTICAL_CAMO_LEGGINGS,
            ArmorItems.TACTICAL_CAMO_BOOTS
        );

        // Items
        registerItemRenderer(AVPItems.ARMOR_CASE);
        registerItemRenderer(AVPItems.F903WE_RIFLE, name -> () -> new F903weItemRenderer(name));
        registerItemRenderer(AVPItems.FLAMETHROWER_SEVASTOPOL, name -> () -> new FlameThrowerItemRenderer(name));
        registerItemRenderer(AVPItems.M37_12_SHOTGUN, name -> () -> new M3712ShotgunItemRenderer(name));
        registerItemRenderer(AVPItems.M41A_PULSE_RIFLE, name -> () -> new M41APulseRifleItemRenderer(name));
        registerItemRenderer(AVPItems.M42A3_SNIPER_RIFLE, name -> () -> new M42a3SniperRifleItemRenderer(name));
        registerItemRenderer(AVPItems.M4RA_BATTLE_RIFLE, name -> () -> new M4raBattleRifileItemRenderer(name));
        registerItemRenderer(AVPItems.M56_SMARTGUN, name -> () -> new M56SmartgunItemRenderer(name));
        registerItemRenderer(AVPItems.M6B_ROCKET_LAUNCHER, name -> () -> new M6BRLItemRenderer(name));
        registerItemRenderer(AVPItems.M88_MOD_4_COMBAT_PISTOL, name -> () -> new M88Mod4CombatPistolItemRenderer(name));
        registerItemRenderer(AVPItems.OLD_PAINLESS, name -> () -> new OldPainlessItemRenderer(name));
        registerItemRenderer(AVPItems.ZX_76_SHOTGUN, name -> () -> new ZX76ShotgunItemRenderer(name));
        registerItemRenderer(AVPBlockItems.DESK_TERMINAL_BLOCK, name -> DeskTerminalItemRenderer::new);
        registerItemRenderer(AVPBlockItems.TRIP_MINE_BLOCK, name -> TripMineItemRenderer::new);
        registerItemRenderer(AVPBlockItems.RESONATOR_BLOCK, name -> ResonatorItemRenderer::new);
        registerItemRenderer(AVPBlockItems.SENTRY_TURRET, name -> SentryItemtemRenderer::new);
        ColorProviderRegistry.ITEM.register(
            (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1),
            ArmorItems.MK50_HELMET
        );
        ColorProviderRegistry.ITEM.register(
            (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1),
            ArmorItems.MK50_CHESTPLATE
        );
        ColorProviderRegistry.ITEM.register(
            (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1),
            ArmorItems.MK50_LEGGINGS
        );
        ColorProviderRegistry.ITEM.register(
            (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1),
            ArmorItems.MK50_BOOTS
        );

        // Entities
        EntityRendererRegistry.register(AVPEntityTypes.ACID, AcidRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.CHESTBURSTER, ChestbursterRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.DRONE, DroneRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.FACEHUGGER, FacehuggerRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.FLAMETHROW, FlamethrowRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.GRENADE_THROWN, ThrownItemRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.OVAMORPH, OvamorphRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.PRAETORIAN, PraetorianRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.QUEEN, QueenRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ROCKET, RocketRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.WARRIOR, WarriorRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.YAUTJA, YautjaRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.MARINE, MarineRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.NUKE_BE, NukeRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.BULLET, EmptyRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.SHURIKEN, SpinningItemRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.SMART_DISC, SpinningItemRenderer::new);

        // Block Entities
        BlockEntityRenderers.register(
            BlockEntityTypes.SENTRY_TURRET_BE,
            (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new SentryTurretRenderer()
        );
        BlockEntityRenderers.register(BlockEntityTypes.LEAD_CHEST_BE, ChestRenderer::new);
        BlockEntityRenderers.register(BlockEntityTypes.AMMO_CHEST_BE, ChestRenderer::new);
        BlockEntityRenderers.register(
                BlockEntityTypes.DESK_TERMINAL_BE,
                (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new DeskTerminalRenderer()
        );
        BlockEntityRenderers.register(
                BlockEntityTypes.RESONATOR_BE,
                (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new ResonatorRenderer()
        );
        BlockEntityRenderers.register(
                BlockEntityTypes.TRIP_MINE_BE,
                (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new TripMineRenderer()
        );

        // Particles
        ParticleFactoryRegistry.getInstance().register(AVPParticleTypes.ACID, AcidParticleProvider::new);
        ParticleFactoryRegistry.getInstance().register(AVPParticleTypes.BLUE_ACID, BlueAcidParticleProvider::new);
        ParticleFactoryRegistry.getInstance().register(AVPParticleTypes.IRRADIATED_ACID, IrradiatedAcidParticleProvider::new);

        // GUI (aka Screens)
        MenuScreens.register(MenuTypes.ARMOR_CASE, ArmorCaseScreen::new);
        MenuScreens.register(MenuTypes.INDUSTRIAL_FURNACE_MENU, IndustrialFurnaceScreen::new);

        // Keybindings
        AVPKeybindingRegistry.initialize();

        // Networking
        AVPClientPacketHandlerRegistry.initialize();
    }

    private void registerItemRenderer(Item item) {
        registerItemRenderer(item, ITEM_RENDERER_SUPPLIER_FACTORY);
    }

    private void registerItemRenderer(Item item, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        var path = BuiltInRegistries.ITEM.getKey(item).getPath();
        var itemRendererSupplier = rendererFactory.apply(path);
        AzItemRendererRegistry.register(itemRendererSupplier, item);
    }
}
