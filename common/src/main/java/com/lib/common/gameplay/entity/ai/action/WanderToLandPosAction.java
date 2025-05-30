package com.lib.common.gameplay.entity.ai.action;

import com.lib.common.gameplay.entity.ai.GOAPConstants;
import com.lib.common.gameplay.entity.ai.util.CombatResponse;
import com.lib.common.gameplay.goap.GOAPAction;
import com.lib.common.gameplay.goap.TypedIdentifier;
import com.lib.common.gameplay.goap.condition.expression.GOAPExpression;
import com.lib.common.gameplay.goap.effect.GOAPEffect;
import com.lib.common.gameplay.goap.state.GOAPBlackboard;
import com.lib.common.gameplay.goap.state.GOAPWorldState;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.pathfinder.Path;

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
