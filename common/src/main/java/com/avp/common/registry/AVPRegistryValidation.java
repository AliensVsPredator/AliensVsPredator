package com.avp.common.registry;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.avp.AVP;

public class AVPRegistryValidation {

    public static <T> void throwIfMissingEntries(
        Collection<? extends Supplier<? extends T>> entries,
        Predicate<T> contains,
        Function<T, String> descriptionIdSupplier,
        String message
    ) {
        var unhandledEntries = entries
            .stream()
            .map(Supplier::get)
            .filter(Predicate.not(contains))
            .toList();

        if (!unhandledEntries.isEmpty()) {
            var unhandledBlocksStrings = String.join("\n", unhandledEntries.stream().map(descriptionIdSupplier).toList());
            AVP.LOGGER.error(
                "Detected {} unhandled entries. Entries:\n{}",
                unhandledEntries.size(),
                unhandledBlocksStrings
            );

            throw new IllegalStateException(message);
        }
    }
}
