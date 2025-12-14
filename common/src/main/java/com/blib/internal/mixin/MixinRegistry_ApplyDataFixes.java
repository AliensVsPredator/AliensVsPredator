package com.blib.internal.mixin;

import com.blib.common.data.fixer.BLibDataFixerRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@ApiStatus.Internal
@Mixin(MappedRegistry.class)
public abstract class MixinRegistry_ApplyDataFixes<T> implements WritableRegistry<T> {

    @Shadow
    @Final
    private Map<ResourceLocation, Holder.Reference<T>> byLocation;

    @Inject(
        method = "get(Lnet/minecraft/resources/ResourceKey;)Ljava/lang/Object;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fixedGet(@Nullable ResourceKey<T> key, CallbackInfoReturnable<@Nullable T> cir) {
        fixedGet(key != null ? key.location() : null, cir);
    }

    @Inject(
        method = "get(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fixedGet(@Nullable ResourceLocation name, CallbackInfoReturnable<@Nullable T> cir) {
        var fixed = convertResourceLocation(name);

        if (fixed != null) {
            cir.setReturnValue(fixed.value());
        }
    }

    @Inject(
        method = "getHolder(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fixedGetHolder(@Nullable ResourceKey<?> key, CallbackInfoReturnable<Optional<Holder.Reference<T>>> cir) {
        fixedGetHolder(key != null ? key.location() : null, cir);
    }

    @Inject(
        method = "getHolder(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fixedGetHolder(@Nullable ResourceLocation resourceLocation, CallbackInfoReturnable<Optional<Holder.Reference<T>>> cir) {
        if (resourceLocation != null) {
            var fixed = convertResourceLocation(resourceLocation);

            if (fixed != null) {
                cir.setReturnValue(Optional.of(fixed));
            }
        }
    }

    @Inject(
        method = "getOrCreateHolderOrThrow",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fixedGetOrCreateHolderOrThrow(ResourceKey<T> key, CallbackInfoReturnable<Holder.Reference<T>> cir) {
        if (key != null) {
            var fixed = convertResourceLocation(key.location());

            if (fixed != null) {
                cir.setReturnValue(fixed);
            }
        }
    }

    @Unique
    private @Nullable Holder.Reference<T> convertResourceLocation(@Nullable ResourceLocation resourceLocation) {
        return convertResourceLocation(this.key().location(), resourceLocation);
    }

    @Unique
    private @Nullable Holder.Reference<T> convertResourceLocation(
        ResourceLocation registryResourceLocation,
        @Nullable ResourceLocation resourceLocation
    ) {
        if (resourceLocation != null) {
            var fixed = BLibDataFixerRegistry.getFixedValueInRegistry(registryResourceLocation, resourceLocation);

            // don't override if the "fixed" version is missing
            if (fixed != null) {
                return this.byLocation.get(fixed);
            }
        }

        return null;
    }
}
