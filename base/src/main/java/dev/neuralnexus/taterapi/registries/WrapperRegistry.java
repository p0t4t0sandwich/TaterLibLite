/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.registries;

import dev.neuralnexus.taterapi.Wrapped;
import dev.neuralnexus.taterapi.serialization.Encoder;

import java.util.HashMap;
import java.util.Map;

public final class WrapperRegistry {
    private final Map<Class<?>, Encoder<Object, ? extends Wrapped<?>>> WRAPPERS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T, W> void register(Class<T> clazz, Encoder<T, W> wrapper) {
        this.WRAPPERS.put(clazz, (Encoder<Object, ? extends Wrapped<?>>) wrapper);
    }

    @SuppressWarnings("unchecked")
    public <T, R> R wrap(T object) {
        return (R) this.WRAPPERS.get(object.getClass()).encode(object).unwrap();
    }
}
