/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data;

import dev.neuralnexus.taterapi.data.value.Value;

import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface DataHolder {
    /**
     * Offer a value to this holder
     *
     * @param key The key
     * @param value The value
     * @param <E> The value's type
     */
    <E> Optional<E> offer(final @NonNull Key<? extends Value<E>> key, final E value);

    /**
     * Get a value from this holder. Returns {@link Optional#empty()} if the key is not registered
     * to this holder.
     *
     * @param key The key
     * @return The value
     * @param <E> The value's type
     */
    <E> Optional<E> get(final @NonNull Key<? extends Value<E>> key);

    @SuppressWarnings("unchecked")
    default <E> Optional<E> transform(Key<? extends Value<E>> key, Function<E, E> function) {
        if (this.isRegistered(key)) {
            return (Optional<E>) this.get(key).map(function).map(value -> this.offer(key, value));
        }
        return Optional.empty();
    }

    /**
     * Get the set of keys supported by this holder
     *
     * @return The set of keys
     */
    Set<Key<?>> getKeys();

    /**
     * Checks if they key is supported by this holder
     *
     * @param key The key
     * @return Whether the key is supported
     */
    default boolean isRegistered(Key<?> key) {
        for (Key<?> k : this.getKeys()) {
            if (k == key) {
                return true;
            }
        }
        return false;
    }
}
