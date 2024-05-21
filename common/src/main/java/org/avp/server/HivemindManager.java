package org.avp.server;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.avp.common.entity.ai.hive.HivemindAI;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HivemindManager {

    private static final Map<UUID, HivemindAI> HIVEMIND_BY_UUID_MAP = new HashMap<>();

    public static void tick(Level level) {
        HIVEMIND_BY_UUID_MAP.forEach((key, value) -> value.tick(level));
    }

    public static HivemindAI createWithLeader(LivingEntity leader) {
        return HIVEMIND_BY_UUID_MAP.put(leader.getUUID(), new HivemindAI(leader));
    }

    public static HivemindAI migrate(HivemindAI hivemindAI, LivingEntity newLeader) {
        var newHivemindAI = createWithLeader(newLeader);
        hivemindAI.getHiveMembers().forEach(newHivemindAI::acceptNewHiveMember);
        remove(hivemindAI.getUUID());
        return newHivemindAI;
    }

    public static Optional<HivemindAI> getByUUID(UUID hivemindUUID) {
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
