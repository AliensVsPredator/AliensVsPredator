package org.avp.common.entity.living;

import mod.azure.azurelib.common.api.common.animatable.GeoEntity;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.avp.api.entity.Boss;
import org.avp.common.util.AVPPredicates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.avp.common.sound.AVPSoundEvents;
import org.avp.common.tag.AVPEntityTags;
import org.avp.common.tag.AVPItemTags;

public class Yautja extends Monster implements Boss, GeoEntity {

    private static final int SPAWN_MIN_Y_LEVEL = 60;
    private static final int SPAWN_MIN_DISTANCE_IN_BLOCKS = 256;
    private static final int SPAWN_DISTANCE_SQUARED = SPAWN_MIN_DISTANCE_IN_BLOCKS * SPAWN_MIN_DISTANCE_IN_BLOCKS;

    public static boolean anyNearbyPredators(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos) {
        var allYautja = serverLevelAccessor.getLevel().getEntities(EntityTypeTest.forClass(Yautja.class), AVPPredicates.ALWAYS_TRUE);
        return allYautja.stream().anyMatch(yautja -> yautja.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < SPAWN_DISTANCE_SQUARED);
    }

    public static boolean checkPredatorSpawnRules(
        EntityType<? extends Monster> entityType,
        ServerLevelAccessor serverLevelAccessor,
        MobSpawnType mobSpawnType,
        BlockPos blockPos,
        RandomSource randomSource
    ) {
        return blockPos.getY() > SPAWN_MIN_Y_LEVEL &&
            Monster.checkAnyLightMonsterSpawnRules(
                entityType,
                serverLevelAccessor,
                mobSpawnType,
                blockPos,
                randomSource
            ) &&
            !anyNearbyPredators(serverLevelAccessor, blockPos);
    }

    private static final EntityDataAccessor<Boolean> HAS_HELMET = SynchedEntityData.defineId(
        Yautja.class,
        EntityDataSerializers.BOOLEAN
    );

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
        this.getDisplayName(),
        BossEvent.BossBarColor.RED,
        BossEvent.BossBarOverlay.PROGRESS
    ).setDarkenScreen(false);

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public Yautja(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1));

        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(
            2,
            new NearestAttackableTargetGoal<>(
                this,
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

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_HELMET, this.random.nextBoolean());
    }

    public boolean hasHelmet() {
        return this.entityData.get(HAS_HELMET);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return AVPSoundEvents.INSTANCE.entityYautjaAmbient.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return AVPSoundEvents.INSTANCE.entityYautjaDeath.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return AVPSoundEvents.INSTANCE.entityYautjaHurt.get();
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
