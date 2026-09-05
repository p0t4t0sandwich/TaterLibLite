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
public class ValueImpl<E> implements Value<E> {
    private final KeyImpl<? extends Value<E>, E> key;
    private final Supplier<E> GET;
    private final Consumer<E> SET;
    private final boolean mutable;

    public ValueImpl(
            final @NonNull Key<? extends Value<E>> key,
            final @NonNull Supplier<E> GET,
            final @NonNull Consumer<E> SET,
            final boolean mutable) {
        this.key = (KeyImpl<? extends Value<E>, E>) key;
        this.GET = GET;
        this.SET = SET;
        this.mutable = mutable;
    }

    @Override
    public E get() {
        return this.GET.get();
    }

    @Override
    public Value<E> set(final E value) {
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
    public Key<? extends Value<E>> key() {
        return this.key;
    }
}
