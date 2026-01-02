/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.adapter;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AdapterDecoder<I, T> {
    @NotNull T from(final @NotNull I object);
}
