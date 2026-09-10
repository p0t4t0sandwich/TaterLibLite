/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.impl.data.action;

import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.data.action.Action;
import dev.neuralnexus.taterapi.data.action.Action1;
import dev.neuralnexus.taterapi.data.action.Action2;
import dev.neuralnexus.taterapi.data.action.Action3;
import dev.neuralnexus.taterapi.data.action.Action4;
import dev.neuralnexus.taterapi.data.action.ActionFactory;
import dev.neuralnexus.taterapi.data.action.KeyedAction;
import dev.neuralnexus.taterapi.data.action.KeyedAction1;
import dev.neuralnexus.taterapi.data.action.KeyedAction2;
import dev.neuralnexus.taterapi.data.action.KeyedAction3;
import dev.neuralnexus.taterapi.data.action.KeyedAction4;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

@ApiStatus.Internal
public class ActionFactoryImpl implements ActionFactory {
    @Override
    public <R> KeyedAction<R> of(
            final @NonNull Key<? extends Action<R>> key, final @NonNull Action<R> delegate) {
        return new ActionImpl<>(key, delegate);
    }

    @Override
    public <R, A> KeyedAction1<R, A> of(
            final @NonNull Key<? extends Action1<R, A>> key,
            final @NonNull Action1<R, A> delegate) {
        return new Action1Impl<>(key, delegate);
    }

    @Override
    public <R, A, B> KeyedAction2<R, A, B> of(
            final @NonNull Key<? extends Action2<R, A, B>> key,
            final @NonNull Action2<R, A, B> delegate) {
        return new Action2Impl<>(key, delegate);
    }

    @Override
    public <R, A, B, C> KeyedAction3<R, A, B, C> of(
            final @NonNull Key<? extends Action3<R, A, B, C>> key,
            final @NonNull Action3<R, A, B, C> delegate) {
        return new Action3Impl<>(key, delegate);
    }

    @Override
    public <R, A, B, C, D> KeyedAction4<R, A, B, C, D> of(
            final @NonNull Key<? extends Action4<R, A, B, C, D>> key,
            final @NonNull Action4<R, A, B, C, D> delegate) {
        return new Action4Impl<>(key, delegate);
    }
}
