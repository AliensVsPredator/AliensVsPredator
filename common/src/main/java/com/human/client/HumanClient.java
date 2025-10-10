package com.human.client;

import com.human.client.render.armor.MK50ArmorRenderer;
import com.human.client.render.armor.PressureArmorRenderer;
import com.human.client.render.armor.TacticalArmorRenderer;
import com.human.client.render.armor.TacticalCamoArmorRenderer;
import com.human.client.render.armor.WYCommandoArmorRenderer;
import com.human.client.render.armor.WYEliteArmorRenderer;
import com.human.client.render.block.DeskTerminalRenderer;
import com.human.client.render.block.ResonatorRenderer;
import com.human.client.render.block.SolarPanelRenderer;
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
import com.human.common.registry.init.HumanMenuTypes;
import com.human.common.registry.init.block.HumanFerroaluminumBlocks;
import com.human.common.registry.init.block.HumanIndustrialGlassBlocks;
import com.human.common.registry.init.block.HumanPlasticBlocks;
import com.human.common.registry.init.block.HumanSteelBlocks;
import com.human.common.registry.init.block.HumanTitaniumBlocks;
import com.human.common.registry.init.entity_type.HumanEntityTypes;
import com.human.common.registry.init.item.HumanGunItems;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.item.component.DyedItemColor;

import java.util.List;

import com.avp.common.registry.init.AVPBlockEntityTypes;
import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.block.CoreBlocks;
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPBlockItems;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.service.Services;

public class HumanClient {

    public static void initialize() {
        registerArmorRenderers();
        registerBlockEntityRenderers();
        registerBlockRenderLayers();
        registerEntityRenderers();
        registerItemRenderers();
        registerMenuScreens();
    }

    private static void registerArmorRenderers() {
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
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            WYCommandoArmorRenderer::new,
            List.of(
                AVPArmorItems.WY_COMMANDO_HELMET,
                AVPArmorItems.WY_COMMANDO_CHESTPLATE,
                AVPArmorItems.WY_COMMANDO_LEGGINGS,
                AVPArmorItems.WY_COMMANDO_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            WYEliteArmorRenderer::new,
            List.of(
                AVPArmorItems.WY_ELITE_HELMET,
                AVPArmorItems.WY_ELITE_CHESTPLATE,
                AVPArmorItems.WY_ELITE_LEGGINGS,
                AVPArmorItems.WY_ELITE_BOOTS
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
            AVPBlockEntityTypes.SOLAR_PANEL,
            (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new SolarPanelRenderer()
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
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanFerroaluminumBlocks.FERROALUMINUM_CHAIN_FENCE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanFerroaluminumBlocks.FERROALUMINUM_GRATE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanSteelBlocks.STEEL_BARS, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanSteelBlocks.STEEL_CHAIN_FENCE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanSteelBlocks.STEEL_GRATE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanTitaniumBlocks.TITANIUM_CHAIN_FENCE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanTitaniumBlocks.TITANIUM_GRATE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AVPBlocks.RAZOR_WIRE, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_DOOR, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_TRAP_DOOR, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanFerroaluminumBlocks.FERROALUMINUM_DOOR, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanSteelBlocks.STEEL_DOOR, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanTitaniumBlocks.TITANIUM_DOOR, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanFerroaluminumBlocks.FERROALUMINUM_TRAP_DOOR, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(CoreBlocks.TRINITITE_BLOCK, RenderType.translucent());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_SLAB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_STAIRS, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanSteelBlocks.STEEL_GRATE_SLAB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanSteelBlocks.STEEL_GRATE_STAIRS, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanTitaniumBlocks.TITANIUM_GRATE_SLAB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanTitaniumBlocks.TITANIUM_GRATE_STAIRS, RenderType.cutout());

        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_SLAB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_STAIRS, RenderType.cutout());
        HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values()
            .forEach(blockSupplier -> Services.CLIENT_REGISTRY.registerBlockRenderLayer(blockSupplier, RenderType.translucent()));
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_PANE, RenderType.cutout());
        HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values()
            .forEach(blockSupplier -> Services.CLIENT_REGISTRY.registerBlockRenderLayer(blockSupplier, RenderType.translucent()));
        HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_GRATE.values()
            .forEach(blockSupplier -> Services.CLIENT_REGISTRY.registerBlockRenderLayer(blockSupplier, RenderType.cutout()));
        HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_GRATE_SLAB.values()
            .forEach(blockSupplier -> Services.CLIENT_REGISTRY.registerBlockRenderLayer(blockSupplier, RenderType.cutout()));
        HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_GRATE_STAIRS.values()
            .forEach(blockSupplier -> Services.CLIENT_REGISTRY.registerBlockRenderLayer(blockSupplier, RenderType.cutout()));
    }

    private static void registerEntityRenderers() {
        Services.CLIENT_REGISTRY.registerEntityRenderer(HumanEntityTypes.FLAMETHROW, FlamethrowRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(HumanEntityTypes.GRENADE_THROWN, ThrownItemRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(HumanEntityTypes.MARINE, MarineRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(HumanEntityTypes.MUSHROOM_CLOUD, MushroomCloudRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(HumanEntityTypes.NUKE, NukeRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(HumanEntityTypes.ROCKET, RocketRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(HumanEntityTypes.SENTRY_TURRET, SentryTurretRenderer::new);
    }

    private static void registerItemRenderers() {
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPItems.ARMOR_CASE);
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPBlockItems.DESK_TERMINAL_BLOCK, name -> DeskTerminalItemRenderer::new);
        Services.CLIENT_REGISTRY.registerItemRenderer(HumanGunItems.F903WE_RIFLE, name -> () -> new F903WEItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(
            HumanGunItems.FLAMETHROWER_SEVASTOPOL,
            name -> () -> new FlamethrowerItemRenderer(name)
        );
        Services.CLIENT_REGISTRY.registerItemRenderer(HumanGunItems.M37_12_SHOTGUN, name -> () -> new M3712ShotgunItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(HumanGunItems.M41A_PULSE_RIFLE, name -> () -> new M41APulseRifleItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(
            HumanGunItems.M42A3_SNIPER_RIFLE,
            name -> () -> new M42A3SniperRifleItemRenderer(name)
        );
        Services.CLIENT_REGISTRY.registerItemRenderer(HumanGunItems.M4RA_BATTLE_RIFLE, name -> () -> new M4RABattleRifleItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(HumanGunItems.M56_SMARTGUN, name -> () -> new M56SmartgunItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(
            HumanGunItems.M6B_ROCKET_LAUNCHER,
            name -> () -> new M6BRocketLauncherItemRenderer(name)
        );
        Services.CLIENT_REGISTRY.registerItemRenderer(
            HumanGunItems.M88MOD4_COMBAT_PISTOL,
            name -> () -> new M88Mod4CombatPistolItemRenderer(name)
        );
        Services.CLIENT_REGISTRY.registerItemRenderer(HumanGunItems.OLD_PAINLESS, name -> () -> new OldPainlessItemRenderer(name));
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPBlockItems.RESONATOR_BLOCK, name -> ResonatorItemRenderer::new);
        Services.CLIENT_REGISTRY.registerItemRenderer(AVPBlockItems.SENTRY_TURRET, name -> SentryTurretItemRenderer::new);
        Services.CLIENT_REGISTRY.registerItemRenderer(HumanGunItems.ZX_76_SHOTGUN, name -> () -> new ZX76ShotgunItemRenderer(name));
    }

    private static void registerMenuScreens() {
        Services.CLIENT_REGISTRY.registerMenuScreen(HumanMenuTypes.ARMOR_CASE, ArmorCaseScreen::new);
        Services.CLIENT_REGISTRY.registerMenuScreen(HumanMenuTypes.INDUSTRIAL_FURNACE_MENU, IndustrialFurnaceScreen::new);
    }
}
