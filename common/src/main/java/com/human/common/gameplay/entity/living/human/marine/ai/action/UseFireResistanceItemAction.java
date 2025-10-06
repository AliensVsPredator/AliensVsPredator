package com.human.common.gameplay.entity.living.human.marine.ai.action;

import com.avp.common.model.inventory.AVPInventoryHolder;
import com.human.common.gameplay.entity.living.human.marine.ai.utility.fire_resistance.strategy.FireResistanceItemStrategy;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class UseFireResistanceItemAction {

    public static <T extends LivingEntity & AVPInventoryHolder> Action.@NotNull Result perform(
        Function<ItemStack, Option<FireResistanceItemStrategy<T>>> strategySelector,
        T livingEntity,
        ReadableWorldState worldState,
        Blackboard blackboard
    ) {
        var mainHandItem = livingEntity.getMainHandItem();
        var strategyOption = strategySelector.apply(mainHandItem);

        if (strategyOption.isNone()) {
            return Action.Result.FAILED;
        }

        var context = FireResistanceItemStrategy.Context.create(livingEntity, worldState, blackboard, FireResistanceItemStrategy.Weights.DEFAULT);
        return strategyOption.unwrap().execute(context);
    }

    private UseFireResistanceItemAction() {
        throw new UnsupportedOperationException();
    }
}
