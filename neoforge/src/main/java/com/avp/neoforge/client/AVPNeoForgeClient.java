package com.avp.neoforge.client;

import com.avp.core.AVP;
import com.avp.core.client.AVPClient;
import com.avp.core.platform.service.Services;
import com.avp.neoforge.platform.service.NeoForgeClientRegistryService;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = AVP.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AVPNeoForgeClient {

    @SubscribeEvent
    public static void initialize(FMLClientSetupEvent event) {
        AVPClient.initialize();
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        var pairs = ((NeoForgeClientRegistryService) Services.CLIENT_REGISTRY).getEntityTypeRendererPairs();

        pairs.forEach(pair -> {
            var entityType = (EntityType<Entity>) pair.getKey().get();
            var provider = (EntityRendererProvider<Entity>) pair.getValue();
            event.registerEntityRenderer(entityType, provider);
        });
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        var pairs = ((NeoForgeClientRegistryService) Services.CLIENT_REGISTRY).getMenuTypeContainerScreenPairs();
        pairs.forEach(pair -> {
            var menuType = (MenuType<AbstractContainerMenu>) pair.getKey().get();
            var screenConstructor = (MenuScreens.ScreenConstructor<AbstractContainerMenu, AbstractContainerScreen<AbstractContainerMenu>>) pair.getValue();
            event.register(menuType, screenConstructor);
        });
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        var pairs = ((NeoForgeClientRegistryService) Services.CLIENT_REGISTRY).getParticleTypeProviderPairs();
        pairs.forEach(pair -> {
            var particleType = (ParticleType<SimpleParticleType>) pair.getKey().get();
            var particleProvider = (ParticleProvider<SimpleParticleType>) pair.getValue();
            event.registerSpecial(particleType, particleProvider);
        });
    }
}
