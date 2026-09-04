/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data;

import dev.neuralnexus.taterapi.data.value.Value;
import dev.neuralnexus.taterapi.impl.data.DataHolderImpl;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@ApiStatus.Internal
public interface DataHolder {

    @SafeVarargs
    static <I, T> DataHolder create(final @NonNull T objRef, final @NonNull Class<I>... ifaces) {
        return new DataHolderImpl(
                Objects.requireNonNull(objRef, "objRef"), Objects.requireNonNull(ifaces, "ifaces"));
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

    /**
     * Get the value for a key
     *
     * @return The value, empty if unsupported
     */
    <E> Optional<Value<E>> value(final @NonNull Key<? extends Value<E>> key);
}
