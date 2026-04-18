/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.codec.StreamDecoder;
import dev.neuralnexus.taterapi.network.codec.StreamMemberEncoder;

import org.jspecify.annotations.NonNull;

public interface Packet {
    // void handle(final @NonNull T handler);

    PacketType<? extends Packet> type();

    static <B extends FriendlyByteBuf, V extends Packet> StreamCodec<B, V> codec(
            final @NonNull StreamMemberEncoder<B, V> encoder,
            final @NonNull StreamDecoder<B, V> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }

    /**
     * Creates a new versioned codec builder. If there are overlapping version ranges, the codec
     * with the highest version will be used. If no codec matches the current Minecraft version, an
     * exception will be thrown.
     *
     * @return a new versioned codec builder
     * @param <B> the buffer type
     * @param <V> the value type
     */
    static <B extends FriendlyByteBuf, V extends Packet>
            StreamCodec.VersionedCodecBuilder<B, V> versioned() {
        return new StreamCodec.VersionedCodecBuilder<>(false);
    }

    static <B extends FriendlyByteBuf, V extends Packet>
            StreamCodec.VersionedCodecBuilder<B, V> versioned(final boolean strict) {
        return new StreamCodec.VersionedCodecBuilder<>(strict);
    }
}
