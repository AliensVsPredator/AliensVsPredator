package com.blib.internal.client.animation.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import com.blib.internal.client.animation.primitive.AzQueuedAnimation;

public class AzAnimationQueue {

    private final Queue<AzQueuedAnimation> animationQueue;

    public AzAnimationQueue() {
        this.animationQueue = new LinkedList<>();
    }

    public void add(@NotNull AzQueuedAnimation queuedAnimation) {
        animationQueue.add(queuedAnimation);
    }

    public void addAll(@NotNull Collection<AzQueuedAnimation> queuedAnimations) {
        animationQueue.addAll(queuedAnimations);
    }

    public @Nullable AzQueuedAnimation peek() {
        return animationQueue.peek();
    }

    public @Nullable AzQueuedAnimation next() {
        return animationQueue.poll();
    }

    public void clear() {
        animationQueue.clear();
    }

    public boolean isEmpty() {
        return animationQueue.isEmpty();
    }
}
