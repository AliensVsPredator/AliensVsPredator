package com.avp.core.common.entity.living.alien.xenomorph;

import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;

import com.avp.core.common.ai.goal.WaterMoveControl;
import com.avp.core.common.ai.goal.combat.DelayedAttackGoal;
import com.avp.core.common.ai.path.CrawlPathNodeEvaluator;

public class XenomorphNavigationManager {

    private final GroundPathNavigation groundNavigation;

    private final MoveControl groundMoveControl;

    private final WaterBoundPathNavigation waterNavigation;

    private final WaterMoveControl waterMoveControl;

    private final Goal groundAttackGoal;

    private final Goal waterAttackGoal;

    public XenomorphNavigationManager(Xenomorph xenomorph, MoveControl moveControl) {
        // Ground navigation.
        this.groundAttackGoal = new DelayedAttackGoal(xenomorph, 1, false, 7, xenomorph::runAttackAnimations);
        this.groundMoveControl = moveControl;
        this.groundNavigation = new GroundPathNavigation(xenomorph, xenomorph.level()) {

            @Override
            protected @NotNull PathFinder createPathFinder(int i) {
                this.nodeEvaluator = new CrawlPathNodeEvaluator();
                this.nodeEvaluator.setCanPassDoors(true);
                return new PathFinder(this.nodeEvaluator, i);
            }
        };

        // Water navigation.
        xenomorph.setPathfindingMalus(PathType.WATER, 0.0F);
        this.waterAttackGoal = new DelayedAttackGoal(xenomorph, 2, false, 7, xenomorph::runAttackAnimations);
        this.waterMoveControl = new WaterMoveControl(xenomorph);
        this.waterNavigation = new WaterBoundPathNavigation(xenomorph, xenomorph.level());
    }

    public void switchToGround(Xenomorph xenomorph, int priority, GoalSelector goalSelector) {
        goalSelector.removeGoal(waterAttackGoal);
        goalSelector.addGoal(priority, groundAttackGoal);
        xenomorph.setMoveControl(groundMoveControl);
        xenomorph.setNavigation(groundNavigation);
    }

    public void switchToWater(Xenomorph xenomorph, int priority, GoalSelector goalSelector) {
        goalSelector.removeGoal(groundAttackGoal);
        goalSelector.addGoal(priority, waterAttackGoal);
        xenomorph.setMoveControl(waterMoveControl);
        xenomorph.setNavigation(waterNavigation);
    }
}
