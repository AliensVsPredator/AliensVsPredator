package org.avp.common.data.block.raw;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.data.block.ore.OreBauxiteBlockDataContainer;

public class RawBauxiteBlockDataContainer extends SingleBlockDataContainer.Holder {

    public static final RawBauxiteBlockDataContainer INSTANCE = new RawBauxiteBlockDataContainer();

    protected RawBauxiteBlockDataContainer() {
        super(
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(OreBauxiteBlockDataContainer.INSTANCE.getHolder().get())),
            "raw_bauxite_block",
            BlockModelData.NORMAL_CUBE,
            BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL)),
            LootProviders.SELF
        );
    }
}
