package org.avp.common.data.block.ore;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.SingleBlockDataContainer;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.common.registry.item.AVPItemRegistry;

public class OreMonaziteBlockDataContainer extends SingleBlockDataContainer.Holder {

    public static final OreMonaziteBlockDataContainer INSTANCE = new OreMonaziteBlockDataContainer();

    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .strength(45F, 15F);

    protected OreMonaziteBlockDataContainer() {
        super(
            () -> new Block(PROPERTIES),
            "ore_monazite",
            BlockModelData.NORMAL_CUBE,
            BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL)),
            block -> LootProviders.ORE.apply(block, AVPItemRegistry.INSTANCE.neodymium.get())
        );
    }
}
