package com.blib.common.event.handle;

public interface BLibEventListenerHandle<Dispatcher> {

    void register(Dispatcher dispatcher);

}
