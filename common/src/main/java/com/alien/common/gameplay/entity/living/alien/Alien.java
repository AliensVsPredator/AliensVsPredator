package com.alien.common.gameplay.entity.living.alien;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.entity.living.alien.xenomorph.drone.Drone;
import com.alien.common.gameplay.entity.living.alien.xenomorph.runner.Runner;
import com.alien.common.gameplay.level.saveddata.HiveLevelData;
import com.alien.common.gameplay.level.saveddata.StrainLeakData;
import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.util.AcidBleedUtil;
import com.alien.common.util.AlienTransitionUtil;
import com.bvanseg.just.functional.option.Option;
import com.lib.common.gameplay.entity.manager.GeneManager;
import com.lib.common.gameplay.entity.manager.VibrationSystemManager;
import com.lib.common.model.GeneCarrier;
import com.lib.common.network.DataAccessor;
import com.lib.common.network.DataUser;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.avp.AVP;
import com.avp.common.config.AVPConfig;
import com.avp.common.registry.init.AVPDataKeys;
import com.avp.common.registry.key.AVPBiomeKeys;
import com.avp.common.registry.tag.AVPDamageTypesTags;
import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.common.registry.tag.AVPMobEffectTags;
import com.avp.common.util.MovementAnalyzer;

public abstract class Alien extends Monster implements DataUser {

    private static final String NBT_HOST_TYPE = "hostType";

    private static final String NBT_JELLY_COUNT = "jellyCount";

    public final DataAccessor<Boolean> hasTarget;

    public final DataAccessor<Boolean> isPoisoned;

    public final DataAccessor<Boolean> isMovingHorizontally;

    protected final HiveManager hiveManager;

    protected final MovementAnalyzer movementAnalyzer;

    private final VibrationSystemManager vibrationSystemManager;

    private Option<EntityType<?>> hostTypeOption;

    private int jellyCount;

    private int lastHurtTimeInTicks;

    protected AVPConfig.StatsConfigs.AdvancedStats config;

    protected Alien(EntityType<? extends Alien> entityType, Level level) {
        super(entityType, level);

        this.hasTarget = new DataAccessor<>(this, AVPDataKeys.ENTITY_HAS_TARGET);
        this.isPoisoned = new DataAccessor<>(this, AVPDataKeys.ALIEN_IS_POISONED);
        this.isMovingHorizontally = new DataAccessor<>(this, AVPDataKeys.ENTITY_IS_MOVING_HORIZONTALLY);

        this.hiveManager = new HiveManager(this);
        this.movementAnalyzer = new MovementAnalyzer(this);
        this.vibrationSystemManager = createVibrationSystemManager();

        this.hostTypeOption = Option.ofNullable(getDefaultHostType(entityType));
        this.jellyCount = 0;
        this.lastHurtTimeInTicks = 0;
    }

    public abstract @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant);

    protected abstract float getHealthRegenPerSecond();

    protected VibrationSystemManager createVibrationSystemManager() {
        return new VibrationSystemManager(this, 2.5F, 32);
    }

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
        } else if (entityType.is(AVPEntityTypeTags.SPITTERS)) {
            return EntityType.LLAMA;
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
        return Objects.equals(getType(), getTypeForVariant(AlienVariant.ABERRANT));
    }

    public boolean isIrradiated() {
        return Objects.equals(getType(), getTypeForVariant(AlienVariant.IRRADIATED));
    }

    public boolean isNetherAfflicted() {
        return Objects.equals(getType(), getTypeForVariant(AlienVariant.NETHER));
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

    public int getJellyCount() {
        return jellyCount;
    }

    public void setJellyCount(int jellyCount) {
        this.jellyCount = Math.max(jellyCount, 0);
    }

    public boolean isPoisoned() {
        return isPoisoned.get();
    }

    public void setPoisoned(boolean isPoisoned) {
        this.isPoisoned.set(isPoisoned);
    }

    public boolean isRoyal() {
        return getType().is(AVPEntityTypeTags.ROYAL_ALIENS);
    }

    protected boolean canEntityRideAlien(@NotNull Entity passenger) {
        return false;
    }

    @Override
    protected final boolean canAddPassenger(@NotNull Entity passenger) {
        return super.canAddPassenger(passenger) && canEntityRideAlien(passenger);
    }

    protected boolean canAlienRideVehicle(@NotNull Entity vehicle) {
        return !(vehicle instanceof Boat) && !(vehicle instanceof Minecart);
    }

    @Override
    protected final boolean canRide(@NotNull Entity vehicle) {
        return super.canRide(vehicle) && canAlienRideVehicle(vehicle);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
        @NotNull ServerLevelAccessor level,
        @NotNull DifficultyInstance difficulty,
        @NotNull MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnGroupData
    ) {
        var alienVariantType = AlienVariantTypes.getFor(getVariant());

        HiveLevelData.getOrCreate(level.getLevel())
            .andThen(
                hiveLevelData -> hiveLevelData.findNearestHive(
                    blockPosition(),
                    // Find the nearest hive for this alien type's variant type.
                    hive -> Objects.equals(hive.getVariant(), alienVariantType.variant())
                )
            )
            .ifSome(hive -> {
                var joinedHiveSuccessfully = hiveManager.tryJoinHive(hive);

                if (joinedHiveSuccessfully) {
                    // Decrease the reserve count for this entity's type.
                    hive.getReserveManager().add(getType(), -1);
                    // Apply genetics of hive leader to this alien.
                    hive.getLeadershipManager()
                        .getLeader()
                        .map(leader -> ((GeneCarrier) leader).getOrCreateGeneManager().getGeneContainer())
                        .ifSome(geneContainer -> {
                            var alienGeneCarrier = ((GeneCarrier) this).getOrCreateGeneManager().getGeneContainer();
                            geneContainer.transfer(alienGeneCarrier, true);
                        });
                }
            });

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void tick() {
        super.tick();
        hiveManager.tick();
        vibrationSystemManager.tick();

        if (!level().isClientSide) {
            movementAnalyzer.tick();

            hasTarget.set(getTarget() != null);
            isMovingHorizontally.set(movementAnalyzer.isMovingHorizontally());

            if (getVehicle() != null && !canRide(getVehicle())) {
                stopRiding();
            }

            if (!getPassengers().isEmpty()) {
                var passengersToRemove = getPassengers().stream()
                    .filter(Predicate.not(this::canEntityRideAlien))
                    .toList();

                passengersToRemove.forEach(Entity::stopRiding);
            }

            healPassively();
            applyMalusBasedOnVariant();
            applyDynamicAttributes(config);
            becomeIrradiated();
        }
    }

    @Override
    public void updateDynamicGameEventListener(@NotNull BiConsumer<DynamicGameEventListener<?>, ServerLevel> biConsumer) {
        vibrationSystemManager.updateDynamicGameEventListener(biConsumer);
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
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.is(AVPDamageTypesTags.DOES_NOT_HURT_ALIENS) || super.isInvulnerableTo(damageSource);
    }

    @Override
    public boolean killedEntity(@NotNull ServerLevel level, @NotNull LivingEntity entity) {
        var killedEntity = super.killedEntity(level, entity);

        if (killedEntity) {
            hiveManager.hive().ifSome(hive -> {
                var droneType = Drone.getType(hive.getVariant());
                hive.getReserveManager().add(droneType, 1);
                var runnerType = Runner.getType(hive.getVariant());
                hive.getReserveManager().add(runnerType, 1);
            });
        }

        return killedEntity;
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float damage) {
        var isHurt = super.hurt(damageSource, damage);

        if (isHurt) {
            this.lastHurtTimeInTicks = tickCount;

            var alienVariantType = AlienVariantTypes.getFor(this);

            if (damageSource.getEntity() != null) {
                // Cry for help so that nearby vents may try and summon help.
                gameEvent(alienVariantType.cryForHelpEvent().getHolder());
            }

            if (canBleedAcid() && damageSource != damageSources().genericKill()) {
                var randomPos = AcidBleedUtil.computeRandomPosFromBoundingBox(this);
                AcidBleedUtil.spawnAcid(this, damage, randomPos);
            }
        }

        return isHurt;
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
            case KILLED -> hiveManager.hive().ifSome(hive -> hive.removeHiveMember(this));
            case DISCARDED -> hiveManager.hive().ifSome(hive -> {
                hive.removeHiveMember(this);

                if (hive.getSpaceManager().isEntityWithinHive(this)) {
                    hive.getReserveManager().add(getType(), 1);
                } else {
                    StrainLeakData.getOrCreate(level())
                        .ifSome(strainLeakData -> {
                            var alienVariant = getVariant();
                            var alienVariantType = AlienVariantTypes.getFor(this);

                            if (strainLeakData.hasVariant(alienVariant) || !(level() instanceof ServerLevel serverLevel)) {
                                return;
                            }

                            var strainBasedLeakMessage = getStrainLeakMessageForVariant(alienVariant);

                            if (strainBasedLeakMessage == null) {
                                return;
                            }

                            strainLeakData.addVariant(alienVariant);

                            for (var player : serverLevel.players()) {
                                player.sendSystemMessage(
                                    Component.literal(strainBasedLeakMessage)
                                        .withStyle(alienVariantType.chatColor(), ChatFormatting.ITALIC)
                                );
                            }
                        });
                }
            });
            case UNLOADED_TO_CHUNK, UNLOADED_WITH_PLAYER, CHANGED_DIMENSION -> { /* NO-OP */ }
        }
    }

    // TODO: Use level-specific phrasing here.
    private @Nullable String getStrainLeakMessageForVariant(AlienVariant alienVariant) {
        return switch (alienVariant) {
            case NORMAL -> "The perfect organism has found a new world to conquer...";
            case NETHER -> "Hell has found its way into this plane of existence...";
            case ABERRANT -> "Genetic experiments have found their way into the wide open world...";
            case IRRADIATED -> null;
        };
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        hiveManager.load(compoundTag);

        if (compoundTag.contains(NBT_JELLY_COUNT)) {
            setJellyCount(compoundTag.getInt(NBT_JELLY_COUNT));
        }

        if (compoundTag.contains(NBT_HOST_TYPE)) {
            var resourceLocationString = compoundTag.getString(NBT_HOST_TYPE);
            var resourceLocation = ResourceLocation.parse(resourceLocationString);
            var entityTypeHolderOptional = BuiltInRegistries.ENTITY_TYPE.getHolder(resourceLocation);

            entityTypeHolderOptional.ifPresent($ -> this.hostTypeOption = Option.some(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation)));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        hiveManager.save(compoundTag);
        compoundTag.putInt(NBT_JELLY_COUNT, getJellyCount());

        hostTypeOption.ifSome(hostType -> {
            var resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(hostTypeOption.unwrap());
            compoundTag.putString(NBT_HOST_TYPE, resourceLocation.toString());
        });
    }

    public GeneManager getGeneManager() {
        return ((GeneCarrier) this).getOrCreateGeneManager();
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

    public @Nullable Integer getMaxJellyToGrowth() {
        return null;
    }

    public MovementAnalyzer getMovementAnalyzer() {
        return movementAnalyzer;
    }

    public VibrationSystemManager getVibrationSystemManager() {
        return vibrationSystemManager;
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
