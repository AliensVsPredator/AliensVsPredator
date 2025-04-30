package com.avp.fabric.common.entity.living.human;

import mod.azure.azurelib.common.api.common.ai.pathing.AzureNavigation;
import mod.azure.azurelib.common.internal.common.ai.pathing.AzurePathFinder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.NotNull;

import com.avp.fabric.common.ai.goal.WaterMoveControl;
import com.avp.fabric.common.ai.goal.combat.UseItemGoal;

public class HumanNavigationManager {

    private final GroundPathNavigation groundNavigation;

    private final MoveControl groundMoveControl;

    private final WaterBoundPathNavigation waterNavigation;

    private final WaterMoveControl waterMoveControl;

    private final Goal waterAttackGoal;

    public HumanNavigationManager(AbstractHuman humanMob, MoveControl moveControl) {
        this.groundMoveControl = moveControl;
        this.groundNavigation = new AzureNavigation(humanMob, humanMob.level()) {

            @Override
            protected @NotNull PathFinder createPathFinder(int maxVisitedNodes) {
                this.nodeEvaluator = new WalkNodeEvaluator();
                this.nodeEvaluator.setCanPassDoors(true);
                this.nodeEvaluator.setCanOpenDoors(true);
                this.nodeEvaluator.setCanFloat(true);
                return new AzurePathFinder(this.nodeEvaluator, maxVisitedNodes);
            }
        };

        // Water navigation.
        humanMob.setPathfindingMalus(PathType.WATER, 0.5F);
        this.waterAttackGoal = new UseItemGoal(humanMob, humanMob::runAttackAnimations);
        this.waterMoveControl = new WaterMoveControl(humanMob);
        this.waterNavigation = new WaterBoundPathNavigation(humanMob, humanMob.level());
    }

    public void switchToGround(AbstractHuman humanMob, int priority, GoalSelector goalSelector) {
        goalSelector.removeGoal(waterAttackGoal);
        humanMob.setMoveControl(groundMoveControl);
        humanMob.setNavigation(groundNavigation);
    }

    public void switchToWater(AbstractHuman humanMob, int priority, GoalSelector goalSelector) {
        goalSelector.addGoal(priority, waterAttackGoal);
        humanMob.setMoveControl(waterMoveControl);
        humanMob.setNavigation(waterNavigation);
    }
}
