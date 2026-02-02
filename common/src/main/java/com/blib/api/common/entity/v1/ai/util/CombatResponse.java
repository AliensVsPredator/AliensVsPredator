package com.blib.api.common.entity.v1.ai.util;

/**
 * @deprecated Will be removed in a future version.
 */
@Deprecated(forRemoval = true)
public sealed interface CombatResponse {

    enum FightType {
        MELEE,
        RANGED
    }

    static Flight flight() {
        return Flight.INSTANCE;
    }

    static Rest rest() {
        return Rest.INSTANCE;
    }

    record Fight(FightType fightType) implements CombatResponse {}

    enum Flight implements CombatResponse {
        INSTANCE;
    }

    enum Rest implements CombatResponse {
        INSTANCE;
    }
}
