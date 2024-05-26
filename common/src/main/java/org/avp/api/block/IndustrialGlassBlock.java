package org.avp.api.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.TransparentBlock;
import org.jetbrains.annotations.NotNull;

public class IndustrialGlassBlock extends TransparentBlock implements BeaconBeamBlock {

    private final DyeColor dyeColor;

    public IndustrialGlassBlock(DyeColor dyeColor, Properties properties) {
        super(properties);
        this.dyeColor = dyeColor;
    }

    @Override
    public @NotNull DyeColor getColor() {
        return dyeColor;
    }
}
