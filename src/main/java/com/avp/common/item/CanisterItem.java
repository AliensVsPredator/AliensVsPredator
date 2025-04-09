package com.avp.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Most of the code were copied from BucketItem
public class CanisterItem extends Item implements DispensibleContainerItem {
    private final Fluid content;

    public CanisterItem(Fluid content, Properties properties) {
        super(properties);
        this.content = content;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack canisterStack = player.getItemInHand(usedHand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);

        if (blockHitResult.getType() == HitResult.Type.MISS || blockHitResult.getType() != HitResult.Type.BLOCK)
            return InteractionResultHolder.pass(canisterStack);

        BlockPos blockHitPos = blockHitResult.getBlockPos();
        Direction blockHitDirection = blockHitResult.getDirection();
        BlockPos blockHitRelative = blockHitPos.relative(blockHitDirection);

        if (!level.mayInteract(player, blockHitPos) || !player.mayUseItemAt(blockHitRelative, blockHitDirection, canisterStack))
            return InteractionResultHolder.fail(canisterStack);

        BlockState blockHitState = level.getBlockState(blockHitPos);

        if (this.content == Fluids.EMPTY && blockHitState.getBlock() instanceof BucketPickup bucketPickup) {
            ItemStack filledCanisterStack = pickupBlock(player, level, bucketPickup, blockHitPos, blockHitState);

            if (!filledCanisterStack.isEmpty()) {
                player.awardStat(Stats.ITEM_USED.get(this));
                bucketPickup.getPickupSound().ifPresent(soundEvent -> player.playSound(soundEvent, 1.0F, 1.0F));
                level.gameEvent(player, GameEvent.FLUID_PICKUP, blockHitPos);

                ItemStack filledCanisterResult = ItemUtils.createFilledResult(canisterStack, player, filledCanisterStack);

                if (!level.isClientSide)
                    CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, filledCanisterStack);

                return InteractionResultHolder.sidedSuccess(filledCanisterResult, level.isClientSide());
            }

        }
        else {
            BlockPos blockPos3 = blockHitState.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER ? blockHitPos : blockHitRelative;

            if (this.emptyContents(player, level, blockPos3, blockHitResult)) {
                this.checkExtraContent(player, level, canisterStack, blockPos3);

                if (player instanceof ServerPlayer)
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockPos3, canisterStack);

                player.awardStat(Stats.ITEM_USED.get(this));

                ItemStack filledCanisterResult = ItemUtils.createFilledResult(canisterStack, player, getEmptySuccessItem(canisterStack, player));

                return InteractionResultHolder.sidedSuccess(filledCanisterResult, level.isClientSide());
            }
            else return InteractionResultHolder.fail(canisterStack);
        }

        return super.use(level, player, usedHand);
    }

    private ItemStack pickupBlock(Player player, Level level, BucketPickup bucketPickup, BlockPos blockPos, BlockState blockState) {
        ItemStack bucketItem = bucketPickup.pickupBlock(player, level, blockPos, blockState);

        if (bucketItem.is(Items.WATER_BUCKET))
            return new ItemStack(AVPItems.WATER_CANISTER);

        else if (bucketItem.is(Items.LAVA_BUCKET))
            return new ItemStack(AVPItems.LAVA_CANISTER);

        return bucketItem;
    }

    public static ItemStack getEmptySuccessItem(ItemStack canisterStack, Player player) {
        return !player.hasInfiniteMaterials() ? new ItemStack(AVPItems.CANISTER) : canisterStack;
    }

    @Override
    public boolean emptyContents(@Nullable Player player, Level level, BlockPos pos, @Nullable BlockHitResult result) {
        if (!(this.content instanceof FlowingFluid flowingFluid)) return false;

        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();
        boolean isReplaceable = blockState.canBeReplaced(this.content);

        boolean canEmptyHere;
        if (!blockState.isAir() && !isReplaceable)
            canEmptyHere = block instanceof LiquidBlockContainer liquidBlockContainer && liquidBlockContainer.canPlaceLiquid(player, level, pos, blockState, this.content);
        else
            canEmptyHere = true;

        if (!canEmptyHere)
            return result != null && this.emptyContents(player, level, result.getBlockPos().relative(result.getDirection()), null);

        if (level.dimensionType().ultraWarm() && this.content.is(FluidTags.WATER)) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

            for (int i = 0; i < 8; i++) {
                level.addParticle(
                        ParticleTypes.LARGE_SMOKE,
                        x + Math.random(), y + Math.random(), z + Math.random(),
                        0.0, 0.0, 0.0
                );
            }
            return true;
        }

        if (block instanceof LiquidBlockContainer liquidBlockContainer && this.content == Fluids.WATER) {
            liquidBlockContainer.placeLiquid(level, pos, blockState, flowingFluid.getSource(false));
            this.playEmptySound(player, level, pos);
            return true;
        }

        if (!level.isClientSide && isReplaceable && !blockState.liquid())
            level.destroyBlock(pos, true);

        if (!level.setBlock(pos, this.content.defaultFluidState().createLegacyBlock(), 11) && !blockState.getFluidState().isSource()) {
            return false;

        } else {
            this.playEmptySound(player, level, pos);
            return true;
        }
    }

    protected void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos pos) {
        SoundEvent soundEvent = this.content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        level.playSound(player, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
    }
}
