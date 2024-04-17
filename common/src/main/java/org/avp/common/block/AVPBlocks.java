package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.GameObject;
import org.avp.api.Tuple;
import org.avp.api.block.BlockData;
import org.avp.common.registry.AVPDeferredBlockRegistry;
import org.avp.common.service.Services;

public class AVPBlocks extends AVPDeferredBlockRegistry {

    public static final AVPBlocks INSTANCE = new AVPBlocks();

    public final BlockBehaviour.Properties ALUMINUM_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.IRON_BLOCK
    );

    public final BlockBehaviour.Properties TITANIUM_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.IRON_BLOCK
    );

    public final GameObject<Block> ALUMINUM_BLOCK;

    public final GameObject<Block> TITANIUM_BLOCK;

    private AVPBlocks() {
        super();
        var aluminum = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL);
        var titanium = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

        ALUMINUM_BLOCK = createHolder("aluminum_block", BlockData.simple(ALUMINUM_PROPERTIES).tags(aluminum));
        TITANIUM_BLOCK = createHolder("titanium_block", BlockData.simple(TITANIUM_PROPERTIES).tags(titanium));
    }
}
