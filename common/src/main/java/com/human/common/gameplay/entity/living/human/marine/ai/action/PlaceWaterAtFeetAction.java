package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.StateKey;
import com.just.goap.state.Blackboard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import com.avp.common.model.inventory.AVPInventory;

public class PlaceWaterAtFeetAction {

    private static final Item WATER_BUCKET_ITEM = Items.WATER_BUCKET;

    private static final InteractionHand HAND_TO_USE = InteractionHand.MAIN_HAND;

    private static final StateKey.Derived<Option<BlockPos>> WATER_POS_OPTION = StateKey.derived("water_pos_option");

    public static Action.@NotNull Result perform(Marine marine, Blackboard blackboard) {
        var mainhandItemStack = marine.getMainHandItem();
        var isWaterBucketEquipped = mainhandItemStack.is(WATER_BUCKET_ITEM);

        if (!isWaterBucketEquipped) {
            return equipWaterBucket(marine, mainhandItemStack);
        }

        var blockPos = marine.blockPosition();
        blackboard.set(WATER_POS_OPTION, Option.some(blockPos));
        marine.level().setBlock(blockPos, Blocks.WATER.defaultBlockState(), Block.UPDATE_ALL);
        marine.setItemInHand(HAND_TO_USE, new ItemStack(Items.BUCKET));

        return Action.Result.CONTINUE;
    }

    public static void onFinish(Marine marine, Blackboard blackboard) {
        var waterPosOption = blackboard.getOrDefault(WATER_POS_OPTION, Option.none());

        waterPosOption.ifSome(blockPos -> {
            marine.level().setBlock(marine.blockPosition(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            marine.setItemInHand(HAND_TO_USE, ItemStack.EMPTY);
            marine.getInventory().addItem(WATER_BUCKET_ITEM);
        });
    }

    private static Action.@NotNull Result equipWaterBucket(Marine marine, ItemStack mainhandItemStack) {
        // Remove the water bucket from the marine's inventory.
        var removeResult = marine.getInventory().removeItem(WATER_BUCKET_ITEM);

        return switch (removeResult) {
            case AVPInventory.RemoveResult.InventoryEmpty inventoryEmpty -> Action.Result.FAILED;
            case AVPInventory.RemoveResult.Partial partial -> Action.Result.FAILED;
            case AVPInventory.RemoveResult.Success success -> {
                // Put mainhand item in inventory.
                marine.getInventory().addItemStack(mainhandItemStack);
                marine.setItemInHand(HAND_TO_USE, ItemStack.EMPTY);
                // Equip water bucket.
                marine.setItemInHand(HAND_TO_USE, new ItemStack(WATER_BUCKET_ITEM));

                yield Action.Result.CONTINUE;
            }
        };
    }
}
