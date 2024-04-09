package org.avp.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
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
        goalSelector.addGoal(8, new LookAtPlayerGoal(monster, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(monster));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(monster, 1.0));

        goalSelector.addGoal(2, new MeleeAttackGoal(monster, 1.0, true));
        targetSelector.addGoal(
            2,
            new NearestAttackableTargetGoal<>(
                monster,
                LivingEntity.class,
                true,
                livingEntity -> !livingEntity.getType().is(AVPEntityTags.ALIENS)
            )
        );
    }

    private AIUtils() {
        throw new UnsupportedOperationException();
    }
}
