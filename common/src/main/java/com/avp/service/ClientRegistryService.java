package com.avp.service;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererRegistry;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ClientRegistryService {

    void registerArmorRenderer(Supplier<AzArmorRenderer> armorRendererSupplier, List<Supplier<Item>> itemSuppliers);

    default void registerArmorRendererImmediately(Supplier<AzArmorRenderer> armorRendererSupplier, List<Supplier<Item>> itemSuppliers) {
        itemSuppliers.forEach(itemSupplier -> AzArmorRendererRegistry.register(armorRendererSupplier, itemSupplier.get()));
    }

    <T extends BlockEntity> void registerBlockEntityRenderer(
        Supplier<BlockEntityType<T>> blockEntityTypeSupplier,
        BlockEntityRendererProvider<T> renderProvider
    );

    void registerBlockRenderLayer(Supplier<Block> blockSupplier, RenderType renderType);

    void registerItemColor(ItemColor itemColor, List<Supplier<Item>> itemSuppliers);

    void registerItemRenderer(Supplier<? extends Item> itemSupplier, Function<String, Supplier<AzItemRenderer>> rendererFactory);

    default void registerItemRendererImmediately(Item item, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        var path = BuiltInRegistries.ITEM.getKey(item).getPath();
        var itemRendererSupplier = rendererFactory.apply(path);
        AzItemRendererRegistry.register(itemRendererSupplier, item);
    }
}
