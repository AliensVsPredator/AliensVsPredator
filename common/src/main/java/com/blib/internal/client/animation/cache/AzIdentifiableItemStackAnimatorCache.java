package com.blib.internal.client.animation.cache;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.WeakHashMap;

import com.blib.api.client.animation.v1.animator.AzItemAnimator;
import com.blib.mod.common.registry.init.BLibDataComponents;

public class AzIdentifiableItemStackAnimatorCache {

    private static final AzIdentifiableItemStackAnimatorCache INSTANCE = new AzIdentifiableItemStackAnimatorCache();

    private static final WeakHashMap<UUID, AzItemAnimator> ANIMATORS_BY_UUID = new WeakHashMap<>();

    public static AzIdentifiableItemStackAnimatorCache getInstance() {
        return INSTANCE;
    }

    private AzIdentifiableItemStackAnimatorCache() {}

    public void add(ItemStack itemStack, AzItemAnimator animator) {
        var uuid = itemStack.get(BLibDataComponents.AZ_ID.get());

        if (uuid != null) {
            ANIMATORS_BY_UUID.computeIfAbsent(uuid, ($) -> animator);
        }
    }

    public @Nullable AzItemAnimator getOrNull(UUID uuid) {
        return uuid == null ? null : ANIMATORS_BY_UUID.get(uuid);
    }
}
