/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data.action;

import dev.neuralnexus.taterapi.Result;
import dev.neuralnexus.taterapi.data.Element;
import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.registries.DataRegistry;
import dev.neuralnexus.taterapi.registries.FactoryRegistry;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

@ApiStatus.Internal
@FunctionalInterface
public interface Action<R> extends Element<R> {
    Result<R> apply();

    static <R> KeyedAction<R> of(
            final @NonNull Key<? extends Action<R>> key, final @NonNull Action<R> delegate) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(delegate, "delegate");
        return FactoryRegistry.get(ActionFactory.class).of(key, delegate);
    }

    static <R, A> KeyedAction1<R, A> of(
            final @NonNull Key<? extends Action1<R, A>> key,
            final @NonNull Action1<R, A> delegate) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(delegate, "delegate");
        return FactoryRegistry.get(ActionFactory.class).of(key, delegate);
    }

    static <R, A, B> KeyedAction2<R, A, B> of(
            final @NonNull Key<? extends Action2<R, A, B>> key,
            final @NonNull Action2<R, A, B> delegate) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(delegate, "delegate");
        return FactoryRegistry.get(ActionFactory.class).of(key, delegate);
    }

    static <R, A, B, C> KeyedAction3<R, A, B, C> of(
            final @NonNull Key<? extends Action3<R, A, B, C>> key,
            final @NonNull Action3<R, A, B, C> delegate) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(delegate, "delegate");
        return FactoryRegistry.get(ActionFactory.class).of(key, delegate);
    }

    static <R, A, B, C, D> KeyedAction4<R, A, B, C, D> of(
            final @NonNull Key<? extends Action4<R, A, B, C, D>> key,
            final @NonNull Action4<R, A, B, C, D> delegate) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(delegate, "delegate");
        return FactoryRegistry.get(ActionFactory.class).of(key, delegate);
    }

    static <O, R> DataRegistry.Initializer<O, R> of(
            final @NonNull Key<? extends Action<R>> key, final @NonNull Provider<O, R> provider) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(provider, "provider");
        return (objRef) -> Action.of(key, provider.init(objRef));
    }

    static <O, R, A> DataRegistry.Initializer<O, R> of(
            final @NonNull Key<? extends Action1<R, A>> key,
            final @NonNull Provider1<O, R, A> provider) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(provider, "provider");
        return (objRef) -> Action.of(key, provider.init(objRef));
    }

    static <O, R, A, B> DataRegistry.Initializer<O, R> of(
            final @NonNull Key<? extends Action2<R, A, B>> key,
            final @NonNull Provider2<O, R, A, B> provider) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(provider, "provider");
        return (objRef) -> Action.of(key, provider.init(objRef));
    }

    static <O, R, A, B, C> DataRegistry.Initializer<O, R> of(
            final @NonNull Key<? extends Action3<R, A, B, C>> key,
            final @NonNull Provider3<O, R, A, B, C> provider) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(provider, "provider");
        return (objRef) -> Action.of(key, provider.init(objRef));
    }

    static <O, R, A, B, C, D> DataRegistry.Initializer<O, R> of(
            final @NonNull Key<? extends Action4<R, A, B, C, D>> key,
            final @NonNull Provider4<O, R, A, B, C, D> provider) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(provider, "provider");
        return (objRef) -> Action.of(key, provider.init(objRef));
    }

    @FunctionalInterface
    interface Provider<O, R> {
        Action<R> init(O objRef);
    }

    @FunctionalInterface
    interface Provider1<O, R, A> {
        Action1<R, A> init(O objRef);
    }

    @FunctionalInterface
    interface Provider2<O, R, A, B> {
        Action2<R, A, B> init(O objRef);
    }

    @FunctionalInterface
    interface Provider3<O, R, A, B, C> {
        Action3<R, A, B, C> init(O objRef);
    }

    @FunctionalInterface
    interface Provider4<O, R, A, B, C, D> {
        Action4<R, A, B, C, D> init(O objRef);
    }
}
