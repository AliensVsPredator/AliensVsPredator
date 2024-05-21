package org.avp.server;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.avp.common.entity.living.Queen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class QueenManager {

    private static final Map<UUID, Queen> QUEEN_BY_UUID_MAP = new HashMap<>();

    public static void tick(Level level) {

    }

    public static Optional<Queen> get(UUID queenUUID) {
        return Optional.ofNullable(QUEEN_BY_UUID_MAP.get(queenUUID));
    }

    public static void remove(UUID queenUUID) {
        QUEEN_BY_UUID_MAP.remove(queenUUID);
    }

    public static void submit(Queen queen) {
        QUEEN_BY_UUID_MAP.put(queen.getUUID(), queen);
    }

    public static Optional<Queen> findClosestTo(Entity entity) {
        return QUEEN_BY_UUID_MAP.entrySet()
            .stream()
            .min((left, right) -> {
                var aDistance = left.getValue().distanceTo(entity);
                var bDistance = right.getValue().distanceToSqr(entity);
                return Double.compare(aDistance, bDistance);
            })
            .map(Map.Entry::getValue);
    }

    private QueenManager() {
        throw new UnsupportedOperationException();
    }
}
