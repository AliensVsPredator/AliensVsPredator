package com.predator.common.gameplay.entity.living.yautja.manager;

import com.predator.common.gameplay.entity.living.yautja.Yautja;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.pathfinder.PathType;

import com.avp.common.gameplay.ai.goal.WaterMoveControl;
import com.avp.common.gameplay.ai.goal.combat.UseItemGoal;

public class YautjaNavigationManager {

    private final GroundPathNavigation groundNavigation;

    private final MoveControl groundMoveControl;

    private final WaterBoundPathNavigation waterNavigation;

    private final WaterMoveControl waterMoveControl;

    private final Goal groundAttackGoal;

    private final Goal waterAttackGoal;

    public YautjaNavigationManager(Yautja yautja, MoveControl moveControl) {
        this.groundAttackGoal = new UseItemGoal(yautja, () -> {});
        this.groundMoveControl = moveControl;
        this.groundNavigation = new GroundPathNavigation(yautja, yautja.level());

        // Water navigation.
        yautja.setPathfindingMalus(PathType.WATER, 0.0F);
        this.waterAttackGoal = new UseItemGoal(yautja, () -> {});
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
