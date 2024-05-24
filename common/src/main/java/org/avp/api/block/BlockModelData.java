package org.avp.api.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.avp.api.Holder;
import org.avp.api.block.model.provider.BlockModelProvider;
import org.avp.api.block.model.provider.CubeBlockModelProvider;
import org.avp.api.block.model.provider.FenceBlockModelProvider;
import org.avp.api.block.model.provider.FenceGateBlockModelProvider;
import org.avp.api.block.model.provider.MultifaceBlockModelProvider;
import org.avp.api.block.model.provider.SlabBlockModelProvider;
import org.avp.api.block.model.provider.StairBlockModelProvider;
import org.avp.api.block.model.provider.WallBlockModelProvider;
import org.avp.api.block.model.provider.WoodBlockModelProvider;
import org.avp.api.block.model.render_type.BlockModelRenderType;
import org.avp.common.service.Services;

import java.util.function.Function;
import java.util.function.Supplier;

public record BlockModelData(
    Supplier<Block> blockSupplier,
    Function<Block, BlockModelProvider> blockModelProvider,
    BlockModelRenderType blockModelRenderType
) {

    public static BlockModelData cube() {
        return cube(BlockBehaviour.Properties.of());
    }

    public static BlockModelData cube(
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new Block(properties),
            CubeBlockModelProvider::new,
            BlockModelRenderType.NORMAL
        );
    }

    public static BlockModelData fence(Holder<Block> parentHolder) {
        return fence(parentHolder, BlockBehaviour.Properties.of());
    }

    public static BlockModelData fence(
        Holder<Block> parentHolder,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new FenceBlock(properties),
            block -> new FenceBlockModelProvider(parentHolder.get(), block),
            BlockModelRenderType.NORMAL
        );
    }

    public static BlockModelData fenceGate(Holder<Block> parentHolder, WoodType woodType) {
        return fenceGate(parentHolder, woodType, BlockBehaviour.Properties.of());
    }

    public static BlockModelData fenceGate(
        Holder<Block> parentHolder,
        WoodType woodType,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new FenceGateBlock(woodType, properties),
            block -> new FenceGateBlockModelProvider(block, parentHolder.get()),
            BlockModelRenderType.NORMAL
        );
    }

    public static BlockModelData multiface() {
        return multiface(BlockBehaviour.Properties.of());
    }

    public static BlockModelData multiface(
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new Block(properties), // FIXME: Ensure this is correct?
            MultifaceBlockModelProvider::new,
            BlockModelRenderType.NORMAL
        );
    }

    public static BlockModelData slab(Holder<Block> parentHolder) {
        return slab(parentHolder, BlockBehaviour.Properties.of());
    }

    public static BlockModelData slab(
        Holder<Block> parentHolder,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new SlabBlock(properties),
            block -> new SlabBlockModelProvider(parentHolder.get(), block),
            BlockModelRenderType.NORMAL
        );
    }

    public static BlockModelData stairs(Holder<Block> parentHolder) {
        return stairs(parentHolder, BlockBehaviour.Properties.of());
    }

    public static BlockModelData stairs(
        Holder<Block> parentHolder,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> Services.BLOCK_SERVICE.createStairBlock(parentHolder, properties),
            block -> new StairBlockModelProvider(parentHolder.get(), block),
            BlockModelRenderType.NORMAL
        );
    }

    public static BlockModelData wall(
        Holder<Block> parentHolder,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new WallBlock(properties),
            block -> new WallBlockModelProvider(parentHolder.get(), block),
            BlockModelRenderType.NORMAL
        );
    }

    public static BlockModelData wood(Holder<Block> parentHolder) {
        return wood(parentHolder, BlockBehaviour.Properties.of());
    }

    public static BlockModelData wood(
        Holder<Block> parentHolder,
        BlockBehaviour.Properties properties
    ) {
        return new BlockModelData(
            () -> new RotatedPillarBlock(properties),
            block -> new WoodBlockModelProvider(parentHolder.get(), block),
            BlockModelRenderType.NORMAL
        );
    }
}
