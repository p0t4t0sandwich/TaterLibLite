/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.handshake;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketType;
import dev.neuralnexus.taterapi.network.protocol.PacketTypes;

import org.jspecify.annotations.NonNull;

public record ClientIntentionPacket(
        int protocolVersion, @NonNull String hostName, int port, @NonNull ClientIntent intention)
        implements Packet {
    public static final StreamCodec<FriendlyByteBuf, ClientIntentionPacket> STREAM_CODEC =
            Packet.codec(ClientIntentionPacket::encode, ClientIntentionPacket::decode);
    private static final int MAX_HOST_LENGTH = 255;

    private static ClientIntentionPacket decode(final @NonNull FriendlyByteBuf input) {
        return new ClientIntentionPacket(
                input.readVarInt(),
                input.readUtf(MAX_HOST_LENGTH),
                input.readUnsignedShort(),
                ClientIntent.byId(input.readVarInt()));
    }

    private void encode(final @NonNull FriendlyByteBuf output) {
        output.writeVarInt(this.protocolVersion);
        output.writeUtf(this.hostName);
        output.writeShort(this.port);
        output.writeVarInt(this.intention.id());
    }

    public PacketType<ClientIntentionPacket> type() {
        return PacketTypes.HANDSHAKING.CLIENT_INTENTION;
    }
}
