/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.registries;

import dev.neuralnexus.taterapi.util.Builder;

import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class BuilderRegistry {
    private static final Map<Class<?>, Supplier<?>> builders = new HashMap<>();

    /**
     * Register a builder
     *
     * @param clazz The class
     * @param builder The builder
     */
    public static <T> void register(
            final @NonNull Class<T> clazz, final @NonNull Supplier<T> builder) {
        builders.put(clazz, builder);
    }

    /**
     * Get a builder
     *
     * @param clazz The class
     * @return The builder
     */
    @SuppressWarnings("unchecked")
    public static <T, B extends Builder<T, B>> B get(final @NonNull Class<B> clazz) {
        final Supplier<?> builder = builders.get(clazz);
        if (builder == null) {
            throw new IllegalStateException(
                    "Builder not found for " + clazz.getName() + ". Perhaps it wasn't registered?");
        }
        return (B) builder.get();
    }
}
