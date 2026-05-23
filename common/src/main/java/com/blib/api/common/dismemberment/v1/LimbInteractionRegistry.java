package com.blib.api.common.dismemberment.v1;

import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;

/**
 * Registry of right-click drop rules for {@link DismemberedLimbEntity}.
 * <p>
 * Each entry pairs a {@link Predicate} that decides whether a given limb is eligible with a {@link Function} that
 * produces the {@link ItemStack} to drop. {@link #tryProduce(DismemberedLimbEntity)} walks the entries in registration
 * order and returns the first non-empty stack — registration order doubles as priority, so more specific rules should
 * be registered before broader fallbacks.
 * <p>
 * Predicates run on every right-click attempt, so they should be cheap (e.g. tag/entity-type checks rather than NBT
 * deserialization). Producers may inspect the limb's source NBT to vary the drop by variant.
 */
public final class LimbInteractionRegistry {

    private record Entry(
        Predicate<DismemberedLimbEntity> predicate,
        Function<DismemberedLimbEntity, ItemStack> producer
    ) {}

    private static final List<Entry> ENTRIES = new CopyOnWriteArrayList<>();

    private LimbInteractionRegistry() {}

    public static void register(
        Predicate<DismemberedLimbEntity> predicate,
        Function<DismemberedLimbEntity, ItemStack> producer
    ) {
        Objects.requireNonNull(predicate, "predicate");
        Objects.requireNonNull(producer, "producer");
        ENTRIES.add(new Entry(predicate, producer));
    }

    /**
     * Returns the first non-empty stack produced by a matching entry, or {@link ItemStack#EMPTY} if no entry matched
     * (or all matching entries returned empty stacks).
     */
    public static ItemStack tryProduce(DismemberedLimbEntity limb) {
        for (var entry : ENTRIES) {
            if (!entry.predicate.test(limb)) {
                continue;
            }

            var stack = entry.producer.apply(limb);

            if (stack != null && !stack.isEmpty()) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }
}
