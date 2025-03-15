package com.avp.common.entity.living.alien;

import com.avp.common.worldgen.biome.AVPBiomes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.config.ConfigPropertyMappings;
import com.avp.common.gene.GeneKeys;
import com.avp.common.level.effect.AVPMobEffectTags;
import com.avp.common.manager.GeneManager;
import com.avp.common.manager.HiveManager;
import com.avp.common.util.AcidBleedUtil;
import com.avp.common.util.AlienHurtUtil;

public abstract class Alien extends Monster {

    private static final String IS_IRRADIATED_KEY = "isIrradiated";

    private static final String IS_ABERRANT_KEY = "isAberrant";

    private static final String IS_NETHER_AFFLICTED_KEY = "isNetherAfflicted";

    private static final EntityDataAccessor<Boolean> IS_ABERRANT = SynchedEntityData.defineId(Alien.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> IS_IRRADIATED = SynchedEntityData.defineId(Alien.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> IS_NETHER_AFFLICTED = SynchedEntityData.defineId(
        Alien.class,
        EntityDataSerializers.BOOLEAN
    );

    public static final EntityDataAccessor<Integer> JELLY_COUNT = SynchedEntityData.defineId(
            Alien.class,
            EntityDataSerializers.INT
    );

    public static final EntityDataAccessor<Boolean> IS_POISONED = SynchedEntityData.defineId(
            Alien.class,
            EntityDataSerializers.BOOLEAN
    );

    protected final GeneManager geneManager;

    protected final HiveManager hiveManager;

    private int lastHurtTimeInTicks;

    protected Alien(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.geneManager = new GeneManager(this);
        this.hiveManager = new HiveManager(this);
    }

    protected boolean canBleedAcid() {
        return true;
    }

    protected boolean canHeal() {
        var requiredTicksAfterHurtToHeal = 10 * 20;
        return this.getTarget() == null && tickCount > getLastHurtByMobTimestamp() + requiredTicksAfterHurtToHeal;
    }

    @Override
    public void setTarget(@Nullable LivingEntity livingEntity) {
        super.setTarget(livingEntity);

        var hive = hiveManager.hiveOrNull();

        if (hive != null && livingEntity instanceof ServerPlayer player && hive.isEntityWithinHive(player)) {
            hive.bossEvent().addPlayer(player);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_ABERRANT, false);
        builder.define(IS_NETHER_AFFLICTED, false);
        builder.define(IS_IRRADIATED, false);
        builder.define(IS_POISONED, false);
        builder.define(JELLY_COUNT, 0);
    }

    public boolean isIrraiated() {
        return entityData.get(IS_IRRADIATED);
    }

    public void setIrraiated(boolean isIrraiated) {
        entityData.set(IS_IRRADIATED, isIrraiated);
    }

    public boolean isAberrant() {
        return entityData.get(IS_ABERRANT);
    }

    public void setAberrant(boolean isAberrant) {
        entityData.set(IS_ABERRANT, isAberrant);
    }

    public boolean isNetherAfflicted() {
        return entityData.get(IS_NETHER_AFFLICTED);
    }

    public void setNetherAfflicted(boolean isNetherAfflicted) {
        entityData.set(IS_NETHER_AFFLICTED, isNetherAfflicted);

        if (isNetherAfflicted) {
            setPathfindingMalus(PathType.LAVA, 0.0F);
            setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
            setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        } else {
            setPathfindingMalus(PathType.LAVA, PathType.LAVA.getMalus());
            setPathfindingMalus(PathType.DANGER_FIRE, PathType.DANGER_FIRE.getMalus());
            setPathfindingMalus(PathType.DAMAGE_FIRE, PathType.DAMAGE_FIRE.getMalus());
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
        ServerLevelAccessor serverLevelAccessor,
        DifficultyInstance difficultyInstance,
        MobSpawnType mobSpawnType,
        @Nullable SpawnGroupData spawnGroupData
    ) {
        updateStateBasedOnGenetics();
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
    }

    public void updateStateBasedOnGenetics() {
        var hasMinimumGeneIntegrity = geneManager.isMinimized(GeneKeys.GENETIC_INTEGRITY);
        if (!isNetherAfflicted() && !isIrraiated()) {
            setAberrant(hasMinimumGeneIntegrity);
        }

        var hasMaximumFireResistance = geneManager.isMinimized(GeneKeys.COLD_RESISTANCE) && geneManager.isMaximized(
            GeneKeys.FIRE_RESISTANCE
        );
        if (!isAberrant() && !isIrraiated()) {
            setNetherAfflicted(hasMaximumFireResistance);
        }
    }

    @Override
    public void tick() {
        super.tick();
        hiveManager.tick();

        if (!level().isClientSide) {
            updateStateBasedOnGenetics();
            healPassively();
            // 10% chance when in Nuked Biome to become Aberrant
            if (this.tickCount % 60 == 0 && this.level().getBiome(this.blockPosition()).is(AVPBiomes.NUKED_BIOME) && this.getRandom().nextIntBetweenInclusive(1, 100) >= 90) {
                this.setIrraiated(true);
            }
        }
    }

    private void healPassively() {
        if (tickCount % 20 != 0)
            return;
        if (getHealth() >= getMaxHealth())
            return;
        if (!isAlive())
            return;

        if (canHeal()) {
            heal(getHealthRegenPerSecond());
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        var isHurt = AlienHurtUtil.isHurt(this, damageSource, damage, super::hurt);

        if (isHurt) {
            lastHurtTimeInTicks = tickCount;

            if (canBleedAcid() && damageSource != damageSources().genericKill()) {
                var randomPos = AcidBleedUtil.computeRandomPosFromBoundingBox(this);
                AcidBleedUtil.spawnAcid(this, damage, randomPos);
            }
        }

        return isHurt;
    }

    protected float getHealthRegenPerSecond() {
        var properties = AVP.STATS_CONFIG.properties();
        var container = ConfigPropertyMappings.BY_ENTITY_TYPE.get(getType());

        return container == null ? 1F : properties.getOrThrow(container.healthRegenPerSecond());
    }

    // Prevent the chestburster from drowning or otherwise running out of air.
    @Override
    public int getAirSupply() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.9F;
    }

    // Prevent fall damage below certain distance values.
    @Override
    public int calculateFallDamage(float fallDistance, float damageMultiplier) {
        return fallDistance < 16 ? 0 : super.calculateFallDamage(fallDistance, damageMultiplier);
    }

    // Max fall distance for pathfinding purposes.
    @Override
    public int getMaxFallDistance() {
        return 14;
    }

    // Prevents chestbursters from being poisoned.
    @Override
    public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
        if (mobEffectInstance.getEffect().is(AVPMobEffectTags.DOES_NOT_AFFECT_ALIENS)) {
            return false;
        }

        return super.canBeAffected(mobEffectInstance);
    }

    @Override
    public boolean dampensVibrations() {
        return true;
    }

    @Override
    public boolean isPersistenceRequired() {
        var hive = hiveManager.hiveOrNull();
        var isHiveLeader = hive != null && hive.hiveLeader().filter(leader -> leader.getUUID().equals(getUUID())).isPresent();
        return super.isPersistenceRequired() || isHiveLeader;
    }

    @Override
    public void remove(RemovalReason removalReason) {
        super.remove(removalReason);

        switch (removalReason) {
            case KILLED, DISCARDED -> {
                var hive = hiveManager.hiveOrNull();

                if (hive != null) {
                    hive.removeHiveMember(this);
                }
            }
            case UNLOADED_TO_CHUNK, UNLOADED_WITH_PLAYER, CHANGED_DIMENSION -> { /* NO-OP */ }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.getEntityData().set(IS_POISONED, compoundTag.getBoolean("isPoisoned"));
        this.getEntityData().set(JELLY_COUNT, compoundTag.getInt("jellyCount"));
        geneManager.load(compoundTag);
        hiveManager.load(compoundTag);
        setAberrant(compoundTag.getBoolean(IS_ABERRANT_KEY));
        setIrraiated(compoundTag.getBoolean(IS_IRRADIATED_KEY));
        setNetherAfflicted(compoundTag.getBoolean(IS_NETHER_AFFLICTED_KEY));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("isPoisoned", this.getEntityData().get(IS_POISONED));
        compoundTag.putInt("jellyCount", this.getEntityData().get(JELLY_COUNT));
        geneManager.save(compoundTag);
        hiveManager.save(compoundTag);
        compoundTag.putBoolean(IS_ABERRANT_KEY, isAberrant());
        compoundTag.putBoolean(IS_IRRADIATED_KEY, isIrraiated());
        compoundTag.putBoolean(IS_NETHER_AFFLICTED_KEY, isNetherAfflicted());
    }

    public GeneManager geneManager() {
        return geneManager;
    }

    public HiveManager hiveManager() {
        return hiveManager;
    }

    public int lastHurtTimeInTicks() {
        return lastHurtTimeInTicks;
    }

    public int maxJellyToGrowth() {
        return 10;
    }
}
