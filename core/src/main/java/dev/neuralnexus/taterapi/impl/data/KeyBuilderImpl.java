/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.impl.data;

import dev.neuralnexus.taterapi.data.Element;
import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.resources.Identifier;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

@ApiStatus.Internal
public class KeyBuilderImpl<T, E extends Element<T>> implements Key.Builder<T, E> {
    private Identifier identifier;
    private Class<E> type;

    @SuppressWarnings("unchecked")
    @Override
    public <A, B extends Element<A>> Key.Builder<A, B> type(@NonNull Class<B> type) {
        this.type = (Class<E>) type;
        return (Key.Builder<A, B>) this;
    }

    @Override
    public Key.Builder<T, E> key(final @NonNull Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public @NonNull Key<E> build() {
        Objects.requireNonNull(this.identifier, "identifier");
        Objects.requireNonNull(this.type, "type");
        return new KeyImpl<>(this.identifier, this.type);
    }
}
