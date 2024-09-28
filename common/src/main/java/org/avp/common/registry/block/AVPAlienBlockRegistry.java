package org.avp.common.registry.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockModelRenderType;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.registry.AVPDeferredBlockRegistry;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.game.block.ResinVeinBlock;

@Deprecated(forRemoval = true)
public class AVPAlienBlockRegistry extends AVPDeferredBlockRegistry {

    public static final AVPAlienBlockRegistry INSTANCE = new AVPAlienBlockRegistry();

    public final BLHolder<Block> resin;

    public final BLHolder<Block> resinVeins;

    public final BLHolder<Block> resinWebbing;

    private AVPAlienBlockRegistry() {
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
