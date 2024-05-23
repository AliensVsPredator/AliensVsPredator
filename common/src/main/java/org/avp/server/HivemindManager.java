package org.avp.server;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.avp.common.entity.ai.hive.Hivemind;

public class HivemindManager {

    private static final Map<UUID, Hivemind> HIVEMIND_BY_UUID_MAP = new HashMap<>();

    public static void tick(Level level) {
        HIVEMIND_BY_UUID_MAP.forEach((key, value) -> value.tick(level));
    }

    public static Hivemind createWithLeader(LivingEntity leader) {
        return HIVEMIND_BY_UUID_MAP.put(leader.getUUID(), new Hivemind(leader));
    }

    public static Hivemind migrate(Hivemind hivemind, LivingEntity newLeader) {
        var newHivemindAI = createWithLeader(newLeader);
        hivemind.getHiveMembers().forEach(newHivemindAI::acceptNewHiveMember);
        remove(hivemind.getUUID());
        return newHivemindAI;
    }

    public static Optional<Hivemind> getByUUID(UUID hivemindUUID) {
        return Optional.ofNullable(HIVEMIND_BY_UUID_MAP.get(hivemindUUID));
    }

    public static void remove(UUID hivemindUUID) {
        var removedHivemindAI = HIVEMIND_BY_UUID_MAP.remove(hivemindUUID);
        // Always destroy a hivemind before it is removed.
        removedHivemindAI.destroy();
    }

    private HivemindManager() {
        throw new UnsupportedOperationException();
    }
}
