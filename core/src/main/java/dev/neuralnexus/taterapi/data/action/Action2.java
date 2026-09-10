/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data.action;

import dev.neuralnexus.taterapi.Result;
import dev.neuralnexus.taterapi.data.Element;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@FunctionalInterface
public interface Action2<R, A, B> extends Element<R> {
    Result<R> apply(A a, B b);
}
