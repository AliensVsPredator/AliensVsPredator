package com.blib.api.client.animation.v1.command.policy;

/**
 * The full dispatch contract for a play action: how to interpret the dispatch ({@link AzDispatchMode}), what to do when
 * blocked by an endless animation ({@link OnBlockedByEndless}), and how to handle same-name-different-properties
 * dispatches ({@link OnPropertiesChanged}).
 */
public record AzDispatchPolicy(
    AzDispatchMode mode,
    OnBlockedByEndless onBlockedByEndless,
    OnPropertiesChanged onPropertiesChanged
) {

    public static AzDispatchPolicy of(AzDispatchMode mode) {
        return new AzDispatchPolicy(mode, OnBlockedByEndless.APPEND_ANYWAY, OnPropertiesChanged.RESTART);
    }
}
