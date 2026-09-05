/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import dev.neuralnexus.taterapi.data.value.Value;
import dev.neuralnexus.taterapi.registries.DataRegistry;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@ApiStatus.Internal
public interface DataHolder {
    Cache<Object, Map<Key<?>, Value<?>>> INSTANCE_STORES =
            CacheBuilder.newBuilder().weakKeys().build();

    /**
     * Get the value for a key
     *
     * @return The value, empty if unsupported
     */
    @SuppressWarnings("unchecked")
    default <E> Optional<Value<E>> value(final @NonNull Key<? extends Value<E>> key) {
        Objects.requireNonNull(key, "key");
        final Map<Key<?>, Value<?>> store;
        try {
            store = INSTANCE_STORES.get(this, ConcurrentHashMap::new);
        } catch (final ExecutionException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(
                (Value<E>) store.computeIfAbsent(key, _ -> DataRegistry.query(key, this)));
    }

    /**
     * Offer a value to this holder
     *
     * @param key The key
     * @param value The value
     * @param <E> The value's type
     */
    default <E> Optional<E> offer(final @NonNull Key<? extends Value<E>> key, final E value) {
        return this.value(Objects.requireNonNull(key, "key"))
                .map(
                        v -> {
                            if (v.isMutable()) {
                                v.set(value);
                            }
                            return v.get();
                        });
    }

    /**
     * Get a value from this holder. Returns {@link Optional#empty()} if the key is not registered
     * to this holder.
     *
     * @param key The key
     * @return The value
     * @param <E> The value's type
     */
    default <E> Optional<E> get(final @NonNull Key<? extends Value<E>> key) {
        return this.value(Objects.requireNonNull(key, "key")).map(Value::get);
    }

    @SuppressWarnings("unchecked")
    default <E> Optional<E> transform(
            final @NonNull Key<? extends Value<E>> key, final @NonNull Function<E, E> function) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(function, "function");
        return (Optional<E>) this.get(key).map(function).map(value -> this.offer(key, value));
    }
}
