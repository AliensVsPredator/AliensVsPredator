package org.avp.api.block.factory;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.avp.api.block.CustomTransparentBlock;

public class CustomTransparentBlockFactory implements BlockFactory {

    private final DyeColor dyeColor;

    public CustomTransparentBlockFactory(DyeColor dyeColor) {
        this.dyeColor = dyeColor;
    }

    @Override
    public Block create(BlockBehaviour.Properties properties) {
        return new CustomTransparentBlock(dyeColor, properties);
    }
}
