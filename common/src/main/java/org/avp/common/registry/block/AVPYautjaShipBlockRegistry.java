package org.avp.common.registry.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.Set;

import org.avp.api.common.data.block.BlockData;
import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.registry.AVPDeferredBlockRegistry;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.registry.holder.BlockHolderSet;
import org.avp.api.common.registry.holder.BlockHolderSetData;

public class AVPYautjaShipBlockRegistry extends AVPDeferredBlockRegistry {

    public static final AVPYautjaShipBlockRegistry INSTANCE = new AVPYautjaShipBlockRegistry();

    public final BlockHolderSet brick;

    public final BLHolder<Block> decor1;

    public final BLHolder<Block> decor2;

    public final BlockHolderSet decor3;

    public final BLHolder<Block> panel;

    public final BLHolder<Block> supportPillar;

    public final BLHolder<Block> wallBase;

    @Override
    protected BLHolder<Block> createHolder(BlockData blockData) {
        return super.createHolder(blockData.withPrefixRegistryName("yautja_ship_"));
    }

    private AVPYautjaShipBlockRegistry() {
        var properties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
            .mapColor(MapColor.COLOR_RED)
            .strength(75, 1500);
        var decor1Properties = BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_RED)
            .strength(75, 1500)
            .lightLevel(($0) -> 10);
        var decor2Properties = BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_RED)
            .strength(75, 1500)
            .lightLevel(($0) -> 8);
        var panelProperties = BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_RED)
            .strength(75, 1500)
            .lightLevel(($0) -> 6);
        var wallBaseProperties = BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_RED)
            .strength(75, 1500)
            .lightLevel(($0) -> 4);

        var tags = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL));

        var brickBlockData = new BlockData("brick", BlockModelData.cube(properties), tags);
        brick = registerBlockHolderSet(new BlockHolderSetData(properties, brickBlockData));

        decor1 = createHolder("decor_1", BlockModelData.cube(decor1Properties), tags);
        decor2 = createHolder("decor_2", BlockModelData.cube(decor2Properties), tags);

        var decor3BlockData = new BlockData("decor_3", BlockModelData.cube(properties), tags);
        decor3 = registerBlockHolderSet(new BlockHolderSetData(properties, decor3BlockData));

        panel = createHolder("panel", BlockModelData.cube(panelProperties), tags);
        supportPillar = createHolder("support_pillar", BlockModelData.cube(properties), tags);
        wallBase = createHolder("wall_base", BlockModelData.cube(wallBaseProperties), tags);
    }
}
