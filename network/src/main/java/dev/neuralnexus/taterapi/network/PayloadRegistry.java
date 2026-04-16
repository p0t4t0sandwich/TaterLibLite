/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.common.custom.CustomPacketPayload;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayload;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayload;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class PayloadRegistry {
    private static final Map<String, CustomPacketPayload.Type<? extends CustomPacketPayload>>
            CUSTOM = new HashMap<>();
    private static final Map<String, CustomQueryPayload.Type<? extends CustomQueryPayload>> QUERY =
            new HashMap<>();
    private static final Map<
                    Integer, CustomQueryAnswerPayload.Type<? extends CustomQueryAnswerPayload>>
            ANSWER = new HashMap<>();

    public static <T extends CustomPacketPayload> void register(
            final CustomPacketPayload.@NonNull Type<T> type, @NonNull Mapping... mappings) {
        if (mappings.length == 0) {
            throw new IllegalArgumentException("At least one mapping must be provided");
        }
        for (final Mapping mapping : mappings) {
            if (mapping.resolve()) {
                CUSTOM.put(Objects.requireNonNullElseGet(mapping.id, type::id), type);
            }
        }
    }

    public static Optional<CustomPacketPayload.Type<? extends CustomPacketPayload>> custom(
            final @NonNull String identifier) {
        return Optional.ofNullable(CUSTOM.get(identifier));
    }

    public static void unregisterCustom(final @NonNull String identifier) {
        CUSTOM.remove(identifier);
    }

    public static <T extends CustomQueryPayload> void register(
            final CustomQueryPayload.@NonNull Type<T> type, @NonNull Mapping... mappings) {
        if (mappings.length == 0) {
            throw new IllegalArgumentException("At least one mapping must be provided");
        }
        for (final Mapping mapping : mappings) {
            if (mapping.resolve()) {
                QUERY.put(Objects.requireNonNullElseGet(mapping.id, type::id), type);
            }
        }
    }

    public static Optional<CustomQueryPayload.Type<? extends CustomQueryPayload>> query(
            final @NonNull String identifier) {
        return Optional.ofNullable(QUERY.get(identifier));
    }

    public static void unregisterQuery(final @NonNull String identifier) {
        QUERY.remove(identifier);
    }

    public static <T extends CustomQueryAnswerPayload> void register(
            final CustomQueryAnswerPayload.@NonNull Type<T> type, @NonNull IntMapping... mappings) {
        if (mappings.length == 0) {
            throw new IllegalArgumentException("At least one mapping must be provided");
        }
        for (final IntMapping mapping : mappings) {
            if (mapping.resolve()) {
                if (mapping.id != null) {
                    ANSWER.put(mapping.id, type);
                } else if (type.id().isPresent()) {
                    ANSWER.put(type.id().get(), type);
                }
            }
        }
    }

    public static <T extends CustomQueryAnswerPayload> void register(
            final CustomQueryAnswerPayload.@NonNull Type<T> type, final int transactionId) {
        if (ANSWER.containsKey(transactionId)) {
            throw new IllegalArgumentException(
                    "A payload is already registered for transaction ID " + transactionId);
        }
        ANSWER.put(transactionId, type);
    }

    public static void unregisterQueryAnswer(final int transactionId) {
        ANSWER.remove(transactionId);
    }

    @SuppressWarnings("unchecked")
    public static <B extends ByteBuf>
            Optional<StreamCodec<? super B, ? extends CustomQueryAnswerPayload>>
                    getQueryAnswerPayloadCodec(final int transactionId) {
        return Optional.ofNullable(
                (StreamCodec<ByteBuf, CustomQueryAnswerPayload>) ANSWER.get(transactionId));
    }

    public record Mapping(
            @Nullable String id, @NonNull MinecraftVersion since, @NonNull MinecraftVersion until) {
        public Mapping(final @Nullable String id, @NonNull MinecraftVersion since) {
            this(id, since, MinecraftVersions.UNKNOWN);
        }

        public Mapping(final @NonNull String id) {
            this(id, MinecraftVersions.UNKNOWN, MinecraftVersions.UNKNOWN);
        }

        public boolean resolve() {
            return Constraint.range(this.since, this.until).result();
        }
    }

    public record IntMapping(
            @Nullable Integer id,
            @NonNull MinecraftVersion since,
            @NonNull MinecraftVersion until) {
        public IntMapping(final @Nullable Integer id, @NonNull MinecraftVersion since) {
            this(id, since, MinecraftVersions.UNKNOWN);
        }

        public IntMapping(final @NonNull Integer id) {
            this(id, MinecraftVersions.UNKNOWN, MinecraftVersions.UNKNOWN);
        }

        public boolean resolve() {
            return Constraint.range(this.since, this.until).result();
        }
    }
}
