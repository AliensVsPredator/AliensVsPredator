package com.avp.neoforge.client;

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

import com.avp.AVP;
import com.avp.client.AVPClient;
import com.avp.client.input.keybind.KeyPressHandler;
import com.avp.neoforge.service.NeoForgeClientRegistryService;
import com.avp.service.Services;

@EventBusSubscriber(modid = AVP.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AVPNeoForgeClient {

    private static final NeoForgeClientRegistryService CLIENT_REGISTRY = ((NeoForgeClientRegistryService) Services.CLIENT_REGISTRY);

    static {
        // We want this to run before any of the other events, as this sets up queues of data pairs (for example, pairs
        // of item suppliers to item renderers) prior the registration events firing.
        AVPClient.initialize();

        // Client game bus events.
        NeoForge.EVENT_BUS.addListener(AVPNeoForgeClient::onClientTick);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        CLIENT_REGISTRY.getArmorRendererPairs()
            .forEach(pair -> CLIENT_REGISTRY.registerArmorRendererImmediately(pair.first(), pair.second()));

        CLIENT_REGISTRY.getItemRendererPairs()
            .forEach(pair -> CLIENT_REGISTRY.registerItemRendererImmediately(pair.first().get(), pair.second()));

        CLIENT_REGISTRY.getBlockRenderLayerPairs()
            .forEach(pair -> ItemBlockRenderTypes.setRenderLayer(pair.first().get(), pair.second()));
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        CLIENT_REGISTRY.getItemColorPairs()
            .forEach(
                pair -> pair.second()
                    .forEach(
                        itemSupplier -> event.getItemColors()
                            .register(pair.first(), itemSupplier.get())
                    )
            );
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Entities
        CLIENT_REGISTRY.getEntityRendererPairs()
            .forEach(pair -> {
                var entityType = pair.first().get();
                @SuppressWarnings("unchecked")
                var entityRendererProvider = (EntityRendererProvider<Entity>) pair.second();
                event.registerEntityRenderer(entityType, entityRendererProvider);
            });

        // Block Entities
        CLIENT_REGISTRY.getBlockEntityRendererPairs()
            .forEach(pair -> {
                var blockEntityType = pair.first().get();
                @SuppressWarnings("unchecked")
                var blockEntityRendererProvider = (BlockEntityRendererProvider<BlockEntity>) pair.second();
                event.registerBlockEntityRenderer(blockEntityType, blockEntityRendererProvider);
            });
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        CLIENT_REGISTRY.getKeyMappingHandlerPairSuppliers()
            .forEach(keyMappingSupplier -> event.register(keyMappingSupplier.get().first()));
    }

    // Game bus event.
    public static void onClientTick(ClientTickEvent.Post event) {
        CLIENT_REGISTRY.getKeyMappingHandlerPairSuppliers()
            .forEach(keyMappingSupplier -> {
                var keyMapping = keyMappingSupplier.get().first();
                var keyInteractTypeConsumer = keyMappingSupplier.get().second();

                KeyPressHandler.handle(keyMapping, keyInteractTypeConsumer);
            });
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        CLIENT_REGISTRY.getMenuScreenConstructorPairs()
            .forEach(pair -> {
                var menuType = pair.first().get();
                @SuppressWarnings("unchecked")
                var screenConstructor = (MenuScreens.ScreenConstructor<AbstractContainerMenu, ?>) pair.second();
                event.register(menuType, screenConstructor);
            });
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        CLIENT_REGISTRY.getParticleProviderFactoryPairs()
            .forEach(pair -> {
                var particleType = (ParticleType<ParticleOptions>) pair.first().get();
                var spriteParticleRegistration = (ParticleEngine.SpriteParticleRegistration<ParticleOptions>) pair.second();
                event.registerSpriteSet(particleType, spriteParticleRegistration);
            });
    }
}
