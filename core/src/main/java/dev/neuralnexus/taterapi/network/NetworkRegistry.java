/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayload;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayload;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class NetworkRegistry {
    private static final Map<String, StreamCodec<?, ?>> CUSTOM_QUERY_PAYLOADS = new HashMap<>();
    private static final Map<
                    Integer, StreamCodec<? extends ByteBuf, ? extends CustomQueryAnswerPayload>>
            CUSTOM_QUERY_ANSWER_PAYLOADS = new HashMap<>();

    public static void registerQueryPayload(
            final @NonNull String identifier,
            final @NonNull StreamCodec<? extends ByteBuf, ? extends CustomQueryPayload> codec) {
        CUSTOM_QUERY_PAYLOADS.put(identifier, codec);
    }

    public static void unregisterQueryPayload(final @NonNull String identifier) {
        CUSTOM_QUERY_PAYLOADS.remove(identifier);
    }

    @SuppressWarnings("unchecked")
    public static <B extends ByteBuf>
            Optional<StreamCodec<? super B, ? extends CustomQueryPayload>> getQueryPayloadCodec(
                    final @NonNull String identifier) {
        return Optional.ofNullable(
                (StreamCodec<? super B, ? extends CustomQueryPayload>)
                        CUSTOM_QUERY_PAYLOADS.get(identifier));
    }

    public static void registerQueryAnswerPayload(
            final int transactionId,
            final @NonNull StreamCodec<? extends ByteBuf, ? extends CustomQueryAnswerPayload>
                    codec) {
        CUSTOM_QUERY_ANSWER_PAYLOADS.put(transactionId, codec);
    }

    public static void unregisterQueryAnswerPayload(final int transactionId) {
        CUSTOM_QUERY_ANSWER_PAYLOADS.remove(transactionId);
    }

    @SuppressWarnings("unchecked")
    public static <B extends ByteBuf>
            Optional<StreamCodec<? super B, ? extends CustomQueryAnswerPayload>>
                    getQueryAnswerPayloadCodec(final int transactionId) {
        return Optional.ofNullable(
                (StreamCodec<ByteBuf, CustomQueryAnswerPayload>)
                        CUSTOM_QUERY_ANSWER_PAYLOADS.get(transactionId));
    }
}
