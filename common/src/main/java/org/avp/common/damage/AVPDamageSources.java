package org.avp.common.damage;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredRegistry;

public class AVPDamageSources extends AVPDeferredRegistry<AVPDamageSource> {

    public static final AVPDamageSources INSTANCE = new AVPDamageSources();

    public final Holder<AVPDamageSource> acid = createHolder("acid", () -> new AVPDamageSource(AVPDamageTypes.INSTANCE.acid));

    public final Holder<AVPDamageSource> bullet = createHolder("bullet", () -> new AVPDamageSource(AVPDamageTypes.INSTANCE.bullet));

    private AVPDamageSources() {}

    @Override
    protected Holder<AVPDamageSource> createHolder(String registryName, Supplier<AVPDamageSource> supplier) {
        return new Holder<>(registryName, supplier);
    }

    @Override
    public void register() { /* NO-OP */ }
}
