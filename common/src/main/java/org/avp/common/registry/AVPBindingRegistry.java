package org.avp.common.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;

public abstract class AVPBindingRegistry<T> {

    public final List<GameObject<T>> ENTRIES = new ArrayList<>();

    protected abstract GameObject<T> registerGameObject(String name, Supplier<T> supplier);

    protected GameObject<T> registerEntry(String name, Supplier<T> supplier) {
        var gameObject = registerGameObject(name, supplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }
}
