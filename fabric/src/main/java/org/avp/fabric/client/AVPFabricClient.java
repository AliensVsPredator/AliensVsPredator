package org.avp.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import org.avp.api.common.registry.AVPDeferredBlockRegistry;
import org.avp.client.AVPClientKeyBindingRegistry;
import org.avp.client.registry.AVPEntityRenderRegistry;
import org.avp.client.render.particle.AVPParticleTypeProviders;

public class AVPFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerEntityRenderBindings();

        AVPParticleTypeProviders.INSTANCE.register();

        AVPDeferredBlockRegistry.getDataEntries().forEach(entry -> {
            var block = entry.getKey().get();
            var blockData = entry.getValue();
            var blockModelRenderType = blockData.blockModelData().blockModelRenderType();

            switch (blockModelRenderType) {
                case CUTOUT -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
                case NORMAL -> {/* NO-OP */}
                case TRANSLUCENT -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent());
            }
        });

        registerKeyBindings();
    }

    @SuppressWarnings("unchecked")
    private static void registerEntityRenderBindings() {
        AVPEntityRenderRegistry.INSTANCE.verifyAllRendererProvidersPresent();
        AVPEntityRenderRegistry.INSTANCE.getValues().forEach(entityDataHolder -> {
            var entityData = entityDataHolder.get();
            var entityType = (EntityType<Entity>) entityData.entityTypeHolder().get();
            var provider = (EntityRendererProvider<Entity>) entityData.entityRendererProvider();
            EntityRendererRegistry.register(entityType, provider);
        });
    }

    private static void registerKeyBindings() {
        AVPClientKeyBindingRegistry.getEntries().forEach(entry -> {
            var keyMapping = entry.getKey();
            var biConsumer = entry.getValue();

            KeyBindingHelper.registerKeyBinding(keyMapping);
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                while (keyMapping.consumeClick()) {
                    biConsumer.accept(keyMapping, client);
                }
            });
        });
    }
}
