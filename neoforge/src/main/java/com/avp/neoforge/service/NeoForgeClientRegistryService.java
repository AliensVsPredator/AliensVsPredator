package com.avp.neoforge.service;

import com.bvanseg.just.functional.tuple.Tuple2;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.avp.service.ClientRegistryService;

public class NeoForgeClientRegistryService implements ClientRegistryService {

    private final List<Tuple2<Supplier<AzArmorRenderer>, List<Supplier<Item>>>> armorRendererPairs;

    private final List<Tuple2<Supplier<? extends BlockEntityType<? extends BlockEntity>>, BlockEntityRendererProvider<? extends BlockEntity>>> blockEntityRendererPairs;

    private final List<Tuple2<ItemColor, List<Supplier<Item>>>> itemColorPairs;

    private final List<Tuple2<Supplier<? extends Item>, Function<String, Supplier<AzItemRenderer>>>> itemRendererPairs;

    public NeoForgeClientRegistryService() {
        this.armorRendererPairs = new ArrayList<>();
        this.blockEntityRendererPairs = new ArrayList<>();
        this.itemColorPairs = new ArrayList<>();
        this.itemRendererPairs = new ArrayList<>();
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
    public void registerItemColor(ItemColor itemColor, List<Supplier<Item>> itemSuppliers) {
        itemColorPairs.add(new Tuple2<>(itemColor, itemSuppliers));
    }

    @Override
    public void registerItemRenderer(Supplier<? extends Item> itemSupplier, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        itemRendererPairs.add(new Tuple2<>(itemSupplier, rendererFactory));
    }

    public List<Tuple2<Supplier<AzArmorRenderer>, List<Supplier<Item>>>> getArmorRendererPairs() {
        return armorRendererPairs;
    }

    public List<Tuple2<Supplier<? extends BlockEntityType<? extends BlockEntity>>, BlockEntityRendererProvider<? extends BlockEntity>>> getBlockEntityRendererPairs() {
        return blockEntityRendererPairs;
    }

    public List<Tuple2<ItemColor, List<Supplier<Item>>>> getItemColorPairs() {
        return itemColorPairs;
    }

    public List<Tuple2<Supplier<? extends Item>, Function<String, Supplier<AzItemRenderer>>>> getItemRendererPairs() {
        return itemRendererPairs;
    }
}
