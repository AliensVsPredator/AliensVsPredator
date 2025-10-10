package com.avp.fabric.data.tag;

import com.human.common.registry.init.block.HumanPlasticBlocks;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TagProviderUtil {

    static @NotNull Stream<Block> getPlasticBlockStream() {
        return Stream.of(
            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC,
            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB,
            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS,
            HumanPlasticBlocks.DYE_COLOR_TO_FRAMED_PLASTIC,
            HumanPlasticBlocks.DYE_COLOR_TO_PITTED_PLASTIC,
            HumanPlasticBlocks.DYE_COLOR_TO_PITTED_PLASTIC_SLAB,
            HumanPlasticBlocks.DYE_COLOR_TO_PITTED_PLASTIC_STAIRS,
            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC,
            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_GRATE,
            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_GRATE_SLAB,
            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_GRATE_STAIRS,
            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_SLAB,
            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_STAIRS
        )
            .map(Map::values)
            .flatMap(Collection::stream)
            .map(Supplier::get);
    }
}
