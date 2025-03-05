package com.avp.common.entity.living.alien.parasite.facehugger;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import com.avp.common.MoveAnalysis;
import com.avp.common.ai.goal.combat.LungeAtTargetGoal;
import com.avp.common.entity.constant.HealthConstants;
import com.avp.common.entity.constant.KnockbackResistanceConstants;
import com.avp.common.entity.constant.MoveSpeedConstants;
import com.avp.common.entity.living.alien.parasite.Parasite;
import com.avp.common.entity.type.AVPEntityTypes;

public class Facehugger extends Parasite {

    public static AttributeSupplier.Builder createFacehuggerAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.ATTACK_DAMAGE)
            .add(Attributes.KNOCKBACK_RESISTANCE, KnockbackResistanceConstants.FACEHUGGER_KNOCKBACK_RESISTANCE)
            .add(Attributes.MAX_HEALTH, HealthConstants.FACEHUGGER_HEALTH)
            .add(Attributes.MOVEMENT_SPEED, MoveSpeedConstants.FACEHUGGER_SPEED);
    }

    private final FacehuggerAnimationDispatcher animationDispatcher;

    private final MoveAnalysis moveAnalysis;

    public Facehugger(EntityType<? extends Facehugger> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new FacehuggerAnimationDispatcher(this);
        this.moveAnalysis = new MoveAnalysis(this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new LungeAtTargetGoal(this, 0.75F, 20 * 3, 1, 12).setOnLungeCallback(this::runLungeAnimation));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.targetSelector.addGoal(
            1,
            new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, this::isValidHost)
        );
    }

    @Override
    public void tick() {
        super.tick();
        moveAnalysis.tick();
    }

    public void runPassiveAnimations() {
        var dispatcher = animationDispatcher;

        if (attachmentManager.isAttachedToHost()) {
            dispatcher.hug();
            return;
        }

        if (!attachmentManager.isFertile()) {
            dispatcher.infertile();
            return;
        }

        var isMovingOnGround = moveAnalysis.isMovingHorizontally() && onGround();

        if (isUnderWater()) {
            // TODO: swim
        } else if (isMovingOnGround) {
            dispatcher.run();
        } else {
            dispatcher.idle();
        }
    }

    private void runLungeAnimation() {
        animationDispatcher.lunge();
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.015F, 2F);
    }

    @Override
    public boolean isPersistenceRequired() {
        return super.isPersistenceRequired() || attachmentManager.isAttachedToHost();
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        SpawnEggItem spawnEggItem = null;

        if (isNetherAfflicted()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.NETHER_FACEHUGGER);
        }

        if (isAberrant()) {
            spawnEggItem = SpawnEggItem.byId(AVPEntityTypes.ABERRANT_FACEHUGGER);
        }

        return spawnEggItem == null ? super.getPickResult() : new ItemStack(spawnEggItem);
    }
}
