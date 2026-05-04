package com.blib.api.common.dismemberment.v1.builtin;

import java.util.function.UnaryOperator;

import com.blib.api.common.dismemberment.v1.LimbDefinition;

/**
 * Composable tweak slot for one part's {@link LimbDefinition.Builder}. Built-in helpers (and any consumer-side helper)
 * use this as the primitive behind their {@code Tweaks} inner classes — each helper exposes one typed method per part
 * that delegates to {@link #append(UnaryOperator)}, then resolves to {@link #build(LimbDefinition.Builder)} when it's
 * time to register.
 * <p>
 * Composition over inheritance: helpers don't extend a base class, they hold {@code LimbTweakSlot} fields and forward
 * to them. Tweaks compose — calling {@code append} twice chains the operators in registration order.
 */
public final class LimbTweakSlot {

    private UnaryOperator<LimbDefinition.Builder> tweak = UnaryOperator.identity();

    /** Append another tweak. Tweaks compose in registration order. */
    public LimbTweakSlot append(UnaryOperator<LimbDefinition.Builder> next) {
        var current = this.tweak;
        this.tweak = builder -> next.apply(current.apply(builder));
        return this;
    }

    /** Apply all accumulated tweaks to the given default builder. */
    public LimbDefinition.Builder apply(LimbDefinition.Builder defaults) {
        return tweak.apply(defaults);
    }

    /** Apply all accumulated tweaks and {@code build} the final {@link LimbDefinition}. */
    public LimbDefinition build(LimbDefinition.Builder defaults) {
        return apply(defaults).build();
    }
}
