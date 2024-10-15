package org.avp.common.data.block.raw;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.data.block.ore.OreLithiumBlockDataContainer;

public class RawLithiumBlockDataContainer extends SingleBlockDataContainer.Holder {

    public static final RawLithiumBlockDataContainer INSTANCE = new RawLithiumBlockDataContainer();

    protected RawLithiumBlockDataContainer() {
        super(
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(OreLithiumBlockDataContainer.INSTANCE.getHolder().get())),
            "lithium_block",
            BlockModelData.NORMAL_CUBE,
            BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE)),
            LootProviders.SELF
        );
    }
}
