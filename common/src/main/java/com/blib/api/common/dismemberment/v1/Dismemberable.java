package com.blib.api.common.dismemberment.v1;

/**
 * Implemented by entities that can lose limbs.
 * <p>
 * Composition over inheritance: the entity owns a {@link DismembermentManager} and exposes it through this interface.
 * AI code uses the manager to query and trigger detachments.
 */
public interface Dismemberable {

    DismembermentManager getDismembermentManager();
}
