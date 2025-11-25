package com.blib.service;

import com.blib.event.key.BLibEventKey;

import java.util.function.Consumer;

public interface BLibEventService {

    <T> void addListener(BLibEventKey<T> key, Consumer<T> consumer);
}
