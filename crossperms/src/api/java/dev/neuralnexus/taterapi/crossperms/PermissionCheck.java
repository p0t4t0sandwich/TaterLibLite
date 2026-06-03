package dev.neuralnexus.taterapi.crossperms;

import org.jspecify.annotations.NonNull;

import java.util.function.Predicate;

@FunctionalInterface
public interface PermissionCheck<S> extends Predicate<S> {
    @NonNull TriState check(final @NonNull S subject);

    @Override
    default boolean test(final @NonNull S subject) {
        return this.check(subject).get();
    }

    default @NonNull PermissionCheck<S> not() {
        return (t) -> this.check(t).not();
    }

    default @NonNull PermissionCheck<S> or(final @NonNull PermissionCheck<? super S> other) {
        return (t) -> {
            final TriState a = this.check(t);
            if (a == TriState.TRUE) {
                return TriState.TRUE;
            }
            return TriState.or(a, other.check(t));
        };
    }

    default @NonNull PermissionCheck<S> and(final @NonNull PermissionCheck<? super S> other) {
        return (t) -> {
            final TriState a = this.check(t);
            if (a == TriState.FALSE) {
                return TriState.FALSE;
            }
            return TriState.and(a, other.check(t));
        };
    }

    default @NonNull PermissionCheck<S> xor(final @NonNull PermissionCheck<? super S> other) {
        return (t) -> {
            final TriState a = this.check(t);
            if (a == TriState.DEFAULT) {
                return TriState.DEFAULT;
            }
            return TriState.xor(a, other.check(t));
        };
    }

    default @NonNull PermissionCheck<S> orElse(final @NonNull PermissionCheck<? super S> other) {
        return (t) -> {
            final TriState a = this.check(t);
            if (a != TriState.DEFAULT) {
                return a;
            }
            return other.check(t);
        };
    }

    @Override
    default @NonNull PermissionCheck<S> or(final @NonNull Predicate<? super S> other) {
        return (t) -> TriState.of(this.test(t) || other.test(t));
    }

    @Override
    default @NonNull PermissionCheck<S> and(final @NonNull Predicate<? super S> other) {
        return (t) -> TriState.of(this.test(t) && other.test(t));
    }

    default @NonNull PermissionCheck<S> xor(final @NonNull Predicate<? super S> other) {
        return (t) -> TriState.of(this.test(t) ^ other.test(t));
    }

    default @NonNull PermissionCheck<S> orElse(final @NonNull Predicate<? super S> other) {
        return (t) -> {
            final TriState a = this.check(t);
            if (a != TriState.DEFAULT) {
                return a;
            }
            return TriState.of(other.test(t));
        };
    }
}
