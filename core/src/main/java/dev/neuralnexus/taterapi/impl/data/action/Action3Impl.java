/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.impl.data.action;

import dev.neuralnexus.taterapi.Result;
import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.data.action.Action3;
import dev.neuralnexus.taterapi.data.action.KeyedAction3;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

@ApiStatus.Internal
public final class Action3Impl<R, A, B, C> extends AbstractActionImpl<R, Action3<R, A, B, C>>
        implements KeyedAction3<R, A, B, C> {
    private final Action3<R, A, B, C> delegate;

    public Action3Impl(
            final @NonNull Key<? extends Action3<R, A, B, C>> key,
            final @NonNull Action3<R, A, B, C> delegate) {
        super(key);
        this.delegate = delegate;
    }

    @Override
    public Result<R> apply(final A a, final B b, final C c) {
        return this.delegate.apply(a, b, c);
    }
}
