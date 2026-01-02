/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.codec;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface StreamMemberEncoder<O, T> {
    void encode(final @NotNull T value, final @NotNull O output);
}
