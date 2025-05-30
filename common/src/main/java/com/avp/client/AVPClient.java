package com.avp.client;

import com.alien.client.AlienClient;
import com.alien.client.particle.AcidParticleProvider;
import com.alien.client.particle.BlueAcidParticleProvider;
import com.alien.client.particle.IrradiatedAcidParticleProvider;
import com.alien.client.render.entity.AcidRenderer;
import com.alien.client.render.entity.ChestbursterRenderer;
import com.alien.client.render.entity.DroneRenderer;
import com.alien.client.render.entity.OvomorphRenderer;
import com.alien.client.render.entity.PraetorianRenderer;
import com.alien.client.render.entity.QueenRenderer;
import com.alien.client.render.entity.WarriorRenderer;
import com.alien.client.render.entity.parasite.facehugger.FacehuggerRenderer;
import com.human.client.render.armor.MK50ArmorRenderer;
import com.human.client.render.armor.PressureArmorRenderer;
import com.human.client.render.armor.TacticalArmorRenderer;
import com.human.client.render.armor.TacticalCamoArmorRenderer;
import com.human.client.render.block.DeskTerminalRenderer;
import com.human.client.render.block.ResonatorRenderer;
import com.human.client.render.entity.FlamethrowRenderer;
import com.human.client.render.entity.MarineRenderer;
import com.human.client.render.entity.MushroomCloudRenderer;
import com.human.client.render.entity.NukeRenderer;
import com.human.client.render.entity.RocketRenderer;
import com.human.client.render.entity.SentryTurretRenderer;
import com.human.client.render.item.DeskTerminalItemRenderer;
import com.human.client.render.item.ResonatorItemRenderer;
import com.human.client.render.item.SentryTurretItemRenderer;
import com.human.client.render.item.gun.FlamethrowerItemRenderer;
import com.human.client.render.item.gun.muzzled.impl.F903WEItemRenderer;
import com.human.client.render.item.gun.muzzled.impl.M3712ShotgunItemRenderer;
import com.human.client.render.item.gun.muzzled.impl.M41APulseRifleItemRenderer;
import com.human.client.render.item.gun.muzzled.impl.M42A3SniperRifleItemRenderer;
import com.human.client.render.item.gun.muzzled.impl.M4RABattleRifleItemRenderer;
import com.human.client.render.item.gun.muzzled.impl.M56SmartgunItemRenderer;
import com.human.client.render.item.gun.muzzled.impl.M6BRocketLauncherItemRenderer;
import com.human.client.render.item.gun.muzzled.impl.M88Mod4CombatPistolItemRenderer;
import com.human.client.render.item.gun.muzzled.impl.OldPainlessItemRenderer;
import com.human.client.render.item.gun.muzzled.impl.ZX76ShotgunItemRenderer;
import com.human.client.screen.ArmorCaseScreen;
import com.human.client.screen.IndustrialFurnaceScreen;
import com.predator.client.render.armor.JunglePredatorArmorRenderer;
import com.predator.client.render.block.TripMineRenderer;
import com.predator.client.render.entity.YautjaRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.item.component.DyedItemColor;

import java.util.List;

import com.avp.client.input.keybind.AVPKeybindingRegistry;
import com.avp.client.render.item.SpinningItemRenderer;
import com.avp.client.render.item.TripMineItemRenderer;
import com.avp.common.registry.init.AVPBlockEntityTypes;
import com.avp.common.registry.init.AVPBlocks;
import com.avp.common.registry.init.AVPMenuTypes;
import com.avp.common.registry.init.AVPParticleTypes;
import com.avp.common.registry.init.entity_type.AVPEntityTypes;
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPBlockItems;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.service.Services;

public class AVPClient {

    public static void initialize() {
        AlienClient.initialize();

        registerArmorRenderers();
        registerBlockEntityRenderers();
        registerBlockRenderLayers();
        registerEntityRenderers();
        registerItemRenderers();
        registerMenuScreens();
        registerParticleProviderFactories();

        // Keybindings
        AVPKeybindingRegistry.initialize();
    }

    private static void registerArmorRenderers() {
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            JunglePredatorArmorRenderer::new,
            List.of(
                AVPArmorItems.JUNGLE_PREDATOR_HELMET,
                AVPArmorItems.JUNGLE_PREDATOR_CHESTPLATE,
                AVPArmorItems.JUNGLE_PREDATOR_LEGGINGS,
                AVPArmorItems.JUNGLE_PREDATOR_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            MK50ArmorRenderer::new,
            List.of(
                AVPArmorItems.MK50_HELMET,
                AVPArmorItems.MK50_CHESTPLATE,
                AVPArmorItems.MK50_LEGGINGS,
                AVPArmorItems.MK50_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PressureArmorRenderer::new,
            List.of(
                AVPArmorItems.PRESSURE_HELMET,
                AVPArmorItems.PRESSURE_CHESTPLATE,
                AVPArmorItems.PRESSURE_LEGGINGS,
                AVPArmorItems.PRESSURE_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            TacticalArmorRenderer::new,
            List.of(
                AVPArmorItems.TACTICAL_HELMET,
                AVPArmorItems.TACTICAL_CHESTPLATE,
                AVPArmorItems.TACTICAL_LEGGINGS,
                AVPArmorItems.TACTICAL_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            TacticalCamoArmorRenderer::new,
            List.of(
                AVPArmorItems.TACTICAL_CAMO_HELMET,
                AVPArmorItems.TACTICAL_CAMO_CHESTPLATE,
                AVPArmorItems.TACTICAL_CAMO_LEGGINGS,
                AVPArmorItems.TACTICAL_CAMO_BOOTS
            )
        );
    }

    private static void registerBlockEntityRenderers() {
        Services.CLIENT_REGISTRY.registerBlockEntityRenderer(AVPBlockEntityTypes.AMMO_CHEST, ChestRenderer::new);
        Services.CLIENT_REGISTRY.registerBlockEntityRenderer(
            AVPBlockEntityTypes.DESK_TERMINAL,
            (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new DeskTerminalRenderer()
        );
        Services.CLIENT_REGISTRY.registerBlockEntityRenderer(AVPBlockEntityTypes.LEAD_CHEST, ChestRenderer::new);
        Services.CLIENT_REGISTRY.registerBlockEntityRenderer(
            AVPBlockEntityTypes.RESONATOR,
            (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new ResonatorRenderer()
        );
        Services.CLIENT_REGISTRY.registerBlockEntityRenderer(
            AVPBlockEntityTypes.TRIP_MINE,
            (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new TripMineRenderer()
        );

        Services.CLIENT_REGISTRY.registerItemColor(
            (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1),
            List.of(AVPArmorItems.MK50_HELMET)
        );
        Services.CLIENT_REGISTRY.registerItemColor(
            (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1),
            List.of(AVPArmorItems.MK50_CHESTPLATE)
        );
        Services.CLIENT_REGISTRY.registerItemColor(
            (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1),
            List.of(AVPArmorItems.MK50_LEGGINGS)
        );
        Services.CLIENT_REGISTRY.registerItemColor(
            (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1),
            List.of(AVPArmorItems.MK50_BOOTS)
        );
    }

    private static void registerBlockRenderLayers() {
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.FERROALUMINUM_CHAIN_FENCE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.FERROALUMINUM_GRATE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.STEEL_BARS, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.STEEL_CHAIN_FENCE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.STEEL_GRATE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.TITANIUM_CHAIN_FENCE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.TITANIUM_GRATE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.RAZOR_WIRE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.INDUSTRIAL_GLASS_DOOR, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.FERROALUMINUM_DOOR, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.STEEL_DOOR, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.TITANIUM_DOOR, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.FERROALUMINUM_TRAP_DOOR, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.TRINITITE_BLOCK, RenderType.translucent());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.FERROALUMINUM_GRATE_SLAB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.FERROALUMINUM_GRATE_STAIRS, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.STEEL_GRATE_SLAB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.STEEL_GRATE_STAIRS, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.TITANIUM_GRATE_SLAB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.TITANIUM_GRATE_STAIRS, RenderType.cutout());

        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.INDUSTRIAL_GLASS, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.INDUSTRIAL_GLASS_SLAB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.INDUSTRIAL_GLASS_STAIRS, RenderType.cutout());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values()
            .forEach(blockSupplier -> Services.CLIENT_REGISTRY.registerBlockRenderLayer(blockSupplier, RenderType.translucent()));
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.INDUSTRIAL_GLASS_PANE, RenderType.cutout());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values()
            .forEach(blockSupplier -> Services.CLIENT_REGISTRY.registerBlockRenderLayer(blockSupplier, RenderType.translucent()));
    }

    private static void registerEntityRenderers() {
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ABERRANT_CHESTBURSTER, ChestbursterRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ABERRANT_DRONE, DroneRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ABERRANT_FACEHUGGER, FacehuggerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ABERRANT_OVOMORPH, OvomorphRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ABERRANT_PRAETORIAN, PraetorianRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ABERRANT_QUEEN, QueenRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ABERRANT_WARRIOR, WarriorRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ACID, AcidRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.CHESTBURSTER, ChestbursterRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.DRONE, DroneRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.FACEHUGGER, FacehuggerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.FLAMETHROW, FlamethrowRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.GRENADE_THROWN, ThrownItemRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.IRRADIATED_DRONE, DroneRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.IRRADIATED_PRAETORIAN, PraetorianRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.IRRADIATED_QUEEN, QueenRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.IRRADIATED_WARRIOR, WarriorRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.MARINE, MarineRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.MUSHROOM_CLOUD, MushroomCloudRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.NETHER_CHESTBURSTER, ChestbursterRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.NETHER_DRONE, DroneRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.NETHER_FACEHUGGER, FacehuggerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.NETHER_OVOMORPH, OvomorphRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.NETHER_PRAETORIAN, PraetorianRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.NETHER_QUEEN, QueenRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.NETHER_WARRIOR, WarriorRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.NUKE, NukeRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.OVOMORPH, OvomorphRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.PRAETORIAN, PraetorianRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.QUEEN, QueenRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ROCKET, RocketRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER, ChestbursterRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER, FacehuggerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ROYAL_ABERRANT_OVOMORPH, OvomorphRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ROYAL_CHESTBURSTER, ChestbursterRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ROYAL_FACEHUGGER, FacehuggerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER, ChestbursterRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ROYAL_NETHER_FACEHUGGER, FacehuggerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ROYAL_NETHER_OVOMORPH, OvomorphRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.ROYAL_OVOMORPH, OvomorphRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.SENTRY_TURRET, SentryTurretRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.SHURIKEN, SpinningItemRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.SMART_DISC, SpinningItemRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.WARRIOR, WarriorRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AVPEntityTypes.YAUTJA, YautjaRenderer::new);
    }

    private static void registerItemRenderers() {
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPItems.ARMOR_CASE);
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPBlockItems.DESK_TERMINAL_BLOCK, name -> DeskTerminalItemRenderer::new);
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPItems.F903WE_RIFLE, name -> () -> new F903WEItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPItems.FLAMETHROWER_SEVASTOPOL, name -> () -> new FlamethrowerItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPItems.M37_12_SHOTGUN, name -> () -> new M3712ShotgunItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPItems.M41A_PULSE_RIFLE, name -> () -> new M41APulseRifleItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPItems.M42A3_SNIPER_RIFLE, name -> () -> new M42A3SniperRifleItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPItems.M4RA_BATTLE_RIFLE, name -> () -> new M4RABattleRifleItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPItems.M56_SMARTGUN, name -> () -> new M56SmartgunItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPItems.M6B_ROCKET_LAUNCHER, name -> () -> new M6BRocketLauncherItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(
            AVPItems.M88MOD4_COMBAT_PISTOL,
            name -> () -> new M88Mod4CombatPistolItemRenderer(name)
        );
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPItems.OLD_PAINLESS, name -> () -> new OldPainlessItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPBlockItems.RESONATOR_BLOCK, name -> ResonatorItemRenderer::new);
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPBlockItems.SENTRY_TURRET, name -> SentryTurretItemRenderer::new);
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPBlockItems.TRIP_MINE_BLOCK, name -> TripMineItemRenderer::new);
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPItems.ZX_76_SHOTGUN, name -> () -> new ZX76ShotgunItemRenderer(name));
    }

    private static void registerMenuScreens() {
        Services.CLIENT_REGISTRY.registerMenuScreen(AVPMenuTypes.ARMOR_CASE, ArmorCaseScreen::new);
        Services.CLIENT_REGISTRY.registerMenuScreen(AVPMenuTypes.INDUSTRIAL_FURNACE_MENU, IndustrialFurnaceScreen::new);
    }

    private static void registerParticleProviderFactories() {
        Services.CLIENT_REGISTRY.registerParticleProviderFactory(AVPParticleTypes.ACID, AcidParticleProvider::new);
        Services.CLIENT_REGISTRY.registerParticleProviderFactory(AVPParticleTypes.BLUE_ACID, BlueAcidParticleProvider::new);
        Services.CLIENT_REGISTRY.registerParticleProviderFactory(AVPParticleTypes.IRRADIATED_ACID, IrradiatedAcidParticleProvider::new);
    }
}
