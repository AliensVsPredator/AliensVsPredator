package com.alien.common.registry.init.block;

import com.alien.common.gameplay.block.resin.IrradiatedResinBlock;
import com.alien.common.gameplay.block.resin.ResinBlock;
import com.alien.common.gameplay.block.resin.node.IrradiatedResinNodeBlock;
import com.alien.common.gameplay.block.resin.node.ResinNodeBlock;
import com.alien.common.gameplay.block.resin.vein.IrradiatedResinVeinBlock;
import com.alien.common.gameplay.block.resin.vein.ResinVeinBlock;
import com.alien.common.gameplay.block.resin.web.IrradiatedResinWebBlock;
import com.alien.common.gameplay.block.resin.web.ResinWebBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;

import com.avp.common.gameplay.block.property.BlockProperties;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.block.AVPBlocks;

public class AlienResinBlocks {

    public static final AVPDeferredHolder<Block> ABERRANT_RESIN = AVPBlocks.register(
        "aberrant_resin",
        () -> new ResinBlock(BlockProperties.ABERRANT_RESIN.build())
    );

    public static final AVPDeferredHolder<Block> ABERRANT_RESIN_STAIRS = AVPBlocks.register(
        "aberrant_resin_stairs",
        () -> new StairBlock(
            ABERRANT_RESIN.get().defaultBlockState(),
            BlockProperties.ABERRANT_RESIN.build()
        )
    );

    public static final AVPDeferredHolder<Block> ABERRANT_RESIN_SLAB = AVPBlocks.register(
        "aberrant_resin_slab",
        () -> new SlabBlock(BlockProperties.ABERRANT_RESIN.build())
    );

    public static final AVPDeferredHolder<Block> ABERRANT_RESIN_NODE = AVPBlocks.register(
        "aberrant_resin_node",
        () -> new ResinNodeBlock(BlockProperties.ABERRANT_RESIN.build())
    );

    public static final AVPDeferredHolder<ResinVeinBlock> ABERRANT_RESIN_VEIN = AVPBlocks.register(
        "aberrant_resin_vein",
        () -> new ResinVeinBlock(BlockProperties.ABERRANT_RESIN_VEIN.build())
    );

    public static final AVPDeferredHolder<Block> ABERRANT_RESIN_WEB = AVPBlocks.register(
        "aberrant_resin_web",
        () -> new ResinWebBlock(BlockProperties.ABERRANT_RESIN_WEB.build())
    );

    public static final AVPDeferredHolder<Block> IRRADIATED_RESIN = AVPBlocks.register(
        "irradiated_resin",
        () -> new IrradiatedResinBlock(BlockProperties.IRRADIATED_RESIN.build())
    );

    public static final AVPDeferredHolder<Block> IRRADIATED_RESIN_STAIRS = AVPBlocks.register(
        "irradiated_resin_stairs",
        () -> new StairBlock(
            IRRADIATED_RESIN.get().defaultBlockState(),
            BlockProperties.IRRADIATED_RESIN.build()
        )
    );

    public static final AVPDeferredHolder<Block> IRRADIATED_RESIN_SLAB = AVPBlocks.register(
        "irradiated_resin_slab",
        () -> new SlabBlock(BlockProperties.IRRADIATED_RESIN.build())
    );

    public static final AVPDeferredHolder<Block> IRRADIATED_RESIN_NODE = AVPBlocks.register(
        "irradiated_resin_node",
        () -> new IrradiatedResinNodeBlock(BlockProperties.IRRADIATED_RESIN.build())
    );

    public static final AVPDeferredHolder<ResinVeinBlock> IRRADIATED_RESIN_VEIN = AVPBlocks.register(
        "irradiated_resin_vein",
        () -> new IrradiatedResinVeinBlock(BlockProperties.IRRADIATED_RESIN_VEIN.build())
    );

    public static final AVPDeferredHolder<Block> IRRADIATED_RESIN_WEB = AVPBlocks.register(
        "irradiated_resin_web",
        () -> new IrradiatedResinWebBlock(BlockProperties.IRRADIATED_RESIN_WEB.build())
    );

    public static final AVPDeferredHolder<Block> NETHER_RESIN = AVPBlocks.register(
        "nether_resin",
        () -> new ResinBlock(BlockProperties.NETHER_RESIN.build())
    );

    public static final AVPDeferredHolder<Block> NETHER_RESIN_STAIRS = AVPBlocks.register(
        "nether_resin_stairs",
        () -> new StairBlock(
            NETHER_RESIN.get().defaultBlockState(),
            BlockProperties.NETHER_RESIN.build()
        )
    );

    public static final AVPDeferredHolder<Block> NETHER_RESIN_SLAB = AVPBlocks.register(
        "nether_resin_slab",
        () -> new SlabBlock(BlockProperties.NETHER_RESIN.build())
    );

    public static final AVPDeferredHolder<Block> NETHER_RESIN_NODE = AVPBlocks.register(
        "nether_resin_node",
        () -> new ResinNodeBlock(BlockProperties.NETHER_RESIN.build())
    );

    public static final AVPDeferredHolder<ResinVeinBlock> NETHER_RESIN_VEIN = AVPBlocks.register(
        "nether_resin_vein",
        () -> new ResinVeinBlock(BlockProperties.NETHER_RESIN_VEIN.build())
    );

    public static final AVPDeferredHolder<Block> NETHER_RESIN_WEB = AVPBlocks.register(
        "nether_resin_web",
        () -> new ResinWebBlock(BlockProperties.NETHER_RESIN_WEB.build())
    );

    public static final AVPDeferredHolder<Block> RESIN = AVPBlocks.register("resin", () -> new ResinBlock(BlockProperties.RESIN.build()));

    public static final AVPDeferredHolder<Block> RESIN_BRICKS = AVPBlocks.register("resin_bricks", BlockProperties.RESIN);

    public static final AVPDeferredHolder<Block> RESIN_BRICK_SLAB = AVPBlocks.register(
        "resin_brick_slab",
        () -> new SlabBlock(BlockProperties.RESIN.build())
    );

    public static final AVPDeferredHolder<Block> RESIN_BRICK_STAIRS = AVPBlocks.register(
        "resin_brick_stairs",
        () -> new StairBlock(
            RESIN_BRICKS.get().defaultBlockState(),
            BlockProperties.RESIN.build()
        )
    );

    public static final AVPDeferredHolder<Block> RESIN_NODE = AVPBlocks.register(
        "resin_node",
        () -> new ResinNodeBlock(BlockProperties.RESIN.build())
    );

    public static final AVPDeferredHolder<Block> RESIN_SLAB = AVPBlocks.register(
        "resin_slab",
        () -> new SlabBlock(BlockProperties.RESIN.build())
    );

    public static final AVPDeferredHolder<Block> RESIN_STAIRS = AVPBlocks.register(
        "resin_stairs",
        () -> new StairBlock(
            RESIN.get().defaultBlockState(),
            BlockProperties.RESIN.build()
        )
    );

    public static final AVPDeferredHolder<ResinVeinBlock> RESIN_VEIN = AVPBlocks.register(
        "resin_vein",
        () -> new ResinVeinBlock(BlockProperties.RESIN_VEIN.build())
    );

    public static final AVPDeferredHolder<Block> RESIN_VENT = AVPBlocks.register("resin_vent", BlockProperties.RESIN);

    public static final AVPDeferredHolder<Block> RESIN_WEB = AVPBlocks.register(
        "resin_web",
        () -> new ResinWebBlock(BlockProperties.RESIN_WEB.build())
    );

    public static final AVPDeferredHolder<Block> RIBBED_RESIN = AVPBlocks.register("ribbed_resin", BlockProperties.RESIN);

    public static final AVPDeferredHolder<Block> SMOOTH_RESIN = AVPBlocks.register("smooth_resin", BlockProperties.RESIN);

    public static void initialize() {}
}
