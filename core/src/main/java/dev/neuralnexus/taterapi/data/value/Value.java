/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data.value;

import dev.neuralnexus.taterapi.data.Element;
import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.registries.DataRegistry;
import dev.neuralnexus.taterapi.registries.FactoryRegistry;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public interface Value<E> extends Element<E> {

    /**
     * Get the underlying value
     *
     * @return The underlying value
     */
    E get();

    /**
     * IF mutable, set the underlying value. If the value is NOT mutable, nothing will happen.
     *
     * @return The underlying value
     */
    Value<E> set(final E newValue);

    /**
     * Whether the value is mutable
     *
     * @return True if the value is mutable
     */
    boolean isMutable();

    /**
     * The key for this value
     *
     * @return The key
     */
    Key<? extends Value<E>> key();

    static <V extends Value<T>, T> V mutableOf(
            final @NonNull Key<V> key,
            final @NonNull Supplier<T> GET,
            final @NonNull Consumer<T> SET) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(GET, "GET");
        Objects.requireNonNull(SET, "SET");
        return FactoryRegistry.get(Factory.class).mutableOf(key, GET, SET);
    }

    static <V extends Value<T>, T> V immutableOf(
            final @NonNull Key<V> key, final @NonNull Supplier<T> GET) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(GET, "GET");
        return FactoryRegistry.get(Factory.class).immutableOf(key, GET);
    }

    static <V extends Value<T>, T, B> DataRegistry.Initializer<B, T> mutableOf(
            final @NonNull Key<V> key,
            final @NonNull Getter<B, T> GET,
            final @NonNull Setter<B, T> SET) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(GET, "GET");
        Objects.requireNonNull(SET, "SET");
        return (objRef) -> mutableOf(key, GET.init(objRef), SET.init(objRef));
    }

    static <V extends Value<T>, T, B> DataRegistry.Initializer<B, T> immutableOf(
            final @NonNull Key<V> key, final @NonNull Getter<B, T> GET) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(GET, "GET");
        return (objRef) -> immutableOf(key, GET.init(objRef));
    }

    interface Factory {
        <V extends Value<E>, E> V mutableOf(
                final @NonNull Key<V> key,
                final @NonNull Supplier<E> GET,
                final @NonNull Consumer<E> SET);

        <V extends Value<E>, E> V immutableOf(
                final @NonNull Key<V> key, final @NonNull Supplier<E> GET);
    }

    @FunctionalInterface
    interface Getter<B, E> {
        Supplier<E> init(B objRef);
    }

    @FunctionalInterface
    interface Setter<B, E> {
        Consumer<E> init(B objRef);
    }
}
