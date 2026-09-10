/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.impl.data.action;

import dev.neuralnexus.taterapi.Result;
import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.data.action.Action4;
import dev.neuralnexus.taterapi.data.action.KeyedAction4;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

@ApiStatus.Internal
public final class Action4Impl<R, A, B, C, D> extends AbstractActionImpl<R, Action4<R, A, B, C, D>>
        implements KeyedAction4<R, A, B, C, D> {
    private final Action4<R, A, B, C, D> delegate;

    public Action4Impl(
            final @NonNull Key<? extends Action4<R, A, B, C, D>> key,
            final @NonNull Action4<R, A, B, C, D> delegate) {
        super(key);
        this.delegate = delegate;
    }

    @Override
    public Result<R> apply(final A a, final B b, final C c, final D d) {
        return this.delegate.apply(a, b, c, d);
    }
}
