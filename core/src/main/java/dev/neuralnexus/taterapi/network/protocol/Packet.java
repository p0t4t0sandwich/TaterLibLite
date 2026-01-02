/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.codec.StreamDecoder;
import dev.neuralnexus.taterapi.network.codec.StreamMemberEncoder;

import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;

public interface Packet {
    // void handle(final @NotNull T handler);

    static <B extends ByteBuf, T extends Packet> StreamCodec<B, T> codec(
            final @NotNull StreamMemberEncoder<B, T> encoder,
            final @NotNull StreamDecoder<B, T> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }
}
