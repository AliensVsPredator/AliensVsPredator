package com.blib.common.gameplay.entity.ai.util;

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
