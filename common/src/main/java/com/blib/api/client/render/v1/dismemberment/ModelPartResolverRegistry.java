package com.blib.api.client.render.v1.dismemberment;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maps a vanilla model class (or interface) to a {@link ModelPartResolver}. The registry's
 * {@link #resolve(EntityModel, String)} method walks the model's class hierarchy and then its implemented interfaces,
 * picking the most specific registered resolver. Lookup results are cached per concrete model class so the hot render
 * path is a single map lookup once warm.
 * <p>
 * BLib registers built-in resolvers for {@code HumanoidModel}, {@code HierarchicalModel}, and {@code HeadedModel} (the
 * head-only fallback) at client init. Mods that use custom model classes can register additional resolvers here.
 */
public final class ModelPartResolverRegistry {

    private static final ModelPartResolver<Object> NONE = (model, name) -> null;

    private static final ConcurrentHashMap<Class<?>, ModelPartResolver<?>> REGISTERED = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Class<?>, ModelPartResolver<?>> RESOLVED_CACHE = new ConcurrentHashMap<>();

    private ModelPartResolverRegistry() {}

    public static <M> void register(Class<M> modelClass, ModelPartResolver<M> resolver) {
        REGISTERED.put(modelClass, resolver);
        // Invalidate the cache so previously-cached "no resolver" answers don't stick around after a late registration.
        RESOLVED_CACHE.clear();
    }

    public static @Nullable ModelPart resolve(EntityModel<?> model, String partName) {
        var resolver = resolverFor(model.getClass());

        if (resolver == NONE) {
            return null;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        var typed = (ModelPartResolver) resolver;
        return typed.find(model, partName);
    }

    private static ModelPartResolver<?> resolverFor(Class<?> modelClass) {
        var cached = RESOLVED_CACHE.get(modelClass);

        if (cached != null) {
            return cached;
        }

        var found = lookup(modelClass);
        var toCache = found != null ? found : NONE;
        RESOLVED_CACHE.put(modelClass, toCache);
        return toCache;
    }

    private static @Nullable ModelPartResolver<?> lookup(Class<?> modelClass) {
        // Class chain first — most specific match wins.
        for (Class<?> c = modelClass; c != null && c != Object.class; c = c.getSuperclass()) {
            var r = REGISTERED.get(c);

            if (r != null) {
                return r;
            }
        }

        // Then interfaces (BFS so closer interfaces win over further ancestors).
        var visited = new HashSet<Class<?>>();
        var queue = new ArrayDeque<Class<?>>();
        queue.add(modelClass);

        while (!queue.isEmpty()) {
            var c = queue.poll();

            if (!visited.add(c)) {
                continue;
            }

            for (var iface : c.getInterfaces()) {
                var r = REGISTERED.get(iface);

                if (r != null) {
                    return r;
                }

                queue.add(iface);
            }

            var sup = c.getSuperclass();

            if (sup != null) {
                queue.add(sup);
            }
        }

        return null;
    }
}
