package com.avp.common.entity.ai.util;

public sealed interface CombatResponse {

    enum FightType {
        MELEE,
        RANGED
    }

    static Flight flight() {
        return Flight.INSTANCE;
    }

    record Fight(FightType fightType) implements CombatResponse {}

    final class Flight implements CombatResponse {

        private static final Flight INSTANCE = new Flight();
    }
}
