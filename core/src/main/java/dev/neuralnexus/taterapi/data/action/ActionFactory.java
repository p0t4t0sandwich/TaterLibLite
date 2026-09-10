/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data.action;

import dev.neuralnexus.taterapi.data.Key;

import org.jspecify.annotations.NonNull;

public interface ActionFactory {
    <R> KeyedAction<R> of(
            final @NonNull Key<? extends Action<R>> key, final @NonNull Action<R> delegate);

    <R, A> KeyedAction1<R, A> of(
            final @NonNull Key<? extends Action1<R, A>> key, final @NonNull Action1<R, A> delegate);

    <R, A, B> KeyedAction2<R, A, B> of(
            final @NonNull Key<? extends Action2<R, A, B>> key,
            final @NonNull Action2<R, A, B> delegate);

    <R, A, B, C> KeyedAction3<R, A, B, C> of(
            final @NonNull Key<? extends Action3<R, A, B, C>> key,
            final @NonNull Action3<R, A, B, C> delegate);

    <R, A, B, C, D> KeyedAction4<R, A, B, C, D> of(
            final @NonNull Key<? extends Action4<R, A, B, C, D>> key,
            final @NonNull Action4<R, A, B, C, D> delegate);
}
