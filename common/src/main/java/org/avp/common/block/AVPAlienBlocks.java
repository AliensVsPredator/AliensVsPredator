package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.avp.api.GameObject;
import org.avp.api.block.BlockData;

import java.util.List;
import java.util.function.Supplier;

public class AVPAlienBlocks {

    public static final BlockBehaviour.Properties RESIN_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.STONE
    )
        .mapColor(MapColor.COLOR_GRAY);

    public static final GameObject<Block> RESIN;
    public static final GameObject<Block> RESIN_VEINS;
    public static final GameObject<Block> RESIN_WEBBING;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static GameObject<Block> register(String name, BlockData.Builder builder) {
        return AVPBlocks.register(name, builder);
    }

    private AVPAlienBlocks() {}

    static {
        var resin = List.of(BlockTags.MINEABLE_WITH_PICKAXE);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(RESIN_PROPERTIES).tags(resin);

        RESIN = register("resin", pickProps.get());
        RESIN_VEINS = register("resin_veins", pickProps.get());
        RESIN_WEBBING = register("resin_webbing", pickProps.get());
    }
}
