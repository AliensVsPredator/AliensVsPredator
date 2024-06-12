package org.avp.common.registry.holder;

import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.AVPResources;

public class AVPHolder<T> extends BLHolder<T> {

    public AVPHolder(String registryName, Supplier<T> supplier) {
        super(AVPResources.location(registryName), supplier);
    }
}
