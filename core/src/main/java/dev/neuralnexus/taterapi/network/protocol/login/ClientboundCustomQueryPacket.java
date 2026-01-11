/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readVarInt;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeVarInt;

import dev.neuralnexus.taterapi.network.NetworkRegistry;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayload;
import dev.neuralnexus.taterapi.serialization.Codec;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

public record ClientboundCustomQueryPacket(int transactionId, @NonNull CustomQueryPayload payload)
        implements Packet {
    public static final StreamCodec<ByteBuf, ClientboundCustomQueryPacket> STREAM_CODEC =
            Packet.codec(ClientboundCustomQueryPacket::write, ClientboundCustomQueryPacket::read);

    public static final Codec<?, ClientboundCustomQueryPacket> ADAPTER_CODEC =
            NetworkRegistry.adapters().getTo(ClientboundCustomQueryPacket.class).orElse(null);

    private static @NonNull ClientboundCustomQueryPacket read(final @NonNull ByteBuf buf) {
        final int transactionId = readVarInt(buf);
        final CustomQueryPayload payload = CustomQueryPayload.DEFAULT_CODEC.decode(buf);
        return new ClientboundCustomQueryPacket(transactionId, payload);
    }

    private void write(final @NonNull ByteBuf buf) {
        writeVarInt(buf, this.transactionId);
        CustomQueryPayload.DEFAULT_CODEC.encode(buf, this.payload);
    }

    @SuppressWarnings("unchecked")
    public static <T> @NonNull ClientboundCustomQueryPacket fromMC(final @NonNull T obj) {
        return ((Codec<T, ClientboundCustomQueryPacket>) ADAPTER_CODEC).encode(obj).unwrap();
    }

    @SuppressWarnings("unchecked")
    public <T> @NonNull T toMC() {
        return ((Codec<T, ClientboundCustomQueryPacket>) ADAPTER_CODEC).decode(this).unwrap();
    }
}
