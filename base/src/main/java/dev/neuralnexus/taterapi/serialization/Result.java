/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.serialization;

import dev.neuralnexus.taterapi.Wrapped;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface Result<R> extends Wrapped<R> {
    static <R> Result<R> success(final R value) {
        return new Success<>(value);
    }

    static <R> Result<R> error(final @NonNull String message) {
        return new Error<>(message, null);
    }

    static <R> Result<R> error(final @NonNull String message, @Nullable final Throwable cause) {
        return new Error<>(message, cause);
    }

    boolean isSuccess();

    default boolean isError() {
        return !this.isSuccess();
    }

    Optional<R> result();

    Optional<Result.Error<R>> error();

    record Success<R>(R value) implements Result<R> {
        @Override
        public R unwrap() {
            return this.value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public Optional<R> result() {
            return Optional.of(this.value);
        }

        @Override
        public Optional<Error<R>> error() {
            return Optional.empty();
        }
    }

    record Error<R>(@NonNull String message, @Nullable Throwable cause) implements Result<R> {
        @Override
        public R unwrap() {
            return null;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public Optional<R> result() {
            return Optional.empty();
        }

        @Override
        public Optional<Error<R>> error() {
            return Optional.of(this);
        }
    }
}
