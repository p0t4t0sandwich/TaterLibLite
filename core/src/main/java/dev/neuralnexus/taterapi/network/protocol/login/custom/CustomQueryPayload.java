/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login.custom;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.codec.StreamDecoder;
import dev.neuralnexus.taterapi.network.codec.StreamMemberEncoder;

import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;

public interface CustomQueryPayload {
    StreamCodec<@NotNull ByteBuf, ? extends @NotNull CustomQueryPayload> DEFAULT_CODEC =
            CustomQueryPayloadImpl.STREAM_CODEC;

    @NotNull String id();

    @NotNull ByteBuf data();

    default @NotNull StreamCodec<@NotNull ByteBuf, ? extends @NotNull CustomQueryPayload> codec() {
        return DEFAULT_CODEC;
    }

    default <T extends @NotNull CustomQueryPayload> T as(
            final @NotNull StreamDecoder<@NotNull ByteBuf, T> codec) {
        return codec.decode(this.data());
    }

    static <B extends @NotNull ByteBuf, T extends @NotNull CustomQueryPayload>
            StreamCodec<B, T> codec(
                    final @NotNull StreamMemberEncoder<B, T> encoder,
                    final @NotNull StreamDecoder<B, T> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }
}
