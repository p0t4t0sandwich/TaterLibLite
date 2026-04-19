/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketType;
import dev.neuralnexus.taterapi.network.protocol.PacketTypes;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayload;

import org.jspecify.annotations.NonNull;

public record ClientboundCustomQueryPacket(int transactionId, @NonNull CustomQueryPayload payload)
        implements Packet {
    public static final StreamCodec<FriendlyByteBuf, ClientboundCustomQueryPacket> STREAM_CODEC =
            Packet.codec(
                    ClientboundCustomQueryPacket::encode, ClientboundCustomQueryPacket::decode);

    private static @NonNull ClientboundCustomQueryPacket decode(
            final @NonNull FriendlyByteBuf input) {
        final int transactionId = input.readVarInt();
        final CustomQueryPayload payload = CustomQueryPayload.DEFAULT_CODEC.decode(input);
        return new ClientboundCustomQueryPacket(transactionId, payload);
    }

    private void encode(final @NonNull FriendlyByteBuf output) {
        output.writeVarInt(this.transactionId);
        CustomQueryPayload.DEFAULT_CODEC.encode(output, this.payload);
    }

    @Override
    public PacketType<ClientboundCustomQueryPacket> type() {
        return PacketTypes.LOGIN.CLIENTBOUND_CUSTOM_QUERY;
    }
}
