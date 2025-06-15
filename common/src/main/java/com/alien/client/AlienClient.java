package com.alien.client;

import com.alien.client.particle.AcidParticleProvider;
import com.alien.client.particle.BlueAcidParticleProvider;
import com.alien.client.particle.IrradiatedAcidParticleProvider;
import com.alien.client.render.armor.AberrantChitinArmorRenderer;
import com.alien.client.render.armor.ChitinArmorRenderer;
import com.alien.client.render.armor.IrradiatedChitinArmorRenderer;
import com.alien.client.render.armor.NetherChitinArmorRenderer;
import com.alien.client.render.armor.PlatedAberrantChitinArmorRenderer;
import com.alien.client.render.armor.PlatedChitinArmorRenderer;
import com.alien.client.render.armor.PlatedIrradiatedChitinArmorRenderer;
import com.alien.client.render.armor.PlatedNetherChitinArmorRenderer;
import com.alien.client.render.entity.AcidRenderer;
import com.alien.client.render.entity.AdolescentRenderer;
import com.alien.client.render.entity.ChestbursterRenderer;
import com.alien.client.render.entity.CrusherRenderer;
import com.alien.client.render.entity.DroneRenderer;
import com.alien.client.render.entity.OvomorphRenderer;
import com.alien.client.render.entity.PraetorianRenderer;
import com.alien.client.render.entity.ProwlerRenderer;
import com.alien.client.render.entity.QueenRenderer;
import com.alien.client.render.entity.RunnerRenderer;
import com.alien.client.render.entity.WarriorRenderer;
import com.alien.client.render.entity.parasite.facehugger.FacehuggerRenderer;
import com.alien.common.registry.init.AlienBlocks;
import com.alien.common.registry.init.AlienEntityTypes;
import com.alien.common.registry.init.block.AlienResinBlocks;
import com.alien.common.registry.init.item.AlienArmorItems;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

import com.avp.common.registry.init.AlienParticleTypes;
import com.avp.service.Services;

public class AlienClient {

    public static void initialize() {
        registerArmorRenderers();
        registerBlockRenderLayers();
        registerEntityRenderers();
        registerParticleProviderFactories();
    }

    private static void registerArmorRenderers() {
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            AberrantChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.ABERRANT_CHITIN_HELMET,
                AlienArmorItems.ABERRANT_CHITIN_CHESTPLATE,
                AlienArmorItems.ABERRANT_CHITIN_LEGGINGS,
                AlienArmorItems.ABERRANT_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            ChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.CHITIN_HELMET,
                AlienArmorItems.CHITIN_CHESTPLATE,
                AlienArmorItems.CHITIN_LEGGINGS,
                AlienArmorItems.CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            IrradiatedChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.IRRADIATED_CHITIN_HELMET,
                AlienArmorItems.IRRADIATED_CHITIN_CHESTPLATE,
                AlienArmorItems.IRRADIATED_CHITIN_LEGGINGS,
                AlienArmorItems.IRRADIATED_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            NetherChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.NETHER_CHITIN_HELMET,
                AlienArmorItems.NETHER_CHITIN_CHESTPLATE,
                AlienArmorItems.NETHER_CHITIN_LEGGINGS,
                AlienArmorItems.NETHER_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PlatedAberrantChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.PLATED_ABERRANT_CHITIN_HELMET,
                AlienArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE,
                AlienArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS,
                AlienArmorItems.PLATED_ABERRANT_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PlatedChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.PLATED_CHITIN_HELMET,
                AlienArmorItems.PLATED_CHITIN_CHESTPLATE,
                AlienArmorItems.PLATED_CHITIN_LEGGINGS,
                AlienArmorItems.PLATED_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PlatedIrradiatedChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_HELMET,
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE,
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS,
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PlatedNetherChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.PLATED_NETHER_CHITIN_HELMET,
                AlienArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE,
                AlienArmorItems.PLATED_NETHER_CHITIN_LEGGINGS,
                AlienArmorItems.PLATED_NETHER_CHITIN_BOOTS
            )
        );
    }

    private static void registerBlockRenderLayers() {
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienResinBlocks.IRRADIATED_RESIN_VEIN, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienResinBlocks.IRRADIATED_RESIN_WEB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienResinBlocks.ABERRANT_RESIN_VEIN, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienResinBlocks.ABERRANT_RESIN_WEB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienResinBlocks.NETHER_RESIN_VEIN, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienResinBlocks.NETHER_RESIN_WEB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienResinBlocks.RESIN_VEIN, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienResinBlocks.RESIN_WEB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienBlocks.ROYAL_JELLY_BLOCK, RenderType.translucent());
    }

    private static void registerEntityRenderers() {
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ABERRANT_ADOLESCENT, AdolescentRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ABERRANT_CHESTBURSTER, ChestbursterRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ABERRANT_CRUSHER, CrusherRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ABERRANT_DRONE, DroneRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ABERRANT_FACEHUGGER, FacehuggerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ABERRANT_OVOMORPH, OvomorphRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ABERRANT_PRAETORIAN, PraetorianRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ABERRANT_PROWLER, ProwlerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ABERRANT_QUEEN, QueenRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ABERRANT_RUNNER, RunnerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ABERRANT_WARRIOR, WarriorRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ACID, AcidRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ADOLESCENT, AdolescentRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.CHESTBURSTER, ChestbursterRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.CRUSHER, CrusherRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.DRONE, DroneRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.FACEHUGGER, FacehuggerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.IRRADIATED_CRUSHER, CrusherRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.IRRADIATED_DRONE, DroneRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.IRRADIATED_PRAETORIAN, PraetorianRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.IRRADIATED_PROWLER, ProwlerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.IRRADIATED_QUEEN, QueenRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.IRRADIATED_RUNNER, RunnerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.IRRADIATED_WARRIOR, WarriorRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.NETHER_ADOLESCENT, AdolescentRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.NETHER_CHESTBURSTER, ChestbursterRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.NETHER_CRUSHER, CrusherRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.NETHER_DRONE, DroneRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.NETHER_FACEHUGGER, FacehuggerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.NETHER_OVOMORPH, OvomorphRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.NETHER_PRAETORIAN, PraetorianRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.NETHER_PROWLER, ProwlerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.NETHER_QUEEN, QueenRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.NETHER_RUNNER, RunnerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.NETHER_WARRIOR, WarriorRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.OVOMORPH, OvomorphRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.PRAETORIAN, PraetorianRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.PROWLER, ProwlerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.QUEEN, QueenRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ROYAL_ABERRANT_ADOLESCENT, AdolescentRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER, ChestbursterRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ROYAL_ABERRANT_FACEHUGGER, FacehuggerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ROYAL_ABERRANT_OVOMORPH, OvomorphRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ROYAL_ADOLESCENT, AdolescentRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ROYAL_CHESTBURSTER, ChestbursterRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ROYAL_FACEHUGGER, FacehuggerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ROYAL_NETHER_ADOLESCENT, AdolescentRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER, ChestbursterRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ROYAL_NETHER_FACEHUGGER, FacehuggerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ROYAL_NETHER_OVOMORPH, OvomorphRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.ROYAL_OVOMORPH, OvomorphRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.RUNNER, RunnerRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(AlienEntityTypes.WARRIOR, WarriorRenderer::new);
    }

    private static void registerParticleProviderFactories() {
        Services.CLIENT_REGISTRY.registerParticleProviderFactory(AlienParticleTypes.ACID, AcidParticleProvider::new);
        Services.CLIENT_REGISTRY.registerParticleProviderFactory(AlienParticleTypes.BLUE_ACID, BlueAcidParticleProvider::new);
        Services.CLIENT_REGISTRY.registerParticleProviderFactory(AlienParticleTypes.IRRADIATED_ACID, IrradiatedAcidParticleProvider::new);
    }
}
