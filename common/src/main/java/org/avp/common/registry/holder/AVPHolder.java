package org.avp.common.registry.holder;

import org.avp.api.registry.holder.BLHolder;
import org.avp.common.AVPResources;

import java.util.function.Supplier;

public class AVPHolder<T> extends BLHolder<T> {
    public AVPHolder(String registryName, Supplier<T> supplier) {
        super(AVPResources.location(registryName), supplier);
    }
}
