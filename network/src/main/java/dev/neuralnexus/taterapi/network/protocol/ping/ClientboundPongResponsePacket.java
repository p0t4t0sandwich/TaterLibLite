/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.ping;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketType;
import dev.neuralnexus.taterapi.network.protocol.PacketTypes;

import org.jspecify.annotations.NonNull;

public record ClientboundPongResponsePacket(long time) implements Packet {
    public static final StreamCodec<FriendlyByteBuf, ClientboundPongResponsePacket> STREAM_CODEC =
            Packet.codec(
                    ClientboundPongResponsePacket::encode, ClientboundPongResponsePacket::decode);

    private static ClientboundPongResponsePacket decode(final @NonNull FriendlyByteBuf input) {
        return new ClientboundPongResponsePacket(input.readLong());
    }

    private void encode(final @NonNull FriendlyByteBuf output) {
        output.writeLong(this.time);
    }

    @Override
    public PacketType<ClientboundPongResponsePacket> type() {
        return PacketTypes.STATUS.CLIENTBOUND_PONG_RESPONSE;
    }
}
