package com.avp.common.registry.init;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.bvanseg.just.serialization.codec.stream.impl.StreamCodecs;
import com.lib.common.network.DataKey;
import com.lib.common.registry.DataKeyRegistry;
import com.mojang.serialization.Codec;

import java.util.function.Function;

import com.avp.AVPResources;

public class AVPDataKeys {

    public static final DataKey<Integer> ACID_MULTIPLIER = register(
        "acid_multiplier",
        builder -> builder.networkSynchronized(StreamCodecs.INT)
            .persistent("Multiplier", Codec.INT)
            .build(1)
    );

    public static final DataKey<Integer> ACID_TICK_COUNT_FOR_MULTIPLIER = register(
        "acid_tick_count_for_multiplier",
        builder -> builder.persistent("TickCountForMultiplier", Codec.INT)
            .build(0)
    );

    public static final DataKey<Boolean> ADOLESCENT_HAS_DORSAL_TUBES = register(
        "adolescent_has_dorsal_tubes",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .build(true)
    );

    public static final DataKey<Boolean> ALIEN_IS_POISONED = register(
        "alien_is_poisoned",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .persistent("isPoisoned", Codec.BOOL)
            .build(false)
    );

    public static final DataKey<Boolean> ENTITY_HAS_WARP_EFFECT = register(
        "entity_has_warp_effect",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .build(false)
    );

    public static final DataKey<Boolean> ENTITY_HAS_TARGET = register(
        "entity_has_target",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .build(false)
    );

    public static final DataKey<Boolean> ENTITY_IS_MOVING_HORIZONTALLY = register(
        "entity_is_moving_horizontally",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .build(false)
    );

    public static final DataKey<Integer> MARINE_SKIN_COLOR = register(
        "marine_skin_color",
        builder -> builder.networkSynchronized(StreamCodecs.INT)
            .persistent("skinColor", Codec.INT)
            .build(0xEED0B6)
    );

    public static final DataKey<Integer> MARINE_BEARD_VARIANT = register(
        "marine_beard_variant",
        builder -> builder.networkSynchronized(StreamCodecs.INT)
            .persistent("beardVariant", Codec.INT)
            .build(0)
    );

    public static final DataKey<Integer> MARINE_EYE_COLOR = register(
        "marine_eye_color",
        builder -> builder.networkSynchronized(StreamCodecs.INT)
            .persistent("eyeColor", Codec.INT)
            .build(0xA1CAF1)
    );

    public static final DataKey<Integer> MARINE_HAIR_COLOR = register(
        "marine_hair_color",
        builder -> builder.networkSynchronized(StreamCodecs.INT)
            .persistent("hairColor", Codec.INT)
            .build(0x86462C)
    );

    public static final DataKey<Integer> MARINE_HAIR_VARIANT = register(
        "marine_hair_variant",
        builder -> builder.networkSynchronized(StreamCodecs.INT)
            .persistent("hairVariant", Codec.INT)
            .build(0)
    );

    public static final DataKey<Boolean> MARINE_IS_MALE = register(
        "marine_is_male",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .persistent("isMale", Codec.BOOL)
            .build(true)
    );

    public static final DataKey<Integer> OVOMORPH_DESIRE_TO_HATCH = register(
        "ovomorph_desire_to_hatch",
        builder -> builder.persistent("desireToHatch", Codec.INT)
            .build(0)
    );

    public static final DataKey<Integer> OVOMORPH_HATCH_DURATION_IN_TICKS = register(
        "ovomorph_hatch_duration_in_ticks",
        builder -> builder.persistent("hatchDurationInTicks", Codec.INT)
            .build(-1)
    );

    public static final DataKey<Byte> OVOMORPH_HATCH_STATE = register(
        "ovomorph_hatch_state",
        builder -> builder.networkSynchronized(StreamCodecs.BYTE)
            .persistent("hatchState", Codec.BYTE)
            .build((byte) Ovomorph.DEFAULT_HATCH_STATE.getId())
    );

    public static final DataKey<Boolean> OVOMORPH_IS_ROOTED = register(
        "ovomorph_is_rooted",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .persistent("isRooted", Codec.BOOL)
            .build(true)
    );

    public static final DataKey<Byte> OVOMORPH_MAXIMUM_SPAWN_COUNT = register(
        "ovomorph_maximum_spawn_count",
        builder -> builder.networkSynchronized(StreamCodecs.BYTE)
            .persistent("maximumSpawnCount", Codec.BYTE)
            .build((byte) 1)
    );

    public static final DataKey<Integer> OVOMORPH_REMAINING_SPAWN_DELAY_IN_TICKS = register(
        "ovomorph_remaining_spawn_delay_in_ticks",
        builder -> builder.persistent("remainingSpawnDelayInTicks", Codec.INT)
            .build(-1)
    );

    public static final DataKey<Integer> OVOMORPH_SPAWN_COUNT = register(
        "ovomorph_spawn_count",
        builder -> builder.persistent("spawnCount", Codec.INT)
            .build(0)
    );

    public static final DataKey<Boolean> PARASITE_IS_FERTILE = register(
        "parasite_is_fertile",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .persistent("isFertile", Codec.BOOL)
            .build(true)
    );

    public static final DataKey<Integer> PARASITE_TICKS_ATTACHED_TO_HOST = register(
        "parasite_ticks_attached_to_host",
        builder -> builder.persistent("ticksAttachedToHost", Codec.INT)
            .build(0)
    );

    public static final DataKey<Boolean> XENOMORPH_IS_CRAWLING = register(
        "xenomorph_is_crawling",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .build(false)
    );

    private static <T> DataKey<T> register(String id, Function<DataKey.Builder<T>, DataKey<T>> factory) {
        var resourceLocation = AVPResources.location(id);
        var dataAccessor = factory.apply(new DataKey.Builder<>(resourceLocation));
        return DataKeyRegistry.register(resourceLocation, dataAccessor);
    }

    public static void initialize() {}
}
