package org.avp.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.TickEvent;

import org.avp.api.block.factory.BlockFactories;
import org.avp.client.AVPClientKeyBindings;
import org.avp.client.render.entity.AVPEntityRenderRegistry;
import org.avp.common.AVPConstants;
import org.avp.common.block.AVPBlocks;

/**
 * @author Boston Vanseghi
 */
@Mod.EventBusSubscriber(modid = AVPConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AVPNeoForgeClient {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        AVPBlocks.getEntries().forEach(tuple -> {
            var block = tuple.first().get();
            var blockData = tuple.second();
            var factory = blockData.getFactory();
            if (factory == BlockFactories.TRANSPARENT) {
                ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
            }
        });
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        AVPEntityRenderRegistry.getBindings().forEach(binding -> {
            var entityType = (EntityType<Entity>) binding.entityTypeGameObject().get();
            var provider = (EntityRendererProvider<Entity>) binding.entityRendererProvider();
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
