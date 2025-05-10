package com.avp.common.hive.manager;

import com.bvanseg.just.functional.option.Option;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.avp.AVP;
import com.avp.common.hive.Hive;
import com.avp.common.hive.HiveMemberData;
import com.avp.common.util.NBTSerializable;

public class HiveMembershipManager implements NBTSerializable {

    private static final String HIVE_MEMBER_DATA_KEY = "HiveMemberData";

    private static final int MEMBERSHIP_PURGE_FREQUENCY = 20 * 60;

    private final Hive hive;

    private final Map<UUID, HiveMemberData> hiveMemberDataByEntityUUIDMap;

    private final Map<EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> hiveMemberDataByEntityTypeMap;

    public HiveMembershipManager(Hive hive) {
        this.hive = hive;
        this.hiveMemberDataByEntityUUIDMap = new HashMap<>();
        this.hiveMemberDataByEntityTypeMap = new HashMap<>();
    }

    public void tick() {
        if (hive.ageInTicks() % MEMBERSHIP_PURGE_FREQUENCY == 0) {
            purgeUnresponsiveHiveMembers();
        }

        // TODO: Shouldn't have to compute this every tick, should only compute on load and then incrementally update.
        computeHiveMembersByType();
    }

    private void computeHiveMembersByType() {
        hiveMemberDataByEntityTypeMap.clear();

        var membersByType = hive.getMembershipManager()
            .getMemberUUIDs()
            .stream()
            .map(uuid -> hive.getMembershipManager().getMemberData(uuid).map(data -> Map.entry(uuid, data)))
            .flatMap(Option::toStream)
            .collect(Collectors.groupingBy(entry -> BuiltInRegistries.ENTITY_TYPE.get(entry.getValue().entityType())));

        hiveMemberDataByEntityTypeMap.putAll(membersByType);
    }

    private void purgeUnresponsiveHiveMembers() {
        hiveMemberDataByEntityUUIDMap.entrySet().removeIf(hiveMemberEntry -> {
            var hiveMemberData = hiveMemberEntry.getValue();
            var lastSeenTimestampInTicks = hiveMemberData.lastSeenTimestampInTicks();
            var lastSeenPos = hiveMemberData.lastSeenPos();
            var isLastSeenPosLoaded = hive.level().isLoaded(lastSeenPos);

            if (!isLastSeenPosLoaded) {
                // TODO: We should probably not remove entities if their last seen position is unloaded.
                return true;
            }

            // Remove the hive member if their last seen pos is loaded and if they haven't been seen in 3 minutes.
            return hive.ageInTicks() - lastSeenTimestampInTicks > 20 * 60 * 3;
        });

        if (!isMember(hive.hiveLeaderId())) {
            // If the hive leader id is no longer present in the hive member data map, clear the leader.
            hive.setHiveLeaderId(null);
        }
    }

    public void addMember(Entity entity) {
        var resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        var hiveMemberData = new HiveMemberData(resourceLocation, entity.blockPosition(), hive.ageInTicks());

        hiveMemberDataByEntityUUIDMap.put(entity.getUUID(), hiveMemberData);
    }

    public boolean isMember(Entity entity) {
        return isMember(entity.getUUID());
    }

    public boolean isMember(UUID uuid) {
        return hiveMemberDataByEntityUUIDMap.containsKey(uuid);
    }

    public void removeMember(Entity entity) {
        hiveMemberDataByEntityUUIDMap.remove(entity.getUUID());
    }

    public Set<UUID> getMemberUUIDs() {
        return hiveMemberDataByEntityUUIDMap.keySet();
    }

    public int getMemberCount() {
        return getMemberUUIDs().size();
    }

    public List<Entity> getLoadedMembers() {
        var level = hive.level();

        return level.isClientSide
            ? List.of()
            : hiveMemberDataByEntityUUIDMap.keySet()
                .stream()
                .map(((ServerLevel) level)::getEntity)
                .filter(Objects::nonNull)
                .toList();
    }

    public @NotNull Option<HiveMemberData> getMemberData(Entity entity) {
        if (entity == null) {
            return Option.none();
        }

        return getMemberData(entity.getUUID());
    }

    public @NotNull Option<HiveMemberData> getMemberData(UUID uuid) {
        if (uuid == null) {
            return Option.none();
        }

        return Option.ofNullable(hiveMemberDataByEntityUUIDMap.get(uuid));
    }

    public Map<EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> getMembersByEntityType() {
        return Collections.unmodifiableMap(hiveMemberDataByEntityTypeMap);
    }

    public Collection<Map.Entry<UUID, HiveMemberData>> getMembersMatching(Predicate<EntityType<?>> predicate) {
        return hiveMemberDataByEntityTypeMap.keySet()
            .stream()
            .filter(predicate)
            .map(hiveMemberDataByEntityTypeMap::get)
            .flatMap(Collection::stream)
            .toList();
    }

    @Override
    public void load(CompoundTag compoundTag) {
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
                .ifPresent(hiveMemberData -> hiveMemberDataByEntityUUIDMap.put(entityUUID, hiveMemberData));
        }

        // TODO: Change this eventually to use a cache populate method or some other mechanism.
        computeHiveMembersByType();
    }

    @Override
    public void save(CompoundTag compoundTag) {
        var hiveMemberDataTag = new CompoundTag();

        for (var entry : hiveMemberDataByEntityUUIDMap.entrySet()) {
            HiveMemberData.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue())
                .resultOrPartial(
                    AVP.LOGGER::error
                )
                .ifPresent(tag -> hiveMemberDataTag.put(entry.getKey().toString(), tag));
        }

        compoundTag.put(HIVE_MEMBER_DATA_KEY, hiveMemberDataTag);
    }
}
