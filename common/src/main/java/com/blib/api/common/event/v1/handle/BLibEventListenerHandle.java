package com.blib.api.common.event.v1.handle;

public interface BLibEventListenerHandle<Dispatcher> {

    void register(Dispatcher dispatcher);

}
