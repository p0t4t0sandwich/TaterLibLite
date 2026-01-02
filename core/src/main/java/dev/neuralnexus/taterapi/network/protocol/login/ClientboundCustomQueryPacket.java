/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readVarInt;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeVarInt;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;
import dev.neuralnexus.taterapi.network.NetworkAdapters;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayload;

import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public record ClientboundCustomQueryPacket(int transactionId, @NotNull CustomQueryPayload payload)
        implements Packet {
    public static final StreamCodec<ByteBuf, ClientboundCustomQueryPacket> STREAM_CODEC =
            Packet.codec(ClientboundCustomQueryPacket::write, ClientboundCustomQueryPacket::new);

    public static final AdapterCodec<?, ClientboundCustomQueryPacket> ADAPTER_CODEC =
            NetworkAdapters.registry().getTo(ClientboundCustomQueryPacket.class).orElse(null);

    private ClientboundCustomQueryPacket(final @NotNull ByteBuf buf) {
        this(readVarInt(buf), CustomQueryPayload.DEFAULT_CODEC.decode(buf));
    }

    private void write(final @NotNull ByteBuf buf) {
        writeVarInt(buf, this.transactionId);
        ((StreamCodec<ByteBuf, CustomQueryPayload>) this.payload.codec()).encode(buf, this.payload);
    }

    public static <T> @NotNull ClientboundCustomQueryPacket fromMC(final @NotNull T obj) {
        return ((AdapterCodec<T, ClientboundCustomQueryPacket>) ADAPTER_CODEC).from(obj);
    }

    public <T> @NotNull T toMC() {
        return ((AdapterCodec<T, ClientboundCustomQueryPacket>) ADAPTER_CODEC).to(this);
    }
}
