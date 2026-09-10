/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.impl.data.action;

import dev.neuralnexus.taterapi.Result;
import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.data.action.Action;
import dev.neuralnexus.taterapi.data.action.KeyedAction;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

@ApiStatus.Internal
public final class ActionImpl<R> extends AbstractActionImpl<R, Action<R>>
        implements KeyedAction<R> {
    private final Action<R> delegate;

    public ActionImpl(
            final @NonNull Key<? extends Action<R>> key, final @NonNull Action<R> delegate) {
        super(key);
        this.delegate = delegate;
    }

    @Override
    public Result<R> apply() {
        return this.delegate.apply();
    }
}
