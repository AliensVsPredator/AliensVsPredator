package com.avp.common.entity.ai.action;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.CombatResponse;
import com.avp.common.entity.ai.util.ItemType;
import com.avp.goap.GOAPAction;
import com.avp.goap.condition.GOAPCondition;
import com.avp.goap.condition.GOAPConditionContainer;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffect;
import com.avp.goap.effect.GOAPEffectContainer;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class MeleeAttackAction<T extends Mob> extends GOAPAction<T> {

    @Override
    public GOAPConditionContainer createPreconditions() {
        return GOAPConditionContainer.of(
            new GOAPCondition<>(
                GOAPConstants.COMBAT_RESPONSE,
                GOAPExpression.equalTo(new CombatResponse.Fight(CombatResponse.FightType.MELEE))
            ),
            new GOAPCondition<>(
                GOAPConstants.MAIN_HAND_ITEM_TYPE,
                GOAPExpression.equalTo(ItemType.meleeWeapon())
            ),
            new GOAPCondition<>(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, GOAPExpression.isSome()),
            new GOAPCondition<>(GOAPConstants.IS_ATTACK_TARGET_ENTITY_IN_RANGE, GOAPExpression.equalTo(true))
        );
    }

    @Override
    public GOAPEffectContainer createEffects() {
        return GOAPEffectContainer.of(
            new GOAPEffect.Value<>(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, Option.none())
        );
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

        performMeleeAttack(context, target);

        return false;
    }

    protected void performMeleeAttack(T context, LivingEntity target) {
        context.doHurtTarget(target);
    }
}
