package org.avp.common.damage;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredRegistry;

import java.util.function.Supplier;

public class AVPDamageSources extends AVPDeferredRegistry<AVPDamageSource> {

    public static final AVPDamageSources INSTANCE = new AVPDamageSources();

    public final Holder<AVPDamageSource> acid = createHolder("acid", () -> new AVPDamageSource(AVPDamageTypes.INSTANCE.acid));

    private AVPDamageSources() {}

    @Override
    protected Holder<AVPDamageSource> createHolder(String registryName, Supplier<AVPDamageSource> supplier) {
        return new Holder<>(registryName, supplier);
    }

    @Override
    public void register() { /* NO-OP */ }
}
