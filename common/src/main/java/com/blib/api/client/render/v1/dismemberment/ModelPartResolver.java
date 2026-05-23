package com.blib.api.client.render.v1.dismemberment;

import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

/**
 * Returns the {@link ModelPart} of a vanilla model that corresponds to a {@code partName} (e.g. {@code "head"},
 * {@code "right_arm"}). Implementations are explicit per model class — no reflection — and registered via
 * {@link ModelPartResolverRegistry}.
 *
 * @param <M> the model class this resolver handles. The registry guarantees by registration that the runtime model
 *            instance is assignment-compatible with {@code M} before {@link #find} is invoked.
 */
@FunctionalInterface
public interface ModelPartResolver<M> {

    @Nullable
    ModelPart find(M model, String partName);
}
