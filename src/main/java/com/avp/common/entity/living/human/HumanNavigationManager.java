package com.avp.common.entity.living.human;

import com.avp.common.ai.goal.WaterMoveControl;
import com.avp.common.ai.goal.combat.DelayedAttackGoal;
import com.avp.common.ai.path.CrawlPathNodeEvaluator;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import mod.azure.azurelib.common.api.common.ai.pathing.AzureNavigation;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;

public class HumanNavigationManager {

    private final GroundPathNavigation groundNavigation;

    private final MoveControl groundMoveControl;

    private final WaterBoundPathNavigation waterNavigation;

    private final WaterMoveControl waterMoveControl;

    private final Goal groundAttackGoal;

    private final Goal waterAttackGoal;

    public HumanNavigationManager(AbstractHumanMob humanMob, MoveControl moveControl) {
        this.groundAttackGoal = new DelayedAttackGoal(humanMob, 1, false, 7, humanMob::runAttackAnimations);
        this.groundMoveControl = moveControl;
        this.groundNavigation = new AzureNavigation(humanMob, humanMob.level());

        // Water navigation.
        humanMob.setPathfindingMalus(PathType.WATER, 0.0F);
        this.waterAttackGoal = new DelayedAttackGoal(humanMob, 2, false, 7, humanMob::runAttackAnimations);
        this.waterMoveControl = new WaterMoveControl(humanMob);
        this.waterNavigation = new WaterBoundPathNavigation(humanMob, humanMob.level());
    }

    public void switchToGround(AbstractHumanMob humanMob, int priority, GoalSelector goalSelector) {
        goalSelector.removeGoal(waterAttackGoal);
        goalSelector.addGoal(priority, groundAttackGoal);
        humanMob.setMoveControl(groundMoveControl);
        humanMob.setNavigation(groundNavigation);
    }

    public void switchToWater(AbstractHumanMob humanMob, int priority, GoalSelector goalSelector) {
        goalSelector.removeGoal(groundAttackGoal);
        goalSelector.addGoal(priority, waterAttackGoal);
        humanMob.setMoveControl(waterMoveControl);
        humanMob.setNavigation(waterNavigation);
    }
}
