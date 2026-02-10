package com.blib.api.common.property.v1;

import com.just.core.functional.option.Option;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

import com.blib.api.common.util.v1.Dirty;

public class BLibPropertyContainer implements Dirty {

    private final Map<String, BLibPropertyValue<?>> pathToValueMap;

    private final BLibPropertySchema schema;

    private boolean dirty;

    public BLibPropertyContainer(Map<String, BLibPropertyValue<?>> pathToValueMap, BLibPropertySchema schema) {
        this.pathToValueMap = pathToValueMap;
        this.schema = schema;
    }

    public <T> @Nullable T getOrNull(BLibPropertyKey.Leaf<T> propertyKey) {
        var path = propertyKey.path();
        var propertyValueOrNull = pathToValueMap.get(path);

        return switch (propertyValueOrNull) {
            case null -> {
                var property = schema.getPropertyByPathOrNull(path);

                if (property == null) {
                    yield null;
                }

                @SuppressWarnings("unchecked")
                var castDefaultValue = (T) property.defaultValue();
                yield castDefaultValue;
            }
            case BLibPropertyValue.Deserialized<?> value -> {
                @SuppressWarnings("unchecked")
                BLibPropertyValue.Deserialized<T> castValue = (BLibPropertyValue.Deserialized<T>) value;
                yield castValue.value();
            }
            case BLibPropertyValue.Serialized<?> value -> {
                @SuppressWarnings("unchecked")
                var castPropertyValue = (BLibPropertyValue.Serialized<T>) value;

                var deserialized = castPropertyValue.deserializedOrNull(propertyKey.serializer());

                pathToValueMap.put(path, new BLibPropertyValue.Deserialized<>(deserialized));

                yield deserialized;
            }
        };
    }

    public <T> Option<T> get(BLibPropertyKey.Leaf<T> propertyKey) {
        return Option.ofNullable(getOrNull(propertyKey));
    }

    public <T> T getOrThrow(BLibPropertyKey.Leaf<T> propertyKey) {
        return Objects.requireNonNull(getOrNull(propertyKey));
    }

    public <T> void set(BLibPropertyKey.Leaf<T> propertyKey, T value) {
        pathToValueMap.put(propertyKey.path(), new BLibPropertyValue.Deserialized<>(value));
        markDirty();
    }

    public Map<String, BLibPropertyValue<?>> getPathToValueMap() {
        return Map.copyOf(pathToValueMap);
    }

    public BLibPropertySchema getSchema() {
        return schema;
    }

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void clearDirty() {
        this.dirty = false;
    }
}
