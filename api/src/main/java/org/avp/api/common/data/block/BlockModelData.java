package org.avp.api.common.data.block;

import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public record BlockModelData(
    Function<Block, BlockModelDataType> blockModelDataTypeFactory,
    BlockModelRenderType blockModelRenderType
) {

    public static final BlockModelData NORMAL_CUBE = new BlockModelData(BlockModelDataType.Cube::new, BlockModelRenderType.NORMAL);

    public static final BlockModelData TRANSPARENT_CUBE = new BlockModelData(
        BlockModelDataType.Cube::new,
        BlockModelRenderType.TRANSLUCENT
    );
}
