package org.avp.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.TickEvent;

import org.avp.api.block.factory.CustomTransparentBlockFactory;
import org.avp.client.AVPClientKeyBindings;
import org.avp.client.render.entity.AVPEntityRenderRegistry;
import org.avp.client.render.particle.AVPParticleTypeProviders;
import org.avp.common.AVPConstants;
import org.avp.common.registry.AVPDeferredBlockRegistry;

@Mod.EventBusSubscriber(modid = AVPConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AVPNeoForgeClient {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        AVPDeferredBlockRegistry.getDataEntries().forEach(tuple -> {
            var block = tuple.first().get();
            var blockData = tuple.second();
            var factory = blockData.getFactory();
            if (factory instanceof CustomTransparentBlockFactory) {
                ItemBlockRenderTypes.setRenderLayer(block, RenderType.translucent());
            }
        });
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        AVPEntityRenderRegistry.INSTANCE.verifyAllRendererProvidersPresent();
        AVPEntityRenderRegistry.INSTANCE.getValues().forEach(entityDataHolder -> {
            var entityData = entityDataHolder.get();
            var entityType = (EntityType<Entity>) entityData.entityTypeHolder().get();
            var provider = (EntityRendererProvider<Entity>) entityData.entityRendererProvider();
            event.registerEntityRenderer(entityType, provider);
        });
    }

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        AVPClientKeyBindings.getEntries().forEach(tuple -> {
            var keyMapping = tuple.first();
            event.register(keyMapping);
        });
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        // Register the providers to our deferred registry, first.
        AVPParticleTypeProviders.INSTANCE.register();

        // Then iterate over them and register for NeoForge.
        AVPParticleTypeProviders.INSTANCE.getValues().forEach(particleProviderDataHolder -> {
            var particleProviderData = particleProviderDataHolder.get();
            var supplier = particleProviderData.particleTypeHolder();
            var factoryProvider = particleProviderData.providerFactory();
            var particleType = supplier.get();
            event.registerSpriteSet(
                (ParticleType<ParticleOptions>) particleType,
                spriteSet -> (ParticleProvider<ParticleOptions>) factoryProvider.apply(spriteSet)
            );
        });
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) { // Only call code once as the tick event is called twice every tick
            AVPClientKeyBindings.getEntries().forEach(tuple -> {
                var keyMapping = tuple.first();
                var biConsumer = tuple.second();
                while (keyMapping.consumeClick()) {
                    biConsumer.accept(keyMapping, Minecraft.getInstance());
                }
            });
        }
    }
}
