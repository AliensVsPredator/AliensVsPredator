package com.avp.common.item;

import com.avp.common.component.DataComponents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
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

import java.util.List;

public class CanisterItem extends Item implements DispensibleContainerItem {
    public final Fluid content;

    public static final int MAX_CONTENT_AMOUNT = 8;

    public CanisterItem(Fluid content, Properties properties) {
        super(properties);
        this.content = content;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int currentContentAmount = stack.getOrDefault(DataComponents.CANISTER_CONTENT_AMOUNT, 0);
        if (currentContentAmount == 0) return;

        tooltipComponents.add(Component.literal("Content Amount: " + currentContentAmount));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack canisterStack = player.getItemInHand(usedHand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, isFluidPlacementAction(player) ? ClipContext.Fluid.NONE : ClipContext.Fluid.SOURCE_ONLY);

        if (isInvalidHitResult(hitResult))
            return InteractionResultHolder.pass(canisterStack);

        BlockPos hitPos = hitResult.getBlockPos();
        Direction hitDir = hitResult.getDirection();
        BlockPos relativePos = hitPos.relative(hitDir);

        if (!canPlayerInteract(level, player, hitPos, relativePos, hitDir, canisterStack))
            return InteractionResultHolder.fail(canisterStack);

        BlockState hitState = level.getBlockState(hitPos);

        if (isFluidPickupAction(player, hitState))
            return handleFluidPickup(player, level, canisterStack, hitPos, hitState);

        else if (isFluidPlacementAction(player))
            return handleFluidPlacement(player, level, canisterStack, hitResult, hitPos, hitState, relativePos);

        return InteractionResultHolder.fail(canisterStack);
    }

    private boolean isInvalidHitResult(BlockHitResult hitResult) {
        return hitResult.getType() == HitResult.Type.MISS || hitResult.getType() != HitResult.Type.BLOCK;
    }

    private boolean canPlayerInteract(Level level, Player player, BlockPos hitPos, BlockPos relativePos, Direction hitDir, ItemStack stack) {
        return level.mayInteract(player, hitPos) && player.mayUseItemAt(relativePos, hitDir, stack);
    }

    private boolean isFluidPickupAction(Player player, BlockState hitState) {
        return !player.isShiftKeyDown() && hitState.getBlock() instanceof BucketPickup;
    }

    private boolean isFluidPlacementAction(Player player) {
        return player.isShiftKeyDown() && this.content != Fluids.EMPTY;
    }

    private InteractionResultHolder<ItemStack> handleFluidPickup(Player player, Level level, ItemStack canisterStack, BlockPos hitPos, BlockState hitState) {
        BucketPickup bucketPickup = (BucketPickup) hitState.getBlock();

        if (canisterStack.getOrDefault(DataComponents.CANISTER_CONTENT_AMOUNT, 0) < MAX_CONTENT_AMOUNT) {
            ItemStack filledStack = pickupBlock(player, level, canisterStack, bucketPickup, hitPos, hitState);

            if (!filledStack.isEmpty()) {
                player.awardStat(Stats.ITEM_USED.get(this));
                bucketPickup.getPickupSound().ifPresent(sound -> player.playSound(sound, 1.0F, 1.0F));
                level.gameEvent(player, GameEvent.FLUID_PICKUP, hitPos);

                ItemStack resultStack;
                if (this.content != Fluids.EMPTY)
                    resultStack = updateContentAmount(filledStack, 1);
                else
                    resultStack = ItemUtils.createFilledResult(canisterStack, player, filledStack);

                if (!level.isClientSide)
                    CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, filledStack);

                return InteractionResultHolder.sidedSuccess(resultStack, level.isClientSide());
            }
        }
        return InteractionResultHolder.fail(canisterStack);
    }

    private InteractionResultHolder<ItemStack> handleFluidPlacement(Player player, Level level, ItemStack canisterStack, BlockHitResult hitResult, BlockPos hitPos, BlockState hitState, BlockPos relativePos) {
        BlockPos targetPos = hitState.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER ? hitPos : relativePos;

        if (emptyContents(player, level, targetPos, hitResult)) {
            checkExtraContent(player, level, canisterStack, targetPos);

            if (player instanceof ServerPlayer)
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, targetPos, canisterStack);

            player.awardStat(Stats.ITEM_USED.get(this));

            ItemStack resultStack;
            if (canisterStack.getOrDefault(DataComponents.CANISTER_CONTENT_AMOUNT, 0) > 1 && !player.isCreative())
                resultStack = updateContentAmount(canisterStack, -1);
            else
                resultStack = ItemUtils.createFilledResult(canisterStack, player, getEmptySuccessItem(canisterStack, player));

            return InteractionResultHolder.sidedSuccess(resultStack, level.isClientSide());
        }
        return InteractionResultHolder.fail(canisterStack);
    }

    public static ItemStack updateContentAmount(ItemStack stack, int amount) {
        int currentContentAmount = stack.getOrDefault(DataComponents.CANISTER_CONTENT_AMOUNT, 0);
        int newAmount = Mth.clamp(currentContentAmount + amount, 0, MAX_CONTENT_AMOUNT);

        stack.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.CANISTER_CONTENT_AMOUNT, newAmount)
                .build()
        );

        return stack;
    }

    private ItemStack pickupBlock(Player player, Level level, ItemStack canisterStack, BucketPickup bucketPickup, BlockPos blockPos, BlockState blockState) {
        ItemStack bucketItem = bucketPickup.pickupBlock(player, level, blockPos, blockState);

        if (this.content == Fluids.EMPTY) {
            if (bucketItem.is(Items.WATER_BUCKET))
                return new ItemStack(AVPItems.WATER_CANISTER);

            else if (bucketItem.is(Items.LAVA_BUCKET))
                return new ItemStack(AVPItems.LAVA_CANISTER);
        }

        return canisterStack;
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
