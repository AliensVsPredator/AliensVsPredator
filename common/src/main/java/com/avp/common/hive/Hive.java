package com.avp.common.hive;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.avp.AVP;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.alien.AlienVariant;
import com.avp.common.hive.ai.task.Task;
import com.avp.common.hive.ai.task.impl.BalanceDronesAndWarriorsHiveTask;
import com.avp.common.hive.ai.task.impl.BalancePraetoriansHiveTask;
import com.avp.common.hive.ai.task.impl.BalanceQueenHiveTask;
import com.avp.common.hive.ai.task.impl.PickBestLeaderTask;
import com.avp.common.hive.ai.task.impl.UpdateHiveBossBarTask;
import com.avp.common.hive.manager.HiveDebugManager;
import com.avp.common.hive.manager.HiveLeadershipManager;
import com.avp.common.hive.manager.HiveMembershipManager;
import com.avp.common.level.saveddata.HiveLevelData;

public class Hive {

    private static final AlienVariant DEFAULT_VARIANT = AlienVariant.NORMAL;

    private static final String AGE_IN_TICKS_KEY = "AgeInTicks";

    private static final String CENTER_POS_KEY = "CenterPos";

    private static final String VARIANT_ID_KEY = "VariantId";

    private final HiveDebugManager debugManager;

    private final HiveLeadershipManager leadershipManager;

    private final HiveMembershipManager membershipManager;

    private final ServerBossEvent bossEvent;

    private final UUID id;

    private final Level level;

    private final List<Task> tasks;

    private BlockPos centerPos;

    private int ageInTicks;

    private AlienVariant variant;

    public Hive(Level level, UUID id) {
        this.variant = DEFAULT_VARIANT;
        this.tasks = new ArrayList<>();
        this.id = id;
        this.level = level;
        this.debugManager = new HiveDebugManager(this);
        this.leadershipManager = new HiveLeadershipManager(this);
        this.membershipManager = new HiveMembershipManager(this);
        this.centerPos = BlockPos.ZERO;
        this.bossEvent = (ServerBossEvent) new ServerBossEvent(
            Component.translatable("bossbar.avp.hive.title"),
            BossEvent.BossBarColor.GREEN,
            BossEvent.BossBarOverlay.PROGRESS
        ).setDarkenScreen(AVP.config.hiveConfigs.HIVE_DARKEN_SCREEN);

        // Order matters here.
        tasks.add(new UpdateHiveBossBarTask(this));
        tasks.add(new BalanceDronesAndWarriorsHiveTask(this));
        tasks.add(new BalancePraetoriansHiveTask(this));
        tasks.add(new BalanceQueenHiveTask(this));
        tasks.add(new PickBestLeaderTask(this));
    }

    public void tick() {
        if (!isChunkLoaded()) {
            // Don't bother updating the hive if it's not in a loaded chunk.
            return;
        }

        debugManager.tick();
        leadershipManager.tick();
        membershipManager.tick();

        tasks.stream()
            .filter(Task::canRun)
            .forEach(Task::run);

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

        var leashDistance = AVP.config.hiveConfigs.HIVE_LEASH_RADIUS_IN_BLOCKS;

        if (!isEntityWithinRangeOfHive(requestingEntity, leashDistance)) {
            // If the entity isn't within range of the hive, it shouldn't be able to join the hive.
            return false;
        }

        membershipManager.addMember(requestingEntity);

        return true;
    }

    public void ping(@NotNull Entity entity) {
        if (!entity.isAlive()) {
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
        // Ovomorphs, facehuggers and chestbursters do not sustain a hive. That's why we check the xenomorph count
        // here instead of the overall hive member map size.
        return !membershipManager.getMembersMatching(entityType -> entityType.is(AVPEntityTypeTags.XENOMORPHS)).isEmpty()
            && HiveLevelData.getOrCreate(level)
                .filter(data -> data.hasHive(this))
                .isSome();
    }

    public void onRemove() {
        bossEvent.removeAllPlayers();
        debugManager.onHiveRemoved();
    }

    public boolean isAngry() {
        return !bossEvent.getPlayers().isEmpty();
    }

    public boolean isEntityWithinRangeOfHive(Entity entity) {
        return isBlockPosWithinRangeOfHive(entity.blockPosition());
    }

    public boolean isEntityWithinRangeOfHive(Entity entity, int rangeInBlocks) {
        return isBlockPosWithinRangeOfHive(entity.blockPosition(), rangeInBlocks);
    }

    public boolean isBlockPosWithinRangeOfHive(BlockPos blockPos) {
        return isBlockPosWithinRangeOfHive(blockPos, AVP.config.hiveConfigs.HIVE_RADIUS_IN_BLOCKS);
    }

    public boolean isBlockPosWithinRangeOfHive(BlockPos blockPos, int rangeInBlocks) {
        var hiveRadiusSquared = rangeInBlocks * rangeInBlocks;
        return distanceToCenterSqr(blockPos) <= hiveRadiusSquared;
    }

    public double distanceToCenterSqr(BlockPos blockPos) {
        var centerPos = centerPosition();
        return blockPos.distToCenterSqr(centerPos.getX(), centerPos.getY(), centerPos.getZ());
    }

    public void load(CompoundTag compoundTag) {
        leadershipManager.load(compoundTag);
        membershipManager.load(compoundTag);

        var centerPosComponents = compoundTag.getIntArray(CENTER_POS_KEY);
        this.ageInTicks = compoundTag.getInt(AGE_IN_TICKS_KEY);
        this.centerPos = new BlockPos(centerPosComponents[0], centerPosComponents[1], centerPosComponents[2]);

        if (compoundTag.contains(VARIANT_ID_KEY)) {
            this.variant = AlienVariant.getById(compoundTag.getByte(VARIANT_ID_KEY)).unwrapOr(DEFAULT_VARIANT);
        }
    }

    public void save(CompoundTag compoundTag) {
        leadershipManager.save(compoundTag);
        membershipManager.save(compoundTag);

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

    public ServerBossEvent bossEvent() {
        return bossEvent;
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
