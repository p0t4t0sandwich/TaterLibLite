/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class WrapperRegistry {
    private final Map<Class<?>, Function<Object, ?>> WRAPPERS = new HashMap<>();

    public <T, W> void register(Class<T> clazz, Function<T, W> wrapper) {
        this.WRAPPERS.put(clazz, (Function<Object, Wrapped<?>>) wrapper);
    }

    public <T, R> R wrap(T object) {
        return (R) this.WRAPPERS.get(object.getClass()).apply(object);
    }
}
