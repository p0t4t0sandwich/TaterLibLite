/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login.custom;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readPayload;

import dev.neuralnexus.taterapi.network.NetworkRegistry;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.codec.StreamDecoder;
import dev.neuralnexus.taterapi.network.codec.StreamMemberEncoder;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

public interface CustomQueryAnswerPayload {
    StreamCodec<@NonNull ByteBuf, @NonNull CustomQueryAnswerPayload> DEFAULT_CODEC =
            CustomQueryAnswerPayload.codec(
                    (value, buf) -> buf.writeBytes(readPayload(value.data())),
                    // TODO: Discover why simplifying this further causes deserialization issues
                    buffer -> {
                        final ByteBuf data = readPayload(buffer);
                        return () -> data;
                    });

    @NonNull ByteBuf data();

    static <B extends @NonNull ByteBuf, T extends @NonNull CustomQueryAnswerPayload>
            StreamCodec<B, T> codec(
                    final @NonNull StreamMemberEncoder<B, T> encoder,
                    final @NonNull StreamDecoder<B, T> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }

    static <B extends ByteBuf> StreamCodec<? super B, ? extends CustomQueryAnswerPayload> codec(
            final int transactionId) {
        return NetworkRegistry.getQueryAnswerPayloadCodec(transactionId).orElse(DEFAULT_CODEC);
    }
}
