package com.avp.neoforge.service;

import com.bvanseg.just.functional.function.Lazy;
import com.bvanseg.just.functional.tuple.Tuple2;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.avp.client.input.keybind.util.KeyMappingUtil;
import com.avp.service.ClientRegistryService;

public class NeoForgeClientRegistryService implements ClientRegistryService {

    private final List<Tuple2<Supplier<AzArmorRenderer>, List<Supplier<Item>>>> armorRendererPairs;

    private final List<Tuple2<Supplier<? extends BlockEntityType<? extends BlockEntity>>, BlockEntityRendererProvider<? extends BlockEntity>>> blockEntityRendererPairs;

    private final List<Tuple2<Supplier<? extends Block>, RenderType>> blockRenderLayerPairs;

    private final List<Tuple2<Supplier<? extends EntityType<?>>, EntityRendererProvider<?>>> entityRendererPairs;

    private final List<Tuple2<ItemColor, List<Supplier<Item>>>> itemColorPairs;

    private final List<Tuple2<Supplier<? extends Item>, Function<String, Supplier<AzItemRenderer>>>> itemRendererPairs;

    private final List<Supplier<Tuple2<KeyMapping, Runnable>>> keyMappingHandlerPairSuppliers;

    private final List<Tuple2<Supplier<? extends MenuType<?>>, MenuScreens.ScreenConstructor<?, ?>>> menuScreenConstructorPairs;

    private final List<Tuple2<Supplier<? extends ParticleType<?>>, ParticleEngine.SpriteParticleRegistration<?>>> particleProviderFactoryPairs;

    public NeoForgeClientRegistryService() {
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

    @Override
    public void registerArmorRenderer(Supplier<AzArmorRenderer> armorRendererSupplier, List<Supplier<Item>> itemSuppliers) {
        armorRendererPairs.add(new Tuple2<>(armorRendererSupplier, itemSuppliers));
    }

    @Override
    public <T extends BlockEntity> void registerBlockEntityRenderer(
        Supplier<BlockEntityType<T>> blockEntityTypeSupplier,
        BlockEntityRendererProvider<T> renderProvider
    ) {
        blockEntityRendererPairs.add(new Tuple2<>(blockEntityTypeSupplier, renderProvider));
    }

    @Override
    public void registerBlockRenderLayer(Supplier<? extends Block> blockSupplier, RenderType renderType) {
        blockRenderLayerPairs.add(new Tuple2<>(blockSupplier, renderType));
    }

    @Override
    public <E extends Entity> void registerEntityRenderer(
        Supplier<EntityType<E>> entityTypeSupplier,
        EntityRendererProvider<E> entityRendererFactory
    ) {
        entityRendererPairs.add(new Tuple2<>(entityTypeSupplier, entityRendererFactory));
    }

    @Override
    public void registerItemColor(ItemColor itemColor, List<Supplier<Item>> itemSuppliers) {
        itemColorPairs.add(new Tuple2<>(itemColor, itemSuppliers));
    }

    @Override
    public void registerItemRenderer(Supplier<? extends Item> itemSupplier, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        itemRendererPairs.add(new Tuple2<>(itemSupplier, rendererFactory));
    }

    @Override
    public Supplier<Tuple2<KeyMapping, Runnable>> registerKeyMapping(String id, String category, int key, Runnable onKeyMappingActivated) {
        // Note the use of Lazy.of(...) here. This is deliberate so that the key mapping is only created once.
        Supplier<Tuple2<KeyMapping, Runnable>> supplier = Lazy.of(
            () -> new Tuple2<>(KeyMappingUtil.createKeyMapping(id, category, key), onKeyMappingActivated)
        );
        keyMappingHandlerPairSuppliers.add(supplier);
        return supplier;
    }

    @Override
    public <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void registerMenuScreen(
        Supplier<? extends MenuType<T>> menuTypeSupplier,
        MenuScreens.ScreenConstructor<T, U> screenConstructor
    ) {
        menuScreenConstructorPairs.add(new Tuple2<>(menuTypeSupplier, screenConstructor));
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProviderFactory(
        Supplier<? extends ParticleType<T>> particleTypeSupplier,
        ParticleEngine.SpriteParticleRegistration<T> spriteParticleRegistration
    ) {
        particleProviderFactoryPairs.add(new Tuple2<>(particleTypeSupplier, spriteParticleRegistration));
    }

    public List<Tuple2<Supplier<AzArmorRenderer>, List<Supplier<Item>>>> getArmorRendererPairs() {
        return armorRendererPairs;
    }

    public List<Tuple2<Supplier<? extends BlockEntityType<? extends BlockEntity>>, BlockEntityRendererProvider<? extends BlockEntity>>> getBlockEntityRendererPairs() {
        return blockEntityRendererPairs;
    }

    public List<Tuple2<Supplier<? extends Block>, RenderType>> getBlockRenderLayerPairs() {
        return blockRenderLayerPairs;
    }

    public List<Tuple2<Supplier<? extends EntityType<?>>, EntityRendererProvider<?>>> getEntityRendererPairs() {
        return entityRendererPairs;
    }

    public List<Tuple2<ItemColor, List<Supplier<Item>>>> getItemColorPairs() {
        return itemColorPairs;
    }

    public List<Tuple2<Supplier<? extends Item>, Function<String, Supplier<AzItemRenderer>>>> getItemRendererPairs() {
        return itemRendererPairs;
    }

    public List<Supplier<Tuple2<KeyMapping, Runnable>>> getKeyMappingHandlerPairSuppliers() {
        return keyMappingHandlerPairSuppliers;
    }

    public List<Tuple2<Supplier<? extends MenuType<?>>, MenuScreens.ScreenConstructor<?, ?>>> getMenuScreenConstructorPairs() {
        return menuScreenConstructorPairs;
    }

    public List<Tuple2<Supplier<? extends ParticleType<?>>, ParticleEngine.SpriteParticleRegistration<?>>> getParticleProviderFactoryPairs() {
        return particleProviderFactoryPairs;
    }
}
