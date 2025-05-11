package com.avp.common.entity.living.alien;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.avp.common.block.AVPBlockTags;
import com.avp.common.block.AVPBlocks;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.acid.Acid;
import com.avp.common.item.AVPItems;
import com.avp.common.level.gameevent.AVPGameEvents;
import com.avp.common.particle.AVPParticleTypes;

public class AlienVariantTypes {

    public static final AlienVariantType ABERRANT = new AlienVariantType(
        AlienVariant.ABERRANT,

        AVPBlocks.ABERRANT_RESIN,
        AVPBlocks.ABERRANT_RESIN_NODE,
        AVPBlocks.ABERRANT_RESIN_VEIN,
        AVPBlocks.ABERRANT_RESIN_WEB,

        AVPBlockTags.ABERRANT_RESIN,

        AVPItems.ABERRANT_CHITIN,
        AVPItems.PLATED_ABERRANT_CHITIN,
        AVPItems.ABERRANT_RESIN_BALL,

        AVPGameEvents.XENOMORPH_ABERRANT_RESIN_SPREAD,

        AVPParticleTypes.ACID
    );

    public static final AlienVariantType IRRADIATED = new AlienVariantType(
        AlienVariant.IRRADIATED,

        AVPBlocks.IRRADIATED_RESIN,
        AVPBlocks.IRRADIATED_RESIN_NODE,
        AVPBlocks.IRRADIATED_RESIN_VEIN,
        AVPBlocks.IRRADIATED_RESIN_WEB,

        AVPBlockTags.IRRADIATED_RESIN,

        AVPItems.IRRADIATED_CHITIN,
        AVPItems.PLATED_IRRADIATED_CHITIN,
        AVPItems.IRRADIATED_RESIN_BALL,

        AVPGameEvents.XENOMORPH_IRRADIATED_RESIN_SPREAD,

        AVPParticleTypes.IRRADIATED_ACID
    );

    public static final AlienVariantType NETHER = new AlienVariantType(
        AlienVariant.NETHER,

        AVPBlocks.NETHER_RESIN,
        AVPBlocks.NETHER_RESIN_NODE,
        AVPBlocks.NETHER_RESIN_VEIN,
        AVPBlocks.NETHER_RESIN_WEB,

        AVPBlockTags.NETHER_RESIN,

        AVPItems.NETHER_CHITIN,
        AVPItems.PLATED_NETHER_CHITIN,
        AVPItems.NETHER_RESIN_BALL,

        AVPGameEvents.XENOMORPH_NETHER_RESIN_SPREAD,

        AVPParticleTypes.BLUE_ACID
    );

    public static final AlienVariantType NORMAL = new AlienVariantType(
        AlienVariant.NORMAL,

        AVPBlocks.RESIN,
        AVPBlocks.RESIN_NODE,
        AVPBlocks.RESIN_VEIN,
        AVPBlocks.RESIN_WEB,

        AVPBlockTags.NORMAL_RESIN,

        AVPItems.CHITIN,
        AVPItems.PLATED_CHITIN,
        AVPItems.RESIN_BALL,

        AVPGameEvents.XENOMORPH_RESIN_SPREAD,

        AVPParticleTypes.ACID
    );

    private static final Map<AlienVariant, AlienVariantType> TYPE_BY_VARIANT = Util.make(() -> {
        var map = new EnumMap<AlienVariant, AlienVariantType>(AlienVariant.class);

        map.put(AlienVariant.ABERRANT, ABERRANT);
        map.put(AlienVariant.IRRADIATED, IRRADIATED);
        map.put(AlienVariant.NETHER, NETHER);
        map.put(AlienVariant.NORMAL, NORMAL);

        if (!Arrays.stream(AlienVariant.values()).allMatch(map::containsKey)) {
            throw new IllegalStateException("Missing variant types from variant map.");
        }

        return map;
    });

    private static final Map<Integer, AlienVariantType> TYPE_BY_ID = TYPE_BY_VARIANT.entrySet()
        .stream()
        .collect(Collectors.toMap(entry -> entry.getKey().getId(), Map.Entry::getValue));

    public static AlienVariantType getFor(AlienVariant alienVariant) {
        return TYPE_BY_VARIANT.get(alienVariant);
    }

    /**
     * Usage of this function is discouraged since it can return Option#none. Consider using getFor(AlienVariant) where
     * possible.
     */
    public static Option<AlienVariantType> getFor(Integer alienVariantId) {
        return Option.ofNullable(TYPE_BY_ID.get(alienVariantId));
    }

    /**
     * Usage of this function is discouraged since it can return Option#none. Consider using getFor(AlienVariant) where
     * possible.
     */
    public static Option<AlienVariantType> getFor(Block block) {
        return getFor(block.defaultBlockState());
    }

    /**
     * Usage of this function is discouraged since it can return null. Consider using getFor(AlienVariant) where
     * possible.
     */
    public static @Nullable AlienVariantType getForOrNull(BlockState blockState) {
        if (blockState.is(AVPBlockTags.ABERRANT_RESIN)) {
            return ABERRANT;
        } else if (blockState.is(AVPBlockTags.IRRADIATED_RESIN)) {
            return IRRADIATED;
        } else if (blockState.is(AVPBlockTags.NETHER_RESIN)) {
            return NETHER;
        } else if (blockState.is(AVPBlockTags.RESIN)) {
            return NORMAL;
        }

        return null;
    }

    /**
     * Usage of this function is discouraged since it can return Option#none. Consider using getFor(AlienVariant) where
     * possible.
     */
    public static Option<AlienVariantType> getFor(BlockState blockState) {
        return Option.ofNullable(getForOrNull(blockState));
    }

    public static AlienVariantType getFor(Alien alien) {
        return getFor(alien.getVariant());
    }

    /**
     * Usage of this function is discouraged since it can return Option#none. Consider using getFor(AlienVariant) where
     * possible.
     */
    public static Option<AlienVariantType> getFor(Entity entity) {
        return getFor(entity.getType());
    }

    /**
     * Usage of this function is discouraged since it can return Option#none. Consider using getFor(AlienVariant) where
     * possible.
     */
    @Deprecated(forRemoval = true)
    public static Option<AlienVariantType> getFor(Acid entity) {
        if (entity.isIrradiated()) {
            return Option.some(IRRADIATED);
        } else if (entity.isNetherAfflicted()) {
            return Option.some(NETHER);
        }

        return Option.some(NORMAL);
    }

    /**
     * Usage of this function is discouraged since it can return Option#none. Consider using getFor(AlienVariant) where
     * possible.
     */
    public static Option<AlienVariantType> getFor(EntityType<?> entityType) {
        if (entityType.is(AVPEntityTypeTags.ABERRANT_ALIENS)) {
            return Option.some(ABERRANT);
        } else if (entityType.is(AVPEntityTypeTags.IRRADIATED_ALIENS)) {
            return Option.some(IRRADIATED);
        } else if (entityType.is(AVPEntityTypeTags.NETHER_ALIENS)) {
            return Option.some(NETHER);
        } else if (entityType.is(AVPEntityTypeTags.NORMAL_ALIENS)) {
            return Option.some(NORMAL);
        }

        return Option.none();
    }
}
