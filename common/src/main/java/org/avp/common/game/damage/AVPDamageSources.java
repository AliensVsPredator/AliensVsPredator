package org.avp.common.game.damage;

import java.util.function.Supplier;

import org.avp.api.common.game.damage.CustomDamageSource;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.registry.AVPDeferredRegistry;
import org.avp.common.registry.holder.AVPHolder;

public class AVPDamageSources extends AVPDeferredRegistry<CustomDamageSource> {

    public static final AVPDamageSources INSTANCE = new AVPDamageSources();

    public final BLHolder<CustomDamageSource> acid = createHolder("acid", () -> new CustomDamageSource(AVPDamageTypes.INSTANCE.acid));

    public final BLHolder<CustomDamageSource> bullet = createHolder("bullet", () -> new CustomDamageSource(AVPDamageTypes.INSTANCE.bullet));

    private AVPDamageSources() {}

    @Override
    protected BLHolder<CustomDamageSource> createHolder(String registryName, Supplier<CustomDamageSource> supplier) {
        return new AVPHolder<>(registryName, supplier);
    }

    @Override
    public void register() { /* NO-OP */ }
}
