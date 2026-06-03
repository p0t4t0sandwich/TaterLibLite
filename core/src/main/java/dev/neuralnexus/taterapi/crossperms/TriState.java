package dev.neuralnexus.taterapi.crossperms;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * <a href="https://en.wikipedia.org/wiki/Three-valued_logic">Kleene and Priest logics</a>
 */
public enum TriState {
    TRUE(1),
    DEFAULT(0),
    FALSE(-1);

    private final int value;

    TriState(final int value) {
        this.value = value;
    }

    private static @NonNull TriState fromValue(final int value) {
        if (value > 0) {
            return TRUE;
        } else if (value < 0) {
            return FALSE;
        } else {
            return DEFAULT;
        }
    }

    public static @NonNull TriState of(final boolean bool) {
        return bool ? TRUE : FALSE;
    }

    public static @NonNull TriState of(@Nullable Boolean bool) {
        return bool == null ? DEFAULT : of(bool.booleanValue());
    }

    public boolean get() {
        return this.value > 0;
    }

    public @NonNull TriState not() {
        return TriState.fromValue(-this.value);
    }

    public static @NonNull TriState and(final @NonNull TriState a, final @NonNull TriState b) {
        Objects.requireNonNull(a, "First TriState cannot be null");
        Objects.requireNonNull(b, "Second TriState cannot be null");
        return TriState.fromValue(Math.min(a.value, b.value));
    }

    public @NonNull TriState and(final @NonNull TriState other) {
        Objects.requireNonNull(other, "Other TriState cannot be null");
        return TriState.and(this, other);
    }

    public static @NonNull TriState or(final @NonNull TriState a, final @NonNull TriState b) {
        Objects.requireNonNull(a, "First TriState cannot be null");
        Objects.requireNonNull(b, "Second TriState cannot be null");
        return TriState.fromValue(Math.max(a.value, b.value));
    }

    public @NonNull TriState or(final @NonNull TriState other) {
        Objects.requireNonNull(other, "Other TriState cannot be null");
        return TriState.or(this, other);
    }

    public static @NonNull TriState xor(final @NonNull TriState a, final @NonNull TriState b) {
        Objects.requireNonNull(a, "First TriState cannot be null");
        Objects.requireNonNull(b, "Second TriState cannot be null");
        return TriState.fromValue(Math.min(
                Math.max(a.value, b.value),
                -Math.min(a.value, b.value)));
    }

    public @NonNull TriState xor(final @NonNull TriState other) {
        Objects.requireNonNull(other, "Other TriState cannot be null");
        return TriState.xor(this, other);
    }

    public boolean orElse(final boolean other) {
        return this.get() || other;
    }

    public boolean orElse(final @Nullable Boolean other) {
        if (this == DEFAULT) {
            return other != null && other;
        }
        return this.get();
    }

    public boolean orElse(final @NonNull TriState other) {
        Objects.requireNonNull(other, "Other TriState cannot be null");
        if (this == DEFAULT) {
            return other.get();
        }
        return this.get();
    }

    public boolean orElseGet(final @NonNull BooleanSupplier other) {
        Objects.requireNonNull(other, "Other BooleanSupplier cannot be null");
        if (this == DEFAULT) {
            return other.getAsBoolean();
        }
        return this.get();
    }

    public <T> Optional<T> map(final @NonNull Function<Boolean, T> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        if (this == DEFAULT) {
            return Optional.empty();
        }
        return Optional.ofNullable(mapper.apply(this.get()));
    }

    public <E extends Throwable> boolean orElseThrow(final @NonNull Function<Boolean, ? extends E> exceptionSupplier) throws E {
        Objects.requireNonNull(exceptionSupplier, "Exception supplier cannot be null");
        if (this != DEFAULT) {
            return this.get();
        }
        throw exceptionSupplier.apply(this.get());
    }
}
