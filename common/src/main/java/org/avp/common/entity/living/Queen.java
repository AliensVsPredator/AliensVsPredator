package org.avp.common.entity.living;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.avp.api.entity.Boss;
import org.avp.common.util.AVPPredicates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.avp.common.entity.ai.AIUtils;
import org.avp.common.sound.AVPSoundEvents;

public class Queen extends Monster implements Boss, GeoEntity {

    private static final int SPAWN_MIN_DISTANCE_IN_BLOCKS = 512;
    private static final int SPAWN_DISTANCE_SQUARED = SPAWN_MIN_DISTANCE_IN_BLOCKS * SPAWN_MIN_DISTANCE_IN_BLOCKS;

    public static boolean anyNearbyQueens(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos) {
        var allQueens = serverLevelAccessor.getLevel().getEntities(EntityTypeTest.forClass(Queen.class), AVPPredicates.ALWAYS_TRUE);
        return allQueens.stream().anyMatch(queen -> queen.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < SPAWN_DISTANCE_SQUARED);
    }

    public static boolean checkQueenSpawnRules(
        EntityType<? extends Monster> entityType,
        ServerLevelAccessor serverLevelAccessor,
        MobSpawnType mobSpawnType,
        BlockPos blockPos,
        RandomSource randomSource
    ) {
        return Monster.checkMonsterSpawnRules(
                entityType,
                serverLevelAccessor,
                mobSpawnType,
                blockPos,
                randomSource
            ) &&
            !anyNearbyQueens(serverLevelAccessor, blockPos);
    }

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
        this.getDisplayName(),
        BossEvent.BossBarColor.GREEN,
        BossEvent.BossBarOverlay.PROGRESS
    ).setDarkenScreen(true);

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public Queen(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1);
        this.setPersistenceRequired();
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        this.playSound(AVPSoundEvents.INSTANCE.entityXenomorphAttack.get());

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.knockback(
                5,
                Mth.sin(this.getYRot() * Mth.DEG_TO_RAD),
                -Mth.cos(this.getYRot() * Mth.DEG_TO_RAD)
            );
        }
        return super.doHurtTarget(entity);
    }

    @Override
    protected void registerGoals() {
        AIUtils.addBasicAlienAI(this, goalSelector, targetSelector);
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Queen.class, true, Queen.class::isInstance));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Dracomorph.class, true, Dracomorph.class::isInstance));
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return AVPSoundEvents.INSTANCE.entityQueenAmbient.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return AVPSoundEvents.INSTANCE.entityQueenDeath.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return AVPSoundEvents.INSTANCE.entityQueenHurt.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO:
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public ServerBossEvent getBossEvent() {
        return bossEvent;
    }
}
