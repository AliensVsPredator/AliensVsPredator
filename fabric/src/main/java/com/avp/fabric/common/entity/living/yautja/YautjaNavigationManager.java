package com.avp.fabric.common.entity.living.yautja;

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

import com.avp.common.ai.goal.WaterMoveControl;
import com.avp.fabric.common.ai.goal.combat.UseItemGoal;

public class YautjaNavigationManager {

    private final GroundPathNavigation groundNavigation;

    private final MoveControl groundMoveControl;

    private final WaterBoundPathNavigation waterNavigation;

    private final WaterMoveControl waterMoveControl;

    private final Goal groundAttackGoal;

    private final Goal waterAttackGoal;

    public YautjaNavigationManager(Yautja yautja, MoveControl moveControl) {
        this.groundAttackGoal = new UseItemGoal(yautja, yautja::runAttackAnimations);
        this.groundMoveControl = moveControl;
        this.groundNavigation = new AzureNavigation(yautja, yautja.level()) {

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
        yautja.setPathfindingMalus(PathType.WATER, 0.0F);
        this.waterAttackGoal = new UseItemGoal(yautja, yautja::runAttackAnimations);
        this.waterMoveControl = new WaterMoveControl(yautja);
        this.waterNavigation = new WaterBoundPathNavigation(yautja, yautja.level());
    }

    public void switchToGround(Yautja yautja, int priority, GoalSelector goalSelector) {
        goalSelector.removeGoal(waterAttackGoal);
        goalSelector.addGoal(priority, groundAttackGoal);
        yautja.setMoveControl(groundMoveControl);
        yautja.setNavigation(groundNavigation);
    }

    public void switchToWater(Yautja yautja, int priority, GoalSelector goalSelector) {
        goalSelector.removeGoal(groundAttackGoal);
        goalSelector.addGoal(priority, waterAttackGoal);
        yautja.setMoveControl(waterMoveControl);
        yautja.setNavigation(waterNavigation);
    }
}
