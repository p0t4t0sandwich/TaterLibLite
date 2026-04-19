/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.ping;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketType;
import dev.neuralnexus.taterapi.network.protocol.PacketTypes;

import org.jspecify.annotations.NonNull;

public record ServerboundPingRequestPacket(long time) implements Packet {
    public static final StreamCodec<FriendlyByteBuf, ServerboundPingRequestPacket> STREAM_CODEC =
            Packet.codec(
                    ServerboundPingRequestPacket::encode, ServerboundPingRequestPacket::decode);

    private static ServerboundPingRequestPacket decode(final @NonNull FriendlyByteBuf input) {
        return new ServerboundPingRequestPacket(input.readLong());
    }

    private void encode(final @NonNull FriendlyByteBuf output) {
        output.writeLong(this.time);
    }

    @Override
    public PacketType<ServerboundPingRequestPacket> type() {
        return PacketTypes.STATUS.SERVERBOUND_PING_REQUEST;
    }
}
