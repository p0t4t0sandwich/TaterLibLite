/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.registries;

import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class FactoryRegistry {
    private static final Map<Class<?>, Supplier<?>> factories = new HashMap<>();

    /**
     * Register a factory
     *
     * @param clazz The class
     * @param factory The factory
     */
    public static <T> void register(
            final @NonNull Class<T> clazz, final @NonNull Supplier<T> factory) {
        factories.put(clazz, factory);
    }

    /**
     * Get a factory
     *
     * @param clazz The class
     * @return The factory
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(final @NonNull Class<T> clazz) {
        final Supplier<?> factory = factories.get(clazz);
        if (factory == null) {
            throw new IllegalStateException(
                    "Factory not found for " + clazz.getName() + ". Perhaps it wasn't registered?");
        }
        return (T) factory.get();
    }
}
