package com.blib.mod.common.property;

import com.just.core.functional.option.Option;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;

import com.blib.api.BLibAPI;
import com.blib.api.common.property.v1.BLibProperties;
import com.blib.api.common.property.v1.BLibPropertyContainer;
import com.blib.api.common.property.v1.BLibPropertyKey;

public class BLibModPropertyAccess {

    private static final Path PATH = BLibAPI.getGameDirectory().resolve(Path.of("blib.properties"));

    public static final BLibModPropertyAccess INSTANCE = new BLibModPropertyAccess();

    private BLibPropertyContainer propertyContainer;

    public void save() {
        BLibProperties.save(PATH, getOrCreatePropertyContainer(), BLibModPropertySchema.SCHEMA);
    }

    private BLibPropertyContainer getOrCreatePropertyContainer() {
        if (this.propertyContainer == null) {
            var result = BLibProperties.load(PATH, BLibModPropertySchema.SCHEMA);

            result
                .inspect(propertyContainer -> this.propertyContainer = propertyContainer)
                .inspectErr(loadError -> {
                    switch (loadError) {
                        case BLibProperties.LoadError.DoesNotExist ignored -> {
                            this.propertyContainer = new BLibPropertyContainer(new HashMap<>(), BLibModPropertySchema.SCHEMA);
                            save();
                        }
                        case BLibProperties.LoadError.ReadFailure readFailure -> throw new RuntimeException(readFailure.exception());
                    }
                });
        }

        return propertyContainer;
    }

    public BLibPropertyContainer getContainer() {
        return getOrCreatePropertyContainer();
    }

    public Path getPath() {
        return PATH;
    }

    public <T> @Nullable T getOrNull(BLibPropertyKey.Leaf<T> propertyKey) {
        return getOrCreatePropertyContainer().getOrNull(propertyKey);
    }

    public <T> Option<T> get(BLibPropertyKey.Leaf<T> propertyKey) {
        return Option.ofNullable(getOrNull(propertyKey));
    }

    public <T> T getOrThrow(BLibPropertyKey.Leaf<T> propertyKey) {
        return Objects.requireNonNull(getOrNull(propertyKey));
    }

    public <T> T getOrDefault(BLibPropertyKey.Leaf<T> propertyKey, T defaultValue) {
        var value = getOrNull(propertyKey);

        return value == null
            ? defaultValue
            : value;
    }

    public <T> T get(BLibModProperty<T> property) {
        return getOrDefault(property.key(), property.defaultValue());
    }

    public <T> void set(BLibPropertyKey.Leaf<T> propertyKey, T value) {
        getOrCreatePropertyContainer().set(propertyKey, value);
    }
}
