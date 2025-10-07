package com.cobblemon.mod.common.api.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class EventHook<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();

    public void subscribe(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void post(T event) {
        for (Consumer<T> listener : listeners) {
            listener.accept(event);
        }
    }
}
