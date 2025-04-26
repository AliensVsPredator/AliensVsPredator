package com.avp.common.entity.ai.action;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.pathfinder.Path;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.CombatResponse;
import com.avp.goap.GOAPAction;
import com.avp.goap.TypedIdentifier;
import com.avp.goap.condition.expression.GOAPExpression;
import com.avp.goap.effect.GOAPEffect;
import com.avp.goap.state.GOAPBlackboard;
import com.avp.goap.state.GOAPWorldState;

public class WanderToLandPosAction<T extends PathfinderMob> extends GOAPAction<T> {

    private static final TypedIdentifier<Path> PATH = new TypedIdentifier<>("path");

    private final double speedMultiplier;

    public WanderToLandPosAction(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;

        addPrecondition(GOAPConstants.IS_BORED, GOAPExpression.isTrue());
        addPrecondition(GOAPConstants.COMBAT_RESPONSE, GOAPExpression.equalTo(CombatResponse.rest()));

        addEffect(new GOAPEffect.Value<>(GOAPConstants.IS_BORED, false));
    }

    @Override
    public boolean perform(T context, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var path = blackboard.get(PATH);
        var navigation = context.getNavigation();

        if (path == null) {
            var randomPosition = LandRandomPos.getPos(context, 10, 7);

            if (randomPosition == null) {
                return false;
            }

            path = navigation.createPath(randomPosition.x, randomPosition.y, randomPosition.z, 1);

            blackboard.set(PATH, path);

            navigation.moveTo(path, speedMultiplier);
        }

        return navigation.isDone();
    }

    @Override
    public float getCost(T context, GOAPWorldState worldState) {
        return 1.0F;
    }
}
