package com.avp.common.entity.living.alien;

import com.bvanseg.just.functional.option.Option;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum AlienVariant {

    NORMAL(0),
    NETHER(1),
    ABERRANT(2),
    IRRADIATED(3);

    private static final Map<Integer, AlienVariant> BY_ID = Arrays.stream(values())
        .collect(Collectors.toMap(AlienVariant::getId, Function.identity()));

    public static Option<AlienVariant> getById(int id) {
        return Option.ofNullable(BY_ID.get(id));
    }

    private final int id;

    AlienVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
