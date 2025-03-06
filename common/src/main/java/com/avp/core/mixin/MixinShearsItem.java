package com.avp.core.mixin;

import com.avp.core.common.block.AVPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ShearsItem.class)
public class MixinShearsItem {
    @Inject(method = "createToolProperties", at = @At("RETURN"), cancellable = true)
    private static void avp$createToolProperties(CallbackInfoReturnable<Tool> cir) {
        Tool originalTool = cir.getReturnValue();

        if (originalTool != null) {
            List<Tool.Rule> modifiedRules = new ArrayList<>(originalTool.rules());
            modifiedRules.add(Tool.Rule.minesAndDrops(List.of(AVPBlocks.RAZOR_WIRE.get()), 15.0F));
            Tool modifiedTool = new Tool(modifiedRules, originalTool.defaultMiningSpeed(), 1);

            cir.setReturnValue(modifiedTool);
        }
    }

    @Inject(method = "mineBlock", at = @At("RETURN"), cancellable = true)
    private void avp$mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(blockState.is(AVPBlocks.RAZOR_WIRE.get()));
    }
}
