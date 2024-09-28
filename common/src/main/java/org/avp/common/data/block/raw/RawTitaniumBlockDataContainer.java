package org.avp.common.data.block.raw;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.data.block.ore.OreTitaniumBlockDataContainer;

import java.util.Set;

public class RawTitaniumBlockDataContainer extends SingleBlockDataContainer.Holder {

    public static final RawTitaniumBlockDataContainer INSTANCE = new RawTitaniumBlockDataContainer();

    protected RawTitaniumBlockDataContainer() {
        super(
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(OreTitaniumBlockDataContainer.INSTANCE.getHolder().get())),
            "raw_titanium_block",
            BlockModelData.NORMAL_CUBE,
            BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)),
            LootProviders.SELF
        );
    }
}
