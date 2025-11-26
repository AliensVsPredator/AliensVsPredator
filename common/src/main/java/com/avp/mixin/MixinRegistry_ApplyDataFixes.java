package com.avp.mixin;

import com.blib.common.data.fixer.BLibDataFixerRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

@Mixin(MappedRegistry.class)
public abstract class MixinRegistry_ApplyDataFixes<T> implements WritableRegistry<T> {

    @Shadow
    @Final
    private Map<ResourceLocation, Holder.Reference<T>> byLocation;

    @Shadow
    @Nullable
    private static <T> T getValueFromNullable(Holder.@Nullable Reference<T> reference) {
        return null;
    }

    @Inject(
        method = "get(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fixedGet(@Nullable ResourceLocation name, CallbackInfoReturnable<@Nullable T> cir) {
        var original = cir.getReturnValue();
        var fixed = convertResourceLocation(name, original);

        if (fixed != original) {
            cir.setReturnValue(fixed);
        }
    }

    @Inject(
        method = "get(Lnet/minecraft/resources/ResourceKey;)Ljava/lang/Object;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fixedGet(@Nullable ResourceKey<T> key, CallbackInfoReturnable<@Nullable T> cir) {
        if (key != null) {
            var original = cir.getReturnValue();
            var fixed = convertResourceLocation(key.location(), original);

            if (fixed != original) {
                cir.setReturnValue(fixed);
            }
        }
    }

    @Inject(
        method = "getHolder(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fixedGetHolder(@Nullable ResourceLocation name, CallbackInfoReturnable<Optional<Holder.Reference<T>>> cir) {
        if (name != null) {
            var original = cir.getReturnValue().orElse(null);
            var fixed = convertResourceLocationHolder(name, original);

            if (fixed != original) {
                cir.setReturnValue(Optional.ofNullable(fixed));
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
            var original = cir.getReturnValue();
            var fixed = convertResourceLocationHolder(key.location(), original);

            if (fixed != original) {
                cir.setReturnValue(fixed);
            }
        }
    }

    @Nullable
    @Unique
    private T convertResourceLocation(@Nullable ResourceLocation name, @Nullable T original) {
        if (name != null) {
            var fixed = BLibDataFixerRegistry.getFixedValueInRegistry(this, name);

            // don't override if the "fixed" version is missing
            if (fixed != null) {
                return getValueFromNullable(this.byLocation.get(fixed));
            }
        }

        return original;
    }

    @Nullable
    @Unique
    private Holder.Reference<T> convertResourceLocationHolder(@Nullable ResourceLocation name, @Nullable Holder.Reference<T> original) {
        if (name != null) {
            var fixed = BLibDataFixerRegistry.getFixedValueInRegistry(this, name);

            // don't override if the "fixed" version is missing
            if (fixed != null) {
                return this.byLocation.get(fixed);
            }
        }

        return original;
    }
}
