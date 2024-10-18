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

public class OreLithiumBlockDataContainer extends SingleBlockDataContainer.Holder {

    public static final OreLithiumBlockDataContainer INSTANCE = new OreLithiumBlockDataContainer();

    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .strength(4.2F, 5.4F);

    protected OreLithiumBlockDataContainer() {
        super(
            () -> new Block(PROPERTIES),
            "ore_lithium",
            BlockModelData.NORMAL_CUBE,
            BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE)),
            block -> LootProviders.ORE_VARIABLE.apply(block, AVPItemRegistry.INSTANCE.dustLithium.get(), 1, 3)
        );
    }
}
