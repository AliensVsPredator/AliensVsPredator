package com.avp.fabric.service;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.avp.service.ClientRegistryService;

public class FabricClientRegistryService implements ClientRegistryService {

    @Override
    public void registerArmorRenderer(Supplier<AzArmorRenderer> armorRendererSupplier, List<Supplier<Item>> itemSuppliers) {
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
    public void registerItemRenderer(Supplier<? extends Item> itemSupplier, Function<String, Supplier<AzItemRenderer>> rendererFactory) {
        registerItemRendererImmediately(itemSupplier.get(), rendererFactory);
    }
}
