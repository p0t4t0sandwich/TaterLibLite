/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login.custom;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.codec.StreamDecoder;
import dev.neuralnexus.taterapi.network.codec.StreamMemberEncoder;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

public interface CustomQueryAnswerPayload {
    StreamCodec<@NonNull ByteBuf, ? extends @NonNull CustomQueryAnswerPayload> DEFAULT_CODEC =
            CustomQueryAnswerPayloadImpl.STREAM_CODEC;

    @NonNull ByteBuf data();

    default @NonNull StreamCodec<@NonNull ByteBuf, ? extends @NonNull CustomQueryAnswerPayload>
            codec() {
        return DEFAULT_CODEC;
    }

    default <T extends @NonNull CustomQueryAnswerPayload> T as(
            final @NonNull StreamDecoder<@NonNull ByteBuf, T> codec) {
        return codec.decode(this.data());
    }

    static <B extends @NonNull ByteBuf, T extends @NonNull CustomQueryAnswerPayload>
            StreamCodec<B, T> codec(
                    final @NonNull StreamMemberEncoder<B, T> encoder,
                    final @NonNull StreamDecoder<B, T> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }
}
