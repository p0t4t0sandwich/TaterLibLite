/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.impl.data.action;

import dev.neuralnexus.taterapi.data.Element;
import dev.neuralnexus.taterapi.data.Key;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

@ApiStatus.Internal
abstract class AbstractActionImpl<R, K extends Element<R>> {
    protected final Key<? extends K> key;

    protected AbstractActionImpl(final @NonNull Key<? extends K> key) {
        this.key = key;
    }

    public @NonNull Key<? extends K> key() {
        return this.key;
    }
}
