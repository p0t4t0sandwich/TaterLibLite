/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.registries;

import dev.neuralnexus.taterapi.serialization.codecs.ReversibleCodec;

import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
public final class AdapterRegistry {
    private final Set<ReversibleCodec<?, ?>> CODECS = new HashSet<>();

    public void register(final @NonNull ReversibleCodec<?, ?>... codecs) {
        for (final ReversibleCodec<?, ?> codec : codecs) {
            for (final ReversibleCodec<?, ?> existingCodec : this.CODECS) {
                if ((existingCodec.inputType().equals(codec.inputType())
                                && existingCodec.outputType().equals(codec.outputType()))
                        || (existingCodec.inputType().equals(codec.outputType()))
                                && existingCodec.outputType().equals(codec.inputType())) {
                    throw new IllegalArgumentException(
                            "Codec for "
                                    + codec.inputType()
                                    + " to "
                                    + codec.outputType()
                                    + " is already registered.");
                }
            }
            this.CODECS.add(codec);
        }
    }

    public <B> Optional<ReversibleCodec<?, B>> getTo(final @NonNull Class<B> objClass) {
        for (final ReversibleCodec<?, ?> codec : this.CODECS) {
            if (codec.outputType().equals(objClass)) {
                return Optional.of((ReversibleCodec<?, B>) codec);
            }
        }
        return Optional.empty();
    }

    public <B> @NonNull ReversibleCodec<?, B> getToOrThrow(final @NonNull Class<B> objClass) {
        return this.getTo(objClass)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "No codec found for object class: " + objClass));
    }

    public <A> Optional<ReversibleCodec<A, ?>> getFrom(final @NonNull Class<A> objClass) {
        for (final ReversibleCodec<?, ?> codec : this.CODECS) {
            if (codec.inputType().equals(objClass)) {
                return Optional.of((ReversibleCodec<A, ?>) codec);
            }
        }
        return Optional.empty();
    }

    public <A> @NonNull ReversibleCodec<A, ?> getFromOrThrow(final @NonNull Class<A> objClass) {
        return this.getFrom(objClass)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "No codec found for object class: " + objClass));
    }

    public <A, B> Optional<ReversibleCodec<A, B>> get(
            final @NonNull Class<A> inputType, final @NonNull Class<B> outputType) {
        for (final ReversibleCodec<?, ?> codec : this.CODECS) {
            if (codec.inputType().equals(inputType) && codec.outputType().equals(outputType)) {
                return Optional.of((ReversibleCodec<A, B>) codec);
            }
        }
        for (final ReversibleCodec<?, ?> codec : this.CODECS) {
            if (codec.inputType().equals(outputType) && codec.outputType().equals(inputType)) {
                return Optional.ofNullable((ReversibleCodec<A, B>) codec.reverse());
            }
        }
        return Optional.empty();
    }

    public <A, B> @NonNull ReversibleCodec<A, B> getOrThrow(
            final @NonNull Class<A> inputType, final @NonNull Class<B> outputType) {
        return this.get(inputType, outputType)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "No codec found for classes: "
                                                + inputType
                                                + ", "
                                                + outputType));
    }
}
