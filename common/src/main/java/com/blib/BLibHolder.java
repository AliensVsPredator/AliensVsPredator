package com.blib;

import com.blib.common.gameplay.model.HolderExtension;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BLibHolder<T> implements Holder<T>, HolderExtension<T>, Supplier<T> {

    private final BLibRegistry<? super T> registry;

    private final ResourceKey<T> key;

    private @Nullable Holder<T> holder = null;

    /* package-private */ BLibHolder(BLibRegistry<? super T> registry, String path) {
        this.registry = registry;
        @SuppressWarnings("unchecked")
        var backingRegistry = (Registry<T>) registry.getBackingRegistry();
        this.key = ResourceKey.create(backingRegistry.key(), registry.getMod().createResourceLocation(path));
    }

    @Override
    public ResourceKey<T> blib$getKey() {
        return key;
    }

    @Override
    public @NotNull T value() {
        bind(true);
        return getHolder().value();
    }

    @Override
    public T get() {
        return value();
    }

    @Override
    public boolean isBound() {
        bind(false);
        return this.holder != null && holder.isBound();
    }

    @Override
    public boolean is(ResourceLocation id) {
        return id.equals(key.location());
    }

    @Override
    public boolean is(@NotNull ResourceKey<T> key) {
        return key == this.key;
    }

    @Override
    public boolean is(Predicate<ResourceKey<T>> filter) {
        return filter.test(key);
    }

    @Override
    public boolean is(@NotNull TagKey<T> tag) {
        bind(false);
        return this.holder != null && holder.is(tag);
    }

    /** @deprecated */
    @Deprecated
    @Override
    public boolean is(@NotNull Holder<T> holder) {
        bind(false);
        return this.holder != null && holder.is(holder);
    }

    @Override
    public @NotNull Stream<TagKey<T>> tags() {
        bind(false);
        return this.holder != null ? holder.tags() : Stream.empty();
    }

    @Override
    public @NotNull Either<ResourceKey<T>, T> unwrap() {
        return Either.left(key);
    }

    @Override
    public @NotNull Optional<ResourceKey<T>> unwrapKey() {
        return Optional.of(key);
    }

    @Override
    public @NotNull Holder.Kind kind() {
        return Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(@NotNull HolderOwner<T> owner) {
        bind(false);
        return this.holder != null && holder.canSerializeIn(owner);
    }

    @SuppressWarnings("unchecked")
    public @Nullable Registry<T> getBackingRegistry() {
        return (Registry<T>) BuiltInRegistries.REGISTRY.get(key.registry());
    }

    public Holder<T> getHolder() {
        if (this.holder == null) {
            throw new IllegalStateException("Attempted to access unregistered BLibHolder. Path: %s".formatted(getPath()));
        }

        return holder;
    }

    public String getPath() {
        return getResourceLocation().getPath();
    }

    public BLibRegistry<? super T> getRegistry() {
        return registry;
    }

    public ResourceLocation getResourceLocation() {
        return key.location();
    }

    protected final void bind(boolean throwOnMissingRegistry) {
        if (this.holder == null) {
            var registry = getBackingRegistry();

            if (registry != null) {
                this.holder = registry.getHolder(key).orElse(null);
            } else if (throwOnMissingRegistry) {
                throw new IllegalStateException("Registry not present for " + this + ": " + key.registry());
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            if (obj instanceof Holder<?> otherHolder && otherHolder.kind() == Kind.REFERENCE) {
                var extendedHolder = (HolderExtension<?>) obj;
                return extendedHolder.blib$getKey() == key;
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "BLibHolder{%s}", key);
    }
}
