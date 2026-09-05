/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.impl.data;

import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.data.value.Value;
import dev.neuralnexus.taterapi.resources.Identifier;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

@ApiStatus.Internal
public class KeyBuilderImpl<E, V extends Value<E>> implements Key.Builder<E, V> {
    private Identifier identifier;
    private Class<V> type;

    @SuppressWarnings("unchecked")
    @Override
    public <T, B extends Value<T>> Key.Builder<T, B> type(@NonNull Class<T> type) {
        this.type = (Class<V>) type;
        return (Key.Builder<T, B>) this;
    }

    @Override
    public Key.Builder<E, V> key(@NonNull Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public @NonNull Key<V> build() {
        return new KeyImpl<>(this.identifier, this.type);
    }
}
