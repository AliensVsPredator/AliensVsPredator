package org.avp.common.registry;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.api.Tuple;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockHolderSet;
import org.avp.api.block.BlockHolderSetData;
import org.avp.common.service.Services;

public class AVPDeferredBlockRegistry extends AVPDeferredRegistry<Block> {

    private static final List<Tuple<Holder<Block>, BlockData>> DATA_ENTRIES = new ArrayList<>();

    public static List<Tuple<Holder<Block>, BlockData>> getDataEntries() {
        return DATA_ENTRIES;
    }

    protected Holder<Block> createHolder(BlockData blockData) {
        var registryName = blockData.registryName();
        var holder = createHolder(registryName,() -> blockData.blockModelData().blockSupplier().get());
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
    protected Holder<Block> createHolder(String registryName, Supplier<Block> supplier) {
        return Services.BLOCK_SERVICE.createHolder(registryName, supplier);
    }

    @Override
    public final void register() {
        getValues().forEach(Services.BLOCK_SERVICE::register);
    }
}
