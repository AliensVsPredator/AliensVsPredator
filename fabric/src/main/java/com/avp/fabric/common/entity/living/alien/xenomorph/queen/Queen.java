package com.avp.fabric.common.entity.living.alien.xenomorph.queen;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.fabric.AVPFabric;
import com.avp.fabric.common.ai.goal.DigToTargetGoal;
import com.avp.fabric.common.ai.goal.QueenLayEggGoal;
import com.avp.common.block.AVPBlockTags;
import com.avp.fabric.common.entity.living.alien.Alien;
import com.avp.fabric.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.fabric.common.entity.type.AVPEntityTypes;
import com.avp.fabric.common.sound.AVPSoundEvents;
import com.avp.fabric.common.util.AlienVariantUtil;
import com.avp.fabric.common.util.resin.ResinData;

public class Queen extends Xenomorph {

    public static AttributeSupplier.Builder createQueenAttributes() {
        return applyFrom(AVPFabric.config.statsConfigs.QUEEN_STATS, Monster.createMonsterAttributes());
    }

    private final QueenAnimationDispatcher animationDispatcher;

    public Queen(EntityType<? extends Queen> entityType, Level level) {
        super(entityType, level);
        this.attackDelayTicks = 20;
        this.animationDispatcher = new QueenAnimationDispatcher(this);
        this.config = AVPFabric.config.statsConfigs.QUEEN_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getAberrantType() {
        return AVPEntityTypes.ABERRANT_QUEEN;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getIrradiatedType() {
        return AVPEntityTypes.IRRADIATED_QUEEN;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getNetherType() {
        return AVPEntityTypes.NETHER_QUEEN;
    }

    @Override
    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 128, 1, AVPFabric.config.statsConfigs.QUEEN_STATS.nestTickrate);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(5, new QueenLayEggGoal(this));
    }

    @Override
    protected void addDigToTargetGoal() {
        goalSelector.addGoal(5, new DigToTargetGoal(this, 32, 4));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            becomeIrradiated();

            if (tickCount < 2) {
                var belowBlockPos = blockPosition().below();
                var blockState = level().getBlockState(belowBlockPos);
                var resinNode = AlienVariantUtil.getResinNodeForType(this).getBlock();

                if (!blockState.is(resinNode) && !blockState.is(AVPBlockTags.ACID_IMMUNE)) {
                    level().setBlockAndUpdate(belowBlockPos, AlienVariantUtil.getResinNodeForType(this));
                }
            }
        }
    }

    @Override
    public float maxUpStep() {
        return 2.5F;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return AVPSoundEvents.ENTITY_QUEEN_IDLE;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return AVPSoundEvents.ENTITY_QUEEN_DEATH;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(DamageSource damageSource) {
        return AVPSoundEvents.ENTITY_QUEEN_HURT;
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVPFabric.config.statsConfigs.QUEEN_STATS.healthRegenPerSecond;
    }

    @Override
    public void runAttackAnimations() {
        var isClawAttack = random.nextBoolean();

        if (isClawAttack) {
            animationDispatcher.clawAttack();
        } else {
            animationDispatcher.tailAttack();
        }
    }

    // Queens are too large to be pushed by fluids.
    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    // Queens are too large to be pushed.
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public int maxJellyToGrowth() {
        return Integer.MAX_VALUE;
    }

    public QueenAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }
}
