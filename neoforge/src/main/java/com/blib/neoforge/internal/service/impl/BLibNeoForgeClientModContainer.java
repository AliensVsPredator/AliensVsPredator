package com.blib.neoforge.internal.service.impl;

import com.just.core.functional.tuple.Tuple2;
import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.item.AzItemRenderer;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.blib.client.BLibClientMod;
import com.blib.client.model.KeyInteractType;

@ApiStatus.Internal
public class BLibNeoForgeClientModContainer {

    private final BLibClientMod mod;

    private final List<Tuple2<Supplier<AzArmorRenderer>, List<Supplier<? extends Item>>>> armorRendererPairs;

    private final List<Tuple2<? extends Supplier<? extends BlockEntityType<? extends BlockEntity>>, ? extends BlockEntityRendererProvider<? extends BlockEntity>>> blockEntityRendererPairs;

    private final List<Tuple2<Supplier<? extends Block>, RenderType>> blockRenderLayerPairs;

    private final List<Tuple2<Supplier<? extends EntityType<?>>, EntityRendererProvider<?>>> entityRendererPairs;

    private final List<Tuple2<ItemColor, List<Supplier<? extends Item>>>> itemColorPairs;

    private final List<Tuple2<Supplier<? extends Item>, Function<String, Supplier<AzItemRenderer>>>> itemRendererPairs;

    private final List<Supplier<Tuple2<KeyMapping, Consumer<KeyInteractType>>>> keyMappingHandlerPairSuppliers;

    private final List<Tuple2<Supplier<? extends MenuType<?>>, MenuScreens.ScreenConstructor<?, ?>>> menuScreenConstructorPairs;

    private final List<Tuple2<Supplier<? extends ParticleType<?>>, ParticleEngine.SpriteParticleRegistration<?>>> particleProviderFactoryPairs;

    public BLibNeoForgeClientModContainer(BLibClientMod mod) {
        this.mod = mod;
        this.armorRendererPairs = new ArrayList<>();
        this.blockEntityRendererPairs = new ArrayList<>();
        this.blockRenderLayerPairs = new ArrayList<>();
        this.entityRendererPairs = new ArrayList<>();
        this.itemColorPairs = new ArrayList<>();
        this.itemRendererPairs = new ArrayList<>();
        this.keyMappingHandlerPairSuppliers = new ArrayList<>();
        this.menuScreenConstructorPairs = new ArrayList<>();
        this.particleProviderFactoryPairs = new ArrayList<>();
    }

    /* package-private */ List<Tuple2<Supplier<AzArmorRenderer>, List<Supplier<? extends Item>>>> getArmorRendererPairs() {
        return armorRendererPairs;
    }

    /* package-private */ List<Tuple2<? extends Supplier<? extends BlockEntityType<? extends BlockEntity>>, ? extends BlockEntityRendererProvider<? extends BlockEntity>>> getBlockEntityRendererPairs() {
        return blockEntityRendererPairs;
    }

    /* package-private */ List<Tuple2<Supplier<? extends Block>, RenderType>> getBlockRenderLayerPairs() {
        return blockRenderLayerPairs;
    }

    /* package-private */ List<Tuple2<Supplier<? extends EntityType<?>>, EntityRendererProvider<?>>> getEntityRendererPairs() {
        return entityRendererPairs;
    }

    /* package-private */ List<Tuple2<ItemColor, List<Supplier<? extends Item>>>> getItemColorPairs() {
        return itemColorPairs;
    }

    /* package-private */ List<Tuple2<Supplier<? extends Item>, Function<String, Supplier<AzItemRenderer>>>> getItemRendererPairs() {
        return itemRendererPairs;
    }

    /* package-private */ List<Supplier<Tuple2<KeyMapping, Consumer<KeyInteractType>>>> getKeyMappingHandlerPairSuppliers() {
        return keyMappingHandlerPairSuppliers;
    }

    /* package-private */ List<Tuple2<Supplier<? extends MenuType<?>>, MenuScreens.ScreenConstructor<?, ?>>> getMenuScreenConstructorPairs() {
        return menuScreenConstructorPairs;
    }

    /* package-private */ List<Tuple2<Supplier<? extends ParticleType<?>>, ParticleEngine.SpriteParticleRegistration<?>>> getParticleProviderFactoryPairs() {
        return particleProviderFactoryPairs;
    }

    /* package-private */ void registerArmorRenderer(
        Supplier<AzArmorRenderer> armorRendererSupplier,
        List<Supplier<? extends Item>> itemSuppliers
    ) {
        armorRendererPairs.add(new Tuple2<>(armorRendererSupplier, itemSuppliers));
    }

    /* package-private */ <T extends BlockEntity> void registerBlockEntityRenderer(
        Supplier<BlockEntityType<T>> blockEntityTypeSupplier,
        BlockEntityRendererProvider<T> renderProvider
    ) {
        blockEntityRendererPairs.add(new Tuple2<>(blockEntityTypeSupplier, renderProvider));
    }

    /* package-private */ void registerBlockRenderLayer(Supplier<? extends Block> blockSupplier, RenderType renderType) {
        blockRenderLayerPairs.add(new Tuple2<>(blockSupplier, renderType));
    }

    /* package-private */ <E extends Entity> void registerEntityRenderer(
        Supplier<EntityType<E>> entityTypeSupplier,
        EntityRendererProvider<E> entityRendererFactory
    ) {
        entityRendererPairs.add(new Tuple2<>(entityTypeSupplier, entityRendererFactory));
    }

    /* package-private */ void registerItemColor(ItemColor itemColor, List<Supplier<? extends Item>> itemSuppliers) {
        itemColorPairs.add(new Tuple2<>(itemColor, itemSuppliers));
    }

    /* package-private */ void registerItemRenderer(
        Supplier<? extends Item> itemSupplier,
        Function<String, Supplier<AzItemRenderer>> rendererFactory
    ) {
        itemRendererPairs.add(new Tuple2<>(itemSupplier, rendererFactory));
    }

    /* package-private */ <T extends AbstractContainerMenu> void registerKeyMapping(
        Supplier<Tuple2<KeyMapping, Consumer<KeyInteractType>>> keyMappingHandlerPairSupplier
    ) {
        keyMappingHandlerPairSuppliers.add(keyMappingHandlerPairSupplier);
    }

    /* package-private */ <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void registerMenuScreen(
        Supplier<? extends MenuType<T>> menuTypeSupplier,
        MenuScreens.ScreenConstructor<T, U> screenConstructor
    ) {
        menuScreenConstructorPairs.add(new Tuple2<>(menuTypeSupplier, screenConstructor));
    }

    /* package-private */ <T extends ParticleOptions> void registerParticleProviderFactory(
        Supplier<? extends ParticleType<T>> particleTypeSupplier,
        ParticleEngine.SpriteParticleRegistration<T> spriteParticleRegistration
    ) {
        particleProviderFactoryPairs.add(new Tuple2<>(particleTypeSupplier, spriteParticleRegistration));
    }
}
