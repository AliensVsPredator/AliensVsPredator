package com.avp.common.entity.ai.util;

public sealed interface CombatResponse {

    enum FightType {
        MELEE,
        RANGED
    }

    record Fight(FightType fightType) implements CombatResponse {}

    record Flight() implements CombatResponse {}
}
