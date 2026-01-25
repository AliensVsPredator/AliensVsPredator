package com.blib.api.common.property.v1;

import org.jetbrains.annotations.Nullable;

import com.blib.api.common.property.v1.serializer.BLibPropertySerializer;

public sealed interface BLibPropertyKey {

    private static String computePath(BLibPropertyKey propertyKey) {
        var parent = propertyKey.parentOrNull();

        if (parent == null) {
            return propertyKey.name();
        }

        return parent.path() + "." + propertyKey.name();
    }

    private static void validateName(String name) {
        if (name.contains(".")) {
            throw new IllegalArgumentException("Property key name cannot contain dots: " + name);
        }
    }

    static Parent parent(String name) {
        validateName(name);
        return new Parent(name, null);
    }

    static <T> Leaf<T> leaf(String name, BLibPropertySerializer<T> serializer) {
        validateName(name);
        return new Leaf<>(name, null, serializer);
    }

    String name();

    @Nullable
    Parent parentOrNull();

    String path();

    final class Parent implements BLibPropertyKey {

        private final String name;

        private final @Nullable Parent parent;

        private final String path;

        Parent(String name, @Nullable Parent parent) {
            this.name = name;
            this.parent = parent;
            this.path = computePath(this);
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public @Nullable Parent parentOrNull() {
            return parent;
        }

        @Override
        public String path() {
            return path;
        }

        public Parent child(String name) {
            validateName(name);
            return new Parent(name, this);
        }

        public <T> Leaf<T> leaf(String name, BLibPropertySerializer<T> serializer) {
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

    final class Leaf<T> implements BLibPropertyKey {

        private final String name;

        private final @Nullable Parent parentOrNull;

        private final String path;

        private final BLibPropertySerializer<T> serializer;

        Leaf(String name, @Nullable Parent parentOrNull, BLibPropertySerializer<T> serializer) {
            this.name = name;
            this.parentOrNull = parentOrNull;
            this.serializer = serializer;
            this.path = BLibPropertyKey.computePath(this);
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public @Nullable Parent parentOrNull() {
            return parentOrNull;
        }

        @Override
        public String path() {
            return path;
        }

        public BLibPropertySerializer<T> serializer() {
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
}
