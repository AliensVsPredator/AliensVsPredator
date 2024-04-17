package org.avp.api.block.factory;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.function.Function;

import org.avp.api.Holder;
import org.avp.api.block.TransparentBlock;

public class BlockFactories {

    public static final BlockFactory CUBE = Block::new;

    public static final BlockFactory FENCE = FenceBlock::new;

    public static final Function<WoodType, BlockFactory> FENCE_GATE = FenceGateBlockFactory::new;

    public static final BlockFactory GRASS = GrassBlock::new;

    public static final BlockFactory ROTATED_PILLAR = RotatedPillarBlock::new;

    public static final BlockFactory ROTATED_VARIANT = Block::new;

    public static final BlockFactory SLAB = SlabBlock::new;

    public static final Function<Holder<Block>, BlockFactory> STAIRS = StairBlockFactory::new;

    public static final BlockFactory TRANSPARENT = TransparentBlock::new;

    public static final BlockFactory WALL = WallBlock::new;

    public static final BlockFactory WOOD = RotatedPillarBlock::new;

    private BlockFactories() {
        throw new UnsupportedOperationException();
    }
}
