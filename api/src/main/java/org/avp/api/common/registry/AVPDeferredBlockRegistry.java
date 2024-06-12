package org.avp.api.common.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BlockHolderSet;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.util.Tuple;
import org.avp.api.common.data.block.BlockData;
import org.avp.api.common.registry.holder.BlockHolderSetData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.service.Services;

public class AVPDeferredBlockRegistry extends AVPDeferredRegistry<Block> {

    private static final List<Tuple<BLHolder<Block>, BlockData>> DATA_ENTRIES = new ArrayList<>();

    public static List<Tuple<BLHolder<Block>, BlockData>> getDataEntries() {
        return DATA_ENTRIES;
    }

    protected BLHolder<Block> createHolder(
        String registryName,
        BlockModelData blockModelData,
        BlockTagData blockTagData
    ) {
        return createHolder(new BlockData(registryName, blockModelData, blockTagData));
    }

    protected BLHolder<Block> createHolder(
        String registryName,
        BlockModelData blockModelData,
        BlockTagData blockTagData,
        Function<Block, LootTable.Builder> lootProvider
    ) {
        return createHolder(new BlockData(registryName, blockModelData, blockTagData, lootProvider));
    }

    protected BLHolder<Block> createHolder(BlockData blockData) {
        var registryName = blockData.registryName();
        var holder = createHolder(registryName, () -> blockData.blockModelData().blockSupplier().get());
        entries.put(registryName, holder);
        DATA_ENTRIES.add(new Tuple<>(holder, blockData));
        return holder;
    }

    protected BlockHolderSet registerBlockHolderSet(BlockHolderSetData blockHolderSetData) {
        var blockData = blockHolderSetData.blockData();
        var properties = blockHolderSetData.properties();
        var holder = createHolder(blockData);
        var slabHolder = createHolder(BlockData.toSlab(holder, properties, blockData));
        var stairsHolder = createHolder(BlockData.toStairs(holder, properties, blockData));
        var wallHolder = createHolder(BlockData.toWall(holder, properties, blockData));
        return new BlockHolderSet(holder, slabHolder, stairsHolder, wallHolder);
    }

    @Override
    protected BLHolder<Block> createHolder(String registryName, Supplier<Block> supplier) {
        return Services.BLOCK_SERVICE.createHolder(registryName, supplier);
    }

    @Override
    public final void register() {
        getValues().forEach(Services.BLOCK_SERVICE::register);
    }
}
