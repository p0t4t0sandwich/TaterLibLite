/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.impl.data.value;

import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.data.value.Value;
import dev.neuralnexus.taterapi.impl.data.KeyImpl;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public class ValueImpl<T> implements Value<T> {
    private final KeyImpl<T, ? extends Value<T>> key;
    private final Supplier<T> GET;
    private final Consumer<T> SET;
    private final boolean mutable;

    public ValueImpl(
            final @NonNull Key<? extends Value<T>> key,
            final @NonNull Supplier<T> GET,
            final @NonNull Consumer<T> SET,
            final boolean mutable) {
        this.key = (KeyImpl<T, ? extends Value<T>>) key;
        this.GET = GET;
        this.SET = SET;
        this.mutable = mutable;
    }

    @Override
    public T get() {
        return this.GET.get();
    }

    @Override
    public Value<T> set(final T value) {
        if (this.isMutable()) {
            this.SET.accept(value);
        }
        return this;
    }

    @Override
    public boolean isMutable() {
        return this.mutable;
    }

    @Override
    public Key<? extends Value<T>> key() {
        return this.key;
    }
}
