package com.blib.api.common.config.v1;

import org.jetbrains.annotations.Nullable;

import com.blib.api.common.config.v1.serializer.PropertySerializer;

/**
 * A hierarchical key for accessing properties in a PropertyConfig. Keys form a tree structure where Parent nodes can
 * have children and Leaf nodes hold typed values.
 */
public sealed interface PropertyKey permits PropertyKey.Parent, PropertyKey.Leaf {

    /**
     * Returns the single name segment of this key (not the full path).
     */
    String name();

    /**
     * Returns the parent key, or null if this is a root key.
     */
    @Nullable
    Parent parent();

    /**
     * Returns the full dot-separated path from root to this key.
     */
    default String path() {
        var parent = parent();

        if (parent == null) {
            return name();
        }

        return parent.path() + "." + name();
    }

    /**
     * Creates a root parent key with the given name.
     *
     * @param name The name of this key segment (must not contain dots)
     * @return A new root Parent key
     * @throws IllegalArgumentException if name contains dots
     */
    static Parent parent(String name) {
        validateName(name);
        return new Parent(name, null);
    }

    /**
     * Validates that a key name does not contain dots.
     */
    private static void validateName(String name) {
        if (name.contains(".")) {
            throw new IllegalArgumentException("Property key name cannot contain dots: " + name);
        }
    }

    /**
     * A parent key that can have child keys. Parent keys do not hold values directly.
     */
    final class Parent implements PropertyKey {

        private final String name;

        private final @Nullable Parent parent;

        Parent(String name, @Nullable Parent parent) {
            this.name = name;
            this.parent = parent;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public @Nullable Parent parent() {
            return parent;
        }

        /**
         * Creates a child parent key under this key.
         *
         * @param name The name of the child key segment (must not contain dots)
         * @return A new child Parent key
         * @throws IllegalArgumentException if name contains dots
         */
        public Parent child(String name) {
            validateName(name);
            return new Parent(name, this);
        }

        /**
         * Creates a leaf key under this key with the given serializer.
         *
         * @param name       The name of the leaf key segment (must not contain dots)
         * @param serializer The serializer for this leaf's value type
         * @param <T>        The value type
         * @return A new Leaf key
         * @throws IllegalArgumentException if name contains dots
         */
        public <T> Leaf<T> leaf(String name, PropertySerializer<T> serializer) {
            validateName(name);
            return new Leaf<>(name, this, serializer);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof Parent other)) {
                return false;
            }

            return path().equals(other.path());
        }

        @Override
        public int hashCode() {
            return path().hashCode();
        }

        @Override
        public String toString() {
            return "PropertyKey.Parent[" + path() + "]";
        }
    }

    /**
     * A leaf key that holds a typed value. Leaf keys cannot have children.
     *
     * @param <T> The type of value this key represents
     */
    final class Leaf<T> implements PropertyKey {

        private final String name;

        private final @Nullable Parent parent;

        private final PropertySerializer<T> serializer;

        Leaf(String name, @Nullable Parent parent, PropertySerializer<T> serializer) {
            this.name = name;
            this.parent = parent;
            this.serializer = serializer;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public @Nullable Parent parent() {
            return parent;
        }

        /**
         * Returns the serializer for this leaf's value type.
         */
        public PropertySerializer<T> serializer() {
            return serializer;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof Leaf<?> other)) {
                return false;
            }

            return path().equals(other.path());
        }

        @Override
        public int hashCode() {
            return path().hashCode();
        }

        @Override
        public String toString() {
            return "PropertyKey.Leaf[" + path() + "]";
        }
    }

    /**
     * Creates a root leaf key with no parent. Useful for single-level properties.
     *
     * @param name       The name of this key (must not contain dots)
     * @param serializer The serializer for this leaf's value type
     * @param <T>        The value type
     * @return A new root Leaf key
     * @throws IllegalArgumentException if name contains dots
     */
    static <T> Leaf<T> leaf(String name, PropertySerializer<T> serializer) {
        validateName(name);
        return new Leaf<>(name, null, serializer);
    }
}
