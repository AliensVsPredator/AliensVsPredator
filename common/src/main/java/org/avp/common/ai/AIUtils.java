package org.avp.common.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
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

import java.util.Objects;

import org.avp.common.data.tag.AVPEntityTypeTags;
import org.avp.common.data.tag.AVPItemTags;
import org.avp.common.game.entity.type.HiveMember;

public class AIUtils {

    public static void addBasicAI(Monster monster, GoalSelector goalSelector) {
        goalSelector.addGoal(1, new FloatGoal(monster));
        goalSelector.addGoal(4, new MeleeAttackGoal(monster, 1, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(monster, 1));
        goalSelector.addGoal(6, new LookAtPlayerGoal(monster, Player.class, 8));
        goalSelector.addGoal(6, new RandomLookAroundGoal(monster));
    }

    public static void addChestbursterAI(Monster monster, GoalSelector goalSelector) {
        goalSelector.addGoal(8, new LookAtPlayerGoal(monster, Player.class, 8));
        goalSelector.addGoal(8, new RandomLookAroundGoal(monster));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(monster, 1));

        goalSelector.addGoal(3, new AvoidEntityGoal<>(monster, Player.class, 6, 1, 1.2));
    }

    public static void addAlienAI(Monster monster, GoalSelector goalSelector, GoalSelector targetSelector) {
        addBasicAI(monster, goalSelector);
        targetSelector.addGoal(1, new HurtByTargetGoal(monster));

        targetSelector.addGoal(
            2,
            new NearestAttackableTargetGoal<>(
                monster,
                Mob.class,
                true,
                livingEntity -> {
                    var shouldKill = !livingEntity.getType().is(AVPEntityTypeTags.ALIENS) &&
                        !livingEntity.getType().is(AVPEntityTypeTags.NOT_WORTH_KILLING);

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

    public static void addYautjaAI(Monster monster, GoalSelector goalSelector, GoalSelector targetSelector) {
        addBasicAI(monster, goalSelector);

        targetSelector.addGoal(1, new HurtByTargetGoal(monster));
        targetSelector.addGoal(
            2,
            new NearestAttackableTargetGoal<>(
                monster,
                LivingEntity.class,
                true,
                livingEntity -> livingEntity.getType().is(AVPEntityTypeTags.ALIENS) ||
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
