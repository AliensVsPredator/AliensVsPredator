package com.blib.internal.service;

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

import com.blib.client.BLibClientMod;
import com.blib.client.model.KeyInteractType;

@ApiStatus.Internal
public interface BLibClientRegistryService {

    void registerArmorRenderer(
        BLibClientMod mod,
        Supplier<AzArmorRenderer> armorRendererSupplier,
        List<Supplier<? extends Item>> itemSuppliers
    );

    default void registerArmorRendererImmediately(
        BLibClientMod mod,
        Supplier<AzArmorRenderer> armorRendererSupplier,
        List<Supplier<? extends Item>> itemSuppliers
    ) {
        itemSuppliers.forEach(itemSupplier -> AzArmorRendererRegistry.register(armorRendererSupplier, itemSupplier.get()));
    }

    <T extends BlockEntity> void registerBlockEntityRenderer(
        BLibClientMod mod,
        Supplier<BlockEntityType<T>> blockEntityTypeSupplier,
        BlockEntityRendererProvider<T> renderProvider
    );

    void registerBlockRenderLayer(BLibClientMod mod, Supplier<? extends Block> blockSupplier, RenderType renderType);

    <E extends Entity> void registerEntityRenderer(
        BLibClientMod mod,
        Supplier<EntityType<E>> entityTypeSupplier,
        EntityRendererProvider<E> entityRendererFactory
    );

    void registerItemColor(BLibClientMod mod, ItemColor itemColor, List<Supplier<? extends Item>> itemSuppliers);

    void registerItemRenderer(
        BLibClientMod mod,
        Supplier<? extends Item> itemSupplier,
        Function<String, Supplier<AzItemRenderer>> rendererFactory
    );

    default void registerItemRendererImmediately(BLibClientMod mod, Item item, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        var path = BuiltInRegistries.ITEM.getKey(item).getPath();
        var itemRendererSupplier = rendererFactory.apply(path);
        AzItemRendererRegistry.register(itemRendererSupplier, item);
    }

    Supplier<Tuple2<KeyMapping, Consumer<KeyInteractType>>> registerKeyMapping(
        BLibClientMod mod,
        ResourceLocation resourceLocation,
        String category,
        int key,
        Consumer<KeyInteractType> keyInteractTypeConsumer
    );

    <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void registerMenuScreen(
        BLibClientMod mod,
        Supplier<? extends MenuType<T>> menuTypeSupplier,
        MenuScreens.ScreenConstructor<T, U> screenConstructor
    );

    <T extends ParticleOptions> void registerParticleProviderFactory(
        BLibClientMod mod,
        Supplier<? extends ParticleType<T>> particleTypeSupplier,
        ParticleEngine.SpriteParticleRegistration<T> spriteParticleRegistration
    );

}
