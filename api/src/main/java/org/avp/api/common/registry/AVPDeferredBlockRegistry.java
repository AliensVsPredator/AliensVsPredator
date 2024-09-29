package org.avp.api.common.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.OldBlockData;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.registry.holder.BlockHolderSet;
import org.avp.api.common.registry.holder.BlockHolderSetData;
import org.avp.api.service.Services;

public class AVPDeferredBlockRegistry extends AVPDeferredRegistry<Block> {

    public static final AVPDeferredBlockRegistry INSTANCE = new AVPDeferredBlockRegistry();

    @Deprecated(forRemoval = true)
    private static final Map<BLHolder<Block>, OldBlockData> DATA_ENTRIES = new LinkedHashMap<>();

    @Deprecated(forRemoval = true)
    public static Set<Map.Entry<BLHolder<Block>, OldBlockData>> getDataEntries() {
        return DATA_ENTRIES.entrySet();
    }

    @Deprecated(forRemoval = true)
    protected BLHolder<Block> createHolder(
        String registryName,
        BlockModelData blockModelData,
        BlockTagData blockTagData
    ) {
        return createHolder(new OldBlockData(registryName, blockModelData, blockTagData));
    }

    @Deprecated(forRemoval = true)
    protected BLHolder<Block> createHolder(
        String registryName,
        BlockModelData blockModelData,
        BlockTagData blockTagData,
        Function<Block, LootTable.Builder> lootProvider
    ) {
        return createHolder(new OldBlockData(registryName, blockModelData, blockTagData, lootProvider));
    }

    @Deprecated(forRemoval = true)
    protected BLHolder<Block> createHolder(OldBlockData oldBlockData) {
        var registryName = oldBlockData.registryName();
        var holder = createHolder(registryName, () -> oldBlockData.blockModelData().blockSupplier().get());
        entries.put(registryName, holder);
        DATA_ENTRIES.put(holder, oldBlockData);
        return holder;
    }

    @Deprecated(forRemoval = true)
    protected BlockHolderSet registerBlockHolderSet(BlockHolderSetData blockHolderSetData) {
        var blockData = blockHolderSetData.oldBlockData();
        var properties = blockHolderSetData.properties();
        var holder = createHolder(blockData);
        var slabHolder = createHolder(OldBlockData.toSlab(holder, properties, blockData));
        var stairsHolder = createHolder(OldBlockData.toStairs(holder, properties, blockData));
        var wallHolder = createHolder(OldBlockData.toWall(holder, properties, blockData));
        return new BlockHolderSet(holder, slabHolder, stairsHolder, wallHolder);
    }

    @Override
    public BLHolder<Block> createHolder(String registryName, Supplier<Block> supplier) {
        var holder = Services.BLOCK_SERVICE.createHolder(registryName, supplier);
        entries.put(registryName, holder);
        return holder;
    }

    public BLHolder<Block> tempCreateHolder(String registryName, Supplier<Block> supplier) {
        return Services.BLOCK_SERVICE.createHolder(registryName, supplier);
    }

    @Override
    public final void register() {
        getValues().forEach(Services.BLOCK_SERVICE::register);
    }
}
