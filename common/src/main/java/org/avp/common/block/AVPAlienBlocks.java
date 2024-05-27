package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Set;

import org.avp.api.Holder;
import org.avp.api.block.BlockTagData;
import org.avp.api.block.model.BlockModelData;
import org.avp.api.block.model.render_type.BlockModelRenderType;
import org.avp.common.block.impl.ResinVeinBlock;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPAlienBlocks extends AVPDeferredBlockRegistry {

    public static final AVPAlienBlocks INSTANCE = new AVPAlienBlocks();

    public final Holder<Block> resin;

    public final Holder<Block> resinVeins;

    public final Holder<Block> resinWebbing;

    private AVPAlienBlocks() {
        var resinTagData = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE));

        var resinProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
            .mapColor(MapColor.COLOR_GRAY);

        // FIXME:
        var resinVeinProperties =
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_GRAY)
                .replaceable()
                .noCollission()
                .randomTicks()
                .strength(0.2F)
                .sound(SoundType.VINE)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY);

        resin = createHolder("resin", BlockModelData.cube(resinProperties), resinTagData);
        resinVeins = createHolder(
            "resin_vein",
            BlockModelData.multiface(
                () -> new ResinVeinBlock(resinVeinProperties),
                BlockModelRenderType.TRANSLUCENT
            ),
            resinTagData
        );

        // FIXME: fix model data
        resinWebbing = createHolder("resin_webbing", BlockModelData.cube(resinProperties), resinTagData);
    }
}
