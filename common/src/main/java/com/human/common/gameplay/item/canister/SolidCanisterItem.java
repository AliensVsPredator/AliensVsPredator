package com.human.common.gameplay.item.canister;

import com.human.common.registry.init.HumanDataComponents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SolidCanisterItem extends BlockItem {

    private final SoundEvent placeSound;

    public SolidCanisterItem(Block block, SoundEvent placeSound, Properties properties) {
        super(block, properties);
        this.placeSound = placeSound;
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack stack,
        @NotNull TooltipContext context,
        @NotNull List<Component> tooltipComponents,
        @NotNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int currentContentAmount = stack.getOrDefault(HumanDataComponents.CANISTER_CAPACITY.get(), 0);

        if (currentContentAmount == 0) {
            return;
        }

        tooltipComponents.add(
            Component.translatable("tooltip.avp.capacity").append(currentContentAmount + "/" + CanisterItem.MAX_CAPACITY)
        );
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();

        if (player == null) {
            return InteractionResult.FAIL;
        }

        var modifiedContext = new UseOnContext(
            context.getLevel(),
            player,
            context.getHand(),
            context.getItemInHand().copy(),
            getPlayerPOVHitResult(context.getLevel(), player, ClipContext.Fluid.NONE)
        );
        var result = super.useOn(modifiedContext);

        if (result.consumesAction() && player.isShiftKeyDown()) {
            int contentAmount = context.getItemInHand().getOrDefault(HumanDataComponents.CANISTER_CAPACITY.get(), 0);

            if (contentAmount > 1 && !player.isCreative()) {
                CanisterItem.updateCapacity(player, context.getItemInHand(), -1);
                return result;
            }

            player.setItemInHand(context.getHand(), CanisterItem.getEmptySuccessItem(context.getItemInHand(), player));
            return InteractionResult.SUCCESS;
        }

        var hitResult = getPlayerPOVHitResult(context.getLevel(), player, ClipContext.Fluid.SOURCE_ONLY);

        if (CanisterItem.isInvalidHitResult(hitResult)) {
            return InteractionResult.PASS;
        }

        var hitPos = hitResult.getBlockPos();
        var hitDir = hitResult.getDirection();
        var relativePos = hitPos.relative(hitDir);

        if (!CanisterItem.canPlayerInteract(context.getLevel(), player, hitPos, relativePos, hitDir, context.getItemInHand())) {
            return InteractionResult.FAIL;
        }

        var hitState = context.getLevel().getBlockState(hitPos);

        if (CanisterItem.isFluidPickupAction(player, hitState)) {
            return handlePowderSnowPickup(player, context.getLevel(), context.getItemInHand(), hitPos, hitState);
        }

        return InteractionResult.FAIL;
    }

    private InteractionResult handlePowderSnowPickup(
        Player player,
        Level level,
        ItemStack canisterStack,
        BlockPos hitPos,
        BlockState hitState
    ) {
        var bucketPickup = (BucketPickup) hitState.getBlock();

        if (canisterStack.getOrDefault(HumanDataComponents.CANISTER_CAPACITY.get(), 0) >= CanisterItem.MAX_CAPACITY) {
            return InteractionResult.FAIL;
        }

        bucketPickup.pickupBlock(player, level, hitPos, hitState);

        player.awardStat(Stats.ITEM_USED.get(this));
        bucketPickup.getPickupSound().ifPresent(sound -> player.playSound(sound, 1.0F, 1.0F));
        level.gameEvent(player, GameEvent.FLUID_PICKUP, hitPos);

        CanisterItem.updateCapacity(player, canisterStack, 1);

        if (!level.isClientSide) {
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, canisterStack);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, @NotNull BlockState state) {
        return context.getPlayer() != null && context.getPlayer().isShiftKeyDown() && super.placeBlock(context, state);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    protected @NotNull SoundEvent getPlaceSound(@NotNull BlockState state) {
        return this.placeSound;
    }
}
