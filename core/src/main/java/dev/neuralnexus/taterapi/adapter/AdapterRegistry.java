/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.adapter;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
public final class AdapterRegistry {
    private final Set<AdapterCodec<?, ?>> CODECS = new HashSet<>();

    public void register(final @NotNull AdapterCodec<?, ?>... codecs) {
        for (final AdapterCodec<?, ?> codec : codecs) {
            for (final AdapterCodec<?, ?> existingCodec : this.CODECS) {
                if (existingCodec.toClass().equals(codec.toClass())
                        && existingCodec.fromClass().equals(codec.fromClass())) {
                    throw new IllegalArgumentException(
                            "Codec for classes "
                                    + codec.toClass()
                                    + " and "
                                    + codec.fromClass()
                                    + " is already registered.");
                }
            }
            this.CODECS.add(codec);
        }
    }

    public <B> Optional<AdapterCodec<?, B>> getTo(final @NotNull Class<B> objClass) {
        for (final AdapterCodec<?, ?> codec : this.CODECS) {
            if (codec.fromClass().equals(objClass)) {
                return Optional.of((AdapterCodec<?, B>) codec);
            }
        }
        return Optional.empty();
    }

    public <B> @NotNull AdapterCodec<?, B> getToOrThrow(final @NotNull Class<B> objClass) {
        return this.getTo(objClass)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "No codec found for object class: " + objClass));
    }

    public <A> Optional<AdapterCodec<A, ?>> getFrom(final @NotNull Class<A> objClass) {
        for (final AdapterCodec<?, ?> codec : this.CODECS) {
            if (codec.toClass().equals(objClass)) {
                return Optional.of((AdapterCodec<A, ?>) codec);
            }
        }
        return Optional.empty();
    }

    public <A> @NotNull AdapterCodec<A, ?> getFromOrThrow(final @NotNull Class<A> objClass) {
        return this.getFrom(objClass)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "No codec found for object class: " + objClass));
    }

    public <A, B> Optional<AdapterCodec<A, B>> get(
            final @NotNull Class<A> toClass, final @NotNull Class<B> fromClass) {
        for (final AdapterCodec<?, ?> codec : this.CODECS) {
            if (codec.toClass().equals(toClass) && codec.fromClass().equals(fromClass)) {
                return Optional.of((AdapterCodec<A, B>) codec);
            }
        }
        for (final AdapterCodec<?, ?> codec : this.CODECS) {
            if (codec.toClass().equals(fromClass) && codec.fromClass().equals(toClass)) {
                return Optional.ofNullable((AdapterCodec<A, B>) codec.reverse());
            }
        }
        return Optional.empty();
    }

    public <A, B> @NotNull AdapterCodec<A, B> getOrThrow(
            final @NotNull Class<A> toClass, final @NotNull Class<B> fromClass) {
        return this.get(toClass, fromClass)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "No codec found for classes: "
                                                + toClass
                                                + ", "
                                                + fromClass));
    }
}
