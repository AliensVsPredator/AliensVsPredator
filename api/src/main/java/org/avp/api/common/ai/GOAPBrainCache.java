package org.avp.api.common.ai;

import org.avp.api.common.ai.sensor.key.SensorKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GOAPBrainCache {

    private final Map<SensorKey<?>, Object> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(SensorKey<T> sensorKey) {
        return Optional.ofNullable((T) cache.get(sensorKey));
    }

    public <T> void cache(SensorKey<T> sensorKey, T value) {
        cache.put(sensorKey, value);
    }

    public void clear() {
        cache.clear();
    }

    public void remove(SensorKey<?> sensorKey) {
        cache.remove(sensorKey);
    }
}
