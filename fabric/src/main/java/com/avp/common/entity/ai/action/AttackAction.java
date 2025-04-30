package com.avp.common.entity.ai.action;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.CombatResponse;
import com.avp.common.entity.ai.util.ItemType;
import com.avp.goap.GOAPAction;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffect;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class AttackAction<T extends Mob> extends GOAPAction<T> {

    public AttackAction(CombatResponse.FightType fightType) {
        addPrecondition(
            GOAPConstants.COMBAT_RESPONSE,
            GOAPExpression.equalTo(new CombatResponse.Fight(fightType))
        );
        addPrecondition(
            GOAPConstants.MAIN_HAND_ITEM_TYPE,
            GOAPExpression.equalTo(fightType == CombatResponse.FightType.MELEE ? ItemType.meleeWeapon() : ItemType.rangedWeapon())
        );
        addPrecondition(GOAPConstants.NEAREST_ATTACK_TARGET_ENTITY, GOAPExpression.isSome());
        addPrecondition(GOAPConstants.IS_ATTACK_TARGET_ENTITY_IN_RANGE, GOAPExpression.equalTo(true));

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

        performAttack(context, target, blackboard);

        return false;
    }

    protected void performAttack(T context, LivingEntity target, GOAPBlackboard blackboard) {
        context.doHurtTarget(target);
    }
}
