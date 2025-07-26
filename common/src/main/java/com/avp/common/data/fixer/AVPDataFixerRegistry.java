package com.avp.common.data.fixer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AVPDataFixerRegistry {

    private static final Map<AVPDataFixerKey, AVPDataFixerEntry> DATA_FIXER_KEY_TO_ENTRY = new HashMap<>();

    public static void register(AVPDataFixerEntry entry) {
        var key = new AVPDataFixerKey(entry.registry(), entry.from());
        DATA_FIXER_KEY_TO_ENTRY.put(key, entry);
    }

    public static <T> ResourceLocation getFixedValueInRegistry(Registry<T> registry, @Nullable ResourceLocation resourceLocation) {
        var key = new AVPDataFixerKey(registry, resourceLocation);
        var entry = DATA_FIXER_KEY_TO_ENTRY.get(key);
        return entry == null ? null : entry.to();
    }
}
