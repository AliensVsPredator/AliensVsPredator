package com.lib.common.gameplay.entity.ai.action;

import com.just.core.functional.option.Option;
import com.lib.common.gameplay.entity.ai.GOAPConstants;
import com.lib.common.gameplay.entity.ai.util.CombatResponse;
import com.lib.common.gameplay.goap.GOAPAction;
import com.lib.common.gameplay.goap.TypedIdentifier;
import com.lib.common.gameplay.goap.condition.expression.GOAPExpression;
import com.lib.common.gameplay.goap.effect.GOAPEffect;
import com.lib.common.gameplay.goap.state.GOAPBlackboard;
import com.lib.common.gameplay.goap.state.GOAPWorldState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.function.Function;

import com.avp.common.model.inventory.AVPInventoryBearer;

public class PickUpItemAction<T extends Mob & AVPInventoryBearer> extends GOAPAction<T> {

    private final TypedIdentifier<Option<? extends ItemEntity>> itemEntityIdentifier;

    private final Function<GOAPWorldState, Float> costFunction;

    public PickUpItemAction(
        TypedIdentifier<Option<? extends ItemEntity>> itemEntityIdentifier,
        TypedIdentifier<Boolean> isInRangeIdentifier,
        Function<GOAPWorldState, Float> costFunction
    ) {
        this.itemEntityIdentifier = itemEntityIdentifier;
        this.costFunction = costFunction;

        // Entity must be out of combat.
        addPrecondition(GOAPConstants.COMBAT_RESPONSE, GOAPExpression.equalTo(CombatResponse.rest()));
        // Must be an item nearby.
        addPrecondition(itemEntityIdentifier, GOAPExpression.isSome());
        // Must have a free inventory slot to pick up the item.
        addPrecondition(GOAPConstants.HAS_FREE_INVENTORY_SLOT, GOAPExpression.isTrue());
        // Item must be close to pick it up.
        addPrecondition(isInRangeIdentifier, GOAPExpression.isTrue());

        addEffect(new GOAPEffect.Value<>(itemEntityIdentifier, Option.none()));
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        worldState.getOrDefault(itemEntityIdentifier, Option.none())
            .ifSome(nearestItemEntity -> context.getInventory().pickUpItem(nearestItemEntity));

        return true;
    }

    @Override
    public float getCost(T context, GOAPWorldState worldState) {
        return costFunction.apply(worldState);
    }
}
