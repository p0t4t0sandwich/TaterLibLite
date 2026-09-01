/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data;

import dev.neuralnexus.taterapi.data.value.Value;
import dev.neuralnexus.taterapi.registries.BuilderRegistry;
import dev.neuralnexus.taterapi.resources.Identifier;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

@SuppressWarnings("unused")
public interface Key<V extends Value<?>> extends Identifier {
    @NonNull Class<V> type();

    @SuppressWarnings("unchecked")
    static Builder<?, ?> builder() {
        return BuilderRegistry.get(Builder.class);
    }

    static <E> Key<Value<E>> from(final Identifier identifier, final Class<E> type) {
        return Key.builder()
                .key(Objects.requireNonNull(identifier, "identifier"))
                .type(Objects.requireNonNull(type, "type"))
                .build();
    }

    interface Builder<E, V extends Value<E>>
            extends dev.neuralnexus.taterapi.util.Builder<Key<V>, Builder<E, V>> {
        /**
         * The type for this key
         *
         * @param type The type (class)
         * @return The builder
         */
        <T, B extends Value<T>> Builder<T, B> type(final @NonNull Class<T> type);

        /**
         * The identifier for this key
         *
         * @param identifier The identifier
         * @return The builder
         */
        Builder<E, V> key(final @NonNull Identifier identifier);

        /**
         * Build the key
         *
         * @return The key
         */
        Key<V> build();
    }
}
