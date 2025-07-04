package com.alien.common.gameplay.entity.living.alien.xenomorph.queen;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.entity.living.alien.xenomorph.drone.Drone;
import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.model.resin.ResinData;
import com.alien.common.registry.init.AlienEntityTypes;
import com.lib.common.util.PlayerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import com.avp.AVP;
import com.avp.common.gameplay.ai.goal.DigToTargetGoal;
import com.avp.common.gameplay.ai.goal.QueenLayEggGoal;
import com.avp.common.registry.init.AVPSoundEvents;
import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.common.util.EntityUtil;
import com.avp.server.ServerLevelManagerAccessor;

public class Queen extends Xenomorph {

    public static AttributeSupplier.Builder createQueenAttributes() {
        return applyFrom(AVP.config.statsConfigs.QUEEN_STATS, Monster.createMonsterAttributes());
    }

    private final QueenAnimationDispatcher animationDispatcher;

    private final OvipositorManager ovipositorManager;

    public Queen(EntityType<? extends Queen> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new QueenAnimationDispatcher(this);
        this.ovipositorManager = new OvipositorManager(this);
        this.config = AVP.config.statsConfigs.QUEEN_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant) {
        return getType(alienVariant);
    }

    @Override
    protected @Nullable ResinData createResinData() {
        return new ResinData(0, 128, 1, AVP.config.statsConfigs.QUEEN_STATS.nestTickrate);
    }

    @Override
    public void tick() {
        super.tick();
        ovipositorManager.tick();
    }

    @Override
    protected boolean canEntityRideAlien(@NotNull Entity passenger) {
        return Objects.equals(passenger.getType(), AlienEntityTypes.OVIPOSITOR.get());
    }

    @Override
    protected void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        if (passenger.getType() == AlienEntityTypes.OVIPOSITOR.get()) {
            var relativePos = EntityUtil.getRelativePosition(this, 3, 0.01, 5.25);
            callback.accept(passenger, relativePos.x, relativePos.y, relativePos.z);
            return;
        }

        super.positionRider(passenger, callback);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
        @NotNull ServerLevelAccessor serverLevelAccessor,
        @NotNull DifficultyInstance difficulty,
        @NotNull MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnGroupData
    ) {
        if (spawnType == MobSpawnType.NATURAL) {
            applyNaturalSpawnEffects(serverLevelAccessor, spawnType);
        }

        return super.finalizeSpawn(serverLevelAccessor, difficulty, spawnType, spawnGroupData);
    }

    private void applyNaturalSpawnEffects(@NotNull ServerLevelAccessor serverLevelAccessor, @NotNull MobSpawnType spawnType) {
        alertPlayersOfSpawn();
        spawnGuards(serverLevelAccessor, spawnType);
        resetQueenSpawnCooldown(serverLevelAccessor);
    }

    private void alertPlayersOfSpawn() {
        for (var player : PlayerUtil.getTrackingPlayers(this)) {
            player.playNotifySound(AVPSoundEvents.ENTITY_QUEEN_SCREAM.get(), SoundSource.MASTER, 1, 1);
            player.sendSystemMessage(
                Component.literal("A scream from the depths sends chills down your spine...")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC)
            );
        }
    }

    private void spawnGuards(@NotNull ServerLevelAccessor serverLevelAccessor, @NotNull MobSpawnType spawnType) {
        var level = level();

        if (!level.isClientSide) {
            for (var i = 0; i < 4; i++) {
                var droneType = Drone.getType(getVariant());
                var drone = droneType.spawn((ServerLevel) level, blockPosition(), spawnType);

                if (drone != null) {
                    drone.setPersistenceRequired();
                    serverLevelAccessor.addFreshEntity(drone);
                }
            }
        }
    }

    private static void resetQueenSpawnCooldown(@NotNull ServerLevelAccessor serverLevelAccessor) {
        ((ServerLevelManagerAccessor) serverLevelAccessor.getLevel()).getServerLevelManager()
            .getQueenSpawnCooldown()
            .reset();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(5, new QueenLayEggGoal(this));
    }

    @Override
    protected void addDigToTargetGoal() {
        goalSelector.addGoal(5, new DigToTargetGoal(this, 32, 4, () -> !Objects.requireNonNull(ovipositorManager).hasOvipositor()));
    }

    @Override
    public float maxUpStep() {
        return 2.5F;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return AVPSoundEvents.ENTITY_QUEEN_IDLE.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return AVPSoundEvents.ENTITY_QUEEN_DEATH.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return AVPSoundEvents.ENTITY_QUEEN_HURT.get();
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.QUEEN_STATS.healthRegenPerSecond;
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

    @Override
    protected void doPush(@NotNull Entity entity) {
        if (
            // If queen does not have an ovipositor...
            !ovipositorManager.hasOvipositor()
                // OR the queen does have an ovipositor and the entity to push is NOT an alien...
                || !entity.getType().is(AVPEntityTypeTags.ALIENS)
        ) {
            // Then push the entity.
            super.doPush(entity);
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

    // Queens should never despawn no matter what.
    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
    }

    @Override
    public Integer getMaxJellyToGrowth() {
        return null;
    }

    public QueenAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }

    public OvipositorManager getOvipositorManager() {
        return ovipositorManager;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        ovipositorManager.load(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        ovipositorManager.save(compoundTag);
    }

    public static EntityType<? extends Alien> getType(AlienVariant alienVariant) {
        return switch (alienVariant) {
            case NORMAL -> AlienEntityTypes.QUEEN.get();
            case NETHER -> AlienEntityTypes.NETHER_QUEEN.get();
            case ABERRANT -> AlienEntityTypes.ABERRANT_QUEEN.get();
            case IRRADIATED -> AlienEntityTypes.IRRADIATED_QUEEN.get();
        };
    }
}
