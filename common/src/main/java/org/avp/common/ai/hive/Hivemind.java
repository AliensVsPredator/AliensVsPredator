package org.avp.common.ai.hive;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.avp.common.game.entity.type.HiveMember;
import org.avp.common.game.entity.type.RoyalJellyHolder;
import org.avp.common.data.tag.AVPEntityTypeTags;
import org.avp.server.HivemindManager;

public class Hivemind {

    private final UUID uuid;

    private final Map<UUID, LivingEntity> hiveMembersByUUID;

    public Hivemind(LivingEntity leader) {
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
            .filter(entry -> {
                var entity = entry.getValue();
                var royalJellyHolder = (RoyalJellyHolder) entity;
                return royalJellyHolder.hasRoyalJelly() || entity.getType().is(AVPEntityTypeTags.ROYAL_ALIENS);
            })
            .findFirst()
            .map(Map.Entry::getValue);
    }

    public void acceptNewHiveMember(LivingEntity hiveMember) {
        if (!hiveMember.getType().is(AVPEntityTypeTags.ALIENS)) {
            throw new IllegalArgumentException(
                "Cannot accept entity that is not an alien entity type! Entity Type: " + hiveMember.getType()
            );
        }

        if (hiveMember instanceof HiveMember member) {
            member.setHivemindSignature(uuid);
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
