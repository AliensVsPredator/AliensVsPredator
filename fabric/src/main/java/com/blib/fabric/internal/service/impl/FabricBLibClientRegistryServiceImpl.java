package com.blib.fabric.internal.service.impl;

import com.just.core.functional.tuple.Tuple2;
import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.item.AzItemRenderer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.blib.client.input.keybind.KeyPressHandler;
import com.blib.client.input.keybind.util.KeyMappingUtil;
import com.blib.client.model.KeyInteractType;
import com.blib.internal.service.BLibClientRegistryService;

@ApiStatus.Internal
public class FabricBLibClientRegistryServiceImpl implements BLibClientRegistryService {

    @Override
    public void registerArmorRenderer(Supplier<AzArmorRenderer> armorRendererSupplier, List<Supplier<? extends Item>> itemSuppliers) {
        registerArmorRendererImmediately(armorRendererSupplier, itemSuppliers);
    }

    @Override
    public <T extends BlockEntity> void registerBlockEntityRenderer(
        Supplier<BlockEntityType<T>> blockEntityTypeSupplier,
        BlockEntityRendererProvider<T> renderProvider
    ) {
        BlockEntityRenderers.register(blockEntityTypeSupplier.get(), renderProvider);
    }

    @Override
    public void registerBlockRenderLayer(Supplier<? extends Block> blockSupplier, RenderType renderType) {
        BlockRenderLayerMap.INSTANCE.putBlock(blockSupplier.get(), renderType);
    }

    @Override
    public <E extends Entity> void registerEntityRenderer(
        Supplier<EntityType<E>> entityTypeSupplier,
        EntityRendererProvider<E> entityRendererFactory
    ) {
        EntityRendererRegistry.register(entityTypeSupplier.get(), entityRendererFactory);
    }

    @Override
    public void registerItemColor(ItemColor itemColor, List<Supplier<? extends Item>> itemSuppliers) {
        itemSuppliers.forEach(itemSupplier -> ColorProviderRegistry.ITEM.register(itemColor, itemSupplier.get()));
    }

    @Override
    public void registerItemRenderer(Supplier<? extends Item> itemSupplier, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        registerItemRendererImmediately(itemSupplier.get(), rendererFactory);
    }

    @Override
    public Supplier<Tuple2<KeyMapping, Consumer<KeyInteractType>>> registerKeyMapping(
        ResourceLocation resourceLocation,
        String category,
        int key,
        Consumer<KeyInteractType> keyInteractTypeConsumer
    ) {
        var keyMapping = KeyMappingUtil.createKeyMapping(resourceLocation, category, key);
        var keyMappingHandlerPair = new Tuple2<>(keyMapping, keyInteractTypeConsumer);

        KeyBindingHelper.registerKeyBinding(keyMapping);

        ClientTickEvents.END_CLIENT_TICK.register(client -> KeyPressHandler.handle(keyMapping, keyInteractTypeConsumer));

        return () -> keyMappingHandlerPair;
    }

    @Override
    public <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void registerMenuScreen(
        Supplier<? extends MenuType<T>> menuTypeSupplier,
        MenuScreens.ScreenConstructor<T, U> screenConstructor
    ) {
        MenuScreens.register(menuTypeSupplier.get(), screenConstructor);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProviderFactory(
        Supplier<? extends ParticleType<T>> particleTypeSupplier,
        ParticleEngine.SpriteParticleRegistration<T> spriteParticleRegistration
    ) {
        ParticleFactoryRegistry.getInstance().register(particleTypeSupplier.get(), spriteParticleRegistration::create);
    }
}
