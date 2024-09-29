package org.avp.api.common.data.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.common.registry.AVPDeferredBlockRegistry;
import org.avp.api.common.registry.holder.BLHolder;

public non-sealed class SingleBlockDataContainer extends BlockDataContainer {

    private final String registryName;

    private final Supplier<Block> supplier;

    private final BlockModelData blockModelData;

    private final BlockTagData blockTagData;

    private final Function<Block, LootTable.Builder> lootTableBuilder;

    public SingleBlockDataContainer(
        Supplier<Block> supplier,
        String registryName,
        BlockModelData blockModelData,
        BlockTagData blockTagData,
        Function<Block, LootTable.Builder> lootTableBuilder
    ) {
        this.registryName = registryName;
        this.supplier = supplier;
        this.blockModelData = blockModelData;
        this.blockTagData = blockTagData;
        this.lootTableBuilder = lootTableBuilder;
    }

    public final Holder withHolder() {
        return new Holder(this);
    }

    public final String getRegistryName() {
        return registryName;
    }

    public final Supplier<Block> getSupplier() {
        return supplier;
    }

    public final BlockModelData getBlockModelData() {
        return blockModelData;
    }

    public final BlockTagData getBlockTagData() {
        return blockTagData;
    }

    public final Function<Block, LootTable.Builder> getLootTableBuilder() {
        return lootTableBuilder;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SingleBlockDataContainer that = (SingleBlockDataContainer) o;
        return Objects.equals(registryName, that.registryName);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(registryName);
    }

    public SingleBlockDataContainer extend(String registryName) {
        return new Builder(this, registryName).build();
    }

    public Builder transform(String registryName) {
        return new Builder(this, registryName);
    }

    public static class Holder extends SingleBlockDataContainer implements ItemLike {

        private final BLHolder<Block> holder;

        public Holder(
            Supplier<Block> supplier,
            String registryName,
            BlockModelData blockModelData,
            BlockTagData blockTagData,
            Function<Block, LootTable.Builder> lootTableBuilder
        ) {
            super(supplier, registryName, blockModelData, blockTagData, lootTableBuilder);
            this.holder = AVPDeferredBlockRegistry.INSTANCE.tempCreateHolder(registryName, supplier);
        }

        public Holder(SingleBlockDataContainer singleBlockDataContainer) {
            this(
                singleBlockDataContainer.supplier,
                singleBlockDataContainer.registryName,
                singleBlockDataContainer.blockModelData,
                singleBlockDataContainer.blockTagData,
                singleBlockDataContainer.lootTableBuilder
            );
        }

        public final BLHolder<Block> getHolder() {
            return holder;
        }

        @Override
        public final @NotNull Item asItem() {
            return this.getHolder().get().asItem();
        }
    }

    public static final class Builder {

        private String registryName;

        private Supplier<Block> supplier;

        private BlockModelData blockModelData;

        private BlockTagData blockTagData;

        private Function<Block, LootTable.Builder> lootTableBuilder;

        private Builder(SingleBlockDataContainer singleBlockDataContainer, String registryName) {
            this.registryName = registryName;
            this.supplier = singleBlockDataContainer.getSupplier();
            this.blockModelData = singleBlockDataContainer.getBlockModelData();
            this.blockTagData = singleBlockDataContainer.getBlockTagData();
            this.lootTableBuilder = singleBlockDataContainer.getLootTableBuilder();
        }

        public Builder withBlockTagData(BlockTagData blockTagData) {
            this.blockTagData = blockTagData;
            return this;
        }

        public Builder withModelData(BlockModelData blockModelData) {
            this.blockModelData = blockModelData;
            return this;
        }

        public Builder withSupplier(Supplier<Block> supplier) {
            this.supplier = supplier;
            return this;
        }

        public SingleBlockDataContainer build() {
            return new SingleBlockDataContainer(
                supplier,
                registryName,
                blockModelData,
                blockTagData,
                lootTableBuilder
            );
        }
    }
}
