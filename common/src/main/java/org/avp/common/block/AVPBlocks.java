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
import org.avp.common.service.Services;

public class AVPBlocks {

    private static final List<Tuple<GameObject<Block>, BlockData>> ENTRIES = new ArrayList<>();

    public static final BlockBehaviour.Properties ALUMINUM_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.IRON_BLOCK
    );

    public static final BlockBehaviour.Properties TITANIUM_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.IRON_BLOCK
    );

    public static final GameObject<Block> ALUMINUM_BLOCK;

    public static final GameObject<Block> TITANIUM_BLOCK;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    public static List<Tuple<GameObject<Block>, BlockData>> getEntries() {
        return ENTRIES;
    }

    protected static GameObject<Block> register(String registryName, BlockData.Builder blockDataBuilder, boolean addEntry) {
        var blockData = blockDataBuilder.build();
        var gameObject = Services.BLOCK_REGISTRY.register(registryName, blockData::create);
        if (addEntry) {
            ENTRIES.add(new Tuple<>(gameObject, blockData));
        }
        return gameObject;
    }

    protected static GameObject<Block> register(String registryName, BlockData.Builder blockDataBuilder) {
        return register(registryName, blockDataBuilder, true);
    }

    private AVPBlocks() {}

    static {
        var aluminum = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL);
        var titanium = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

        ALUMINUM_BLOCK = register("aluminum_block", BlockData.simple(ALUMINUM_PROPERTIES).tags(aluminum));
        TITANIUM_BLOCK = register("titanium_block", BlockData.simple(TITANIUM_PROPERTIES).tags(titanium));
    }
}
