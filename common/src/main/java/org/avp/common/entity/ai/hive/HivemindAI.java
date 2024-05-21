package org.avp.common.entity.ai.hive;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.avp.common.tag.AVPEntityTags;
import org.avp.server.HivemindManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HivemindAI {

    private final UUID uuid;

    private final Map<UUID, LivingEntity> hiveMembersByUUID;

    public HivemindAI(LivingEntity leader) {
        this.uuid = leader.getUUID();
        this.hiveMembersByUUID = new HashMap<>();
        hiveMembersByUUID.put(uuid, leader);
    }

    public void tick(Level level) {
        var hasLeader = hasLeader();

        if (!hasLeader) {
            var nextLeaderInLineOptional = findBestCandidateForLeader();
            nextLeaderInLineOptional.ifPresent(leader -> HivemindManager.migrate(this, leader));
        }
    }

    public Optional<LivingEntity> getLeader() {
        return Optional.ofNullable(hiveMembersByUUID.get(uuid));
    }

    public boolean hasLeader() {
        return getLeader().isPresent();
    }

    public Optional<LivingEntity> findBestCandidateForLeader() {
        if (hasLeader()) {
            return getLeader();
        }

        return hiveMembersByUUID.entrySet()
            .stream()
            .filter(entry -> entry.getValue().getType().is(AVPEntityTags.ROYAL_ALIENS))
            .findFirst()
            .map(Map.Entry::getValue);
    }

    public void acceptNewHiveMember(LivingEntity hiveMember) {
        if (!hiveMember.getType().is(AVPEntityTags.ALIENS)) {
            throw new IllegalArgumentException("Cannot accept entity that is not an alien entity type! Entity Type: " + hiveMember.getType());
        }

        hiveMembersByUUID.put(hiveMember.getUUID(), hiveMember);
    }

    public void destroy() {
        hiveMembersByUUID.clear();
    }

    public UUID getUUID() {
        return uuid;
    }

    public Collection<LivingEntity> getHiveMembers() {
        return hiveMembersByUUID.values();
    }
}
