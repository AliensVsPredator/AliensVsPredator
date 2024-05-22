package org.avp.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

import org.avp.api.entity.HiveMember;
import org.avp.common.tag.AVPEntityTags;
import org.avp.common.tag.AVPItemTags;

import java.util.Objects;

public class AIUtils {

    public static void addBasicAlienAI(Monster monster, GoalSelector goalSelector, GoalSelector targetSelector) {
        goalSelector.addGoal(1, new FloatGoal(monster));
        goalSelector.addGoal(4, new MeleeAttackGoal(monster, 1, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(monster, 1));
        goalSelector.addGoal(6, new LookAtPlayerGoal(monster, Player.class, 8));
        goalSelector.addGoal(6, new RandomLookAroundGoal(monster));

        targetSelector.addGoal(1, new HurtByTargetGoal(monster));
        targetSelector.addGoal(
            2,
            new NearestAttackableTargetGoal<>(
                monster,
                LivingEntity.class,
                true,
                livingEntity -> {
                    var shouldKill = !livingEntity.getType().is(AVPEntityTags.ALIENS) &&
                        !livingEntity.getType().is(AVPEntityTags.NOT_WORTH_KILLING);

                    var isOpposingHiveMember = monster instanceof HiveMember selfMember &&
                        livingEntity instanceof HiveMember otherMember &&
                        selfMember.hasHivemind() &&
                        otherMember.hasHivemind() &&
                        Objects.equals(selfMember.getHivemindSignature(), otherMember.getHivemindSignature());

                    return shouldKill || isOpposingHiveMember;
                }
            )
        );
    }

    public static void addFacehuggerAI(Monster monster, GoalSelector goalSelector, GoalSelector targetSelector) {
        goalSelector.addGoal(1, new FloatGoal(monster));
        goalSelector.addGoal(2, new MeleeAttackGoal(monster, 1, true));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(monster, 1));
        targetSelector.addGoal(
            1,
            new NearestAttackableTargetGoal<>(
                monster,
                LivingEntity.class,
                true,
                livingEntity -> !livingEntity.getType().is(AVPEntityTags.ALIENS) &&
                    !livingEntity.getType().is(AVPEntityTags.NON_HOSTS)
            )
        );
    }

    public static void addYautjaAI(Monster monster, GoalSelector goalSelector, GoalSelector targetSelector) {
        goalSelector.addGoal(8, new LookAtPlayerGoal(monster, Player.class, 8));
        goalSelector.addGoal(8, new RandomLookAroundGoal(monster));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(monster, 1));

        goalSelector.addGoal(2, new MeleeAttackGoal(monster, 1, true));
        targetSelector.addGoal(1, new HurtByTargetGoal(monster));
        targetSelector.addGoal(
            2,
            new NearestAttackableTargetGoal<>(
                monster,
                LivingEntity.class,
                true,
                livingEntity -> livingEntity.getType().is(AVPEntityTags.ALIENS) ||
                    livingEntity instanceof Player player && player.getInventory()
                        .hasAnyMatching(
                            itemStack -> itemStack.is(AVPItemTags.THREATENS_PREDATORS)
                        )
            )
        );
    }

    private AIUtils() {
        throw new UnsupportedOperationException();
    }
}
