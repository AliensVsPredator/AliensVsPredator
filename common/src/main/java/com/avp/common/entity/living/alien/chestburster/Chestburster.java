package com.avp.common.entity.living.alien.chestburster;

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
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.alien.manager.GrowthManager;
import com.avp.common.entity.living.alien.manager.ResinManager;
import com.avp.common.entity.living.alien.manager.resin.ResinData;
import com.avp.common.entity.living.alien.manager.resin.ResinProducer;
import com.avp.common.entity.living.alien.util.AlienPredicates;
import com.avp.common.entity.living.alien.util.AlienVariantUtil;
import com.avp.common.entity.living.alien.xenomorph.Xenomorph;
import com.avp.common.entity.living.alien.xenomorph.util.XenomorphGrowthUtil;
import com.avp.common.entity.living.gene.GeneKeys;
import com.avp.common.entity.living.gene.behavior.GeneDecoders;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;
import com.avp.common.util.AVPPredicates;

public class Chestburster extends Alien implements ResinProducer {

    public static AttributeSupplier.Builder createChestbursterAttributes() {
        return applyFrom(AVP.config.statsConfigs.CHESTBURSTER_STATS, Monster.createMonsterAttributes());
    }

    private final ChestbursterAnimationDispatcher animationDispatcher;

    private final GrowthManager growthManager;

    private final ResinManager resinManager;

    public Chestburster(EntityType<? extends Chestburster> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new ChestbursterAnimationDispatcher(this);
        this.growthManager = new GrowthManager(this, XenomorphGrowthUtil.GROW_UP_CALLBACK)
            .setGrowOverTime(true)
            .setGrowthTimeReductionMultiplierProvider(
                () -> geneManager.get(GeneKeys.GROWTH_SPEED, GeneDecoders.GROWTH_SPEED)
            );
        this.resinManager = new ResinManager(this, createResinData())
            .setBonusResinProvider(
                () -> geneManager.get(GeneKeys.BONUS_RESIN_PRODUCTION, GeneDecoders.BONUS_RESIN_PRODUCTION).intValue()
            );
        this.config = AVP.config.statsConfigs.CHESTBURSTER_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getAberrantType() {
        return isRoyal() ? AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get() : AVPEntityTypes.ABERRANT_CHESTBURSTER.get();
    }

    @Override
    public @Nullable EntityType<? extends Alien> getIrradiatedType() {
        return null;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getNetherType() {
        return isRoyal() ? AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER.get() : AVPEntityTypes.NETHER_CHESTBURSTER.get();
    }

    @Override
    public @Nullable EntityType<? extends Alien> getDefaultType() {
        return isRoyal() ? AVPEntityTypes.ROYAL_CHESTBURSTER.get() : AVPEntityTypes.CHESTBURSTER.get();
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
        resinManager.tick();

        if (!this.level().isClientSide() && !this.isIrradiated()) {
            var type = AlienVariantUtil.getVariantTypeFor(this);
            var growthStage = AlienLifecycleRegistry.getOrNull(null, type);

            if (
                growthStage != null && !this.getEntityData().get(Xenomorph.IS_POISONED) && this.getEntityData()
                    .get(Xenomorph.JELLY_COUNT) == this.maxJellyToGrowth()
            ) {
                this.growthManager().grow(growthStage);
            }
        }
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.CHESTBURSTER_STATS.healthRegenPerSecond;
    }

    protected @NotNull ResinData createResinData() {
        return new ResinData(0, 8, 1, AVP.config.statsConfigs.CHESTBURSTER_STATS.nestTickrate);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        growthManager.load(compoundTag);
        resinManager.load(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        growthManager.save(compoundTag);
        resinManager.save(compoundTag);
    }

    @Override
    public ResinManager getResinManager() {
        return resinManager;
    }

    public GrowthManager growthManager() {
        return growthManager;
    }

    @Override
    public int maxJellyToGrowth() {
        return 1;
    }

    public ChestbursterAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }
}
