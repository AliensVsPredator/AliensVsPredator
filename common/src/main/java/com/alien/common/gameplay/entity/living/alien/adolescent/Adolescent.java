package com.alien.common.gameplay.entity.living.alien.adolescent;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.GrowthManager;
import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.model.resin.ResinData;
import com.alien.common.registry.init.AlienEntityTypes;
import com.alien.common.util.AlienPredicates;
import com.alien.common.util.XenomorphGrowthUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.util.AVPPredicates;

public class Adolescent extends Alien {

    public static AttributeSupplier.Builder createAdolescentAttributes() {
        return applyFrom(AVP.config.statsConfigs.ADOLESCENT_STATS, Monster.createMonsterAttributes());
    }

    private final AdolescentAnimationDispatcher animationDispatcher;

    private final GrowthManager growthManager;

    public Adolescent(EntityType<? extends Adolescent> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new AdolescentAnimationDispatcher(this);
        this.growthManager = new GrowthManager(this, XenomorphGrowthUtil.GROW_UP_CALLBACK)
            .setGrowOverTime(true);
        this.config = AVP.config.statsConfigs.ADOLESCENT_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant) {
        return getType(alienVariant, isRoyal());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(
            3,
            new AvoidEntityGoal<>(
                this,
                LivingEntity.class,
                8,
                1,
                1.2,
                entity -> entity instanceof Alien alien
                    ? AlienPredicates.areAliensEnemies(this, alien)
                    : !AVPPredicates.IS_IMMORTAL.test(entity)
            )
        );
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5));
    }

    @Override
    public void tick() {
        super.tick();
        growthManager.tick();
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.ADOLESCENT_STATS.healthRegenPerSecond;
    }

    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 8, 1, AVP.config.statsConfigs.ADOLESCENT_STATS.nestTickrate);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        growthManager.load(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        growthManager.save(compoundTag);
    }

    @Override
    public int getMaxJellyToGrowth() {
        return 1;
    }

    public AdolescentAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }

    public static @Nullable EntityType<? extends Alien> getType(AlienVariant alienVariant, boolean isRoyal) {
        if (isRoyal) {
            return switch (alienVariant) {
                case NORMAL -> AlienEntityTypes.ROYAL_ADOLESCENT.get();
                case NETHER -> AlienEntityTypes.ROYAL_NETHER_ADOLESCENT.get();
                case ABERRANT -> AlienEntityTypes.ROYAL_ABERRANT_ADOLESCENT.get();
                case IRRADIATED -> null;
            };
        }

        return switch (alienVariant) {
            case NORMAL -> AlienEntityTypes.ADOLESCENT.get();
            case NETHER -> AlienEntityTypes.NETHER_ADOLESCENT.get();
            case ABERRANT -> AlienEntityTypes.ABERRANT_ADOLESCENT.get();
            case IRRADIATED -> null;
        };
    }
}
