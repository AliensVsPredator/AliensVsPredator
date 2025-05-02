package com.avp.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;

import java.util.List;

import com.avp.client.render.armor.AberrantChitinArmorRenderer;
import com.avp.client.render.armor.ChitinArmorRenderer;
import com.avp.client.render.armor.IrradiatedChitinArmorRenderer;
import com.avp.client.render.armor.JunglePredatorArmorRenderer;
import com.avp.client.render.armor.MK50ArmorRenderer;
import com.avp.client.render.armor.NetherChitinArmorRenderer;
import com.avp.client.render.armor.PlatedAberrantChitinArmorRenderer;
import com.avp.client.render.armor.PlatedChitinArmorRenderer;
import com.avp.client.render.armor.PlatedIrradiatedChitinArmorRenderer;
import com.avp.client.render.armor.PlatedNetherChitinArmorRenderer;
import com.avp.client.render.armor.PressureArmorRenderer;
import com.avp.client.render.armor.TacticalArmorRenderer;
import com.avp.client.render.armor.TacticalCamoArmorRenderer;
import com.avp.client.render.block.DeskTerminalRenderer;
import com.avp.client.render.block.ResonatorRenderer;
import com.avp.client.render.block.TripMineRenderer;
import com.avp.client.render.item.DeskTerminalItemRenderer;
import com.avp.client.render.item.ResonatorItemRenderer;
import com.avp.client.render.item.TripMineItemRenderer;
import com.avp.common.block.entity.AVPBlockEntityTypes;
import com.avp.common.item.AVPArmorItems;
import com.avp.common.item.TempAVPBlockItems;
import com.avp.service.Services;

public class AVPClient {

    public static void initialize() {
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            AberrantChitinArmorRenderer::new,
            List.of(
                AVPArmorItems.ABERRANT_CHITIN_HELMET,
                AVPArmorItems.ABERRANT_CHITIN_CHESTPLATE,
                AVPArmorItems.ABERRANT_CHITIN_LEGGINGS,
                AVPArmorItems.ABERRANT_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            ChitinArmorRenderer::new,
            List.of(
                AVPArmorItems.CHITIN_HELMET,
                AVPArmorItems.CHITIN_CHESTPLATE,
                AVPArmorItems.CHITIN_LEGGINGS,
                AVPArmorItems.CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            IrradiatedChitinArmorRenderer::new,
            List.of(
                AVPArmorItems.IRRADIATED_CHITIN_HELMET,
                AVPArmorItems.IRRADIATED_CHITIN_CHESTPLATE,
                AVPArmorItems.IRRADIATED_CHITIN_LEGGINGS,
                AVPArmorItems.IRRADIATED_CHITIN_BOOTS
            )
        );
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
            NetherChitinArmorRenderer::new,
            List.of(
                AVPArmorItems.NETHER_CHITIN_HELMET,
                AVPArmorItems.NETHER_CHITIN_CHESTPLATE,
                AVPArmorItems.NETHER_CHITIN_LEGGINGS,
                AVPArmorItems.NETHER_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PlatedAberrantChitinArmorRenderer::new,
            List.of(
                AVPArmorItems.PLATED_ABERRANT_CHITIN_HELMET,
                AVPArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE,
                AVPArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS,
                AVPArmorItems.PLATED_ABERRANT_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PlatedChitinArmorRenderer::new,
            List.of(
                AVPArmorItems.PLATED_CHITIN_HELMET,
                AVPArmorItems.PLATED_CHITIN_CHESTPLATE,
                AVPArmorItems.PLATED_CHITIN_LEGGINGS,
                AVPArmorItems.PLATED_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PlatedIrradiatedChitinArmorRenderer::new,
            List.of(
                AVPArmorItems.PLATED_IRRADIATED_CHITIN_HELMET,
                AVPArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE,
                AVPArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS,
                AVPArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PlatedNetherChitinArmorRenderer::new,
            List.of(
                AVPArmorItems.PLATED_NETHER_CHITIN_HELMET,
                AVPArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE,
                AVPArmorItems.PLATED_NETHER_CHITIN_LEGGINGS,
                AVPArmorItems.PLATED_NETHER_CHITIN_BOOTS
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

        Services.CLIENT_REGISTRY.registerItemRenderer(TempAVPBlockItems.DESK_TERMINAL_BLOCK, name -> DeskTerminalItemRenderer::new);
        Services.CLIENT_REGISTRY.registerItemRenderer(TempAVPBlockItems.TRIP_MINE_BLOCK, name -> TripMineItemRenderer::new);
        Services.CLIENT_REGISTRY.registerItemRenderer(TempAVPBlockItems.RESONATOR_BLOCK, name -> ResonatorItemRenderer::new);
    }
}
