package org.avp.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

import org.avp.common.tag.AVPEntityTags;

public class AIUtils {

    public static void addBasicAlienAI(Monster monster, GoalSelector goalSelector, GoalSelector targetSelector) {
        goalSelector.addGoal(1, new FloatGoal(monster));
        goalSelector.addGoal(4, new MeleeAttackGoal(monster, 1.0, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(monster, 1.0));
        goalSelector.addGoal(6, new LookAtPlayerGoal(monster, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(monster));

        targetSelector.addGoal(
            2,
            new NearestAttackableTargetGoal<>(
                monster,
                LivingEntity.class,
                true,
                livingEntity -> !livingEntity.getType().is(AVPEntityTags.ALIENS) &&
                    !livingEntity.getType().is(AVPEntityTags.NOT_WORTH_KILLING)
            )
        );
    }

    private AIUtils() {
        throw new UnsupportedOperationException();
    }
}
