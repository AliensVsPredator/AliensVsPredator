package com.blib.internal.client.render;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.internal.client.animation.AzAnimatorAccessor;
import com.blib.internal.client.model.AzBakedModel;
import com.blib.internal.client.model.AzBakedModelCache;

public class AzProvider<K, T> {

    protected final Supplier<AzAnimator<K, T>> animatorSupplier;

    protected final BiFunction<Entity, T, ResourceLocation> modelLocationProvider;

    protected final Function<T, K> UUIDProvider;

    public AzProvider(
        Supplier<AzAnimator<K, T>> animatorSupplier,
        BiFunction<Entity, T, ResourceLocation> modelLocationProvider,
        Function<T, K> UUIDProvider
    ) {
        this.animatorSupplier = animatorSupplier;
        this.modelLocationProvider = modelLocationProvider;
        this.UUIDProvider = UUIDProvider;
    }

    public @Nullable AzBakedModel provideBakedModel(@Nullable Entity entity, @NotNull T animatable) {
        // Always have a safe fallback
        var modelLocation = modelLocationProvider.apply(entity, animatable);
        var shared = AzBakedModelCache.getInstance().getOrNull(modelLocation);

        if (shared == null) {
            return AzBakedModel.getDefault();
        }

        // Try to return the per-instance model if an animator/context already exists
        var animator = AzAnimatorAccessor.getOrNull(animatable);
        if (animator == null) {
            return shared; // <- avoid NPE: animator not created yet
        }

        var ctx = animator.context();
        if (ctx == null) {
            return shared; // <- avoid NPE: context not set yet this frame
        }

        var cache = ctx.boneCache();
        if (cache == null || cache.isEmpty()) {
            return shared; // <- cache isn't initialized yet
        }

        return cache.getBakedModel(); // <- the deep-copied, per-instance model
    }

    public @Nullable AzAnimator<K, T> provideAnimator(@Nullable Entity entity, T animatable) {
        // TODO: Instead of caching the entire animator itself, we're going to want to cache the relevant data for the
        // entity.
        var accessor = AzAnimatorAccessor.<K, T>cast(animatable);
        var cachedAnimator = accessor.getAnimatorOrNull();

        if (cachedAnimator == null) {
            cachedAnimator = animatorSupplier.get();
            if (cachedAnimator != null) {
                // Create a per-instance context now
                var ctx = cachedAnimator.getOrCreateContext(UUIDProvider.apply(animatable));

                // Install a deep-copied model into the bone cache BEFORE controllers
                var modelLocation = modelLocationProvider.apply(entity, animatable);
                var shared = AzBakedModelCache.getInstance().getOrNull(modelLocation);
                if (shared != null) {
                    ctx.boneCache().setActiveModel(shared); // setActiveModel deep-copies internally
                }

                // Controllers see a ready context & model
                cachedAnimator.registerControllers(cachedAnimator.getAnimationControllerContainer());
                accessor.setAnimator(cachedAnimator);
            }
        }

        return cachedAnimator;
    }
}
