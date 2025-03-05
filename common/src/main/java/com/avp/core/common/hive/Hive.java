package com.avp.core.common.hive;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.avp.core.AVP;
import com.avp.core.common.config.ConfigProperties;
import com.avp.core.common.entity.living.alien.Alien;
import com.avp.core.common.hive.ai.task.Task;
import com.avp.core.common.hive.ai.task.impl.BalanceHiveTask;
import com.avp.core.common.hive.ai.task.impl.DebugHiveTask;
import com.avp.core.common.hive.ai.task.impl.PickBestLeaderTask;
import com.avp.core.common.hive.ai.task.impl.UpdateHiveBossBarTask;
import com.avp.core.common.hive.ai.task.impl.UpdateHiveMembershipsTask;
import com.avp.core.common.level.saveddata.HiveLevelData;
import com.avp.core.common.util.CompoundTagUtil;

public class Hive {

    private static final String AGE_IN_TICKS_KEY = "AgeInTicks";

    private static final String CENTER_POS_KEY = "CenterPos";

    private static final String HIVE_LEADER_ID_KEY = "HiveLeaderId";

    private static final String HIVE_MEMBER_DATA_KEY = "HiveMemberData";

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
        Component.translatable("bossbar.avp.hive.title"),
        BossEvent.BossBarColor.GREEN,
        BossEvent.BossBarOverlay.PROGRESS
    ).setDarkenScreen(AVP.HIVES_CONFIG.properties().getOrThrow(ConfigProperties.HIVE_DARKEN_SCREEN));

    private final List<Task> tasks;

    private final Map<UUID, HiveMemberData> hiveMemberDataMap;

    private final UUID id;

    private final Level level;

    private BlockPos centerPos;

    private int ageInTicks;

    private @Nullable UUID hiveLeaderId;

    public Hive(Level level, UUID id) {
        this.tasks = new ArrayList<>();
        this.hiveMemberDataMap = new HashMap<>();
        this.id = id;
        this.level = level;
        centerPos = BlockPos.ZERO;

        // Order matters here.
        tasks.add(new UpdateHiveBossBarTask(this));
        tasks.add(new DebugHiveTask(this));
        tasks.add(new UpdateHiveMembershipsTask(this));
        tasks.add(new BalanceHiveTask(this));
        tasks.add(new PickBestLeaderTask(this));
    }

    public void tick() {
        if (!isChunkLoaded()) {
            // Don't bother updating the hive if it's not in a loaded chunk.
            return;
        }

        tasks.stream()
            .filter(Task::canRun)
            .forEach(Task::run);

        ageInTicks++;
    }

    public void moveCenter(BlockPos newCenterPos) {
        this.centerPos = newCenterPos;
    }

    public boolean requestToJoin(Entity requestingEntity) {
        var isAlien = requestingEntity instanceof Alien;

        if (isAlien) {
            var hivePos = centerPosition();
            int leashDistance = AVP.HIVES_CONFIG.properties().getOrThrow(ConfigProperties.HIVE_LEASH_RADIUS_IN_BLOCKS);
            var leashDistanceSquared = leashDistance * leashDistance;
            var distanceFromHiveSquared = requestingEntity.distanceToSqr(hivePos.getX(), hivePos.getY(), hivePos.getZ());

            if (distanceFromHiveSquared > leashDistanceSquared) {
                // if the alien is too far away, then they can't join.
                return false;
            }

            var resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(requestingEntity.getType());
            var hiveMemberData = new HiveMemberData(resourceLocation, requestingEntity.blockPosition(), ageInTicks);
            hiveMemberDataMap.put(requestingEntity.getUUID(), hiveMemberData);
        }

        // Hives only accept aliens.
        return isAlien;
    }

    public void ping(@NotNull Entity entity) {
        if (!entity.isAlive()) {
            removeHiveMember(entity);
            return;
        }

        var resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        var uuid = entity.getUUID();
        var hiveMemberData = new HiveMemberData(resourceLocation, entity.blockPosition(), ageInTicks);
        hiveMemberDataMap.put(uuid, hiveMemberData);
    }

    public void removeHiveMember(@NotNull Entity entity) {
        hiveMemberDataMap.remove(entity.getUUID());
    }

    public boolean hasHiveLeader() {
        return hiveLeaderId != null;
    }

    public boolean isChunkLoaded() {
        return level.getChunkSource().getChunkNow(centerPos.getX() >> 4, centerPos.getZ() >> 4) != null;
    }

    public boolean isAlive() {
        return !hiveMemberDataMap.isEmpty() && HiveLevelData.getOrCreate(level)
            .filter(data -> data.hasHive(this))
            .isPresent();
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
        return entity.getUUID().equals(hiveLeaderId);
    }

    public boolean isEntityWithinHive(Entity entity) {
        var centerPos = centerPosition();
        var properties = AVP.HIVES_CONFIG.properties();
        var hiveRadius = properties.getOrThrow(ConfigProperties.HIVE_RADIUS_IN_BLOCKS);
        var hiveRadiusSquared = hiveRadius * hiveRadius;
        var distanceSquared = entity.distanceToSqr(centerPos.getX(), centerPos.getY(), centerPos.getZ());

        return distanceSquared <= hiveRadiusSquared;
    }

    public UUID id() {
        return id;
    }

    public BlockPos centerPosition() {
        return centerPos;
    }

    public @Nullable Alien hiveLeaderOrNull() {
        if (level instanceof ServerLevel serverLevel) {
            var entity = serverLevel.getEntity(hiveLeaderId);

            if (entity instanceof Alien alien) {
                return alien;
            }
        }

        return null;
    }

    public Optional<Alien> hiveLeader() {
        return Optional.ofNullable(hiveLeaderOrNull());
    }

    public void setHiveLeaderId(@Nullable UUID id) {
        hiveLeaderId = id;
    }

    public void load(CompoundTag compoundTag) {
        var centerPosComponents = compoundTag.getIntArray(CENTER_POS_KEY);
        this.ageInTicks = compoundTag.getInt(AGE_IN_TICKS_KEY);
        this.centerPos = new BlockPos(centerPosComponents[0], centerPosComponents[1], centerPosComponents[2]);
        this.hiveLeaderId = CompoundTagUtil.getUUIDOrNull(compoundTag, HIVE_LEADER_ID_KEY);

        var hiveMemberDataMapTag = compoundTag.getCompound(HIVE_MEMBER_DATA_KEY);

        for (var key : hiveMemberDataMapTag.getAllKeys()) {
            var entityUUID = UUID.fromString(key);
            var hiveMemberDataTag = hiveMemberDataMapTag.getCompound(key);

            HiveMemberData.CODEC.parse(
                new Dynamic<>(NbtOps.INSTANCE, hiveMemberDataTag)
            )
                .resultOrPartial(
                    AVP.LOGGER::error
                )
                .ifPresent(hiveMemberData -> hiveMemberDataMap.put(entityUUID, hiveMemberData));
        }
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putInt(AGE_IN_TICKS_KEY, ageInTicks);

        var centerPosComponents = new int[] { centerPos.getX(), centerPos.getY(), centerPos.getZ() };
        compoundTag.putIntArray(CENTER_POS_KEY, centerPosComponents);

        if (hiveLeaderId != null) {
            compoundTag.putUUID(HIVE_LEADER_ID_KEY, hiveLeaderId);
        }

        var hiveMemberDataTag = new CompoundTag();

        for (var entry : hiveMemberDataMap.entrySet()) {
            HiveMemberData.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue())
                .resultOrPartial(
                    AVP.LOGGER::error
                )
                .ifPresent(tag -> hiveMemberDataTag.put(entry.getKey().toString(), tag));
        }

        compoundTag.put(HIVE_MEMBER_DATA_KEY, hiveMemberDataTag);
    }

    public int ageInTicks() {
        return ageInTicks;
    }

    public ServerBossEvent bossEvent() {
        return bossEvent;
    }

    public @Nullable UUID hiveLeaderId() {
        return hiveLeaderId;
    }

    public Map<UUID, HiveMemberData> hiveMemberDataMap() {
        return hiveMemberDataMap;
    }

    public Level level() {
        return level;
    }

    public boolean isDebugEnabled() {
        var properties = AVP.HIVES_CONFIG.properties();
        return properties.getOrThrow(ConfigProperties.HIVE_DEBUG_ENABLED);
    }

    public boolean isDebugHiveMemberHighlightEnabled() {
        var properties = AVP.HIVES_CONFIG.properties();
        return properties.getOrThrow(ConfigProperties.HIVE_DEBUG_HIGHLIGHT_ALL_MEMBERS);
    }

    public boolean isDebugLeaderHighlightEnabled() {
        var properties = AVP.HIVES_CONFIG.properties();
        return properties.getOrThrow(ConfigProperties.HIVE_DEBUG_HIGHLIGHT_LEADER);
    }

    public boolean isDebugMarkHiveCenterEnabled() {
        var properties = AVP.HIVES_CONFIG.properties();
        return properties.getOrThrow(ConfigProperties.HIVE_DEBUG_MARK_HIVE_CENTER);
    }
}
