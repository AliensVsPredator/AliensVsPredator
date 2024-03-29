package org.avp.neoforge.util;

import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public class ForgeGameObject<T> extends GameObject<T> {

    public ForgeGameObject(DeferredRegister<T> deferredRegister, String registryName, Supplier<T> supplier) {
        super(registryName, deferredRegister.register(registryName, supplier));
    }
}
