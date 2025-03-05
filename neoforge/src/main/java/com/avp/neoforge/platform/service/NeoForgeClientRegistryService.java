package com.avp.neoforge.platform.service;

import com.avp.core.platform.PacketHandleContext;
import com.avp.core.platform.service.ClientRegistryService;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NeoForgeClientRegistryService implements ClientRegistryService {

    private final List<Map.Entry<Supplier<? extends EntityType<?>>, EntityRendererProvider<?>>> entityTypeRendererPairs = new ArrayList<>();

    private final List<Map.Entry<Supplier<? extends MenuType<?>>, MenuScreens.ScreenConstructor<?, ?>>> menuTypeContainerScreenPairs = new ArrayList<>();

    private final List<Map.Entry<Supplier<? extends ParticleType<?>>, Function<SpriteSet, ParticleProvider<?>>>> particleTypeProviderPairs = new ArrayList<>();

    @Override
    public void registerBlockRenderType(Supplier<Block> blockSupplier, RenderType renderType) {
        ItemBlockRenderTypes.setRenderLayer(blockSupplier.get(), renderType);
    }

    @Override
    public <E extends Entity> void registerEntityRenderer(Supplier<? extends EntityType<? extends E>> entityTypeSupplier, EntityRendererProvider<E> entityRendererFactory) {
        entityTypeRendererPairs.add(Map.entry(entityTypeSupplier, entityRendererFactory));
    }

    @Override
    public <M extends AbstractContainerMenu, U extends AbstractContainerScreen<M>> void registerMenuScreen(Supplier<? extends MenuType<? extends M>> menuTypeSupplier, MenuScreens.ScreenConstructor<M, U> screenConstructor) {
        menuTypeContainerScreenPairs.add(Map.entry(menuTypeSupplier, screenConstructor));
    }

    @Override
    public <T extends CustomPacketPayload> void registerS2CPacket(CustomPacketPayload.Type<T> type, Consumer<PacketHandleContext.Client<T>> handler) {

    }

    @Override
    public <T extends ParticleOptions> void registerParticleFactory(Supplier<? extends ParticleType<T>> particleTypeSupplier, Function<SpriteSet, ParticleProvider<T>> particleProviderFactory) {
        particleTypeProviderPairs.add(Map.entry(particleTypeSupplier, particleProviderFactory::apply));
    }

    public List<Map.Entry<Supplier<? extends EntityType<?>>, EntityRendererProvider<?>>> getEntityTypeRendererPairs() {
        return Collections.unmodifiableList(entityTypeRendererPairs);
    }

    public List<Map.Entry<Supplier<? extends MenuType<?>>, MenuScreens.ScreenConstructor<?, ?>>> getMenuTypeContainerScreenPairs() {
        return Collections.unmodifiableList(menuTypeContainerScreenPairs);
    }

    public List<Map.Entry<Supplier<? extends ParticleType<?>>, Function<SpriteSet, ParticleProvider<?>>>> getParticleTypeProviderPairs() {
        return Collections.unmodifiableList(particleTypeProviderPairs);
    }
}
