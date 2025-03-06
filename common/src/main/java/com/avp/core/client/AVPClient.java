package com.avp.core.client;

import com.avp.core.client.particle.AcidParticleProvider;
import com.avp.core.client.particle.BlueAcidParticleProvider;
import com.avp.core.client.render.armor.ChitinArmorRenderer;
import com.avp.core.client.render.armor.JunglePredatorArmorRenderer;
import com.avp.core.client.render.armor.MK50ArmorRenderer;
import com.avp.core.client.render.armor.NetherChitinArmorRenderer;
import com.avp.core.client.render.armor.PlatedChitinArmorRenderer;
import com.avp.core.client.render.armor.PlatedNetherChitinArmorRenderer;
import com.avp.core.client.render.armor.PressureArmorRenderer;
import com.avp.core.client.render.armor.TacticalArmorRenderer;
import com.avp.core.client.render.entity.AcidRenderer;
import com.avp.core.client.render.entity.ChestbursterRenderer;
import com.avp.core.client.render.entity.DroneRenderer;
import com.avp.core.client.render.entity.FlamethrowRenderer;
import com.avp.core.client.render.entity.OvamorphRenderer;
import com.avp.core.client.render.entity.PraetorianRenderer;
import com.avp.core.client.render.entity.QueenRenderer;
import com.avp.core.client.render.entity.RocketRenderer;
import com.avp.core.client.render.entity.WarriorRenderer;
import com.avp.core.client.render.entity.YautjaRenderer;
import com.avp.core.client.render.entity.parasite.facehugger.FacehuggerRenderer;
import com.avp.core.client.render.item.OldPainlessItemRenderer;
import com.avp.core.client.render.item.SimpleItemRenderer;
import com.avp.core.client.screen.ArmorCaseScreen;
import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.config.Config;
import com.avp.core.common.config.io.ConfigLoader;
import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.core.common.item.AVPItems;
import com.avp.core.common.item.ArmorItems;
import com.avp.core.common.menu.MenuTypes;
import com.avp.core.common.particle.AVPParticleTypes;
import com.avp.core.platform.service.Services;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.Supplier;

public class AVPClient {

    public static Config CLIENT_CONFIG;

    private static final Function<String, Supplier<AzItemRenderer>> ITEM_RENDERER_SUPPLIER_FACTORY = name -> () -> new SimpleItemRenderer(
        name
    );

    public static void initialize() {
        var commonConfigName = "client";
        CLIENT_CONFIG = ConfigLoader.load(commonConfigName).orElse(Config.empty(commonConfigName));

        registerArmorRenderers();
        registerItemRenderers();

        // Blocks
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.FERROALUMINUM_CHAIN_FENCE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.FERROALUMINUM_GRATE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.NETHER_RESIN_VEIN, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.NETHER_RESIN_WEB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.RESIN_VEIN, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.RESIN_WEB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.STEEL_BARS, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.STEEL_CHAIN_FENCE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.STEEL_GRATE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.TITANIUM_CHAIN_FENCE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.TITANIUM_GRATE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.RAZOR_WIRE, RenderType.cutout());

        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.INDUSTRIAL_GLASS, RenderType.cutout());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values()
            .forEach(block -> Services.CLIENT_REGISTRY.registerBlockRenderType(block, RenderType.translucent()));
        Services.CLIENT_REGISTRY.registerBlockRenderType(AVPBlocks.INDUSTRIAL_GLASS_PANE, RenderType.cutout());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values()
            .forEach(block -> Services.CLIENT_REGISTRY.registerBlockRenderType(block, RenderType.translucent()));
    }

    private static void registerArmorRenderers() {
        AzArmorRendererRegistry.register(
            ChitinArmorRenderer::new,
            ArmorItems.CHITIN_HELMET.get(),
            ArmorItems.CHITIN_CHESTPLATE.get(),
            ArmorItems.CHITIN_LEGGINGS.get(),
            ArmorItems.CHITIN_BOOTS.get()
        );
        AzArmorRendererRegistry.register(
            JunglePredatorArmorRenderer::new,
            ArmorItems.JUNGLE_PREDATOR_HELMET.get(),
            ArmorItems.JUNGLE_PREDATOR_CHESTPLATE.get(),
            ArmorItems.JUNGLE_PREDATOR_LEGGINGS.get(),
            ArmorItems.JUNGLE_PREDATOR_BOOTS.get()
        );
        AzArmorRendererRegistry.register(
            MK50ArmorRenderer::new,
            ArmorItems.MK50_HELMET.get(),
            ArmorItems.MK50_CHESTPLATE.get(),
            ArmorItems.MK50_LEGGINGS.get(),
            ArmorItems.MK50_BOOTS.get()
        );
        AzArmorRendererRegistry.register(
            NetherChitinArmorRenderer::new,
            ArmorItems.NETHER_CHITIN_HELMET.get(),
            ArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
            ArmorItems.NETHER_CHITIN_LEGGINGS.get(),
            ArmorItems.NETHER_CHITIN_BOOTS.get()
        );
        AzArmorRendererRegistry.register(
            PlatedChitinArmorRenderer::new,
            ArmorItems.PLATED_CHITIN_HELMET.get(),
            ArmorItems.PLATED_CHITIN_CHESTPLATE.get(),
            ArmorItems.PLATED_CHITIN_LEGGINGS.get(),
            ArmorItems.PLATED_CHITIN_BOOTS.get()
        );
        AzArmorRendererRegistry.register(
            PlatedNetherChitinArmorRenderer::new,
            ArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
            ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
            ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get(),
            ArmorItems.PLATED_NETHER_CHITIN_BOOTS.get()
        );
        AzArmorRendererRegistry.register(
            PressureArmorRenderer::new,
            ArmorItems.PRESSURE_HELMET.get(),
            ArmorItems.PRESSURE_CHESTPLATE.get(),
            ArmorItems.PRESSURE_LEGGINGS.get(),
            ArmorItems.PRESSURE_BOOTS.get()
        );
        AzArmorRendererRegistry.register(
            TacticalArmorRenderer::new,
            ArmorItems.TACTICAL_HELMET.get(),
            ArmorItems.TACTICAL_CHESTPLATE.get(),
            ArmorItems.TACTICAL_LEGGINGS.get(),
            ArmorItems.TACTICAL_BOOTS.get()
        );
    }

    private static void registerItemRenderers() {
        registerItemRenderer(AVPItems.ARMOR_CASE.get());
        registerItemRenderer(AVPItems.F903WE_RIFLE.get());
        registerItemRenderer(AVPItems.FLAMETHROWER_SEVASTOPOL.get());
        registerItemRenderer(AVPItems.M37_12_SHOTGUN.get());
        registerItemRenderer(AVPItems.M41A_PULSE_RIFLE.get());
        registerItemRenderer(AVPItems.M42A3_SNIPER_RIFLE.get());
        registerItemRenderer(AVPItems.M4RA_BATTLE_RIFLE.get());
        registerItemRenderer(AVPItems.M56_SMARTGUN.get());
        registerItemRenderer(AVPItems.M6B_ROCKET_LAUNCHER.get());
        registerItemRenderer(AVPItems.M88_MOD_4_COMBAT_PISTOL.get());
        registerItemRenderer(AVPItems.OLD_PAINLESS.get(), name -> () -> new OldPainlessItemRenderer(name));
        registerItemRenderer(AVPItems.ZX_76_SHOTGUN.get());
    }

    private static void registerItemRenderer(Item item) {
        registerItemRenderer(item, ITEM_RENDERER_SUPPLIER_FACTORY);
    }

    private static void registerItemRenderer(Item item, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        var path = BuiltInRegistries.ITEM.getKey(item).getPath();
        var itemRendererSupplier = rendererFactory.apply(path);
        AzItemRendererRegistry.register(itemRendererSupplier, item);
    }
}
