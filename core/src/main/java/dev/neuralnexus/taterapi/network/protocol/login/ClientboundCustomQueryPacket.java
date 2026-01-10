/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readVarInt;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeVarInt;

import dev.neuralnexus.taterapi.network.NetworkAdapters;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayload;
import dev.neuralnexus.taterapi.serialization.codecs.ReversibleCodec;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

@SuppressWarnings("unchecked")
public record ClientboundCustomQueryPacket(int transactionId, @NonNull CustomQueryPayload payload)
        implements Packet {
    public static final StreamCodec<ByteBuf, ClientboundCustomQueryPacket> STREAM_CODEC =
            Packet.codec(ClientboundCustomQueryPacket::write, ClientboundCustomQueryPacket::new);

    public static final ReversibleCodec<?, ClientboundCustomQueryPacket> ADAPTER_CODEC =
            NetworkAdapters.registry().getTo(ClientboundCustomQueryPacket.class).orElse(null);

    private ClientboundCustomQueryPacket(final @NonNull ByteBuf buf) {
        this(readVarInt(buf), CustomQueryPayload.DEFAULT_CODEC.decode(buf));
    }

    private void write(final @NonNull ByteBuf buf) {
        writeVarInt(buf, this.transactionId);
        ((StreamCodec<ByteBuf, CustomQueryPayload>) this.payload.codec()).encode(buf, this.payload);
    }

    public static <T> @NonNull ClientboundCustomQueryPacket fromMC(final @NonNull T obj) {
        return ((ReversibleCodec<T, ClientboundCustomQueryPacket>) ADAPTER_CODEC)
                .encode(obj)
                .unwrap();
    }

    public <T> @NonNull T toMC() {
        return ((ReversibleCodec<T, ClientboundCustomQueryPacket>) ADAPTER_CODEC)
                .decode(this)
                .unwrap();
    }
}
