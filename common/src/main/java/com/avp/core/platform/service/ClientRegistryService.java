package com.avp.core.platform.service;

import com.avp.core.platform.PacketHandleContext;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ClientRegistryService {

    void registerBlockRenderType(Supplier<Block> blockSupplier, RenderType renderType);

    <E extends Entity> void registerEntityRenderer(Supplier<? extends EntityType<? extends E>> entityTypeSupplier, EntityRendererProvider<E> entityRendererFactory);

    <M extends AbstractContainerMenu, U extends AbstractContainerScreen<M>> void registerMenuScreen(Supplier<? extends MenuType<? extends M>> menuTypeSupplier, MenuScreens.ScreenConstructor<M, U> containerScreen);

    <T extends CustomPacketPayload> void registerS2CPacket(CustomPacketPayload.Type<T> type, Consumer<PacketHandleContext.Client<T>> handler);

    <T extends ParticleOptions> void registerParticleFactory(Supplier<? extends ParticleType<T>> particleTypeSupplier, Function<SpriteSet, ParticleProvider<T>> particleProviderFactory);
}