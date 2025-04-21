package com.avp.common.entity.ai.action;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.world.entity.LivingEntity;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.CombatResponse;
import com.avp.common.entity.ai.util.ItemType;
import com.avp.goap.GOAPAction;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffect;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class RangedAttackAction<T extends LivingEntity> extends GOAPAction<T> {

    public RangedAttackAction() {
        addPrecondition(
            GOAPConstants.COMBAT_RESPONSE,
            GOAPExpression.equalTo(new CombatResponse.Fight(CombatResponse.FightType.RANGED))
        );
        addPrecondition(GOAPConstants.MAIN_HAND_ITEM_TYPE, GOAPExpression.equalTo(ItemType.rangedWeapon()));
        addPrecondition(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, GOAPExpression.isSome());
        addPrecondition(GOAPConstants.IS_ATTACK_TARGET_ENTITY_IN_RANGE, GOAPExpression.isTrue());

        addEffect(new GOAPEffect.Value<>(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, Option.none()));
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var targetOption = worldState.getOrDefault(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, Option.none());

        if (targetOption.isNone()) {
            // Target does not exist, mission success.
            return true;
        }

        var target = targetOption.unwrap();

        if (target.isDeadOrDying()) {
            // Target is dead or dying, mission success.
            return true;
        }

        performRangedAttack(context, target, blackboard);

        return false;
    }

    protected void performRangedAttack(T context, LivingEntity target, GOAPBlackboard blackboard) {
        context.doHurtTarget(target);
    }
}
