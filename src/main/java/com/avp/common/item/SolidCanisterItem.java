package com.avp.common.item;

import com.avp.common.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
        InteractionResult interactionResult = super.useOn(context);
        Player player = context.getPlayer();
        if (interactionResult.consumesAction() && player != null) {
            if (context.getItemInHand().getOrDefault(DataComponents.CANISTER_CONTENT_AMOUNT, 0) > 1 && !player.isCreative()) {
                CanisterItem.updateContentAmount(context.getItemInHand(), -1);
                return interactionResult;
            }

            player.setItemInHand(context.getHand(), CanisterItem.getEmptySuccessItem(context.getItemInHand(), player));
        }

        return interactionResult;
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
