package com.blib.common.gameplay.model;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public interface HolderExtension<T> {
    @SuppressWarnings("unchecked")
    default Holder<T> blib$getDelegate() {
        return (Holder<T>) this;
    }

    default @Nullable ResourceKey<T> blib$getKey() {
        return blib$getDelegate().unwrapKey().orElse(null);
    }
}
