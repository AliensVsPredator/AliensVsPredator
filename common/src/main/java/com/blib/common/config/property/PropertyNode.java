package com.blib.common.config.property;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Internal tree node for storing property values hierarchically. Each node can have a value and/or children.
 */
final class PropertyNode {

    private @Nullable String rawValue;

    private @Nullable Object cachedValue;

    private final Map<String, PropertyNode> children;

    PropertyNode() {
        this.children = new LinkedHashMap<>();
    }

    /**
     * Gets the raw string value at this node, or null if no value is set.
     */
    @Nullable String getRawValue() {
        return rawValue;
    }

    /**
     * Sets the raw string value at this node. Clears the cached value.
     */
    void setRawValue(@Nullable String rawValue) {
        this.rawValue = rawValue;
        this.cachedValue = null;
    }

    /**
     * Gets the cached deserialized value, or null if not cached.
     */
    @Nullable Object getCachedValue() {
        return cachedValue;
    }

    /**
     * Sets the cached deserialized value.
     */
    void setCachedValue(@Nullable Object cachedValue) {
        this.cachedValue = cachedValue;
    }

    /**
     * Clears the cached value.
     */
    void clearCache() {
        this.cachedValue = null;
    }

    /**
     * Returns the children map.
     */
    Map<String, PropertyNode> getChildren() {
        return children;
    }

    /**
     * Gets a child node by name, or null if it doesn't exist.
     */
    @Nullable PropertyNode getChild(String name) {
        return children.get(name);
    }

    /**
     * Gets or creates a child node by name.
     */
    PropertyNode getOrCreateChild(String name) {
        return children.computeIfAbsent(name, k -> new PropertyNode());
    }

    /**
     * Removes a child node by name. Returns the removed node, or null if it didn't exist.
     */
    @Nullable PropertyNode removeChild(String name) {
        return children.remove(name);
    }

    /**
     * Returns true if this node has a non-blank value.
     */
    boolean hasValue() {
        return rawValue != null && !rawValue.isBlank();
    }

    /**
     * Returns true if this node has any children.
     */
    boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * Returns true if this node is empty (no value and no children).
     */
    boolean isEmpty() {
        return !hasValue() && !hasChildren();
    }
}
