package com.blib.common.data.fixer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BLibDataFixerRegistry {

    private static final Map<Key, Entry> DATA_FIXER_KEY_TO_ENTRY = new HashMap<>();

    public static void register(Entry entry) {
        DATA_FIXER_KEY_TO_ENTRY.put(entry.asKey(), entry);
    }

    public static ResourceLocation getFixedValueInRegistry(ResourceLocation registryResourceLocation, @Nullable ResourceLocation resourceLocation) {
        var key = new Key.Resource(registryResourceLocation, resourceLocation);
        return getFixedValueInRegistry(key);
    }

    public static <T> ResourceLocation getFixedValueInRegistry(Registry<T> registry, @Nullable ResourceLocation resourceLocation) {
        var key = new Key.Direct(registry, resourceLocation);
        return getFixedValueInRegistry(key);
    }

    private static @Nullable ResourceLocation getFixedValueInRegistry(Key key) {
        var entry = DATA_FIXER_KEY_TO_ENTRY.get(key);
        return entry == null ? null : entry.to();
    }

    public sealed interface Key {

        ResourceLocation from();

        record Direct(
            Registry<?> registry,
            ResourceLocation from
        ) implements Key {}

        record Resource(
            ResourceLocation registryResourceLocation,
            ResourceLocation from
        ) implements Key {}
    }

    public sealed interface Entry {

        Key asKey();

        ResourceLocation from();

        ResourceLocation to();

        record Direct(
            Registry<?> registry,
            ResourceLocation from,
            ResourceLocation to
        ) implements Entry {

            @Override
            public Key asKey() {
                return new Key.Direct(registry, from);
            }
        }

        record Resource(
            ResourceLocation registryResourceLocation,
            ResourceLocation from,
            ResourceLocation to
        ) implements Entry {

            public Resource(
                ResourceKey<? extends Registry<?>> registryResourceKey,
                ResourceLocation from,
                ResourceLocation to
            ) {
                this(registryResourceKey.location(), from, to);
            }

            @Override
            public Key asKey() {
                return new Key.Resource(registryResourceLocation, from);
            }
        }
    }
}
