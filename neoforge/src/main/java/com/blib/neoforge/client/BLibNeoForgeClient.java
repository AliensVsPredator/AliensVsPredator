package com.blib.neoforge.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;

import com.blib.BLib;
import com.blib.client.input.keybind.KeyPressHandler;
import com.blib.neoforge.service.impl.NeoForgeBLibClientRegistryServiceImpl;
import com.blib.service.BLibServices;

@EventBusSubscriber(modid = BLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BLibNeoForgeClient {

    private static final NeoForgeBLibClientRegistryServiceImpl CLIENT_REGISTRY =
        (NeoForgeBLibClientRegistryServiceImpl) BLibServices.CLIENT_REGISTRY;

    static {
        // Client game bus events.
        NeoForge.EVENT_BUS.addListener(BLibNeoForgeClient::onClientTick);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        CLIENT_REGISTRY.getArmorRendererPairs()
            .forEach(pair -> CLIENT_REGISTRY.registerArmorRendererImmediately(pair.v1(), pair.v2()));

        CLIENT_REGISTRY.getItemRendererPairs()
            .forEach(pair -> CLIENT_REGISTRY.registerItemRendererImmediately(pair.v1().get(), pair.v2()));

        CLIENT_REGISTRY.getBlockRenderLayerPairs()
            .forEach(pair -> ItemBlockRenderTypes.setRenderLayer(pair.v1().get(), pair.v2()));
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        CLIENT_REGISTRY.getItemColorPairs()
            .forEach(
                pair -> pair.v2()
                    .forEach(
                        itemSupplier -> event.getItemColors()
                            .register(pair.v1(), itemSupplier.get())
                    )
            );
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Entities
        CLIENT_REGISTRY.getEntityRendererPairs()
            .forEach(pair -> {
                var entityType = pair.v1().get();
                @SuppressWarnings("unchecked")
                var entityRendererProvider = (EntityRendererProvider<Entity>) pair.v2();
                event.registerEntityRenderer(entityType, entityRendererProvider);
            });

        // Block Entities
        CLIENT_REGISTRY.getBlockEntityRendererPairs()
            .forEach(pair -> {
                var blockEntityType = pair.v1().get();
                @SuppressWarnings("unchecked")
                var blockEntityRendererProvider = (BlockEntityRendererProvider<BlockEntity>) pair.v2();
                event.registerBlockEntityRenderer(blockEntityType, blockEntityRendererProvider);
            });
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        CLIENT_REGISTRY.getKeyMappingHandlerPairSuppliers()
            .forEach(keyMappingSupplier -> event.register(keyMappingSupplier.get().v1()));
    }

    // Game bus event.
    public static void onClientTick(ClientTickEvent.Post event) {
        CLIENT_REGISTRY.getKeyMappingHandlerPairSuppliers()
            .forEach(keyMappingSupplier -> {
                var keyMapping = keyMappingSupplier.get().v1();
                var keyInteractTypeConsumer = keyMappingSupplier.get().v2();

                KeyPressHandler.handle(keyMapping, keyInteractTypeConsumer);
            });
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        CLIENT_REGISTRY.getMenuScreenConstructorPairs()
            .forEach(pair -> {
                var menuType = pair.v1().get();
                @SuppressWarnings("unchecked")
                var screenConstructor = (MenuScreens.ScreenConstructor<AbstractContainerMenu, ?>) pair.v2();
                event.register(menuType, screenConstructor);
            });
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        CLIENT_REGISTRY.getParticleProviderFactoryPairs()
            .forEach(pair -> {
                var particleType = (ParticleType<ParticleOptions>) pair.v1().get();
                var spriteParticleRegistration = (ParticleEngine.SpriteParticleRegistration<ParticleOptions>) pair.v2();
                event.registerSpriteSet(particleType, spriteParticleRegistration);
            });
    }
}
