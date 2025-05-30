package com.alien.common.gameplay.entity.living.alien.parasite.facehugger;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.parasite.Parasite;
import com.alien.common.model.alien.variant.AlienVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.gameplay.ai.goal.combat.LungeAtTargetGoal;
import com.avp.common.registry.init.entity_type.AVPEntityTypes;

public class Facehugger extends Parasite {

    public static AttributeSupplier.Builder createFacehuggerAttributes() {
        return applyFrom(AVP.config.statsConfigs.FACEHUGGER_STATS, Monster.createMonsterAttributes());
    }

    private final FacehuggerAnimationDispatcher animationDispatcher;

    public Facehugger(EntityType<? extends Facehugger> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new FacehuggerAnimationDispatcher(this);
        this.config = AVP.config.statsConfigs.FACEHUGGER_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant) {
        return getType(alienVariant, isRoyal());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new LungeAtTargetGoal(this, 0.75F, 20 * 3, 1, 12).setOnLungeCallback(this::runLungeAnimation));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.targetSelector.addGoal(
            1,
            new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, this::isValidHost)
        );
    }

    private void runLungeAnimation() {
        animationDispatcher.lunge();
    }

    @Override
    protected void playStepSound(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.015F, 2F);
    }

    @Override
    public boolean isPersistenceRequired() {
        return super.isPersistenceRequired() || attachmentManager.isAttachedToHost();
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return 0;
    }

    public FacehuggerAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }

    public static @Nullable EntityType<? extends Alien> getType(AlienVariant alienVariant, boolean isRoyal) {
        if (isRoyal) {
            return switch (alienVariant) {
                case NORMAL -> AVPEntityTypes.ROYAL_FACEHUGGER.get();
                case NETHER -> AVPEntityTypes.ROYAL_NETHER_FACEHUGGER.get();
                case ABERRANT -> AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get();
                case IRRADIATED -> null;
            };
        }

        return switch (alienVariant) {
            case NORMAL -> AVPEntityTypes.FACEHUGGER.get();
            case NETHER -> AVPEntityTypes.NETHER_FACEHUGGER.get();
            case ABERRANT -> AVPEntityTypes.ABERRANT_FACEHUGGER.get();
            case IRRADIATED -> null;
        };
    }
}
