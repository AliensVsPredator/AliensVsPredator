package com.avp.common.registry.init.block;

import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.avp.common.gameplay.block.AshBlock;
import com.avp.common.gameplay.block.LithiumBlock;
import com.avp.common.gameplay.block.RadiatedBlock;
import com.avp.common.gameplay.block.property.BlockProperties;
import com.avp.common.registry.AVPDeferredHolder;

public class CoreBlocks {

    public static final AVPDeferredHolder<Block> ALUMINUM_BLOCK = AVPBlocks.register("aluminum_block", BlockProperties.ALUMINUM);

    public static final AVPDeferredHolder<Block> ASH_BLOCK = AVPBlocks.register(
        "ash_block",
        () -> new AshBlock(BlockProperties.ASH_BLOCK.build())
    );

    public static final AVPDeferredHolder<Block> AUTUNITE_BLOCK = AVPBlocks.register(
        "autunite_block",
        () -> new RadiatedBlock(BlockProperties.AUTUNITE_ORE.build())
    );

    public static final AVPDeferredHolder<Block> AUTUNITE_ORE = AVPBlocks.register(
        "autunite_ore",
        () -> new RadiatedBlock(BlockProperties.AUTUNITE_ORE.build())
    );

    public static final AVPDeferredHolder<Block> BAUXITE_ORE = AVPBlocks.register("bauxite_ore", BlockProperties.BAUXITE_ORE);

    public static final AVPDeferredHolder<Block> BRASS_BLOCK = AVPBlocks.register("brass_block", BlockProperties.BRASS);

    public static final AVPDeferredHolder<Block> DEEPSLATE_TITANIUM_ORE = AVPBlocks.register(
        "deepslate_titanium_ore",
        BlockProperties.DEEPSLATE_TITANIUM_ORE
    );

    public static final AVPDeferredHolder<Block> DEEPSLATE_ZINC_ORE = AVPBlocks.register(
        "deepslate_zinc_ore",
        BlockProperties.DEEPSLATE_ZINC_ORE
    );

    public static final AVPDeferredHolder<Block> GALENA_ORE = AVPBlocks.register("galena_ore", BlockProperties.GALENA_ORE);

    public static final AVPDeferredHolder<Block> LEAD_BLOCK = AVPBlocks.register("lead_block", BlockProperties.LEAD);

    public static final AVPDeferredHolder<Block> LITHIUM_BLOCK = AVPBlocks.register(
        "lithium_block",
        () -> new LithiumBlock(BlockProperties.LITHIUM_ORE.build())
    );

    public static final AVPDeferredHolder<Block> LITHIUM_ORE = AVPBlocks.register(
        "lithium_ore",
        () -> new LithiumBlock(BlockProperties.LITHIUM_ORE.build())
    );

    public static final AVPDeferredHolder<Block> MONAZITE_ORE = AVPBlocks.register("monazite_ore", BlockProperties.MONAZITE_ORE);

    public static final AVPDeferredHolder<Block> RAW_BAUXITE_BLOCK = AVPBlocks.register("raw_bauxite_block", BlockProperties.BAUXITE_ORE);

    public static final AVPDeferredHolder<Block> RAW_GALENA_BLOCK = AVPBlocks.register("raw_galena_block", BlockProperties.GALENA_ORE);

    public static final AVPDeferredHolder<Block> RAW_MONAZITE_BLOCK = AVPBlocks.register(
        "raw_monazite_block",
        BlockProperties.MONAZITE_ORE
    );

    public static final AVPDeferredHolder<Block> RAW_TITANIUM_BLOCK = AVPBlocks.register(
        "raw_titanium_block",
        BlockProperties.TITANIUM_ORE
    );

    public static final AVPDeferredHolder<Block> RAW_ZINC_BLOCK = AVPBlocks.register("raw_zinc_block", BlockProperties.ZINC_ORE);

    public static final AVPDeferredHolder<Block> SILICA_GRAVEL = AVPBlocks.register(
        "silica_gravel",
        () -> new ColoredFallingBlock(new ColorRGBA(-8356741), BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL))
    );

    public static final AVPDeferredHolder<Block> SILICON_BLOCK = AVPBlocks.register(
        // TODO: Change this to "silicon_block" with 0.2.0.
        "raw_silica_block",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL).strength(0.9F))
    );

    public static final AVPDeferredHolder<Block> TRINITITE_BLOCK = AVPBlocks.register(
        "trinitite_block",
        () -> new RadiatedBlock(BlockProperties.TRINITITE.build())
    );

    public static final AVPDeferredHolder<Block> URANIUM_BLOCK = AVPBlocks.register(
        "uranium_block",
        () -> new RadiatedBlock(BlockProperties.URANIUM.build())
    );

    public static final AVPDeferredHolder<Block> ZINC_BLOCK = AVPBlocks.register("zinc_block", BlockProperties.ZINC);

    public static final AVPDeferredHolder<Block> ZINC_ORE = AVPBlocks.register("zinc_ore", BlockProperties.ZINC_ORE);

    public static void initialize() {}
}
