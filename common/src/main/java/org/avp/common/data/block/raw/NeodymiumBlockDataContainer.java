package org.avp.common.data.block.raw;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.data.block.ore.OreMonaziteBlockDataContainer;

public class NeodymiumBlockDataContainer extends SingleBlockDataContainer.Holder {

    public static final NeodymiumBlockDataContainer INSTANCE = new NeodymiumBlockDataContainer();

    protected NeodymiumBlockDataContainer() {
        super(
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(OreMonaziteBlockDataContainer.INSTANCE.getHolder().get())),
            "neodymium_block",
            BlockModelData.NORMAL_CUBE,
            BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)),
            LootProviders.SELF
        );
    }
}
