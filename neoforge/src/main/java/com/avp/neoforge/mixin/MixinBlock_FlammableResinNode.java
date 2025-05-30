package com.avp.neoforge.mixin;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.block.resin.node.ResinNodeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ResinNodeBlock.class)
public class MixinBlock_FlammableResinNode implements IBlockExtension {

    @Override
    public int getFireSpreadSpeed(
        @NotNull BlockState state,
        @NotNull BlockGetter level,
        @NotNull BlockPos pos,
        @NotNull Direction direction
    ) {
        return AlienVariantTypes.getFor(state)
            .isSomeAnd(alienVariantType -> alienVariantType != AlienVariantTypes.NETHER)
                ? 1
                : IBlockExtension.super.getFireSpreadSpeed(state, level, pos, direction);
    }

    @Override
    public int getFlammability(
        @NotNull BlockState state,
        @NotNull BlockGetter level,
        @NotNull BlockPos pos,
        @NotNull Direction direction
    ) {
        return AlienVariantTypes.getFor(state)
            .isSomeAnd(alienVariantType -> alienVariantType != AlienVariantTypes.NETHER)
                ? 20
                : IBlockExtension.super.getFlammability(state, level, pos, direction);
    }
}
