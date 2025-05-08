package com.avp.common.entity.living.alien.ovomorph;

import com.bvanseg.just.functional.option.Option;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HatchState {

    SLEEPING(0),
    HATCHING(1),
    HATCHED(2);

    public static final Map<Integer, HatchState> ID_TO_HATCH_STATE_MAP = Arrays.stream(values())
        .collect(Collectors.toMap(HatchState::getId, Function.identity()));

    private final int id;

    HatchState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Option<HatchState> fromId(int id) {
        return Option.ofNullable(ID_TO_HATCH_STATE_MAP.get(id));
    }
}
