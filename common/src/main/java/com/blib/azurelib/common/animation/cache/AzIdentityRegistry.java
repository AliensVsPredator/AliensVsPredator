package com.blib.azurelib.common.animation.cache;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AzIdentityRegistry {

    private static final Set<Item> IDENTITY_OF_ITEMS = new HashSet<>();

    public static void register(@NotNull Item first, Item... rest) {
        IDENTITY_OF_ITEMS.add(first);
        IDENTITY_OF_ITEMS.addAll(Arrays.asList(rest));
    }

    public static boolean hasIdentity(Item item) {
        return IDENTITY_OF_ITEMS.contains(item);
    }
}
