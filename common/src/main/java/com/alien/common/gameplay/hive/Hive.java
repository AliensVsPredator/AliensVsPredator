package com.alien.common.gameplay.hive;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.xenomorph.crusher.Crusher;
import com.alien.common.gameplay.entity.living.alien.xenomorph.drone.Drone;
import com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian.Praetorian;
import com.alien.common.gameplay.entity.living.alien.xenomorph.prowler.Prowler;
import com.alien.common.gameplay.entity.living.alien.xenomorph.runner.Runner;
import com.alien.common.gameplay.entity.living.alien.xenomorph.warrior.Warrior;
import com.alien.common.gameplay.hive.ai.task.Task;
import com.alien.common.gameplay.hive.ai.task.impl.BalanceAveragingHiveTask;
import com.alien.common.gameplay.hive.ai.task.impl.BalanceQueenHiveTask;
import com.alien.common.gameplay.hive.ai.task.impl.BalanceStepHiveTask;
import com.alien.common.gameplay.hive.ai.task.impl.MergeWithNearbyHiveTask;
import com.alien.common.gameplay.hive.ai.task.impl.PickBestLeaderTask;
import com.alien.common.gameplay.hive.membership.HiveLeadershipManager;
import com.alien.common.gameplay.hive.membership.HiveMembershipManager;
import com.alien.common.gameplay.hive.membership.HiveReserveManager;
import com.alien.common.gameplay.level.saveddata.QueenSpawnChunkData;
import com.alien.common.model.alien.variant.AlienVariant;
import com.lib.common.gameplay.NBTSerializable;
import com.lib.common.gameplay.util.spatial.chunk.ChunkPosUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.avp.AVP;
import com.avp.common.registry.tag.AVPEntityTypeTags;

public class Hive implements NBTSerializable {

    private static final AlienVariant DEFAULT_VARIANT = AlienVariant.NORMAL;

    private static final String AGE_IN_TICKS_KEY = "AgeInTicks";

    private static final String CENTER_POS_KEY = "CenterPos";

    private static final String VARIANT_ID_KEY = "VariantId";

    private final HiveBossBarManager bossBarManager;

    private final HiveDebugManager debugManager;

    private final HiveLeadershipManager leadershipManager;

    private final HiveMembershipManager membershipManager;

    private final HiveReserveManager reserveManager;

    private final HiveSpaceManager spaceManager;

    private final UUID id;

    private final Level level;

    private final List<Task> tasks;

    private @Nullable HiveRemovalReason removalReason;

    private BlockPos centerPos;

    private int ageInTicks;

    private AlienVariant variant;

    public Hive(Level level, UUID id) {
        this.variant = DEFAULT_VARIANT;
        this.tasks = new ArrayList<>();
        this.id = id;
        this.removalReason = null;
        this.level = level;
        this.bossBarManager = new HiveBossBarManager(this);
        this.debugManager = new HiveDebugManager(this);
        this.leadershipManager = new HiveLeadershipManager(this);
        this.membershipManager = new HiveMembershipManager(this);
        this.reserveManager = new HiveReserveManager(this);
        this.spaceManager = new HiveSpaceManager(this);
        this.centerPos = BlockPos.ZERO;

        // Order matters for hive tasks.

        tasks.add(
            new BalanceAveragingHiveTask(
                this,
                () -> Runner.getType(getVariant()),
                () -> Prowler.getType(getVariant())
            )
        );
        tasks.add(
            new BalanceStepHiveTask(
                this,
                () -> Prowler.getType(getVariant()),
                () -> Crusher.getType(getVariant())
            )
        );

        tasks.add(
            new BalanceAveragingHiveTask(
                this,
                () -> Drone.getType(getVariant()),
                () -> Warrior.getType(getVariant())
            )
        );
        tasks.add(
            new BalanceStepHiveTask(
                this,
                () -> Warrior.getType(getVariant()),
                () -> Praetorian.getType(getVariant())
            )
        );

        tasks.add(new BalanceQueenHiveTask(this));

        tasks.add(new PickBestLeaderTask(this));
        tasks.add(new MergeWithNearbyHiveTask(this));
    }

    public void tick() {
        if (!isChunkLoaded()) {
            // Don't bother updating the hive if it's not in a loaded chunk.
            return;
        }

        bossBarManager.tick();
        debugManager.tick();
        leadershipManager.tick();
        membershipManager.tick();
        reserveManager.tick();

        tasks.stream()
            .filter(Task::canRun)
            .forEach(Task::run);

        if (ageInTicks % 20 == 0 && !hasXenomorphs()) {
            remove(HiveRemovalReason.KILLED);
        }

        ageInTicks++;
    }

    public void moveCenter(BlockPos newCenterPos) {
        this.centerPos = newCenterPos;
    }

    public boolean requestToJoin(Entity requestingEntity) {
        if (
            // If the requesting entity is not an alien...
            !(requestingEntity instanceof Alien alien)
                // OR the alien is not the same variant as the hive...
                || !Objects.equals(alien.getVariant(), variant)
        ) {
            // Then reject the entity's request to join the hive.
            return false;
        }

        if (!spaceManager.isEntityLeashedToHive(requestingEntity)) {
            // If the entity isn't within range of the hive, it shouldn't be able to join the hive.
            return false;
        }

        membershipManager.addMember(requestingEntity);

        return true;
    }

    public void ping(@NotNull Entity entity) {
        if (
            !entity.isAlive()
                || (membershipManager.isMember(entity)
                    && !spaceManager.isEntityLeashedToHive(entity))
        ) {
            removeHiveMember(entity);
            return;
        }

        membershipManager.addMember(entity);
    }

    public void removeHiveMember(@NotNull Entity entity) {
        leadershipManager.removeLeadership(entity);
        membershipManager.removeMember(entity);
    }

    public boolean isChunkLoaded() {
        return level.getChunkSource().getChunkNow(centerPos.getX() >> 4, centerPos.getZ() >> 4) != null;
    }

    public boolean isAlive() {
        return !isRemoved();
    }

    public boolean hasXenomorphs() {
        // Ovomorphs, facehuggers and chestbursters do not sustain a hive. That's why we check the xenomorph count
        // here instead of the overall hive member map size.
        return !membershipManager.getMembersMatching(entityType -> entityType.is(AVPEntityTypeTags.XENOMORPHS))
            .isEmpty();
    }

    public @Nullable HiveRemovalReason getRemovalReason() {
        return removalReason;
    }

    public boolean isRemoved() {
        return removalReason != null;
    }

    public void remove(HiveRemovalReason removalReason) {
        this.removalReason = removalReason;
    }

    public void onRemove() {
        bossBarManager.onHiveRemoved();
        debugManager.onHiveRemoved();

        // Difficulty check because we don't want to blacklist chunks if the hive members simply de-spawned.
        if (level.getDifficulty() != Difficulty.PEACEFUL) {
            // Once the hive is defeated, blacklist chunks around the hive center so no more queens can spawn.
            QueenSpawnChunkData.getOrCreate(level)
                .ifSome(queenSpawnChunkData -> {
                    // TODO: Use a precise circular area of chunks based on the hive's radius/size.
                    var chunkRadiusToBlacklist = AVP.config.hiveConfigs.MINIMUM_DISTANCE_BETWEEN_NATURAL_QUEEN_SPAWNS_IN_CHUNKS;
                    var nearbyChunkPositions = ChunkPosUtil.getChunksAround(centerPosition(), chunkRadiusToBlacklist);

                    nearbyChunkPositions.forEach(queenSpawnChunkData::addChunkToBlacklist);
                });
        }
    }

    public boolean isAngry() {
        return bossBarManager.isTrackingPlayers();
    }

    @Override
    public void load(CompoundTag compoundTag) {
        leadershipManager.load(compoundTag);
        membershipManager.load(compoundTag);
        reserveManager.load(compoundTag);

        var centerPosComponents = compoundTag.getIntArray(CENTER_POS_KEY);
        this.ageInTicks = compoundTag.getInt(AGE_IN_TICKS_KEY);
        this.centerPos = new BlockPos(centerPosComponents[0], centerPosComponents[1], centerPosComponents[2]);

        if (compoundTag.contains(VARIANT_ID_KEY)) {
            this.variant = AlienVariant.getById(compoundTag.getByte(VARIANT_ID_KEY)).unwrapOr(DEFAULT_VARIANT);
        }
    }

    @Override
    public void save(CompoundTag compoundTag) {
        leadershipManager.save(compoundTag);
        membershipManager.save(compoundTag);
        reserveManager.save(compoundTag);

        compoundTag.putInt(AGE_IN_TICKS_KEY, ageInTicks);

        var centerPosComponents = new int[] { centerPos.getX(), centerPos.getY(), centerPos.getZ() };
        compoundTag.putIntArray(CENTER_POS_KEY, centerPosComponents);

        compoundTag.putByte(VARIANT_ID_KEY, (byte) variant.getId());
    }

    public int ageInTicks() {
        return ageInTicks;
    }

    public BlockPos centerPosition() {
        return centerPos;
    }

    public HiveBossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public HiveDebugManager getDebugManager() {
        return debugManager;
    }

    public UUID id() {
        return id;
    }

    public HiveLeadershipManager getLeadershipManager() {
        return leadershipManager;
    }

    public HiveMembershipManager getMembershipManager() {
        return membershipManager;
    }

    public HiveReserveManager getReserveManager() {
        return reserveManager;
    }

    public HiveSpaceManager getSpaceManager() {
        return spaceManager;
    }

    public AlienVariant getVariant() {
        return variant;
    }

    public void setVariant(AlienVariant variant) {
        this.variant = variant;
    }

    public Level level() {
        return level;
    }
}
