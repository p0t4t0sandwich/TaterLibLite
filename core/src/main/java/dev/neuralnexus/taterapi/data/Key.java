/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data;

import dev.neuralnexus.taterapi.registries.BuilderRegistry;
import dev.neuralnexus.taterapi.resources.Identifier;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

@SuppressWarnings("unused")
@ApiStatus.Internal
public interface Key<E extends Element<?>> extends Identifier {
    @NonNull Class<E> type();

    @SuppressWarnings("unchecked")
    static Builder<?, ?> builder() {
        return BuilderRegistry.get(Builder.class);
    }

    static @NonNull <T, V extends Element<T>> Key<V> create(
            final @NonNull Identifier identifier,
            final @NonNull Class<V> type,
            final @NonNull Class<T> innerType) {
        return Key.builder()
                .key(Objects.requireNonNull(identifier, "identifier"))
                .type(Objects.requireNonNull(type, "type"))
                .build();
    }

    interface Builder<T, E extends Element<T>>
            extends dev.neuralnexus.taterapi.util.Builder<Key<E>, Builder<T, E>> {
        /**
         * The element holder type for this key
         *
         * @param type The holder type (class)
         * @return The builder
         */
        <A, B extends Element<A>> Builder<A, B> type(final @NonNull Class<B> type);

        /**
         * The identifier for this key
         *
         * @param identifier The identifier
         * @return The builder
         */
        Builder<T, E> key(final @NonNull Identifier identifier);

        /**
         * Build the key
         *
         * @return The key
         */
        @NonNull Key<E> build();
    }
}
