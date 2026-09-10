/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data.action;

import dev.neuralnexus.taterapi.data.Key;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

@ApiStatus.Internal
public interface KeyedAction4<R, A, B, C, D> extends Action4<R, A, B, C, D> {
    @NonNull Key<? extends Action4<R, A, B, C, D>> key();
}
