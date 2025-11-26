package com.blib.common.data.fixer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BLibDataFixerRegistry {

    private static final Map<Key, Entry> DATA_FIXER_KEY_TO_ENTRY = new HashMap<>();

    public static void register(Entry entry) {
        var key = new Key(entry.registry(), entry.from());
        DATA_FIXER_KEY_TO_ENTRY.put(key, entry);
    }

    public static <T> ResourceLocation getFixedValueInRegistry(Registry<T> registry, @Nullable ResourceLocation resourceLocation) {
        var key = new Key(registry, resourceLocation);
        var entry = DATA_FIXER_KEY_TO_ENTRY.get(key);
        return entry == null ? null : entry.to();
    }

    public record Key(
        Registry<?> registry,
        ResourceLocation from
    ) {}

    public record Entry(
        Registry<?> registry,
        ResourceLocation from,
        ResourceLocation to
    ) {}
}
