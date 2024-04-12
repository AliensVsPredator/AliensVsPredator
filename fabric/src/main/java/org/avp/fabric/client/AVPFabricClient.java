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

import org.avp.api.block.factory.BlockFactories;
import org.avp.client.AVPClientKeyBindings;
import org.avp.client.render.entity.AVPEntityRenderRegistry;
import org.avp.common.block.AVPBlocks;

public class AVPFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerEntityRenderBindings();

        AVPBlocks.getEntries().forEach(tuple -> {
            var block = tuple.first().get();
            var blockData = tuple.second();
            var factory = blockData.getFactory();
            if (factory == BlockFactories.TRANSPARENT) {
                BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
            }
        });

        registerKeyBindings();
    }

    @SuppressWarnings("unchecked")
    private static void registerEntityRenderBindings() {
        AVPEntityRenderRegistry.getBindings().forEach(binding -> {
            var entityType = (EntityType<Entity>) binding.entityTypeGameObject().get();
            var provider = (EntityRendererProvider<Entity>) binding.entityRendererProvider();
            EntityRendererRegistry.register(entityType, provider);
        });
    }

    private static void registerKeyBindings() {
        AVPClientKeyBindings.getEntries().forEach(tuple -> {
            var keyMapping = tuple.first();
            var biConsumer = tuple.second();

            KeyBindingHelper.registerKeyBinding(keyMapping);
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                while (keyMapping.consumeClick()) {
                    biConsumer.accept(keyMapping, client);
                }
            });
        });
    }
}
