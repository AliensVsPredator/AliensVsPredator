package org.avp.api.common.data.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.service.Services;

public record BlockModelData(
    Supplier<Block> blockSupplier,
    Function<Block, BlockModelDataType> blockModelDataTypeFactory,
    BlockModelRenderType blockModelRenderType
) {

    public static final BlockModelData NORMAL_CUBE = new BlockModelData(BlockModelDataType.Cube::new, BlockModelRenderType.NORMAL);

    public static final BlockModelData TRANSPARENT_CUBE = new BlockModelData(
        BlockModelDataType.Cube::new,
        BlockModelRenderType.TRANSLUCENT
    );

    public BlockModelData(
        Function<Block, BlockModelDataType> blockModelDataTypeFactory,
        BlockModelRenderType blockModelRenderType
    ) {
        this(() -> {
            throw new UnsupportedOperationException("This supplier should not be used.");
        }, blockModelDataTypeFactory, blockModelRenderType);
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData cube() {
        return cube(BlockBehaviour.Properties.of());
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData cube(
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new Block(properties),
            BlockModelDataType.Cube::new,
            BlockModelRenderType.NORMAL
        );
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData fence(BLHolder<Block> parentHolder) {
        return fence(parentHolder, BlockBehaviour.Properties.of());
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData fence(
        BLHolder<Block> parentHolder,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new FenceBlock(properties),
            block -> new BlockModelDataType.Fence(parentHolder.get(), block),
            BlockModelRenderType.NORMAL
        );
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData fenceGate(BLHolder<Block> parentHolder, WoodType woodType) {
        return fenceGate(parentHolder, woodType, BlockBehaviour.Properties.of());
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData fenceGate(
        BLHolder<Block> parentHolder,
        WoodType woodType,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new FenceGateBlock(woodType, properties),
            block -> new BlockModelDataType.FenceGate(parentHolder.get(), block),
            BlockModelRenderType.NORMAL
        );
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData grass(BLHolder<Block> parentHolder) {
        return grass(parentHolder, BlockBehaviour.Properties.of());
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData grass(
        BLHolder<Block> parentHolder,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new GrassBlock(properties),
            block -> new BlockModelDataType.GrassLike(parentHolder.get(), block),
            BlockModelRenderType.NORMAL
        );
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData multiface(Supplier<Block> blockSupplier) {
        return multiface(blockSupplier, BlockModelRenderType.NORMAL);
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData multiface(
        Supplier<Block> blockSupplier,
        BlockModelRenderType blockModelRenderType
    ) {
        return new BlockModelData(
            blockSupplier,
            BlockModelDataType.MultiFace::new,
            blockModelRenderType
        );
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData rotatedPillar() {
        return rotatedPillar(BlockBehaviour.Properties.of());
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData rotatedPillar(
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new RotatedPillarBlock(properties),
            BlockModelDataType.RotatedPillar::new,
            BlockModelRenderType.NORMAL
        );
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData slab(BLHolder<Block> parentHolder) {
        return slab(parentHolder, BlockBehaviour.Properties.of());
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData slab(
        BLHolder<Block> parentHolder,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new SlabBlock(properties),
            block -> new BlockModelDataType.Slab(parentHolder.get(), block),
            BlockModelRenderType.NORMAL
        );
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData stairs(BLHolder<Block> parentHolder) {
        return stairs(parentHolder, BlockBehaviour.Properties.of());
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData stairs(
        BLHolder<Block> parentHolder,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> Services.BLOCK_SERVICE.createStairBlock(parentHolder, properties),
            block -> new BlockModelDataType.Stair(parentHolder.get(), block),
            BlockModelRenderType.NORMAL
        );
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData wall(
        BLHolder<Block> parentHolder,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new WallBlock(properties),
            block -> new BlockModelDataType.Wall(parentHolder.get(), block),
            BlockModelRenderType.NORMAL
        );
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData wood(BLHolder<Block> parentHolder) {
        return wood(parentHolder, BlockBehaviour.Properties.of());
    }

    @Deprecated(forRemoval = true)
    public static BlockModelData wood(
        BLHolder<Block> parentHolder,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new RotatedPillarBlock(properties),
            block -> new BlockModelDataType.Wood(parentHolder.get(), block),
            BlockModelRenderType.NORMAL
        );
    }
}
