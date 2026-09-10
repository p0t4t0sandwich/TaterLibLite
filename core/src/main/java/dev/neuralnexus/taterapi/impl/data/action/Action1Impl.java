/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.impl.data.action;

import dev.neuralnexus.taterapi.Result;
import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.data.action.Action1;
import dev.neuralnexus.taterapi.data.action.KeyedAction1;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

@ApiStatus.Internal
public final class Action1Impl<R, A> extends AbstractActionImpl<R, Action1<R, A>>
        implements KeyedAction1<R, A> {
    private final Action1<R, A> delegate;

    public Action1Impl(
            final @NonNull Key<? extends Action1<R, A>> key,
            final @NonNull Action1<R, A> delegate) {
        super(key);
        this.delegate = delegate;
    }

    @Override
    public Result<R> apply(final A a) {
        return this.delegate.apply(a);
    }
}
