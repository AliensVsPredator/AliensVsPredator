package com.avp.common.hive;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.avp.AVP;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.hive.ai.task.Task;
import com.avp.common.hive.ai.task.impl.BalanceHiveTask;
import com.avp.common.hive.ai.task.impl.DebugHiveTask;
import com.avp.common.hive.ai.task.impl.PickBestLeaderTask;
import com.avp.common.hive.ai.task.impl.UpdateHiveBossBarTask;
import com.avp.common.hive.manager.HiveMembershipManager;
import com.avp.common.level.saveddata.HiveLevelData;
import com.avp.common.util.CompoundTagUtil;

public class Hive {

    private static final String AGE_IN_TICKS_KEY = "AgeInTicks";

    private static final String CENTER_POS_KEY = "CenterPos";

    private static final String HIVE_LEADER_ID_KEY = "HiveLeaderId";

    private final HiveMembershipManager membershipManager;

    private final ServerBossEvent bossEvent;

    private final List<Task> tasks;

    private final UUID id;

    private final Level level;

    private BlockPos centerPos;

    private int ageInTicks;

    private Option<UUID> hiveLeaderIdOption;

    public Hive(Level level, UUID id) {
        this.tasks = new ArrayList<>();
        this.id = id;
        this.level = level;
        this.membershipManager = new HiveMembershipManager(this);
        this.hiveLeaderIdOption = Option.none();
        this.centerPos = BlockPos.ZERO;
        this.bossEvent = (ServerBossEvent) new ServerBossEvent(
            Component.translatable("bossbar.avp.hive.title"),
            BossEvent.BossBarColor.GREEN,
            BossEvent.BossBarOverlay.PROGRESS
        ).setDarkenScreen(AVP.config.hiveConfigs.HIVE_DARKEN_SCREEN);

        // Order matters here.
        tasks.add(new UpdateHiveBossBarTask(this));
        tasks.add(new DebugHiveTask(this));
        tasks.add(new BalanceHiveTask(this));
        tasks.add(new PickBestLeaderTask(this));
    }

    public void tick() {
        if (!isChunkLoaded()) {
            // Don't bother updating the hive if it's not in a loaded chunk.
            return;
        }

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
        if (!(requestingEntity instanceof Alien)) {
            // Hives only accept aliens, non-aliens get rejected.
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
        membershipManager.removeMember(entity);

        if (hiveLeaderIdOption.contains(entity.getUUID())) {
            // If the entity being removed is the hive leader, then set the hive leader ID to none.
            this.hiveLeaderIdOption = Option.none();
        }
    }

    public boolean isChunkLoaded() {
        return level.getChunkSource().getChunkNow(centerPos.getX() >> 4, centerPos.getZ() >> 4) != null;
    }

    public boolean isAlive() {
        // Ovomorphs, facehuggers and chestbursters do not sustain a hive. That's why we check the xenomorph count
        // here instead of the overall hive member map size.
        return membershipManager.getXenomorphCount() > 0
            && HiveLevelData.getOrCreate(level)
                .filter(data -> data.hasHive(this))
                .isSome();
    }

    public void onRemove() {
        bossEvent.removeAllPlayers();

        if (isDebugEnabled() && isDebugMarkHiveCenterEnabled() && level.getBlockState(centerPosition()).is(HiveConstants.DEBUG_BLOCK)) {
            level.setBlock(centerPosition(), Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public boolean isAngry() {
        return !bossEvent.getPlayers().isEmpty();
    }

    public boolean isHiveLeader(Entity entity) {
        return hiveLeaderIdOption.contains(entity.getUUID());
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

    public UUID id() {
        return id;
    }

    public BlockPos centerPosition() {
        return centerPos;
    }

    public @Nullable Alien hiveLeaderOrNull() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        return hiveLeaderIdOption.map(serverLevel::getEntity)
            .filter(entity -> entity instanceof Alien)
            .map(entity -> (Alien) entity)
            .unwrapOr(null);
    }

    public Option<Alien> hiveLeader() {
        return Option.ofNullable(hiveLeaderOrNull());
    }

    public void setHiveLeaderId(@Nullable UUID id) {
        this.hiveLeaderIdOption = Option.ofNullable(id);
    }

    public void load(CompoundTag compoundTag) {
        var centerPosComponents = compoundTag.getIntArray(CENTER_POS_KEY);
        this.ageInTicks = compoundTag.getInt(AGE_IN_TICKS_KEY);
        this.centerPos = new BlockPos(centerPosComponents[0], centerPosComponents[1], centerPosComponents[2]);
        this.hiveLeaderIdOption = Option.ofNullable(CompoundTagUtil.getUUIDOrNull(compoundTag, HIVE_LEADER_ID_KEY));

        membershipManager.load(compoundTag);
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putInt(AGE_IN_TICKS_KEY, ageInTicks);

        var centerPosComponents = new int[] { centerPos.getX(), centerPos.getY(), centerPos.getZ() };
        compoundTag.putIntArray(CENTER_POS_KEY, centerPosComponents);

        hiveLeaderIdOption.ifSome(hiveLeaderId -> compoundTag.putUUID(HIVE_LEADER_ID_KEY, hiveLeaderId));

        membershipManager.save(compoundTag);
    }

    public int ageInTicks() {
        return ageInTicks;
    }

    public ServerBossEvent bossEvent() {
        return bossEvent;
    }

    public @Nullable UUID hiveLeaderId() {
        return hiveLeaderIdOption.unwrapOr(null);
    }

    public HiveMembershipManager getMembershipManager() {
        return membershipManager;
    }

    public Level level() {
        return level;
    }

    public boolean isDebugEnabled() {
        return AVP.config.hiveConfigs.HIVE_DEBUG_ENABLED;
    }

    public boolean isDebugHiveMemberHighlightEnabled() {
        return AVP.config.hiveConfigs.HIVE_DEBUG_HIGHLIGHT_ALL_MEMBERS;
    }

    public boolean isDebugLeaderHighlightEnabled() {
        return AVP.config.hiveConfigs.HIVE_DEBUG_HIGHLIGHT_LEADER;
    }

    public boolean isDebugMarkHiveCenterEnabled() {
        return AVP.config.hiveConfigs.HIVE_DEBUG_MARK_HIVE_CENTER;
    }
}
