package com.avp.common.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AVPDeferredHolder<T> implements Holder<T>, Supplier<T> {

    private final Supplier<T> valueSupplier;

    private final Supplier<Holder<T>> valueHolderSupplier;

    public AVPDeferredHolder(Supplier<T> valueSupplier, Supplier<Holder<T>> valueHolderSupplier) {
        this.valueSupplier = valueSupplier;
        this.valueHolderSupplier = valueHolderSupplier;
    }

    @Override
    public T get() {
        return valueSupplier.get();
    }

    @Override
    public @NotNull T value() {
        return valueHolderSupplier.get().value();
    }

    @Override
    public boolean isBound() {
        return valueHolderSupplier.get().isBound();
    }

    @Override
    public boolean is(@NotNull ResourceLocation location) {
        return valueHolderSupplier.get().is(location);
    }

    @Override
    public boolean is(@NotNull ResourceKey<T> resourceKey) {
        return valueHolderSupplier.get().is(resourceKey);
    }

    @Override
    public boolean is(@NotNull Predicate<ResourceKey<T>> predicate) {
        return valueHolderSupplier.get().is(predicate);
    }

    @Override
    public boolean is(@NotNull TagKey<T> tagKey) {
        return valueHolderSupplier.get().is(tagKey);
    }

    @Override
    public boolean is(@NotNull Holder<T> holder) {
        return valueHolderSupplier.get().is(holder);
    }

    @Override
    public @NotNull Stream<TagKey<T>> tags() {
        return valueHolderSupplier.get().tags();
    }

    @Override
    public @NotNull Either<ResourceKey<T>, T> unwrap() {
        return valueHolderSupplier.get().unwrap();
    }

    @Override
    public @NotNull Optional<ResourceKey<T>> unwrapKey() {
        return valueHolderSupplier.get().unwrapKey();
    }

    @Override
    public @NotNull Kind kind() {
        return valueHolderSupplier.get().kind();
    }

    @Override
    public boolean canSerializeIn(@NotNull HolderOwner<T> owner) {
        return valueHolderSupplier.get().canSerializeIn(owner);
    }
}
