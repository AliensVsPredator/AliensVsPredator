package com.avp.service;

import com.just.core.functional.tuple.Tuple2;
import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.avp.client.model.KeyInteractType;
import com.avp.client.render.item.SimpleItemRenderer;

public interface ClientRegistryService {

    Function<String, Supplier<AzItemRenderer>> ITEM_RENDERER_SUPPLIER_FACTORY = name -> () -> new SimpleItemRenderer(
        name
    );

    void registerArmorRenderer(Supplier<AzArmorRenderer> armorRendererSupplier, List<Supplier<? extends Item>> itemSuppliers);

    default void registerArmorRendererImmediately(
        Supplier<AzArmorRenderer> armorRendererSupplier,
        List<Supplier<? extends Item>> itemSuppliers
    ) {
        itemSuppliers.forEach(itemSupplier -> AzArmorRendererRegistry.register(armorRendererSupplier, itemSupplier.get()));
    }

    <T extends BlockEntity> void registerBlockEntityRenderer(
        Supplier<BlockEntityType<T>> blockEntityTypeSupplier,
        BlockEntityRendererProvider<T> renderProvider
    );

    void registerBlockRenderLayer(Supplier<? extends Block> blockSupplier, RenderType renderType);

    <E extends Entity> void registerEntityRenderer(
        Supplier<EntityType<E>> entityTypeSupplier,
        EntityRendererProvider<E> entityRendererFactory
    );

    void registerItemColor(ItemColor itemColor, List<Supplier<? extends Item>> itemSuppliers);

    default void registerItemRenderer(Supplier<? extends Item> itemSupplier) {
        registerItemRenderer(itemSupplier, ITEM_RENDERER_SUPPLIER_FACTORY);
    }

    void registerItemRenderer(Supplier<? extends Item> itemSupplier, Function<String, Supplier<AzItemRenderer>> rendererFactory);

    default void registerItemRendererImmediately(Item item, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        var path = BuiltInRegistries.ITEM.getKey(item).getPath();
        var itemRendererSupplier = rendererFactory.apply(path);
        AzItemRendererRegistry.register(itemRendererSupplier, item);
    }

    Supplier<Tuple2<KeyMapping, Consumer<KeyInteractType>>> registerKeyMapping(
        String id,
        String category,
        int key,
        Consumer<KeyInteractType> keyInteractTypeConsumer
    );

    <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void registerMenuScreen(
        Supplier<? extends MenuType<T>> menuTypeSupplier,
        MenuScreens.ScreenConstructor<T, U> screenConstructor
    );

    <T extends ParticleOptions> void registerParticleProviderFactory(
        Supplier<? extends ParticleType<T>> particleTypeSupplier,
        ParticleEngine.SpriteParticleRegistration<T> spriteParticleRegistration
    );

}
