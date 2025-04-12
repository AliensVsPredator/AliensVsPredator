package com.avp.common.item;

import com.avp.common.component.DataComponents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SolidCanisterItem extends BlockItem {
    private final SoundEvent placeSound;

    public SolidCanisterItem(Block block, SoundEvent placeSound, Properties properties) {
        super(block, properties);
        this.placeSound = placeSound;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int currentContentAmount = stack.getOrDefault(DataComponents.CANISTER_CONTENT_AMOUNT, 0);
        if (currentContentAmount == 0) return;

        tooltipComponents.add(Component.translatable("tooltip.avp.capacity").append(currentContentAmount + "/" + CanisterItem.MAX_CONTENT_AMOUNT));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.FAIL;

        UseOnContext modifiedContext = new UseOnContext(
                context.getLevel(),
                player,
                context.getHand(),
                context.getItemInHand().copy(),
                getPlayerPOVHitResult(context.getLevel(), player, ClipContext.Fluid.NONE)
        );
        InteractionResult result = super.useOn(modifiedContext);

        if (result.consumesAction() && player.isShiftKeyDown()) {
            int contentAmount = context.getItemInHand().getOrDefault(DataComponents.CANISTER_CONTENT_AMOUNT, 0);

            if (contentAmount > 1 && !player.isCreative()) {
                CanisterItem.updateContentAmount(context.getItemInHand(), -1);
                return result;
            }

            player.setItemInHand(context.getHand(), CanisterItem.getEmptySuccessItem(context.getItemInHand(), player));
            return InteractionResult.SUCCESS;
        }

        BlockHitResult hitResult = getPlayerPOVHitResult(context.getLevel(), player, ClipContext.Fluid.SOURCE_ONLY);

        if (CanisterItem.isInvalidHitResult(hitResult))
            return InteractionResult.PASS;

        BlockPos hitPos = hitResult.getBlockPos();
        Direction hitDir = hitResult.getDirection();
        BlockPos relativePos = hitPos.relative(hitDir);

        if (!CanisterItem.canPlayerInteract(context.getLevel(), player, hitPos, relativePos, hitDir,  context.getItemInHand())) {
            return InteractionResult.FAIL;
        }

        BlockState hitState = context.getLevel().getBlockState(hitPos);

        if (CanisterItem.isFluidPickupAction(player, hitState))
            return handlePowderSnowPickup(player, context.getLevel(),  context.getItemInHand(), hitPos, hitState);

        return InteractionResult.FAIL;
    }


    private InteractionResult handlePowderSnowPickup(Player player, Level level, ItemStack canisterStack, BlockPos hitPos, BlockState hitState) {
        BucketPickup bucketPickup = (BucketPickup) hitState.getBlock();

        if (canisterStack.getOrDefault(DataComponents.CANISTER_CONTENT_AMOUNT, 0) < CanisterItem.MAX_CONTENT_AMOUNT) {
            bucketPickup.pickupBlock(player, level, hitPos, hitState);

            player.awardStat(Stats.ITEM_USED.get(this));
            bucketPickup.getPickupSound().ifPresent(sound -> player.playSound(sound, 1.0F, 1.0F));
            level.gameEvent(player, GameEvent.FLUID_PICKUP, hitPos);

            CanisterItem.updateContentAmount(canisterStack, 1);

            if (!level.isClientSide)
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, canisterStack);

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return context.getPlayer() != null && context.getPlayer().isShiftKeyDown() && super.placeBlock(context, state);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    protected @NotNull SoundEvent getPlaceSound(BlockState state) {
        return this.placeSound;
    }
}
