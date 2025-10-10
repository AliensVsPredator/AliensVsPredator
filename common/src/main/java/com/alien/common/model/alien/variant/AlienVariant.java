package com.alien.common.model.alien.variant;

import com.just.core.functional.option.Option;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum AlienVariant {

    NORMAL(0),
    NETHER(1),
    ABERRANT(2),
    IRRADIATED(3);

    public static final AlienVariant[] VALUES = values();

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
