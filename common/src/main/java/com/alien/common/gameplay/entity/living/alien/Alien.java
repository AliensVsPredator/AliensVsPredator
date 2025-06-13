package com.alien.common.gameplay.entity.living.alien;

import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.util.AcidBleedUtil;
import com.alien.common.util.AlienHurtUtil;
import com.alien.common.util.AlienTransitionUtil;
import com.bvanseg.just.functional.option.Option;
import com.google.common.base.Objects;
import com.lib.common.gameplay.entity.manager.GeneManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.config.AVPConfig;
import com.avp.common.registry.key.AVPBiomeKeys;
import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.common.registry.tag.AVPMobEffectTags;
import com.avp.common.util.MovementAnalyzer;

public abstract class Alien extends Monster {

    private static final String NBT_HOST_TYPE = "hostType";

    private static final String NBT_IS_POISONED = "isPoisoned";

    private static final String NBT_IS_ROYAL = "isRoyal";

    private static final String NBT_JELLY_COUNT = "jellyCount";

    public static final EntityDataAccessor<Boolean> IS_POISONED = SynchedEntityData.defineId(
        Alien.class,
        EntityDataSerializers.BOOLEAN
    );

    private static final EntityDataAccessor<Boolean> IS_ROYAL = SynchedEntityData.defineId(Alien.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<Integer> JELLY_COUNT = SynchedEntityData.defineId(
        Alien.class,
        EntityDataSerializers.INT
    );

    protected final GeneManager geneManager;

    protected final HiveManager hiveManager;

    protected final MovementAnalyzer movementAnalyzer;

    private Option<EntityType<?>> hostTypeOption;

    private int lastHurtTimeInTicks;

    protected AVPConfig.StatsConfigs.AdvancedStats config;

    protected Alien(EntityType<? extends Alien> entityType, Level level) {
        super(entityType, level);
        this.geneManager = new GeneManager(this);
        this.hiveManager = new HiveManager(this);
        this.hostTypeOption = Option.ofNullable(getDefaultHostType(entityType));
        this.movementAnalyzer = new MovementAnalyzer(this);
    }

    public abstract @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant);

    @Override
    public float maxUpStep() {
        return 1.5F;
    }

    private EntityType<? extends Entity> getDefaultHostType(EntityType<? extends Alien> entityType) {
        if (
            entityType.is(AVPEntityTypeTags.RUNNERS)
                || entityType.is(AVPEntityTypeTags.PROWLERS)
                || entityType.is(AVPEntityTypeTags.CRUSHERS)
        ) {
            return EntityType.PIG;
        }

        return EntityType.VILLAGER;
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

        if (livingEntity instanceof ServerPlayer player) {
            hiveManager.hive()
                .filter(hive -> hive.getSpaceManager().isEntityWithinHive(player))
                .ifSome(hive -> hive.getBossBarManager().trackPlayer(player));
        }
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_POISONED, false);
        builder.define(IS_ROYAL, false);
        builder.define(JELLY_COUNT, 0);
    }

    public AlienVariant getVariant() {
        if (isAberrant()) {
            return AlienVariant.ABERRANT;
        } else if (isIrradiated()) {
            return AlienVariant.IRRADIATED;
        } else if (isNetherAfflicted()) {
            return AlienVariant.NETHER;
        }

        return AlienVariant.NORMAL;
    }

    public boolean isAberrant() {
        return Objects.equal(getType(), getTypeForVariant(AlienVariant.ABERRANT));
    }

    public boolean isIrradiated() {
        return Objects.equal(getType(), getTypeForVariant(AlienVariant.IRRADIATED));
    }

    public boolean isNetherAfflicted() {
        return Objects.equal(getType(), getTypeForVariant(AlienVariant.NETHER));
    }

    private void applyMalusBasedOnVariant() {
        if (isNetherAfflicted()) {
            setPathfindingMalus(PathType.LAVA, 0.0F);
            setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
            setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        } else {
            setPathfindingMalus(PathType.LAVA, PathType.LAVA.getMalus());
            setPathfindingMalus(PathType.DANGER_FIRE, PathType.DANGER_FIRE.getMalus());
            setPathfindingMalus(PathType.DAMAGE_FIRE, PathType.DAMAGE_FIRE.getMalus());
        }
    }

    public boolean isPoisoned() {
        return entityData.get(IS_POISONED);
    }

    public void setPoisoned(boolean isPoisoned) {
        entityData.set(IS_POISONED, isPoisoned);
    }

    public boolean isRoyal() {
        return entityData.get(IS_ROYAL);
    }

    public void setRoyal(boolean isRoyal) {
        entityData.set(IS_ROYAL, isRoyal);
    }

    @Override
    public void tick() {
        super.tick();
        movementAnalyzer.tick();
        hiveManager.tick();

        if (!level().isClientSide) {
            healPassively();
            applyMalusBasedOnVariant();
            applyDynamicAttributes(config);
            becomeIrradiated();
        }
    }

    /**
     * 10% chance when in Nuked Biome to become Irradiated
     */
    private void becomeIrradiated() {
        if (tickCount % 60 != 0) {
            return;
        }

        if (!level().getBiome(blockPosition()).is(AVPBiomeKeys.NUKED_BIOME)) {
            return;
        }

        if (!isAlive()) {
            return;
        }

        if (getRandom().nextIntBetweenInclusive(1, 100) >= 90) {
            AlienTransitionUtil.transitionIntoVariant(this, AlienVariant.IRRADIATED);
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
    public boolean hurt(@NotNull DamageSource damageSource, float damage) {
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

    protected abstract float getHealthRegenPerSecond();

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
    public boolean fireImmune() {
        return isNetherAfflicted();
    }

    @Override
    public boolean isPersistenceRequired() {
        return super.isPersistenceRequired()
            || hiveManager.hive()
                .filter(
                    // If the hive is angry, then the alien shouldn't despawn.
                    hive -> hive.isAngry()
                        // OR if this alien is the hive leader, then they shouldn't despawn, either.
                        || hive.getLeadershipManager().isLeader(this)
                )
                .isSome();
    }

    @Override
    public void remove(@NotNull RemovalReason removalReason) {
        super.remove(removalReason);

        switch (removalReason) {
            case KILLED, DISCARDED -> hiveManager.hive().ifSome(hive -> hive.removeHiveMember(this));
            case UNLOADED_TO_CHUNK, UNLOADED_WITH_PLAYER, CHANGED_DIMENSION -> { /* NO-OP */ }
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        geneManager.load(compoundTag);
        hiveManager.load(compoundTag);

        if (compoundTag.contains(NBT_IS_POISONED)) {
            setPoisoned(compoundTag.getBoolean(NBT_IS_POISONED));
        }

        if (compoundTag.contains(NBT_IS_ROYAL)) {
            setRoyal(compoundTag.getBoolean(NBT_IS_ROYAL));
        }

        if (compoundTag.contains(NBT_JELLY_COUNT)) {
            getEntityData().set(JELLY_COUNT, compoundTag.getInt(NBT_JELLY_COUNT));
        }

        var resourceLocationString = compoundTag.getString(NBT_HOST_TYPE);
        var resourceLocation = ResourceLocation.parse(resourceLocationString);
        var entityTypeHolderOptional = BuiltInRegistries.ENTITY_TYPE.getHolder(resourceLocation);

        entityTypeHolderOptional.ifPresent($ -> this.hostTypeOption = Option.some(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation)));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        geneManager.save(compoundTag);
        hiveManager.save(compoundTag);
        compoundTag.putBoolean(NBT_IS_POISONED, isPoisoned());
        compoundTag.putBoolean(NBT_IS_ROYAL, isRoyal());
        compoundTag.putInt(NBT_JELLY_COUNT, getEntityData().get(JELLY_COUNT));

        hostTypeOption.ifSome(hostType -> {
            var resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(hostTypeOption.unwrap());
            compoundTag.putString(NBT_HOST_TYPE, resourceLocation.toString());
        });
    }

    public GeneManager getGeneManager() {
        return geneManager;
    }

    public HiveManager getHiveManager() {
        return hiveManager;
    }

    public Option<EntityType<?>> getHostType() {
        return hostTypeOption;
    }

    public int getLastHurtTimeInTicks() {
        return lastHurtTimeInTicks;
    }

    public int getMaxJellyToGrowth() {
        return 10;
    }

    public MovementAnalyzer getMovementAnalyzer() {
        return movementAnalyzer;
    }

    public void setHostType(EntityType<?> hostType) {
        this.hostTypeOption = Option.some(hostType);
    }

    public void applyDynamicAttributes(AVPConfig.StatsConfigs.AdvancedStats config) {
        if (isAberrant()) {
            applyAttributes(config, AVP.config.statsConfigs.ABERRANT_STATS_MULTIPLIER);
        } else if (isIrradiated()) {
            applyAttributes(config, AVP.config.statsConfigs.IRRADIATED_STATS_MULTIPLIER);
        }
    }

    private void applyAttributes(AVPConfig.StatsConfigs.AdvancedStats config, float scaleFactor) {
        setAttribute(Attributes.MAX_HEALTH, config.health * scaleFactor);
        setAttribute(Attributes.ATTACK_DAMAGE, config.attackDamage * scaleFactor);
        setAttribute(Attributes.KNOCKBACK_RESISTANCE, config.knockbackResistance * scaleFactor);
        setAttribute(Attributes.ARMOR, config.armor * scaleFactor);
        setAttribute(Attributes.ARMOR_TOUGHNESS, config.armorToughness * scaleFactor);
    }

    private void setAttribute(Holder<Attribute> attribute, float value) {
        var instance = getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    public static AttributeSupplier.Builder applyFrom(AVPConfig.StatsConfigs.AdvancedStats config, AttributeSupplier.Builder builder) {
        builder.add(Attributes.ARMOR, config.armor);
        builder.add(Attributes.ARMOR_TOUGHNESS, config.armorToughness);
        builder.add(Attributes.ATTACK_DAMAGE, config.attackDamage);
        builder.add(Attributes.FOLLOW_RANGE, config.followRange);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, config.knockbackResistance);
        builder.add(Attributes.MAX_HEALTH, config.health);
        builder.add(Attributes.MOVEMENT_SPEED, config.moveSpeed);
        builder.add(Attributes.JUMP_STRENGTH, 0.1F);

        return builder;
    }
}
