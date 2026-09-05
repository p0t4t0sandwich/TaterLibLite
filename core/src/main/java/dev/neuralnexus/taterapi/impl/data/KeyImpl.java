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
public class KeyImpl<V extends Value<E>, E> implements Key<V> {
    private final Identifier id;
    private final Class<V> type;

    public KeyImpl(final @NonNull Identifier id, final @NonNull Class<V> type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public @NonNull String namespace() {
        return this.id.namespace();
    }

    @Override
    public @NonNull String path() {
        return this.id.path();
    }

    @Override
    public @NonNull Class<V> type() {
        return this.type;
    }
}
