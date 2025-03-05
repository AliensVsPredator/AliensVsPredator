package com.avp.client;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.Supplier;

import com.avp.client.input.keybind.AVPKeybindingRegistry;
import com.avp.client.network.AVPClientPacketHandlerRegistry;
import com.avp.client.particle.AcidParticleProvider;
import com.avp.client.particle.BlueAcidParticleProvider;
import com.avp.client.render.armor.ChitinArmorRenderer;
import com.avp.client.render.armor.JunglePredatorArmorRenderer;
import com.avp.client.render.armor.MK50ArmorRenderer;
import com.avp.client.render.armor.NetherChitinArmorRenderer;
import com.avp.client.render.armor.PlatedChitinArmorRenderer;
import com.avp.client.render.armor.PlatedNetherChitinArmorRenderer;
import com.avp.client.render.armor.PressureArmorRenderer;
import com.avp.client.render.armor.TacticalArmorRenderer;
import com.avp.client.render.entity.AcidRenderer;
import com.avp.client.render.entity.ChestbursterRenderer;
import com.avp.client.render.entity.DroneRenderer;
import com.avp.client.render.entity.FlamethrowRenderer;
import com.avp.client.render.entity.OvamorphRenderer;
import com.avp.client.render.entity.PraetorianRenderer;
import com.avp.client.render.entity.QueenRenderer;
import com.avp.client.render.entity.RocketRenderer;
import com.avp.client.render.entity.WarriorRenderer;
import com.avp.client.render.entity.YautjaRenderer;
import com.avp.client.render.entity.parasite.facehugger.FacehuggerRenderer;
import com.avp.client.render.item.OldPainlessItemRenderer;
import com.avp.client.render.item.SimpleItemRenderer;
import com.avp.client.screen.ArmorCaseScreen;
import com.avp.common.block.AVPBlocks;
import com.avp.common.config.Config;
import com.avp.common.config.io.ConfigLoader;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.item.AVPItems;
import com.avp.common.item.ArmorItems;
import com.avp.common.menu.MenuTypes;
import com.avp.common.particle.AVPParticleTypes;

public class AVPClient implements ClientModInitializer {

    public static Config CLIENT_CONFIG;

    private static final Function<String, Supplier<AzItemRenderer>> ITEM_RENDERER_SUPPLIER_FACTORY = name -> () -> new SimpleItemRenderer(
        name
    );

    @Override
    public void onInitializeClient() {
        var commonConfigName = "client";
        CLIENT_CONFIG = ConfigLoader.load(commonConfigName).orElse(Config.empty(commonConfigName));

        // Blocks
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.FERROALUMINUM_CHAIN_FENCE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.FERROALUMINUM_GRATE, RenderType.cutout());
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

        // Items
        registerItemRenderer(AVPItems.ARMOR_CASE);
        registerItemRenderer(AVPItems.F903WE_RIFLE);
        registerItemRenderer(AVPItems.FLAMETHROWER_SEVASTOPOL);
        registerItemRenderer(AVPItems.M37_12_SHOTGUN);
        registerItemRenderer(AVPItems.M41A_PULSE_RIFLE);
        registerItemRenderer(AVPItems.M42A3_SNIPER_RIFLE);
        registerItemRenderer(AVPItems.M4RA_BATTLE_RIFLE);
        registerItemRenderer(AVPItems.M56_SMARTGUN);
        registerItemRenderer(AVPItems.M6B_ROCKET_LAUNCHER);
        registerItemRenderer(AVPItems.M88_MOD_4_COMBAT_PISTOL);
        registerItemRenderer(AVPItems.OLD_PAINLESS, name -> () -> new OldPainlessItemRenderer(name));
        registerItemRenderer(AVPItems.ZX_76_SHOTGUN);

        // Entities
        EntityRendererRegistry.register(AVPEntityTypes.ACID, AcidRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.CHESTBURSTER, ChestbursterRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.DRONE, DroneRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.FACEHUGGER, FacehuggerRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.FLAMETHROW, FlamethrowRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.OVAMORPH, OvamorphRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.PRAETORIAN, PraetorianRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.QUEEN, QueenRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ROCKET, RocketRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.WARRIOR, WarriorRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.YAUTJA, YautjaRenderer::new);

        // Particles
        ParticleFactoryRegistry.getInstance().register(AVPParticleTypes.ACID, AcidParticleProvider::new);
        ParticleFactoryRegistry.getInstance().register(AVPParticleTypes.BLUE_ACID, BlueAcidParticleProvider::new);

        // GUI (aka Screens)
        MenuScreens.register(MenuTypes.ARMOR_CASE, ArmorCaseScreen::new);

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
