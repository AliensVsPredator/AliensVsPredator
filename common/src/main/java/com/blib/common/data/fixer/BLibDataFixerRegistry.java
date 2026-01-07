package com.blib.common.data.fixer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BLibDataFixerRegistry {

    private static final Map<Integer, Entry> DATA_FIXER_KEY_TO_ENTRY = new HashMap<>();

    public static void register(Entry entry) {
        var hash = computeHash(entry.registryResourceLocation(), entry.from());
        DATA_FIXER_KEY_TO_ENTRY.put(hash, entry);
    }

    public static @Nullable ResourceLocation getFixedValueInRegistry(
        ResourceLocation registryResourceLocation,
        @Nullable ResourceLocation resourceLocation
    ) {
        if (resourceLocation == null) {
            return null;
        }

        var hash = computeHash(registryResourceLocation, resourceLocation);
        var entry = DATA_FIXER_KEY_TO_ENTRY.get(hash);

        return entry == null ? null : entry.to();
    }

    private static int computeHash(ResourceLocation registryResourceLocation, ResourceLocation resourceLocation) {
        return 31 * registryResourceLocation.hashCode() + resourceLocation.hashCode();
    }

    public sealed interface Entry {

        ResourceLocation registryResourceLocation();

        ResourceLocation from();

        ResourceLocation to();

        record Direct(
            Registry<?> registry,
            ResourceLocation from,
            ResourceLocation to
        ) implements Entry {

            @Override
            public ResourceLocation registryResourceLocation() {
                return registry.key().location();
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
        }
    }
}
