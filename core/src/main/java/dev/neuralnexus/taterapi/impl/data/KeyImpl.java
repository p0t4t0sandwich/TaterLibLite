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

@ApiStatus.Internal
public class KeyImpl<T, E extends Element<T>> implements Key<E> {
    private final Identifier id;
    private final Class<E> type;

    public KeyImpl(final @NonNull Identifier id, final @NonNull Class<E> type) {
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
    public @NonNull Class<E> type() {
        return this.type;
    }
}
