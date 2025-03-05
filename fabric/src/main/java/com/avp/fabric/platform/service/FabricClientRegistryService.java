package com.avp.fabric.platform.service;

import com.avp.core.platform.PacketHandleContext;
import com.avp.core.platform.service.ClientRegistryService;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricClientRegistryService implements ClientRegistryService {

    @Override
    public void registerBlockRenderType(Supplier<Block> blockSupplier, RenderType renderType) {
        BlockRenderLayerMap.INSTANCE.putBlock(blockSupplier.get(), RenderType.cutout());
    }

    @Override
    public <E extends Entity> void registerEntityRenderer(Supplier<? extends EntityType<? extends E>> entityTypeSupplier, EntityRendererProvider<E> entityRendererFactory) {
        EntityRendererRegistry.register(entityTypeSupplier.get(), entityRendererFactory);
    }

    @Override
    public <M extends AbstractContainerMenu, U extends AbstractContainerScreen<M>> void registerMenuScreen(Supplier<? extends MenuType<? extends M>> menuTypeSupplier, MenuScreens.ScreenConstructor<M, U> containerScreen) {
        MenuScreens.register(menuTypeSupplier.get(), containerScreen);
    }

    @Override
    public <T extends CustomPacketPayload> void registerS2CPacket(CustomPacketPayload.Type<T> type, Consumer<PacketHandleContext.Client<T>> handler) {
        ClientPlayNetworking.registerGlobalReceiver(
            type,
            (payload, ctx) -> ctx.client().execute(() -> handler.accept(new PacketHandleContext.Client<>(payload, ctx.client(), ctx.player())))
        );
    }

    @Override
    public <T extends ParticleOptions> void registerParticleFactory(Supplier<? extends ParticleType<T>> particleTypeSupplier, Function<SpriteSet, ParticleProvider<T>> particleProviderFactory) {
        ParticleFactoryRegistry.getInstance().register(particleTypeSupplier.get(), particleProviderFactory::apply);
    }
}
