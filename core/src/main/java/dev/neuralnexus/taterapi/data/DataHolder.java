/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import dev.neuralnexus.taterapi.Result;
import dev.neuralnexus.taterapi.Wrapped;
import dev.neuralnexus.taterapi.data.value.Value;
import dev.neuralnexus.taterapi.registries.DataRegistry;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
@ApiStatus.Internal
public interface DataHolder {
    Cache<Object, Map<Key<?>, Value<?>>> INSTANCE_STORES =
            CacheBuilder.newBuilder().weakKeys().build();

    /**
     * Get a value from this holder. Returns {@link Result#error()} if the key is not registered to
     * this holder.
     *
     * @param key The key
     * @return The value
     * @param <V> The value's type
     * @param <E> The value's inner type
     */
    default <V extends Value<E>, E> Result<V> get(final @NonNull Key<V> key) {
        Objects.requireNonNull(key, "key");
        final Map<Key<?>, Value<?>> store;
        try {
            store = INSTANCE_STORES.get(this, ConcurrentHashMap::new);
        } catch (final ExecutionException e) {
            return Result.error("An issue occurred accessing the holder instance cache", e);
        }
        try {
            return Result.success(
                    (V)
                            store.computeIfAbsent(
                                    key,
                                    _ -> {
                                        if (this instanceof Wrapped<?> wrapped) {
                                            return DataRegistry.query(key, wrapped.unwrap());
                                        } else {
                                            return DataRegistry.query(key, this);
                                        }
                                    }));
        } catch (final RuntimeException e) {
            return Result.error(
                    "An exception occurred querying a value for Key " + key.asString(), e);
        }
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
        return this.get(key).result().orElseThrow(() -> new MissingKeyException(key, this));
    }

    /**
     * Get a value from this holder. Throws the supplied exception if the key is not registered to
     * this holder.
     *
     * @param key The key
     * @param exception The exception to throw
     * @return The value
     * @param <V> The value's type
     * @param <E> The value's inner type
     */
    default <V extends Value<E>, E> @NonNull V getOrThrow(
            final @NonNull Key<V> key,
            final @NonNull Supplier<? extends RuntimeException> exception) {
        return this.get(key).result().orElseThrow(exception);
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
        return this.get(key).result().orElse(null);
    }

    /**
     * Offer a value to this holder. Returns {@link Result#error()} if the key is not registered to
     * this holder. Throws if the value is not mutable.
     *
     * @param key The key
     * @param value The value's new backing value
     * @param <V> The value's type
     * @param <E> The value's inner type
     * @return The value
     */
    default <V extends Value<E>, E> Result<V> offer(final @NonNull Key<V> key, final E value) {
        Objects.requireNonNull(key, "key");
        final Result<V> r = this.get(key);
        if (r.isError()) return r;
        if (r.result().isEmpty()) return r;
        final V val = r.unwrap();
        if (!val.isMutable())
            return Result.error(
                    "This value is immutable",
                    new IllegalStateException("This value is immutable"));
        return Result.success((V) val.set(value));
    }

    /**
     * Offer a value to this holder. Throws if the key is not registered to this holder. Throws if
     * the value is not mutable.
     *
     * @param key The key
     * @param value The value's new backing value
     * @param <V> The value's type
     * @param <E> The value's inner type
     * @return The value
     */
    default <V extends Value<E>, E> @NonNull V offerOrThrow(
            final @NonNull Key<V> key, final E value) {
        return this.offer(key, value).result().orElseThrow();
    }

    /**
     * Offer a value to this holder. Returns null if the value is not registered to this holder.
     * Throws if the value is not mutable.
     *
     * @param key The key
     * @param value The value's new backing value
     * @param <V> The value's type
     * @param <E> The value's inner type
     * @return The value
     */
    default <V extends Value<E>, E> @Nullable V offerOrNull(
            final @NonNull Key<V> key, final E value) {
        return this.offer(key, value).result().orElse(null);
    }

    /**
     * @param key The key
     * @param function The function to use when transforming the backing value
     * @return The value
     * @param <V> The value's type
     * @param <E> The value's inner type
     */
    default <V extends Value<E>, E> Result<V> transform(
            final @NonNull Key<V> key, final @NonNull Function<E, E> function) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(function, "function");
        Objects.requireNonNull(key, "key");
        final Result<V> r = this.get(key);
        if (r.isError()) return r;
        if (r.result().isEmpty()) return r;
        //noinspection OptionalGetWithoutIsPresent
        return r.result().map(Value::get).map(function).map(value -> this.offer(key, value)).get();
    }
}
