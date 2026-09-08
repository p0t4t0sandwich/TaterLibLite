/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import dev.neuralnexus.taterapi.Wrapped;
import dev.neuralnexus.taterapi.data.value.Value;
import dev.neuralnexus.taterapi.registries.DataRegistry;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@SuppressWarnings("unchecked")
@ApiStatus.Internal
public interface DataHolder {
    Cache<Object, Map<Key<?>, Value<?>>> INSTANCE_STORES =
            CacheBuilder.newBuilder().weakKeys().build();

    /**
     * Get a value from this holder. Returns {@link Optional#empty()} if the key is not registered
     * to this holder.
     *
     * @param key The key
     * @return The value
     * @param <V> The value's type
     * @param <E> The value's inner type
     */
    default <V extends Value<E>, E> Optional<V> get(final @NonNull Key<V> key) {
        Objects.requireNonNull(key, "key");
        final Map<Key<?>, Value<?>> store;
        try {
            store = INSTANCE_STORES.get(this, ConcurrentHashMap::new);
        } catch (final ExecutionException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(
                (V) store.computeIfAbsent(key, _ -> {
                    if (this instanceof Wrapped<?> wrapped) {
                        return DataRegistry.query(key, wrapped.unwrap());
                    } else {
                        return DataRegistry.query(key, this);
                    }
                }));
    }

    /**
     * Get a value from this holder. Throws if the key is not registered to this holder.
     *
     * @param key The key
     * @return The value
     * @param <V> The value's type
     * @param <E> The value's inner type
     */
    default <V extends Value<E>, E> @NonNull V getOrThrow(final @NonNull Key<V> key) {
        return this.get(key).orElseThrow();
    }

    /**
     * Get a value from this holder. Returns null if the value is not registered to this holder.
     *
     * @param key The key
     * @return The value
     * @param <V> The value's type
     * @param <E> The value's inner type
     */
    default <V extends Value<E>, E> @Nullable V getOrNull(final @NonNull Key<V> key) {
        return this.get(key).orElse(null);
    }

    /**
     * Offer a value to this holder. Returns {@link Optional#empty()} if the key is not registered
     * to this holder. Throws if the value is not mutable.
     *
     * @param key The key
     * @param value The value's new backing value
     * @param <V> The value's type
     * @param <E> The value's inner type
     * @return The value
     */
    default <V extends Value<E>, E> Optional<V> offer(final @NonNull Key<V> key, final E value) {
        return this.get(Objects.requireNonNull(key, "key"))
                .map(
                        v -> {
                            if (!v.isMutable()) {
                                throw new IllegalStateException("This value is immutable");
                            }
                            return (V) v.set(value);
                        });
    }

    /**
     * Offer a value to this holder. Returns {@link Optional#empty()} if the key is not registered
     * to this holder. Throws if the value is not mutable. Throws if the value is not registered.
     *
     * @param key The key
     * @param value The value's new backing value
     * @param <V> The value's type
     * @param <E> The value's inner type
     * @return The value
     */
    default <V extends Value<E>, E> @NonNull V offerOrThrow(
            final @NonNull Key<V> key, final E value) {
        return this.offer(key, value).orElseThrow();
    }

    /**
     * Offer a value to this holder. Returns {@link Optional#empty()} if the key is not registered
     * to this holder. Throws if the value is not mutable. Returns null if the value is not
     * registered.
     *
     * @param key The key
     * @param value The value's new backing value
     * @param <V> The value's type
     * @param <E> The value's inner type
     * @return The value
     */
    default <V extends Value<E>, E> @Nullable V offerOrNull(
            final @NonNull Key<V> key, final E value) {
        return this.offer(key, value).orElse(null);
    }

    /**
     * @param key The key
     * @param function The function to use when transforming the backing value
     * @return The value
     * @param <V> The value's type
     * @param <E> The value's inner type
     */
    default <V extends Value<E>, E> Optional<V> transform(
            final @NonNull Key<V> key, final @NonNull Function<E, E> function) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(function, "function");
        return this.get(key).map(Value::get).map(function).flatMap(value -> this.offer(key, value));
    }
}
