package com.alien.common.registry.init;

import com.alien.common.gameplay.block.resin.IrradiatedResinBlock;
import com.alien.common.gameplay.block.resin.ResinBlock;
import com.alien.common.gameplay.block.resin.node.IrradiatedResinNodeBlock;
import com.alien.common.gameplay.block.resin.node.ResinNodeBlock;
import com.alien.common.gameplay.block.resin.vein.IrradiatedResinVeinBlock;
import com.alien.common.gameplay.block.resin.vein.ResinVeinBlock;
import com.alien.common.gameplay.block.resin.web.IrradiatedResinWebBlock;
import com.alien.common.gameplay.block.resin.web.ResinWebBlock;
import net.minecraft.world.level.block.Block;

import com.avp.common.gameplay.block.property.BlockProperties;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.AVPBlocks;

public class AlienBlocks {

    public static final AVPDeferredHolder<Block> ABERRANT_RESIN = AVPBlocks.register(
        "aberrant_resin",
        () -> new ResinBlock(BlockProperties.ABERRANT_RESIN.build())
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

    public static final AVPDeferredHolder<Block> RESIN_BRICKS = AVPBlocks.register("resin_bricks", BlockProperties.BRASS);

    public static final AVPDeferredHolder<Block> RESIN_NODE = AVPBlocks.register(
        "resin_node",
        () -> new ResinNodeBlock(BlockProperties.RESIN.build())
    );

    public static final AVPDeferredHolder<Block> RESIN_O = AVPBlocks.register("resin_o", BlockProperties.BRASS);

    public static final AVPDeferredHolder<Block> RESIN_RIBBED = AVPBlocks.register("resin_ribbed", BlockProperties.BRASS);

    public static final AVPDeferredHolder<Block> RESIN_SMOOTH = AVPBlocks.register("resin_smooth", BlockProperties.BRASS);

    public static final AVPDeferredHolder<ResinVeinBlock> RESIN_VEIN = AVPBlocks.register(
        "resin_vein",
        () -> new ResinVeinBlock(BlockProperties.RESIN_VEIN.build())
    );

    public static final AVPDeferredHolder<Block> RESIN_WEB = AVPBlocks.register(
        "resin_web",
        () -> new ResinWebBlock(BlockProperties.RESIN_WEB.build())
    );

    public static final AVPDeferredHolder<Block> ROYAL_JELLY_BLOCK = AVPBlocks.register("royal_jelly_block", BlockProperties.JELLY);

    public static void initialize() {}
}
