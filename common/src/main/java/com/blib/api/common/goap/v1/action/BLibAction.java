package com.blib.api.common.goap.v1.action;

import com.just.ai.goap.action.Action;
import com.just.ai.goap.action.DelegatingAction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BLibAction<T> extends DelegatingAction<T> {

    public static <T> ConcreteBuilder<T> builder(String name) {
        return new ConcreteBuilder<>(name);
    }

    private final Set<ActionMask> masks;

    protected BLibAction(Action<T> delegate, Set<ActionMask> masks) {
        super(delegate);
        this.masks = masks;
    }

    public Set<ActionMask> getMasks() {
        return masks;
    }

    public abstract static class Builder<T, B extends Builder<T, B>> extends DelegatingAction.Builder<T, B> {

        protected final Set<ActionMask> masks;

        protected Builder(String name) {
            super(name);
            this.masks = new HashSet<>();
        }

        public B addMask(ActionMask mask) {
            masks.add(mask);
            return self();
        }

        public final B addMasks(ActionMask actionMask, ActionMask... masks) {
            addMask(actionMask);
            Collections.addAll(this.masks, masks);
            return self();
        }

        @Override
        protected BLibAction<T> build(Action<T> delegate) {
            return new BLibAction<>(delegate, masks);
        }

        @Override
        public BLibAction<T> build() {
            return (BLibAction<T>) super.build();
        }
    }

    public static class ConcreteBuilder<T> extends Builder<T, ConcreteBuilder<T>> {

        protected ConcreteBuilder(String name) {
            super(name);
        }

        @Override
        protected ConcreteBuilder<T> self() {
            return this;
        }
    }
}
