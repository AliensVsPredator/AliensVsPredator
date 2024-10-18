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

public class OreSilicaBlockDataContainer extends SingleBlockDataContainer.Holder {

    public static final OreSilicaBlockDataContainer INSTANCE = new OreSilicaBlockDataContainer();

    private static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .strength(2.2F, 1.4F);

    protected OreSilicaBlockDataContainer() {
        super(
            () -> new Block(PROPERTIES),
            "ore_silica",
            BlockModelData.NORMAL_CUBE,
            BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)),
            block -> LootProviders.ORE_VARIABLE.apply(block, AVPItemRegistry.INSTANCE.silica.get(), 3, 5)
        );
    }
}
