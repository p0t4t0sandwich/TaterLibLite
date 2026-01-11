/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.common;

import dev.neuralnexus.taterapi.network.NetworkRegistry;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.common.custom.CustomPacketPayload;
import dev.neuralnexus.taterapi.serialization.Codec;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

public record ServerboundCustomPayloadPacket(@NonNull CustomPacketPayload payload)
        implements Packet {
    public static final StreamCodec<ByteBuf, ServerboundCustomPayloadPacket> STREAM_CODEC =
            CustomPacketPayload.DEFAULT_CODEC.map(
                    ServerboundCustomPayloadPacket::new, ServerboundCustomPayloadPacket::payload);

    public static final Codec<?, ServerboundCustomPayloadPacket> ADAPTER_CODEC =
            NetworkRegistry.adapters().getTo(ServerboundCustomPayloadPacket.class).orElse(null);

    @SuppressWarnings("unchecked")
    public static <T> @NonNull ServerboundCustomPayloadPacket fromMC(final @NonNull T obj) {
        return ((Codec<T, ServerboundCustomPayloadPacket>) ADAPTER_CODEC).encode(obj).unwrap();
    }

    @SuppressWarnings("unchecked")
    public <T> @NonNull T toMC() {
        return ((Codec<T, ServerboundCustomPayloadPacket>) ADAPTER_CODEC).decode(this).unwrap();
    }
}
