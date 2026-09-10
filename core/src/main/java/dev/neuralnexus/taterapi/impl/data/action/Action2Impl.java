/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.impl.data.action;

import dev.neuralnexus.taterapi.Result;
import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.data.action.Action2;
import dev.neuralnexus.taterapi.data.action.KeyedAction2;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

@ApiStatus.Internal
public final class Action2Impl<R, A, B> extends AbstractActionImpl<R, Action2<R, A, B>>
        implements KeyedAction2<R, A, B> {
    private final Action2<R, A, B> delegate;

    public Action2Impl(
            final @NonNull Key<? extends Action2<R, A, B>> key,
            final @NonNull Action2<R, A, B> delegate) {
        super(key);
        this.delegate = delegate;
    }

    @Override
    public Result<R> apply(final A a, final B b) {
        return this.delegate.apply(a, b);
    }
}
