package com.avp.common.block.resin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import com.avp.common.entity.living.alien.AlienVariantTypes;

class ResinVeinSpreaderConfig extends MultifaceSpreader.DefaultSpreaderConfig {

    private final MultifaceSpreader.SpreadType[] spreadTypes;

    public ResinVeinSpreaderConfig(ResinVeinBlock resinVeinBlock, final MultifaceSpreader.SpreadType... spreadTypes) {
        super(resinVeinBlock);
        this.spreadTypes = spreadTypes;
    }

    @Override
    public boolean stateCanBeReplaced(
        @NotNull BlockGetter blockGetter,
        @NotNull BlockPos sourcePos,
        @NotNull BlockPos destinationPos,
        @NotNull Direction direction,
        @NotNull BlockState destinationBlockState
    ) {
        var alienVariantType = AlienVariantTypes.getForOrNull(block);

        if (alienVariantType == null) {
            // 'block' should always be a resin vein block, and therefore always have a variant type.
            return false;
        }

        // I don't normally leave commented-out code, but reverse-engineering this vein spreading was such a PITA
        // that I don't really want to forget how to do this later on.
        // var destinationSupportBlockPos = destinationPos.relative(direction);
        // var destinationSupportBlockState = blockGetter.getBlockState(destinationSupportBlockPos);

        // If the destination block state is replaceable for this variant (ex. enemy variant resin veins)...
        var canDestinationBlockStateBeReplaced = destinationBlockState.is(alienVariantType.resinReplaceableTag())
            // OR the state is air (in which case it's free real-estate)
            || destinationBlockState.isAir()
            // OR the state is a matching resin vein variant (allows same-space spreading, veins occupy multiple faces).
            || (destinationBlockState.is(alienVariantType.resinVein().get()));

        if (!canDestinationBlockStateBeReplaced) {
            return false;
        }

        if (sourcePos.distManhattan(destinationPos) == 2) {
            var blockPos3 = sourcePos.relative(direction.getOpposite());

            if (blockGetter.getBlockState(blockPos3).isFaceSturdy(blockGetter, blockPos3, direction)) {
                return false;
            }
        }

        var fluidState = destinationBlockState.getFluidState();

        if ((!fluidState.isEmpty() && !fluidState.is(Fluids.WATER)) || destinationBlockState.is(BlockTags.FIRE)) {
            return false;
        } else {
            return destinationBlockState.canBeReplaced() || super.stateCanBeReplaced(
                blockGetter,
                sourcePos,
                destinationPos,
                direction,
                destinationBlockState
            );
        }
    }

    @Override
    public MultifaceSpreader.SpreadType @NotNull [] getSpreadTypes() {
        return this.spreadTypes;
    }

    @Override
    public boolean isOtherBlockValidAsSource(@NotNull BlockState blockState) {
        var alienVariantType = AlienVariantTypes.getForOrNull(blockState);
        return alienVariantType == null || !blockState.is(alienVariantType.resinVein().get());
    }
}
