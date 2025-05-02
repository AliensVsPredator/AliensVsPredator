package com.avp.fabric.client;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;

import java.util.function.Function;
import java.util.function.Supplier;

import com.avp.client.AVPClient;
import com.avp.common.block.TempAVPBlocks;
import com.avp.common.item.AVPArmorItems;
import com.avp.common.item.TempAVPBlockItems;
import com.avp.common.menu.AVPMenuTypes;
import com.avp.fabric.client.input.keybind.AVPKeybindingRegistry;
import com.avp.fabric.client.network.AVPClientPacketHandlerRegistry;
import com.avp.fabric.client.particle.AcidParticleProvider;
import com.avp.fabric.client.particle.BlueAcidParticleProvider;
import com.avp.fabric.client.particle.IrradiatedAcidParticleProvider;
import com.avp.fabric.client.render.block.SentryTurretRenderer;
import com.avp.fabric.client.render.entity.AcidRenderer;
import com.avp.fabric.client.render.entity.ChestbursterRenderer;
import com.avp.fabric.client.render.entity.DroneRenderer;
import com.avp.fabric.client.render.entity.EmptyRenderer;
import com.avp.fabric.client.render.entity.FlamethrowRenderer;
import com.avp.fabric.client.render.entity.MarineRenderer;
import com.avp.fabric.client.render.entity.MushroomCloudRenderer;
import com.avp.fabric.client.render.entity.NukeRenderer;
import com.avp.fabric.client.render.entity.OvamorphRenderer;
import com.avp.fabric.client.render.entity.PraetorianRenderer;
import com.avp.fabric.client.render.entity.QueenRenderer;
import com.avp.fabric.client.render.entity.RocketRenderer;
import com.avp.fabric.client.render.entity.WarriorRenderer;
import com.avp.fabric.client.render.entity.YautjaRenderer;
import com.avp.fabric.client.render.entity.parasite.facehugger.FacehuggerRenderer;
import com.avp.fabric.client.render.item.F903weItemRenderer;
import com.avp.fabric.client.render.item.FlameThrowerItemRenderer;
import com.avp.fabric.client.render.item.M3712ShotgunItemRenderer;
import com.avp.fabric.client.render.item.M41APulseRifleItemRenderer;
import com.avp.fabric.client.render.item.M42a3SniperRifleItemRenderer;
import com.avp.fabric.client.render.item.M4raBattleRifileItemRenderer;
import com.avp.fabric.client.render.item.M56SmartgunItemRenderer;
import com.avp.fabric.client.render.item.M6BRLItemRenderer;
import com.avp.fabric.client.render.item.M88Mod4CombatPistolItemRenderer;
import com.avp.fabric.client.render.item.OldPainlessItemRenderer;
import com.avp.fabric.client.render.item.SentryItemtemRenderer;
import com.avp.fabric.client.render.item.SimpleItemRenderer;
import com.avp.fabric.client.render.item.SpinningItemRenderer;
import com.avp.fabric.client.render.item.ZX76ShotgunItemRenderer;
import com.avp.fabric.client.screen.ArmorCaseScreen;
import com.avp.fabric.client.screen.IndustrialFurnaceScreen;
import com.avp.fabric.common.block.AVPBlocks;
import com.avp.fabric.common.entity.type.AVPEntityTypes;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.common.particle.AVPParticleTypes;

public class AVPFabricClient implements ClientModInitializer {

    private static final Function<String, Supplier<AzItemRenderer>> ITEM_RENDERER_SUPPLIER_FACTORY = name -> () -> new SimpleItemRenderer(
        name
    );

    @Override
    public void onInitializeClient() {
        AVPClient.initialize();

        // Blocks
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.FERROALUMINUM_GRATE.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.IRRADIATED_RESIN_VEIN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.IRRADIATED_RESIN_WEB.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.ABERRANT_RESIN_VEIN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.ABERRANT_RESIN_WEB.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.NETHER_RESIN_VEIN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.NETHER_RESIN_WEB.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.RESIN_VEIN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.RESIN_WEB.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.STEEL_BARS.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.STEEL_CHAIN_FENCE.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.STEEL_GRATE.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.TITANIUM_CHAIN_FENCE.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.TITANIUM_GRATE.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(AVPBlocks.RAZOR_WIRE, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.INDUSTRIAL_GLASS_DOOR.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.FERROALUMINUM_DOOR.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.STEEL_DOOR.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.TITANIUM_DOOR.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.FERROALUMINUM_TRAP_DOOR.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.ROYAL_JELLY_BLOCK.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.TRINITITE_BLOCK.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.FERROALUMINUM_GRATE_SLAB.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.FERROALUMINUM_GRATE_STAIRS.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.STEEL_GRATE_SLAB.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.STEEL_GRATE_STAIRS.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.TITANIUM_GRATE_SLAB.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.TITANIUM_GRATE_STAIRS.get(), RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.INDUSTRIAL_GLASS.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.INDUSTRIAL_GLASS_SLAB.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.INDUSTRIAL_GLASS_STAIRS.get(), RenderType.cutout());
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values()
            .forEach(blockSupplier -> BlockRenderLayerMap.INSTANCE.putBlock(blockSupplier.get(), RenderType.translucent()));
        BlockRenderLayerMap.INSTANCE.putBlock(TempAVPBlocks.INDUSTRIAL_GLASS_PANE.get(), RenderType.cutout());
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values()
            .forEach(blockSupplier -> BlockRenderLayerMap.INSTANCE.putBlock(blockSupplier.get(), RenderType.translucent()));

        // Armors

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
        registerItemRenderer(AVPItems.M88MOD4_COMBAT_PISTOL, name -> () -> new M88Mod4CombatPistolItemRenderer(name));
        registerItemRenderer(AVPItems.OLD_PAINLESS, name -> () -> new OldPainlessItemRenderer(name));
        registerItemRenderer(AVPItems.ZX_76_SHOTGUN, name -> () -> new ZX76ShotgunItemRenderer(name));
        registerItemRenderer(TempAVPBlockItems.SENTRY_TURRET.get(), name -> SentryItemtemRenderer::new);
        ColorProviderRegistry.ITEM.register(
            (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1),
            AVPArmorItems.MK50_HELMET.get()
        );
        ColorProviderRegistry.ITEM.register(
            (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1),
            AVPArmorItems.MK50_CHESTPLATE.get()
        );
        ColorProviderRegistry.ITEM.register(
            (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1),
            AVPArmorItems.MK50_LEGGINGS.get()
        );
        ColorProviderRegistry.ITEM.register(
            (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1),
            AVPArmorItems.MK50_BOOTS.get()
        );

        // Entities
        EntityRendererRegistry.register(AVPEntityTypes.ACID, AcidRenderer::new);

        EntityRendererRegistry.register(AVPEntityTypes.CHESTBURSTER, ChestbursterRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.DRONE, DroneRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.FACEHUGGER, FacehuggerRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.OVAMORPH, OvamorphRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.PRAETORIAN, PraetorianRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.QUEEN, QueenRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.WARRIOR, WarriorRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ROYAL_CHESTBURSTER, ChestbursterRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ROYAL_FACEHUGGER, FacehuggerRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ROYAL_OVAMORPH, OvamorphRenderer::new);

        EntityRendererRegistry.register(AVPEntityTypes.ABERRANT_CHESTBURSTER, ChestbursterRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ABERRANT_DRONE, DroneRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ABERRANT_FACEHUGGER, FacehuggerRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ABERRANT_OVAMORPH, OvamorphRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ABERRANT_PRAETORIAN, PraetorianRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ABERRANT_QUEEN, QueenRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ABERRANT_WARRIOR, WarriorRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER, ChestbursterRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER, FacehuggerRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ROYAL_ABERRANT_OVAMORPH, OvamorphRenderer::new);

        EntityRendererRegistry.register(AVPEntityTypes.IRRADIATED_DRONE, DroneRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.IRRADIATED_PRAETORIAN, PraetorianRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.IRRADIATED_QUEEN, QueenRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.IRRADIATED_WARRIOR, WarriorRenderer::new);

        EntityRendererRegistry.register(AVPEntityTypes.NETHER_CHESTBURSTER, ChestbursterRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.NETHER_DRONE, DroneRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.NETHER_FACEHUGGER, FacehuggerRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.NETHER_OVAMORPH, OvamorphRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.NETHER_PRAETORIAN, PraetorianRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.NETHER_QUEEN, QueenRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.NETHER_WARRIOR, WarriorRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER, ChestbursterRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ROYAL_NETHER_FACEHUGGER, FacehuggerRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ROYAL_NETHER_OVAMORPH, OvamorphRenderer::new);

        EntityRendererRegistry.register(AVPEntityTypes.YAUTJA, YautjaRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.MARINE, MarineRenderer::new);

        // Block-like entities (like primed TNT)
        EntityRendererRegistry.register(AVPEntityTypes.NUKE, NukeRenderer::new);

        EntityRendererRegistry.register(AVPEntityTypes.BULLET, EmptyRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.FLAMETHROW, FlamethrowRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.GRENADE_THROWN, ThrownItemRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.MUSHROOM_CLOUD, MushroomCloudRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.ROCKET, RocketRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.SHURIKEN, SpinningItemRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.SMART_DISC, SpinningItemRenderer::new);
        EntityRendererRegistry.register(AVPEntityTypes.SENTRY_TURRET, SentryTurretRenderer::new);

        // Block Entities

        // Particles
        ParticleFactoryRegistry.getInstance().register(AVPParticleTypes.ACID, AcidParticleProvider::new);
        ParticleFactoryRegistry.getInstance().register(AVPParticleTypes.BLUE_ACID, BlueAcidParticleProvider::new);
        ParticleFactoryRegistry.getInstance().register(AVPParticleTypes.IRRADIATED_ACID, IrradiatedAcidParticleProvider::new);

        // GUI (aka Screens)
        MenuScreens.register(AVPMenuTypes.ARMOR_CASE.get(), ArmorCaseScreen::new);
        MenuScreens.register(AVPMenuTypes.INDUSTRIAL_FURNACE_MENU.get(), IndustrialFurnaceScreen::new);

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
