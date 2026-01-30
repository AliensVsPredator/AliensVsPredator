package com.blib.internal.client.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class AzBakedModelFactoryRegistry {

    private static final Map<String, AzBakedModelFactory> FACTORIES = new Object2ObjectOpenHashMap<>(1);

    private static final AzBakedModelFactory DEFAULT_FACTORY = new AzBuiltinBakedModelFactory();

    public static AzBakedModelFactory getForNamespace(String namespace) {
        return FACTORIES.getOrDefault(namespace, DEFAULT_FACTORY);
    }

    public static void register(String namespace, AzBakedModelFactory factory) {
        FACTORIES.put(namespace, factory);
    }
}
