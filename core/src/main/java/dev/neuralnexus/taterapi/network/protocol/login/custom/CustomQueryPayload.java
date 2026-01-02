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

public interface CustomQueryPayload {
    StreamCodec<@NonNull ByteBuf, ? extends @NonNull CustomQueryPayload> DEFAULT_CODEC =
            CustomQueryPayloadImpl.STREAM_CODEC;

    @NonNull String id();

    @NonNull ByteBuf data();

    default @NonNull StreamCodec<@NonNull ByteBuf, ? extends @NonNull CustomQueryPayload> codec() {
        return DEFAULT_CODEC;
    }

    default <T extends @NonNull CustomQueryPayload> T as(
            final @NonNull StreamDecoder<@NonNull ByteBuf, T> codec) {
        return codec.decode(this.data());
    }

    static <B extends @NonNull ByteBuf, T extends @NonNull CustomQueryPayload>
            StreamCodec<B, T> codec(
                    final @NonNull StreamMemberEncoder<B, T> encoder,
                    final @NonNull StreamDecoder<B, T> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }
}
