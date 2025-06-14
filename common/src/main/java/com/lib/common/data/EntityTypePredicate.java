package com.lib.common.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Objects;

public sealed interface EntityTypePredicate permits EntityTypePredicate.Single, EntityTypePredicate.List, EntityTypePredicate.Tag {

    boolean test(EntityType<?> entityType);

    record Single(EntityType<?> entityType) implements EntityTypePredicate {

        @Override
        public boolean test(EntityType<?> type) {
            return this.entityType == type;
        }
    }

    record List(java.util.List<? extends EntityType<?>> entityTypes) implements EntityTypePredicate {

        @Override
        public boolean test(EntityType<?> type) {
            return entityTypes.stream()
                .anyMatch(type::equals);
        }
    }

    record Tag(TagKey<EntityType<?>> entityTypeTagKey) implements EntityTypePredicate {

        @Override
        public boolean test(EntityType<?> type) {
            return type.is(entityTypeTagKey);
        }
    }

    Codec<EntityTypePredicate> CODEC = Codec.either(
        // Single or tag string
        Codec.STRING,
        // List of strings
        Codec.list(Codec.STRING)
    ).flatXmap(EntityTypePredicate::decodePredicate, EntityTypePredicate::encodePredicate);

    private static DataResult<EntityTypePredicate> decodePredicate(Either<String, java.util.List<String>> either) {
        try {
            if (either.left().isPresent()) {
                var str = either.left().get();

                if (str.startsWith("#")) {
                    var tagId = ResourceLocation.parse(str.substring(1));
                    TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, tagId);
                    return DataResult.success(new EntityTypePredicate.Tag(tagKey));
                } else {
                    EntityType<?> entityType = EntityType.byString(str)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown EntityType: " + str));
                    return DataResult.success(new EntityTypePredicate.Single(entityType));
                }
            } else {
                var list = either.right().get();
                var types = list.stream()
                    .map(EntityType::byString)
                    .map(opt -> opt.orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
                return DataResult.success(new EntityTypePredicate.List(types));
            }
        } catch (Exception e) {
            return DataResult.error(() -> "Invalid EntityTypePredicate: " + e.getMessage());
        }
    }

    private static DataResult<Either<String, java.util.List<String>>> encodePredicate(EntityTypePredicate predicate) {
        if (predicate instanceof Single(EntityType<?> entityType)) {
            return DataResult.success(Either.left(EntityType.getKey(entityType).toString()));
        } else if (predicate instanceof Tag(TagKey<EntityType<?>> entityTypeTagKey)) {
            return DataResult.success(Either.left("#" + entityTypeTagKey.location()));
        } else if (predicate instanceof List(java.util.List<? extends EntityType<?>> entityTypes)) {
            var encoded = entityTypes.stream()
                .map(EntityType::getKey)
                .map(ResourceLocation::toString)
                .toList();

            return DataResult.success(Either.right(encoded));
        }
        return DataResult.error(() -> "Unknown EntityTypePredicate variant");
    }
}
