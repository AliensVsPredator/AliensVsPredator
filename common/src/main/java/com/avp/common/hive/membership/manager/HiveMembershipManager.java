package com.avp.common.hive.membership.manager;

import com.bvanseg.just.functional.option.Option;
import com.mojang.serialization.Dynamic;
import com.xlib.NBTSerializable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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
import com.avp.common.hive.membership.HiveMembershipCache;

public class HiveMembershipManager implements NBTSerializable {

    private static final String HIVE_MEMBER_DATA_KEY = "HiveMemberData";

    private static final int MEMBERSHIP_PURGE_FREQUENCY = 20 * 60;

    private final Hive hive;

    private final HiveMembershipCache hiveMembershipCache;

    public HiveMembershipManager(Hive hive) {
        this.hive = hive;
        this.hiveMembershipCache = new HiveMembershipCache();
    }

    public void tick() {
        if (hive.ageInTicks() % MEMBERSHIP_PURGE_FREQUENCY == 0) {
            purgeUnresponsiveHiveMembers();
        }
    }

    private void purgeUnresponsiveHiveMembers() {
        hiveMembershipCache.removeIf(hiveMemberEntry -> {
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
    }

    public void addMember(Entity entity) {
        var resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        var hiveMemberData = new HiveMemberData(resourceLocation, entity.blockPosition(), hive.ageInTicks());

        hiveMembershipCache.add(entity.getUUID(), hiveMemberData);
    }

    public boolean isMember(Entity entity) {
        return isMember(entity.getUUID());
    }

    public boolean isMember(UUID uuid) {
        return hiveMembershipCache.has(uuid);
    }

    public void removeMember(Entity entity) {
        hiveMembershipCache.remove(entity.getUUID());
    }

    public Set<UUID> getMemberUUIDs() {
        return hiveMembershipCache.streamEntries()
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    public int getMemberCount() {
        return getMemberUUIDs().size();
    }

    public List<Entity> getLoadedMembers() {
        var level = hive.level();

        return level.isClientSide
            ? List.of()
            : hiveMembershipCache.streamEntries()
                .map(Map.Entry::getKey)
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

        return hiveMembershipCache.get(uuid);
    }

    public Map<EntityType<?>, List<Map.Entry<UUID, HiveMemberData>>> getMembersByEntityType() {
        return hiveMembershipCache.getMembersByEntityTypeMap();
    }

    public Collection<Map.Entry<UUID, HiveMemberData>> getMembersMatching(Predicate<EntityType<?>> predicate) {
        return hiveMembershipCache.getMembersByEntityTypeMap()
            .keySet()
            .stream()
            .filter(predicate)
            .map(hiveMembershipCache::getMembersByEntityType)
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
                .ifPresent(hiveMemberData -> hiveMembershipCache.add(entityUUID, hiveMemberData));
        }
    }

    @Override
    public void save(CompoundTag compoundTag) {
        var hiveMemberDataTag = new CompoundTag();

        hiveMembershipCache.streamEntries()
            .forEach(
                entry -> HiveMemberData.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue())
                    .resultOrPartial(
                        AVP.LOGGER::error
                    )
                    .ifPresent(tag -> hiveMemberDataTag.put(entry.getKey().toString(), tag))
            );

        compoundTag.put(HIVE_MEMBER_DATA_KEY, hiveMemberDataTag);
    }
}
