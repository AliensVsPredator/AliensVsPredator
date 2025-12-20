package com.blib.common.event;

public interface BLibEventListenerHandle<Dispatcher> {

    void register(Dispatcher dispatcher);

}
