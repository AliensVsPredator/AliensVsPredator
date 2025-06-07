package com.alien.common.registry.init.block;

import com.avp.common.gameplay.block.property.BlockProperties;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.block.AVPBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;

public class AlienChitinBlocks {

    public static final AVPDeferredHolder<Block> ABERRANT_CHITIN_BLOCK = AVPBlocks.register(
        "aberrant_chitin_block",
        BlockProperties.ABERRANT_CHITIN
    );

    public static final AVPDeferredHolder<Block> ABERRANT_CHITIN_BLOCK_SLAB = AVPBlocks.register(
        "aberrant_chitin_block_slab",
        () -> new SlabBlock(BlockProperties.ABERRANT_CHITIN.build())
    );

    public static final AVPDeferredHolder<Block> ABERRANT_CHITIN_BLOCK_STAIRS = AVPBlocks.register(
        "aberrant_chitin_block_stairs",
        () -> new StairBlock(
            ABERRANT_CHITIN_BLOCK.get().defaultBlockState(),
            BlockProperties.ABERRANT_CHITIN.build()
        )
    );

    public static final AVPDeferredHolder<Block> ABERRANT_CHITIN_BRICKS = AVPBlocks.register(
        "aberrant_chitin_bricks",
        BlockProperties.ABERRANT_CHITIN
    );

    public static final AVPDeferredHolder<Block> ABERRANT_CHITIN_BRICK_SLAB = AVPBlocks.register(
        "aberrant_chitin_brick_slab",
        () -> new SlabBlock(BlockProperties.ABERRANT_CHITIN.build())
    );

    public static final AVPDeferredHolder<Block> ABERRANT_CHITIN_BRICK_STAIRS = AVPBlocks.register(
        "aberrant_chitin_brick_stairs",
        () -> new StairBlock(
            ABERRANT_CHITIN_BRICKS.get().defaultBlockState(),
            BlockProperties.ABERRANT_CHITIN.build()
        )
    );

    public static final AVPDeferredHolder<Block> CHITIN_BLOCK = AVPBlocks.register(
        "chitin_block",
        BlockProperties.CHITIN
    );

    public static final AVPDeferredHolder<Block> CHITIN_BLOCK_SLAB = AVPBlocks.register(
        "chitin_block_slab",
        () -> new SlabBlock(BlockProperties.CHITIN.build())
    );

    public static final AVPDeferredHolder<Block> CHITIN_BLOCK_STAIRS = AVPBlocks.register(
        "chitin_block_stairs",
        () -> new StairBlock(
            CHITIN_BLOCK.get().defaultBlockState(),
            BlockProperties.CHITIN.build()
        )
    );

    public static final AVPDeferredHolder<Block> CHITIN_BRICKS = AVPBlocks.register(
        "chitin_bricks",
        BlockProperties.CHITIN
    );

    public static final AVPDeferredHolder<Block> CHITIN_BRICK_SLAB = AVPBlocks.register(
        "chitin_brick_slab",
        () -> new SlabBlock(BlockProperties.CHITIN.build())
    );

    public static final AVPDeferredHolder<Block> CHITIN_BRICK_STAIRS = AVPBlocks.register(
        "chitin_brick_stairs",
        () -> new StairBlock(
            CHITIN_BRICKS.get().defaultBlockState(),
            BlockProperties.CHITIN.build()
        )
    );

    public static final AVPDeferredHolder<Block> NETHER_CHITIN_BLOCK = AVPBlocks.register(
        "nether_chitin_block",
        BlockProperties.NETHER_CHITIN
    );

    public static final AVPDeferredHolder<Block> NETHER_CHITIN_BLOCK_SLAB = AVPBlocks.register(
        "nether_chitin_block_slab",
        () -> new SlabBlock(BlockProperties.NETHER_CHITIN.build())
    );

    public static final AVPDeferredHolder<Block> NETHER_CHITIN_BLOCK_STAIRS = AVPBlocks.register(
        "nether_chitin_block_stairs",
        () -> new StairBlock(
            NETHER_CHITIN_BLOCK.get().defaultBlockState(),
            BlockProperties.NETHER_CHITIN.build()
        )
    );

    public static final AVPDeferredHolder<Block> NETHER_CHITIN_BRICKS = AVPBlocks.register(
        "nether_chitin_bricks",
        BlockProperties.NETHER_CHITIN
    );

    public static final AVPDeferredHolder<Block> NETHER_CHITIN_BRICK_SLAB = AVPBlocks.register(
        "nether_chitin_brick_slab",
        () -> new SlabBlock(BlockProperties.NETHER_CHITIN.build())
    );

    public static final AVPDeferredHolder<Block> NETHER_CHITIN_BRICK_STAIRS = AVPBlocks.register(
        "nether_chitin_brick_stairs",
        () -> new StairBlock(
            NETHER_CHITIN_BRICKS.get().defaultBlockState(),
            BlockProperties.NETHER_CHITIN.build()
        )
    );

    public static final AVPDeferredHolder<Block> POLISHED_ABERRANT_CHITIN = AVPBlocks.register(
        "polished_aberrant_chitin",
        BlockProperties.ABERRANT_CHITIN
    );

    public static final AVPDeferredHolder<Block> POLISHED_ABERRANT_CHITIN_SLAB = AVPBlocks.register(
        "polished_aberrant_chitin_slab",
        () -> new SlabBlock(BlockProperties.ABERRANT_CHITIN.build())
    );

    public static final AVPDeferredHolder<Block> POLISHED_ABERRANT_CHITIN_STAIRS = AVPBlocks.register(
        "polished_aberrant_chitin_stairs",
        () -> new StairBlock(
            POLISHED_ABERRANT_CHITIN.get().defaultBlockState(),
            BlockProperties.ABERRANT_CHITIN.build()
        )
    );

    public static final AVPDeferredHolder<Block> POLISHED_CHITIN = AVPBlocks.register(
        "polished_chitin",
        BlockProperties.CHITIN
    );

    public static final AVPDeferredHolder<Block> POLISHED_CHITIN_SLAB = AVPBlocks.register(
        "polished_chitin_slab",
        () -> new SlabBlock(BlockProperties.CHITIN.build())
    );

    public static final AVPDeferredHolder<Block> POLISHED_CHITIN_STAIRS = AVPBlocks.register(
        "polished_chitin_stairs",
        () -> new StairBlock(
            POLISHED_CHITIN.get().defaultBlockState(),
            BlockProperties.CHITIN.build()
        )
    );

    public static final AVPDeferredHolder<Block> POLISHED_NETHER_CHITIN = AVPBlocks.register(
        "polished_nether_chitin",
        BlockProperties.NETHER_CHITIN
    );

    public static final AVPDeferredHolder<Block> POLISHED_NETHER_CHITIN_SLAB = AVPBlocks.register(
        "polished_nether_chitin_slab",
        () -> new SlabBlock(BlockProperties.NETHER_CHITIN.build())
    );

    public static final AVPDeferredHolder<Block> POLISHED_NETHER_CHITIN_STAIRS = AVPBlocks.register(
        "polished_nether_chitin_stairs",
        () -> new StairBlock(
            POLISHED_NETHER_CHITIN.get().defaultBlockState(),
            BlockProperties.NETHER_CHITIN.build()
        )
    );

    public static void initialize() {}
}
