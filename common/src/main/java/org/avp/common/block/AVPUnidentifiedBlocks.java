package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.api.block.BlockData;

public class AVPUnidentifiedBlocks {

    public static final BlockBehaviour.Properties STONE_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.STONE
    )
        .mapColor(MapColor.COLOR_GRAY);

    public static final GameObject<Block> DIRT;

    public static final GameObject<Block> GRAVEL;

    public static final GameObject<Block> ROCK;

    public static final GameObject<Block> SAND;

    public static final GameObject<Block> STONE;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static GameObject<Block> register(String name, BlockData.Builder builder) {
        return AVPBlocks.register("unidentified_" + name, builder);
    }

    private AVPUnidentifiedBlocks() {}

    static {
        var stone = List.of(BlockTags.MINEABLE_WITH_PICKAXE);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(STONE_PROPERTIES).tags(stone);

        DIRT = register("dirt", pickProps.get());
        GRAVEL = register("gravel", pickProps.get());
        ROCK = register("rock", pickProps.get());
        SAND = register("sand", pickProps.get());
        STONE = register("stone", pickProps.get());
    }
}
